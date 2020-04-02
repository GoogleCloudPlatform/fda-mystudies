/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.cloud.healthcare.fdamystudies.bean.RefreshTokenBean;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.controller.bean.AuthInfoBean;
import com.google.cloud.healthcare.fdamystudies.controller.bean.CheckCredentialRequest;
import com.google.cloud.healthcare.fdamystudies.controller.bean.RegisterUser;
import com.google.cloud.healthcare.fdamystudies.controller.bean.ResponseBean;
import com.google.cloud.healthcare.fdamystudies.controller.bean.UpdateInfo;
import com.google.cloud.healthcare.fdamystudies.exception.DuplicateUserRegistrationException;
import com.google.cloud.healthcare.fdamystudies.exception.EmailIdAlreadyVerifiedException;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidClientException;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidUserIdException;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.exception.UserNotFoundException;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.DaoUserBO;
import com.google.cloud.healthcare.fdamystudies.model.LoginAttemptsBO;
import com.google.cloud.healthcare.fdamystudies.model.PasswordHistoryBO;
import com.google.cloud.healthcare.fdamystudies.repository.LoginAttemptRepository;
import com.google.cloud.healthcare.fdamystudies.repository.PasswordHistoryRepository;
import com.google.cloud.healthcare.fdamystudies.repository.SessionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserRepository.MyView;
import com.google.cloud.healthcare.fdamystudies.service.bean.RefreshTokenServiceResponse;
import com.google.cloud.healthcare.fdamystudies.service.bean.ServiceRegistrationSuccessResponse;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.EmailNotification;
import com.google.cloud.healthcare.fdamystudies.utils.JwtTokenUtil;
import com.google.cloud.healthcare.fdamystudies.utils.MyStudiesUserRegUtil;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

  @Autowired private UserRepository userRepo;

  @Autowired private SessionRepository session;

  @Autowired private ApplicationPropertyConfiguration appConfig;

  @Autowired private JwtTokenUtil jwtTokenUtil;

  @Autowired private EmailNotification emailNotification;

  @Autowired private UserRepository userRepository;

  @Autowired private LoginAttemptRepository loginAttemptRepo;

  @Autowired private PasswordHistoryRepository passHistoryRepo;

  public DaoUserBO loadUserByEmailIdAndAppIdAndOrgIdAndAppCode(
      String userName, String appId, String orgId, String appCode) throws SystemException {
    logger.info("UserDetailsServiceImpl loadUserByEmailIdAndAppIdAndOrgIdAndAppCode() - starts");
    try {
      DaoUserBO daoUser = null;
      if ((userName != null)) {
        daoUser = userRepo.findByEmailIdAndAppIdAndOrgIdAndAppCode(userName, appId, orgId, appCode);
      }
      logger.info("UserDetailsServiceImpl loadUserByEmailIdAndAppIdAndOrgIdAndAppCode() - ends");
      return daoUser;
    } catch (Exception e) {
      logger.error(
          "UserDetailsServiceImpl loadUserByEmailIdAndAppIdAndOrgIdAndAppCode() - error: ", e);
      throw new SystemException();
    }
  }

  @Transactional
  public ServiceRegistrationSuccessResponse save(
      RegisterUser user, String appId, String orgId, String appCode)
      throws DuplicateUserRegistrationException, SystemException {

    logger.info("UserDetailsServiceImpl save() - starts");
    ServiceRegistrationSuccessResponse successResp = null;
    try {
      String emailId = getEmailId(user.getEmailId(), appId, orgId, appCode);
      DaoUserBO newUser = null;
      if (emailId == null) {
        newUser = new DaoUserBO();
        newUser.setEmailId(user.getEmailId());
        String userId =
            RandomStringUtils.randomAlphanumeric(15)
                + "-"
                + RandomStringUtils.randomAlphanumeric(15)
                + "-"
                + RandomStringUtils.randomAlphanumeric(15);
        newUser.setUserId(userId);

        String salt =
            RandomStringUtils.randomAlphanumeric(15)
                + "-"
                + RandomStringUtils.randomAlphanumeric(15)
                + "-"
                + RandomStringUtils.randomAlphanumeric(15);
        newUser.setSalt(salt);
        String encryptedPwd = MyStudiesUserRegUtil.getEncryptedString(user.getPassword(), salt);
        newUser.setPassword(encryptedPwd);
        newUser.setAppId(appId);
        newUser.setOrgId(orgId);
        newUser.setEmailVerificationStatus(AppConstants.PENDING);
        newUser.setCreatedOn(LocalDateTime.now(ZoneId.systemDefault()));

        logger.info("Password Expire time: " + appConfig.getPasswdExpiryInDay());
        /*newUser.setPasswordExpireDate(
        LocalDateTime.now(ZoneId.systemDefault())
            .plusDays(Long.valueOf(appConfig.getPasswdExpiryInDay())));*/

        newUser.setPasswordExpireDate(
            LocalDateTime.now(ZoneId.systemDefault())
                .plusMinutes(Long.valueOf(appConfig.getPasswdExpiryInMin())));

        newUser.setAppCode(appCode);
        newUser.setAccountStatus(AppConstants.ACTIVE);
        DaoUserBO savedUser = userRepo.save(newUser);

        PasswordHistoryBO passwordHistoryBO = new PasswordHistoryBO();
        passwordHistoryBO.set_ts(new Date());
        passwordHistoryBO.setCreated(new Date());
        passwordHistoryBO.setPassword(encryptedPwd);
        passwordHistoryBO.setSalt(salt);
        passwordHistoryBO.setUserId(userId);
        passHistoryRepo.save(passwordHistoryBO);

        // if (savedUser != null && savedPwdHistory != null) {
        Set<GrantedAuthority> roles = new HashSet<>();
        UserDetails userDetails = new User(savedUser.getEmailId(), savedUser.getPassword(), roles);
        AuthInfoBO userTokenDetails =
            jwtTokenUtil.generateToken(
                userDetails, savedUser.getAppId(), savedUser.getOrgId(), savedUser.getAppCode());

        successResp =
            new ServiceRegistrationSuccessResponse(
                savedUser,
                userTokenDetails.getAccessToken(),
                userTokenDetails.getRefreshToken(),
                userTokenDetails.getClientToken());

        logger.info("UserDetailsServiceImpl save() - ends");
        return successResp;
        /*} else {
          logger.error("UserDetailsServiceImpl save() ends With SystemException");
          throw new SystemException();
        }*/
      } else {
        throw new DuplicateUserRegistrationException();
      }
    } catch (DuplicateUserRegistrationException e) {
      logger.error(
          "UserDetailsServiceImpl save() - ends With DuplicateUserRegistrationException: ", e);
      throw new DuplicateUserRegistrationException();
    } catch (Exception e) {
      logger.error(" UserDetailsServiceImpl save() ends With SystemException");
      throw new SystemException();
    }
  }

  private String getEmailId(String user, String appId, String orgId, String appCode)
      throws SystemException {
    logger.info("UserDetailsServiceImpl getEmailId() - starts");
    String emailId = null;
    try {
      DaoUserBO daoUser =
          userRepo.findByEmailIdAndAppIdAndOrgIdAndAppCode(user, appId, orgId, appCode);
      if (daoUser != null) {
        emailId = daoUser.getEmailId();
      }
    } catch (Exception e) {
      logger.error("UserDetailsServiceImpl getEmailId() ends With SystemException: ", e);
      throw new SystemException();
    }
    logger.info("UserDetailsServiceImpl getEmailId() - ends");
    return emailId;
  }

  public Integer validateAccessToken(AuthInfoBean authInfo) throws SystemException {
    logger.info("UserDetailsServiceImpl validateAccessToken() - starts");
    long sessionMinutes = 0;
    Integer value = null;
    try {
      sessionMinutes = Long.parseLong(appConfig.getSessionTimeOutInMinutes());
      AuthInfoBO sessionDetails =
          session.findByUserIdAndAccessToken(authInfo.getUserId(), authInfo.getAccessToken());
      if (null != sessionDetails) {

        LocalDateTime expireDateTime = sessionDetails.getExpireDate();
        if (LocalDateTime.now(ZoneId.systemDefault()).isBefore(expireDateTime)) {
          sessionDetails.setExpireDate(
              LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(sessionMinutes));
          session.save(sessionDetails);
          value = 1; // valid session
        } else {
          value = 0; // session expired
        }
      } else {
        value = 2; // Invalid access token or access token changed
      }
    } catch (Exception e) {
      logger.error("UserDetailsServiceImpl validateAccessToken() - error ", e);
      throw new SystemException();
    }
    logger.info("UserDetailsServiceImpl validateAccessToken() - ends");
    return value;
  }

  public RefreshTokenServiceResponse generateNewTokens(
      RefreshTokenBean refreshToken, String userId, String appCode)
      throws SystemException, UserNotFoundException, InvalidUserIdException,
          InvalidClientException {

    logger.info("UserDetailsServiceImpl generateNewTokens() - starts");

    RefreshTokenServiceResponse response = null;
    AuthInfoBO sessionDetails = null;

    try {

      sessionDetails = session.findByRefreshTokenAndUserId(refreshToken.getRefreshToken(), userId);

      if (sessionDetails != null) {
        if (sessionDetails.getUserId().equals(userId)) {

          DaoUserBO userInfo = userRepository.findByUserId(userId);

          if (userInfo != null) {
            if (appCode.equals(userInfo.getAppCode())) {

              Set<GrantedAuthority> roles = new HashSet<>();
              UserDetails userDetails =
                  new User(userInfo.getEmailId(), userInfo.getPassword(), roles);
              AuthInfoBO tokenDetails =
                  jwtTokenUtil.generateToken(
                      userDetails, userInfo.getAppId(), userInfo.getOrgId(), userInfo.getAppCode());

              if (tokenDetails != null) {
                response = new RefreshTokenServiceResponse();
                response.setAccessToken(tokenDetails.getAccessToken());
                response.setRefreshToken(tokenDetails.getRefreshToken());
                response.setClientToken(tokenDetails.getClientToken());
                response.setUserId(tokenDetails.getUserId());
                return response;
              } else {
                return response;
              }
            } else throw new InvalidClientException();
          } else {
            throw new UserNotFoundException();
          }
        } else {
          throw new InvalidUserIdException();
        }
      } else {
        throw new UserNotFoundException();
      }

    } catch (InvalidUserIdException | UserNotFoundException | InvalidClientException e) {
      logger.error("UserDetailsServiceImpl generateNewTokens() - error ", e);
      throw e;
    } catch (Exception e) {
      logger.error("UserDetailsServiceImpl generateNewTokens() - error ", e);
      throw new SystemException();
    }
  }

  public boolean verify(CheckCredentialRequest checkCredentialRequest) throws SystemException {

    logger.info("UserDetailsServiceImpl verify() - starts");
    if (checkCredentialRequest != null) {

      try {
        DaoUserBO userDetails = null;
        if ("MA".equals(checkCredentialRequest.getAppCode())) {
          userDetails =
              userRepository.findByEmailIdAndAppIdAndOrgIdAndAppCode(
                  checkCredentialRequest.getEmailId(), checkCredentialRequest.getAppId(),
                  checkCredentialRequest.getOrgId(), checkCredentialRequest.getAppCode());
        } else {
          userDetails =
              userRepository.findByEmailIdAndAppCode(
                  checkCredentialRequest.getEmailId(), checkCredentialRequest.getAppCode());
        }

        if (userDetails != null
            && checkCredentialRequest.getAppCode().equals(userDetails.getAppCode())) {
          String inputHashedPassword =
              MyStudiesUserRegUtil.getEncryptedString(
                  checkCredentialRequest.getPassword(), userDetails.getSalt());
          if ((checkCredentialRequest.getEmailId().equals(userDetails.getEmailId())
              && (inputHashedPassword.equals(userDetails.getPassword())))) {
            return true;
          } else {
            logger.info("UserDetailsServiceImpl.verify() ends with Invalid Credential");
            return false;
          }

        } else {
          logger.info("UserDetailsServiceImpl.verify() ends with Invalid AppCode");
          return false;
        }

      } catch (Exception e) {
        logger.error("UserDetailsServiceImpl verify() - error ", e);
        throw new SystemException();
      }
    } else {
      logger.info("UserDetailsServiceImpl verify() - ends");
      return false;
    }
  }

  public String updateStatus(UpdateInfo userInfo, String userId)
      throws SystemException, UserNotFoundException, EmailIdAlreadyVerifiedException {

    logger.info("UserDetailsServiceImpl updateStatus() - starts");
    String message = AppConstants.FAILURE;
    if (userInfo.isEmailVerified()) {
      try {
        DaoUserBO dbResponse = userRepo.findByUserId(userId);
        if (dbResponse != null) {
          if (!"Verified".equals(dbResponse.getEmailVerificationStatus())) {
            dbResponse.setEmailVerificationStatus("Verified");
            DaoUserBO updatedResult = userRepo.save(dbResponse);
            if (updatedResult != null) {
              message = AppConstants.SUCCESS;
              logger.info(message);
              return message;
            } else {
              throw new SystemException();
            }
          } else {
            throw new EmailIdAlreadyVerifiedException();
          }
        } else {
          throw new UserNotFoundException();
        }

      } catch (UserNotFoundException | EmailIdAlreadyVerifiedException e) {
        logger.error("UserDetailsServiceImpl updateStatus() - error ", e);
        throw e;
      } catch (Exception e) {
        logger.error("UserDetailsServiceImpl updateStatus() - error ", e);
        throw new SystemException();
      }
    } else {
      logger.info("UserDetailsServiceImpl updateStatus() - ends");
      return message;
    }
  }

  public String deleteUserDetails(String userId) throws UserNotFoundException, SystemException {

    logger.info("UserDetailsServiceImpl deleteUserDetails() - starts");
    String message = AppConstants.FAILURE;

    if (userId != null) {
      List<Integer> idList = null;
      AuthInfoBO sessionDetails = null;

      try {
        idList =
            userRepo
                .findByUserId(userId, MyView.class)
                .stream()
                .map(
                    (ob) -> {
                      if (ob != null) {
                        return ob.getId();
                      }
                      return null;
                    })
                .collect(Collectors.toList());

        sessionDetails = session.findByUserId(userId);
      } catch (Exception e) {
        logger.error("UserDetailsServiceImpl deleteUserDetails() - error ", e);
        throw new SystemException();
      }
      if (!idList.isEmpty() && (sessionDetails != null)) {
        Integer id = idList.get(0);

        try {
          userRepo.deleteById(id);
          session.deleteById(sessionDetails.getId());
          message = AppConstants.SUCCESS;
          return message;
        } catch (Exception e) {
          logger.error("UserDetailsServiceImpl deleteUserDetails() - error ", e);
          throw new SystemException();
        }
      } else {
        throw new UserNotFoundException();
      }
    } else {
      logger.info("UserDetailsServiceImpl deleteUserDetails() - ends");
      return message;
    }
  }

  public int sendPasswordResetLinkthroughEmail(
      String emailId, String tempPassword, DaoUserBO participantDetails) {
    logger.info("UserDetailsServiceImpl - sendPasswordResetLinkthroughEmail() - starts");
    String dynamicContent = "";
    String content = "";
    Map<String, String> emailMap = new HashMap<>();
    boolean isSent = false;
    int isEmailSent = 0;
    String subject = "";
    try {
      if (participantDetails != null) {
        emailMap = new HashMap<>();
        userRepo.save(participantDetails);
        subject = appConfig.getPasswdResetLinkSubject();
        content = appConfig.getPasswdResetLinkContent();
        emailMap.put("$tempPassword", tempPassword);
        dynamicContent = MyStudiesUserRegUtil.generateEmailContent(content, emailMap);
        isSent =
            emailNotification.sendEmailNotification(subject, dynamicContent, emailId, null, null);
        if (!isSent) {
          isEmailSent = 1;
        } else {
          isEmailSent = 2;
        }
      } else {
        isEmailSent = 3;
      }
    } catch (Exception e) {
      isEmailSent = 3;
      logger.error("UserDetailsServiceImpl - sendPasswordResetLinkthroughEmail() - error() ", e);
    }
    logger.info("UserDetailsServiceImpl - sendPasswordResetLinkthroughEmail() - ends");
    return isEmailSent;
  }

  public DaoUserBO loadUserByEmailIdAndAppCode(String emailId, String appCode)
      throws SystemException {
    logger.info("UserDetailsServiceImpl loadUserByEmailIdAndAppCode() - starts ");
    try {
      DaoUserBO daoUser = null;
      if ((emailId != null)) {
        daoUser = userRepo.findByEmailIdAndAppCode(emailId, appCode);
      }
      logger.info("UserDetailsServiceImpl loadUserByEmailIdAndAppCode() - ends ");
      return daoUser;
    } catch (Exception e) {
      throw new SystemException();
    }
  }

  public LoginAttemptsBO getLoginAttempts(String email) {
    logger.info("UserDetailsServiceImpl getLoginAttempts() - starts ");
    LoginAttemptsBO loginAttempts = null;
    try {
      loginAttempts = loginAttemptRepo.findByEmail(email);
    } catch (Exception e) {
      logger.error("UserDetailsServiceImpl - getLoginAttempts() - error", e);
    }

    logger.info("UserDetailsServiceImpl getLoginAttempts() - ends ");
    return loginAttempts;
  }

  public void resetLoginAttempts(String email) {
    logger.info("UserDetailsServiceImpl resetLoginAttempts() - starts ");
    LoginAttemptsBO loginAttempts = null;
    try {
      loginAttempts = loginAttemptRepo.findByEmail(email);
      if (loginAttempts != null) loginAttemptRepo.delete(loginAttempts);
    } catch (Exception e) {
      logger.error("UserDetailsServiceImpl - resetLoginAttempts() - Error", e);
    }

    logger.info("UserDetailsServiceImpl resetLoginAttempts() - ends ");
  }

  public DaoUserBO loadUserByUserId(String userId) throws SystemException {

    logger.info("UserDetailsServiceImpl loadUserByUserId() - starts");
    DaoUserBO dbResponse = null;
    if (userId != null) {
      try {
        dbResponse = userRepo.findByUserId(userId);
        logger.info("UserDetailsServiceImpl loadUserByUserId() - ends");
        return dbResponse;
      } catch (Exception e) {
        logger.error("UserDetailsServiceImpl loadUserByUserId() - error ", e);
        throw new SystemException();
      }
    } else return dbResponse;
  }

  public ResponseBean changePassword(DaoUserBO userInfo) {
    logger.info("UserDetailsServiceImpl changePassword() - starts");
    ResponseBean responseBean = new ResponseBean();
    try {

      if (userInfo != null) {
        userRepo.save(userInfo);
        responseBean.setMessage(AppConstants.SUCCESS);
      }
    } catch (Exception e) {
      responseBean.setMessage(AppConstants.FAILURE);
      logger.error("UserDetailsServiceImpl changePassword() - error() ", e);
    }

    logger.info("UserDetailsServiceImpl changePassword() - ends");
    return responseBean;
  }

  public ResponseBean deactivateAcct(DaoUserBO userInfo) {
    logger.info("UserDetailsServiceImpl deactivateAcct() - starts");
    ResponseBean responseBean = new ResponseBean();
    try {
      if (userInfo != null) {
        userRepo.delete(userInfo);
        responseBean.setMessage(AppConstants.SUCCESS);
      }
    } catch (Exception e) {
      responseBean.setMessage(AppConstants.FAILURE);
      logger.error("UserDetailsServiceImpl deactivateAcct() - error() ", e);
    }

    logger.info("UserDetailsServiceImpl deactivateAcct() - ends");
    return responseBean;
  }

  public LoginAttemptsBO updateLoginFailureAttempts(String email) {
    logger.info("UserDetailsServiceImpl updateLoginFailureAttempts() - starts");
    LoginAttemptsBO loginAttempts = null;
    int count = 0;
    try {
      loginAttempts = getLoginAttempts(email);
      if (loginAttempts != null) {
        if (loginAttempts.getAttempts() > 0) {
          count = loginAttempts.getAttempts();
        }
        count++;
        loginAttempts.setAttempts(count);
        loginAttempts.setLastModified(MyStudiesUserRegUtil.getCurrentUtilDateTime());
        loginAttemptRepo.save(loginAttempts);
      } else {
        loginAttempts = new LoginAttemptsBO();
        count++;
        loginAttempts.setAttempts(count);
        loginAttempts.setEmail(email);
        loginAttempts.setLastModified(MyStudiesUserRegUtil.getCurrentUtilDateTime());
        loginAttemptRepo.save(loginAttempts);
      }
    } catch (Exception e) {
      logger.error("UserDetailsServiceImpl - updateLoginFailureAttempts() - error() ", e);
    }
    logger.info("UserDetailsServiceImpl updateLoginFailureAttempts() - ends");
    return loginAttempts;
  }

  public DaoUserBO saveUserDetails(DaoUserBO participant) {
    logger.info("UserDetailsServiceImpl saveUserDetails() - Starts");
    DaoUserBO addParticipant = null;
    try {
      addParticipant = userRepo.save(participant);
    } catch (Exception e) {
      logger.error("UserDetailsServiceImpl saveUserDetails() - error() ", e);
    }
    logger.info("UserDetailsServiceImpl saveUserDetails() - Ends");
    return addParticipant;
  }

  public Boolean getPasswordHistory(String userId, String newPassword) {
    logger.info("UserDetailsServiceImpl getPasswordHistory() - starts");
    List<PasswordHistoryBO> passwordHistoryList = null;
    Boolean isValidPassword = true;
    try {
      passwordHistoryList = passHistoryRepo.findByUserId(userId);
      if ((passwordHistoryList != null) && !passwordHistoryList.isEmpty()) {
        for (PasswordHistoryBO userPasswordHistory : passwordHistoryList) {
          if (MyStudiesUserRegUtil.getEncryptedString(newPassword, userPasswordHistory.getSalt())
              .equalsIgnoreCase(userPasswordHistory.getPassword())) {
            isValidPassword = false;
            break;
          }
        }
      }
    } catch (Exception e) {
      isValidPassword = false;
      logger.error("UserDetailsServiceImpl getPasswordHistory() - error() ", e);
    }
    logger.info("UserDetailsServiceImpl getPasswordHistory() - ends");
    return isValidPassword;
  }

  public String savePasswordHistory(String userId, String password, String salt) {
    logger.info("UserDetailsServiceImpl savePasswordHistory() - starts");
    String message = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
    PasswordHistoryBO passHistoryBo = null;
    String passwordHistoryCount = appConfig.getPasswordHistoryCount();
    List<PasswordHistoryBO> passwordHistories = null;
    List<Integer> pwsIds = new ArrayList<>();
    try {
      passwordHistories = passHistoryRepo.findByUserIdOrderByPasswordHistoryIdAsc(userId);
      if (passwordHistories != null
          && !passwordHistories.isEmpty()
          && passwordHistories.size() > (Integer.parseInt(passwordHistoryCount) - 1)) {
        passHistoryRepo.deleteById(passwordHistories.get(0).getPasswordHistoryId());
      }
      passHistoryBo = new PasswordHistoryBO();
      passHistoryBo.setUserId(userId);
      passHistoryBo.setPassword(password);
      passHistoryBo.setSalt(salt);
      passHistoryBo.set_ts(new Date());
      passHistoryBo.setCreated(new Date());
      passHistoryRepo.save(passHistoryBo);

      message = MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue();
    } catch (Exception e) {
      logger.error("UserDetailsServiceImpl savePasswordHistory() - error() ", e);
    }
    logger.info("UserDetailsServiceImpl savePasswordHistory() - ends");
    return message;
  }
}
