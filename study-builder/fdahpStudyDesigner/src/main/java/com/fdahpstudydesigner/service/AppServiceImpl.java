/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as
 * Contract no. HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.fdahpstudydesigner.service;

import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.FAILURE;

import com.fdahpstudydesigner.bean.AppDetailsBean;
import com.fdahpstudydesigner.bean.AppListBean;
import com.fdahpstudydesigner.bo.AppsBo;
import com.fdahpstudydesigner.dao.AppDAO;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.SessionObject;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppServiceImpl implements AppService {
  private static XLogger logger = XLoggerFactory.getXLogger(StudyServiceImpl.class.getName());

  @Autowired private AppDAO appDAO;

  @Override
  public List<AppListBean> getAppList(String userId) {
    logger.entry("AppServiceImpl - getAppList() - Starts");
    List<AppListBean> appBos = null;
    try {
      if (StringUtils.isNotEmpty(userId)) {
        appBos = appDAO.getAppList(userId);
      }
    } catch (Exception e) {
      logger.error("AppServiceImpl - getAppList() - ERROR ", e);
    }
    logger.exit("AppServiceImpl - getAppList() - Ends");
    return appBos;
  }

  @Override
  public AppsBo getAppById(String appId, String userId) {
    logger.entry("AppServiceImpl - getAppById() - Starts");
    AppsBo appsBo = null;
    try {
      appsBo = appDAO.getAppById(appId, userId);
    } catch (Exception e) {
      logger.error("AppServiceImpl - getAppById() - ERROR ", e);
    }
    logger.exit("AppServiceImpl - getAppById() - Ends");
    return appsBo;
  }

  @Override
  public boolean validateAppId(String appId) {
    logger.entry("AppServiceImpl - validateAppId() - Starts");
    boolean flag = false;
    try {
      if (StringUtils.isNotEmpty(appId)) {
        flag = appDAO.validateAppId(appId);
      }
    } catch (Exception e) {
      logger.error("AppServiceImpl - getAppList() - ERROR ", e);
    }
    logger.exit("AppServiceImpl - getAppList() - Ends");
    return flag;
  }

  @Override
  public String saveOrUpdateApp(AppsBo appsBo, SessionObject sessionObject) {
    logger.entry("AppServiceImpl - saveOrUpdateApp() - Starts");
    String message = FAILURE;
    try {
      message = appDAO.saveOrUpdateApp(appsBo, sessionObject);
    } catch (Exception e) {
      logger.error("AppServiceImpl - saveOrUpdateApp() - ERROR ", e);
    }
    logger.exit("AppServiceImpl - saveOrUpdateApp() - Ends");
    return message;
  }

  @Override
  public String saveOrUpdateAppSettings(AppsBo appsBo, SessionObject sessionObject) {
    logger.entry("AppServiceImpl - saveOrUpdateApp() - Starts");
    String message = FdahpStudyDesignerConstants.FAILURE;
    try {
      message = appDAO.saveOrUpdateAppSettings(appsBo, sessionObject);
    } catch (Exception e) {
      logger.error("AppServiceImpl - saveOrUpdateApp() - ERROR ", e);
    }
    logger.exit("AppServiceImpl - saveOrUpdateApp() - Ends");
    return message;
  }

  public String updateAppAction(String appId, String buttonText, SessionObject sesObj) {
    logger.entry("AppServiceImpl - updateAppAction() - Starts");
    String message = "";
    try {
      if (StringUtils.isNotEmpty(appId) && StringUtils.isNotEmpty(buttonText)) {
        message = appDAO.updateAppAction(appId, buttonText, sesObj);
      }
    } catch (Exception e) {
      logger.error("AppServiceImpl - updateAppAction() - ERROR ", e);
    }
    logger.exit("AppServiceImpl - updateAppAction() - Ends");
    return message;
  }

  @Override
  public AppDetailsBean getAppDetailsBean(String customAppId) {
    logger.entry("StudyServiceImpl - getAppDetailsBean - Starts");

    try {
      AppsBo app = appDAO.getAppByLatestVersion(customAppId);
      if (app != null) {
        AppDetailsBean appDetailsBean = new AppDetailsBean();
        appDetailsBean.setAppId(app.getCustomAppId());
        appDetailsBean.setAppName(app.getName());
        appDetailsBean.setAppType(app.getType());
        appDetailsBean.setAppPlatform(app.getAppPlatform());
        appDetailsBean.setOraganizationName(app.getOrganizationName());

        appDetailsBean.setContactEmail(app.getContactEmailAddress());
        appDetailsBean.setFeedBackEmail(app.getFeedbackEmailAddress());
        appDetailsBean.setAppSupportEmail(app.getAppSupportEmailAddress());
        appDetailsBean.setFromEmail(app.getFromEmailAddress());

        appDetailsBean.setAppTermsUrl(app.getAppTermsUrl());
        appDetailsBean.setAppPrivacyUrl(app.getAppPrivacyUrl());
        appDetailsBean.setAppStoreUrl(app.getAppStoreUrl());
        appDetailsBean.setPlayStoreUrl(app.getPlayStoreUrl());
        appDetailsBean.setAppWebSiteUrl(app.getAppWebsiteUrl());

        appDetailsBean.setAndroidBundleId(app.getAndroidBundleId());
        appDetailsBean.setAndroidServerKey(app.getAndroidServerKey());
        appDetailsBean.setAndroidForceUpdrade(app.getAndroidForceUpgrade());
        appDetailsBean.setAndroidAppBuildVersion(app.getAndroidAppBuildVersion());

        appDetailsBean.setIosBundleId(app.getIosBundleId());
        appDetailsBean.setIosServerKey(app.getIosServerKey());
        appDetailsBean.setIosForceUpgrade(app.getIosForceUpgrade());
        appDetailsBean.setIosAppBuildVersion(app.getIosAppBuildVersion());
        appDetailsBean.setIosXCodeAppVersion(app.getIosXCodeAppVersion());

        appDetailsBean.setAppStatus(app.getAppStatus());

        return appDetailsBean;
      }
    } catch (Exception e) {
      logger.error("StudyServiceImpl - getAppDetailsBean - Error", e);
    }
    logger.exit("StudyServiceImpl - getAppDetailsBean - Ends");
    return null;
  }

  @Override
  public String saveOrUpdateAppProperties(AppsBo appsBo, SessionObject sessionObject) {
    logger.entry("AppServiceImpl - saveOrUpdateAppProperties() - Starts");
    String message = FdahpStudyDesignerConstants.FAILURE;
    try {
      message = appDAO.saveOrUpdateAppProperties(appsBo, sessionObject);
    } catch (Exception e) {
      logger.error("AppServiceImpl - saveOrUpdateAppProperties() - ERROR ", e);
    }
    logger.exit("AppServiceImpl - saveOrUpdateAppProperties() - Ends");
    return message;
  }

  public boolean validateAppActions(String appId) {
    boolean markAsCompleted = false;
    try {
      markAsCompleted = appDAO.validateAppActions(appId);
    } catch (Exception e) {
      logger.error("AppServiceImpl - validateAppActions() - ERROR ", e);
    }
    return markAsCompleted;
  }

  @Override
  public String saveOrUpdateAppDeveloperConfig(AppsBo appsBo, SessionObject sessionObject) {
    logger.entry("AppServiceImpl - saveOrUpdateAppProperties() - Starts");
    String message = FdahpStudyDesignerConstants.FAILURE;
    try {
      message = appDAO.saveOrUpdateAppDeveloperConfig(appsBo, sessionObject);
    } catch (Exception e) {
      logger.error("AppServiceImpl - saveOrUpdateAppProperties() - ERROR ", e);
    }
    logger.exit("AppServiceImpl - saveOrUpdateAppProperties() - Ends");
    return message;
  }

  @Override
  public List<AppsBo> getActiveApps(String userId) {
    List<AppsBo> appBos = null;
    try {
      if (StringUtils.isNotEmpty(userId)) {
        appBos = appDAO.getActiveApps(userId);
      }
    } catch (Exception e) {
      logger.error("AppServiceImpl - getActiveApps() - ERROR ", e);
    }
    logger.exit("AppServiceImpl - getActiveApps() - Ends");
    return appBos;
  }

  @Override
  public AppsBo getAppbyCustomAppId(String customAppId) {
    logger.entry("AppServiceImpl - getAppbyCustomAppId() - Starts");
    AppsBo appsBo = null;
    try {
      appsBo = appDAO.getAppByLatestVersion(customAppId);
    } catch (Exception e) {
      logger.error("AppServiceImpl - getAppbyCustomAppId() - ERROR ", e);
    }
    logger.exit("AppServiceImpl - getAppbyCustomAppId() - Ends");
    return appsBo;
  }

  @Override
  public boolean getAppPermission(String appId, String userId) {
    logger.entry("AppServiceImpl - getAppPermission() - Starts");
    boolean permission = false;
    try {
      permission = appDAO.getAppPermission(appId, userId);
    } catch (Exception e) {
      logger.error("AppServiceImpl - getAppPermission() - ERROR ", e);
    }
    logger.exit("AppServiceImpl - getAppPermission() - Ends");
    return permission;
  }

  @Override
  public int getStudiesByAppId(String customAppId) {
    logger.entry("AppServiceImpl - getStudiesByAppId() - Starts");
    int count = 0;
    try {
      count = appDAO.getStudiesByAppId(customAppId);
    } catch (Exception e) {
      logger.error("AppServiceImpl - getStudiesByAppId() - ERROR ", e);
    }
    logger.exit("AppServiceImpl - getStudiesByAppId() - Ends");
    return count;
  }
}
