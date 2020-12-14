/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.service;

import static com.google.cloud.healthcare.fdamystudies.common.HashUtils.hash;
import static com.google.cloud.healthcare.fdamystudies.common.HashUtils.salt;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.createArrayNode;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.getObjectNode;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.getTextValue;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.ACCOUNT_LOCKED_PASSWORD;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.ACCOUNT_LOCK_EMAIL_TIMESTAMP;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.EXPIRE_TIMESTAMP;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.HASH;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.LOGIN_ATTEMPTS;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.LOGIN_TIMESTAMP;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.OTP_USED;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.PASSWORD;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.PASSWORD_HISTORY;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.REFRESH_TOKEN;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.SALT;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.TEMP_PASSWORD_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.TOKEN;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.ACCOUNT_LOCKED;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.PASSWORD_CHANGE_FAILED;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.PASSWORD_CHANGE_SUCCEEDED;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.PASSWORD_HELP_EMAIL_FAILED;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.PASSWORD_HELP_EMAIL_SENT;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.PASSWORD_HELP_REQUESTED_FOR_UNREGISTERED_USERNAME;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.PASSWORD_RESET_EMAIL_FAILED_FOR_LOCKED_ACCOUNT;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.PASSWORD_RESET_EMAIL_SENT_FOR_LOCKED_ACCOUNT;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.PASSWORD_RESET_FAILED;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.SIGNIN_FAILED_EXPIRED_PASSWORD;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.SIGNIN_FAILED_EXPIRED_TEMPORARY_PASSWORD;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.SIGNIN_FAILED_INVALID_PASSWORD;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.SIGNIN_FAILED_UNREGISTERED_USER;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.SIGNIN_WITH_TEMPORARY_PASSWORD_FAILED;

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
import com.google.cloud.healthcare.fdamystudies.common.TextEncryptor;
import com.google.cloud.healthcare.fdamystudies.common.UserAccountStatus;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimAuditHelper;
import com.google.cloud.healthcare.fdamystudies.oauthscim.config.AppPropertyConfig;
import com.google.cloud.healthcare.fdamystudies.oauthscim.mapper.UserMapper;
import com.google.cloud.healthcare.fdamystudies.oauthscim.model.UserEntity;
import com.google.cloud.healthcare.fdamystudies.oauthscim.repository.UserRepository;
import com.google.cloud.healthcare.fdamystudies.service.EmailService;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class UserServiceImpl implements UserService {

  private XLogger logger = XLoggerFactory.getXLogger(UserServiceImpl.class.getName());

  @Autowired private UserRepository repository;

  @Autowired private AppPropertyConfig appConfig;

  @Autowired private EmailService emailService;

  @Autowired private AuthScimAuditHelper auditHelper;

  @Autowired private OAuthService oauthService;

  @Autowired private TextEncryptor encryptor;

  @Override
  @Transactional
  public UserResponse createUser(UserRequest userRequest) {
    logger.entry("begin createUser()");

    // check if the email already been used
    Optional<UserEntity> user =
        repository.findByAppIdAndEmail(userRequest.getAppId(), userRequest.getEmail());

    if (user.isPresent()) {
      throw new ErrorCodeException(ErrorCode.EMAIL_EXISTS);
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
    String hashValue = hash(password, rawSalt);

    ObjectNode passwordNode = getObjectNode();
    passwordNode.put(HASH, hashValue);
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

    if (userAccountStatus == UserAccountStatus.ACCOUNT_LOCKED) {
      userInfo.set(ACCOUNT_LOCKED_PASSWORD, passwordNode);
    } else {
      userInfo.set(PASSWORD, passwordNode);
      userInfo.set(PASSWORD_HISTORY, passwordHistory);
    }
  }

  @Override
  @Transactional
  public ResetPasswordResponse resetPassword(
      ResetPasswordRequest resetPasswordRequest, AuditLogEventRequest auditRequest)
      throws JsonProcessingException {
    logger.entry("begin resetPassword()");

    Optional<UserEntity> optUserEntity =
        repository.findByAppIdAndEmail(
            resetPasswordRequest.getAppId(), resetPasswordRequest.getEmail());

    if (!optUserEntity.isPresent()) {
      auditHelper.logEvent(PASSWORD_HELP_REQUESTED_FOR_UNREGISTERED_USERNAME, auditRequest);
      logger.exit(String.format("reset password failed, error code=%s", ErrorCode.USER_NOT_FOUND));
      throw new ErrorCodeException(ErrorCode.USER_NOT_FOUND);
    }

    UserEntity userEntity = optUserEntity.get();
    ObjectNode userInfo = (ObjectNode) userEntity.getUserInfo();
    if (userEntity.getStatus() == UserAccountStatus.PENDING_CONFIRMATION.getStatus()) {
      throw new ErrorCodeException(ErrorCode.ACCOUNT_NOT_VERIFIED);
    }

    if (userEntity.getStatus() == UserAccountStatus.DEACTIVATED.getStatus()) {
      throw new ErrorCodeException(ErrorCode.ACCOUNT_DEACTIVATED);
    }

    if (userEntity.getStatus() == UserAccountStatus.ACCOUNT_LOCKED.getStatus()) {
      JsonNode accountLockedPwdNode = userInfo.get(ACCOUNT_LOCKED_PASSWORD);
      if (null != accountLockedPwdNode
          && Instant.now().toEpochMilli()
              < accountLockedPwdNode.get(EXPIRE_TIMESTAMP).longValue()) {
        throw new ErrorCodeException(ErrorCode.ACCOUNT_LOCKED);
      }
    }

    Integer accountStatusBeforePasswordReset = userEntity.getStatus();
    String tempPassword = PasswordGenerator.generate(TEMP_PASSWORD_LENGTH);
    EmailResponse emailResponse = sendPasswordResetEmail(resetPasswordRequest, tempPassword);

    if (HttpStatus.ACCEPTED.value() == emailResponse.getHttpStatusCode()) {
      setPasswordAndPasswordHistoryFields(
          tempPassword, userInfo, UserAccountStatus.PASSWORD_RESET.getStatus());
      userEntity.setStatus(UserAccountStatus.PASSWORD_RESET.getStatus());
      userInfo.remove(ACCOUNT_LOCK_EMAIL_TIMESTAMP);
      userInfo.remove(ACCOUNT_LOCKED_PASSWORD);
      userInfo.put(LOGIN_ATTEMPTS, 0);
      userEntity.setUserInfo(userInfo);
      repository.saveAndFlush(userEntity);
      if (accountStatusBeforePasswordReset == UserAccountStatus.ACCOUNT_LOCKED.getStatus()) {
        auditHelper.logEvent(PASSWORD_RESET_EMAIL_SENT_FOR_LOCKED_ACCOUNT, auditRequest);
      } else {
        auditHelper.logEvent(PASSWORD_HELP_EMAIL_SENT, auditRequest);
      }

      logger.exit(MessageCode.FORGOT_PASSWORD);
      return new ResetPasswordResponse(MessageCode.FORGOT_PASSWORD);
    }

    auditHelper.logEvent(PASSWORD_RESET_FAILED, auditRequest);
    if (accountStatusBeforePasswordReset == UserAccountStatus.ACCOUNT_LOCKED.getStatus()) {
      auditHelper.logEvent(PASSWORD_RESET_EMAIL_FAILED_FOR_LOCKED_ACCOUNT, auditRequest);
    } else {
      auditHelper.logEvent(PASSWORD_HELP_EMAIL_FAILED, auditRequest);
    }

    logger.exit(
        String.format(
            "reset password failed, error code=%s", ErrorCode.EMAIL_SEND_FAILED_EXCEPTION));
    throw new ErrorCodeException(ErrorCode.EMAIL_SEND_FAILED_EXCEPTION);
  }

  private EmailResponse sendPasswordResetEmail(
      ResetPasswordRequest resetPasswordRequest, String tempPassword) {
    Map<String, String> templateArgs = new HashMap<>();
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
    return emailService.sendMimeMail(emailRequest);
  }

  @Transactional
  public ChangePasswordResponse changePassword(
      ChangePasswordRequest userRequest, AuditLogEventRequest auditRequest)
      throws JsonProcessingException {
    logger.entry("begin changePassword()");
    Optional<UserEntity> optionalEntity = repository.findByUserId(userRequest.getUserId());

    if (!optionalEntity.isPresent()) {
      throw new ErrorCodeException(ErrorCode.USER_NOT_FOUND);
    }

    UserEntity userEntity = optionalEntity.get();
    ObjectNode userInfo = (ObjectNode) userEntity.getUserInfo();
    ArrayNode passwordHistory =
        userInfo.hasNonNull(PASSWORD_HISTORY)
            ? (ArrayNode) userInfo.get(PASSWORD_HISTORY)
            : createArrayNode();

    JsonNode currentPwdNode = userInfo.get(PASSWORD);

    if (userEntity.getStatus() == UserAccountStatus.ACCOUNT_LOCKED.getStatus()) {
      currentPwdNode = userInfo.get(ACCOUNT_LOCKED_PASSWORD);
    }

    ErrorCode errorCode =
        validateChangePasswordRequest(userRequest, currentPwdNode, passwordHistory, userEntity);
    if (errorCode != null) {
      auditHelper.logEvent(PASSWORD_CHANGE_FAILED, auditRequest);
      throw new ErrorCodeException(errorCode);
    }

    setPasswordAndPasswordHistoryFields(
        userRequest.getNewPassword(), userInfo, UserAccountStatus.ACTIVE.getStatus());
    userInfo.remove(ACCOUNT_LOCK_EMAIL_TIMESTAMP);
    userInfo.remove(ACCOUNT_LOCKED_PASSWORD);
    userInfo.put(LOGIN_ATTEMPTS, 0);
    userEntity.setStatus(UserAccountStatus.ACTIVE.getStatus());
    userEntity.setUserInfo(userInfo);
    repository.saveAndFlush(userEntity);
    auditHelper.logEvent(PASSWORD_CHANGE_SUCCEEDED, auditRequest);
    logger.exit("Your password has been changed successfully!");
    return new ChangePasswordResponse(MessageCode.PASSWORD_RESET_SUCCESS);
  }

  private ErrorCode validateChangePasswordRequest(
      ChangePasswordRequest userRequest,
      JsonNode passwordNode,
      ArrayNode passwordHistory,
      UserEntity userEntity) {
    // determine whether the current password matches the password stored in database
    String hash = getTextValue(passwordNode, HASH);
    String rawSalt = getTextValue(passwordNode, SALT);
    String currentPasswordHash = hash(userRequest.getCurrentPassword(), rawSalt);

    if (!StringUtils.equals(currentPasswordHash, hash)) {
      return userEntity.getStatus() == UserAccountStatus.ACCOUNT_LOCKED.getStatus()
              || userEntity.getStatus() == UserAccountStatus.PASSWORD_RESET.getStatus()
          ? ErrorCode.TEMP_PASSWORD_INCORRECT
          : ErrorCode.CURRENT_PASSWORD_INVALID;
    }

    // evaluate whether the new password matches any of the previous passwords
    String prevPasswordHash;
    String salt;
    for (JsonNode pwd : passwordHistory) {
      salt = getTextValue(pwd, SALT);
      prevPasswordHash = getTextValue(pwd, HASH);
      String newPasswordHash = hash(userRequest.getNewPassword(), salt);
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
  @Transactional(noRollbackFor = ErrorCodeException.class)
  public AuthenticationResponse authenticate(UserRequest user, AuditLogEventRequest auditRequest)
      throws JsonProcessingException {
    logger.entry("begin authenticate(user)");
    Optional<UserEntity> optUserEntity =
        repository.findByAppIdAndEmail(user.getAppId(), user.getEmail());

    if (!optUserEntity.isPresent()) {
      auditHelper.logEvent(SIGNIN_FAILED_UNREGISTERED_USER, auditRequest);
      throw new ErrorCodeException(ErrorCode.USER_NOT_FOUND);
    }

    UserEntity userEntity = optUserEntity.get();
    JsonNode userInfo = userEntity.getUserInfo();

    JsonNode passwordNode = userInfo.get(PASSWORD);
    if (userEntity.getStatus() == UserAccountStatus.ACCOUNT_LOCKED.getStatus()) {
      JsonNode accountLockedPasswordNode = userInfo.get(ACCOUNT_LOCKED_PASSWORD);
      if (Instant.now().toEpochMilli()
          < accountLockedPasswordNode.get(EXPIRE_TIMESTAMP).longValue()) {
        passwordNode = userInfo.get(ACCOUNT_LOCKED_PASSWORD);
      } else {
        // unlock the user account
        userEntity.setStatus(UserAccountStatus.ACTIVE.getStatus());
      }
    }
    String hash = getTextValue(passwordNode, HASH);
    String salt = getTextValue(passwordNode, SALT);

    // check the account status and password expiry condition
    validatePasswordExpiryAndAccountStatus(userEntity, userInfo, auditRequest);

    // compare passwords
    String passwordHash = hash(user.getPassword(), salt);
    if (StringUtils.equals(passwordHash, hash)) {
      // reset login attempts
      return updateLoginAttemptsAndAuthenticationTime(userEntity, userInfo, auditRequest);
    }

    // authentication unsuccessful
    if (UserAccountStatus.ACTIVE.getStatus() == userEntity.getStatus()) {
      auditHelper.logEvent(SIGNIN_FAILED_INVALID_PASSWORD, auditRequest);
    } else {
      auditHelper.logEvent(SIGNIN_WITH_TEMPORARY_PASSWORD_FAILED, auditRequest);
    }

    // increment login attempts
    return updateInvalidLoginAttempts(userEntity, userInfo, auditRequest);
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
    EmailResponse emailResponse = emailService.sendMimeMail(emailRequest);
    logger.exit(
        String.format("send account locked email status=%d", emailResponse.getHttpStatusCode()));
    return emailResponse;
  }

  private AuthenticationResponse updateInvalidLoginAttempts(
      UserEntity userEntity, JsonNode userInfoJsonNode, AuditLogEventRequest auditRequest) {
    if (userEntity.getStatus() == UserAccountStatus.ACCOUNT_LOCKED.getStatus()) {
      throw new ErrorCodeException(ErrorCode.ACCOUNT_LOCKED);
    }

    ObjectNode userInfo = (ObjectNode) userInfoJsonNode;
    int loginAttempts =
        userInfo.hasNonNull(LOGIN_ATTEMPTS) ? userInfo.get(LOGIN_ATTEMPTS).intValue() : 0;
    loginAttempts += 1;
    userInfo.put(LOGIN_ATTEMPTS, loginAttempts);

    long systemTime = Instant.now().toEpochMilli();
    if (userInfo.get(LOGIN_ATTEMPTS).intValue() >= appConfig.getMaxInvalidLoginAttempts()) {
      String tempPassword = PasswordGenerator.generate(12);
      setPasswordAndPasswordHistoryFields(
          tempPassword, userInfo, UserAccountStatus.ACCOUNT_LOCKED.getStatus());
      sendAccountLockedEmail(userEntity, tempPassword);
      userEntity.setStatus(UserAccountStatus.ACCOUNT_LOCKED.getStatus());
      userInfo.put(ACCOUNT_LOCK_EMAIL_TIMESTAMP, systemTime);

      Map<String, String> placeHolders = new HashMap<>();
      placeHolders.put("lock_time", String.valueOf(systemTime));
      placeHolders.put("failed_attempts", String.valueOf(loginAttempts));
      auditHelper.logEvent(ACCOUNT_LOCKED, auditRequest, placeHolders);
    }

    userEntity.setUserInfo(userInfo);
    userEntity = repository.saveAndFlush(userEntity);

    ErrorCode errorCode =
        userEntity.getStatus() == UserAccountStatus.ACCOUNT_LOCKED.getStatus()
            ? ErrorCode.ACCOUNT_LOCKED
            : ErrorCode.INVALID_LOGIN_CREDENTIALS;

    throw new ErrorCodeException(errorCode);
  }

  private AuthenticationResponse updateLoginAttemptsAndAuthenticationTime(
      UserEntity userEntity, JsonNode userInfoJsonNode, AuditLogEventRequest auditRequest) {
    ObjectNode userInfo = (ObjectNode) userInfoJsonNode;
    userInfo.put(LOGIN_TIMESTAMP, Instant.now().toEpochMilli());

    UserAccountStatus status = UserAccountStatus.valueOf(userEntity.getStatus());

    if (UserAccountStatus.ACCOUNT_LOCKED.equals(status)) {
      ObjectNode passwordNode = (ObjectNode) userInfo.get(ACCOUNT_LOCKED_PASSWORD);
      passwordNode.put(OTP_USED, true);
      userInfo.set(ACCOUNT_LOCKED_PASSWORD, passwordNode);
    } else if (UserAccountStatus.PASSWORD_RESET.equals(status)) {
      ObjectNode passwordNode = (ObjectNode) userInfo.get(PASSWORD);
      passwordNode.put(OTP_USED, true);
      userInfo.set(PASSWORD, passwordNode);
    } else {
      ObjectNode passwordNode = (ObjectNode) userInfo.get(PASSWORD);
      passwordNode.remove(OTP_USED);
      userInfo.remove(ACCOUNT_LOCK_EMAIL_TIMESTAMP);
      userInfo.remove(ACCOUNT_LOCKED_PASSWORD);
      userInfo.put(LOGIN_ATTEMPTS, 0);
      userInfo.set(PASSWORD, passwordNode);
    }

    userEntity.setUserInfo(userInfo);
    userEntity = repository.saveAndFlush(userEntity);

    AuthenticationResponse authenticationResponse = new AuthenticationResponse();
    authenticationResponse.setUserId(userEntity.getUserId());
    authenticationResponse.setAccountStatus(userEntity.getStatus());
    authenticationResponse.setHttpStatusCode(HttpStatus.OK.value());
    return authenticationResponse;
  }

  private void validatePasswordExpiryAndAccountStatus(
      UserEntity userEntity, JsonNode userInfo, AuditLogEventRequest auditRequest) {
    JsonNode passwordNode =
        userEntity.getStatus() == UserAccountStatus.ACCOUNT_LOCKED.getStatus()
            ? userInfo.get(ACCOUNT_LOCKED_PASSWORD)
            : userInfo.get(PASSWORD);
    boolean passwordExpired = isPasswordExpired(passwordNode);
    UserAccountStatus accountStatus = UserAccountStatus.valueOf(userEntity.getStatus());
    switch (accountStatus) {
      case DEACTIVATED:
        throw new ErrorCodeException(ErrorCode.ACCOUNT_DEACTIVATED);
      case ACCOUNT_LOCKED:
        if (passwordExpired) {
          auditHelper.logEvent(SIGNIN_FAILED_EXPIRED_TEMPORARY_PASSWORD, auditRequest);
          throw new ErrorCodeException(ErrorCode.TEMP_PASSWORD_EXPIRED);
        }
      case PASSWORD_RESET:
        if (passwordExpired) {
          auditHelper.logEvent(SIGNIN_FAILED_EXPIRED_TEMPORARY_PASSWORD, auditRequest);
          throw new ErrorCodeException(ErrorCode.TEMP_PASSWORD_EXPIRED);
        }
      default:
        if (passwordExpired) {
          auditHelper.logEvent(SIGNIN_FAILED_EXPIRED_PASSWORD, auditRequest);
          throw new ErrorCodeException(ErrorCode.PASSWORD_EXPIRED);
        }
    }
  }

  private boolean isPasswordExpired(JsonNode passwordNode) {
    return (passwordNode.hasNonNull(EXPIRE_TIMESTAMP)
            && Instant.now().toEpochMilli() > passwordNode.get(EXPIRE_TIMESTAMP).longValue()
        || passwordNode.hasNonNull(OTP_USED) && passwordNode.get(OTP_USED).booleanValue());
  }

  @Override
  public void resetTempRegId(String userId) {
    repository.removeTempRegIDForUser(userId);
  }

  @Override
  @Transactional
  public void removeExpiredTempRegIds() {
    long timeInMillis =
        Instant.now()
            .minus(appConfig.getTempRegIdExpiryMinutes(), ChronoUnit.MINUTES)
            .toEpochMilli();
    repository.removeTempRegIdBeforeTime(new Timestamp(timeInMillis));
  }

  @Transactional
  public UpdateEmailStatusResponse updateEmailStatusAndTempRegId(
      UpdateEmailStatusRequest userRequest) throws JsonProcessingException {
    logger.entry("begin updateEmailStatusAndTempRegId()");
    Optional<UserEntity> optUser = repository.findByUserId(userRequest.getUserId());
    if (!optUser.isPresent()) {
      throw new ErrorCodeException(ErrorCode.USER_NOT_FOUND);
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

  @Override
  @Transactional
  public UserResponse logout(String userId, AuditLogEventRequest auditRequest)
      throws JsonProcessingException {
    Optional<UserEntity> optUserEntity = repository.findByUserId(userId);

    if (!optUserEntity.isPresent()) {
      throw new ErrorCodeException(ErrorCode.USER_NOT_FOUND);
    }

    return revokeAndReplaceRefreshToken(userId, null, auditRequest);
  }

  @Override
  @Transactional
  public UserResponse revokeAndReplaceRefreshToken(
      String userId, String refreshToken, AuditLogEventRequest auditRequest)
      throws JsonProcessingException {
    logger.entry("revokeAndReplaceRefreshToken(userId, refreshToken)");
    Optional<UserEntity> optUserEntity = repository.findByUserId(userId);
    if (!optUserEntity.isPresent()) {
      throw new ErrorCodeException(ErrorCode.USER_NOT_FOUND);
    }

    UserEntity userEntity = optUserEntity.get();
    ObjectNode userInfo = (ObjectNode) userEntity.getUserInfo();

    if (userInfo.hasNonNull(REFRESH_TOKEN)) {
      String prevRefreshToken = getTextValue(userInfo, REFRESH_TOKEN);
      prevRefreshToken = encryptor.decrypt(prevRefreshToken);
      HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
      MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
      requestParams.add(TOKEN, prevRefreshToken);
      ResponseEntity<JsonNode> response = oauthService.revokeToken(requestParams, headers);
      if (!response.getStatusCode().is2xxSuccessful()) {
        throw new ErrorCodeException(ErrorCode.APPLICATION_ERROR);
      }
    }

    if (StringUtils.isEmpty(refreshToken)) {
      userInfo.remove(REFRESH_TOKEN);
    } else {
      userInfo.put(REFRESH_TOKEN, encryptor.encrypt(refreshToken));
    }

    userEntity.setUserInfo(userInfo);
    repository.saveAndFlush(userEntity);

    UserResponse userResponse = new UserResponse();
    userResponse.setHttpStatusCode(HttpStatus.OK.value());
    logger.exit(
        "previous refresh token revoked and replaced with new refresh token for the given user");
    return userResponse;
  }

  @Override
  @Transactional
  public void deleteUserAccount(String userId) {
    logger.entry("deleteUserAccount");
    Optional<UserEntity> optUserEntity = repository.findByUserId(userId);

    if (!optUserEntity.isPresent()) {
      throw new ErrorCodeException(ErrorCode.USER_NOT_FOUND);
    }

    repository.delete(optUserEntity.get());

    logger.exit("user account deleted");
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<UserEntity> findByUserId(String userId) {
    return repository.findByUserId(userId);
  }
}
