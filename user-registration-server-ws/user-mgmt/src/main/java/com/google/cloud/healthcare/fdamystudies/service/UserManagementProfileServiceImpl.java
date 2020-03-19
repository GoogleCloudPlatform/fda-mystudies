/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.google.cloud.healthcare.fdamystudies.beans.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.beans.DeactivateAcctBean;
import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.beans.UserProfileRespBean;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequestBean;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.dao.UserProfileManagementDao;
import com.google.cloud.healthcare.fdamystudies.model.AppInfoDetailsBO;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.LoginAttemptsBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetails;
import com.google.cloud.healthcare.fdamystudies.util.EmailNotification;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;
import com.google.cloud.healthcare.fdamystudies.util.UserManagementUtil;

@Service
public class UserManagementProfileServiceImpl implements UserManagementProfileService {

  @Autowired UserProfileManagementDao userProfileManagementDao;

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Autowired EmailNotification emailNotification;

  @Autowired CommonDao commonDao;

  @Autowired private UserManagementUtil userManagementUtil;

  private static final Logger logger =
      LoggerFactory.getLogger(UserManagementProfileServiceImpl.class);

  @Override
  public UserProfileRespBean getParticipantInfoDetails(
      String userId, Integer appInfoId, Integer orgInfoId) {
    logger.info("UserManagementProfileServiceImpl getParticipantInfoDetails() - Started ");
    UserDetails userDetails = null;
    UserProfileRespBean userProfileRespBean = new UserProfileRespBean();
    try {
      userDetails = userProfileManagementDao.getParticipantInfoDetails(userId);
      if (userDetails != null) {
        userProfileRespBean.getProfile().setEmailId(userDetails.getEmail());
        userProfileRespBean
            .getSettings()
            .setRemoteNotifications(userDetails.getRemoteNotificationFlag());
        userProfileRespBean
            .getSettings()
            .setLocalNotifications(userDetails.getLocalNotificationFlag());
        userProfileRespBean.getSettings().setTouchId(userDetails.getTouchId());
        userProfileRespBean.getSettings().setPasscode(userDetails.getUsePassCode());
        userProfileRespBean.getSettings().setLocale(userDetails.getLocale());
      }

    } catch (Exception e) {
      logger.error("UserManagementProfileServiceImpl getParticipantInfoDetails() - error ", e);
    }
    logger.info("UserManagementProfileServiceImpl getParticipantInfoDetails() - Ends ");
    return userProfileRespBean;
  }

