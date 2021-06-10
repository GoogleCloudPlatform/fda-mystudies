/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
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

package com.fdahpstudydesigner.dao;

import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_LEVEL_NOTIFICATION_CREATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_LEVEL_NOTIFICATION_REPLICATED_FOR_RESEND;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_NEW_NOTIFICATION_CREATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_NOTIFICATION_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_NOTIFICATION_REPLICATED_FOR_RESEND;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_NOTIFICATION_SAVED_OR_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderConstants.NEW_NOTIFICATION_ID;
import static com.fdahpstudydesigner.common.StudyBuilderConstants.NOTIFICATION_ID;
import static com.fdahpstudydesigner.common.StudyBuilderConstants.OLD_NOTIFICATION_ID;

import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.fdahpstudydesigner.bean.PushNotificationBean;
import com.fdahpstudydesigner.bo.NotificationBO;
import com.fdahpstudydesigner.bo.NotificationHistoryBO;
import com.fdahpstudydesigner.bo.StudyBo;
import com.fdahpstudydesigner.common.StudyBuilderAuditEvent;
import com.fdahpstudydesigner.common.StudyBuilderAuditEventHelper;
import com.fdahpstudydesigner.mapper.AuditEventMapper;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationDAOImpl implements NotificationDAO {

  private static XLogger logger = XLoggerFactory.getXLogger(NotificationDAOImpl.class.getName());

  @Autowired private StudyBuilderAuditEventHelper auditLogHelper;

  HibernateTemplate hibernateTemplate;

  private Query query = null;

  private Transaction transaction = null;

  @Autowired private HttpServletRequest request;

  @Override
  public String deleteNotification(
      String notificationIdForDelete, SessionObject sessionObject, String notificationType) {
    logger.entry("begin deleteNotification()");
    Session session = null;
    String message = FdahpStudyDesignerConstants.FAILURE;
    String queryString = "";
    int i = 0;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      if (notificationIdForDelete != null) {
        queryString =
            "update NotificationBO NBO set NBO.modifiedBy = :userId "
                + ", NBO.modifiedOn = now(), NBO.notificationStatus = 1 ,NBO.notificationDone = 1 ,NBO.notificationAction = 1 where NBO.notificationId =:notificationId";
        query =
            session
                .createQuery(queryString)
                .setParameter("userId", sessionObject.getUserId())
                .setParameter("notificationId", notificationIdForDelete);
        i = query.executeUpdate();
        if (i > 0) {
          message = FdahpStudyDesignerConstants.SUCCESS;
        }
      }

      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("NotificationDAOImpl - deleteNotification - ERROR", e);
    } finally {
      if (null != session) {
        session.close();
      }
    }
    logger.exit("deleteNotification - Ends");
    return message;
  }

  @Override
  public NotificationBO getNotification(String notificationId) {
    logger.entry("begin getNotification()");
    Session session = null;
    String queryString = null;
    NotificationBO notificationBO = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      queryString = "from NotificationBO NBO where NBO.notificationId = :notificationId";
      query = session.createQuery(queryString).setParameter("notificationId", notificationId);
      notificationBO = (NotificationBO) query.uniqueResult();
      if (null != notificationBO) {
        notificationBO.setNotificationId(
            null != notificationBO.getNotificationId() ? notificationBO.getNotificationId() : null);
        notificationBO.setNotificationText(
            null != notificationBO.getNotificationText()
                ? notificationBO.getNotificationText()
                : "");
        notificationBO.setScheduleDate(
            null != notificationBO.getScheduleDate() ? notificationBO.getScheduleDate() : "");
        notificationBO.setScheduleTime(
            null != notificationBO.getScheduleTime() ? notificationBO.getScheduleTime() : "");
        notificationBO.setScheduleTimestamp(
            (FdahpStudyDesignerUtil.isNotEmpty(notificationBO.getScheduleDate())
                    && FdahpStudyDesignerUtil.isNotEmpty(notificationBO.getScheduleTime()))
                ? FdahpStudyDesignerUtil.getTimeStamp(
                    notificationBO.getScheduleDate(), notificationBO.getScheduleTime())
                : null);

        notificationBO.setNotificationSent(notificationBO.isNotificationSent());
        notificationBO.setNotificationScheduleType(
            null != notificationBO.getNotificationScheduleType()
                ? notificationBO.getNotificationScheduleType()
                : "");
        if (FdahpStudyDesignerConstants.NOTIFICATION_IMMEDIATE.equalsIgnoreCase(
            notificationBO.getNotificationScheduleType())) {
          notificationBO.setScheduleDate("");
          notificationBO.setScheduleTime("");
          notificationBO.setScheduleTimestamp(null);
        }
      }
    } catch (Exception e) {
      logger.error("NotificationDAOImpl - getNotification - ERROR", e);
    } finally {
      if (null != session) {
        session.close();
      }
    }
    logger.exit("getNotification - Ends");
    return notificationBO;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<NotificationHistoryBO> getNotificationHistoryListNoDateTime(String notificationId) {
    logger.entry("begin getNotificationHistoryListNoDateTime()");
    Session session = null;
    String queryString = null;
    List<NotificationHistoryBO> notificationHistoryListNoDateTime = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      queryString =
          "from NotificationHistoryBO NHBO where NHBO.notificationSentDateTime <> null and NHBO.notificationId =:notificationId "
              + " order by NHBO.notificationSentDateTime desc";
      query = session.createQuery(queryString).setParameter("notificationId", notificationId);
      notificationHistoryListNoDateTime = query.list();
    } catch (Exception e) {
      logger.error("NotificationDAOImpl - getNotificationHistoryListNoDateTime - ERROR", e);
    } finally {
      if (null != session) {
        session.close();
      }
    }
    logger.exit("getNotificationHistoryListNoDateTime - Ends");
    return notificationHistoryListNoDateTime;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<NotificationBO> getNotificationList(String studyId, String type) {
    logger.entry("begin getNotificationList()");
    List<NotificationBO> notificationList = null;
    Session session = null;
    String queryString = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if (FdahpStudyDesignerConstants.STUDYLEVEL.equals(type)
          && (StringUtils.isNotEmpty(studyId))) {
        queryString =
            "from NotificationBO NBO where NBO.studyId = :studyId "
                + " and NBO.notificationSubType = 'Announcement' and NBO.notificationType = 'ST' and NBO.notificationStatus = 0 "
                + "order by NBO.notificationId desc";
        query = session.createQuery(queryString).setParameter("studyId", studyId);
        notificationList = query.list();
      } else {
        queryString =
            "from NotificationBO NBO where NBO.studyId IS NULL"
                + " and NBO.notificationType = 'GT' and NBO.notificationStatus = 0 order by NBO.notificationId desc";
        query = session.createQuery(queryString);
        notificationList = query.list();
      }
    } catch (Exception e) {
      logger.error("NotificationDAOImpl - getNotificationList() - ERROR", e);
    } finally {
      if (null != session) {
        session.close();
      }
    }
    logger.exit("getNotificationList() - Ends");
    return notificationList;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<PushNotificationBean> getPushNotificationList(String scheduledTimestamp) {
    logger.entry("begin getPushNotificationList");
    Session session = null;
    String sb = "";
    List<PushNotificationBean> pushNotificationBeans = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      sb =
          "select n.notification_id as notificationId, n.notification_text as notificationText, s.custom_study_id as customStudyId, n.notification_type as notificationType, n.notification_subType as notificationSubType,n.app_id as appId"
              + " from (notification as n) LEFT OUTER JOIN studies as s ON s.id = n.study_id where n.schedule_timestamp <=:scheduledTimestamp"
              + " AND n.is_anchor_date = false AND n.notification_done = true AND n.notification_sent=false"
              + " AND (n.notification_subType=:subType OR n.notification_type =:type OR s.status =:status)";

      query =
          session
              .createSQLQuery(sb.toString())
              .addScalar("notificationId")
              .addScalar("notificationText")
              .addScalar("customStudyId")
              .addScalar("notificationType")
              .addScalar("notificationSubType")
              .addScalar("appId")
              .setParameter("scheduledTimestamp", scheduledTimestamp)
              .setParameter("subType", FdahpStudyDesignerConstants.STUDY_EVENT)
              .setParameter("type", FdahpStudyDesignerConstants.NOTIFICATION_GT)
              .setParameter("status", FdahpStudyDesignerConstants.STUDY_ACTIVE);
      pushNotificationBeans =
          query.setResultTransformer(Transformers.aliasToBean(PushNotificationBean.class)).list();

    } catch (Exception e) {

      logger.error("NotificationDAOImpl - getPushNotificationList - ERROR", e);
    } finally {
      if (null != session) {
        session.close();
      }
    }
    logger.exit("getPushNotificationList - Ends");
    return pushNotificationBeans;
  }

  @Override
  public String saveOrUpdateOrResendNotification(
      NotificationBO notificationBO,
      String notificationType,
      String buttonType,
      SessionObject sessionObject) {
    logger.entry("begin saveOrUpdateOrResendNotification()");
    Session session = null;
    NotificationBO notificationBOUpdate = null;
    String notificationId = null;
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      if (StringUtils.isEmpty(notificationBO.getNotificationId())) {
        notificationBOUpdate = new NotificationBO();
        notificationBOUpdate.setNotificationText(notificationBO.getNotificationText().trim());
        notificationBOUpdate.setCreatedBy(notificationBO.getCreatedBy());
        notificationBOUpdate.setCreatedOn(notificationBO.getCreatedOn());
        notificationBOUpdate.setNotificationScheduleType(
            notificationBO.getNotificationScheduleType());
        if ("".equals(notificationBO.getScheduleTime())) {
          notificationBOUpdate.setScheduleTime(null);
        } else {
          notificationBOUpdate.setScheduleTime(notificationBO.getScheduleTime());
        }
        if ("".equals(notificationBO.getScheduleDate())) {
          notificationBOUpdate.setScheduleDate(null);
        } else {
          notificationBOUpdate.setScheduleDate(notificationBO.getScheduleDate());
        }

        notificationBOUpdate.setScheduleTimestamp(
            (FdahpStudyDesignerUtil.isNotEmpty(notificationBO.getScheduleDate())
                    && FdahpStudyDesignerUtil.isNotEmpty(notificationBO.getScheduleTime()))
                ? FdahpStudyDesignerUtil.getTimeStamp(
                    notificationBO.getScheduleDate(), notificationBO.getScheduleTime())
                : null);

        if (notificationType.equals(FdahpStudyDesignerConstants.STUDYLEVEL)) {
          notificationBOUpdate.setNotificationDone(notificationBO.isNotificationDone());
          notificationBOUpdate.setNotificationType(FdahpStudyDesignerConstants.NOTIFICATION_ST);
          notificationBOUpdate.setCustomStudyId(notificationBO.getCustomStudyId());
          notificationBOUpdate.setStudyId(notificationBO.getStudyId());
          notificationBOUpdate.setNotificationAction(notificationBO.isNotificationAction());
        } else {
          notificationBOUpdate.setNotificationType(FdahpStudyDesignerConstants.NOTIFICATION_GT);
          notificationBOUpdate.setStudyId(null);
          notificationBOUpdate.setCustomStudyId("");
          notificationBOUpdate.setNotificationAction(false);
          notificationBOUpdate.setNotificationDone(true);
        }
        if (StringUtils.isNotEmpty(notificationBO.getAppId())) {
          notificationBOUpdate.setAppId(notificationBO.getAppId());
        }
        notificationBOUpdate.setNotificationSubType(
            FdahpStudyDesignerConstants.NOTIFICATION_SUBTYPE_ANNOUNCEMENT);
        notificationId = (String) session.save(notificationBOUpdate);
      } else {
        query =
            session
                .createQuery(" from NotificationBO NBO where NBO.notificationId =:notificationId ")
                .setParameter("notificationId", notificationBO.getNotificationId());
        notificationBOUpdate = (NotificationBO) query.uniqueResult();

        if (StringUtils.isNotBlank(notificationBO.getNotificationText())) {
          notificationBOUpdate.setNotificationText(notificationBO.getNotificationText().trim());
        } else {
          notificationBOUpdate.setNotificationText(
              notificationBOUpdate.getNotificationText().trim());
        }
        notificationBOUpdate.setModifiedBy(notificationBO.getModifiedBy());
        notificationBOUpdate.setModifiedOn(notificationBO.getModifiedOn());
        notificationBOUpdate.setCustomStudyId(notificationBO.getCustomStudyId());
        notificationBOUpdate.setStudyId(notificationBOUpdate.getStudyId());
        notificationBOUpdate.setNotificationSent(notificationBO.isNotificationSent());
        notificationBOUpdate.setNotificationScheduleType(
            notificationBO.getNotificationScheduleType());
        if (FdahpStudyDesignerUtil.isNotEmpty(notificationBO.getScheduleTime())) {
          notificationBOUpdate.setScheduleTime(notificationBO.getScheduleTime());
        } else {
          notificationBOUpdate.setScheduleTime(null);
        }
        if (FdahpStudyDesignerUtil.isNotEmpty(notificationBO.getScheduleDate())) {
          notificationBOUpdate.setScheduleDate(notificationBO.getScheduleDate());
        } else {
          notificationBOUpdate.setScheduleDate(null);
        }
        notificationBOUpdate.setScheduleTimestamp(
            (FdahpStudyDesignerUtil.isNotEmpty(notificationBO.getScheduleDate())
                    && FdahpStudyDesignerUtil.isNotEmpty(notificationBO.getScheduleTime()))
                ? FdahpStudyDesignerUtil.getTimeStamp(
                    notificationBO.getScheduleDate(), notificationBO.getScheduleTime())
                : null);
        if (notificationType.equals(FdahpStudyDesignerConstants.STUDYLEVEL)) {
          notificationBOUpdate.setNotificationDone(notificationBO.isNotificationDone());
          notificationBOUpdate.setNotificationType(FdahpStudyDesignerConstants.NOTIFICATION_ST);
          notificationBOUpdate.setNotificationAction(notificationBO.isNotificationAction());
        } else {
          notificationBOUpdate.setNotificationDone(notificationBOUpdate.isNotificationDone());
          notificationBOUpdate.setNotificationType(FdahpStudyDesignerConstants.NOTIFICATION_GT);
          notificationBOUpdate.setNotificationAction(notificationBOUpdate.isNotificationAction());
        }
        notificationBOUpdate.setNotificationSubType(
            FdahpStudyDesignerConstants.NOTIFICATION_SUBTYPE_ANNOUNCEMENT);
        if (StringUtils.isNotEmpty(notificationBO.getAppId())) {
          notificationBOUpdate.setAppId(notificationBO.getAppId());
        }
        session.update(notificationBOUpdate);
        notificationId = notificationBOUpdate.getNotificationId();
        session.flush();
      }
      // Audit log capturing for specified request
      if (notificationId != null) {
        StudyBuilderAuditEvent auditLogEvent = null;
        Map<String, String> values = new HashMap<>();
        values.put(NOTIFICATION_ID, String.valueOf(notificationId));
        values.put(OLD_NOTIFICATION_ID, String.valueOf(notificationBO.getNotificationId()));
        values.put(NEW_NOTIFICATION_ID, String.valueOf(notificationId));
        if (notificationType.equals(FdahpStudyDesignerConstants.STUDYLEVEL)) {
          auditRequest.setStudyId(notificationBO.getCustomStudyId());
          query =
              session
                  .getNamedQuery("StudyBo.getStudyBycustomStudyId")
                  .setString("customStudyId", notificationBO.getCustomStudyId());
          query.setMaxResults(1);
          StudyBo study = (StudyBo) query.uniqueResult();
          auditRequest.setStudyVersion(study.getVersion().toString());
          auditRequest.setAppId(study.getAppId());
          auditLogHelper.logEvent(STUDY_NEW_NOTIFICATION_CREATED, auditRequest, values);
        }
        if ("add".equals(buttonType)) {
          auditLogEvent = APP_LEVEL_NOTIFICATION_CREATED;
        } else if ("update".equals(buttonType)) {
          auditLogEvent = APP_LEVEL_NOTIFICATION_CREATED;
        } else if ("resend".equals(buttonType)
            && !notificationType.equals(FdahpStudyDesignerConstants.STUDYLEVEL)) {
          auditLogEvent = APP_LEVEL_NOTIFICATION_REPLICATED_FOR_RESEND;
        } else if ("resend".equals(buttonType)
            && notificationType.equals(FdahpStudyDesignerConstants.STUDYLEVEL)) {
          auditLogEvent = STUDY_NOTIFICATION_REPLICATED_FOR_RESEND;
        } else if ("save".equals(buttonType)
            && FdahpStudyDesignerConstants.STUDYLEVEL.equals(notificationType)) {
          values.put(NOTIFICATION_ID, String.valueOf(notificationId));
          auditLogEvent = STUDY_NOTIFICATION_SAVED_OR_UPDATED;
        } else if ("done".equals(buttonType)
            && FdahpStudyDesignerConstants.STUDYLEVEL.equals(notificationType)) {
          values.put(NOTIFICATION_ID, String.valueOf(notificationId));
          auditLogEvent = STUDY_NOTIFICATION_MARKED_COMPLETE;
        }
        auditLogHelper.logEvent(auditLogEvent, auditRequest, values);
      }
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("NotificationDAOImpl - saveOrUpdateOrResendNotification() - ERROR", e);
    } finally {
      if (null != session) {
        session.close();
      }
    }
    logger.exit("saveOrUpdateOrResendNotification - Ends");
    return notificationId;
  }

  @Autowired
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.hibernateTemplate = new HibernateTemplate(sessionFactory);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<String> getGatwayAppList() {
    logger.entry("begin getGatwayAppList()");
    List<String> gatewayAppList = null;
    Session session = null;
    String queryString = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      queryString =
          "select s.app_id from studies s where"
              + " s.type='GT'"
              + " and s.version=1"
              + " and s.app_id IS NOT NULL;";
      query = session.createSQLQuery(queryString);
      gatewayAppList = query.list();
    } catch (Exception e) {
      logger.error("NotificationDAOImpl - getGatwayAppList() - ERROR", e);
    } finally {
      if (null != session) {
        session.close();
      }
    }
    logger.exit("getGatwayAppList - Ends");
    return gatewayAppList;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<NotificationBO> getNotificationList(String studyId) {
    logger.entry("begin getNotificationList()");
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      return session.getNamedQuery("getNotification").setString("studyId", studyId).list();
    } catch (Exception e) {
      logger.error("NotificationDAOImpl - getNotificationList() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getNotificationList() - Ends");
    return null;
  }

  @Override
  public void saveNotification(NotificationBO notificationBO) {
    logger.info("NotificationDAOImpl - saveNotification() - Starts");
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      session.save(notificationBO);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      logger.error("NotificationDAOImpl - saveNotification()  - ERROR ", e);
    } finally {
      if ((session != null) && session.isOpen()) {
        session.close();
      }
    }
    logger.info("NotificationDAOImpl - saveNotification()  - Ends");
  }
}
