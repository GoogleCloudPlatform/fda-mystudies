/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.USER_ACCOUNT_ACTIVATED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.USER_ACCOUNT_ACTIVATION_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.USER_ACCOUNT_ACTIVATION_FAILED_DUE_TO_EXPIRED_INVITATION;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.AuthUserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.BaseResponse;
import com.google.cloud.healthcare.fdamystudies.beans.PatchUserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.PatchUserResponse;
import com.google.cloud.healthcare.fdamystudies.beans.SetUpAccountRequest;
import com.google.cloud.healthcare.fdamystudies.beans.SetUpAccountResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateEmailStatusRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateEmailStatusResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UserProfileRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserProfileResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UserResponse;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerAuditLogHelper;
import com.google.cloud.healthcare.fdamystudies.common.UserAccountStatus;
import com.google.cloud.healthcare.fdamystudies.common.UserStatus;
import com.google.cloud.healthcare.fdamystudies.config.AppPropertyConfig;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.mapper.UserProfileMapper;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.repository.UserRegAdminRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
public class UserProfileServiceImpl implements UserProfileService {

  private XLogger logger = XLoggerFactory.getXLogger(UserProfileServiceImpl.class.getName());

  @Autowired private UserRegAdminRepository userRegAdminRepository;

  @Autowired private AppPropertyConfig appPropertyConfig;

  @Autowired private RestTemplate restTemplate;

  @Autowired private OAuthService oauthService;

  @Autowired private ParticipantManagerAuditLogHelper participantManagerHelper;

  @Override
  @Transactional(readOnly = true)
  public UserProfileResponse getUserProfile(String userId) {
    logger.entry("begin getUserProfile()");

    Optional<UserRegAdminEntity> optUserRegAdminUser =
        userRegAdminRepository.findByUrAdminAuthId(userId);

    if (!optUserRegAdminUser.isPresent()) {
      throw new ErrorCodeException(ErrorCode.USER_NOT_EXISTS);
    }

    UserRegAdminEntity adminUser = optUserRegAdminUser.get();
    if (!adminUser.isActive()) {
      throw new ErrorCodeException(ErrorCode.USER_NOT_ACTIVE);
    }

    UserProfileResponse userProfileResponse =
        UserProfileMapper.toUserProfileResponse(adminUser, MessageCode.GET_USER_PROFILE_SUCCESS);
    logger.exit(userProfileResponse.getMessage());
    return userProfileResponse;
  }

  @Override
  @Transactional(readOnly = true)
  public UserProfileResponse findUserProfileBySecurityCode(
      String securityCode, AuditLogEventRequest auditRequest) {
    logger.entry("begin getUserProfileBySecurityCode()");

    Optional<UserRegAdminEntity> optUserRegAdminUser =
        userRegAdminRepository.findBySecurityCode(securityCode);

    if (!optUserRegAdminUser.isPresent()) {
      return new UserProfileResponse("login", HttpStatus.OK.value());
    }

    UserRegAdminEntity user = optUserRegAdminUser.get();
    Timestamp now = new Timestamp(Instant.now().toEpochMilli());

    if (now.after(user.getSecurityCodeExpireDate())) {
      participantManagerHelper.logEvent(
          USER_ACCOUNT_ACTIVATION_FAILED_DUE_TO_EXPIRED_INVITATION, auditRequest);
      throw new ErrorCodeException(ErrorCode.SECURITY_CODE_EXPIRED);
    }

    UserProfileResponse userProfileResponse =
        UserProfileMapper.toUserProfileResponse(
            user, MessageCode.GET_USER_PROFILE_WITH_SECURITY_CODE_SUCCESS);
    logger.exit(String.format("message=%s", userProfileResponse.getMessage()));
    return userProfileResponse;
  }

