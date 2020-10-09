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
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

  private static Logger logger = Logger.getLogger(NotificationServiceImpl.class);

  @Autowired private NotificationDAO notificationDAO;

  @Autowired private StudyDAO studyDAO;

  @Override
  public String deleteNotification(
      int notificationIdForDelete, SessionObject sessionObject, String notificationType) {
    logger.info("NotificationServiceImpl - deleteNotification - Starts");
    String message = FdahpStudyDesignerConstants.FAILURE;
    try {
      message =
          notificationDAO.deleteNotification(
              notificationIdForDelete, sessionObject, notificationType);
    } catch (Exception e) {
      logger.error("NotificationServiceImpl - deleteNotification - ERROR", e);
    }
    logger.info("NotificationServiceImpl - deleteNotification - Ends");
    return message;
  }

  @Override
  public NotificationBO getNotification(int notificationId) {
    logger.info("NotificationServiceImpl - getNotification - Starts");
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
      }
    } catch (Exception e) {
      logger.error("NotificationServiceImpl - getNotification - ERROR", e);
    }
    logger.info("NotificationServiceImpl - getNotification - Ends");
    return notificationBO;
  }

  @Override
  public List<NotificationHistoryBO> getNotificationHistoryListNoDateTime(int notificationId) {
    logger.info("NotificationServiceImpl - getNotificationHistoryListNoDateTime() - Starts");
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
            notificationHistoryBO.setNotificationSentdtTime("Last Sent on " + date + " at " + time);
          }
        }
      }
    } catch (Exception e) {
      logger.error("NotificationServiceImpl - getNotificationHistoryListNoDateTime - ERROR", e);
    }
    logger.info("NotificationServiceImpl - getNotificationHistoryListNoDateTime - Ends");
    return notificationHistoryListNoDateTime;
  }

  @Override
  public List<NotificationBO> getNotificationList(int studyId, String type) {
    logger.info("NotificationServiceImpl - getNotificationList() - Starts");
    List<NotificationBO> notificationList = null;
    try {
      notificationList = notificationDAO.getNotificationList(studyId, type);
    } catch (Exception e) {
      logger.error("NotificationServiceImpl - getNotificationList() - ERROR ", e);
    }
    logger.info("NotificationServiceImpl - getNotificationList() - Ends , e");
    return notificationList;
  }

  @Override
  public Integer saveOrUpdateOrResendNotification(
      NotificationBO notificationBO,
      String notificationType,
      String buttonType,
      SessionObject sessionObject,
      String customStudyId) {
    logger.info("NotificationServiceImpl - saveOrUpdateNotification - Starts");
    int notificationId = 0;
    try {
      if (notificationBO != null) {
        notificationId =
            notificationDAO.saveOrUpdateOrResendNotification(
                notificationBO, notificationType, buttonType, sessionObject);
        if (notificationType.equals(FdahpStudyDesignerConstants.STUDYLEVEL)
            && (notificationId != 0)) {
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
    logger.info("NotificationServiceImpl - saveOrUpdateNotification - Ends");
    return notificationId;
  }

  public void setStudyDAO(StudyDAO studyDAO) {
    this.studyDAO = studyDAO;
  }

  @Override
  public List<String> getGatwayAppList() {
    logger.info("NotificationServiceImpl - saveOrUpdateNotification - Starts");
    List<String> gatewayAppList = new ArrayList<>(new HashSet(notificationDAO.getGatwayAppList()));
    logger.info("NotificationServiceImpl - saveOrUpdateNotification - Ends");
    return gatewayAppList;
  }
}
