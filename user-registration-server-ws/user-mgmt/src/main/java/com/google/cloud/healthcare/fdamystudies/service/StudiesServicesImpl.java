/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.healthcare.fdamystudies.bean.StudyMetadataBean;
import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.beans.NotificationBean;
import com.google.cloud.healthcare.fdamystudies.beans.NotificationForm;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.dao.AuthInfoBODao;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.dao.StudiesDao;
import com.google.cloud.healthcare.fdamystudies.model.AppInfoDetailsBO;
import com.google.cloud.healthcare.fdamystudies.model.StudyInfoBO;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;

@Service
public class StudiesServicesImpl implements StudiesServices {

  private static Logger logger = LoggerFactory.getLogger(StudiesServicesImpl.class);

  @Autowired private StudiesDao studiesDao;

  @Autowired private AuthInfoBODao authInfoBODao;

  @Autowired private CommonDao commonDao;

  @Autowired ApplicationPropertyConfiguration applicationPropertyConfiguration;

  @Override
  public ErrorBean saveStudyMetadata(StudyMetadataBean studyMetadataBean) {
    logger.info("StudiesServicesImpl - saveStudyMetadata() : starts");
    ErrorBean errorBean = null;
    try {
      errorBean = studiesDao.saveStudyMetadata(studyMetadataBean);
    } catch (Exception e) {
      logger.error("StudiesServicesImpl - saveStudyMetadata() : error ", e);
      return new ErrorBean(ErrorCode.EC_500.code(), ErrorCode.EC_500.errorMessage());
    }
    logger.info("StudiesServicesImpl - saveStudyMetadata() : ends");
    return errorBean;
  }

  @Override
  public ErrorBean SendNotificationAction(NotificationForm notificationForm) {
    HashSet<String> studySet = new HashSet<>();
    HashSet<String> appSet = new HashSet<>();
    Map<Integer, Map<String, JSONArray>> studiesMap = null;
    Map<Object, StudyInfoBO> studyInfobyStudyCustomId = new HashMap<>();
    Map<String, JSONArray> allDeviceTokens = new HashMap<>();
    Map<Object, AppInfoDetailsBO> appInfobyAppCustomId = new HashMap<>();
    logger.info("StudiesServicesImpl.SendNotificationAction() - starts");
    try {

      for (NotificationBean notificationBean : notificationForm.getNotifications()) {
        if (notificationBean.getNotificationType().equalsIgnoreCase(AppConstants.STUDY_LEVEL)) {
          studySet.add(notificationBean.getCustomStudyId());
        }
        appSet.add(notificationBean.getAppId());
      }

      if (appSet == null && appSet.isEmpty()) {
        return new ErrorBean(ErrorCode.EC_400.code(), ErrorCode.EC_400.errorMessage());
      } else {
        List<AppInfoDetailsBO> appInfos = commonDao.getAppInfoSet(appSet);
        if (appInfos != null && !appInfos.isEmpty()) {
          allDeviceTokens = authInfoBODao.getDeviceTokenOfAllUsers(appInfos);
          appInfobyAppCustomId =
              appInfos
                  .stream()
                  .collect(Collectors.toMap(AppInfoDetailsBO::getAppId, Function.identity()));
        }

        if (studySet != null && !studySet.isEmpty()) {
          List<StudyInfoBO> studyInfos = commonDao.getStudyInfoSet(studySet);
          if (studyInfos != null && !studyInfos.isEmpty()) {
            studiesMap = commonDao.getStudyLevelDeviceToken(studyInfos);
            studyInfobyStudyCustomId =
                studyInfos
                    .stream()
                    .collect(Collectors.toMap(StudyInfoBO::getCustomId, Function.identity()));
          }
        }
        if ((allDeviceTokens != null && !allDeviceTokens.isEmpty())
            || (studiesMap != null && !studiesMap.isEmpty())) {
          for (NotificationBean notificationBean : notificationForm.getNotifications()) {
            if (notificationBean.getNotificationType().equalsIgnoreCase(AppConstants.GATEWAY_LEVEL)
                && appInfobyAppCustomId != null) {

              sendGatewaylevelNotification(allDeviceTokens, appInfobyAppCustomId, notificationBean);
            } else if (notificationBean
                    .getNotificationType()
                    .equalsIgnoreCase(AppConstants.STUDY_LEVEL)
                && studyInfobyStudyCustomId != null
                && studyInfobyStudyCustomId.get(notificationBean.getCustomStudyId()) != null
                && studiesMap != null) {

              sendStudyLevelNotification(
                  studiesMap, studyInfobyStudyCustomId, appInfobyAppCustomId, notificationBean);
            }
          }
        } else {
          return new ErrorBean(ErrorCode.EC_400.code(), ErrorCode.EC_400.errorMessage());
        }
      }
    } catch (Exception e) {
      logger.error("StudiesServicesImpl.SendNotificationAction() - error", e);
      return new ErrorBean(ErrorCode.EC_500.code(), ErrorCode.EC_500.errorMessage());
    }
    logger.info("StudiesServicesImpl.SendNotificationAction() - ends");
    return new ErrorBean(ErrorCode.EC_200.code(), ErrorCode.EC_200.errorMessage());
  }

