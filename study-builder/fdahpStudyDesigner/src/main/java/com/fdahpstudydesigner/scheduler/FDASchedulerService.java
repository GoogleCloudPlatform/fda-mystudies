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

package com.fdahpstudydesigner.scheduler;

import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NOTIFICATION_METADATA_SEND_OPERATION_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NOTIFICATION_METADATA_SENT_TO_PARTICIPANT_DATASTORE;

import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.fdahpstudydesigner.bean.PushNotificationBean;
import com.fdahpstudydesigner.bo.NotificationHistoryBO;
import com.fdahpstudydesigner.common.PlatformComponent;
import com.fdahpstudydesigner.common.StudyBuilderAuditEvent;
import com.fdahpstudydesigner.common.StudyBuilderAuditEventHelper;
import com.fdahpstudydesigner.dao.LoginDAO;
import com.fdahpstudydesigner.dao.NotificationDAO;
import com.fdahpstudydesigner.dao.UsersDAO;
import com.fdahpstudydesigner.service.NotificationService;
import com.fdahpstudydesigner.service.OAuthService;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@EnableScheduling
public class FDASchedulerService {

  private static XLogger logger = XLoggerFactory.getXLogger(FDASchedulerService.class.getName());

  private static final Map<?, ?> configMap = FdahpStudyDesignerUtil.getAppProperties();

  @Autowired private LoginDAO loginDAO;

  @Autowired private NotificationDAO notificationDAO;

  @Autowired private UsersDAO usersDAO;

  @Autowired private NotificationService notificationService;

  @Autowired private StudyBuilderAuditEventHelper auditLogEventHelper;

  @Autowired private OAuthService oauthService;

  @Bean()
  public ThreadPoolTaskScheduler taskScheduler() {
    ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    taskScheduler.setPoolSize(2);
    return taskScheduler;
  }

  HibernateTemplate hibernateTemplate;