  @Override
  public ErrorBean updateUserProfile(String userId, UserRequestBean user) {
    logger.info("UserManagementProfileServiceImpl updateUserProfile() - Started ");
    ErrorBean errorBean = null;
    UserDetails userDetails = null;
    AuthInfoBO authInfo = null;
    Integer userDetailsId = 0;
    try {
      userDetails = userProfileManagementDao.getParticipantInfoDetails(userId);
      if (user != null) {
        System.out.println("User Not Null");
        if (userDetails != null) {
          System.out.println("User details Not Null" + userDetails.getUserDetailsId());
          if (user.getSettings() != null) {
            if (user.getSettings().getRemoteNotifications() != null) {
              userDetails.setRemoteNotificationFlag(user.getSettings().getRemoteNotifications());
              try {
                authInfo = userProfileManagementDao.getAuthInfo(userDetails.getUserDetailsId());
                if (authInfo != null) {
                  authInfo.setRemoteNotificationFlag(user.getSettings().getRemoteNotifications());

                  if ((user.getInfo().getOs() != null)
                      && !StringUtils.isEmpty(user.getInfo().getOs())) {
                    authInfo.setDeviceType(user.getInfo().getOs());
                  }
                  if ((user.getInfo().getOs() != null)
                      && !StringUtils.isEmpty(user.getInfo().getOs())
                      && (user.getInfo().getOs().equalsIgnoreCase("IOS")
                          || user.getInfo().getOs().equalsIgnoreCase("I"))) {
                    authInfo.setIosAppVersion(user.getInfo().getAppVersion());
                  } else {
                    authInfo.setAndroidAppVersion(user.getInfo().getAppVersion());
                  }
                  if ((user.getInfo().getDeviceToken() != null)
                      && !StringUtils.isEmpty(user.getInfo().getDeviceToken())) {
                    authInfo.setDeviceToken(user.getInfo().getDeviceToken());
                  }

                  authInfo.setModifiedOn(new Date());
                }
              } catch (Exception e) {
                logger.error("UserManagementProfileServiceImpl - updateUserProfile() - Error", e);
              }
            }
            if (user.getSettings().getLocalNotifications() != null) {
              userDetails.setLocalNotificationFlag(user.getSettings().getLocalNotifications());
            }
            if (user.getSettings().getPasscode() != null) {
              userDetails.setUsePassCode(user.getSettings().getPasscode());
            }
            if (user.getSettings().getTouchId() != null) {
              userDetails.setTouchId(user.getSettings().getTouchId());
            }
            if ((user.getSettings().getReminderLeadTime() != null)
                && !StringUtils.isEmpty(user.getSettings().getReminderLeadTime())) {
              userDetails.setReminderLeadTime(user.getSettings().getReminderLeadTime());
            }
            if ((user.getSettings().getLocale() != null)
                && !StringUtils.isEmpty(user.getSettings().getLocale())) {
              userDetails.setLocale(user.getSettings().getLocale());
            }
          }
          errorBean = userProfileManagementDao.updateUserProfile(userId, userDetails, authInfo);
        }
      }

    } catch (Exception e) {
      logger.error("UserManagementProfileServiceImpl - updateUserProfile() - Error", e);
      errorBean = new ErrorBean(ErrorCode.EC_500.code(), ErrorCode.EC_500.errorMessage());
    }
    logger.info("UserManagementProfileServiceImpl updateUserProfile() - Ends ");
    return errorBean;
  }

  @Override
  public UserDetails getParticipantDetailsByEmail(
      String email, Integer appInfoId, Integer orgInfoId) {
    logger.info("UserManagementProfileServiceImpl getParticipantDetailsByEmail() - Started ");
    UserDetails userDetails = null;
    try {
      userDetails =
          userProfileManagementDao.getParticipantDetailsByEmail(email, appInfoId, orgInfoId);
    } catch (Exception e) {
      logger.error("UserManagementProfileServiceImpl - getParticipantDetailsByEmail() - Error", e);
    }

    logger.info("UserManagementProfileServiceImpl getParticipantDetailsByEmail() - Ends ");
    return userDetails;
  }

  @Override
  public LoginAttemptsBO getLoginAttempts(String email) {
    logger.info("UserManagementProfileServiceImpl getLoginAttempts() - Started ");
    LoginAttemptsBO loginAttempts = null;
    try {
      loginAttempts = userProfileManagementDao.getLoginAttempts(email);
    } catch (Exception e) {
      logger.error("UserManagementProfileServiceImpl - getLoginAttempts() - Error", e);
    }

    logger.info("UserManagementProfileServiceImpl getLoginAttempts() - Started ");
    return loginAttempts;
  }

  @Override
  public void resetLoginAttempts(String email) {
    logger.info("UserManagementProfileServiceImpl resetLoginAttempts() - Started ");
    try {
      userProfileManagementDao.resetLoginAttempts(email);
    } catch (Exception e) {
      logger.error("UserManagementProfileServiceImpl - resetLoginAttempts() - Error", e);
    }

    logger.info("UserManagementProfileServiceImpl getLoginAttempts() - Ends ");
  }

