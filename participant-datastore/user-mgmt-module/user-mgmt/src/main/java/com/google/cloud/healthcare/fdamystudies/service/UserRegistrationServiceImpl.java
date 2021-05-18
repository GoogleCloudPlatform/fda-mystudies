/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import static com.google.cloud.healthcare.fdamystudies.common.ErrorCode.USER_ALREADY_EXISTS;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.ACCOUNT_REGISTRATION_REQUEST_RECEIVED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.USER_REGISTRATION_ATTEMPT_FAILED_EXISTING_USERNAME;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.VERIFICATION_EMAIL_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.VERIFICATION_EMAIL_SENT;

import com.google.cloud.healthcare.fdamystudies.beans.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.EmailRequest;
import com.google.cloud.healthcare.fdamystudies.beans.EmailResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UserRegistrationForm;
import com.google.cloud.healthcare.fdamystudies.beans.UserRegistrationResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserResponse;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.common.UserAccountStatus;
import com.google.cloud.healthcare.fdamystudies.common.UserMgmntAuditHelper;
import com.google.cloud.healthcare.fdamystudies.common.UserStatus;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserAppDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.repository.AppRepository;
import com.google.cloud.healthcare.fdamystudies.repository.AuthInfoRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserAppDetailsRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserDetailsRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {

  private static final int VERIFICATION_CODE_LENGTH = 6;

  private XLogger logger = XLoggerFactory.getXLogger(UserRegistrationServiceImpl.class.getName());

  @Autowired private UserDetailsRepository userDetailsRepository;

  @Autowired private CommonDao commonDao;

  @Autowired private OAuthService oauthService;

  @Autowired private RestTemplate restTemplate;

  @Autowired private ApplicationPropertyConfiguration appConfig;

  @Autowired private EmailService emailService;

  @Autowired private UserMgmntAuditHelper userMgmntAuditHelper;

  @Autowired private AppRepository appRepository;

  @Autowired private UserAppDetailsRepository userAppDetailsRepository;

  @Autowired private AuthInfoRepository authInfoRepository;

  @Value("${register.url}")
  private String authRegisterUrl;

  @Value("${email.code.expire_time}")
  private long expireTime;

  @Override
  @Transactional()
  public UserRegistrationResponse register(
      UserRegistrationForm user, AuditLogEventRequest auditRequest) {
    logger.entry("begin register()");
    auditRequest.setAppId(user.getAppId());
    userMgmntAuditHelper.logEvent(ACCOUNT_REGISTRATION_REQUEST_RECEIVED, auditRequest);

    // find appInfoId using appId
    AppOrgInfoBean appOrgInfoBean =
        commonDao.getUserAppDetailsByAllApi(user.getUserId(), user.getAppId());

    // find user by email and appId

    Optional<UserDetailsEntity> optUserDetails =
        userDetailsRepository.findByEmailAndAppId(user.getEmailId(), appOrgInfoBean.getAppInfoId());

    // Return USER_ALREADY_EXISTS error code if user account already exists for the given email
    if (optUserDetails.isPresent()) {
      UserDetailsEntity existingUserDetails = optUserDetails.get();
      userMgmntAuditHelper.logEvent(
          USER_REGISTRATION_ATTEMPT_FAILED_EXISTING_USERNAME, auditRequest);
      if (UserStatus.ACTIVE.getValue().equals(existingUserDetails.getStatus())) {
        throw new ErrorCodeException(USER_ALREADY_EXISTS);
      } else if (!generateVerificationCode(existingUserDetails)) {
        throw new ErrorCodeException(ErrorCode.PENDING_CONFIRMATION);
      }

      EmailResponse emailResponse =
          generateAndSaveVerificationCode(existingUserDetails, user.getAppName());

      if (MessageCode.EMAIL_ACCEPTED_BY_MAIL_SERVER
          .getMessage()
          .equals(emailResponse.getMessage())) {
        userMgmntAuditHelper.logEvent(VERIFICATION_EMAIL_SENT, auditRequest);
        throw new ErrorCodeException(ErrorCode.PENDING_CONFIRMATION);
      } else {
        userMgmntAuditHelper.logEvent(VERIFICATION_EMAIL_FAILED, auditRequest);
        throw new ErrorCodeException(ErrorCode.REGISTRATION_EMAIL_SEND_FAILED);
      }
    }

    UserDetailsEntity userDetails = fromUserRegistrationForm(user);
    Optional<AppEntity> app = appRepository.findByAppId(appOrgInfoBean.getAppInfoId());
    if (app.isPresent()) {
      userDetails.setApp(app.get());
    }

    // Call POST /users API to create a user account in oauth-scim-server
    UserResponse authUserResponse = registerUserInAuthServer(user, auditRequest);

    // save authUserId
    userDetails.setUserId(authUserResponse.getUserId());
    userDetails = userDetailsRepository.saveAndFlush(userDetails);

    // save to UserAppDetailsEntity and AuthInfoEntity for Push notification
    saveAuthInfoAndUserAppDetails(userDetails);

    auditRequest.setUserId(userDetails.getUserId());

    // generate save and email the verification code
    EmailResponse emailResponse = generateAndSaveVerificationCode(userDetails, user.getAppName());

    // verification code is empty if send email is failed
    if (MessageCode.EMAIL_ACCEPTED_BY_MAIL_SERVER.getMessage().equals(emailResponse.getMessage())) {
      userMgmntAuditHelper.logEvent(VERIFICATION_EMAIL_SENT, auditRequest);
    } else {
      userMgmntAuditHelper.logEvent(VERIFICATION_EMAIL_FAILED, auditRequest);
      // throwing ErrorCodeException will result in Transaction roll back so this is handled in
      // UserRegistrationController
      return new UserRegistrationResponse(
          authUserResponse.getUserId(),
          authUserResponse.getTempRegId(),
          ErrorCode.REGISTRATION_EMAIL_SEND_FAILED);
    }

    logger.exit("user account successfully created and email sent with verification code");

    return new UserRegistrationResponse(
        authUserResponse.getUserId(), authUserResponse.getTempRegId());
  }

  private boolean generateVerificationCode(UserDetailsEntity userDetails) {
    return UserStatus.PENDING_EMAIL_CONFIRMATION.getValue() == userDetails.getStatus()
        && (StringUtils.isEmpty(userDetails.getEmailCode())
            || Timestamp.from(Instant.now()).after(userDetails.getCodeExpireDate()));
  }

  private EmailResponse generateAndSaveVerificationCode(
      UserDetailsEntity userDetails, String appName) {
    String verificationCode = RandomStringUtils.randomAlphanumeric(VERIFICATION_CODE_LENGTH);
    EmailResponse emailResponse = sendConfirmationEmail(userDetails, verificationCode, appName);
    if (MessageCode.EMAIL_ACCEPTED_BY_MAIL_SERVER.getMessage().equals(emailResponse.getMessage())) {
      userDetails.setEmailCode(verificationCode);
      userDetails.setCodeExpireDate(Timestamp.valueOf(LocalDateTime.now().plusHours(expireTime)));
      userDetailsRepository.saveAndFlush(userDetails);
    }
    return emailResponse;
  }

  private UserDetailsEntity fromUserRegistrationForm(UserRegistrationForm user) {
    UserDetailsEntity userDetails = new UserDetailsEntity();
    userDetails.setStatus(UserStatus.PENDING_EMAIL_CONFIRMATION.getValue());
    userDetails.setVerificationDate(new Timestamp(System.currentTimeMillis()));
    userDetails.setUserId(user.getUserId());
    userDetails.setEmail(user.getEmailId());
    userDetails.setUsePassCode(user.isUsePassCode());
    userDetails.setLocalNotificationFlag(user.isLocalNotification());
    userDetails.setRemoteNotificationFlag(user.isRemoteNotification());
    userDetails.setTouchId(user.isTouchId());
    return userDetails;
  }

  private UserResponse registerUserInAuthServer(
      UserRegistrationForm user, AuditLogEventRequest auditRequest) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + oauthService.getAccessToken());
    AuditEventMapper.addAuditEventHeaderParams(headers, auditRequest);

    UserRequest userRequest = new UserRequest();
    userRequest.setEmail(user.getEmailId());
    userRequest.setPassword(user.getPassword());
    userRequest.setAppId(user.getAppId());
    userRequest.setStatus(UserAccountStatus.PENDING_CONFIRMATION.getStatus());

    HttpEntity<UserRequest> requestEntity = new HttpEntity<>(userRequest, headers);

    ResponseEntity<UserResponse> response =
        restTemplate.postForEntity(authRegisterUrl, requestEntity, UserResponse.class);

    return response.getBody();
  }

  private EmailResponse sendConfirmationEmail(
      UserDetailsEntity userDetails, String verificationCode, String appName) {
    Map<String, String> templateArgs = new HashMap<>();
    templateArgs.put("securitytoken", verificationCode);
    templateArgs.put("orgName", appConfig.getOrgName());
    templateArgs.put("contactEmail", appConfig.getContactEmail());
    templateArgs.put("appName", appName);
    EmailRequest emailRequest =
        new EmailRequest(
            appConfig.getFromEmail(),
            new String[] {userDetails.getEmail()},
            null,
            null,
            appConfig.getConfirmationMailSubject(),
            appConfig.getConfirmationMail(),
            templateArgs);
    return emailService.sendMimeMail(emailRequest);
  }

  private void saveAuthInfoAndUserAppDetails(UserDetailsEntity userDetails) {

    AuthInfoEntity authInfo = new AuthInfoEntity();
    authInfo.setApp(userDetails.getApp());
    authInfo.setUserDetails(userDetails);
    authInfoRepository.saveAndFlush(authInfo);

    UserAppDetailsEntity userAppDetails = new UserAppDetailsEntity();
    userAppDetails.setApp(userDetails.getApp());
    userAppDetails.setUserDetails(userDetails);
    userAppDetailsRepository.saveAndFlush(userAppDetails);
  }
}
