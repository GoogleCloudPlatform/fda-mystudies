/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.USER_DELETED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.USER_DELETION_FAILED;

import com.google.cloud.healthcare.fdamystudies.bean.StudyReqBean;
import com.google.cloud.healthcare.fdamystudies.beans.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.DeactivateAcctBean;
import com.google.cloud.healthcare.fdamystudies.beans.EmailRequest;
import com.google.cloud.healthcare.fdamystudies.beans.EmailResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.beans.UserProfileRespBean;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequestBean;
import com.google.cloud.healthcare.fdamystudies.beans.WithdrawFromStudyBean;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.PlatformComponent;
import com.google.cloud.healthcare.fdamystudies.common.UserMgmntAuditHelper;
import com.google.cloud.healthcare.fdamystudies.common.UserStatus;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.dao.UserProfileManagementDao;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoEntity;
import com.google.cloud.healthcare.fdamystudies.model.LoginAttemptsEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.repository.AppRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserDetailsRepository;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;
import com.google.cloud.healthcare.fdamystudies.util.UserManagementUtil;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserManagementProfileServiceImpl implements UserManagementProfileService {

  @Autowired UserProfileManagementDao userProfileManagementDao;

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Autowired CommonDao commonDao;

  @Autowired private UserManagementUtil userManagementUtil;

  @Autowired private UserMgmntAuditHelper userMgmntAuditHelper;

  @Autowired private EmailService emailService;

  @Autowired private AppRepository appRepository;

  @Autowired private StudyRepository studyRepository;

  @Autowired UserDetailsRepository userDetailsRepository;

  private XLogger logger =
      XLoggerFactory.getXLogger(UserManagementProfileServiceImpl.class.getName());

  @Override
  @Transactional(readOnly = true)
  public UserProfileRespBean getParticipantInfoDetails(String userId, Integer appInfoId) {
    logger.entry("Begin getParticipantInfoDetails()");
    UserDetailsEntity userDetails = null;
    UserProfileRespBean userProfileRespBean = null;

    userDetails = userProfileManagementDao.getParticipantInfoDetails(userId);
    if (userDetails != null) {
      userProfileRespBean = new UserProfileRespBean();
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

    logger.exit("getParticipantInfoDetails() - Ends ");
    return userProfileRespBean;
  }

  @Override
  @Transactional()
  public ErrorBean updateUserProfile(String userId, UserRequestBean user) {
    logger.entry("Begin updateUserProfile()");
    ErrorBean errorBean = null;
    UserDetailsEntity userDetails = null;
    AuthInfoEntity authInfo = null;

    userDetails = userProfileManagementDao.getParticipantInfoDetails(userId);
    if (userDetails == null) {
      throw new ErrorCodeException(ErrorCode.USER_NOT_EXISTS);
    }

    if (user != null) {
      if (user.getSettings() != null) {
        if (user.getSettings().getRemoteNotifications() != null) {
          userDetails.setRemoteNotificationFlag(user.getSettings().getRemoteNotifications());
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
      authInfo = authInfoDetails(userDetails, user);
      errorBean = userProfileManagementDao.updateUserProfile(userId, userDetails, authInfo);
    }

    logger.exit("updateUserProfile() - Ends ");
    return errorBean;
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetailsEntity getParticipantDetailsByEmail(String email, String appInfoId) {
    logger.entry("Begin getParticipantDetailsByEmail()");
    UserDetailsEntity userDetails = null;

    Optional<AppEntity> optApp = appRepository.findByAppId(appInfoId);
    if (optApp.isPresent()) {
      userDetails = userProfileManagementDao.getParticipantDetailsByEmail(email, optApp.get());
    }

    logger.exit("getParticipantDetailsByEmail() - Ends ");
    return userDetails;
  }

  @Override
  @Transactional(readOnly = true)
  public LoginAttemptsEntity getLoginAttempts(String email) {
    logger.entry("Begin getLoginAttempts()");
    LoginAttemptsEntity loginAttempts = null;

    loginAttempts = userProfileManagementDao.getLoginAttempts(email);

    logger.exit("getLoginAttempts() - Ends ");
    return loginAttempts;
  }

  @Override
  @Transactional(readOnly = true)
  public void resetLoginAttempts(String email) {
    logger.entry("Begin resetLoginAttempts()");

    userProfileManagementDao.resetLoginAttempts(email);

    logger.exit("getLoginAttempts() - Ends ");
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetailsEntity getParticipantDetails(String id) {
    logger.entry("Begin getParticipantDetails()");

    UserDetailsEntity userDetails = userProfileManagementDao.getParticipantDetails(id);

    logger.exit("getParticipantDetails() - Ends");
    return userDetails;
  }

  @Override
  @Transactional()
  public UserDetailsEntity saveParticipant(UserDetailsEntity participant) {
    logger.entry("Begin saveParticipant()");

    UserDetailsEntity userDetails = userProfileManagementDao.saveParticipant(participant);

    logger.exit("saveParticipant() - Ends");
    return userDetails;
  }

  @Override
  public void processDeactivatePendingRequests() {
    // findUsers With Status DEACTIVATE_PENDING
    List<UserDetailsEntity> listOfUserDetails =
        (List<UserDetailsEntity>)
            CollectionUtils.emptyIfNull(
                userDetailsRepository.findByStatus(UserStatus.DEACTIVATE_PENDING.getValue()));

    // call deactivateUserAccount() for each userID's
    listOfUserDetails.forEach(
        userDetails -> {
          try {
            userManagementUtil.deleteUserInfoInAuthServer(userDetails.getUserId());
            userProfileManagementDao.deactivateUserAccount(userDetails.getUserId());
          } catch (ErrorCodeException e) {
            if (e.getErrorCode() == ErrorCode.USER_NOT_FOUND) {
              userProfileManagementDao.deactivateUserAccount(userDetails.getUserId());
            } else {
              logger.warn(
                  String.format(
                      "Delete user from %s failed with ErrorCode=%s",
                      PlatformComponent.SCIM_AUTH_SERVER.getValue(), e.getErrorCode()));
            }
          } catch (Exception e) {
            logger.error("processDeactivatePendingRequests() failed with an exception", e);
          }
        });
  }

  @Override
  @Transactional()
  public String deactivateAccount(
      String userId, DeactivateAcctBean deactivateAcctBean, AuditLogEventRequest auditRequest) {
    logger.entry("Begin deActivateAcct()");
    String message = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
    String userDetailsId = String.valueOf(0);
    WithdrawFromStudyBean studyBean = null;
    String participantId = "";
    String retVal = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
    List<String> deleteData = new ArrayList<String>();

    Optional<UserDetailsEntity> optUserDetails = userDetailsRepository.findByUserId(userId);

    if (!optUserDetails.isPresent()) {
      throw new ErrorCodeException(ErrorCode.USER_NOT_FOUND);
    }

    userDetailsId = commonDao.getUserInfoDetails(userId);

    logger.info(
        "deactivateAcctBean Request=" + ReflectionToStringBuilder.toString(deactivateAcctBean));
    logger.info("deactivateAcctBean Request=" + deactivateAcctBean.toString());

    if (deactivateAcctBean != null
        && deactivateAcctBean.getStudyData() != null
        && !deactivateAcctBean.getStudyData().isEmpty()) {
      for (StudyReqBean studyReqBean : deactivateAcctBean.getStudyData()) {
        studyBean = new WithdrawFromStudyBean();
        participantId = commonDao.getParticipantId(userDetailsId, studyReqBean.getStudyId());
        studyReqBean.setStudyId(studyReqBean.getStudyId());
        if (participantId != null && !participantId.isEmpty())
          studyBean.setParticipantId(participantId);
        studyBean.setStudyId(studyReqBean.getStudyId());
        deleteData.add(studyReqBean.getStudyId());

        auditRequest.setStudyId(studyBean.getStudyId());
        auditRequest.setParticipantId(studyBean.getParticipantId());
        auditRequest.setUserId(userId);

        Optional<StudyEntity> optStudyEntity =
            studyRepository.findByCustomStudyId(studyBean.getStudyId());

        if (optStudyEntity.isPresent()) {
          auditRequest.setStudyVersion(String.valueOf(optStudyEntity.get().getVersion()));
          retVal =
              userManagementUtil.withdrawParticipantFromStudy(
                  studyBean.getParticipantId(),
                  studyBean.getStudyId(),
                  String.valueOf(optStudyEntity.get().getVersion()),
                  auditRequest);
        }
      }
    } else {
      retVal = MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue();
    }
    if (retVal != null
        && retVal.equalsIgnoreCase(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue())) {

      userProfileManagementDao.deactivateAcct(userId, deleteData, userDetailsId);

      UserDetailsEntity userDetailsEntity = optUserDetails.get();
      userDetailsEntity.setStatus(UserStatus.DEACTIVATE_PENDING.getValue());
      userDetailsRepository.saveAndFlush(userDetailsEntity);

      userManagementUtil.deleteUserInfoInAuthServer(userId);

      // change the status from DEACTIVATE_PENDING to DEACTIVATED
      userProfileManagementDao.deactivateUserAccount(userId);
      message = MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue();

      userMgmntAuditHelper.logEvent(USER_DELETED, auditRequest);
    } else {
      userMgmntAuditHelper.logEvent(USER_DELETION_FAILED, auditRequest);
    }

    logger.exit("deActivateAcct() - Ends");
    return message;
  }

  @Transactional()
  @Override
  public EmailResponse resendConfirmationthroughEmail(
      String applicationId, String securityToken, String emailId, String appName) {
    logger.entry("Begin resendConfirmationthroughEmail()");
    AppEntity appPropertiesDetails = null;

    String content = "";
    String subject = "";
    AppOrgInfoBean appOrgInfoBean = null;
    appOrgInfoBean = commonDao.getUserAppDetailsByAllApi("", applicationId);
    appPropertiesDetails =
        userProfileManagementDao.getAppPropertiesDetailsByAppId(appOrgInfoBean.getAppInfoId());
    Map<String, String> templateArgs = new HashMap<>();
    if ((appPropertiesDetails == null)
        || (appPropertiesDetails.getRegEmailSub() == null)
        || (appPropertiesDetails.getRegEmailBody() == null)
        || appPropertiesDetails.getRegEmailBody().equalsIgnoreCase("")
        || appPropertiesDetails.getRegEmailSub().equalsIgnoreCase("")) {
      subject = appConfig.getConfirmationMailSubject();
      content = appConfig.getConfirmationMail();
    } else {
      content = appPropertiesDetails.getRegEmailBody();
      subject = appPropertiesDetails.getRegEmailSub();
    }

    templateArgs.put("appName", appName);
    // TODO(#496): replace with actual study's org name.
    templateArgs.put("orgName", appConfig.getOrgName());
    templateArgs.put("contactEmail", appConfig.getContactEmail());
    templateArgs.put("securitytoken", securityToken);
    EmailRequest emailRequest =
        new EmailRequest(
            appConfig.getFromEmail(),
            new String[] {emailId},
            null,
            null,
            subject,
            content,
            templateArgs);
    logger.exit("resendConfirmationthroughEmail() - Ends");
    return emailService.sendMimeMail(emailRequest);
  }

  private AuthInfoEntity authInfoDetails(UserDetailsEntity userDetails, UserRequestBean user) {
    AuthInfoEntity authInfo = null;
    authInfo = userProfileManagementDao.getAuthInfo(userDetails);
    if (authInfo != null) {
      if (user.getSettings() != null && user.getSettings().getRemoteNotifications() != null) {
        authInfo.setRemoteNotificationFlag(user.getSettings().getRemoteNotifications());
      }
      if (user.getInfo() != null) {
        if (!StringUtils.isBlank(user.getInfo().getOs())) {
          authInfo.setDeviceType(user.getInfo().getOs());
        }
        if (!StringUtils.isBlank(user.getInfo().getOs())
            && (user.getInfo().getOs().equalsIgnoreCase("IOS")
                || user.getInfo().getOs().equalsIgnoreCase("I"))) {
          authInfo.setIosAppVersion(user.getInfo().getAppVersion());
        } else {
          authInfo.setAndroidAppVersion(user.getInfo().getAppVersion());
        }
        if (!StringUtils.isBlank(user.getInfo().getDeviceToken())) {
          authInfo.setDeviceToken(user.getInfo().getDeviceToken());
        } else if (!StringUtils.isBlank(authInfo.getDeviceToken())) {
          authInfo.setDeviceToken(null);
        }
      }
      authInfo.setModified(Timestamp.from(Instant.now()));
    }
    return authInfo;
  }
}
