/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.service;

import static com.google.cloud.healthcare.fdamystudies.common.EncryptionUtils.encrypt;
import static com.google.cloud.healthcare.fdamystudies.common.EncryptionUtils.hash;
import static com.google.cloud.healthcare.fdamystudies.common.EncryptionUtils.salt;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.createArrayNode;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.getObjectNode;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.getTextValue;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.ACCOUNT_LOCK_EMAIL_TIMESTAMP;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.EXPIRE_TIMESTAMP;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.HASH;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.LOGIN_ATTEMPTS;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.LOGIN_TIMESTAMP;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.OTP_USED;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.PASSWORD;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.PASSWORD_HISTORY;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.SALT;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.TEMP_PASSWORD_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.PASSWORD_RESET_FAILED;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.PASSWORD_RESET_SUCCESS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.AuthenticationResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ChangePasswordRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ChangePasswordResponse;
import com.google.cloud.healthcare.fdamystudies.beans.EmailRequest;
import com.google.cloud.healthcare.fdamystudies.beans.EmailResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ResetPasswordRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ResetPasswordResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateEmailStatusRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateEmailStatusResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserResponse;
import com.google.cloud.healthcare.fdamystudies.common.DateTimeUtils;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.common.PasswordGenerator;
import com.google.cloud.healthcare.fdamystudies.common.UserAccountStatus;
import com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimAuditLogHelper;
import com.google.cloud.healthcare.fdamystudies.oauthscim.config.AppPropertyConfig;
import com.google.cloud.healthcare.fdamystudies.oauthscim.mapper.UserMapper;
import com.google.cloud.healthcare.fdamystudies.oauthscim.model.UserEntity;
import com.google.cloud.healthcare.fdamystudies.oauthscim.repository.UserRepository;
import com.google.cloud.healthcare.fdamystudies.service.EmailService;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

  private XLogger logger = XLoggerFactory.getXLogger(UserServiceImpl.class.getName());

  @Autowired private UserRepository repository;

  @Autowired private AppPropertyConfig appConfig;

  @Autowired private EmailService emailService;

  @Autowired private AuthScimAuditLogHelper auditHelper;

  @Override
  public UserResponse createUser(UserRequest userRequest) {
    logger.entry("begin createUser()");

    // check if the email already been used
    Optional<UserEntity> user =
        repository.findByAppIdAndOrgIdAndEmail(
            userRequest.getAppId(), userRequest.getOrgId(), userRequest.getEmail());

    if (user.isPresent()) {
      UserResponse userResponse = new UserResponse(ErrorCode.EMAIL_EXISTS);
      logger.exit(String.format("error=%s", ErrorCode.EMAIL_EXISTS));
      return userResponse;
    }

    // save user account details
    UserEntity userEntity = UserMapper.fromUserRequest(userRequest);
    ObjectNode userInfo = getObjectNode();
    setPasswordAndPasswordHistoryFields(
        userRequest.getPassword(), userInfo, UserAccountStatus.PENDING_CONFIRMATION.getStatus());

    userEntity.setUserInfo(userInfo);
    userEntity = repository.saveAndFlush(userEntity);
    logger.exit(String.format("id=%s", userEntity.getId()));
    return UserMapper.toUserResponse(userEntity);
  }

  private void setPasswordAndPasswordHistoryFields(
      String password, JsonNode userInfoJsonNode, int accountStatus) {
    // encrypt the password using random salt
    String rawSalt = salt();
    String encrypted = encrypt(password, rawSalt);

    ObjectNode passwordNode = getObjectNode();
    passwordNode.put(HASH, hash(encrypted));
    passwordNode.put(SALT, rawSalt);

    UserAccountStatus userAccountStatus = UserAccountStatus.valueOf(accountStatus);
    switch (userAccountStatus) {
      case ACCOUNT_LOCKED:
        passwordNode.put(
            EXPIRE_TIMESTAMP,
            DateTimeUtils.getSystemDateTimestamp(0, 0, appConfig.getAccountLockPeriodInMinutes()));
        break;
      case PASSWORD_RESET:
        passwordNode.put(
            EXPIRE_TIMESTAMP,
            DateTimeUtils.getSystemDateTimestamp(0, appConfig.getResetPasswordExpiryInHours(), 0));
        break;
      default:
        passwordNode.put(
            EXPIRE_TIMESTAMP,
            DateTimeUtils.getSystemDateTimestamp(appConfig.getPasswordExpiryDays(), 0, 0));
    }

    ObjectNode userInfo = (ObjectNode) userInfoJsonNode;
    ArrayNode passwordHistory =
        userInfo.hasNonNull(PASSWORD_HISTORY)
            ? (ArrayNode) userInfo.get(PASSWORD_HISTORY)
            : createArrayNode();
    passwordHistory.add(passwordNode);

    // keep only 'X' previous passwords
    logger.trace(String.format("password history has %d elements", passwordHistory.size()));
    while (passwordHistory.size() > appConfig.getPasswordHistoryMaxSize()) {
      passwordHistory.remove(0);
    }

    userInfo.set(PASSWORD, passwordNode);
    userInfo.set(PASSWORD_HISTORY, passwordHistory);
  }

  @Override
  public ResetPasswordResponse resetPassword(
      ResetPasswordRequest resetPasswordRequest, AuditLogEventRequest auditRequest)
      throws JsonProcessingException {
    logger.entry("begin resetPassword()");
    Optional<UserEntity> entity =
        repository.findByAppIdAndOrgIdAndEmail(
            resetPasswordRequest.getAppId(),
            resetPasswordRequest.getOrgId(),
            resetPasswordRequest.getEmail());

    if (!entity.isPresent()) {
      logger.exit(String.format("reset password failed, error code=%s", ErrorCode.USER_NOT_FOUND));
      return new ResetPasswordResponse(ErrorCode.USER_NOT_FOUND);
    }

    String tempPassword = PasswordGenerator.generate(TEMP_PASSWORD_LENGTH);
    EmailResponse emailResponse = sendPasswordResetEmail(resetPasswordRequest, tempPassword);

    if (HttpStatus.ACCEPTED.value() == emailResponse.getHttpStatusCode()) {
      UserEntity userEntity = entity.get();
      ObjectNode userInfo = (ObjectNode) userEntity.getUserInfo();
      setPasswordAndPasswordHistoryFields(tempPassword, userInfo, userEntity.getStatus());
      userEntity.setUserInfo(userInfo);
      repository.saveAndFlush(userEntity);
      logger.exit(MessageCode.PASSWORD_RESET_SUCCESS);
      auditHelper.logEvent(PASSWORD_RESET_SUCCESS, auditRequest);
      return new ResetPasswordResponse(MessageCode.PASSWORD_RESET_SUCCESS);
    } else {
      auditHelper.logEvent(PASSWORD_RESET_FAILED, auditRequest);
    }
    logger.exit(
        String.format(
            "reset password failed, error code=%s", ErrorCode.EMAIL_SEND_FAILED_EXCEPTION));
    return new ResetPasswordResponse(ErrorCode.EMAIL_SEND_FAILED_EXCEPTION);
  }

  private EmailResponse sendPasswordResetEmail(
      ResetPasswordRequest resetPasswordRequest, String tempPassword) {
    Map<String, String> templateArgs = new HashMap<>();
    templateArgs.put("orgId", resetPasswordRequest.getOrgId());
    templateArgs.put("appId", resetPasswordRequest.getAppId());
    templateArgs.put("contactEmail", appConfig.getContactEmail());
    templateArgs.put("tempPassword", tempPassword);
    EmailRequest emailRequest =
        new EmailRequest(
            appConfig.getFromEmail(),
            new String[] {resetPasswordRequest.getEmail()},
            null,
            null,
            appConfig.getMailResetPasswordSubject(),
            appConfig.getMailResetPasswordBody(),
            templateArgs);
    return emailService.sendSimpleMail(emailRequest);
  }

  public ChangePasswordResponse changePassword(ChangePasswordRequest userRequest)
      throws JsonProcessingException {
    logger.entry("begin changePassword()");
    Optional<UserEntity> optionalEntity = repository.findByUserId(userRequest.getUserId());

    if (!optionalEntity.isPresent()) {
      logger.exit(ErrorCode.USER_NOT_FOUND);
      return new ChangePasswordResponse(ErrorCode.USER_NOT_FOUND);
    }

    UserEntity userEntity = optionalEntity.get();
    ObjectNode userInfo = (ObjectNode) userEntity.getUserInfo();
    ArrayNode passwordHistory =
        userInfo.hasNonNull(PASSWORD_HISTORY)
            ? (ArrayNode) userInfo.get(PASSWORD_HISTORY)
            : createArrayNode();
    JsonNode currentPwdNode = userInfo.get(PASSWORD);

    ErrorCode errorCode =
        validateChangePasswordRequest(userRequest, currentPwdNode, passwordHistory);
    if (errorCode != null) {
      logger.exit(String.format("change password failed with error code=%s", errorCode));
      return new ChangePasswordResponse(errorCode);
    }

    setPasswordAndPasswordHistoryFields(
        userRequest.getNewPassword(), userInfo, userEntity.getStatus());
    userEntity.setUserInfo(userInfo);
    repository.saveAndFlush(userEntity);
    logger.exit("Your password has been changed successfully!");
    return new ChangePasswordResponse(MessageCode.CHANGE_PASSWORD_SUCCESS);
  }

  private ErrorCode validateChangePasswordRequest(
      ChangePasswordRequest userRequest, JsonNode passwordNode, ArrayNode passwordHistory) {
    // determine whether the current password matches the password stored in database
    String hash = getTextValue(passwordNode, HASH);
    String rawSalt = getTextValue(passwordNode, SALT);
    String currentPasswordHash = hash(encrypt(userRequest.getCurrentPassword(), rawSalt));
    if (!StringUtils.equals(currentPasswordHash, hash)) {
      return ErrorCode.CURRENT_PASSWORD_INVALID;
    }

    // evaluate whether the new password matches any of the previous passwords
    String prevPasswordHash;
    String salt;
    for (JsonNode pwd : passwordHistory) {
      salt = getTextValue(pwd, SALT);
      prevPasswordHash = getTextValue(pwd, HASH);
      String newPasswordHash = hash(encrypt(userRequest.getNewPassword(), salt));
      if (StringUtils.equals(prevPasswordHash, newPasswordHash)) {
        return ErrorCode.ENFORCE_PASSWORD_HISTORY;
      }
    }
    return null;
  }

  @Override
  public Optional<UserEntity> findUserByTempRegId(String tempRegId) {
    logger.entry("begin findUserByTempRegId()");
    return repository.findByTempRegId(tempRegId);
  }

  public AuthenticationResponse authenticate(UserRequest user) throws JsonProcessingException {
    logger.entry("begin authenticate(user)");
    // check if the email present in the database
    Optional<UserEntity> optUserEntity =
        repository.findByAppIdAndOrgIdAndEmail(user.getAppId(), user.getOrgId(), user.getEmail());

    if (!optUserEntity.isPresent()) {
      return new AuthenticationResponse(ErrorCode.USER_NOT_FOUND);
    }

    UserEntity userEntity = optUserEntity.get();
    JsonNode userInfo = userEntity.getUserInfo();

    JsonNode passwordNode = userInfo.get(PASSWORD);
    String hash = getTextValue(passwordNode, HASH);
    String salt = getTextValue(passwordNode, SALT);

    // check the account status and password expiry condition
    ErrorCode errorCode = validatePasswordExpiryAndAccountStatus(userEntity, userInfo);
    if (errorCode != null) {
      return new AuthenticationResponse(errorCode);
    }

    String passwordHash = hash(encrypt(user.getPassword(), salt));
    if (StringUtils.equals(passwordHash, hash)) {
      // reset login attempts
      return updateLoginAttemptsAndAuthenticationTime(userEntity, userInfo);
    } else {
      // increment login attempts
      return updateInvalidLoginAttempts(userEntity, userInfo);
    }
  }

  private EmailResponse sendAccountLockedEmail(UserEntity user, String tempPassword) {
    logger.entry("sendAccountLockedEmail()");
    Map<String, String> templateArgs = new HashMap<>();
    templateArgs.put("appId", user.getAppId());
    templateArgs.put("contactEmail", appConfig.getContactEmail());
    templateArgs.put("tempPassword", tempPassword);
    EmailRequest emailRequest =
        new EmailRequest(
            appConfig.getFromEmail(),
            new String[] {user.getEmail()},
            null,
            null,
            appConfig.getMailAccountLockedSubject(),
            appConfig.getMailAccountLockedBody(),
            templateArgs);
    EmailResponse emailResponse = emailService.sendSimpleMail(emailRequest);
    logger.exit(
        String.format("send account locked email status=%d", emailResponse.getHttpStatusCode()));
    return emailResponse;
  }

  private AuthenticationResponse updateInvalidLoginAttempts(
      UserEntity userEntity, JsonNode userInfoJsonNode) {
    ObjectNode userInfo = (ObjectNode) userInfoJsonNode;
    int loginAttempts =
        userInfo.hasNonNull(LOGIN_ATTEMPTS) ? userInfo.get(LOGIN_ATTEMPTS).intValue() : 0;
    userInfo.put(LOGIN_ATTEMPTS, ++loginAttempts);

    if (userInfo.get(LOGIN_ATTEMPTS).intValue() >= appConfig.getMaxInvalidLoginAttempts()) {
      String tempPassword = PasswordGenerator.generate(12);
      setPasswordAndPasswordHistoryFields(
          tempPassword, userInfo, UserAccountStatus.ACCOUNT_LOCKED.getStatus());
      sendAccountLockedEmail(userEntity, tempPassword);
      userEntity.setStatus(UserAccountStatus.ACCOUNT_LOCKED.getStatus());
      userInfo.put(ACCOUNT_LOCK_EMAIL_TIMESTAMP, Instant.now().toEpochMilli());
    }

    userEntity.setUserInfo(userInfo);
    userEntity = repository.saveAndFlush(userEntity);

    ErrorCode ec =
        UserAccountStatus.ACCOUNT_LOCKED.equals(UserAccountStatus.valueOf(userEntity.getStatus()))
            ? ErrorCode.ACCOUNT_LOCKED
            : ErrorCode.INVALID_LOGIN_CREDENTIALS;

    return new AuthenticationResponse(ec);
  }

  private AuthenticationResponse updateLoginAttemptsAndAuthenticationTime(
      UserEntity userEntity, JsonNode userInfoJsonNode) {
    ObjectNode passwordNode = (ObjectNode) userInfoJsonNode.get(PASSWORD);
    UserAccountStatus status = UserAccountStatus.valueOf(userEntity.getStatus());
    passwordNode.remove(EXPIRE_TIMESTAMP);
    if (UserAccountStatus.PASSWORD_RESET.equals(status)
        || UserAccountStatus.ACCOUNT_LOCKED.equals(status)) {
      passwordNode.remove(EXPIRE_TIMESTAMP);
      passwordNode.put(OTP_USED, true);
    } else {
      passwordNode.remove(OTP_USED);
    }

    ObjectNode userInfo = (ObjectNode) userInfoJsonNode;
    userInfo.remove(ACCOUNT_LOCK_EMAIL_TIMESTAMP);
    userInfo.set(PASSWORD, passwordNode);
    userInfo.put(LOGIN_ATTEMPTS, 0);
    userInfo.put(LOGIN_TIMESTAMP, Instant.now().toEpochMilli());

    userEntity.setUserInfo(userInfo);
    userEntity = repository.saveAndFlush(userEntity);

    AuthenticationResponse authenticationResponse = new AuthenticationResponse();
    authenticationResponse.setUserId(userEntity.getUserId());
    authenticationResponse.setAccountStatus(userEntity.getStatus());
    authenticationResponse.setHttpStatusCode(HttpStatus.OK.value());
    return authenticationResponse;
  }

  private ErrorCode validatePasswordExpiryAndAccountStatus(
      UserEntity userEntity, JsonNode userInfo) {
    JsonNode passwordNode = userInfo.get(PASSWORD);
    UserAccountStatus accountStatus = UserAccountStatus.valueOf(userEntity.getStatus());
    switch (accountStatus) {
      case DEACTIVATED:
        return ErrorCode.ACCOUNT_DEACTIVATED;
      case PENDING_CONFIRMATION:
        return ErrorCode.PENDING_CONFIRMATION;
      case ACCOUNT_LOCKED:
        return isPasswordExpired(passwordNode) ? ErrorCode.TEMP_PASSWORD_EXPIRED : null;
      case PASSWORD_RESET:
        return isPasswordExpired(passwordNode) ? ErrorCode.TEMP_PASSWORD_EXPIRED : null;
      default:
        return isPasswordExpired(passwordNode) ? ErrorCode.PASSWORD_EXPIRED : null;
    }
  }

  private boolean isPasswordExpired(JsonNode passwordNode) {
    return (passwordNode.hasNonNull(EXPIRE_TIMESTAMP)
            && Instant.now().toEpochMilli() > passwordNode.get(EXPIRE_TIMESTAMP).longValue()
        || passwordNode.hasNonNull(OTP_USED) && passwordNode.get(OTP_USED).booleanValue());
  }

  @Override
  @Transactional
  public UpdateEmailStatusResponse updateEmailStatusAndTempRegId(
      UpdateEmailStatusRequest userRequest) throws JsonProcessingException {
    logger.entry("begin updateEmailStatusAndTempRegId()");
    Optional<UserEntity> optUser = repository.findByUserId(userRequest.getUserId());
    if (!optUser.isPresent()) {
      logger.exit(ErrorCode.USER_NOT_FOUND);
      return new UpdateEmailStatusResponse(ErrorCode.USER_NOT_FOUND);
    }

    UserEntity userEntity = optUser.get();
    Integer status =
        userRequest.getStatus() == null ? userEntity.getStatus() : userRequest.getStatus();
    String email = StringUtils.defaultIfEmpty(userRequest.getEmail(), userEntity.getEmail());

    String tempRegId = null;
    if (userRequest.getStatus() != null
        && UserAccountStatus.ACTIVE.getStatus() == userRequest.getStatus()) {
      tempRegId = IdGenerator.id();
    }
    repository.updateEmailStatusAndTempRegId(email, status, tempRegId, userEntity.getUserId());
    logger.exit(MessageCode.UPDATE_USER_DETAILS_SUCCESS);
    return new UpdateEmailStatusResponse(MessageCode.UPDATE_USER_DETAILS_SUCCESS, tempRegId);
  }
}
