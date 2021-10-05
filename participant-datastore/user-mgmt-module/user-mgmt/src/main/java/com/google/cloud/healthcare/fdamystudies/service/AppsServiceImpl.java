/*
 * Copyright 2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.APP_USER_ACCOUNTS_DELETED;

import com.google.cloud.healthcare.fdamystudies.bean.AppDetailsVersionBean;
import com.google.cloud.healthcare.fdamystudies.bean.AppMetadataBean;
import com.google.cloud.healthcare.fdamystudies.bean.DeviceVersionBean;
import com.google.cloud.healthcare.fdamystudies.beans.AppContactEmailsResponse;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.common.PlatformComponent;
import com.google.cloud.healthcare.fdamystudies.common.UserMgmntAuditHelper;
import com.google.cloud.healthcare.fdamystudies.common.UserStatus;
import com.google.cloud.healthcare.fdamystudies.dao.UserProfileManagementDao;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.repository.AppRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserDetailsRepository;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.util.UserManagementUtil;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppsServiceImpl implements AppsService {

  private XLogger logger = XLoggerFactory.getXLogger(AppsServiceImpl.class.getName());

  @Autowired private AppRepository appRepository;

  @Autowired UserDetailsRepository userDetailsRepository;

  @Autowired private UserManagementUtil userManagementUtil;

  @Autowired UserProfileManagementDao userProfileManagementDao;

  @Autowired UserMgmntAuditHelper userMgmntAuditHelper;

  @Override
  @Transactional()
  public ErrorBean saveAppMetadata(AppMetadataBean appMetadataBean) {
    logger.entry("Begin saveAppMetadata()");

    Optional<AppEntity> optAppEntity = appRepository.findByAppId(appMetadataBean.getAppId());

    if (optAppEntity.isPresent()) {
      AppEntity app = optAppEntity.get();
      app = fromAppMetadataBean(appMetadataBean, app);
      app = appRepository.saveAndFlush(app);

    } else {
      AppEntity app = new AppEntity();
      app.setAppId(appMetadataBean.getAppId());
      if (appMetadataBean.getAppType() != null) {
        app.setType(appMetadataBean.getAppType());
      }
      if (appMetadataBean.getAppPlatform() != null) {
        app.setAppPlatform(appMetadataBean.getAppPlatform());
      }

      app = fromAppMetadataBean(appMetadataBean, app);
      app = appRepository.saveAndFlush(app);
    }
    logger.exit("saveAppMetadata() : ends");
    return new ErrorBean(ErrorCode.EC_200.code(), ErrorCode.EC_200.errorMessage());
  }

  private AppEntity fromAppMetadataBean(AppMetadataBean appMetadataBean, AppEntity app) {

    app.setAppName(appMetadataBean.getAppName());
    //  app.setAppDescription(appMetadataBean.getAppDescription());
    if (appMetadataBean.getFeedBackEmail() != null) {
      app.setFeedBackToEmail(appMetadataBean.getFeedBackEmail());
    }
    if (appMetadataBean.getContactEmail() != null) {
      app.setContactUsToEmail(appMetadataBean.getContactEmail());
    }
    if (appMetadataBean.getAppSupportEmail() != null) {
      app.setAppSupportEmailAddress(appMetadataBean.getAppSupportEmail());
    }
    if (appMetadataBean.getFromEmail() != null) {
      app.setFromEmailId(appMetadataBean.getFromEmail());
    }

    if (appMetadataBean.getAppTermsUrl() != null) {
      app.setAppTermsUrl(appMetadataBean.getAppTermsUrl());
    }
    if (appMetadataBean.getAppPrivacyUrl() != null) {
      app.setAppPrivacyUrl(appMetadataBean.getAppPrivacyUrl());
    }
    if (appMetadataBean.getOrganizationName() != null) {
      app.setOrganizationName(appMetadataBean.getOrganizationName());
    }
    if (appMetadataBean.getAppStoreUrl() != null) {
      app.setAppStoreUrl(appMetadataBean.getAppStoreUrl());
    }
    if (appMetadataBean.getPlayStoreUrl() != null) {
      app.setPlayStoreUrl(appMetadataBean.getPlayStoreUrl());
    }
    if (appMetadataBean.getAppWebSiteUrl() != null) {
      app.setAppWebsite(appMetadataBean.getAppWebSiteUrl());
    }

    if (appMetadataBean.getAppStatus() != null) {
      app.setAppStatus(appMetadataBean.getAppStatus());
    }

    if (appMetadataBean.getAndroidVersion() != null) {
      app.setAndroidAppBuildVersion(appMetadataBean.getAndroidVersion());
    }
    if (appMetadataBean.getAndroidForceUpgrade() != null) {
      app.setAndroidForceUpgrade(appMetadataBean.getAndroidForceUpgrade());
    }
    if (appMetadataBean.getIosVersion() != null) {
      app.setIosAppBuildVersion(appMetadataBean.getIosVersion());
    }
    if (appMetadataBean.getIosForceUpgrade() != null) {
      app.setIosForceUpgrade(appMetadataBean.getIosForceUpgrade());
    }

    if (appMetadataBean.getAndroidBundleId() != null) {
      app.setAndroidBundleId(appMetadataBean.getAndroidBundleId());
    }
    if (appMetadataBean.getIosBundleId() != null) {
      app.setIosBundleId(appMetadataBean.getIosBundleId());
    }
    if (appMetadataBean.getAndroidServerKey() != null) {
      app.setAndroidServerKey(appMetadataBean.getAndroidServerKey());
    }
    if (appMetadataBean.getIosServerKey() != null) {
      app.setIosServerKey(appMetadataBean.getIosServerKey());
    }

    return app;
  }

  @Override
  public AppContactEmailsResponse getAppContactEmails(String appId) {
    logger.entry("getAppContactEmails(customAppId)");
    AppContactEmailsResponse appResponse = null;
    Optional<AppEntity> optApp = appRepository.findByAppId(appId);
    if (!optApp.isPresent()) {
      throw new ErrorCodeException(
          com.google.cloud.healthcare.fdamystudies.common.ErrorCode.APP_NOT_FOUND);
    } else {
      AppEntity app = optApp.get();

      DeviceVersionBean android =
          new DeviceVersionBean(
              app.getAndroidAppBuildVersion(), String.valueOf(app.getAndroidForceUpgrade()));

      DeviceVersionBean ios =
          new DeviceVersionBean(
              app.getIosAppBuildVersion(), String.valueOf(app.getIosForceUpgrade()));

      appResponse =
          new AppContactEmailsResponse(
              MessageCode.GET_APP_SUCCESS,
              app.getContactUsToEmail(),
              app.getFromEmailId(),
              app.getAppName(),
              app.getAppTermsUrl(),
              app.getAppPrivacyUrl(),
              app.getAppWebsite(),
              app.getAppSupportEmailAddress(),
              new AppDetailsVersionBean(android, ios));
    }

    logger.exit(String.format("customAppId=%s contact details fetched successfully", appId));
    return appResponse;
  }

  @Override
  @Transactional
  public ErrorBean deactivateAppAndUsers(String customAppId, AuditLogEventRequest auditRequest) {
    logger.entry("Begin deactivateAppAndUsers()");

    Optional<AppEntity> optAppEntity = appRepository.findByAppId(customAppId);

    if (optAppEntity.isPresent()) {
      AppEntity app = optAppEntity.get();
      app.setAppStatus(UserStatus.DEACTIVATED.getDescription());
      appRepository.saveAndFlush(app);

      List<UserDetailsEntity> listOfUserDetails =
          (List<UserDetailsEntity>)
              CollectionUtils.emptyIfNull(userDetailsRepository.findByAppId(app.getId()));

      listOfUserDetails.forEach(
          userDetails -> {
            try {

              userDetails.setStatus(UserStatus.DEACTIVATE_PENDING.getValue());
              userDetailsRepository.saveAndFlush(userDetails);

              userManagementUtil.deleteUserInfoInAuthServer(userDetails.getUserId(), true);

              // change the status from DEACTIVATE_PENDING to DEACTIVATED
              userProfileManagementDao.deactivateUserAccount(userDetails.getUserId());
            } catch (ErrorCodeException e) {
              if (e.getErrorCode()
                  == com.google.cloud.healthcare.fdamystudies.common.ErrorCode.USER_NOT_FOUND) {
                userProfileManagementDao.deactivateUserAccount(userDetails.getUserId());
              } else {
                logger.warn(
                    String.format(
                        "Delete user from %s failed with ErrorCode=%s",
                        PlatformComponent.SCIM_AUTH_SERVER.getValue(), e.getErrorCode()));
              }
            } catch (Exception e) {
              logger.error("deactivateAppAndUsers() failed with an exception", e);
            }
          });
    } else {
      throw new ErrorCodeException(
          com.google.cloud.healthcare.fdamystudies.common.ErrorCode.APP_NOT_FOUND);
    }
    logger.exit("deactivateAppAndUsers() : ends");

    Map<String, String> map = Collections.singletonMap("AppID", customAppId);

    userMgmntAuditHelper.logEvent(APP_USER_ACCOUNTS_DELETED, auditRequest, map);
    return new ErrorBean(ErrorCode.EC_200.code(), ErrorCode.EC_200.errorMessage());
  }
}