  @Override
  @Transactional
  public UserProfileResponse updateUserProfile(UserProfileRequest userProfileRequest) {
    logger.entry("begin updateUserProfile()");

    Optional<UserRegAdminEntity> optUserRegAdminUser =
        userRegAdminRepository.findById(userProfileRequest.getUserId());

    if (!optUserRegAdminUser.isPresent()) {
      throw new ErrorCodeException(ErrorCode.USER_NOT_EXISTS);
    }

    UserRegAdminEntity adminUser = optUserRegAdminUser.get();
    if (!adminUser.isActive()) {
      throw new ErrorCodeException(ErrorCode.USER_NOT_ACTIVE);
    }
    adminUser.setFirstName(userProfileRequest.getFirstName());
    adminUser.setLastName(userProfileRequest.getLastName());
    userRegAdminRepository.saveAndFlush(adminUser);

    logger.exit(MessageCode.PROFILE_UPDATE_SUCCESS);
    return new UserProfileResponse(MessageCode.PROFILE_UPDATE_SUCCESS);
  }

  @Override
  @Transactional
  public SetUpAccountResponse saveUser(
      SetUpAccountRequest setUpAccountRequest, AuditLogEventRequest auditRequest) {
    logger.entry("saveUser");

    Optional<UserRegAdminEntity> optUsers =
        userRegAdminRepository.findByEmail(setUpAccountRequest.getEmail());

    auditRequest.setAppId("PARTICIPANT MANAGER");
    if (!optUsers.isPresent()) {
      participantManagerHelper.logEvent(USER_ACCOUNT_ACTIVATION_FAILED, auditRequest);
      throw new ErrorCodeException(ErrorCode.USER_NOT_INVITED);
    }

    // Bad request and errors handled in RestResponseErrorHandler class
    UserResponse authRegistrationResponse = registerUserInAuthServer(setUpAccountRequest);

    UserRegAdminEntity userRegAdminUser = optUsers.get();
    userRegAdminUser.setUrAdminAuthId(authRegistrationResponse.getUserId());
    userRegAdminUser.setFirstName(setUpAccountRequest.getFirstName());
    userRegAdminUser.setLastName(setUpAccountRequest.getLastName());
    userRegAdminUser.setStatus(UserStatus.ACTIVE.getValue());
    userRegAdminUser.setSecurityCode(null);
    userRegAdminUser.setSecurityCodeExpireDate(null);
    userRegAdminUser = userRegAdminRepository.saveAndFlush(userRegAdminUser);

    SetUpAccountResponse setUpAccountResponse =
        new SetUpAccountResponse(
            userRegAdminUser.getId(),
            authRegistrationResponse.getTempRegId(),
            authRegistrationResponse.getUserId(),
            MessageCode.SET_UP_ACCOUNT_SUCCESS);

    auditRequest.setUserId(userRegAdminUser.getId());
    participantManagerHelper.logEvent(USER_ACCOUNT_ACTIVATED, auditRequest);

    logger.exit(MessageCode.SET_UP_ACCOUNT_SUCCESS);
    return setUpAccountResponse;
  }

  private UserResponse registerUserInAuthServer(SetUpAccountRequest setUpAccountRequest) {
    logger.entry("registerUserInAuthServer()");

    AuthUserRequest userRequest =
        new AuthUserRequest(
            "Participant Manager",
            setUpAccountRequest.getEmail(),
            setUpAccountRequest.getPassword(),
            UserAccountStatus.ACTIVE.getStatus());

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + oauthService.getAccessToken());

    HttpEntity<AuthUserRequest> requestEntity = new HttpEntity<>(userRequest, headers);

    ResponseEntity<UserResponse> response =
        restTemplate.postForEntity(
            appPropertyConfig.getAuthRegisterUrl(), requestEntity, UserResponse.class);

