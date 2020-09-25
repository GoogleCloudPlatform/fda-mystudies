package com.fdahpstudydesigner.controller;

import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.ACCOUNT_DETAILS_VIEWED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NEW_USER_CREATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NEW_USER_CREATION_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NEW_USER_INVITATION_EMAIL_SENT;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NEW_USER_INVITATION_RESENT;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_CHANGE_ENFORCED_FOR_ALL_USERS;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.USER_RECORD_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.USER_RECORD_VIEWED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import com.fdahpstudydesigner.common.BaseMockIT;
import com.fdahpstudydesigner.common.PathMappingUri;
import org.junit.Test;
import org.springframework.http.HttpHeaders;

public class UsersControllerTest extends BaseMockIT {

  @Test
  public void shouldViewUserDetails() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    mockMvc
        .perform(
            post(PathMappingUri.VIEW_USER_DETAILS.getPath())
                .param("userId", "1")
                .param("checkViewRefreshFlag", "Google_009")
                .headers(headers)
                .sessionAttrs(getSessionAttributes()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(view().name("addOrEditUserPage"));

    verifyAuditEventCall(ACCOUNT_DETAILS_VIEWED);

    mockMvc
        .perform(
            post(PathMappingUri.VIEW_USER_DETAILS.getPath())
                .param("userId", "1")
                .param("checkViewRefreshFlag", "Google_009")
                .headers(headers)
                .sessionAttrs(getSessionAttributes()))
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
                .sessionAttrs(getSessionAttributes()))
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
                .param("changePassworduserId", "1")
                .param("emailId", "superadmin@gmail.com")
                .headers(headers)
                .sessionAttrs(getSessionAttributes()))
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/adminUsersView/getUserList.do"));

    verifyAuditEventCall(NEW_USER_INVITATION_RESENT);
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
                .sessionAttrs(getSessionAttributes()))
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/adminUsersView/getUserList.do"));

    verifyAuditEventCall(PASSWORD_CHANGE_ENFORCED_FOR_ALL_USERS);
  }

  @Test
  public void shouldAddUserDetails() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    mockMvc
        .perform(
            post(PathMappingUri.RESEND_ACTIVATE_DETAILS_LINK.getPath())
                .param("userId", "1")
                .param("manageUsers", "1")
                .param("manageNotifications", "1")
                .param("manageStudies", "1")
                .param("addingNewStudy", "1")
                .param("selectedStudies", "1")
                .param("permissionValues", "1")
                .param("ownUser", "1")
                .headers(headers)
                .sessionAttrs(getSessionAttributes()))
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/adminUsersView/getUserList.do"));

    verifyAuditEventCall(NEW_USER_CREATED);
    verifyAuditEventCall(NEW_USER_INVITATION_EMAIL_SENT);
    verifyAuditEventCall(NEW_USER_CREATED);
  }

  @Test
  public void shouldUpdateUserDetails() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    mockMvc
        .perform(
            post(PathMappingUri.RESEND_ACTIVATE_DETAILS_LINK.getPath())
                .param("userId", "1")
                .param("manageUsers", "1")
                .param("manageNotifications", "1")
                .param("manageStudies", "1")
                .param("addingNewStudy", "1")
                .param("selectedStudies", "1")
                .param("permissionValues", "1")
                .param("ownUser", "1")
                .headers(headers)
                .sessionAttrs(getSessionAttributes()))
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/adminUsersView/getUserList.do"));

    verifyAuditEventCall(USER_RECORD_UPDATED);
  }

  @Test
  public void shouldFailAddOrUpdateUserDetails() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    mockMvc
        .perform(
            post(PathMappingUri.RESEND_ACTIVATE_DETAILS_LINK.getPath())
                .param("userId", "1")
                .param("manageUsers", "1")
                .param("manageNotifications", "1")
                .param("manageStudies", "1")
                .param("addingNewStudy", "1")
                .param("selectedStudies", "1")
                .param("permissionValues", "1")
                .param("ownUser", "1")
                .headers(headers)
                .sessionAttrs(getSessionAttributes()))
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/adminUsersView/getUserList.do"));

    verifyAuditEventCall(NEW_USER_CREATION_FAILED);
  }
}
