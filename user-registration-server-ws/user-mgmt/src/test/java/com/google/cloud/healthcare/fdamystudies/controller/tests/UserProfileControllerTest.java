package com.google.cloud.healthcare.fdamystudies.controller.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import com.google.cloud.healthcare.fdamystudies.bean.StudyReqBean;
import com.google.cloud.healthcare.fdamystudies.beans.DeactivateAcctBean;
import com.google.cloud.healthcare.fdamystudies.beans.LoginBean;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequestBean;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.controller.UserProfileController;
import com.google.cloud.healthcare.fdamystudies.service.UserManagementProfileService;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;

public class UserProfileControllerTest extends BaseMockIT {

  private static final String PING_PATH = "/ping";
  private static final String USER_PROFILE_PATH = "/userProfile";
  private static final String UPDATE_USER_PROFILE_PATH = "/updateUserProfile";
  private static final String DEACTIVATE_PATH = "/deactivate";
  private static final String RESEND_CONFIRMATION_PATH = "/resendConfirmation";

  @Autowired UserProfileController profileController;
  @Autowired UserManagementProfileService profileService;

  @Test
  public void contextLoads() {
    assertNotNull(profileController);
    assertNotNull(mockMvc);
    assertNotNull(profileService);
  }

  @Test
  public void ping() throws Exception {
    performGet(PING_PATH, "", TestUtils.getCommonHeaders(Constants.USER_ID_HEADER), "", OK);
  }

  @Test
  public void getUserProfile() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.USER_ID_HEADER);
    performGet(USER_PROFILE_PATH, "", headers, "", OK);
  }

  @Test
  public void getUserProfileInvalid() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders();

    // Invalid userId
    headers.set(Constants.USER_ID_HEADER, Constants.INVALID_USER_ID);
    performGet(USER_PROFILE_PATH, "", headers, "", BAD_REQUEST);
  }

  @Test
  public void updateUserProfile() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.USER_ID_HEADER);

    UserRequestBean bean = new UserRequestBean();
    String requestJson = getObjectMapper().writeValueAsString(bean);
    performPost(UPDATE_USER_PROFILE_PATH, requestJson, headers, "", OK);
  }

  @Test
  public void deactivateAccount() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.USER_ID_HEADER);

    StudyReqBean studyReqBean = new StudyReqBean(Constants.STUDY_ID, Constants.DELETE);
    List<StudyReqBean> list = new ArrayList<StudyReqBean>();
    list.add(studyReqBean);
    DeactivateAcctBean acctBean = new DeactivateAcctBean(list);
    String requestJson = getObjectMapper().writeValueAsString(acctBean);
    performDelete(DEACTIVATE_PATH, requestJson, headers, "", OK);
  }

  @Test
  public void resendConfirmationInvalid() throws Exception {

    HttpHeaders headers =
        TestUtils.getCommonHeaders(Constants.APP_ID_HEADER, Constants.ORG_ID_HEADER);

    // without email
    LoginBean loginBean = new LoginBean(null, Constants.PASSWORD);
    String requestJson = getObjectMapper().writeValueAsString(loginBean);
    performPost(RESEND_CONFIRMATION_PATH, requestJson, headers, "", BAD_REQUEST);

    // invalid email
    loginBean = new LoginBean(Constants.INVALID_EMAIL, Constants.PASSWORD);
    requestJson = getObjectMapper().writeValueAsString(loginBean);
    performPost(RESEND_CONFIRMATION_PATH, requestJson, headers, "", BAD_REQUEST);

    // without appId
    headers.set(Constants.APP_ID_HEADER, "");
    loginBean = new LoginBean(Constants.EMAIL_ID, Constants.PASSWORD);
    requestJson = getObjectMapper().writeValueAsString(loginBean);
    performPost(RESEND_CONFIRMATION_PATH, requestJson, headers, "", BAD_REQUEST);

    // without OrgId
    headers.set(Constants.APP_ID_HEADER, Constants.APP_ID_VALUE);
    headers.set(Constants.ORG_ID_HEADER, "");
    loginBean = new LoginBean(Constants.EMAIL_ID, Constants.PASSWORD);
    requestJson = getObjectMapper().writeValueAsString(loginBean);
    performPost(RESEND_CONFIRMATION_PATH, requestJson, headers, "", BAD_REQUEST);
  }

  @Test
  public void resendConfirmation() throws Exception {

    HttpHeaders headers =
        TestUtils.getCommonHeaders(Constants.APP_ID_HEADER, Constants.ORG_ID_HEADER);

    LoginBean bean = new LoginBean(Constants.VALID_EMAIL, Constants.PASSWORD);
    String requestJson = getObjectMapper().writeValueAsString(bean);
    performPost(RESEND_CONFIRMATION_PATH, requestJson, headers, "", OK);
  }
}
