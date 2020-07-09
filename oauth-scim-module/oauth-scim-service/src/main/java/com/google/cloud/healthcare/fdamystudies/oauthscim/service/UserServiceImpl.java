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
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.EXPIRES_AT;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.HASH;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.PASSWORD;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.PASSWORD_HISTORY;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.SALT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.healthcare.fdamystudies.beans.ChangePasswordRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ChangePasswordResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserResponse;
import com.google.cloud.healthcare.fdamystudies.common.DateTimeUtils;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.oauthscim.config.AppPropertyConfig;
import com.google.cloud.healthcare.fdamystudies.oauthscim.mapper.UserMapper;
import com.google.cloud.healthcare.fdamystudies.oauthscim.model.UserEntity;
import com.google.cloud.healthcare.fdamystudies.oauthscim.repository.UserRepository;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private XLogger logger = XLoggerFactory.getXLogger(UserServiceImpl.class.getName());

  @Autowired private UserRepository repository;

  @Autowired private AppPropertyConfig appConfig;

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
    setPasswordAndPasswordHistoryFields(userRequest.getPassword(), userInfo);

    userEntity.setUserInfo(userInfo.toString());
    userEntity = repository.saveAndFlush(userEntity);
    logger.exit(String.format("id=%s", userEntity.getId()));
    return UserMapper.toUserResponse(userEntity);
  }

  private void setPasswordAndPasswordHistoryFields(String password, ObjectNode userInfo) {
    // encrypt the password using random salt
    String rawSalt = salt();
    String encrypted = encrypt(password, rawSalt);

    ObjectNode passwordNode = getObjectNode();
    passwordNode.put(HASH, hash(encrypted));
    passwordNode.put(SALT, rawSalt);
    passwordNode.put(
        EXPIRES_AT, DateTimeUtils.getSystemDateTimestamp(appConfig.getPasswordExpiryDays(), 0, 0));

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
  public ChangePasswordResponse changePassword(ChangePasswordRequest userRequest)
      throws JsonProcessingException {
    logger.entry("begin changePassword()");
    Optional<UserEntity> optionalEntity = repository.findByUserId(userRequest.getUserId());

    if (!optionalEntity.isPresent()) {
      logger.exit(ErrorCode.USER_NOT_FOUND);
      return new ChangePasswordResponse(ErrorCode.USER_NOT_FOUND);
    }

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
      return new ChangePasswordResponse(errorCode);
    }

    setPasswordAndPasswordHistoryFields(userRequest.getNewPassword(), userInfo);
    userEntity.setUserInfo(userInfo.toString());
    repository.saveAndFlush(userEntity);
    logger.exit("Your password has been changed successfully!");
    return new ChangePasswordResponse(
        HttpStatus.OK, "Your password has been changed successfully!");
  }

  private ErrorCode validatePasswords(
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
}