  @Override
  public int sendPasswordResetLinkthroughEmail(
      String emailId, String tempPassword, UserDetails participantDetails) {
    logger.info("UserManagementProfileServiceImpl - sendPasswordResetLinkthroughEmail() - start");
    String dynamicContent = "";
    String content = "";
    Map<String, String> emailMap = null;
    boolean isSent = false;
    int isEmailSent = 0;
    String subject = "";
    UserDetails upParticipantDetails = null;
    try {
      upParticipantDetails = userProfileManagementDao.saveParticipant(participantDetails);
      if (upParticipantDetails != null) {
        subject = appConfig.getPasswdResetLinkSubject();
        content = appConfig.getPasswdResetLinkContent();
        dynamicContent = MyStudiesUserRegUtil.generateEmailContent(content, emailMap);
        isSent =
            emailNotification.sendEmailNotification(subject, dynamicContent, emailId, null, null);
        if (!isSent) {
          isEmailSent = 1;
        } else {
          isEmailSent = 2;
        }
      } else {
        isEmailSent = 2;
      }
    } catch (Exception e) {
      isEmailSent = 3;
      logger.error(
          "UserManagementProfileServiceImpl - sendPasswordResetLinkthroughEmail() - error() ", e);
    }
    logger.info("UserManagementProfileServiceImpl - sendPasswordResetLinkthroughEmail() - end");
    return isEmailSent;
  }

  @Override
  public UserDetails getParticipantDetails(String id) {
    logger.info("UserManagementProfileServiceImpl - getParticipantDetails() - Starts");
    UserDetails userDetails = null;
    AppOrgInfoBean appOrgInfoBean = new AppOrgInfoBean();
    try {
      userDetails = userProfileManagementDao.getParticipantDetails(id);
    } catch (Exception e) {
      logger.error("UserManagementProfileServiceImpl - getParticipantDetails() - error() ", e);
    }

    logger.info("UserManagementProfileServiceImpl - getParticipantDetails() - Ends");
    return userDetails;
  }

  /*@Override
  public Boolean getPasswordHistory(String userId, String newPassword) {
    logger.info("UserManagementProfileServiceImpl - getPasswordHistory() - Starts");
    List<PasswordHistoryBO> passwordHistoryList = null;
    Boolean isValidPassword = true;
    Integer userDetailsId = null;
    try {
      userDetailsId = commonDao.getUserInfoDetails(userId);
      passwordHistoryList = userProfileManagementDao.getPasswordHistoryList(userDetailsId);
      if ((passwordHistoryList != null) && !passwordHistoryList.isEmpty()) {
        for (PasswordHistoryBO userPasswordHistory : passwordHistoryList) {
          if (MyStudiesUserRegUtil.getEncryptedString(newPassword)
              .equalsIgnoreCase(userPasswordHistory.getPassword())) {
            isValidPassword = false;
            break;
          }
        }
      }
    } catch (Exception e) {
      isValidPassword = false;
      logger.error("UserManagementProfileServiceImpl - getPasswordHistory() - error() ", e);
    }
    logger.info("UserManagementProfileServiceImpl - getPasswordHistory() - Starts");
    return isValidPassword;
  }*/

  @Override
  public UserDetails saveParticipant(UserDetails participant) {
    logger.info("UserManagementProfileServiceImpl - saveParticipant() - Starts");
    UserDetails userDetails = null;

    try {
      userDetails = userProfileManagementDao.saveParticipant(participant);
    } catch (Exception e) {
      logger.error("UserManagementProfileServiceImpl - getPasswordHistory() - error() ", e);
    }

    logger.info("UserManagementProfileServiceImpl - saveParticipant() - Starts");
    return userDetails;
  }

  /* @Override
  public ResponseBean savePasswordHistory(
      String userId, String password, String applicationId, String orgId) {
    logger.info("UserManagementProfileServiceImpl - getPasswordHistory() - Starts");
    ResponseBean responseBean = new ResponseBean();
    String message = "";
    try {
      message =
          userProfileManagementDao.savePasswordHistory(userId, password, applicationId, orgId);
      if (message.equalsIgnoreCase(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue())) {
        responseBean.setMessage(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
      }
    } catch (Exception e) {
      logger.error("UserManagementProfileServiceImpl - getPasswordHistory() - error() ", e);
    }
    logger.info("UserManagementProfileServiceImpl - getPasswordHistory() - Ends");
    return responseBean;
  }*/

