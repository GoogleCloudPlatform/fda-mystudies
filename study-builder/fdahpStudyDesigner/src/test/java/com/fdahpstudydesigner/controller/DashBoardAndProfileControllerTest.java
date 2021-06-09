/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.fdahpstudydesigner.controller;

import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.ACCOUNT_DETAILS_VIEWED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_CHANGE_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_CHANGE_SUCCEEDED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.USER_ACCOUNT_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.USER_ACCOUNT_UPDATED_FAILED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import com.fdahpstudydesigner.common.BaseMockIT;
import com.fdahpstudydesigner.common.PathMappingUri;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.SessionObject;
import java.util.HashMap;
import java.util.UUID;
import org.junit.Test;
import org.springframework.http.HttpHeaders;

public class DashBoardAndProfileControllerTest extends BaseMockIT {

  private static final String USER_ID = "1";
  private static final String LAST_NAME = "Last-name";
  private static final String FIRST_NAME = "First-name";

  @Test
  public void shouldReturnPasswordChangedSuccessfully() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    mockMvc
        .perform(
            post(PathMappingUri.ADMIN_DASHBOARD_CHANGE_PASSWORD.getPath())
                .headers(headers)
                .param("newPassword", "BostonTechnology@123")
                .param("oldPassword", "Mock-it-Password")
                .sessionAttrs(getSessionObjectForDashboard()))
        .andDo(print())
        .andExpect(status().isOk());

    verifyAuditEventCall(PASSWORD_CHANGE_SUCCEEDED);
  }

  @Test
  public void shouldReturnPasswordChangedFailure() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    mockMvc
        .perform(
            post(PathMappingUri.ADMIN_DASHBOARD_CHANGE_PASSWORD.getPath())
                .headers(headers)
                .param("newPassword", "Mock-password-user")
                .param("oldPassword", "Invalid_For_User")
                .sessionAttrs(getSessionObjectForDashboard()))
        .andDo(print())
        .andExpect(status().isOk());

    verifyAuditEventCall(PASSWORD_CHANGE_FAILED);
  }

  @Test
  public void shouldUpdateUserAccountSuccessfully() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    mockMvc
        .perform(
            post(PathMappingUri.UPDATE_PROFILE_DETAILS.getPath())
                .headers(headers)
                .sessionAttrs(getSessionAttributes()))
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/adminDashboard/viewUserDetails.do"));

    verifyAuditEventCall(USER_ACCOUNT_UPDATED);
  }

  @Test
  public void shouldUpdateUserAccountFailure() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    mockMvc
        .perform(
            post(PathMappingUri.UPDATE_PROFILE_DETAILS.getPath())
                .headers(headers)
                .sessionAttrs(getSessionObjectForDashboard()))
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/adminDashboard/viewUserDetails.do"));

    verifyAuditEventCall(USER_ACCOUNT_UPDATED_FAILED);
  }

  @Test
  public void shouldViewUserAccountDetails() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    mockMvc
        .perform(
            get(PathMappingUri.ADMIN_DASHBOARD_VIEW_USER_DETAILS.getPath())
                .headers(headers)
                .sessionAttrs(getSessionObjectForDashboard()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(view().name("myAccount"));

    verifyAuditEventCall(ACCOUNT_DETAILS_VIEWED);
  }

  public HashMap<String, Object> getSessionObjectForDashboard() {
    HashMap<String, Object> sessionAttributesMap = getSessionAttributes();
    SessionObject session =
        (SessionObject) sessionAttributesMap.get(FdahpStudyDesignerConstants.SESSION_OBJECT);
    session.setSessionId(UUID.randomUUID().toString());
    session.setFirstName(FIRST_NAME);
    session.setLastName(LAST_NAME);
    session.setUserId(USER_ID);
    sessionAttributesMap.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    return sessionAttributesMap;
  }
}
