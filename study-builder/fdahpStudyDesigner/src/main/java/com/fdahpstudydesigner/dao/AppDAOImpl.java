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

import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.ACTION_UPDATES;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.COMPLETED_BUTTON;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.FAILURE;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.IMP_VALUE;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.SAVE_BUTTON;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.SUCCESS;

import com.fdahpstudydesigner.bean.AppListBean;
import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.fdahpstudydesigner.bo.AppPermissionBO;
import com.fdahpstudydesigner.bo.AppSequenceBo;
import com.fdahpstudydesigner.bo.AppsBo;
import com.fdahpstudydesigner.bo.StudyBo;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.common.StudyBuilderAuditEvent;
import com.fdahpstudydesigner.common.StudyBuilderAuditEventHelper;
import com.fdahpstudydesigner.mapper.AuditEventMapper;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;
import java.math.BigInteger;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AppDAOImpl implements AppDAO {

  private static XLogger logger = XLoggerFactory.getXLogger(AppDAOImpl.class.getName());

  HibernateTemplate hibernateTemplate;
  private Query query = null;
  String queryString = "";
  private Transaction transaction = null;
  @Autowired private StudyBuilderAuditEventHelper auditLogEventHelper;

  public AppDAOImpl() {
    // Unused
  }

  @Autowired
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.hibernateTemplate = new HibernateTemplate(sessionFactory);
  }

  @Autowired private HttpServletRequest request;

  @Override
  public List<AppListBean> getAppList(String userId) {
    logger.entry("begin getAppList()");
    Session session = null;
    List<AppListBean> appListBean = null;
    AppsBo liveApp = null;
    AppsBo appBo = null;
    StudyBo studyBo = null;
    BigInteger studyCount;
    try {

      session = hibernateTemplate.getSessionFactory().openSession();

      if (StringUtils.isNotEmpty(userId)) {

        query = session.getNamedQuery("getUserById").setString("userId", userId);
        UserBO userBO = (UserBO) query.uniqueResult();

        if (userBO.getRoleId().equals("1")) {
          query =
              session.createQuery(
                  "select new com.fdahpstudydesigner.bean.AppListBean(a.id,a.customAppId,a.name,a.appStatus,a.type,a.createdOn)"
                      + " from AppsBo a, UserBO user"
                      + " where user.userId = a.createdBy"
                      + " and a.version=0"
                      + " order by a.createdOn desc");

        } else {
          query =
              session.createQuery(
                  "select new com.fdahpstudydesigner.bean.AppListBean(a.id,a.customAppId,a.name,a.appStatus,a.type,a.createdOn,ap.viewPermission)"
                      + " from AppsBo a,AppPermissionBO ap, UserBO user"
                      + " where a.id=ap.appId"
                      + " and user.userId = a.createdBy"
                      + " and a.version=0"
                      + " and ap.userId=:impValue"
                      + " order by a.createdOn desc");
          query.setString(IMP_VALUE, userId);
        }
        appListBean = query.list();
        if ((appListBean != null) && !appListBean.isEmpty()) {
          for (AppListBean appDetails : appListBean) {

            if (StringUtils.isNotEmpty(appDetails.getCustomAppId())) {
              liveApp =
                  (AppsBo)
                      session
                          .createQuery("from AppsBo where customAppId=:customAppId and live=1")
                          .setParameter("customAppId", appDetails.getCustomAppId())
                          .uniqueResult();
              if (liveApp != null) {
                appDetails.setLiveAppId(liveApp.getId());
              } else {
                appDetails.setLiveAppId(null);
              }
            }

            // for draft app
            if ((appDetails.getId() != null) && (appDetails.getLiveAppId() != null)) {
              appBo =
                  (AppsBo)
                      session
                          .createQuery("from AppsBo where id=:id")
                          .setParameter("id", appDetails.getId())
                          .uniqueResult();
              if (appBo.getHasAppDraft() == 1) {
                appDetails.setFlag(true);
              }
            }
            if (userBO.getRoleId().equals("1")) {
              studyCount =
                  (BigInteger)
                      session
                          .createSQLQuery(
                              "select count(*) from studies"
                                  + " WHERE app_id=:customAppId AND is_live=0")
                          .setString("customAppId", appDetails.getCustomAppId())
                          .uniqueResult();
              appDetails.setStudiesCount(studyCount);
              appDetails.setViewPermission(true);
            } else {
              studyCount =
                  (BigInteger)
                      session
                          .createSQLQuery(
                              "select count(*) "
                                  + "from studies s,study_permission p, users user "
                                  + "where s.id=p.study_id "
                                  + "and user.user_id = s.created_by "
                                  + "and s.app_id=:customAppId "
                                  + "and p.user_id=:impValue "
                                  + "and s.is_live=0")
                          .setString("customAppId", appDetails.getCustomAppId())
                          .setString(IMP_VALUE, userId)
                          .uniqueResult();
              appDetails.setStudiesCount(studyCount);
            }
          }
        }
      }

    } catch (Exception e) {
      logger.error("AppDAOImpl - getAppList() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getAppList() - Ends");
    return appListBean;
  }

  @Override
  public AppsBo getAppById(String appId, String userId) {
    logger.entry("begin getAppById()");
    Session session = null;
    AppsBo appsBo = null;
    AppsBo liveAppsBo = null;
    AppSequenceBo appSequenceBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if (StringUtils.isNotEmpty(appId)) {
        appsBo =
            (AppsBo)
                session.getNamedQuery("AppsBo.getAppsById").setString("id", appId).uniqueResult();
        if (appsBo != null) {
          // To get the live version of app by passing customAppId
          liveAppsBo =
              (AppsBo)
                  session
                      .createQuery("FROM AppsBo where customAppId=:customAppId and live=1")
                      .setParameter("customAppId", appsBo.getCustomAppId())
                      .uniqueResult();
          if (liveAppsBo != null) {
            appsBo.setLiveAppsBo(liveAppsBo);
          }

          appSequenceBo =
              (AppSequenceBo)
                  session
                      .getNamedQuery("getAppSequenceByAppId")
                      .setString("appId", appId)
                      .uniqueResult();

          if (appSequenceBo != null) {
            appsBo.setAppSequenceBo(appSequenceBo);
          }
        }
      }
    } catch (Exception e) {
      logger.error("AppDAOImpl - getAppById() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getAppById() - Ends");
    return appsBo;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean validateAppId(String appId) {
    logger.entry("begin validateAppId()");
    boolean flag = false;
    Session session = null;
    List<AppsBo> appsBos = null;
    String searchQuery = "";
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if (!appId.isEmpty()) {
        searchQuery = " From AppsBo WHERE customAppId=:appId";
        appsBos = session.createQuery(searchQuery).setString("appId", appId).list();
      }

      if ((appsBos != null) && !appsBos.isEmpty()) {
        flag = true;
      }

    } catch (Exception e) {
      logger.error("AppDAOImpl - validateAppId() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    return flag;
  }

  @Override
  public String saveOrUpdateApp(AppsBo appBo, SessionObject sessionObject) {
    logger.entry("begin saveOrUpdateApp()");
    Session session = null;
    String message = SUCCESS;
    StudyBuilderAuditEvent auditLogEvent = null;
    AppSequenceBo appSequenceBo = null;
    String appId = null;
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();

      if (StringUtils.isEmpty(appBo.getId())) {
        appBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
        appSequenceBo = new AppSequenceBo();
        appBo.setAppStatus("Draft");
        appBo.setCreatedBy(appBo.getUserId());
        appId = (String) session.save(appBo);

        appSequenceBo.setAppId(appId);
        session.save(appSequenceBo);
      } else {
        AppsBo dbappBo =
            (AppsBo)
                session
                    .getNamedQuery("AppsBo.getAppsById")
                    .setString("id", appBo.getId())
                    .uniqueResult();
        if (dbappBo != null) {
          dbappBo.setCustomAppId(appBo.getCustomAppId());
          dbappBo.setName(appBo.getName());
          dbappBo.setModifiedBy(appBo.getUserId());
          dbappBo.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
          appSequenceBo =
              (AppSequenceBo)
                  session
                      .getNamedQuery("getAppSequenceByAppId")
                      .setString("appId", dbappBo.getId())
                      .uniqueResult();
          session.update(dbappBo);
        }
      }

      auditRequest.setAppId(appBo.getId());
      if (appSequenceBo != null) {
        if (StringUtils.isNotEmpty(appBo.getButtonText())
            && appBo.getButtonText().equalsIgnoreCase(COMPLETED_BUTTON)) {
          appSequenceBo.setAppInfo(true);
          // auditLogEvent = STUDY_BASIC_INFO_SECTION_MARKED_COMPLETE;
        } else if (StringUtils.isNotEmpty(appBo.getButtonText())
            && appBo.getButtonText().equalsIgnoreCase(SAVE_BUTTON)) {
          // auditLogEvent = STUDY_BASIC_INFO_SECTION_SAVED_OR_UPDATED;
          appSequenceBo.setAppInfo(false);
        }
        session.saveOrUpdate(appSequenceBo);
      }

      auditLogEventHelper.logEvent(auditLogEvent, auditRequest);

      transaction.commit();
    } catch (Exception e) {
      message = FAILURE;
      transaction.rollback();
      logger.error("AppDAOImpl - saveOrUpdateApp() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("saveOrUpdateApp() - Ends");
    return message;
  }

  public String saveOrUpdateAppSettings(AppsBo appBo, SessionObject sessionObject) {
    logger.entry("begin saveOrUpdateAppSettings()");
    Session session = null;
    String message = SUCCESS;
    StudyBuilderAuditEvent auditLogEvent = null;
    AppSequenceBo appSequenceBo = null;
    String appId = null;
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();

      if (StringUtils.isNotEmpty(appBo.getId())) {
        AppsBo dbappBo =
            (AppsBo)
                session
                    .getNamedQuery("AppsBo.getAppsById")
                    .setString("id", appBo.getId())
                    .uniqueResult();
        if (dbappBo != null) {
          dbappBo.setType(appBo.getType());
          dbappBo.setAppPlatform(appBo.getAppPlatform());
          dbappBo.setModifiedBy(appBo.getUserId());
          dbappBo.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
          appSequenceBo =
              (AppSequenceBo)
                  session
                      .getNamedQuery("getAppSequenceByAppId")
                      .setString("appId", dbappBo.getId())
                      .uniqueResult();
          session.update(dbappBo);
        }
      }

      auditRequest.setAppId(appBo.getId());
      if (appSequenceBo != null) {
        if (StringUtils.isNotEmpty(appBo.getButtonText())
            && appBo.getButtonText().equalsIgnoreCase(COMPLETED_BUTTON)) {
          appSequenceBo.setAppSettings(true);
          // auditLogEvent = STUDY_BASIC_INFO_SECTION_MARKED_COMPLETE;
        } else if (StringUtils.isNotEmpty(appBo.getButtonText())
            && appBo.getButtonText().equalsIgnoreCase(SAVE_BUTTON)) {
          // auditLogEvent = STUDY_BASIC_INFO_SECTION_SAVED_OR_UPDATED;
          appSequenceBo.setAppSettings(false);
        }
        session.saveOrUpdate(appSequenceBo);
      }

      auditLogEventHelper.logEvent(auditLogEvent, auditRequest);

      transaction.commit();
    } catch (Exception e) {
      message = FAILURE;
      transaction.rollback();
      logger.error("AppDAOImpl - saveOrUpdateAppSettings() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("saveOrUpdateAppSettings() - Ends");
    return message;
  }

  @Override
  public String updateAppAction(String appId, String buttonText, SessionObject sesObj) {
    logger.entry("begin updateAppAction()");
    String message = FAILURE;
    Session session = null;
    AppsBo app = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      if (StringUtils.isNotEmpty(appId) && StringUtils.isNotEmpty(buttonText)) {

        if (!appId.isEmpty()) {
          String searchQuery = " From AppsBo WHERE id=:appId";
          app = (AppsBo) session.createQuery(searchQuery).setString("appId", appId);
        }

        if (app != null) {
          if (buttonText.equalsIgnoreCase(ACTION_UPDATES)) {
            app.setIsAppPublished(true);
            app.setAppLaunchDate(FdahpStudyDesignerUtil.getCurrentDateTime());
            app.setAppStatus("Published");
            session.update(app);
          }
        }
      }
    } catch (Exception e) {
      transaction.rollback();
      logger.error("AppDAOImpl - updateAppAction() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }

    logger.exit("updateAppAction() - Ends");
    return message;
  }

  @Override
  public AppsBo getAppByLatestVersion(String customAppId) {
    logger.entry("begin getAppByLatestVersion()");
    Session session = null;
    AppsBo app = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      app =
          (AppsBo)
              session
                  .getNamedQuery("getAppByLatestVersion")
                  .setString("customAppId", customAppId)
                  .uniqueResult();
    } catch (Exception e) {
      logger.error("AppDAOImpl - getAppByLatestVersion() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getAppByLatestVersion() - Ends");
    return app;
  }

  @Override
  public String saveOrUpdateAppProperties(AppsBo appBo, SessionObject sessionObject) {
    logger.entry("begin saveOrUpdateAppProperties()");
    Session session = null;
    String message = SUCCESS;
    StudyBuilderAuditEvent auditLogEvent = null;
    AppSequenceBo appSequenceBo = null;
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();

      if (StringUtils.isNotEmpty(appBo.getId())) {
        AppsBo dbappBo =
            (AppsBo)
                session
                    .getNamedQuery("AppsBo.getAppsById")
                    .setString("id", appBo.getId())
                    .uniqueResult();
        if (dbappBo != null) {

          if (StringUtils.isNotEmpty(appBo.getFeedbackEmailAddress())) {
            dbappBo.setFeedbackEmailAddress(appBo.getFeedbackEmailAddress());
          }
          if (StringUtils.isNotEmpty(appBo.getContactEmailAddress())) {
            dbappBo.setContactEmailAddress(appBo.getContactEmailAddress());
          }

          if (StringUtils.isNotEmpty(appBo.getAppSupportEmailAddress())) {
            dbappBo.setAppSupportEmailAddress(appBo.getAppSupportEmailAddress());
          }
          if (StringUtils.isNotEmpty(appBo.getAppTermsUrl())) {
            dbappBo.setAppTermsUrl(appBo.getAppTermsUrl());
          }
          if (StringUtils.isNotEmpty(appBo.getAppPrivacyUrl())) {
            dbappBo.setAppPrivacyUrl(appBo.getAppPrivacyUrl());
          }
          if (StringUtils.isNotEmpty(appBo.getOrganizationName())) {
            dbappBo.setOrganizationName(appBo.getOrganizationName());
          }
          if (StringUtils.isNotEmpty(appBo.getAppStoreUrl())) {
            dbappBo.setAppStoreUrl(appBo.getAppStoreUrl());
          }
          if (StringUtils.isNotEmpty(appBo.getPlayStoreUrl())) {
            dbappBo.setPlayStoreUrl(appBo.getPlayStoreUrl());
          }

          if (StringUtils.isNotEmpty(appBo.getFromEmailAddress())) {
            dbappBo.setFromEmailAddress(appBo.getFromEmailAddress());
          }

          if (StringUtils.isNotEmpty(appBo.getAppWebsiteUrl())) {
            dbappBo.setAppWebsiteUrl(appBo.getAppWebsiteUrl());
          }

          dbappBo.setModifiedBy(appBo.getUserId());
          dbappBo.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
          appSequenceBo =
              (AppSequenceBo)
                  session
                      .getNamedQuery("getAppSequenceByAppId")
                      .setString("appId", dbappBo.getId())
                      .uniqueResult();
          session.update(dbappBo);
        }
      }

      auditRequest.setAppId(appBo.getId());
      if (appSequenceBo != null) {
        if (StringUtils.isNotEmpty(appBo.getButtonText())
            && appBo.getButtonText().equalsIgnoreCase(COMPLETED_BUTTON)) {
          appSequenceBo.setAppProperties(true);
          // auditLogEvent = STUDY_BASIC_INFO_SECTION_MARKED_COMPLETE;
        } else if (StringUtils.isNotEmpty(appBo.getButtonText())
            && appBo.getButtonText().equalsIgnoreCase(SAVE_BUTTON)) {
          // auditLogEvent = STUDY_BASIC_INFO_SECTION_SAVED_OR_UPDATED;
          appSequenceBo.setAppProperties(false);
        }
        session.saveOrUpdate(appSequenceBo);
      }

      auditLogEventHelper.logEvent(auditLogEvent, auditRequest);

      transaction.commit();
    } catch (Exception e) {
      message = FAILURE;
      transaction.rollback();
      logger.error("AppDAOImpl - saveOrUpdateAppProperties() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("saveOrUpdateAppProperties() - Ends");
    return message;
  }

  public boolean validateAppActions(String appId) {
    String message = FdahpStudyDesignerConstants.SUCCESS;
    Session session = null;
    AppSequenceBo appSequenceBo = null;
    AppsBo appBo = null;
    boolean markedAsCompleted = false;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if (StringUtils.isNotEmpty(appId)) {
        appBo =
            (AppsBo)
                session.getNamedQuery("AppsBo.getAppsById").setString("id", appId).uniqueResult();
        appSequenceBo =
            (AppSequenceBo)
                session
                    .getNamedQuery("getAppSequenceByAppId")
                    .setString("appId", appBo.getId())
                    .uniqueResult();

        // 1-all validation mark as completed
        if (appSequenceBo != null) {

          markedAsCompleted = getErrorForAction(appSequenceBo);
          if (markedAsCompleted) {
            return markedAsCompleted;
          } else {
            markedAsCompleted = false;
          }
        }

      } else {
        message = "Action is missing";
      }

    } catch (Exception e) {
      logger.error("StudyDAOImpl - validateStudyAction() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("validateStudyAction() - Ends");
    return markedAsCompleted;
  }

  public boolean getErrorForAction(AppSequenceBo appSequenceBo) {
    boolean completed = false;
    if (appSequenceBo != null && appSequenceBo.isAppInfo() && appSequenceBo.isAppSettings()) {
      completed = true;
      return completed;
    }
    return completed;
  }

  @Override
  public String saveOrUpdateAppDeveloperConfig(AppsBo appBo, SessionObject sessionObject) {
    logger.entry("begin saveOrUpdateAppDeveloperConfig()");
    Session session = null;
    String message = SUCCESS;
    StudyBuilderAuditEvent auditLogEvent = null;
    AppSequenceBo appSequenceBo = null;
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();

      if (StringUtils.isNotEmpty(appBo.getId())) {
        AppsBo dbappBo =
            (AppsBo)
                session
                    .getNamedQuery("AppsBo.getAppsById")
                    .setString("id", appBo.getId())
                    .uniqueResult();
        if (dbappBo != null) {

          if (StringUtils.isNotEmpty(appBo.getAndroidBundleId())) {
            dbappBo.setAndroidBundleId(appBo.getAndroidBundleId());
          }
          if (StringUtils.isNotEmpty(appBo.getAndroidServerKey())) {
            dbappBo.setAndroidServerKey(appBo.getAndroidServerKey());
          }

          if (StringUtils.isNotEmpty(appBo.getIosBundleId())) {
            dbappBo.setIosBundleId(appBo.getIosBundleId());
          }
          if (StringUtils.isNotEmpty(appBo.getIosServerKey())) {
            dbappBo.setIosServerKey(appBo.getIosServerKey());
          }
          if (StringUtils.isNotEmpty(appBo.getIosXCodeAppVersion())) {
            dbappBo.setIosXCodeAppVersion(appBo.getIosXCodeAppVersion());
          }
          if (StringUtils.isNotEmpty(appBo.getIosAppBuildVersion())) {
            dbappBo.setIosAppBuildVersion(appBo.getIosAppBuildVersion());
          }
          if (appBo.getIosForceUpgrade() != null) {
            dbappBo.setIosForceUpgrade(appBo.getIosForceUpgrade());
          }

          if (StringUtils.isNotEmpty(appBo.getAndroidAppBuildVersion())) {
            dbappBo.setAndroidAppBuildVersion(appBo.getAndroidAppBuildVersion());
          }

          if (appBo.getAndroidForceUpgrade() != null) {
            dbappBo.setAndroidForceUpgrade(appBo.getAndroidForceUpgrade());
          }

          dbappBo.setModifiedBy(appBo.getUserId());
          dbappBo.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
          appSequenceBo =
              (AppSequenceBo)
                  session
                      .getNamedQuery("getAppSequenceByAppId")
                      .setString("appId", dbappBo.getId())
                      .uniqueResult();
          session.update(dbappBo);
        }
      }

      auditRequest.setAppId(appBo.getId());
      if (appSequenceBo != null) {
        if (StringUtils.isNotEmpty(appBo.getButtonText())
            && appBo.getButtonText().equalsIgnoreCase(COMPLETED_BUTTON)) {
          appSequenceBo.setDeveloperConfigs(true);
          // auditLogEvent = STUDY_BASIC_INFO_SECTION_MARKED_COMPLETE;
        } else if (StringUtils.isNotEmpty(appBo.getButtonText())
            && appBo.getButtonText().equalsIgnoreCase(SAVE_BUTTON)) {
          // auditLogEvent = STUDY_BASIC_INFO_SECTION_SAVED_OR_UPDATED;
          appSequenceBo.setDeveloperConfigs(false);
        }
        session.saveOrUpdate(appSequenceBo);
      }

      auditLogEventHelper.logEvent(auditLogEvent, auditRequest);

      transaction.commit();
    } catch (Exception e) {
      message = FAILURE;
      transaction.rollback();
      logger.error("AppDAOImpl - saveOrUpdateAppDeveloperConfig() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("saveOrUpdateAppDeveloperConfig() - Ends");
    return message;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<AppsBo> getActiveApps(String userId) {
    Session session = null;
    List<AppsBo> appListBean = null;
    try {

      session = hibernateTemplate.getSessionFactory().openSession();

      if (StringUtils.isNotEmpty(userId)) {

        query = session.getNamedQuery("getUserById").setString("userId", userId);
        UserBO userBO = (UserBO) query.uniqueResult();

        if (userBO.getRoleId().equals("1")) {
          appListBean = session.getNamedQuery("getApps").setString("status", "Active").list();

        } else {
          query =
              session.createQuery(
                  "Select *"
                      + " from AppsBo a,AppPermissionBO ap, UserBO user"
                      + " where a.id=ap.appId"
                      + " and a.version=0"
                      + " and ap.userId=:impValue"
                      + " and a.appStatus = 'Active'"
                      + " order by a.createdOn desc");
          appListBean = query.setString(IMP_VALUE, userId).list();
        }
      }
    } catch (Exception e) {
      logger.error("AppDAOImpl - getActiveApps() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getActiveApps() - Ends");
    return appListBean;
  }

  @Override
  public boolean getAppPermission(String apppId, String userId) {
    logger.entry("begin getAppPermission()");
    Session session = null;
    AppPermissionBO appPermissionBO = null;
    boolean permission = false;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();

      query = session.getNamedQuery("getUserById").setString("userId", userId);
      UserBO userBO = (UserBO) query.uniqueResult();

      if (userBO.getRoleId().equals("1")) {
        return true;
      } else {
        appPermissionBO =
            (AppPermissionBO)
                session
                    .getNamedQuery("getAppPermission")
                    .setString("appId", apppId)
                    .setString("userId", userId)
                    .uniqueResult();
        if (appPermissionBO != null) {
          permission = appPermissionBO.isViewPermission();
        }
      }
    } catch (Exception e) {
      logger.error("AppDAOImpl - getAppPermission() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getAppPermission() - Ends");
    return permission;
  }
}
