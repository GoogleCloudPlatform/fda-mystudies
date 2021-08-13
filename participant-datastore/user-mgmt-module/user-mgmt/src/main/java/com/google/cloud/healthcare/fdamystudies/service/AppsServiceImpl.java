/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.bean.AppMetadataBean;
import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.repository.AppRepository;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;
import java.util.Optional;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppsServiceImpl implements AppsService {

  private XLogger logger = XLoggerFactory.getXLogger(AppsServiceImpl.class.getName());

  @Autowired private AppRepository appRepository;

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
    app.setAppPlatform(appMetadataBean.getAppPlatform());
    app.setFeedBackToEmail(appMetadataBean.getFeedBackEmail());
    app.setContactUsToEmail(appMetadataBean.getContactEmail());
    app.setAppSupportEmailAddress(appMetadataBean.getAppSupportEmail());
    app.setFormEmailId(appMetadataBean.getFromEmail());

    app.setAppTermsUrl(appMetadataBean.getAppTermsUrl());
    app.setAppPrivacyUrl(appMetadataBean.getAppPrivacyUrl());
    app.setOrganizationName(appMetadataBean.getOraganizationName());
    app.setAppStoreUrl(appMetadataBean.getAppStoreUrl());
    app.setPlayStoreUrl(appMetadataBean.getPlayStoreUrl());

    app.setIosXCodeAppVersion(appMetadataBean.getIosXCodeAppVersion());
    app.setIosAppBuildVersion(appMetadataBean.getIosAppBuildVersion());
    app.setIosForceUpgrade(appMetadataBean.getIosForceUpgrade());
    app.setAndroidAppBuildVersion(appMetadataBean.getAndroidAppBuildVersion());
    app.setAndroidForceUpdrade(appMetadataBean.getAndroidForceUpdrade());

    return app;
  }
}
