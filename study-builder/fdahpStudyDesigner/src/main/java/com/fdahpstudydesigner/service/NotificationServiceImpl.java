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

import com.fdahpstudydesigner.bo.NotificationBO;
import com.fdahpstudydesigner.bo.NotificationHistoryBO;
import com.fdahpstudydesigner.dao.NotificationDAO;
import com.fdahpstudydesigner.dao.StudyDAO;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

  private static XLogger logger =
      XLoggerFactory.getXLogger(NotificationServiceImpl.class.getName());

  @Autowired private NotificationDAO notificationDAO;

  @Autowired private StudyDAO studyDAO;

  @Override
  public String deleteNotification(
      String notificationIdForDelete, SessionObject sessionObject, String notificationType) {
    logger.entry("begin deleteNotification()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    try {
      message =
          notificationDAO.deleteNotification(
              notificationIdForDelete, sessionObject, notificationType);
    } catch (Exception e) {
      logger.error("NotificationServiceImpl - deleteNotification - ERROR", e);
    }
    logger.exit("deleteNotification() - Ends");
    return message;
  }

  @Override
  public NotificationBO getNotification(String notificationId) {
    logger.entry("begin getNotification()");
    NotificationBO notificationBO = null;
    try {
      notificationBO = notificationDAO.getNotification(notificationId);
      if (null != notificationBO) {
        notificationBO.setScheduleDate(
            FdahpStudyDesignerUtil.isNotEmpty(notificationBO.getScheduleDate())
                ? String.valueOf(
                    FdahpStudyDesignerUtil.getFormattedDate(
                        notificationBO.getScheduleDate(),
                        FdahpStudyDesignerConstants.DB_SDF_DATE,
                        FdahpStudyDesignerConstants.UI_SDF_DATE))
                : "");
        notificationBO.setScheduleTime(
            FdahpStudyDesignerUtil.isNotEmpty(notificationBO.getScheduleTime())
                ? String.valueOf(
                    FdahpStudyDesignerUtil.getFormattedDate(
                        notificationBO.getScheduleTime(),
                        FdahpStudyDesignerConstants.DB_SDF_TIME,
                        FdahpStudyDesignerConstants.SDF_TIME))
                : "");

        notificationBO.setScheduleTimestamp(
            (FdahpStudyDesignerUtil.isNotEmpty(notificationBO.getScheduleDate())
                    && FdahpStudyDesignerUtil.isNotEmpty(notificationBO.getScheduleTime()))
                ? FdahpStudyDesignerUtil.getTimeStamp(
                    notificationBO.getScheduleDate(), notificationBO.getScheduleTime())
                : null);
      }
    } catch (Exception e) {
      logger.error("NotificationServiceImpl - getNotification - ERROR", e);
    }
    logger.exit("getNotification() - Ends");
    return notificationBO;
  }

  @Override
  public List<NotificationHistoryBO> getNotificationHistoryListNoDateTime(String notificationId) {
    logger.entry("begin getNotificationHistoryListNoDateTime()");
    List<NotificationHistoryBO> notificationHistoryListNoDateTime = null;
    try {
      notificationHistoryListNoDateTime =
          notificationDAO.getNotificationHistoryListNoDateTime(notificationId);
      if ((notificationHistoryListNoDateTime != null)
          && !notificationHistoryListNoDateTime.isEmpty()) {
        for (NotificationHistoryBO notificationHistoryBO : notificationHistoryListNoDateTime) {
          if (notificationHistoryBO.getNotificationSentDateTime() != null) {
            String date =
                FdahpStudyDesignerUtil.getFormattedDate(
                    notificationHistoryBO.getNotificationSentDateTime(),
                    FdahpStudyDesignerConstants.DB_SDF_DATE_TIME,
                    FdahpStudyDesignerConstants.UI_SDF_DATE); // 8/29/2011
            String time =
                FdahpStudyDesignerUtil.getFormattedDate(
                    notificationHistoryBO.getNotificationSentDateTime(),
                    FdahpStudyDesignerConstants.DB_SDF_DATE_TIME,
                    FdahpStudyDesignerConstants.SDF_TIME); // 11:16:12
            // AM
            notificationHistoryBO.setNotificationSentdtTime(date + " at " + time);
          }
        }
      }
    } catch (Exception e) {
      logger.error("NotificationServiceImpl - getNotificationHistoryListNoDateTime - ERROR", e);
    }
    logger.exit("getNotificationHistoryListNoDateTime() - Ends");
    return notificationHistoryListNoDateTime;
  }

  @Override
  public List<NotificationBO> getNotificationList(String studyId, String type) {
    logger.entry("begin getNotificationList()");
    List<NotificationBO> notificationList = null;
    try {
      notificationList = notificationDAO.getNotificationList(studyId, type);
    } catch (Exception e) {
      logger.error("NotificationServiceImpl - getNotificationList() - ERROR ", e);
    }
    logger.exit("getNotificationList() - Ends");
    return notificationList;
  }

  @Override
  public String saveOrUpdateOrResendNotification(
      NotificationBO notificationBO,
      String notificationType,
      String buttonType,
      SessionObject sessionObject,
      String customStudyId) {
    logger.entry("begin saveOrUpdateOrResendNotification()");
    String notificationId = null;
    try {
      if (notificationBO != null) {
        notificationId =
            notificationDAO.saveOrUpdateOrResendNotification(
                notificationBO, notificationType, buttonType, sessionObject);
        if (notificationType.equals(FdahpStudyDesignerConstants.STUDYLEVEL)
            && (StringUtils.isNotEmpty(notificationId))) {
          studyDAO.markAsCompleted(
              notificationBO.getStudyId(),
              FdahpStudyDesignerConstants.NOTIFICATION,
              false,
              sessionObject,
              customStudyId);
        }
      }
    } catch (Exception e) {
      logger.error("NotificationServiceImpl - saveOrUpdateNotification - ERROR", e);
    }
    logger.exit("saveOrUpdateOrResendNotification() - Ends");
    return notificationId;
  }

  public void setStudyDAO(StudyDAO studyDAO) {
    this.studyDAO = studyDAO;
  }

  @Override
  public List<String> getGatwayAppList() {
    logger.entry("begin getGatwayAppList()");
    List<String> gatewayAppList = new ArrayList<>(new HashSet(notificationDAO.getGatwayAppList()));
    logger.exit("getGatwayAppList() - Ends");
    return gatewayAppList;
  }
}
