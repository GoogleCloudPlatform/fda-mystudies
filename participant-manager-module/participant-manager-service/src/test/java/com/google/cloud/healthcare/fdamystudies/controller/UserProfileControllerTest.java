package com.google.cloud.healthcare.fdamystudies.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.cloud.healthcare.fdamystudies.beans.SetUpAccountRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateEmailStatusRequest;
import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.common.TestConstants;
import com.google.cloud.healthcare.fdamystudies.common.UserAccountStatus;
import com.google.cloud.healthcare.fdamystudies.common.UserStatus;
import com.google.cloud.healthcare.fdamystudies.helper.TestDataHelper;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.repository.UserRegAdminRepository;
import com.google.cloud.healthcare.fdamystudies.service.UserProfileService;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.asJsonString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserProfileControllerTest extends BaseMockIT {

  @Autowired private UserProfileController controller;

  @Autowired private UserProfileService userProfileService;

  @Autowired private TestDataHelper testDataHelper;

  private UserRegAdminEntity userRegAdminEntity;

  @Autowired UserRegAdminRepository userRegAdminRepository;

  public static final String EMAIL_VALUE = "mockit_email@grr.la";

  protected static final String VALID_BEARER_TOKEN = "Bearer 7fd50c2c-d618-493c-89d6-f1887e3e4bb8";

  @Test
  public void contextLoads() {
    assertNotNull(controller);
    assertNotNull(mockMvc);
    assertNotNull(userProfileService);
  }

  @BeforeEach
  public void setUp() {
    userRegAdminEntity = testDataHelper.createUserRegAdmin();
    WireMock.resetAllRequests();
  }

  @Test
  public void shouldSetUpNewAccount() throws Exception {
    // Step 1: Setting up the request for set up account
    SetUpAccountRequest request = setUpAccountRequest();
    userRegAdminEntity.setEmail(TestConstants.USER_EMAIL_VALUE);
    testDataHelper.getUserRegAdminRepository().saveAndFlush(userRegAdminEntity);

    // Step 2: Call the API and expect SET_UP_ACCOUNT_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();

    mockMvc
        .perform(
            post(ApiEndpoint.SET_UP_ACCOUNT.getPath())
                .content(asJsonString(request))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value(MessageCode.SET_UP_ACCOUNT_SUCCESS.getMessage()))
        .andExpect(jsonPath("$.userId", notNullValue()))
        .andReturn();

    // Step 3: verify saved values
    Optional<UserRegAdminEntity> optUser = userRegAdminRepository.findByEmail(request.getEmail());
    UserRegAdminEntity user = optUser.get();
    assertEquals(request.getFirstName(), user.getFirstName());
    assertEquals(request.getLastName(), user.getLastName());

    verify(1, postRequestedFor(urlEqualTo("/oauth-scim-service/users")));
  }

  @Test
  public void shouldReturnUserNotInvitedError() throws Exception {
    // Step 1: Setting up the request
    SetUpAccountRequest request = setUpAccountRequest();

    // Step 2: Call the API and expect USER_NOT_INVITED error
    HttpHeaders headers = testDataHelper.newCommonHeaders();

    mockMvc
        .perform(
            post(ApiEndpoint.SET_UP_ACCOUNT.getPath())
                .content(asJsonString(request))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.error_description", is(ErrorCode.USER_NOT_INVITED.getDescription())));
  }

  @Test
  public void shouldReturnAuthServerApplicationError() throws Exception {
    // Step 1: Setting up the request for AuthServerApplicationError
    SetUpAccountRequest request = setUpAccountRequest();
    userRegAdminEntity.setEmail(TestConstants.USER_EMAIL_VALUE);
    request.setPassword("AuthServerError@b0ston");
    testDataHelper.getUserRegAdminRepository().saveAndFlush(userRegAdminEntity);

    // Step 2: Call the API and expect APPLICATION_ERROR error
    HttpHeaders headers = testDataHelper.newCommonHeaders();

    mockMvc
        .perform(
            post(ApiEndpoint.SET_UP_ACCOUNT.getPath())
                .content(asJsonString(request))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isInternalServerError())
        .andExpect(
            jsonPath("$.error_description", is(ErrorCode.APPLICATION_ERROR.getDescription())));
  }

  @Test
  public void shouldReturnAuthServerBadRequestError() throws Exception {
    // Step 1: Setting up the request for bad request
    SetUpAccountRequest request = setUpAccountRequest();
    userRegAdminEntity.setEmail(TestConstants.USER_EMAIL_VALUE);
    request.setPassword("AuthServerBadRequest@b0ston");
    testDataHelper.getUserRegAdminRepository().saveAndFlush(userRegAdminEntity);

    // Step 2: Call the API and expect BAD_REQUEST error
    HttpHeaders headers = testDataHelper.newCommonHeaders();

    mockMvc
        .perform(
            post(ApiEndpoint.SET_UP_ACCOUNT.getPath())
                .content(asJsonString(request))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldDeactivateUserAccount() throws Exception {
    // Step 1: Setting up the request for deactivate account
    UpdateEmailStatusRequest emailStatusRequest = new UpdateEmailStatusRequest();
    emailStatusRequest.setStatus(UserAccountStatus.DEACTIVATED.getStatus());

    // Step 2: Call the API and expect DEACTIVATE_USER_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();

    mockMvc
        .perform(
            patch(ApiEndpoint.DEACTIVATE_ACCOUNT.getPath(), userRegAdminEntity.getId())
                .content(asJsonString(emailStatusRequest))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(MessageCode.DEACTIVATE_USER_SUCCESS.getMessage()))
        .andReturn();

    // Step 3: verify updated values
    Optional<UserRegAdminEntity> optUser =
        userRegAdminRepository.findById(userRegAdminEntity.getId());
    UserRegAdminEntity user = optUser.get();
    // assertEquals(request.getEmail(), user.getEmail());
    assertEquals(UserStatus.DEACTIVATED.getValue(), user.getStatus());

    // verify external API call
    verify(
        1,
        putRequestedFor(
            urlEqualTo(
                "/oauth-scim-service/users/TuKUeFdyWz4E2A1-LqQcoYKBpMsfLnl-KjiuRFuxWcM3sQg")));
  }

  @Test
  public void shouldReturnUserNotFoundForDeactivateUser() throws Exception {
    // Step 2: Call the API and expect USER_NOT_FOUND error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    UpdateEmailStatusRequest emailStatusRequest = new UpdateEmailStatusRequest();
    mockMvc
        .perform(
            patch(ApiEndpoint.DEACTIVATE_ACCOUNT.getPath(), IdGenerator.id())
                .content(asJsonString(emailStatusRequest))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error_description", is(ErrorCode.USER_NOT_FOUND.getDescription())));
  }

  @AfterEach
  public void cleanUp() {
    testDataHelper.getUserRegAdminRepository().deleteAll();
  }

  private SetUpAccountRequest setUpAccountRequest() {
    SetUpAccountRequest request = new SetUpAccountRequest();
    request.setEmail(TestConstants.USER_EMAIL_VALUE);
    request.setFirstName(TestDataHelper.ADMIN_FIRST_NAME);
    request.setLastName(TestDataHelper.ADMIN_LAST_NAME);
    request.setPassword("Kantharaj#1123");
    request.setAppId("PARTICIPANT MANAGER");
    request.setStatus(UserAccountStatus.ACTIVE.getStatus());
    return request;
  }
}
