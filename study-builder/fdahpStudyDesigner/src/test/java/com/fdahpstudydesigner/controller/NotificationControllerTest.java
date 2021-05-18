/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.controller;

import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_LEVEL_NOTIFICATION_CREATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_LEVEL_NOTIFICATION_LIST_VIEWED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_LEVEL_NOTIFICATION_REPLICATED_FOR_RESEND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

public class NotificationControllerTest extends BaseMockIT {

  @Test
  public void shouldViewNotificationList() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    mockMvc
        .perform(
            get(PathMappingUri.VIEW_NOTIFICATION_LIST.getPath())
                .headers(headers)
                .sessionAttrs(getSessionAttributes()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(view().name("notificationListPage"));

    verifyAuditEventCall(APP_LEVEL_NOTIFICATION_LIST_VIEWED);
  }

  @Test
  public void shouldSaveOrUpdateOrResendNotification() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    NotificationBO notificationBo = new NotificationBO();
    notificationBo.setNotificationText("Study notification");
    notificationBo.setNotificationType("App Level");

    MockHttpServletRequestBuilder requestBuilder =
        post(PathMappingUri.SAVE_OR_UPDATE_NOTIFICATION.getPath())
            .headers(headers)
            .param("buttonType", "resend")
            .sessionAttrs(getSessionAttributes());

    addParams(requestBuilder, notificationBo);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/adminNotificationView/viewNotificationList.do"));

    verifyAuditEventCall(APP_LEVEL_NOTIFICATION_REPLICATED_FOR_RESEND);
  }

  @Test
  public void shouldSaveOrUpdateNotification() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    NotificationBO notificationBo = new NotificationBO();
    notificationBo.setNotificationText("Study notification");

    MockHttpServletRequestBuilder requestBuilder =
        post(PathMappingUri.SAVE_OR_UPDATE_NOTIFICATION.getPath())
            .headers(headers)
            .param("buttonType", "add")
            .sessionAttr("copyAppNotification", false)
            .sessionAttrs(getSessionAttributes());

    addParams(requestBuilder, notificationBo);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/adminNotificationView/viewNotificationList.do"));

    verifyAuditEventCall(APP_LEVEL_NOTIFICATION_CREATED);
  }
}
