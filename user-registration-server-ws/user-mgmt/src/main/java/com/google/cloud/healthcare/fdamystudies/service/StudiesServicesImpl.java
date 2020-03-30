/*
 *Copyright 2020 Google LLC
 *
 *Use of this source code is governed by an MIT-style license that can be found in the LICENSE file
 *or at https://opensource.org/licenses/MIT.
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

  @Autowired private ApplicationPropertyConfiguration applicationPropertyConfiguration;

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

  @SuppressWarnings("unlikely-arg-type")
  @Override
  public ErrorBean SendNotificationAction(NotificationForm notificationForm) {
    HashSet<String> studySet = new HashSet<>();
    HashSet<String> appSet = new HashSet<>();
    Map<Integer, Map<String, JSONArray>> studiesMap = null;
    ErrorBean errorBean = null;
    List<Integer> studyInfoIds = new ArrayList<Integer>();
    Map<Object, StudyInfoBO> studyInfobyStudyCustomId = new HashMap<>();
    Map<String, JSONArray> allDeviceTokens = new HashMap<>();
    List<Integer> appInfoIds = new ArrayList<>();
    Map<Object, AppInfoDetailsBO> appInfobyAppCustomId = new HashMap<>();
    logger.info("StudiesServicesImpl - SendNotificationAction() : starts");
    try {

      for (NotificationBean notificationBean : notificationForm.getNotifications()) {
        if (notificationBean.getNotificationType().equalsIgnoreCase(AppConstants.STUDY_LEVEL)) {
          studySet.add(notificationBean.getCustomStudyId());
          appSet.add(notificationBean.getAppId());
        } else {
          appSet.add(notificationBean.getAppId());
        }
      }
      if (appSet != null && !appSet.isEmpty()) {
        List<AppInfoDetailsBO> appInfos = commonDao.getAppInfoIds(appSet);
        if (appInfos != null && !appInfos.isEmpty()) {
          appInfoIds =
              appInfos.stream().map(a -> a.getAppInfoId()).distinct().collect(Collectors.toList());

          appInfobyAppCustomId =
              appInfos
                  .stream()
                  .collect(Collectors.toMap(AppInfoDetailsBO::getAppId, Function.identity()));

          if (appInfoIds != null && !appInfoIds.isEmpty()) {
            allDeviceTokens = authInfoBODao.getDeviceTokenOfAllUsers(appInfoIds);
          }
        }
        if (studySet != null
            && !studySet.isEmpty()
            && appInfoIds != null
            && !appInfoIds.isEmpty()) {

          List<StudyInfoBO> studyInfos = commonDao.getStudyInfoIds(studySet);
          if (studyInfos != null && !studyInfos.isEmpty()) {
            studyInfoIds =
                studyInfos.stream().map(a -> a.getId()).distinct().collect(Collectors.toList());

            studyInfobyStudyCustomId =
                studyInfos
                    .stream()
                    .collect(Collectors.toMap(StudyInfoBO::getCustomId, Function.identity()));

            if (studyInfoIds != null && !studyInfoIds.isEmpty()) {
              studiesMap = commonDao.getStudyLevelDeviceToken(studyInfoIds, appInfoIds);
            }
          }
        }
        if ((allDeviceTokens != null && !allDeviceTokens.isEmpty())
            || (studiesMap != null && !studiesMap.isEmpty())) {
          for (NotificationBean notificationBean : notificationForm.getNotifications()) {
            if (notificationBean
                .getNotificationType()
                .equalsIgnoreCase(AppConstants.GATEWAY_LEVEL)) {

              notificationBean.setNotificationType(AppConstants.GATEWAY);
              if (allDeviceTokens.get(AppConstants.DEVICE_ANDROID) != null
                  && allDeviceTokens.get(AppConstants.DEVICE_ANDROID).length() != 0) {

                notificationBean.setDeviceToken(allDeviceTokens.get(AppConstants.DEVICE_ANDROID));
                if (notificationBean.getDeviceToken() != null
                    && notificationBean.getDeviceToken().length() > 0
                    && appInfobyAppCustomId != null
                    && appInfobyAppCustomId.get(notificationBean.getAppId()) != null) {
                  pushFCMNotification(
                      notificationBean, appInfobyAppCustomId.get(notificationBean.getAppId()));
                }
              }
              if (allDeviceTokens.get(AppConstants.DEVICE_IOS) != null) {

                notificationBean.setDeviceToken(allDeviceTokens.get(AppConstants.DEVICE_IOS));
                if (notificationBean.getDeviceToken() != null
                    && notificationBean.getDeviceToken().length() > 0
                    && appInfobyAppCustomId != null
                    && appInfobyAppCustomId.get(notificationBean.getAppId()) != null) {
                  pushNotification(
                      notificationBean, appInfobyAppCustomId.get(notificationBean.getAppId()));
                }
              }
            } else if (notificationBean
                    .getNotificationType()
                    .equalsIgnoreCase(AppConstants.STUDY_LEVEL)
                && studyInfobyStudyCustomId != null
                && studyInfobyStudyCustomId.get(notificationBean.getCustomStudyId()) != null
                && studiesMap != null) {
              Map<String, JSONArray> deviceTokensMap =
                  studiesMap.get(
                      studyInfobyStudyCustomId.get(notificationBean.getCustomStudyId()).getId());

              notificationBean.setNotificationType(AppConstants.STUDY);
              if (deviceTokensMap != null) {
                if (deviceTokensMap.get(AppConstants.DEVICE_ANDROID) != null) {

                  notificationBean.setDeviceToken(deviceTokensMap.get(AppConstants.DEVICE_ANDROID));
                  if (notificationBean.getDeviceToken() != null
                      && notificationBean.getDeviceToken().length() > 0
                      && appInfobyAppCustomId != null
                      && appInfobyAppCustomId.get(notificationBean.getAppId()) != null) {
                    pushFCMNotification(
                        notificationBean, appInfobyAppCustomId.get(notificationBean.getAppId()));
                  }
                }
                if (deviceTokensMap.get(AppConstants.DEVICE_IOS) != null) {

                  notificationBean.setDeviceToken(deviceTokensMap.get(AppConstants.DEVICE_IOS));
                  if (notificationBean.getDeviceToken() != null
                      && notificationBean.getDeviceToken().length() > 0
                      && appInfobyAppCustomId != null
                      && appInfobyAppCustomId.get(notificationBean.getAppId()) != null) {
                    pushNotification(
                        notificationBean, appInfobyAppCustomId.get(notificationBean.getAppId()));
                  }
                }
              }
            }
          }
        }
      } else {
        errorBean = new ErrorBean(ErrorCode.EC_400.code(), ErrorCode.EC_400.errorMessage());
        return errorBean;
      }
      errorBean = new ErrorBean(ErrorCode.EC_200.code(), ErrorCode.EC_200.errorMessage());
    } catch (Exception e) {
      logger.info("StudiesServicesImpl - SendNotificationAction() : error", e);
      errorBean = new ErrorBean(ErrorCode.EC_500.code(), ErrorCode.EC_500.errorMessage());
    }
    logger.info("StudiesServicesImpl - SendNotificationAction() : ends");
    return errorBean;
  }
  /**
   * Andriod push notification
   *
   * @param notification
   */
  public void pushFCMNotification(
      NotificationBean notification, AppInfoDetailsBO appPropertiesDetails) {
    String authKey = "";
    logger.info("StudiesServicesImpl - pushFCMNotification() : starts");
    try {

      if (appPropertiesDetails != null) {
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
      logger.info("StudiesServicesImpl - pushFCMNotification() : error", e);
    }
    logger.info("StudiesServicesImpl - pushFCMNotification() : ends");
  }

  public static void pushNotification(
      NotificationBean notificationBean, AppInfoDetailsBO appPropertiesDetails) {
    logger.info("StudiesServicesImpl - pushNotification() : starts");
    String certificatePassword = "";
    try {
      File file = null;
      if (appPropertiesDetails != null) {
        File root = null;
        certificatePassword = appPropertiesDetails.getIosCertificatePassword();
        try {
          byte[] decodedBytes;
          FileOutputStream fop;
          decodedBytes =
              java.util.Base64.getDecoder()
                  .decode(appPropertiesDetails.getIosCertificate().replaceAll("\n", ""));
          file = File.createTempFile("pushCert_" + appPropertiesDetails.getAppId(), ".p12");
          System.out.println(file.getAbsolutePath());
          fop = new FileOutputStream(file);
          fop.write(decodedBytes);
          fop.flush();
          fop.close();
          file.deleteOnExit();
        } catch (Exception e) {
          logger.error("FdahpUserRegWSController pushNotificationCertCreation:", e);
        }
        ApnsService service = null;
        if (file != null) {
          service =
              APNS.newService()
                  .withCert(file.getPath(), certificatePassword)
                  .withProductionDestination()
                  .build();
          // for Production with production certificate
          /* service =
          APNS.newService()
              .withCert(file.getPath(), certificatePassword)
              .withSandboxDestination()
              .build();*/
          // for Test and UAT with dev certificate

          List<String> tokens = new ArrayList<String>();
          if (notificationBean.getDeviceToken() != null) {
            for (int i = 0; i < notificationBean.getDeviceToken().length(); i++) {
              String token = (String) notificationBean.getDeviceToken().get(i);
              tokens.add(token);
            }
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
