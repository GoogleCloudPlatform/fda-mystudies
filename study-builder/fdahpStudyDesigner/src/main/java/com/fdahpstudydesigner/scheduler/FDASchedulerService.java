/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020 Google LLC
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

import com.fdahpstudydesigner.bean.PushNotificationBean;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.dao.LoginDAO;
import com.fdahpstudydesigner.dao.NotificationDAO;
import com.fdahpstudydesigner.dao.UsersDAO;
import com.fdahpstudydesigner.service.NotificationService;
import com.fdahpstudydesigner.util.EmailNotification;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@EnableScheduling
public class FDASchedulerService {

  private static Logger logger = Logger.getLogger(FDASchedulerService.class.getName());

  private static final Map<?, ?> configMap = FdahpStudyDesignerUtil.getAppProperties();

  @Autowired private LoginDAO loginDAO;

  @Autowired private NotificationDAO notificationDAO;

  @Autowired private UsersDAO usersDAO;

  @Autowired private NotificationService notificationService;

  @Bean()
  public ThreadPoolTaskScheduler taskScheduler() {
    ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    taskScheduler.setPoolSize(2);
    return taskScheduler;
  }

  @Scheduled(cron = "0 0 0 * * ?")
  public void createAuditLogs() {
    logger.info("FDASchedulerService - createAuditLogs - Starts");
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

      List<String> emailAddresses = usersDAO.getSuperAdminList();
      String failLogBody;
      if ((emailAddresses != null) && !emailAddresses.isEmpty()) {
        Map<String, String> genarateEmailContentMap = new HashMap<>();
        String date =
            new SimpleDateFormat(FdahpStudyDesignerConstants.DB_SDF_DATE)
                .format(FdahpStudyDesignerUtil.addDaysToDate(new Date(), -1));
        if (emailAddresses.size() > 1) {
          genarateEmailContentMap.put("$firstName", "Admin");
        } else {
          UserBO userBO = loginDAO.getValidUserByEmail(emailAddresses.get(0));
          genarateEmailContentMap.put("$firstName", userBO.getFirstName());
        }
        genarateEmailContentMap.put("$startTime", date + " 00:00:00");
        genarateEmailContentMap.put("$endTime", date + " 23:59:59");
        failLogBody =
            FdahpStudyDesignerUtil.genarateEmailContent(
                (String) configMap.get("mail.audit.failure.content"), genarateEmailContentMap);
        EmailNotification.sendEmailNotificationToMany(
            "mail.audit.failure.subject", failLogBody, emailAddresses, null, null);
      }
    }
    logger.info("FDASchedulerService - createAuditLogs - Ends");
  }

  @Scheduled(cron = "0 * * * * ?")
  public void sendPushNotification() {
    logger.info("FDASchedulerService - sendPushNotification - Starts");
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
              new SimpleDateFormat(FdahpStudyDesignerConstants.UI_SDF_TIME).format(new Date()),
              FdahpStudyDesignerConstants.UI_SDF_TIME,
              1);
      pushNotificationBeans = notificationDAO.getPushNotificationList(date, time);
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

        JSONArray arrayToJson =
            new JSONArray(objectMapper.writeValueAsString(finalPushNotificationBeans));
        logger.info("FDASchedulerService - sendPushNotification " + arrayToJson);
        JSONObject json = new JSONObject();
        json.put("notifications", arrayToJson);

        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
        HttpConnectionParams.setSoTimeout(httpParams, 30000);
        HttpClient client = new DefaultHttpClient(httpParams);
        HttpPost post =
            new HttpPost(
                FdahpStudyDesignerUtil.getAppProperties().get("fda.registration.root.url")
                    + FdahpStudyDesignerUtil.getAppProperties().get("push.notification.uri"));

        post.setHeader("Content-type", "application/json");

        post.setHeader("clientId", configMap.get("security.oauth2.client.client-id").toString());
        post.setHeader(
            "secretKey",
            FdahpStudyDesignerUtil.getHashedValue(
                configMap.get("security.oauth2.client.client-secret").toString()));

        StringEntity requestEntity =
            new StringEntity(json.toString(), ContentType.APPLICATION_JSON);
        post.setEntity(requestEntity);
        client.execute(post);
      }
    } catch (Exception e) {
      logger.error("FDASchedulerService - sendPushNotification - ERROR", e.getCause());
      e.printStackTrace();
    }
    logger.info("FDASchedulerService - sendPushNotification - Ends");
  }
}
