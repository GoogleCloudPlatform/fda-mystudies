/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.bean.AppMetadataBean;
import com.google.cloud.healthcare.fdamystudies.beans.AppContactEmailsResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.common.PlatformComponent;
import com.google.cloud.healthcare.fdamystudies.common.UserStatus;
import com.google.cloud.healthcare.fdamystudies.dao.UserProfileManagementDao;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.repository.AppRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserDetailsRepository;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.util.UserManagementUtil;
import java.util.List;
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
      app.setType(appMetadataBean.getAppType());
      app.setAppPlatform(appMetadataBean.getAppPlatform());
      app.setAndroidBundleId(appMetadataBean.getAndroidBundleId());
      app.setIosBundleId(appMetadataBean.getIosBundleId());
      app.setAndroidServerKey(appMetadataBean.getAndroidServerKey());
      app.setIosBundleId(appMetadataBean.getIosBundleId());
      app = fromAppMetadataBean(appMetadataBean, app);
      app = appRepository.saveAndFlush(app);
    }
    logger.exit("saveAppMetadata() : ends");
    return new ErrorBean(ErrorCode.EC_200.code(), ErrorCode.EC_200.errorMessage());
  }

  private AppEntity fromAppMetadataBean(AppMetadataBean appMetadataBean, AppEntity app) {

    app.setAppName(appMetadataBean.getAppName());
    //  app.setAppDescription(appMetadataBean.getAppDescription());
    app.setFeedBackToEmail(appMetadataBean.getFeedBackEmail());
    app.setContactUsToEmail(appMetadataBean.getContactEmail());
    app.setAppSupportEmailAddress(appMetadataBean.getAppSupportEmail());
    app.setFormEmailId(appMetadataBean.getFromEmail());

    app.setAppTermsUrl(appMetadataBean.getAppTermsUrl());
    app.setAppPrivacyUrl(appMetadataBean.getAppPrivacyUrl());
    app.setOrganizationName(appMetadataBean.getOraganizationName());
    app.setAppStoreUrl(appMetadataBean.getAppStoreUrl());
    app.setPlayStoreUrl(appMetadataBean.getPlayStoreUrl());
    app.setAppWebsite(appMetadataBean.getAppWebsite());

    app.setIosXCodeAppVersion(appMetadataBean.getIosXCodeAppVersion());
    app.setIosAppBuildVersion(appMetadataBean.getIosAppBuildVersion());
    app.setIosForceUpgrade(appMetadataBean.getIosForceUpgrade());
    app.setAndroidAppBuildVersion(appMetadataBean.getAndroidAppBuildVersion());
    app.setAndroidForceUpdrade(appMetadataBean.getAndroidForceUpdrade());
    app.setAppStatus(appMetadataBean.getAppStatus());

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
      appResponse =
          new AppContactEmailsResponse(
              MessageCode.GET_APP_SUCCESS,
              app.getContactUsToEmail(),
              app.getFormEmailId(),
              app.getAppName());
    }

    logger.exit(String.format("customAppId=%s contact details fetched successfully", appId));
    return appResponse;
  }

  @Override
  @Transactional
  public ErrorBean deactivateAppAndUsers(String customAppId) {
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
              userManagementUtil.deleteUserInfoInAuthServer(userDetails.getUserId());
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
    return new ErrorBean(ErrorCode.EC_200.code(), ErrorCode.EC_200.errorMessage());
  }
}
