package com.google.cloud.healthcare.fdamystudies.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.asJsonString;
import static com.google.cloud.healthcare.fdamystudies.common.TestConstants.ADMIN_FIRST_NAME;
import static com.google.cloud.healthcare.fdamystudies.common.TestConstants.ADMIN_LAST_NAME;
import static com.google.cloud.healthcare.fdamystudies.common.TestConstants.USER_EMAIL_VALUE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.cloud.healthcare.fdamystudies.beans.SetUpAccountRequest;
import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.CommonConstants;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.common.UserAccountStatus;
import com.google.cloud.healthcare.fdamystudies.helper.TestDataHelper;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.repository.UserRegAdminRepository;
import com.google.cloud.healthcare.fdamystudies.service.UserProfileService;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

public class UserProfileControllerTest extends BaseMockIT {

  @Autowired private UserProfileController controller;

  @Autowired private UserProfileService userProfileService;

  @Autowired private TestDataHelper testDataHelper;

  private UserRegAdminEntity userRegAdminEntity;

  @Autowired UserRegAdminRepository userRegAdminRepository;

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
  public void shouldReturnUserProfile() throws Exception {
    mockMvc
        .perform(
            get(ApiEndpoint.GET_USER_PROFILE.getPath(), TestDataHelper.ADMIN_AUTH_ID_VALUE)
                .headers(new HttpHeaders())
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId", notNullValue()))
        .andExpect(jsonPath("$.firstName", is(TestDataHelper.ADMIN_FIRST_NAME)))
        .andExpect(jsonPath("$.lastName", is(TestDataHelper.ADMIN_LAST_NAME)))
        .andExpect(jsonPath("$.email", is(TestDataHelper.EMAIL_VALUE)))
        .andExpect(jsonPath("$.superAdmin", is(true)))
        .andExpect(jsonPath("$.message", is(MessageCode.GET_USER_PROFILE_SUCCESS.getMessage())));
  }

  @Test
  public void shouldReturnUserNotExistForUserProfile() throws Exception {
    mockMvc
        .perform(
            get(ApiEndpoint.GET_USER_PROFILE.getPath(), IdGenerator.id())
                .headers(new HttpHeaders())
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error_description", is(ErrorCode.USER_NOT_EXISTS.getDescription())));
  }

  @Test
  public void shouldReturnUserNotActiveForUserProfile() throws Exception {
    // Step 1: change the status to inactive
    userRegAdminEntity.setStatus(CommonConstants.INACTIVE_STATUS);
    userRegAdminRepository.saveAndFlush(userRegAdminEntity);

    // Step 2: Call API and expect error message USER_NOT_ACTIVE
    mockMvc
        .perform(
            get(ApiEndpoint.GET_USER_PROFILE.getPath(), TestDataHelper.ADMIN_AUTH_ID_VALUE)
                .headers(new HttpHeaders())
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error_description", is(ErrorCode.USER_NOT_ACTIVE.getDescription())));
  }

  @Test
  public void shouldReturnUserDetailsBySecurityCode() throws Exception {
    mockMvc
        .perform(
            get(
                    ApiEndpoint.GET_USER_DETAILS_BY_SECURITY_CODE.getPath(),
                    userRegAdminEntity.getSecurityCode())
                .headers(new HttpHeaders())
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId", notNullValue()))
        .andExpect(jsonPath("$.firstName", is(TestDataHelper.ADMIN_FIRST_NAME)))
        .andExpect(jsonPath("$.lastName", is(TestDataHelper.ADMIN_LAST_NAME)))
        .andExpect(jsonPath("$.email", is(TestDataHelper.EMAIL_VALUE)))
        .andExpect(
            jsonPath(
                "$.message",
                is(MessageCode.GET_USER_PROFILE_WITH_SECURITY_CODE_SUCCESS.getMessage())));
  }

  @Test
  public void shouldReturnNotFoundForUserDetailsBySecurityCode() throws Exception {
    mockMvc
        .perform(
            get(ApiEndpoint.GET_USER_DETAILS_BY_SECURITY_CODE.getPath(), IdGenerator.id())
                .headers(new HttpHeaders())
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.error_description", is(ErrorCode.INVALID_SECURITY_CODE.getDescription())));
  }

  @Test
  public void shouldReturnUnauthorizedForUserDetailsBySecurityCode() throws Exception {
    // Step 1: change the security code expire date to before current date
    userRegAdminEntity.setSecurityCodeExpireDate(
        new Timestamp(Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli()));
    userRegAdminRepository.saveAndFlush(userRegAdminEntity);

    // Step 2: Call API and expect error message SECURITY_CODE_EXPIRED
    mockMvc
        .perform(
            get(
                    ApiEndpoint.GET_USER_DETAILS_BY_SECURITY_CODE.getPath(),
                    userRegAdminEntity.getSecurityCode())
                .headers(new HttpHeaders())
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isUnauthorized())
        .andExpect(
            jsonPath("$.error_description", is(ErrorCode.SECURITY_CODE_EXPIRED.getDescription())));
  }

  @Test
  public void shouldSetUpNewAccount() throws Exception {
    // Step 1: Setting up the request for set up account
    SetUpAccountRequest request = setUpAccountRequest();
    userRegAdminEntity.setEmail(USER_EMAIL_VALUE);
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
    request.setEmail("Invalid@grr.la");

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
    userRegAdminEntity.setEmail(USER_EMAIL_VALUE);
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
    userRegAdminEntity.setEmail(USER_EMAIL_VALUE);
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

  @AfterEach
  public void cleanUp() {
    testDataHelper.getUserRegAdminRepository().deleteAll();
  }

  private SetUpAccountRequest setUpAccountRequest() {
    SetUpAccountRequest request = new SetUpAccountRequest();
    request.setEmail(USER_EMAIL_VALUE);
    request.setFirstName(ADMIN_FIRST_NAME);
    request.setLastName(ADMIN_LAST_NAME);
    request.setPassword("Kantharaj#1123");
    request.setAppId("PARTICIPANT MANAGER");
    request.setStatus(UserAccountStatus.ACTIVE.getStatus());
    return request;
  }
}
