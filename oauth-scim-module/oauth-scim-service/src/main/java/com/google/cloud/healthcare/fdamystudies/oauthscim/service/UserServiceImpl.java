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
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.toJsonNode;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.ACCOUNT_LOCK_EMAIL_TIMESTAMP;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.CHANGE_PASSWORD;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.EXPIRE_TIMESTAMP;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.HASH;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.LOGIN_ATTEMPTS;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.LOGIN_TIMESTAMP;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.OTP_USED;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.PASSWORD;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.PASSWORD_HISTORY;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.SALT;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.TEMP_PASSWORD_LENGTH;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.AuthenticationResponse;
import com.google.cloud.healthcare.fdamystudies.beans.EmailRequest;
import com.google.cloud.healthcare.fdamystudies.beans.EmailResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateUserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateUserResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserResponse;
import com.google.cloud.healthcare.fdamystudies.common.DateTimeUtils;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.PasswordGenerator;
import com.google.cloud.healthcare.fdamystudies.common.UserAccountStatus;
import com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimAuditLogHelper;
import com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent;
import com.google.cloud.healthcare.fdamystudies.oauthscim.config.AppPropertyConfig;
import com.google.cloud.healthcare.fdamystudies.oauthscim.mapper.UserMapper;
import com.google.cloud.healthcare.fdamystudies.oauthscim.model.UserEntity;
import com.google.cloud.healthcare.fdamystudies.oauthscim.repository.UserRepository;
import com.google.cloud.healthcare.fdamystudies.service.EmailService;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

  @Autowired private AuthScimAuditLogHelper aleHelper;

  @Override
  @Transactional
  public UserResponse createUser(UserRequest userRequest) {
    logger.entry("begin createUser()");

    // check if the email already been used
    Optional<UserEntity> user =
        repository.findByAppIdAndEmail(userRequest.getAppId(), userRequest.getEmail());

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

    userEntity.setUserInfo(userInfo.toString());
    userEntity = repository.saveAndFlush(userEntity);
    logger.exit(String.format("id=%s", userEntity.getId()));
    return UserMapper.toUserResponse(userEntity);
  }

  private void setPasswordAndPasswordHistoryFields(
      String password, ObjectNode userInfo, int accountStatus) {
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
  @Transactional
  public UpdateUserResponse updateUser(UpdateUserRequest userRequest)
      throws JsonProcessingException {
    logger.entry(String.format("begin updateUser() for %s action", userRequest.getAction()));
    if (CHANGE_PASSWORD.equalsIgnoreCase(userRequest.getAction())) {
      return changePassword(userRequest);
    }

    return new UpdateUserResponse(ErrorCode.APPLICATION_ERROR);
  }

  @Override
  @Transactional
  public UpdateUserResponse resetPassword(
      UpdateUserRequest userRequest, AuditLogEventRequest aleRequest)
      throws JsonProcessingException {
    logger.entry("begin resetPassword()");
    Optional<UserEntity> entity =
        repository.findByAppIdAndEmail(userRequest.getAppId(), userRequest.getEmail());
    if (entity.isPresent()) {
      UserEntity userEntity = entity.get();
      aleRequest.setUserId(userEntity.getUserId());
      String tempPassword = PasswordGenerator.generate(TEMP_PASSWORD_LENGTH);
      EmailResponse emailResponse = sendPasswordResetEmail(userRequest, tempPassword);

      if (HttpStatus.ACCEPTED.value() == emailResponse.getHttpStatusCode()) {

        ObjectNode userInfo = (ObjectNode) toJsonNode(userEntity.getUserInfo());
        setPasswordAndPasswordHistoryFields(
            tempPassword, userInfo, UserAccountStatus.PASSWORD_RESET.getStatus());
        userEntity.setUserInfo(userInfo.toString());
        repository.saveAndFlush(userEntity);

        aleHelper.logEvent(AuthScimEvent.PASSWORD_RESET_SUCCESS, aleRequest);

        logger.exit("Password reset successful.");
        return new UpdateUserResponse(HttpStatus.OK, "Password reset successful");
      } else {
        logger.exit(
            String.format(
                "Password reset failed, error code=%s", ErrorCode.EMAIL_SEND_FAILED_EXCEPTION));

        aleHelper.logEvent(AuthScimEvent.PASSWORD_RESET_FAILED, aleRequest);
        return new UpdateUserResponse(ErrorCode.EMAIL_SEND_FAILED_EXCEPTION);
      }
    }
    logger.exit(String.format("Password reset failed, error code=%s", ErrorCode.USER_NOT_FOUND));
    return new UpdateUserResponse(ErrorCode.USER_NOT_FOUND);
  }

  private EmailResponse sendPasswordResetEmail(UpdateUserRequest userRequest, String tempPassword) {
    Map<String, String> templateArgs = new HashMap<>();
    templateArgs.put("appId", userRequest.getAppId());
    templateArgs.put("contactEmail", appConfig.getContactEmail());
    templateArgs.put("tempPassword", tempPassword);
    EmailRequest emailRequest =
        new EmailRequest(
            appConfig.getFromEmail(),
            new String[] {userRequest.getEmail()},
            null,
            null,
            appConfig.getMailResetPasswordSubject(),
            appConfig.getMailResetPasswordBody(),
            templateArgs);
    return emailService.sendSimpleMail(emailRequest);
  }

  private UpdateUserResponse changePassword(UpdateUserRequest userRequest)
      throws JsonProcessingException {
    Optional<UserEntity> optionalEntity = repository.findByUserId(userRequest.getUserId());
    if (optionalEntity.isPresent()) {
      UserEntity userEntity = optionalEntity.get();
      ObjectNode userInfo = (ObjectNode) toJsonNode(userEntity.getUserInfo());
      ArrayNode passwordHistory =
          userInfo.hasNonNull(PASSWORD_HISTORY)
              ? (ArrayNode) userInfo.get(PASSWORD_HISTORY)
              : createArrayNode();
      JsonNode currentPwdNode = userInfo.get(PASSWORD);

      ErrorCode errorCode = validatePasswords(userRequest, currentPwdNode, passwordHistory);
      if (errorCode != null) {
        logger.exit(String.format("change password failed with error code=%s", errorCode));
        return new UpdateUserResponse(errorCode);
      }

      setPasswordAndPasswordHistoryFields(
          userRequest.getNewPassword(), userInfo, userEntity.getStatus());
      userEntity.setUserInfo(userInfo.toString());
      repository.saveAndFlush(userEntity);
      return new UpdateUserResponse(HttpStatus.OK, "Your password has been changed successfully!");
    } else {
      return new UpdateUserResponse(ErrorCode.USER_NOT_FOUND);
    }
  }

  private ErrorCode validatePasswords(
      UpdateUserRequest userRequest, JsonNode passwordNode, ArrayNode passwordHistory) {

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
    return repository.findByTempRegId(tempRegId);
  }

  @Override
  @Transactional
  public AuthenticationResponse authenticate(UserRequest user) throws JsonProcessingException {
    logger.entry("begin authenticate(user)");
    // check if the email present in the database
    Optional<UserEntity> optUserEntity =
        repository.findByAppIdAndEmail(user.getAppId(), user.getEmail());

    if (!optUserEntity.isPresent()) {
      return new AuthenticationResponse(ErrorCode.USER_NOT_FOUND);
    }

    UserEntity userEntity = optUserEntity.get();
    ObjectNode userInfo = (ObjectNode) toJsonNode(userEntity.getUserInfo());
    JsonNode passwordNode = userInfo.get(PASSWORD);
    String hash = getTextValue(passwordNode, HASH);
    String salt = getTextValue(passwordNode, SALT);

    // check the account status and password expiry condition
    ErrorCode errorCode = validatePasswordExpiryAndAccountStatus(userEntity, userInfo);
    if (errorCode != null) {
      return new AuthenticationResponse(errorCode, userEntity.getUserId(), userEntity.getStatus());
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
      UserEntity userEntity, ObjectNode userInfo) {

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

    userEntity.setUserInfo(userInfo.toString());
    userEntity = repository.saveAndFlush(userEntity);

    ErrorCode ec =
        UserAccountStatus.ACCOUNT_LOCKED.equals(UserAccountStatus.valueOf(userEntity.getStatus()))
            ? ErrorCode.ACCOUNT_LOCKED
            : ErrorCode.INVALID_LOGIN_CREDENTIALS;

    return new AuthenticationResponse(ec);
  }

  private AuthenticationResponse updateLoginAttemptsAndAuthenticationTime(
      UserEntity userEntity, ObjectNode userInfo) {
    ObjectNode passwordNode = (ObjectNode) userInfo.get(PASSWORD);
    UserAccountStatus status = UserAccountStatus.valueOf(userEntity.getStatus());
    passwordNode.remove(EXPIRE_TIMESTAMP);
    if (UserAccountStatus.PASSWORD_RESET.equals(status)
        || UserAccountStatus.ACCOUNT_LOCKED.equals(status)) {
      passwordNode.remove(EXPIRE_TIMESTAMP);
      passwordNode.put(OTP_USED, true);
    } else {
      passwordNode.remove(OTP_USED);
    }

    userInfo.remove(ACCOUNT_LOCK_EMAIL_TIMESTAMP);
    userInfo.set(PASSWORD, passwordNode);
    userInfo.put(LOGIN_ATTEMPTS, 0);
    userInfo.put(LOGIN_TIMESTAMP, Instant.now().toEpochMilli());

    userEntity.setUserInfo(userInfo.toString());
    userEntity = repository.saveAndFlush(userEntity);

    AuthenticationResponse authenticationResponse = new AuthenticationResponse();
    authenticationResponse.setUserId(userEntity.getUserId());
    authenticationResponse.setAccountStatus(userEntity.getStatus());
    authenticationResponse.setHttpStatusCode(HttpStatus.OK.value());
    return authenticationResponse;
  }

  private ErrorCode validatePasswordExpiryAndAccountStatus(
      UserEntity userEntity, ObjectNode userInfo) {
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
  public void resetTempRegId(String userId) {
    repository.resetTempRegId(userId);
  }

  @Override
  @Transactional
  public void removeExpiredTempRegIds() {
    long timeInMillis =
        Instant.now()
            .minus(appConfig.getTempRegIdExpiryMinutes(), ChronoUnit.MINUTES)
            .toEpochMilli();
    repository.updateTempRegId(new Timestamp(timeInMillis));
  }
}
