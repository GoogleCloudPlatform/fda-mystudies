/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.PUSH_NOTIFICATION_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.PUSH_NOTIFICATION_SENT;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.PushNotificationResponse;
import com.eatthepath.pushy.apns.util.SimpleApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.concurrent.PushNotificationFuture;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.healthcare.fdamystudies.bean.StudyMetadataBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.beans.FcmPushNotificationResponse;
import com.google.cloud.healthcare.fdamystudies.beans.NotificationBean;
import com.google.cloud.healthcare.fdamystudies.beans.NotificationForm;
import com.google.cloud.healthcare.fdamystudies.common.UserMgmntAuditHelper;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.dao.AuthInfoBODao;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.dao.StudiesDao;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudiesServicesImpl implements StudiesServices {

  private XLogger logger = XLoggerFactory.getXLogger(StudiesServicesImpl.class.getName());

  @Autowired private StudiesDao studiesDao;

  @Autowired private AuthInfoBODao authInfoBoDao;

  @Autowired private CommonDao commonDao;

  @Autowired ApplicationPropertyConfiguration applicationPropertyConfiguration;

  @Autowired UserMgmntAuditHelper userMgmntAuditLogHelper;

  @Override
  @Transactional()
  public ErrorBean saveStudyMetadata(StudyMetadataBean studyMetadataBean) {
    logger.entry("Begin saveStudyMetadata()");
    ErrorBean errorBean = null;

    errorBean = studiesDao.saveStudyMetadata(studyMetadataBean);

    logger.exit("saveStudyMetadata() : ends");
    return errorBean;
  }

  @Override
  @Transactional()
  public ErrorBean SendNotificationAction(
      NotificationForm notificationForm, AuditLogEventRequest auditRequest) throws IOException {
    HashSet<String> studySet = new HashSet<>();
    HashSet<String> appSet = new HashSet<>();
    Map<String, Map<String, JSONArray>> studiesMap = null;
    Map<Object, StudyEntity> studyInfobyStudyCustomId = new HashMap<>();
    Map<String, JSONArray> allDeviceTokens = new HashMap<>();
    Map<Object, AppEntity> appInfobyAppCustomId = new HashMap<>();
    logger.entry("Begin SendNotificationAction()");

    for (NotificationBean notificationBean : notificationForm.getNotifications()) {
      if (notificationBean.getNotificationType().equalsIgnoreCase(AppConstants.STUDY_LEVEL)) {
        studySet.add(notificationBean.getCustomStudyId());
      }
      appSet.add(notificationBean.getAppId());
    }

    if (appSet == null && appSet.isEmpty()) {
      logger.debug("appset is empty return bad request");
      return new ErrorBean(ErrorCode.EC_400.code(), ErrorCode.EC_400.errorMessage());
    } else {
      List<AppEntity> appInfos = commonDao.getAppInfoSet(appSet);
      logger.debug(String.format("hasAppInfos=%b", (appInfos != null && !appInfos.isEmpty())));
      if (appInfos != null && !appInfos.isEmpty()) {
        allDeviceTokens = authInfoBoDao.getDeviceTokenOfAllUsers(appInfos);
        appInfobyAppCustomId =
            appInfos.stream().collect(Collectors.toMap(AppEntity::getAppId, Function.identity()));
      }
      logger.debug(String.format("hasStudiesSet=%b", (studySet != null && !studySet.isEmpty())));
      if (studySet != null && !studySet.isEmpty()) {
        List<StudyEntity> studyInfos = commonDao.getStudyInfoSet(studySet);
        if (studyInfos != null && !studyInfos.isEmpty()) {
          studiesMap = commonDao.getStudyLevelDeviceToken(studyInfos);
          studyInfobyStudyCustomId =
              studyInfos
                  .stream()
                  .collect(Collectors.toMap(StudyEntity::getCustomId, Function.identity()));
        }
      }
      FcmPushNotificationResponse fcmNotificationResponse = null;
      if ((allDeviceTokens != null && !allDeviceTokens.isEmpty())
          || (studiesMap != null && !studiesMap.isEmpty())) {
        for (NotificationBean notificationBean : notificationForm.getNotifications()) {
          auditRequest.setStudyId(notificationBean.getStudyId());
          auditRequest.setAppId(notificationBean.getAppId());

          if (notificationBean.getNotificationType().equalsIgnoreCase(AppConstants.GATEWAY_LEVEL)
              && appInfobyAppCustomId != null) {

            fcmNotificationResponse =
                sendGatewaylevelNotification(
                    allDeviceTokens, appInfobyAppCustomId, notificationBean);

            auditRequest.setStudyId(notificationBean.getStudyId());

            if (fcmNotificationResponse.getStatus() == HttpStatus.OK.value()) {
              userMgmntAuditLogHelper.logEvent(PUSH_NOTIFICATION_SENT, auditRequest);
            } else {
              userMgmntAuditLogHelper.logEvent(PUSH_NOTIFICATION_FAILED, auditRequest);
            }

            logger.debug(
                String.format(
                    "status=%d and fcmNotificationResponse=%s",
                    fcmNotificationResponse.getStatus(), fcmNotificationResponse.getFcmResponse()));
            return new ErrorBean(
                ErrorCode.EC_200.code(),
                ErrorCode.EC_200.errorMessage(),
                fcmNotificationResponse.getFcmResponse());

          } else if (notificationBean
                  .getNotificationType()
                  .equalsIgnoreCase(AppConstants.STUDY_LEVEL)
              && studyInfobyStudyCustomId != null
              && studyInfobyStudyCustomId.get(notificationBean.getCustomStudyId()) != null
              && studiesMap != null) {
            logger.info("StudiesServicesImpl.SendNotificationAction() " + AppConstants.STUDY_LEVEL);
            fcmNotificationResponse =
                sendStudyLevelNotification(
                    studiesMap, studyInfobyStudyCustomId, appInfobyAppCustomId, notificationBean);

            auditRequest.setStudyId(notificationBean.getStudyId());

            if (fcmNotificationResponse.getStatus() == HttpStatus.OK.value()) {
              userMgmntAuditLogHelper.logEvent(PUSH_NOTIFICATION_SENT, auditRequest);
            } else {
              userMgmntAuditLogHelper.logEvent(PUSH_NOTIFICATION_FAILED, auditRequest);
            }

            logger.debug(
                String.format(
                    "status=%d and fcmNotificationResponse=%s",
                    fcmNotificationResponse.getStatus(), fcmNotificationResponse.getFcmResponse()));
            return new ErrorBean(
                ErrorCode.EC_200.code(),
                ErrorCode.EC_200.errorMessage(),
                fcmNotificationResponse.getFcmResponse());
          }
        }
      } else {
        logger.debug(
            String.format(
                "hasDeviceTokens=%b and hasElementsInStudiesMap=%b",
                (allDeviceTokens != null && !allDeviceTokens.isEmpty()),
                (studiesMap != null && !studiesMap.isEmpty())));
        return new ErrorBean(ErrorCode.EC_400.code(), ErrorCode.EC_400.errorMessage());
      }
    }

    logger.exit("SendNotificationAction() - ends");
    return new ErrorBean(ErrorCode.EC_200.code(), ErrorCode.EC_200.errorMessage());
  }

  private FcmPushNotificationResponse sendStudyLevelNotification(
      Map<String, Map<String, JSONArray>> studiesMap,
      Map<Object, StudyEntity> studyInfobyStudyCustomId,
      Map<Object, AppEntity> appInfobyAppCustomId,
      NotificationBean notificationBean)
      throws IOException {

    Map<String, JSONArray> deviceTokensMap =
        studiesMap.get(studyInfobyStudyCustomId.get(notificationBean.getCustomStudyId()).getId());
    notificationBean.setNotificationType(AppConstants.STUDY);
    FcmPushNotificationResponse pushNotificationResponse = null;
    if (deviceTokensMap != null) {
      if (deviceTokensMap.get(AppConstants.DEVICE_ANDROID) != null) {
        notificationBean.setDeviceToken(deviceTokensMap.get(AppConstants.DEVICE_ANDROID));
        pushNotificationResponse =
            pushFcmNotification(
                notificationBean, appInfobyAppCustomId.get(notificationBean.getAppId()));
      }
      if (deviceTokensMap.get(AppConstants.DEVICE_IOS) != null) {
        notificationBean.setDeviceToken(deviceTokensMap.get(AppConstants.DEVICE_IOS));
        pushNotification(notificationBean, appInfobyAppCustomId.get(notificationBean.getAppId()));
      }
    }
    JsonNode fcmResponse =
        pushNotificationResponse != null ? pushNotificationResponse.getFcmResponse() : null;
    return new FcmPushNotificationResponse(fcmResponse, HttpStatus.OK.value(), "success");
  }

  private FcmPushNotificationResponse sendGatewaylevelNotification(
      Map<String, JSONArray> allDeviceTokens,
      Map<Object, AppEntity> appInfobyAppCustomId,
      NotificationBean notificationBean)
      throws IOException {

    notificationBean.setNotificationType(AppConstants.GATEWAY);
    FcmPushNotificationResponse pushNotificationResponse = null;
    if (allDeviceTokens.get(AppConstants.DEVICE_ANDROID) != null
        && allDeviceTokens.get(AppConstants.DEVICE_ANDROID).length() != 0) {
      notificationBean.setDeviceToken(allDeviceTokens.get(AppConstants.DEVICE_ANDROID));
      pushNotificationResponse =
          pushFcmNotification(
              notificationBean, appInfobyAppCustomId.get(notificationBean.getAppId()));
    }
    if (allDeviceTokens.get(AppConstants.DEVICE_IOS) != null) {
      notificationBean.setDeviceToken(allDeviceTokens.get(AppConstants.DEVICE_IOS));
      pushNotification(notificationBean, appInfobyAppCustomId.get(notificationBean.getAppId()));
    }
    JsonNode fcmResponse =
        pushNotificationResponse != null ? pushNotificationResponse.getFcmResponse() : null;
    return new FcmPushNotificationResponse(fcmResponse, HttpStatus.OK.value(), "success");
  }

  public FcmPushNotificationResponse pushFcmNotification(
      NotificationBean notification, AppEntity appPropertiesDetails) throws IOException {

    String authKey = "";
    logger.entry("Begin pushFCMNotification()");

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
      String response = IOUtils.toString(conn.getInputStream(), StandardCharsets.UTF_8);
      JsonNode responseJson = new ObjectMapper().readTree(response);
      FcmPushNotificationResponse fcmNotificationResponse =
          new FcmPushNotificationResponse(
              responseJson, conn.getResponseCode(), conn.getResponseMessage());
      logger.trace(
          String.format(
              "FCM Notification Response status=%d, response=%s",
              conn.getResponseCode(), response));
      return fcmNotificationResponse;
    }

    return new FcmPushNotificationResponse(null, HttpStatus.OK.value(), "SUCCESS");
  }

  public void pushNotification(NotificationBean notificationBean, AppEntity appPropertiesDetails)
      throws IOException {

    logger.entry("Begin pushNotification()");
    String certificatePassword = "";

    File file = null;
    if (notificationBean.getDeviceToken() != null
        && notificationBean.getDeviceToken().length() > 0
        && appPropertiesDetails != null) {
      certificatePassword = appPropertiesDetails.getIosCertificatePassword();

      byte[] decodedBytes;
      FileOutputStream fop;
      decodedBytes =
          java.util.Base64.getDecoder()
              .decode(appPropertiesDetails.getIosCertificate().replaceAll("\n", ""));
      file = File.createTempFile("pushCert_" + appPropertiesDetails.getAppId(), ".p12");
      fop = new FileOutputStream(file);
      fop.write(decodedBytes);
      fop.flush();
      fop.close();
      file.deleteOnExit();

      if (file != null) {
        ApnsClient apnsClient =
            new ApnsClientBuilder()
                .setApnsServer(applicationPropertyConfiguration.getIosPushNotificationType())
                .setClientCredentials(new File(file.getPath()), certificatePassword)
                .build();

        String payload =
            new SimpleApnsPayloadBuilder()
                .setAlertBody(notificationBean.getNotificationText())
                .addCustomProperty("subtype", notificationBean.getNotificationSubType())
                .addCustomProperty("type", notificationBean.getNotificationType())
                .addCustomProperty("studyId", notificationBean.getCustomStudyId())
                .setSound("default")
                .build();

        for (int i = 0; i < notificationBean.getDeviceToken().length(); i++) {
          String token = (String) notificationBean.getDeviceToken().get(i);
          SimpleApnsPushNotification pushNotification =
              new SimpleApnsPushNotification(token, appPropertiesDetails.getIosBundleId(), payload);
          try {
            final PushNotificationFuture<
                    SimpleApnsPushNotification,
                    PushNotificationResponse<SimpleApnsPushNotification>>
                result = apnsClient.sendNotification(pushNotification);

            // getting the response from APNs
            final PushNotificationResponse<SimpleApnsPushNotification> pushNotificationResponse =
                result.get();
            if (pushNotificationResponse.isAccepted()) {
              logger.info("Push notification accepted by APNs gateway.");
            } else {
              logger.info(
                  "Notification rejected by the APNs gateway: "
                      + pushNotificationResponse.getRejectionReason());
              if (pushNotificationResponse.getTokenInvalidationTimestamp() != null) {
                logger.info(
                    "\t…and the token is invalid as of "
                        + pushNotificationResponse.getTokenInvalidationTimestamp());
              }
            }
          } catch (final Exception e) {
            logger.error("Failed to send push notification.");
            e.printStackTrace();
          }
        }
      }
    }
  }
}
