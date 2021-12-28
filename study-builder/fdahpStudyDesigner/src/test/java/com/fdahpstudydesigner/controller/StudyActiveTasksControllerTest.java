/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.controller;

import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_ACTIVE_TASK_DELETED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_ACTIVE_TASK_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_ACTIVE_TASK_SAVED_OR_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_ACTIVE_TASK_SECTION_MARKED_COMPLETE;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import com.fdahpstudydesigner.bo.ActiveTaskBo;
import com.fdahpstudydesigner.bo.NotificationBO;
import com.fdahpstudydesigner.bo.StudyBo;
import com.fdahpstudydesigner.common.BaseMockIT;
import com.fdahpstudydesigner.common.PathMappingUri;
import com.fdahpstudydesigner.common.UserAccessLevel;
import com.fdahpstudydesigner.dao.NotificationDAOImpl;
import com.fdahpstudydesigner.dao.StudyActiveTasksDAOImpl;
import com.fdahpstudydesigner.dao.StudyDAOImpl;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.SessionObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class StudyActiveTasksControllerTest extends BaseMockIT {
  @Autowired StudyDAOImpl studyDaoImpl;

  @Autowired StudyActiveTasksDAOImpl studyActiveTasksDAOImpl;

  @Autowired NotificationDAOImpl notificationDaoImpl;

  private static final String STUDY_ID_VALUE = "678574";

  private static final String CUSTOM_STUDY_ID_VALUE = "678590";

  private static final String USER_ID_VALUE = "4878641";

  @Test
  public void shouldMarkActiveTaskAsCompleted() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(
        new ArrayList<>(Arrays.asList(Integer.parseInt(STUDY_SESSION_COUNT_VALUE))));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(STUDY_ID_ATTR_NAME, STUDY_ID_VALUE);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, STUDY_ID_VALUE);
    sessionAttributes.put(FdahpStudyDesignerConstants.PERMISSION, "View");
    sessionAttributes.put(FdahpStudyDesignerConstants.IS_LIVE, "isLive");

    mockMvc
        .perform(
            post(PathMappingUri.ACTIVE_TASK_MARK_AS_COMPLETED.getPath())
                .param(STUDY_SESSION_COUNT_PARAM, STUDY_SESSION_COUNT_VALUE)
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:viewStudyActiveTasks.do"));

    verifyAuditEventCall(STUDY_ACTIVE_TASK_SECTION_MARKED_COMPLETE);
  }

  @Test
  public void shouldStudyActiveTaskMarkedComplete() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(
        new ArrayList<>(Arrays.asList(Integer.parseInt(STUDY_SESSION_COUNT_VALUE))));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);

    ActiveTaskBo activeTaskBo = new ActiveTaskBo();
    activeTaskBo.setTaskTypeId("123");
    activeTaskBo.setStudyId("678578");
    activeTaskBo.setActiveTaskFrequenciesBo(null);
    activeTaskBo.setShortTitle("ActiveTaskShortTitle");

    MockHttpServletRequestBuilder requestBuilder =
        post(PathMappingUri.SAVE_OR_UPDATE_ACTIVE_TASK_CONTENT.getPath())
            .param("buttonText", "completed")
            .headers(headers)
            .sessionAttrs(sessionAttributes);

    addParams(requestBuilder, activeTaskBo);
    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/adminStudies/viewStudyActiveTasks.do"));

    verifyAuditEventCall(STUDY_ACTIVE_TASK_MARKED_COMPLETE);
  }

  @Test
  public void shouldSaveStudyActiveTaskDetails() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(
        new ArrayList<>(Arrays.asList(Integer.parseInt(STUDY_SESSION_COUNT_VALUE))));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);

    ActiveTaskBo activeTaskBo = new ActiveTaskBo();
    activeTaskBo.setTaskTypeId("1");
    activeTaskBo.setStudyId("678574");
    activeTaskBo.setLive(1);
    activeTaskBo.setActiveTaskFrequenciesBo(null);
    activeTaskBo.setActiveTaskLifetimeStart("05/11/2020");
    activeTaskBo.setActiveTaskLifetimeEnd("25/11/2020");
    activeTaskBo.setFrequency(FREQUENCY_TYPE_ONE_TIME);
    activeTaskBo.setShortTitle("FetalKickCounter");
    activeTaskBo.setType("notscheduled");
    activeTaskBo.setCustomStudyId(CUSTOM_STUDY_ID_VALUE);

    MockHttpServletRequestBuilder requestBuilder =
        post(PathMappingUri.SAVE_OR_UPDATE_ACTIVE_TASK_Schedule.getPath())
            .headers(headers)
            .sessionAttr(CUSTOM_STUDY_ID_ATTR_NAME, CUSTOM_STUDY_ID_VALUE)
            .sessionAttrs(sessionAttributes);

    addParams(requestBuilder, activeTaskBo);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/adminStudies/viewStudyActiveTasks.do"));

    List<ActiveTaskBo> activeTaskList =
        studyActiveTasksDAOImpl.getStudyActiveTasksByStudyId(CUSTOM_STUDY_ID_VALUE, true);

    assertNotNull(activeTaskList);

    for (ActiveTaskBo activeTask : activeTaskList) {
      assertEquals("2020-11-05", activeTask.getActiveTaskLifetimeStart());
      assertEquals("2020-11-25", activeTask.getActiveTaskLifetimeEnd());
      assertEquals(FREQUENCY_TYPE_ONE_TIME, activeTask.getFrequency());
      assertEquals("FetalKickCounter", activeTask.getShortTitle());
    }
  }

  @Test
  public void shouldStudyActiveTaskSavedOrUpdate() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(
        new ArrayList<>(Arrays.asList(Integer.parseInt(STUDY_SESSION_COUNT_VALUE))));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);

    ActiveTaskBo activeTaskBo = new ActiveTaskBo();
    activeTaskBo.setTaskTypeId("123");
    activeTaskBo.setStudyId("678574");
    activeTaskBo.setActiveTaskFrequenciesBo(null);
    activeTaskBo.setShortTitle("ActiveTaskShortTitle");

    MockHttpServletRequestBuilder requestBuilder =
        post(PathMappingUri.SAVE_OR_UPDATE_ACTIVE_TASK_CONTENT.getPath())
            .headers(headers)
            .sessionAttr(CUSTOM_STUDY_ID_ATTR_NAME, CUSTOM_STUDY_ID_VALUE)
            .sessionAttrs(sessionAttributes);

    addParams(requestBuilder, activeTaskBo);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/adminStudies/viewActiveTask.do#"));

    StudyBo study = studyDaoImpl.getStudy(activeTaskBo.getStudyId());

    List<NotificationBO> notificationList =
        notificationDaoImpl.getNotificationList(activeTaskBo.getStudyId());
    assertNotNull(notificationList);

    for (NotificationBO notification : notificationList) {
      if (notification.getCreatedBy().equals(Integer.parseInt(USER_ID_VALUE))) {
        assertTrue(notification.getNotificationText().contains(study.getName()));
        assertEquals(
            FdahpStudyDesignerConstants.NOTIFICATION_ST, notification.getNotificationType());
        assertEquals(
            FdahpStudyDesignerConstants.NOTIFICATION_SUBTYPE_ACTIVITY,
            notification.getNotificationSubType());
      }
    }

    verifyAuditEventCall(STUDY_ACTIVE_TASK_SAVED_OR_UPDATED);
  }

  @Test
  public void shouldDeleteStudyActiveTask() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(
        new ArrayList<>(Arrays.asList(Integer.parseInt(STUDY_SESSION_COUNT_VALUE))));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(
        STUDY_SESSION_COUNT_VALUE + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID, "678592");

    mockMvc
        .perform(
            post(PathMappingUri.DELETE_ACTIVE_TASK.getPath())
                .headers(headers)
                .param("activeTaskInfoId", "28500")
                .param(FdahpStudyDesignerConstants.STUDY_ID, STUDY_ID_VALUE)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isOk());

    verifyAuditEventCall(STUDY_ACTIVE_TASK_DELETED);
  }
}
