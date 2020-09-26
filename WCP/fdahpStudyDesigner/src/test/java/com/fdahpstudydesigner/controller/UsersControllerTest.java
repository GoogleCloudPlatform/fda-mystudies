package com.fdahpstudydesigner.controller;

import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.ACCOUNT_DETAILS_VIEWED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NEW_USER_CREATION_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_CHANGE_ENFORCED_FOR_ALL_USERS;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_CHANGE_ENFORCED_FOR_USER;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_CHANGE_ENFORCEMENT_EMAIL_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_HELP_EMAIL_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.USER_ACCOUNT_UPDATED_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.USER_RECORD_VIEWED;
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

public class UsersControllerTest extends BaseMockIT {

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
  public void shouldUserRecordViewed() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    SessionObject session = new SessionObject();
    session.setSessionId(UUID.randomUUID().toString());
    session.setEmail("super@gmail.com");
    session.setFirstName("firstname");
    session.setLastName("lastname");
    session.setAccessLevel("2");
    session.setUserId(1);

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
                .param("userId", "2")
                .headers(headers)
                .sessionAttrs(getSession()))
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/adminUsersView/getUserList.do"));

    // H2 database doesn't support Column "BINARY". Expect LoginDAOImpl throws
    // org.h2.jdbc.JdbcSQLException: Column "BINARY" not found;
    verifyAuditEventCall(PASSWORD_HELP_EMAIL_FAILED);
  }

  @Test
  public void shouldEnforcePasswordChange() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    mockMvc
        .perform(
            post(PathMappingUri.ENFORCE_PASSWORD_CHANGE.getPath())
                .param("changePassworduserId", "2")
                .param("emailId", "super@gmail.com")
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

    mockMvc
        .perform(
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
                .sessionAttrs(getSession()))
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/adminUsersView/getUserList.do"));

    // H2 database doesn't support Column "BINARY". Expect LoginDAOImpl throws
    // org.h2.jdbc.JdbcSQLException: Column "BINARY" not found;
    verifyAuditEventCall(USER_ACCOUNT_UPDATED_FAILED);
  }

  @Test
  public void shouldAddUserDetails() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    mockMvc
        .perform(
            post(PathMappingUri.ADD_OR_UPDATE_USER_DETAILS.getPath())
                .param("manageUsers", "1")
                .param("manageNotifications", "1")
                .param("manageStudies", "1")
                .param("addingNewStudy", "1")
                .param("selectedStudies", "1")
                .param("permissionValues", "1")
                .param("ownUser", "1")
                .headers(headers)
                .sessionAttrs(getSession()))
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/adminUsersView/getUserList.do"));

    // H2 database doesn't support Column "BINARY". Expect LoginDAOImpl throws
    // org.h2.jdbc.JdbcSQLException: Column "BINARY" not found;
    verifyAuditEventCall(NEW_USER_CREATION_FAILED);
  }

  protected SessionObject getSessionObject() {
    SessionObject session = new SessionObject();
    session.setSessionId(UUID.randomUUID().toString());
    session.setEmail(SESSION_USER_EMAIL);
    return session;
  }

  private HashMap<String, Object> getSession() {
    SessionObject session = new SessionObject();
    session.setSessionId(UUID.randomUUID().toString());
    session.setEmail("super@gmail.com");
    session.setFirstName("firstname");
    session.setLastName("lastname");
    session.setAccessLevel("2");
    session.setUserId(2);

    HashMap<String, Object> sessionAttributes = new HashMap<String, Object>();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);

    return sessionAttributes;
  }
}
