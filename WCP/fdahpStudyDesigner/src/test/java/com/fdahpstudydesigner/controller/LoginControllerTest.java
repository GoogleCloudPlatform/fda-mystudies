/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.controller;

import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NEW_USER_ACCOUNT_ACTIVATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NEW_USER_ACCOUNT_ACTIVATION_FAILED_INVALID_ACCESS_CODE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_CHANGE_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_CHANGE_SUCCEEDED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_HELP_EMAIL_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_RESET_EMAIL_SENT_FOR_LOCKED_ACCOUNT;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_RESET_SUCCEEDED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.USER_SIGNOUT_SUCCEEDED;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.common.BaseMockIT;
import com.fdahpstudydesigner.common.PathMappingUri;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.SessionObject;
import java.util.HashMap;
import java.util.UUID;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class LoginControllerTest extends BaseMockIT {

  @Test
  public void shouldLogoutSuccessfully() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    mockMvc
        .perform(
            get(PathMappingUri.SESSION_OUT.getPath())
                .headers(headers)
                .sessionAttrs(getSessionAttributes()))
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:login.do"));

    verifyAuditEventCall(USER_SIGNOUT_SUCCEEDED);
  }

  @Test
  public void shouldThrowBadRequestException() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.set("source", "INVALID STUDY BUILDER");
    try {
      mockMvc
          .perform(
              get(PathMappingUri.SESSION_OUT.getPath())
                  .headers(headers)
                  .sessionAttrs(getSessionAttributes()))
          .andDo(print())
          .andExpect(status().isFound())
          .andExpect(view().name("redirect:login.do"));
      fail(
          "shouldThrowBadRequestException() didn't throw BadRequestException when I expected it to");
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("Invalid 'source' value"));
    }
  }

  @Test
  public void shouldChangePassword() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    mockMvc
        .perform(
            post(PathMappingUri.CHANGE_PASSWORD.getPath())
                .param("oldPassword", "Mock-it-Password")
                .param("newPassword", "newPD@009")
                .param("_csrf", "")
                .headers(headers)
                .sessionAttrs(getSession()))
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(
            view()
                .name(
                    "redirect:sessionOut.do?sucMsg=Your password has been changed successfully. Please login again with new password."));

    verifyAuditEventCall(PASSWORD_CHANGE_SUCCEEDED);
  }

  @Test
  public void shouldNotChangePassword() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    SessionObject session = new SessionObject();
    session.setSessionId(UUID.randomUUID().toString());
    session.setEmail("superadmin@gmail.com");
    session.setFirstName("Account");
    session.setLastName("Manager");
    session.setUserId(1);

    HashMap<String, Object> sessionAttributes = new HashMap<String, Object>();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);

    mockMvc
        .perform(
            post(PathMappingUri.CHANGE_PASSWORD.getPath())
                .param("oldPassword", "Password@1234")
                .param("newPassword", "rAndom_009")
                .param("_csrf", "")
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/profile/changeExpiredPassword.do"));

    verifyAuditEventCall(PASSWORD_CHANGE_FAILED);
  }

  @Test
  public void testForgotPasswordFailed() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    mockMvc
        .perform(
            get(PathMappingUri.FORGOT_PASSWORD.getPath())
                .param("email", "ttt@gmail.com")
                .headers(headers)
                .sessionAttrs(getSessionAttributes()))
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:login.do"));

    // H2 database doesn't support Column "BINARY". Expect LoginDAOImpl throws
    // org.h2.jdbc.JdbcSQLException: Column "BINARY" not found;
    verifyAuditEventCall(PASSWORD_HELP_EMAIL_FAILED);
  }

  @Test
  public void shouldValidateSecurityToken() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    mockMvc
        .perform(
            get(PathMappingUri.SECURITY_TOKEN_VALIDATION.getPath())
                .param("securityToken", "NK7zYrc0F")
                .headers(headers)
                .sessionAttrs(getSessionAttributes()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(view().name("userPasswordReset"));

    verifyAuditEventCall(PASSWORD_RESET_EMAIL_SENT_FOR_LOCKED_ACCOUNT);
  }

  @Test
  public void shouldActivateNewUserAccount() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    UserBO userBO = new UserBO();
    userBO.setFirstName("updated_first_name");
    userBO.setLastName("updated_last_name");
    userBO.setPhoneNumber("654665146432");

    MockHttpServletRequestBuilder requestBuilder =
        post(PathMappingUri.ADD_PASSWORD.getPath())
            .param("accessCode", "ja67Ll")
            .param("password", "Password@1234")
            .param("securityToken", "N8K7zYrc0F")
            .param("_csrf", "")
            .headers(headers)
            .sessionAttrs(getSessionAttributes());

    addParams(requestBuilder, userBO);
    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:login.do"));

    verifyAuditEventCall(NEW_USER_ACCOUNT_ACTIVATED);
    verifyAuditEventCall(PASSWORD_RESET_SUCCEEDED);
  }

  @Test
  public void shouldNotAddPasswordForInvalidAccessCode() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    UserBO userBO = new UserBO();
    userBO.setFirstName("updated_first_name");
    userBO.setLastName("updated_last_name");
    userBO.setPhoneNumber("654665146432");

    MockHttpServletRequestBuilder requestBuilder =
        post(PathMappingUri.ADD_PASSWORD.getPath())
            .param("accessCode", "jf47Ll")
            .param("password", "Password@1234")
            .param("securityToken", "N8K7zYrc0F")
            .param("_csrf", "")
            .headers(headers)
            .sessionAttrs(getSessionAttributes());

    addParams(requestBuilder, userBO);
    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:createPassword.do?securityToken=N8K7zYrc0F"));

    verifyAuditEventCall(NEW_USER_ACCOUNT_ACTIVATION_FAILED_INVALID_ACCESS_CODE);
  }

  public HashMap<String, Object> getSession() {
    HashMap<String, Object> sessionAttributesMap = getSessionAttributes();
    SessionObject session =
        (SessionObject) sessionAttributesMap.get(FdahpStudyDesignerConstants.SESSION_OBJECT);
    session.setSessionId(UUID.randomUUID().toString());
    session.setEmail("super@gmail.com");
    session.setFirstName("firstname");
    session.setLastName("lastname");
    session.setUserId(3);
    sessionAttributesMap.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    return sessionAttributesMap;
  }
}
