package com.google.cloud.healthcare.fdamystudies.service;

import static com.google.cloud.healthcare.fdamystudies.common.ErrorCode.EMAIL_SEND_FAILED_EXCEPTION;
import static com.google.cloud.healthcare.fdamystudies.common.ErrorCode.USER_ALREADY_EXISTS;

import com.google.cloud.healthcare.fdamystudies.beans.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.beans.EmailRequest;
import com.google.cloud.healthcare.fdamystudies.beans.EmailResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UserRegistrationForm;
import com.google.cloud.healthcare.fdamystudies.beans.UserRegistrationResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserResponse;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.common.UserAccountStatus;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.repository.UserDetailsBORepository;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.UserDetailsBO;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;
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
import org.springframework.web.client.RestTemplate;

@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {

  private XLogger logger = XLoggerFactory.getXLogger(UserRegistrationServiceImpl.class.getName());

  @Autowired private UserDetailsBORepository userDetailsRepository;

  @Autowired private CommonDao commonDao;

  @Autowired private OAuthService oauthService;

  @Autowired private RestTemplate restTemplate;

  @Autowired private ApplicationPropertyConfiguration appConfig;

  @Autowired private EmailService emailService;

  @Value("${register.url}")
  private String authRegisterUrl;

  @Value("${email.code.expire_time}")
  private long expireTime;

  @Override
  public UserRegistrationResponse register(UserRegistrationForm user) {
    logger.entry("begin register()");

    // find appInfoId using appId
    AppOrgInfoBean appOrgInfoBean =
        commonDao.getUserAppDetailsByAllApi(user.getUserId(), user.getAppId(), user.getOrgId());

    // find user by email and appId
    Optional<UserDetailsBO> optUserDetails =
        userDetailsRepository.findByEmailAndAppInfoId(
            user.getEmailId(), appOrgInfoBean.getAppInfoId());

    // Return USER_ALREADY_EXISTS error code if user account already exists for the given email
    UserDetailsBO userDetailsBO = new UserDetailsBO();
    if (optUserDetails.isPresent()) {
      userDetailsBO = optUserDetails.get();
      if (StringUtils.isNotEmpty(userDetailsBO.getUserId())) {
        if (generateVerificationCode(userDetailsBO)) {
          generateAndSaveVerificationCode(userDetailsBO);
        }

        logger.warn(USER_ALREADY_EXISTS.toString());
        throw new ErrorCodeException(USER_ALREADY_EXISTS);
      }
    }

    // save user details
    userDetailsBO = toUserDetailsBO(user, userDetailsBO);
    userDetailsBO.setAppInfoId(appOrgInfoBean.getAppInfoId());
    userDetailsBO = userDetailsRepository.saveAndFlush(userDetailsBO);

    // Call POST /users API to create a user account in oauth-scim-server
    UserResponse authUserResponse = registerUserInAuthServer(user);

    // save authUserId and verfication code
    userDetailsBO.setUserId(authUserResponse.getUserId());
    userDetailsBO = userDetailsRepository.saveAndFlush(userDetailsBO);

    // generate save and email the verification code
    userDetailsBO = generateAndSaveVerificationCode(userDetailsBO);

    // verification code is empty if send email is failed
    if (StringUtils.isEmpty(userDetailsBO.getEmailCode())) {
      logger.warn(EMAIL_SEND_FAILED_EXCEPTION.toString());
      throw new ErrorCodeException(EMAIL_SEND_FAILED_EXCEPTION);
    }

    logger.exit("user account successfully created and email sent with verification code");

    return new UserRegistrationResponse(
        String.valueOf(userDetailsBO.getUserDetailsId()),
        authUserResponse.getTempRegId(),
        authUserResponse.getUserId());
  }

  private boolean generateVerificationCode(UserDetailsBO userDetailsBO) {
    return UserAccountStatus.PENDING_CONFIRMATION.getStatus() == userDetailsBO.getStatus()
        && (StringUtils.isEmpty(userDetailsBO.getEmailCode())
            || LocalDateTime.now().isAfter(userDetailsBO.getCodeExpireDate()));
  }

  private UserDetailsBO generateAndSaveVerificationCode(UserDetailsBO userDetailsBO) {
    String verificationCode = RandomStringUtils.randomAlphanumeric(6);
    EmailResponse emailResponse = sendConfirmationEmail(userDetailsBO, verificationCode);
    if (MessageCode.EMAIL_ACCEPTED_BY_MAIL_SERVER.getMessage().equals(emailResponse.getMessage())) {
      userDetailsBO.setEmailCode(verificationCode);
      userDetailsBO.setCodeExpireDate(LocalDateTime.now().plusMinutes(expireTime));
      userDetailsBO = userDetailsRepository.saveAndFlush(userDetailsBO);
    }
    return userDetailsBO;
  }

  private UserDetailsBO toUserDetailsBO(UserRegistrationForm user, UserDetailsBO userDetailsBO) {
    userDetailsBO.setStatus(UserAccountStatus.PENDING_CONFIRMATION.getStatus());
    userDetailsBO.setVerificationDate(MyStudiesUserRegUtil.getCurrentUtilDateTime());
    userDetailsBO.setUserId(user.getUserId());
    userDetailsBO.setEmail(user.getEmailId());
    userDetailsBO.setUsePassCode(user.isUsePassCode());
    userDetailsBO.setLocalNotificationFlag(user.isLocalNotification());
    userDetailsBO.setRemoteNotificationFlag(user.isRemoteNotification());
    userDetailsBO.setTouchId(user.isTouchId());
    return userDetailsBO;
  }

  private UserResponse registerUserInAuthServer(UserRegistrationForm user) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + oauthService.getAccessToken());

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
      UserDetailsBO userDetailsBO, String verificationCode) {
    Map<String, String> templateArgs = new HashMap<>();
    templateArgs.put("securitytoken", verificationCode);
    templateArgs.put("orgName", appConfig.getOrgName());
    EmailRequest emailRequest =
        new EmailRequest(
            appConfig.getFromEmailAddress(),
            new String[] {userDetailsBO.getEmail()},
            null,
            null,
            appConfig.getConfirmationMailSubject(),
            appConfig.getConfirmationMail(),
            templateArgs);
    return emailService.sendMimeMail(emailRequest);
  }
}
