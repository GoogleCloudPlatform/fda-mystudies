/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.AuthUserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.SetUpAccountRequest;
import com.google.cloud.healthcare.fdamystudies.beans.SetUpAccountResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UserResponse;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.common.UserAccountStatus;
import com.google.cloud.healthcare.fdamystudies.common.UserStatus;
import com.google.cloud.healthcare.fdamystudies.config.AppPropertyConfig;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.repository.UserRegAdminRepository;
import java.util.Optional;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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

  @Override
  @Transactional
  public SetUpAccountResponse saveUser(SetUpAccountRequest setUpAccountRequest) {
    logger.entry("saveUser");

    Optional<UserRegAdminEntity> optUsers =
        userRegAdminRepository.findByEmail(setUpAccountRequest.getEmail());
    if (!optUsers.isPresent()) {
      return new SetUpAccountResponse(ErrorCode.USER_NOT_INVITED);
    }

    // Bad request and errors handled in RestResponseErrorHandler class
    UserResponse authRegistrationResponse = registerUserInAuthServer(setUpAccountRequest);

    UserRegAdminEntity userRegAdminUser = optUsers.get();
    userRegAdminUser.setUrAdminAuthId(authRegistrationResponse.getUserId());
    userRegAdminUser.setFirstName(setUpAccountRequest.getFirstName());
    userRegAdminUser.setLastName(setUpAccountRequest.getLastName());
    userRegAdminUser.setStatus(UserStatus.ACTIVE.getValue());
    userRegAdminUser = userRegAdminRepository.saveAndFlush(userRegAdminUser);

    SetUpAccountResponse setUpAccountResponse =
        new SetUpAccountResponse(
            userRegAdminUser.getId(),
            authRegistrationResponse.getTempRegId(),
            authRegistrationResponse.getUserId(),
            MessageCode.SET_UP_ACCOUNT_SUCCESS);

    logger.exit(MessageCode.SET_UP_ACCOUNT_SUCCESS);
    return setUpAccountResponse;
  }

  private UserResponse registerUserInAuthServer(SetUpAccountRequest setUpAccountRequest) {
    logger.entry("registerUserInAuthServer()");

    AuthUserRequest userRequest =
        new AuthUserRequest(
            "PARTICIPANT MANAGER",
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
}