    logger.exit(String.format("status=%d", response.getStatusCodeValue()));
    return response.getBody();
  }

  @Override
  public PatchUserResponse updateUserAccountStatus(PatchUserRequest statusRequest) {
    logger.entry("updateUserAccountStatus()");

    Optional<UserRegAdminEntity> optSuperAdmin =
        userRegAdminRepository.findById(statusRequest.getSignedInUserId());
    UserRegAdminEntity admin =
        optSuperAdmin.orElseThrow(() -> new ErrorCodeException(ErrorCode.USER_NOT_FOUND));
    if (!admin.isSuperAdmin()) {
      throw new ErrorCodeException(ErrorCode.NOT_SUPER_ADMIN_ACCESS);
    }

    Optional<UserRegAdminEntity> optUser =
        userRegAdminRepository.findById(statusRequest.getUserId());
    optUser.orElseThrow(() -> new ErrorCodeException(ErrorCode.USER_NOT_FOUND));

    UserStatus userStatus = UserStatus.fromValue(statusRequest.getStatus());
    if (userStatus == null) {
      throw new ErrorCodeException(ErrorCode.INVALID_USER_STATUS);
    }

    UserRegAdminEntity user = optUser.get();
    if (StringUtils.isNotEmpty(user.getUrAdminAuthId())) {
      updateUserAccountStatusInAuthServer(user.getUrAdminAuthId(), statusRequest.getStatus());
    }

    user.setStatus(statusRequest.getStatus());
    userRegAdminRepository.saveAndFlush(user);

    MessageCode messageCode =
        (user.getStatus() == UserStatus.ACTIVE.getValue()
            ? MessageCode.REACTIVATE_USER_SUCCESS
            : MessageCode.DEACTIVATE_USER_SUCCESS);

    logger.exit(messageCode);
    return new PatchUserResponse(messageCode);
  }

  private void updateUserAccountStatusInAuthServer(String authUserId, Integer status) {
    logger.entry("updateUserAccountStatusInAuthServer()");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("Authorization", "Bearer " + oauthService.getAccessToken());

    UpdateEmailStatusRequest emailStatusRequest = new UpdateEmailStatusRequest();
    UserStatus userStatus = UserStatus.fromValue(status);

    switch (userStatus) {
      case DEACTIVATED:
        emailStatusRequest.setStatus(UserAccountStatus.DEACTIVATED.getStatus());
        break;
      case ACTIVE:
        emailStatusRequest.setStatus(UserAccountStatus.ACTIVE.getStatus());
        break;
      default:
    }

    HttpEntity<UpdateEmailStatusRequest> request = new HttpEntity<>(emailStatusRequest, headers);

    ResponseEntity<UpdateEmailStatusResponse> responseEntity =
        restTemplate.exchange(
            appPropertyConfig.getAuthServerUpdateStatusUrl(),
            HttpMethod.PUT,
            request,
            UpdateEmailStatusResponse.class,
            authUserId);

    // Bad request and errors handled in RestResponseErrorHandler class

    logger.exit(
        String.format("status=%d", ((BaseResponse) responseEntity.getBody()).getHttpStatusCode()));
  }

  @Override
  public void deleteInvitation(String signedInUserId, String userId) {
    logger.entry("deleteInvitation()");

    Optional<UserRegAdminEntity> optSuperAdmin = userRegAdminRepository.findById(signedInUserId);
    UserRegAdminEntity admin =
        optSuperAdmin.orElseThrow(() -> new ErrorCodeException(ErrorCode.USER_NOT_FOUND));
    if (!admin.isSuperAdmin()) {
      throw new ErrorCodeException(ErrorCode.NOT_SUPER_ADMIN_ACCESS);
    }

    Optional<UserRegAdminEntity> optUser = userRegAdminRepository.findById(userId);
    UserRegAdminEntity user =
        optUser.orElseThrow(() -> new ErrorCodeException(ErrorCode.USER_NOT_FOUND));

    if (user.getStatus() != UserStatus.INVITED.getValue()) {
      throw new ErrorCodeException(ErrorCode.CANNOT_DELETE_INVITATION);
    }

    userRegAdminRepository.delete(user);

    logger.exit("Sucessfully deleted invitation");
  }
}