  @Override
  public String deActivateAcct(
      String userId,
      DeactivateAcctBean deactivateAcctBean,
      String accessToken,
      String clientToken) {
    logger.info("UserManagementProfileServiceImpl - deActivateAcct() - Starts");
    String message = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
    Integer userDetailsId = 0;
    boolean returnVal = false;

    try {
      userDetailsId = commonDao.getUserInfoDetails(userId);
      returnVal =
          userProfileManagementDao.deActivateAcct(userId, deactivateAcctBean, userDetailsId);
      if (returnVal) {
        message = userManagementUtil.deactivateAcct(userId, accessToken, clientToken);
      }
    } catch (Exception e) {
      logger.error("UserManagementProfileServiceImpl - deActivateAcct() - error() ", e);
    }
    logger.info("UserManagementProfileServiceImpl - deActivateAcct() - Ends");
    return message;
  }

  @Override
  public int resendConfirmationthroughEmail(
      String applicationId, String securityToken, String emailId) {
    logger.info("UserManagementProfileServiceImpl - resendConfirmationthroughEmail() - Starts");
    String code = "";
    AppInfoDetailsBO appPropertiesDetails = null;
    String dynamicContent = "";
    String content = "";
    Map<String, String> emailMap = new HashMap<String, String>();
    boolean isSent = false;
    int isEmailSent = 0;
    String subject = "";
    AppOrgInfoBean appOrgInfoBean = null;
    try {
      appOrgInfoBean = commonDao.getUserAppDetailsByAllApi("", applicationId, "");
      appPropertiesDetails =
          userProfileManagementDao.getAppPropertiesDetailsByAppId(appOrgInfoBean.getAppInfoId());
      if ((appPropertiesDetails == null)
          || (appPropertiesDetails.getRegEmailSub() == null)
          || (appPropertiesDetails.getRegEmailBody() == null)
          || appPropertiesDetails.getRegEmailBody().equalsIgnoreCase("")
          || appPropertiesDetails.getRegEmailSub().equalsIgnoreCase("")) {
        subject = appConfig.getResendConfirmationMailSubject();
        content = appConfig.getResendConfirmationMail();
        emailMap.put("$securitytoken", securityToken);
      } else {
        content =
            appPropertiesDetails.getRegEmailBody().replace("<<< TOKEN HERE >>>", securityToken);
        subject = appPropertiesDetails.getRegEmailSub();
      }
      dynamicContent = MyStudiesUserRegUtil.generateEmailContent(content, emailMap);
      isSent =
          emailNotification.sendEmailNotification(subject, dynamicContent, emailId, null, null);
      if (!isSent) {
        isEmailSent = 1;
      } else {
        isEmailSent = 2;
      }
    } catch (Exception e) {
      logger.error(
          "UserManagementProfileServiceImpl - resendConfirmationthroughEmail() - error() ", e);
    }
    logger.info("UserManagementProfileServiceImpl - resendConfirmationthroughEmail() - Ends");
    return isEmailSent;
  }

  /*@Override
  public AppOrgInfoBean getUserAppDetailsByAllApi(
      String userId, String emailId, String appId, String orgId) {
    AppOrgInfoBean appOrgInfoBean = new AppOrgInfoBean();
    logger.info("MyStudiesUserRegUtil - getUserAppDetailsByAllApi() Start");
    try {
      appOrgInfoBean = commonDao.getUserAppDetailsByAllApi(userId, appId, orgId);
    } catch (Exception e) {
      logger.error("MyStudiesUserRegUtil - getUserAppDetailsByAllApi() - error() ", e);
    }

    logger.info("MyStudiesUserRegUtil - getUserAppDetailsByAllApi() Ends");
    return appOrgInfoBean;
  }*/
}
