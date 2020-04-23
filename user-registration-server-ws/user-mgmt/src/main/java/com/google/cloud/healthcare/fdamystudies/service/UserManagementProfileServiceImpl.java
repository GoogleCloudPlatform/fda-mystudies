/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.google.cloud.healthcare.fdamystudies.bean.StudyReqBean;
import com.google.cloud.healthcare.fdamystudies.beans.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.beans.DeactivateAcctBean;
import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.beans.UserProfileRespBean;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequestBean;
import com.google.cloud.healthcare.fdamystudies.beans.WithdrawFromStudyBean;
import com.google.cloud.healthcare.fdamystudies.beans.WithdrawFromStudyRespFromServer;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.dao.UserProfileManagementDao;
import com.google.cloud.healthcare.fdamystudies.model.AppInfoDetailsBO;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.LoginAttemptsBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;
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
    logger.info("UserManagementProfileServiceImpl getParticipantInfoDetails() - Starts ");
    UserDetailsBO userDetailsBO = null;
    UserProfileRespBean userProfileRespBean = new UserProfileRespBean();
    try {
      userDetailsBO = userProfileManagementDao.getParticipantInfoDetails(userId);
      if (userDetailsBO != null) {
        userProfileRespBean.getProfile().setEmailId(userDetailsBO.getEmail());
        userProfileRespBean
            .getSettings()
            .setRemoteNotifications(userDetailsBO.getRemoteNotificationFlag());
        userProfileRespBean
            .getSettings()
            .setLocalNotifications(userDetailsBO.getLocalNotificationFlag());
        userProfileRespBean.getSettings().setTouchId(userDetailsBO.getTouchId());
        userProfileRespBean.getSettings().setPasscode(userDetailsBO.getUsePassCode());
        userProfileRespBean.getSettings().setLocale(userDetailsBO.getLocale());
      }

    } catch (Exception e) {
      logger.error("UserManagementProfileServiceImpl getParticipantInfoDetails() - error ", e);
    }
    logger.info("UserManagementProfileServiceImpl getParticipantInfoDetails() - Ends ");
    return userProfileRespBean;
  }

  @Override
  public ErrorBean updateUserProfile(String userId, UserRequestBean user) {
    logger.info("UserManagementProfileServiceImpl updateUserProfile() - Starts ");
    ErrorBean errorBean = null;
    UserDetailsBO userDetailsBO = null;
    AuthInfoBO authInfo = null;
    try {
      userDetailsBO = userProfileManagementDao.getParticipantInfoDetails(userId);
      if (user != null && userDetailsBO != null) {
        if (user.getSettings() != null) {
          if (user.getSettings().getRemoteNotifications() != null) {
            userDetailsBO.setRemoteNotificationFlag(user.getSettings().getRemoteNotifications());
            try {
              authInfo = userProfileManagementDao.getAuthInfo(userDetailsBO.getUserDetailsId());
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
            userDetailsBO.setLocalNotificationFlag(user.getSettings().getLocalNotifications());
          }
          if (user.getSettings().getPasscode() != null) {
            userDetailsBO.setUsePassCode(user.getSettings().getPasscode());
          }
          if (user.getSettings().getTouchId() != null) {
            userDetailsBO.setTouchId(user.getSettings().getTouchId());
          }
          if ((user.getSettings().getReminderLeadTime() != null)
              && !StringUtils.isEmpty(user.getSettings().getReminderLeadTime())) {
            userDetailsBO.setReminderLeadTime(user.getSettings().getReminderLeadTime());
          }
          if ((user.getSettings().getLocale() != null)
              && !StringUtils.isEmpty(user.getSettings().getLocale())) {
            userDetailsBO.setLocale(user.getSettings().getLocale());
          }
        }
        errorBean = userProfileManagementDao.updateUserProfile(userId, userDetailsBO, authInfo);
      }

    } catch (Exception e) {
      logger.error("UserManagementProfileServiceImpl - updateUserProfile() - Error", e);
      errorBean = new ErrorBean(ErrorCode.EC_500.code(), ErrorCode.EC_500.errorMessage());
    }
    logger.info("UserManagementProfileServiceImpl updateUserProfile() - Ends ");
    return errorBean;
  }

  @Override
  public UserDetailsBO getParticipantDetailsByEmail(
      String email, Integer appInfoId, Integer orgInfoId) {
    logger.info("UserManagementProfileServiceImpl getParticipantDetailsByEmail() - Starts ");
    UserDetailsBO userDetailsBO = null;
    try {
      userDetailsBO =
          userProfileManagementDao.getParticipantDetailsByEmail(email, appInfoId, orgInfoId);
    } catch (Exception e) {
      logger.error("UserManagementProfileServiceImpl - getParticipantDetailsByEmail() - Error", e);
    }

    logger.info("UserManagementProfileServiceImpl getParticipantDetailsByEmail() - Ends ");
    return userDetailsBO;
  }

  @Override
  public LoginAttemptsBO getLoginAttempts(String email) {
    logger.info("UserManagementProfileServiceImpl getLoginAttempts() - Starts ");
    LoginAttemptsBO loginAttempts = null;
    try {
      loginAttempts = userProfileManagementDao.getLoginAttempts(email);
    } catch (Exception e) {
      logger.error("UserManagementProfileServiceImpl - getLoginAttempts() - Error", e);
    }

    logger.info("UserManagementProfileServiceImpl getLoginAttempts() - Ends ");
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
  public UserDetailsBO getParticipantDetails(String id) {
    logger.info("UserManagementProfileServiceImpl - getParticipantDetails() - Starts");
    UserDetailsBO userDetailsBO = null;
    try {
      userDetailsBO = userProfileManagementDao.getParticipantDetails(id);
    } catch (Exception e) {
      logger.error("UserManagementProfileServiceImpl - getParticipantDetails() - error() ", e);
    }

    logger.info("UserManagementProfileServiceImpl - getParticipantDetails() - Ends");
    return userDetailsBO;
  }

  @Override
  public UserDetailsBO saveParticipant(UserDetailsBO participant) {
    logger.info("UserManagementProfileServiceImpl - saveParticipant() - Starts");
    UserDetailsBO userDetailsBO = null;

    try {
      userDetailsBO = userProfileManagementDao.saveParticipant(participant);
    } catch (Exception e) {
      logger.error("UserManagementProfileServiceImpl - getPasswordHistory() - error() ", e);
    }

    logger.info("UserManagementProfileServiceImpl - saveParticipant() - Ends");
    return userDetailsBO;
  }

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
    WithdrawFromStudyBean studyBean = null;
    WithdrawFromStudyRespFromServer resp = null;
    String participantId = "";
    String retVal = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
    List<String> deleteData = new ArrayList<String>();
    try {
      userDetailsId = commonDao.getUserInfoDetails(userId);
      message = userManagementUtil.deactivateAcct(userId, accessToken, clientToken);
      if (message.equalsIgnoreCase(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue())) {
        if (deactivateAcctBean != null
            && deactivateAcctBean.getDeleteData() != null
            && !deactivateAcctBean.getDeleteData().isEmpty()) {
          for (StudyReqBean studyReqBean : deactivateAcctBean.getDeleteData()) {
            studyBean = new WithdrawFromStudyBean();
            participantId = commonDao.getParticicpantId(userDetailsId, studyReqBean.getStudyId());
            studyReqBean.setStudyId(studyReqBean.getStudyId());
            if (participantId != null && !participantId.isEmpty())
              studyBean.setParticipantId(participantId);
            studyBean.setDelete(studyReqBean.getDelete());
            studyBean.setStudyId(studyReqBean.getStudyId());
            deleteData.add(studyReqBean.getStudyId());
            retVal =
                userManagementUtil.withdrawParticipantFromStudy(
                    studyBean.getParticipantId(), studyBean.getStudyId(), studyBean.getDelete());
          }
        } else {
          retVal = MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue();
        }
        if (retVal != null
            && retVal.equalsIgnoreCase(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue())) {
          returnVal = userProfileManagementDao.deActivateAcct(userId, deleteData, userDetailsId);
          if (returnVal) {
            message = MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue();
          } else {
            message = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
          }
        }
      }
    } catch (Exception e) {
      message = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
      logger.error("UserManagementProfileServiceImpl - deActivateAcct() - error() ", e);
    }
    logger.info("UserManagementProfileServiceImpl - deActivateAcct() - Ends");
    return message;
  }

  @Override
  public int resendConfirmationthroughEmail(
      String applicationId, String securityToken, String emailId) {
    logger.info("UserManagementProfileServiceImpl - resendConfirmationthroughEmail() - Starts");
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
}
