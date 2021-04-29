/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package com.hphc.mystudies.integration;

import com.hphc.mystudies.bean.AppUpdatesResponse;
import com.hphc.mystudies.bean.AppVersionInfoBean;
import com.hphc.mystudies.bean.DeviceVersion;
import com.hphc.mystudies.bean.ErrorBean;
import com.hphc.mystudies.bean.ErrorResponse;
import com.hphc.mystudies.bean.NotificationsResponse;
import com.hphc.mystudies.bean.StudyUpdatesResponse;
import com.hphc.mystudies.bean.TermsPolicyResponse;
import com.hphc.mystudies.dao.AppMetaDataDao;
import com.hphc.mystudies.dto.AppVersionInfo;
import com.hphc.mystudies.exception.OrchestrationException;
import com.hphc.mystudies.util.StudyMetaDataConstants;
import com.hphc.mystudies.util.StudyMetaDataUtil;
import java.util.HashMap;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class AppMetaDataOrchestration {

  private static final XLogger LOGGER =
      XLoggerFactory.getXLogger(AppMetaDataOrchestration.class.getName());

  @SuppressWarnings("unchecked")
  HashMap<String, String> propMap = StudyMetaDataUtil.getAppProperties();

  AppMetaDataDao appMetaDataDao = new AppMetaDataDao();

  public TermsPolicyResponse termsPolicy() throws OrchestrationException {
    LOGGER.entry("begin termsPolicy()");
    TermsPolicyResponse termsPolicyResponse = new TermsPolicyResponse();
    try {
      termsPolicyResponse = appMetaDataDao.termsPolicy();
    } catch (Exception e) {
      LOGGER.error("AppMetaDataOrchestration - termsPolicy() :: ERROR", e);
    }
    LOGGER.exit("termsPolicy() :: Ends");
    return termsPolicyResponse;
  }

  public NotificationsResponse notifications(String skip, String authorization, String appId)
      throws OrchestrationException {
    LOGGER.entry("begin notifications()");
    NotificationsResponse notificationsResponse = new NotificationsResponse();
    try {
      notificationsResponse = appMetaDataDao.notifications(skip, authorization, appId);
    } catch (Exception e) {
      LOGGER.error("AppMetaDataOrchestration - notifications() :: ERROR", e);
    }
    LOGGER.exit("notifications() :: Ends");
    return notificationsResponse;
  }

  public AppUpdatesResponse appUpdates(String appVersion, String app)
      throws OrchestrationException {
    LOGGER.entry("begin appUpdates()");
    AppUpdatesResponse appUpdates = new AppUpdatesResponse();
    try {
      appUpdates = appMetaDataDao.appUpdates(appVersion, app);
    } catch (Exception e) {
      LOGGER.error("AppMetaDataOrchestration - appUpdates() :: ERROR", e);
    }
    LOGGER.exit("appUpdates() :: Ends");
    return appUpdates;
  }

  public StudyUpdatesResponse studyUpdates(String studyId, String studyVersion)
      throws OrchestrationException {
    LOGGER.entry("begin studyUpdates()");
    StudyUpdatesResponse studyUpdates = new StudyUpdatesResponse();
    try {
      studyUpdates = appMetaDataDao.studyUpdates(studyId, studyVersion);
    } catch (Exception e) {
      LOGGER.error("AppMetaDataOrchestration - studyUpdates() :: ERROR", e);
    }
    LOGGER.exit("studyUpdates() :: Ends");
    return studyUpdates;
  }

  public String updateAppVersionDetails(
      String forceUpdate,
      String osType,
      String appVersion,
      String bundleId,
      String customStudyId,
      String message)
      throws OrchestrationException {
    LOGGER.entry("begin updateAppVersionDetails()");
    String updateAppVersionResponse = "OOPS! Something went wrong.";
    try {
      updateAppVersionResponse =
          appMetaDataDao.updateAppVersionDetails(
              forceUpdate, osType, appVersion, bundleId, customStudyId, message);
    } catch (Exception e) {
      LOGGER.error("AppMetaDataOrchestration - updateAppVersionDetails() :: ERROR", e);
    }
    LOGGER.exit("updateAppVersionDetails() :: Ends");
    return updateAppVersionResponse;
  }

  public AppVersionInfoBean getAppVersionInfo(String appId) {
    LOGGER.entry("begin getAppVersionInfo()");
    AppVersionInfoBean aAppVersionInfoBean;
    AppVersionInfo appVersionInfo = null;
    DeviceVersion android = new DeviceVersion();
    DeviceVersion ios = new DeviceVersion();

    appVersionInfo = appMetaDataDao.getAppVersionInfo(appId);
    if (appVersionInfo == null) {
      LOGGER.exit("getAppVersionInfo() :: Ends");
      return null;
    }
    android.setLatestVersion(appVersionInfo.getAndroidVersion());
    if (appVersionInfo.getAndroidForceUpdate() != null && appVersionInfo.getAndroidForceUpdate()) {
      android.setForceUpdate("true");
    } else {
      android.setForceUpdate("false");
    }
    if (appVersionInfo.getIosForceUpdate() != null && appVersionInfo.getIosForceUpdate()) {
      ios.setForceUpdate("true");
    } else {
      ios.setForceUpdate("false");
    }
    ios.setLatestVersion(appVersionInfo.getIosVersion());
    aAppVersionInfoBean = new AppVersionInfoBean();
    aAppVersionInfoBean.setAndroid(android);
    aAppVersionInfoBean.setIos(ios);
    LOGGER.exit("getAppVersionInfo() :: Ends");
    return aAppVersionInfoBean;
  }

  public ErrorResponse storeResponseActivitiesTemp(String jsonData) throws Exception {
    LOGGER.entry("begin storeResponseActivitiesTemp()");
    ErrorResponse errorResponse = new ErrorResponse();
    JSONObject json = null, metadataJson = null;
    try {
      json = new JSONObject(jsonData);

      metadataJson = json.getJSONObject("metadata");

      String activityId = metadataJson.getString("activityId");
      String studyId = metadataJson.getString("studyId");
      String activityRunId = metadataJson.getString("activityRunId");
      String version = metadataJson.getString("version");
      String participantId = json.getString("participantId");

      if (StringUtils.isNotEmpty(activityId)
          && StringUtils.isNotEmpty(studyId)
          && StringUtils.isNotEmpty(activityRunId)
          && StringUtils.isNotEmpty(participantId)) {
        String jsonResponseDocName =
            StudyMetaDataUtil.saveResponsesActivityDocument(
                jsonData, activityId, studyId, activityRunId, participantId, version);
        if (StringUtils.isNotEmpty(jsonResponseDocName)) {

          ErrorBean errorBean = new ErrorBean();
          errorBean.setStatus(StudyMetaDataConstants.SUCCESS);
          errorResponse.setError(errorBean);
        }
      }

    } catch (Exception e) {
      LOGGER.error("ERROR: AppMetaDataOrchestration - storeResponseActivitiesTemp()", e);
    }
    LOGGER.exit("storeResponseActivitiesTemp() :: ends");
    return errorResponse;
  }
}
