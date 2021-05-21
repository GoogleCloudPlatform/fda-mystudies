/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style license that can be found in the LICENSE file
 * or at https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.controller;

import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.LAST_PUBLISHED_VERSION_OF_STUDY_VIEWED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NEW_STUDY_CREATION_INITIATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_ACCESSED_IN_EDIT_MODE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_BASIC_INFO_SECTION_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_BASIC_INFO_SECTION_SAVED_OR_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_COMPREHENSION_TEST_SECTION_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_COMPREHENSION_TEST_SECTION_SAVED_OR_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_CONSENT_CONTENT_NEW_VERSION_PUBLISHED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_CONSENT_DOCUMENT_NEW_VERSION_PUBLISHED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_CONSENT_SECTIONS_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_CONSENT_SECTIONS_SAVED_OR_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_DEACTIVATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_ELIGIBILITY_SECTION_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_ELIGIBILITY_SECTION_SAVED_OR_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_LAUNCHED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_LIST_VIEWED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_METADATA_SEND_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_METADATA_SEND_OPERATION_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_METADATA_SENT_TO_PARTICIPANT_DATASTORE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_METADATA_SENT_TO_RESPONSE_DATASTORE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_NEW_NOTIFICATION_CREATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_NEW_RESOURCE_CREATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_NOTIFICATIONS_SECTION_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_NOTIFICATION_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_NOTIFICATION_SAVED_OR_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_PAUSED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_QUESTIONNAIRES_SECTION_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_RESOURCE_MARKED_COMPLETED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_RESOURCE_SAVED_OR_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_RESOURCE_SECTION_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_RESUMED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_REVIEW_AND_E_CONSENT_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_REVIEW_AND_E_CONSENT_SAVED_OR_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_SAVED_IN_DRAFT_STATE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_SETTINGS_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_SETTINGS_SAVED_OR_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_VIEWED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.UPDATES_PUBLISHED_TO_STUDY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.fasterxml.jackson.databind.JsonNode;
import com.fdahpstudydesigner.bean.StudyDetailsBean;
import com.fdahpstudydesigner.bean.StudySessionBean;
import com.fdahpstudydesigner.bo.ConsentBo;
import com.fdahpstudydesigner.bo.ConsentInfoBo;
import com.fdahpstudydesigner.bo.EligibilityBo;
import com.fdahpstudydesigner.bo.NotificationBO;
import com.fdahpstudydesigner.bo.ResourceBO;
import com.fdahpstudydesigner.bo.StudyBo;
import com.fdahpstudydesigner.common.BaseMockIT;
import com.fdahpstudydesigner.common.JsonUtils;
import com.fdahpstudydesigner.common.PathMappingUri;
import com.fdahpstudydesigner.common.UserAccessLevel;
import com.fdahpstudydesigner.dao.NotificationDAOImpl;
import com.fdahpstudydesigner.service.StudyExportService;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.client.support.RestGatewaySupport;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class StudyControllerTest extends BaseMockIT {

  private static final String STUDY_ID_VALUE = "678574";

  private static final String CUSTOM_STUDY_ID_VALUE = "678590";

  private static final String USER_ID_VALUE = "4878641";

  private static final String STUDY_ID_INT_VALUE = "678574";

  private static final String STUDIES_META_DATA_URI = "/studies/studymetadata";

  private static final String STUDY_META_DATA_URI = "/studymetadata";

  private static final String TEST_STUDY_ID_STRING = "678680";

  @Autowired NotificationDAOImpl notificationDaoImpl;

  @Autowired StudyExportService studyExportService;

  private static final String OAUTH_TOKEN = "/oauth2/token";

  @Test
  public void shouldSaveOrUpdateOrResendNotificationForSave() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    NotificationBO notificationBo = new NotificationBO();
    notificationBo.setNotificationText("Study notification");
    notificationBo.setCustomStudyId("678595");

    MockHttpServletRequestBuilder requestBuilder =
        post(PathMappingUri.SAVE_OR_UPDATE_STUDY_NOTIFICATION.getPath())
            .headers(headers)
            .param("buttonType", "save")
            .sessionAttrs(getSessionAttributes());

    addParams(requestBuilder, notificationBo);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:getStudyNotification.do"));

    verifyAuditEventCall(STUDY_NOTIFICATION_SAVED_OR_UPDATED);
  }

  @Test
  public void shouldSaveOrUpdateOrResendNotificationForAdd() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    NotificationBO notificationBo = new NotificationBO();
    notificationBo.setCustomStudyId("678595");
    notificationBo.setNotificationText("Study notification");

    MockHttpServletRequestBuilder requestBuilder =
        post(PathMappingUri.SAVE_OR_UPDATE_STUDY_NOTIFICATION.getPath())
            .headers(headers)
            .param("buttonType", "save")
            .sessionAttrs(getSessionAttributes());

    addParams(requestBuilder, notificationBo);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:getStudyNotification.do"));

    verifyAuditEventCall(STUDY_NEW_NOTIFICATION_CREATED);
  }

  @Test
  public void shouldSaveOrUpdateOrResendNotificationForDone() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    NotificationBO notificationBo = new NotificationBO();
    notificationBo.setNotificationText("Study notification");
    notificationBo.setCustomStudyId("678592");

    MockHttpServletRequestBuilder requestBuilder =
        post(PathMappingUri.SAVE_OR_UPDATE_STUDY_NOTIFICATION.getPath())
            .headers(headers)
            .param("buttonType", "done")
            .sessionAttrs(getSessionAttributes());

    addParams(requestBuilder, notificationBo);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/adminStudies/viewStudyNotificationList.do"));

    verifyAuditEventCall(STUDY_NOTIFICATION_MARKED_COMPLETE);
  }

  @Test
  public void shouldMarkActiveTaskAsCompleted() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(STUDY_ID_ATTR_NAME, STUDY_ID_VALUE);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, CUSTOM_STUDY_ID_VALUE);

    mockMvc
        .perform(
            post(PathMappingUri.CONSENT_MARKED_AS_COMPLETE.getPath())
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:consentListPage.do"));

    verifyAuditEventCall(STUDY_CONSENT_SECTIONS_MARKED_COMPLETE);
  }

  @Test
  public void shouldMarkNotificationAsComplete() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(STUDY_ID_ATTR_NAME, STUDY_ID_VALUE);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, CUSTOM_STUDY_ID_VALUE);

    mockMvc
        .perform(
            post(PathMappingUri.NOTIFICATION_MARK_AS_COMPLETED.getPath())
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:viewStudyNotificationList.do"));

    verifyAuditEventCall(STUDY_NOTIFICATIONS_SECTION_MARKED_COMPLETE);
  }

  @Test
  public void shouldMarkQuestionaireSectionAsComplete() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(STUDY_ID_ATTR_NAME, STUDY_ID_VALUE);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, CUSTOM_STUDY_ID_VALUE);

    mockMvc
        .perform(
            get(PathMappingUri.QUESTIONAIRE_MARK_AS_COMPLETED.getPath())
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:viewStudyQuestionnaires.do"));

    verifyAuditEventCall(STUDY_QUESTIONNAIRES_SECTION_MARKED_COMPLETE);
  }

  @Test
  public void shouldMarkStudyResourceSectionAsComplete() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(STUDY_ID_ATTR_NAME, STUDY_ID_VALUE);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, CUSTOM_STUDY_ID_VALUE);

    mockMvc
        .perform(
            post(PathMappingUri.RESOURCE_MARK_AS_COMPLETED.getPath())
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:getResourceList.do"));

    verifyAuditEventCall(STUDY_RESOURCE_SECTION_MARKED_COMPLETE);
  }

  @Test
  public void checkNewStudyCreationInitiated() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    StudySessionBean studySessionBean = new StudySessionBean();
    studySessionBean.setIsLive("live");
    studySessionBean.setPermission("permission");

    List<StudySessionBean> studySessionBeans = new ArrayList<>();
    studySessionBeans.add(studySessionBean);
    SessionObject session = getSessionObject();
    session.setStudySessionBeans(studySessionBeans);
    session.setUserId("0");
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);

    mockMvc
        .perform(
            post(PathMappingUri.VIEW_BASIC_INFO.getPath())
                .param(FdahpStudyDesignerConstants.IS_LIVE, "live")
                .param(FdahpStudyDesignerConstants.PERMISSION, "permission")
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(view().name("viewBasicInfo"));

    verifyAuditEventCall(NEW_STUDY_CREATION_INITIATED);
  }

  @Test
  public void checkLastPublishedVersionOfStudiedViewed() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    List<StudySessionBean> studySessionBeans = new ArrayList<>();
    StudySessionBean studySession = new StudySessionBean();
    studySession.setIsLive("true");
    studySession.setPermission("View");
    studySession.setSessionStudyCount(10);
    studySession.setStudyId("10");
    studySessionBeans.add(studySession);

    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setStudySessionBeans(studySessionBeans);
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);

    mockMvc
        .perform(
            post(PathMappingUri.VIEW_STUDY_DETAILS.getPath())
                .param(FdahpStudyDesignerConstants.STUDY_ID, STUDY_ID_VALUE)
                .param(FdahpStudyDesignerConstants.PERMISSION, "View")
                .param(FdahpStudyDesignerConstants.IS_LIVE, "isLive")
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/adminStudies/viewBasicInfo.do"));

    verifyAuditEventCall(LAST_PUBLISHED_VERSION_OF_STUDY_VIEWED);
  }

  @Test
  public void checkStudyViewed() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    List<StudySessionBean> studySessionBeans = new ArrayList<>();
    StudySessionBean studySession = new StudySessionBean();
    studySession.setIsLive("true");
    studySession.setPermission("View");
    studySession.setSessionStudyCount(10);
    studySessionBeans.add(studySession);

    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setStudySessionBeans(studySessionBeans);
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);

    mockMvc
        .perform(
            post(PathMappingUri.VIEW_STUDY_DETAILS.getPath())
                .param(FdahpStudyDesignerConstants.PERMISSION, "View")
                .param(FdahpStudyDesignerConstants.STUDY_ID, STUDY_ID_VALUE)
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/adminStudies/viewBasicInfo.do"));

    verifyAuditEventCall(STUDY_VIEWED);
  }

  @Test
  public void checkStudyAccessedInEditMode() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    List<StudySessionBean> studySessionBeans = new ArrayList<>();
    StudySessionBean studySession = new StudySessionBean();
    studySession.setIsLive("true");
    studySession.setPermission("Edit");
    studySession.setSessionStudyCount(10);
    studySessionBeans.add(studySession);

    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setStudySessionBeans(studySessionBeans);
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);

    mockMvc
        .perform(
            post(PathMappingUri.VIEW_STUDY_DETAILS.getPath())
                .headers(headers)
                .param(FdahpStudyDesignerConstants.STUDY_ID, STUDY_ID_VALUE)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/adminStudies/viewBasicInfo.do"));

    verifyAuditEventCall(STUDY_ACCESSED_IN_EDIT_MODE);
  }

  @Test
  public void shouldLaunchStudy() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    SessionObject session = getSessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, "678595");

    mockMvc
        .perform(
            post(PathMappingUri.UPDATE_STUDY_ACTION.getPath())
                .param(FdahpStudyDesignerConstants.STUDY_ID, "678579")
                .param(FdahpStudyDesignerConstants.BUTTON_TEXT, "lunchId")
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isOk());

    verifyAuditEventCall(STUDY_LAUNCHED);
  }

  @Test
  public void shouldUpdatesPublishedToStudy() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    SessionObject session = new SessionObject();
    session.setUserId("4878642");
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, "678591");

    mockMvc
        .perform(
            post(PathMappingUri.UPDATE_STUDY_ACTION.getPath())
                .param(FdahpStudyDesignerConstants.STUDY_ID, "678575")
                .param(FdahpStudyDesignerConstants.BUTTON_TEXT, "updatesId")
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isOk());

    verifyAuditEventCall(UPDATES_PUBLISHED_TO_STUDY);
  }

  @Test
  public void shouldPauseStudy() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, "678594");

    mockMvc
        .perform(
            post(PathMappingUri.UPDATE_STUDY_ACTION.getPath())
                .param(FdahpStudyDesignerConstants.STUDY_ID, "678578")
                .param(FdahpStudyDesignerConstants.BUTTON_TEXT, "pauseId")
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isOk());

    verifyAuditEventCall(STUDY_PAUSED);
  }

  @Test
  public void shouldResumeStudy() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, "678593");

    mockMvc
        .perform(
            post(PathMappingUri.UPDATE_STUDY_ACTION.getPath())
                .param(FdahpStudyDesignerConstants.STUDY_ID, "678577")
                .param(FdahpStudyDesignerConstants.BUTTON_TEXT, "resumeId")
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isOk());

    verifyAuditEventCall(STUDY_RESUMED);
  }

  @Test
  public void shouldSaveOrUpdateSyudySettings() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    SessionObject session = new SessionObject();
    session.setUserId(STUDY_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, STUDY_ID_VALUE);

    StudyBo studyBo = new StudyBo();
    studyBo.setId(STUDY_ID_INT_VALUE);
    studyBo.setStudySequenceBo(null);

    MockHttpServletRequestBuilder requestBuilder =
        post(PathMappingUri.SAVE_OR_UPDATE_SETTINGS_AND_ADMINS.getPath())
            .param("userIds", STUDY_ID_VALUE)
            .param(FdahpStudyDesignerConstants.BUTTON_TEXT, "save")
            .headers(headers)
            .sessionAttrs(sessionAttributes);

    addParams(requestBuilder, studyBo);
    mockMvc.perform(requestBuilder).andDo(print()).andExpect(status().isFound());

    verifyAuditEventCall(STUDY_SETTINGS_SAVED_OR_UPDATED);
  }

  @Test
  public void shouldSaveOrUpdateStudyConsentSection() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(STUDY_ID_ATTR_NAME, STUDY_ID_VALUE);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, CUSTOM_STUDY_ID_VALUE);

    ConsentInfoBo ConsentInfoBo = new ConsentInfoBo();
    ConsentInfoBo.setStudyId(STUDY_ID_INT_VALUE);

    MockHttpServletRequestBuilder requestBuilder =
        post(PathMappingUri.SAVE_OR_UPDATE_CONSENT_INFO.getPath())
            .headers(headers)
            .sessionAttrs(sessionAttributes);

    addParams(requestBuilder, ConsentInfoBo);
    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/adminStudies/consentListPage.do"));

    verifyAuditEventCall(STUDY_CONSENT_SECTIONS_SAVED_OR_UPDATED);
  }

  @Test
  public void shouldCompleteStudySettings() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    SessionObject session = new SessionObject();
    session.setUserId(STUDY_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, STUDY_ID_VALUE);

    StudyBo studyBo = new StudyBo();
    studyBo.setId(STUDY_ID_INT_VALUE);
    studyBo.setStudySequenceBo(null);

    MockHttpServletRequestBuilder requestBuilder =
        post(PathMappingUri.SAVE_OR_UPDATE_SETTINGS_AND_ADMINS.getPath())
            .param("userIds", STUDY_ID_VALUE)
            .param(FdahpStudyDesignerConstants.BUTTON_TEXT, "completed")
            .headers(headers)
            .sessionAttrs(sessionAttributes);

    addParams(requestBuilder, studyBo);
    mockMvc.perform(requestBuilder).andDo(print()).andExpect(status().isFound());

    verifyAuditEventCall(STUDY_SETTINGS_MARKED_COMPLETE);
  }

  @Test
  public void shouldDeactivateStudy() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, "678592");

    mockMvc
        .perform(
            post(PathMappingUri.UPDATE_STUDY_ACTION.getPath())
                .param(FdahpStudyDesignerConstants.STUDY_ID, "678576")
                .param(FdahpStudyDesignerConstants.BUTTON_TEXT, "deactivateId")
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isOk());

    verifyAuditEventCall(STUDY_DEACTIVATED);
  }

  @Test
  public void shouldSaveStudyInDraftState() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(STUDY_ID_ATTR_NAME, STUDY_ID_VALUE);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, CUSTOM_STUDY_ID_VALUE);

    StudyBo studyBo = new StudyBo();
    studyBo.setId(STUDY_ID_INT_VALUE);
    studyBo.setCustomStudyId(CUSTOM_STUDY_ID_VALUE);
    studyBo.setVersion(1.1f);
    studyBo.setAppId("GCP112");
    studyBo.setStudySequenceBo(null);

    MockHttpServletRequestBuilder requestBuilder =
        post(PathMappingUri.SAVE_OR_UPDATE_BASIC_INFO.getPath())
            .headers(headers)
            .param(FdahpStudyDesignerConstants.BUTTON_TEXT, "save")
            .sessionAttrs(sessionAttributes);

    addParams(requestBuilder, studyBo);

    mockMvc.perform(requestBuilder).andDo(print()).andExpect(status().isFound());

    verifyAuditEventCall(STUDY_SAVED_IN_DRAFT_STATE);
  }

  @Test
  public void shouldMarkStudySectionAsComplete() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(STUDY_ID_ATTR_NAME, STUDY_ID_VALUE);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, CUSTOM_STUDY_ID_VALUE);

    StudyBo studyBo = new StudyBo();
    studyBo.setId(STUDY_ID_INT_VALUE);
    studyBo.setCustomStudyId(CUSTOM_STUDY_ID_VALUE);
    studyBo.setStudySequenceBo(null);
    studyBo.setAppId("GCP123");

    MockHttpServletRequestBuilder requestBuilder =
        post(PathMappingUri.SAVE_OR_UPDATE_BASIC_INFO.getPath())
            .headers(headers)
            .param(FdahpStudyDesignerConstants.BUTTON_TEXT, "completed")
            .sessionAttrs(sessionAttributes);

    addParams(requestBuilder, studyBo);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:viewBasicInfo.do"));

    verifyAuditEventCall(STUDY_BASIC_INFO_SECTION_MARKED_COMPLETE);
  }

  @Test
  public void shouldSaveOrUpdateStudySection() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(STUDY_ID_ATTR_NAME, STUDY_ID_VALUE);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, CUSTOM_STUDY_ID_VALUE);

    StudyBo studyBo = new StudyBo();
    studyBo.setId(STUDY_ID_INT_VALUE);
    studyBo.setCustomStudyId(CUSTOM_STUDY_ID_VALUE);
    studyBo.setVersion(0.0f);
    studyBo.setAppId("GCP123");
    studyBo.setStudySequenceBo(null);

    MockHttpServletRequestBuilder requestBuilder =
        post(PathMappingUri.SAVE_OR_UPDATE_BASIC_INFO.getPath())
            .headers(headers)
            .param(FdahpStudyDesignerConstants.BUTTON_TEXT, "save")
            .sessionAttrs(sessionAttributes);

    addParams(requestBuilder, studyBo);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:viewBasicInfo.do"));

    verifyAuditEventCall(STUDY_BASIC_INFO_SECTION_SAVED_OR_UPDATED);
  }

  @Test
  public void shouldCreateNewStudyResource() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(STUDY_ID_ATTR_NAME, STUDY_ID_VALUE);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, CUSTOM_STUDY_ID_VALUE);

    ResourceBO resourceBO = new ResourceBO();
    resourceBO.setResourceText("text");
    resourceBO.setAction(true);

    MockHttpServletRequestBuilder requestBuilder =
        post(PathMappingUri.SAVE_OR_UPDATE_RESOURCE.getPath())
            .headers(headers)
            .sessionAttrs(sessionAttributes);

    addParams(requestBuilder, resourceBO);

    mockMvc.perform(requestBuilder).andDo(print()).andExpect(status().isFound());

    List<NotificationBO> notificationList = notificationDaoImpl.getNotificationList(STUDY_ID_VALUE);
    assertTrue(notificationList.size() > 0);

    for (NotificationBO notification : notificationList) {
      if (notification.getCreatedBy().equals(USER_ID_VALUE)) {
        assertEquals(resourceBO.getResourceText(), notification.getNotificationText());
      }
    }

    verifyAuditEventCall(STUDY_NEW_RESOURCE_CREATED);
  }

  @Test
  public void shouldNotSaveNotificationBo() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(STUDY_ID_ATTR_NAME, STUDY_ID_VALUE);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, CUSTOM_STUDY_ID_VALUE);

    ResourceBO resourceBO = new ResourceBO();
    resourceBO.setResourceText("text");
    resourceBO.setAction(true);
    resourceBO.setResourceVisibility(false);

    MockHttpServletRequestBuilder requestBuilder =
        post(PathMappingUri.SAVE_OR_UPDATE_RESOURCE.getPath())
            .headers(headers)
            .param("resourceVisibilityParam", "0")
            .sessionAttrs(sessionAttributes);

    addParams(requestBuilder, resourceBO);

    mockMvc.perform(requestBuilder).andDo(print()).andExpect(status().isFound());

    List<NotificationBO> notificationList = notificationDaoImpl.getNotificationList(STUDY_ID_VALUE);
    assertEquals(0, notificationList.size());

    verifyAuditEventCall(STUDY_NEW_RESOURCE_CREATED);
  }

  @Test
  public void shouldSaveOrUpdateStudyResource() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(STUDY_ID_ATTR_NAME, STUDY_ID_VALUE);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, CUSTOM_STUDY_ID_VALUE);

    ResourceBO ResourceBO = new ResourceBO();

    MockHttpServletRequestBuilder requestBuilder =
        post(PathMappingUri.SAVE_OR_UPDATE_RESOURCE.getPath())
            .headers(headers)
            .param(FdahpStudyDesignerConstants.BUTTON_TEXT, "save")
            .sessionAttrs(sessionAttributes);

    addParams(requestBuilder, ResourceBO);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:addOrEditResource.do"));

    verifyAuditEventCall(STUDY_RESOURCE_SAVED_OR_UPDATED);
  }

  @Test
  public void shouldMarkStudyResourceAsComplete() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(STUDY_ID_ATTR_NAME, STUDY_ID_VALUE);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, CUSTOM_STUDY_ID_VALUE);

    ResourceBO ResourceBO = new ResourceBO();

    MockHttpServletRequestBuilder requestBuilder =
        post(PathMappingUri.SAVE_OR_UPDATE_RESOURCE.getPath())
            .headers(headers)
            .param(FdahpStudyDesignerConstants.BUTTON_TEXT, "completed")
            .sessionAttrs(sessionAttributes);

    addParams(requestBuilder, ResourceBO);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:getResourceList.do"));

    verifyAuditEventCall(STUDY_RESOURCE_MARKED_COMPLETED);
  }

  @Test
  public void shouldSaveOrUpdateStudyEligibilitySection() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(STUDY_ID_ATTR_NAME, STUDY_ID_VALUE);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, CUSTOM_STUDY_ID_VALUE);

    EligibilityBo eligibilityBo = new EligibilityBo();
    eligibilityBo.setStudyId(STUDY_ID_INT_VALUE);
    eligibilityBo.setActionType("save");

    MockHttpServletRequestBuilder requestBuilder =
        post(PathMappingUri.SAVE_OR_UPDATE_STUDY_ELIGIBILITY.getPath())
            .headers(headers)
            .sessionAttrs(sessionAttributes);

    addParams(requestBuilder, eligibilityBo);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:viewStudyEligibilty.do"));
    verifyAuditEventCall(STUDY_ELIGIBILITY_SECTION_SAVED_OR_UPDATED);
  }

  @Test
  public void shouldMarkStudyEligibilitySectionAsComplete() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(STUDY_ID_ATTR_NAME, STUDY_ID_VALUE);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, CUSTOM_STUDY_ID_VALUE);

    EligibilityBo eligibilityBo = new EligibilityBo();
    eligibilityBo.setStudyId(STUDY_ID_INT_VALUE);
    eligibilityBo.setActionType("complete");

    MockHttpServletRequestBuilder requestBuilder =
        post(PathMappingUri.SAVE_OR_UPDATE_STUDY_ELIGIBILITY.getPath())
            .headers(headers)
            .sessionAttrs(sessionAttributes);

    addParams(requestBuilder, eligibilityBo);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:viewStudyEligibilty.do"));
    verifyAuditEventCall(STUDY_ELIGIBILITY_SECTION_MARKED_COMPLETE);
  }

  @Test
  public void shouldReviewStudyAndSaveEConsent() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, CUSTOM_STUDY_ID_VALUE);

    ConsentBo consentBo = new ConsentBo();
    consentBo.setStudyId(STUDY_ID_INT_VALUE);
    consentBo.setType("save");

    mockMvc
        .perform(
            post(PathMappingUri.SAVE_CONSENT_REVIEW_AND_ECONSENT_INFO.getPath())
                .param("consentInfo", asJsonString(consentBo))
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isOk());
    verifyAuditEventCall(STUDY_REVIEW_AND_E_CONSENT_SAVED_OR_UPDATED);
  }

  @Test
  public void shouldMarkCompleteStudyReviewAndEConsent() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, "678590");

    ConsentBo consentBo = new ConsentBo();
    consentBo.setStudyId("678574");
    consentBo.setType("complete");
    consentBo.setConsentDocContent(
        "<span style=&#34;font-size:20px;&#34;><strong>Data gathering</strong></span><br/><span style=&#34;display: block; overflow-wrap: break-word; width: 100%;&#34;>Auto0016</span><br/>");

    mockMvc
        .perform(
            post(PathMappingUri.SAVE_CONSENT_REVIEW_AND_ECONSENT_INFO.getPath())
                .param("consentInfo", asJsonString(consentBo))
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isOk());
    verifyAuditEventCall(STUDY_REVIEW_AND_E_CONSENT_MARKED_COMPLETE);
  }

  @Test
  public void shouldConsentReviewAndEConsentInfo() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, CUSTOM_STUDY_ID_VALUE);

    ConsentBo consentBo = new ConsentBo();
    consentBo.setStudyId(STUDY_ID_INT_VALUE);
    consentBo.setComprehensionTest("save");
    consentBo.setConsentDocContent("doc");

    mockMvc
        .perform(
            post(PathMappingUri.SAVE_CONSENT_REVIEW_AND_ECONSENT_INFO.getPath())
                .param("consentInfo", asJsonString(consentBo))
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isOk());
    verifyAuditEventCall(STUDY_COMPREHENSION_TEST_SECTION_SAVED_OR_UPDATED);
  }

  @Test
  public void shouldMarkStudyComprehensionTestSectionAsComplete() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, CUSTOM_STUDY_ID_VALUE);

    ConsentBo consentBo = new ConsentBo();
    consentBo.setStudyId("678576");
    consentBo.setComprehensionTest("complete");
    consentBo.setConsentDocContent("doc");

    mockMvc
        .perform(
            post(PathMappingUri.SAVE_CONSENT_REVIEW_AND_ECONSENT_INFO.getPath())
                .param("consentInfo", asJsonString(consentBo))
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isOk());
    verifyAuditEventCall(STUDY_COMPREHENSION_TEST_SECTION_MARKED_COMPLETE);
  }

  @Test
  public void shouldViewStudyList() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, CUSTOM_STUDY_ID_VALUE);

    mockMvc
        .perform(
            post(PathMappingUri.STUDY_LIST.getPath())
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isOk());

    verifyAuditEventCall(STUDY_LIST_VIEWED);
  }

  @Test
  public void shouldPublishStudyConsentContent() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    SessionObject session = new SessionObject();
    session.setUserId("4878642");
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, "678599");

    mockMvc
        .perform(
            post(PathMappingUri.UPDATE_STUDY_ACTION.getPath())
                .param(FdahpStudyDesignerConstants.STUDY_ID, "678580")
                .param(FdahpStudyDesignerConstants.BUTTON_TEXT, "updatesId")
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isOk());
    verifyAuditEventCall(STUDY_CONSENT_DOCUMENT_NEW_VERSION_PUBLISHED);
  }

  @Test
  public void shouldPublishStudyConsentDoc() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    SessionObject session = new SessionObject();
    session.setUserId("4878642");
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, "678999");

    mockMvc
        .perform(
            post(PathMappingUri.UPDATE_STUDY_ACTION.getPath())
                .param(FdahpStudyDesignerConstants.STUDY_ID, TEST_STUDY_ID_STRING)
                .param(FdahpStudyDesignerConstants.BUTTON_TEXT, "updatesId")
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isOk());

    verifyAuditEventCall(STUDY_CONSENT_CONTENT_NEW_VERSION_PUBLISHED);
  }

  @Test
  public void shouldSendStudyMetadataToParticipantAndResponseDatastore() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, "678999");

    Map<String, String> map = FdahpStudyDesignerUtil.getAppProperties();
    String responseDatastoreUrl = map.get("responseServerUrl");
    String participantDatastoreUrl = map.get("userRegistrationServerUrl");
    String authServerUrl = map.get("security.oauth2.token_endpoint");

    RestGatewaySupport gateway = new RestGatewaySupport();
    gateway.setRestTemplate(restTemplate);
    mockServer = MockRestServiceServer.createServer(gateway);

    StudyDetailsBean studyDetailsBean = new StudyDetailsBean();
    studyDetailsBean.setStudyId(CUSTOM_STUDY_ID_VALUE);

    mockServer
        .expect(requestTo(authServerUrl))
        .andExpect(method(HttpMethod.POST))
        .andRespond(
            withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(readJsonFile("/token_response_oauth_scim_service.json")));

    mockServer
        .expect(requestTo(participantDatastoreUrl))
        .andExpect(method(HttpMethod.POST))
        .andRespond(
            withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(studyDetailsBean)));

    mockServer
        .expect(requestTo(responseDatastoreUrl))
        .andExpect(method(HttpMethod.POST))
        .andRespond(
            withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(studyDetailsBean)));

    mockMvc
        .perform(
            post(PathMappingUri.UPDATE_STUDY_ACTION.getPath())
                .param(FdahpStudyDesignerConstants.STUDY_ID, TEST_STUDY_ID_STRING)
                .param(FdahpStudyDesignerConstants.BUTTON_TEXT, "deactivateId")
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isOk());

    mockServer.verify();

    verifyAuditEventCall(STUDY_METADATA_SENT_TO_PARTICIPANT_DATASTORE);
    verifyAuditEventCall(STUDY_METADATA_SENT_TO_RESPONSE_DATASTORE);
  }

  @Test
  public void shouldFailSendingStudyMetadata() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    sessionAttributes.put(CUSTOM_STUDY_ID_ATTR_NAME, CUSTOM_STUDY_ID_VALUE);

    Map<String, String> map = FdahpStudyDesignerUtil.getAppProperties();
    String responseDatastoreUrl = map.get("responseServerUrl");
    String participantDatastoreUrl = map.get("userRegistrationServerUrl");
    String authServerUrl = map.get("security.oauth2.token_endpoint");

    RestGatewaySupport gateway = new RestGatewaySupport();
    gateway.setRestTemplate(restTemplate);
    mockServer = MockRestServiceServer.createServer(gateway);

    StudyDetailsBean studyDetailsBean = new StudyDetailsBean();
    studyDetailsBean.setStudyId(CUSTOM_STUDY_ID_VALUE);

    mockServer
        .expect(requestTo(authServerUrl))
        .andExpect(method(HttpMethod.POST))
        .andRespond(
            withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(readJsonFile("/token_response_oauth_scim_service.json")));

    mockServer
        .expect(requestTo(participantDatastoreUrl))
        .andExpect(method(HttpMethod.POST))
        .andRespond(
            withStatus(HttpStatus.FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(studyDetailsBean)));

    mockServer
        .expect(requestTo(responseDatastoreUrl))
        .andExpect(method(HttpMethod.POST))
        .andRespond(
            withStatus(HttpStatus.FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(studyDetailsBean)));

    mockMvc
        .perform(
            post(PathMappingUri.UPDATE_STUDY_ACTION.getPath())
                .param(FdahpStudyDesignerConstants.STUDY_ID, STUDY_ID_VALUE)
                .param(FdahpStudyDesignerConstants.BUTTON_TEXT, "deactivateId")
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isOk());

    mockServer.verify();

    verifyAuditEventCall(STUDY_METADATA_SEND_OPERATION_FAILED);
    verifyAuditEventCall(STUDY_METADATA_SEND_FAILED);
  }

  @Test
  public void shouldCreateInsertSqlQueries() throws Exception {

    SessionObject session = new SessionObject();
    session.setUserId("1");

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);

    mockMvc
        .perform(
            post("/studies/{studyId}/export.do", "f24b5b94l66b1n4286v8884w9ccb90306363")
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isOk());
  }

  public static String readJsonFile(String filepath) throws IOException {
    return JsonUtils.getObjectMapper()
        .readValue(JsonUtils.class.getResourceAsStream(filepath), JsonNode.class)
        .toString();
  }

  @Test
  public void shouldreplicateStudies() throws Exception {

    SessionObject session = new SessionObject();
    session.setUserId("1");

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);

    mockMvc
        .perform(post("/studies/{studyId}/replicate.do", "678574").sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isOk());
  }
}