  @Autowired
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.hibernateTemplate = new HibernateTemplate(sessionFactory);
  }

  @Scheduled(cron = "0 0 0 * * ?")
  public void createAuditLogs() {
    logger.entry("begin createAuditLogs()");
    StringBuilder logString = null;
    try {
      logString = new StringBuilder();
      if ((logString != null) && StringUtils.isNotBlank(logString.toString())) {
        String date =
            new SimpleDateFormat(FdahpStudyDesignerConstants.DB_SDF_DATE)
                .format(FdahpStudyDesignerUtil.addDaysToDate(new Date(), -1));
        File file =
            new File(
                ((String) configMap.get("fda.logFilePath")).trim()
                    + ((String) configMap.get("fda.logFileIntials")).trim()
                    + "_"
                    + date
                    + ".log");
        FileUtils.writeStringToFile(file, logString.toString());
      }
      // user last login expired locking user
      loginDAO.passwordLoginBlocked();
    } catch (Exception e) {
      logger.error("FDASchedulerService - createAuditLogs - ERROR", e);
    }
    logger.exit("createAuditLogs() - Ends");
  }

  @Scheduled(cron = "0 * * * * ?")
  public void sendPushNotification() {
    logger.entry("begin sendPushNotification()");
    List<PushNotificationBean> pushNotificationBeans;
    List<PushNotificationBean> pushNotificationBeanswithAppId =
        new ArrayList<PushNotificationBean>();
    List<PushNotificationBean> finalPushNotificationBeans = new ArrayList<PushNotificationBean>();
    String date;
    String time;
    ObjectMapper objectMapper = new ObjectMapper();
    String responseString = "";

    try {

      date = FdahpStudyDesignerUtil.getCurrentDate();
      time =
          FdahpStudyDesignerUtil.privMinDateTime(
              new SimpleDateFormat(FdahpStudyDesignerConstants.DB_SDF_TIME).format(new Date()),
              FdahpStudyDesignerConstants.DB_SDF_TIME,
              1);
      pushNotificationBeans =
          notificationDAO.getPushNotificationList(
              FdahpStudyDesignerUtil.getTimeStamp(date, time).toString());
      // pushNotificationToSaveInHistory = pushNotificationBeans;
      if ((pushNotificationBeans != null) && !pushNotificationBeans.isEmpty()) {
        for (PushNotificationBean p : pushNotificationBeans) {
          if (p.getAppId() == null) {
            List<String> appIds = notificationService.getGatwayAppList();
            if (!appIds.isEmpty()) {
              for (String appId : appIds) {
                PushNotificationBean pushBean = new PushNotificationBean();
                BeanUtils.copyProperties(pushBean, p);
                pushBean.setAppId(appId);
                pushNotificationBeanswithAppId.add(pushBean);
              }
            }
          }
        }
        if (!pushNotificationBeanswithAppId.isEmpty()) {
          for (PushNotificationBean pushBean : pushNotificationBeanswithAppId) {
            pushNotificationBeans.add(pushBean);
          }
        }

        if (!pushNotificationBeans.isEmpty()) {
          for (PushNotificationBean pushBean : pushNotificationBeans) {
            if (pushBean.getAppId() != null) {
              finalPushNotificationBeans.add(pushBean);
            }
          }
        }
        List<PushNotificationBean> pushNotification = new ArrayList<PushNotificationBean>();
        for (PushNotificationBean finalPushNotificationBean : finalPushNotificationBeans) {
          pushNotification.add(finalPushNotificationBean);

          JSONArray arrayToJson = new JSONArray(objectMapper.writeValueAsString(pushNotification));
          logger.info("FDASchedulerService - sendPushNotification " + arrayToJson);
          JSONObject json = new JSONObject();
          json.put("notifications", arrayToJson);
          logger.info("FDASchedulerService - sendPushNotification " + arrayToJson);

          HttpParams httpParams = new BasicHttpParams();
          HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
          HttpConnectionParams.setSoTimeout(httpParams, 30000);
          HttpClient client = new DefaultHttpClient(httpParams);

          HttpResponse response =
              invokePushNotificationApi(json, client, oauthService.getAccessToken());
          if (response.getStatusLine().getStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
            // Below method is called to indicate that the content of this entity is no longer
            // required.This will fix the error
            // Invalid use of BasicClientConnManager: connection still allocated.
            // Make sure to release the connection before allocating another one.
            response.getEntity().consumeContent();
            response = invokePushNotificationApi(json, client, oauthService.getNewAccessToken());
          }

          if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
            logger.error(
                String.format(
                    "Push notification API failed with status=%d",
                    response.getStatusLine().getStatusCode()));
            logSendNotificationFailedEvent(NOTIFICATION_METADATA_SEND_OPERATION_FAILED);
          } else {
            updateNotification(finalPushNotificationBean);
            logSendNotificationFailedEvent(NOTIFICATION_METADATA_SENT_TO_PARTICIPANT_DATASTORE);
          }
          pushNotification.clear();
        }
      }
    } catch (Exception e) {
      logger.error("FDASchedulerService - sendPushNotification - ERROR", e.getCause());
      e.printStackTrace();

      logSendNotificationFailedEvent(NOTIFICATION_METADATA_SEND_OPERATION_FAILED);
    }
    logger.exit("sendPushNotification() - Ends");
  }

  private void updateNotification(PushNotificationBean pushNotificationBean) {
    String sb = "";
    Session session = hibernateTemplate.getSessionFactory().openSession();
    Transaction trans = session.beginTransaction();
    if (null != pushNotificationBean) {
      if ((pushNotificationBean.getNotificationSubType() == null)
          || ((pushNotificationBean.getNotificationSubType() != null)
              && !FdahpStudyDesignerConstants.RESOURCE.equals(
                  pushNotificationBean.getNotificationSubType())
              && !FdahpStudyDesignerConstants.STUDY_EVENT.equals(
                  pushNotificationBean.getNotificationSubType()))) {
        NotificationHistoryBO historyBO = new NotificationHistoryBO();
        historyBO.setNotificationId(pushNotificationBean.getNotificationId());
        historyBO.setNotificationSentDateTime(FdahpStudyDesignerUtil.getCurrentDateTime());
        session.save(historyBO);
      }
      sb =
          "update NotificationBO NBO set NBO.notificationSent = true  where NBO.notificationId = :notificationId";
      session
          .createQuery(sb)
          .setParameter("notificationId", pushNotificationBean.getNotificationId())
          .executeUpdate();
    }
    trans.commit();
  }

  private void logSendNotificationFailedEvent(StudyBuilderAuditEvent eventEnum) {
    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setSource(PlatformComponent.STUDY_BUILDER.getValue());
    auditRequest.setDestination(PlatformComponent.PARTICIPANT_DATASTORE.getValue());
    auditRequest.setCorrelationId(UUID.randomUUID().toString());
    auditRequest.setDescription(eventEnum.getDescription());
    auditRequest.setEventCode(eventEnum.getEventCode());
    auditRequest.setOccurred(new Timestamp(Instant.now().toEpochMilli()));
    auditLogEventHelper.logEvent(eventEnum, auditRequest);
  }

  private HttpResponse invokePushNotificationApi(
      JSONObject json, HttpClient client, String accessToken)
      throws IOException, ClientProtocolException {
    HttpPost post =
        new HttpPost(
            FdahpStudyDesignerUtil.getAppProperties().get("fda.registration.root.url")
                + FdahpStudyDesignerUtil.getAppProperties().get("push.notification.uri"));
    post.setHeader("Authorization", "Bearer " + accessToken);
    post.setHeader("Content-type", "application/json");

    StringEntity requestEntity = new StringEntity(json.toString(), ContentType.APPLICATION_JSON);
    post.setEntity(requestEntity);
    return client.execute(post);
  }
}