  private void sendStudyLevelNotification(
      Map<Integer, Map<String, JSONArray>> studiesMap,
      Map<Object, StudyInfoBO> studyInfobyStudyCustomId,
      Map<Object, AppInfoDetailsBO> appInfobyAppCustomId,
      NotificationBean notificationBean) {
    Map<String, JSONArray> deviceTokensMap =
        studiesMap.get(studyInfobyStudyCustomId.get(notificationBean.getCustomStudyId()).getId());

    notificationBean.setNotificationType(AppConstants.STUDY);
    if (deviceTokensMap != null) {
      if (deviceTokensMap.get(AppConstants.DEVICE_ANDROID) != null) {
        notificationBean.setDeviceToken(deviceTokensMap.get(AppConstants.DEVICE_ANDROID));
        pushFCMNotification(
            notificationBean, appInfobyAppCustomId.get(notificationBean.getAppId()));
      }
      if (deviceTokensMap.get(AppConstants.DEVICE_IOS) != null) {
        notificationBean.setDeviceToken(deviceTokensMap.get(AppConstants.DEVICE_IOS));
        pushNotification(notificationBean, appInfobyAppCustomId.get(notificationBean.getAppId()));
      }
    }
  }

  private void sendGatewaylevelNotification(
      Map<String, JSONArray> allDeviceTokens,
      Map<Object, AppInfoDetailsBO> appInfobyAppCustomId,
      NotificationBean notificationBean) {
    notificationBean.setNotificationType(AppConstants.GATEWAY);
    if (allDeviceTokens.get(AppConstants.DEVICE_ANDROID) != null
        && allDeviceTokens.get(AppConstants.DEVICE_ANDROID).length() != 0) {
      notificationBean.setDeviceToken(allDeviceTokens.get(AppConstants.DEVICE_ANDROID));
      pushFCMNotification(notificationBean, appInfobyAppCustomId.get(notificationBean.getAppId()));
    }
    if (allDeviceTokens.get(AppConstants.DEVICE_IOS) != null) {
      notificationBean.setDeviceToken(allDeviceTokens.get(AppConstants.DEVICE_IOS));
      pushNotification(notificationBean, appInfobyAppCustomId.get(notificationBean.getAppId()));
    }
  }

  public void pushFCMNotification(
      NotificationBean notification, AppInfoDetailsBO appPropertiesDetails) {
    String authKey = "";
    logger.info("StudiesServicesImpl - pushFCMNotification() : starts");
    try {
      if (notification.getDeviceToken() != null
          && notification.getDeviceToken().length() > 0
          && appPropertiesDetails != null) {

        authKey = appPropertiesDetails.getAndroidServerKey(); // You FCM AUTH key

        URL url = new URL((String) applicationPropertyConfiguration.getApiUrlFcm());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "key=" + authKey);
        conn.setRequestProperty("Content-Type", "application/json");

        JSONObject json = new JSONObject();

        json.put("registration_ids", notification.getDeviceToken());
        json.put("priority", "high");

        JSONObject dataInfo = new JSONObject();
        dataInfo.put("subtype", notification.getNotificationSubType());
        dataInfo.put("type", notification.getNotificationType());
        dataInfo.put("title", notification.getNotificationTitle());
        dataInfo.put("message", notification.getNotificationText());
        if (notification.getCustomStudyId() != null
            && StringUtils.isNotEmpty(notification.getCustomStudyId())) {
          dataInfo.put("studyId", notification.getCustomStudyId());
        }
        json.put("data", dataInfo);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(json.toString());
        wr.flush();
        conn.getInputStream();
      }
    } catch (Exception e) {
      logger.error("StudiesServicesImpl - pushFCMNotification() : error", e);
    }
    logger.info("StudiesServicesImpl - pushFCMNotification() : ends");
  }

  public void pushNotification(
      NotificationBean notificationBean, AppInfoDetailsBO appPropertiesDetails) {
    logger.info("StudiesServicesImpl - pushNotification() : starts");
    String certificatePassword = "";
    String iosNotificationType = applicationPropertyConfiguration.getIosPushNotificationType();

    try {
      File file = null;
      if (notificationBean.getDeviceToken() != null
          && notificationBean.getDeviceToken().length() > 0
          && appPropertiesDetails != null) {
        File root = null;
        FileOutputStream fop=null;
        certificatePassword = appPropertiesDetails.getIosCertificatePassword();
        try {
          if(appPropertiesDetails.getIosCertificate()!=null) {
            byte[] decodedBytes =
              java.util.Base64.getDecoder()
                  .decode(appPropertiesDetails.getIosCertificate().replaceAll("\n", ""));
          file = File.createTempFile("pushCert_" + appPropertiesDetails.getAppId(), ".p12");
          fop = new FileOutputStream(file);
          fop.write(decodedBytes);
          fop.flush();
          fop.close();
          file.deleteOnExit();
          }
        } catch (Exception e) {
          logger.error("FdahpUserRegWSController pushNotificationCertCreation:", e);
        }finally {
          if(fop!=null) {
            fop.close();
          }
        }
        ApnsService service = null;
        if (file != null && certificatePassword!= null) {
          if (iosNotificationType.equals("production")) {
            service =
                APNS.newService()
                    .withCert(file.getPath(), certificatePassword)
                    .withProductionDestination()
                    .build();
          } else {
            service =
                APNS.newService()
                    .withCert(file.getPath(), certificatePassword)
                    .withSandboxDestination()
                    .build();
          }
          List<String> tokens = new ArrayList<String>();

          for (int i = 0; i < notificationBean.getDeviceToken().length(); i++) {
            String token = (String) notificationBean.getDeviceToken().get(i);
            tokens.add(token);
          }
          String customPayload =
              APNS.newPayload()
                  .badge(1)
                  .alertTitle("")
                  .alertBody(notificationBean.getNotificationText())
                  .customField("subtype", notificationBean.getNotificationSubType())
                  .customField("type", notificationBean.getNotificationType())
                  .customField("studyId", notificationBean.getCustomStudyId())
                  .sound("default")
                  .build();
          service.push(tokens, customPayload);
        }
      }
    } catch (Exception e) {
      logger.error("pushNotification ", e);
    }
  }
}
