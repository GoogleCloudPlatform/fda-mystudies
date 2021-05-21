/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.controller;

import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.ACCOUNT_DETAILS_VIEWED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NEW_USER_CREATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NEW_USER_INVITATION_EMAIL_SENT;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NEW_USER_INVITATION_RESENT;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_CHANGE_ENFORCED_FOR_ALL_USERS;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_CHANGE_ENFORCED_FOR_USER;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_CHANGE_ENFORCEMENT_EMAIL_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.USER_ACCOUNT_RE_ACTIVATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.USER_RECORD_DEACTIVATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.USER_RECORD_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.USER_RECORD_VIEWED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.common.BaseMockIT;
import com.fdahpstudydesigner.common.PathMappingUri;
import com.fdahpstudydesigner.common.UserAccessLevel;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.Mail;
import com.fdahpstudydesigner.util.SessionObject;
import java.util.HashMap;
import java.util.UUID;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class UsersControllerTest extends BaseMockIT {

  @Autowired Mail mail;

  @Test
  public void shouldViewUserDetails() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    mockMvc
        .perform(
            post(PathMappingUri.VIEW_USER_DETAILS.getPath())
                .param("userId", "2")
                .param("checkViewRefreshFlag", "true")
                .headers(headers)
                .sessionAttrs(getSession()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(view().name("addOrEditUserPage"));

    verifyAuditEventCall(ACCOUNT_DETAILS_VIEWED);
  }

  @Test
  public void shouldActivateUser() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    mockMvc
        .perform(
            post(PathMappingUri.ACTIVATE_OR_DEACTIVATE_USER.getPath())
                .param("userId", "2")
                .param("userStatus", "0")
                .headers(headers)
                .sessionAttrs(getSession()))
        .andDo(print())
        .andExpect(status().isOk());

    verifyAuditEventCall(USER_ACCOUNT_RE_ACTIVATED);
  }

  @Test
  public void shouldDeactivateUser() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    mockMvc
        .perform(
            post(PathMappingUri.ACTIVATE_OR_DEACTIVATE_USER.getPath())
                .param("userId", "2")
                .param("userStatus", "1")
                .headers(headers)
                .sessionAttrs(getSession()))
        .andDo(print())
        .andExpect(status().isOk());

    verifyAuditEventCall(USER_RECORD_DEACTIVATED);
  }

  @Test
  public void shouldUserRecordViewed() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    SessionObject session = new SessionObject();
    session.setSessionId(UUID.randomUUID().toString());
    session.setEmail("super@gmail.com");
    session.setFirstName("firstname");
    session.setLastName("lastname");
    session.setAccessLevel("2");
    session.setUserId("1");
    session.setAccessLevel(UserAccessLevel.STUDY_BUILDER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = new HashMap<String, Object>();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);

    mockMvc
        .perform(
            post(PathMappingUri.VIEW_USER_DETAILS.getPath())
                .param("userId", "2")
                .param("checkViewRefreshFlag", "true")
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(view().name("addOrEditUserPage"));

    verifyAuditEventCall(USER_RECORD_VIEWED);
  }

  @Test
  public void shouldResendActivateDetailsLink() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    mockMvc
        .perform(
            post(PathMappingUri.RESEND_ACTIVATE_DETAILS_LINK.getPath())
                .param("userId", "1")
                .headers(headers)
                .sessionAttrs(getSession()))
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/adminUsersView/getUserList.do"));

    verifyAuditEventCall(NEW_USER_INVITATION_RESENT);
  }

  @Test
  public void shouldEnforcePasswordChange() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    mockMvc
        .perform(
            post(PathMappingUri.ENFORCE_PASSWORD_CHANGE.getPath())
                .param("changePassworduserId", "2")
                .param("emailId", "invalid@gmail.com")
                .headers(headers)
                .sessionAttrs(getSession()))
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/adminUsersView/getUserList.do"));

    verifyAuditEventCall(PASSWORD_CHANGE_ENFORCED_FOR_USER);

    // H2 database doesn't support Column "BINARY". Expect LoginDAOImpl throws
    // org.h2.jdbc.JdbcSQLException: Column "BINARY" not found;
    verifyAuditEventCall(PASSWORD_CHANGE_ENFORCEMENT_EMAIL_FAILED);
  }

  @Test
  public void shouldEnforcePasswordChangeForAllUsers() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    mockMvc
        .perform(
            post(PathMappingUri.ENFORCE_PASSWORD_CHANGE.getPath())
                .param("changePassworduserId", "")
                .param("emailId", "")
                .headers(headers)
                .sessionAttrs(getSession()))
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/adminUsersView/getUserList.do"));

    verifyAuditEventCall(PASSWORD_CHANGE_ENFORCED_FOR_ALL_USERS);
  }

  @Test
  public void shouldUpdateUserDetails() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    UserBO userBo = new UserBO();
    userBo.setRoleId("1");
    userBo.setUserEmail("superunittest@grr.la");

    MockHttpServletRequestBuilder requestBuilder =
        post(PathMappingUri.ADD_OR_UPDATE_USER_DETAILS.getPath())
            .param("userId", "2")
            .param("manageUsers", "1")
            .param("manageNotifications", "1")
            .param("manageStudies", "1")
            .param("addingNewStudy", "1")
            .param("selectedStudies", "1")
            .param("permissionValues", "1")
            .param("ownUser", "1")
            .headers(headers)
            .sessionAttrs(getSessionAttributes());

    addParams(requestBuilder, userBo);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/adminUsersView/getUserList.do"));

    verifyAuditEventCall(USER_RECORD_UPDATED);
  }

  @Test
  public void shouldAddUserDetails() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    UserBO userBo = new UserBO();
    userBo.setUserEmail("newuser@grr.la");
    userBo.setFirstName("new_user_first_name");
    userBo.setLastName("new_user_last_name");
    userBo.setPhoneNumber("654665146432");
    userBo.setRoleId("2");

    MockHttpServletRequestBuilder requestBuilder =
        post(PathMappingUri.ADD_OR_UPDATE_USER_DETAILS.getPath())
            .param("manageUsers", "1")
            .param("manageNotifications", "1")
            .param("manageStudies", "1")
            .param("addingNewStudy", "1")
            .param("selectedStudies", "1")
            .param("permissionValues", "1")
            .param("ownUser", "1")
            .headers(headers)
            .sessionAttrs(getSessionAttributes());

    addParams(requestBuilder, userBo);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/adminUsersView/getUserList.do"));

    verifyAuditEventCall(NEW_USER_CREATED);
    verifyAuditEventCall(NEW_USER_INVITATION_EMAIL_SENT);
  }

  public HashMap<String, Object> getSession() {
    HashMap<String, Object> sessionAttributesMap = getSessionAttributes();
    SessionObject session =
        (SessionObject) sessionAttributesMap.get(FdahpStudyDesignerConstants.SESSION_OBJECT);
    session.setSessionId(UUID.randomUUID().toString());
    session.setEmail("super@gmail.com");
    session.setFirstName("firstname");
    session.setLastName("lastname");
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());
    session.setUserId("2");
    sessionAttributesMap.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);
    return sessionAttributesMap;
  }
}
