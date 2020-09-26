/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.controller;

import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_NEW_NOTIFICATION_CREATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_NOTIFICATION_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_NOTIFICATION_SAVED_OR_UPDATED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import com.fdahpstudydesigner.bo.NotificationBO;
import com.fdahpstudydesigner.common.BaseMockIT;
import com.fdahpstudydesigner.common.PathMappingUri;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class StudyControllerTest extends BaseMockIT {

  @Test
  public void shouldSaveOrUpdateOrResendNotificationForSave() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    NotificationBO notificationBo = new NotificationBO();
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

    verifyAuditEventCall(STUDY_NOTIFICATION_SAVED_OR_UPDATED);
  }

  @Test
  public void shouldSaveOrUpdateOrResendNotificationForAdd() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    NotificationBO notificationBo = new NotificationBO();
    notificationBo.setNotificationText("Study notification");

    MockHttpServletRequestBuilder requestBuilder =
        post(PathMappingUri.SAVE_OR_UPDATE_STUDY_NOTIFICATION.getPath())
            .headers(headers)
            .param("buttonType", "add")
            .sessionAttr("copyAppNotification", true)
            .sessionAttrs(getSessionAttributes());

    addParams(requestBuilder, notificationBo);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/adminStudies/viewStudyNotificationList.do"));

    verifyAuditEventCall(STUDY_NEW_NOTIFICATION_CREATED);
  }

  @Test
  public void shouldSaveOrUpdateOrResendNotificationForDone() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    NotificationBO notificationBo = new NotificationBO();
    notificationBo.setNotificationText("Study notification");

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
}
