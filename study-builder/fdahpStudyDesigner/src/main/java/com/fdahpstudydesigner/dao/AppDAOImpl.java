/*
 * Copyright 2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.dao;

import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_DEVELOPER_CONFIGURATIONS_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_DEVELOPER_CONFIGURATIONS_SAVED_OR_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_INFORMATION_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_INFORMATION_SAVED_OR_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_PROPERTIES_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_PROPERTIES_SAVED_OR_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_SETTINGS_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_SETTINGS_SAVED_OR_UPDATED;
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
import com.fdahpstudydesigner.bo.VersionInfoBO;
import com.fdahpstudydesigner.common.StudyBuilderAuditEvent;
import com.fdahpstudydesigner.common.StudyBuilderAuditEventHelper;
import com.fdahpstudydesigner.mapper.AuditEventMapper;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;
import java.math.BigInteger;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.SerializationUtils;
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
    AppsBo appBo = null;
    AppsBo liveApp = null;
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
                      + " and a.live=0"
                      + " order by a.createdOn desc");

        } else {
          query =
              session.createQuery(
                  "select new com.fdahpstudydesigner.bean.AppListBean(a.id,a.customAppId,a.name,a.appStatus,a.type,a.createdOn,ap.viewPermission)"
                      + " from AppsBo a,AppPermissionBO ap, UserBO user"
                      + " where a.id=ap.appId"
                      + " and user.userId = a.createdBy"
                      + " and a.live=0"
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
                AppSequenceBo appSequenceBo =
                    (AppSequenceBo)
                        session
                            .getNamedQuery("getAppSequenceByAppId")
                            .setString("appId", appDetails.getId())
                            .uniqueResult();

                if (appSequenceBo.isAppInfo()
                    && appSequenceBo.isAppSettings()
                    && appSequenceBo.isActions()) {
                  appDetails.setCreateFlag(true);
                }
                appDetails.setLiveAppId(null);
              }
            }

            // for draft app
            if (appDetails.getId() != null && (appDetails.getLiveAppId() != null)) {
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

            BigInteger totalStudyCount =
                (BigInteger)
                    session
                        .createSQLQuery(
                            "select count(*) from studies"
                                + " WHERE app_id=:customAppId AND is_live=0")
                        .setString("customAppId", appDetails.getCustomAppId())
                        .uniqueResult();
            appDetails.setStudiesCount(totalStudyCount);
            if (userBO.getRoleId().equals("1")) {
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
              appDetails.setStudyPermissionCount(studyCount);
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
    VersionInfoBO versionInfoBO = null;
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

          versionInfoBO =
              (VersionInfoBO)
                  session
                      .getNamedQuery("getVersionByappId")
                      .setString("appId", appsBo.getCustomAppId())
                      .uniqueResult();

          if (appSequenceBo != null) {
            appsBo.setAppSequenceBo(appSequenceBo);
          }

          if (versionInfoBO != null) {
            appsBo.setVersionInfoBO(versionInfoBO);
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
          if (StringUtils.isNotEmpty(appBo.getCustomAppId())) {
            dbappBo.setCustomAppId(appBo.getCustomAppId());
          }

          if (appBo.getName() != null) {
            dbappBo.setName(appBo.getName());
          }
          dbappBo.setModifiedBy(appBo.getUserId());
          dbappBo.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());

          appSequenceBo =
              (AppSequenceBo)
                  session
                      .getNamedQuery("getAppSequenceByAppId")
                      .setString("appId", dbappBo.getId())
                      .uniqueResult();

          if (Boolean.TRUE.equals(dbappBo.getIsAppPublished())) {
            dbappBo.setHasAppDraft(1);
          }
          session.update(dbappBo);
          auditRequest.setAppId(dbappBo.getCustomAppId());
          auditRequest.setUserId(sessionObject.getUserId());
        }
      }

      if (appSequenceBo != null) {
        if (StringUtils.isNotEmpty(appBo.getButtonText())
            && appBo.getButtonText().equalsIgnoreCase(COMPLETED_BUTTON)) {
          appSequenceBo.setAppInfo(true);
          auditLogEvent = APP_INFORMATION_MARKED_COMPLETE;
        } else if (StringUtils.isNotEmpty(appBo.getButtonText())
            && appBo.getButtonText().equalsIgnoreCase(SAVE_BUTTON)) {
          auditLogEvent = APP_INFORMATION_SAVED_OR_UPDATED;
          appSequenceBo.setAppInfo(false);
        }
        appSequenceBo.setActions(false);
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
          if (appBo.getType() != null) {
            dbappBo.setType(appBo.getType());
          }
          if (!dbappBo.getAppStatus().equals("Active")) {
            dbappBo.setAppPlatform(appBo.getAppPlatform());
          }
          dbappBo.setModifiedBy(appBo.getUserId());
          dbappBo.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
          appSequenceBo =
              (AppSequenceBo)
                  session
                      .getNamedQuery("getAppSequenceByAppId")
                      .setString("appId", dbappBo.getId())
                      .uniqueResult();

          if (Boolean.TRUE.equals(dbappBo.getIsAppPublished())) {
            dbappBo.setHasAppDraft(1);
          }

          session.update(dbappBo);
          auditRequest.setAppId(dbappBo.getCustomAppId());
          auditRequest.setUserId(sessionObject.getUserId());
        }
      }

      if (appSequenceBo != null) {
        if (StringUtils.isNotEmpty(appBo.getButtonText())
            && appBo.getButtonText().equalsIgnoreCase(COMPLETED_BUTTON)) {
          appSequenceBo.setAppSettings(true);
          auditLogEvent = APP_SETTINGS_MARKED_COMPLETE;
        } else if (StringUtils.isNotEmpty(appBo.getButtonText())
            && appBo.getButtonText().equalsIgnoreCase(SAVE_BUTTON)) {
          auditLogEvent = APP_SETTINGS_SAVED_OR_UPDATED;
          appSequenceBo.setAppSettings(false);
        }
        appSequenceBo.setActions(false);
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
  public String updateAppAction(
      String appId, String buttonText, SessionObject sesObj, AuditLogEventRequest auditRequest) {
    logger.entry("begin updateAppAction()");
    String message = FAILURE;
    Session session = null;
    AppsBo app = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      if (StringUtils.isNotEmpty(appId) && StringUtils.isNotEmpty(buttonText)) {

        if (!appId.isEmpty()) {
          app =
              (AppsBo)
                  session.getNamedQuery("AppsBo.getAppsById").setString("id", appId).uniqueResult();
        }

        if (app != null) {
          auditRequest.setUserId(sesObj.getUserId());
          auditRequest.setAppId(app.getCustomAppId());
          if (buttonText.equalsIgnoreCase("createAppId")) {
            app.setAppStatus("Active");
            app.setCreatedBy(sesObj.getUserId());
            AppSequenceBo appSequenceBo =
                (AppSequenceBo)
                    session
                        .getNamedQuery("getAppSequenceByAppId")
                        .setString("appId", appId)
                        .uniqueResult();

            if (appSequenceBo != null) {
              appSequenceBo.setActions(true);
              session.update(appSequenceBo);
            }
          } else if (buttonText.equalsIgnoreCase("publishAppId")) {
            app.setIsAppPublished(true);
            app.setAppLaunchDate(FdahpStudyDesignerUtil.getCurrentDateTime());
            app.setHasAppDraft(0);

            appDraftCreation(app, session, auditRequest);

          } else if (buttonText.equalsIgnoreCase("iosDistributedId")) {
            app.setIosAppDistributed(true);
          } else if (buttonText.equalsIgnoreCase("androidDistributedId")) {
            app.setAndroidAppDistributed(true);
          } else if (buttonText.equalsIgnoreCase("deactivateId")) {
            app.setAppStatus("Deactivated");
          }
          session.update(app);
          message = SUCCESS;
        }
      }

      transaction.commit();

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
  public void changeSatusToActive(String appId) {
    logger.entry("begin changeSatusToActive()");
    Session session = null;
    AppsBo app = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      if (StringUtils.isNotEmpty(appId)) {

        if (!appId.isEmpty()) {
          app =
              (AppsBo)
                  session.getNamedQuery("AppsBo.getAppsById").setString("id", appId).uniqueResult();
        }

        if (app != null) {
          app.setAppStatus("Active");
          session.update(app);
        }
      }

      transaction.commit();

    } catch (Exception e) {
      transaction.rollback();
      logger.error("AppDAOImpl - changeSatusToActive() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }

    logger.exit("changeSatusToActive() - Ends");
  }

  @Override
  public AppsBo getAppByLatestVersion(String customAppId) {
    logger.entry("begin getAppByLatestVersion()");
    Session session = null;
    AppsBo app = null;

    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query =
          session
              .getNamedQuery("getAppByLatestVersion")
              .setString("customAppId", customAppId)
              .setMaxResults(1);

      app = (AppsBo) query.uniqueResult();

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
          if (appBo.getFeedbackEmailAddress() != null) {
            dbappBo.setFeedbackEmailAddress(appBo.getFeedbackEmailAddress());
          }
          if (appBo.getContactEmailAddress() != null) {
            dbappBo.setContactEmailAddress(appBo.getContactEmailAddress());
          }

          if (appBo.getAppSupportEmailAddress() != null) {
            dbappBo.setAppSupportEmailAddress(appBo.getAppSupportEmailAddress());
          }
          if (appBo.getAppTermsUrl() != null) {
            dbappBo.setAppTermsUrl(appBo.getAppTermsUrl());
          }
          if (appBo.getAppPrivacyUrl() != null) {
            dbappBo.setAppPrivacyUrl(appBo.getAppPrivacyUrl());
          }
          if (appBo.getOrganizationName() != null) {
            dbappBo.setOrganizationName(appBo.getOrganizationName());
          }
          if (appBo.getAppStoreUrl() != null) {
            dbappBo.setAppStoreUrl(appBo.getAppStoreUrl());
          }
          if (appBo.getPlayStoreUrl() != null) {
            dbappBo.setPlayStoreUrl(appBo.getPlayStoreUrl());
          }

          if (appBo.getFromEmailAddress() != null) {
            dbappBo.setFromEmailAddress(appBo.getFromEmailAddress());
          }

          if (appBo.getAppWebsiteUrl() != null) {
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

          if (Boolean.TRUE.equals(dbappBo.getIsAppPublished())) {
            dbappBo.setHasAppDraft(1);
          }
          session.update(dbappBo);
          auditRequest.setAppId(dbappBo.getCustomAppId());
          auditRequest.setUserId(sessionObject.getUserId());
        }
      }

      if (appSequenceBo != null) {
        if (StringUtils.isNotEmpty(appBo.getButtonText())
            && appBo.getButtonText().equalsIgnoreCase(COMPLETED_BUTTON)) {
          appSequenceBo.setAppProperties(true);
          auditLogEvent = APP_PROPERTIES_MARKED_COMPLETE;
        } else if (StringUtils.isNotEmpty(appBo.getButtonText())
            && appBo.getButtonText().equalsIgnoreCase(SAVE_BUTTON)) {
          auditLogEvent = APP_PROPERTIES_SAVED_OR_UPDATED;
          appSequenceBo.setAppProperties(false);
        }
        appSequenceBo.setActions(false);
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
  public List<AppsBo> getAllApps() {
    logger.entry("begin getAllStudyList()");
    Session session = null;
    List<AppsBo> appList = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query =
          session.createQuery(
              " FROM AppsBo ABO WHERE ABO.live = 0 AND ABO.appStatus <> :deActivateStatus");
      query.setParameter("deActivateStatus", FdahpStudyDesignerConstants.APP_DEACTIVATED);
      appList = query.list();
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getAllStudyList() - ERROR ", e);
    }
    logger.exit("getAllStudyList() - Ends");
    return appList;
  }

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

          if (appBo.getAndroidBundleId() != null) {
            dbappBo.setAndroidBundleId(appBo.getAndroidBundleId());
          }

          if (appBo.getAndroidServerKey() != null) {
            dbappBo.setAndroidServerKey(appBo.getAndroidServerKey());
          }
          if (appBo.getIosBundleId() != null) {
            dbappBo.setIosBundleId(appBo.getIosBundleId());
          }

          if (appBo.getIosServerKey() != null) {
            dbappBo.setIosServerKey(appBo.getIosServerKey());
          }

          if (appBo.getIosXCodeAppVersion() != null) {
            dbappBo.setIosXCodeAppVersion(appBo.getIosXCodeAppVersion());
          }
          if (appBo.getIosAppBuildVersion() != null) {
            dbappBo.setIosAppBuildVersion(appBo.getIosAppBuildVersion());
          }

          if (appBo.getAndroidAppBuildVersion() != null) {
            dbappBo.setAndroidAppBuildVersion(appBo.getAndroidAppBuildVersion());
          }

          VersionInfoBO versionInfoBO =
              (VersionInfoBO)
                  session
                      .getNamedQuery("getVersionByappId")
                      .setString("appId", dbappBo.getCustomAppId())
                      .uniqueResult();

          if (versionInfoBO == null) {
            versionInfoBO = new VersionInfoBO();
          }

          if (appBo.getAndroidAppBuildVersion() != null) {
            versionInfoBO.setAndroid(appBo.getAndroidAppBuildVersion());
          }

          if (StringUtils.isNotEmpty(appBo.getIosXCodeAppVersion())
              && StringUtils.isNotEmpty(appBo.getIosAppBuildVersion())) {
            versionInfoBO.setIos(
                appBo.getIosXCodeAppVersion() + "." + appBo.getIosAppBuildVersion());
          }
          if (appBo.getIosForceUpgrade() != null) {
            versionInfoBO.setIosForceUpgrade((appBo.getIosForceUpgrade() == 1) ? true : false);
          }

          if (appBo.getAndroidForceUpgrade() != null) {
            versionInfoBO.setAndroidForceUpgrade(
                (appBo.getAndroidForceUpgrade() == 1) ? true : false);
          }

          versionInfoBO.setAppId(dbappBo.getCustomAppId());
          session.saveOrUpdate(versionInfoBO);

          dbappBo.setModifiedBy(appBo.getUserId());
          dbappBo.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
          appSequenceBo =
              (AppSequenceBo)
                  session
                      .getNamedQuery("getAppSequenceByAppId")
                      .setString("appId", dbappBo.getId())
                      .uniqueResult();

          if (Boolean.TRUE.equals(dbappBo.getIsAppPublished())) {
            dbappBo.setHasAppDraft(1);
          }

          session.update(dbappBo);
          auditRequest.setAppId(dbappBo.getCustomAppId());
          auditRequest.setUserId(sessionObject.getUserId());
        }
      }

      if (appSequenceBo != null) {
        if (StringUtils.isNotEmpty(appBo.getButtonText())
            && appBo.getButtonText().equalsIgnoreCase(COMPLETED_BUTTON)) {
          appSequenceBo.setDeveloperConfigs(true);
          auditLogEvent = APP_DEVELOPER_CONFIGURATIONS_MARKED_COMPLETE;
        } else if (StringUtils.isNotEmpty(appBo.getButtonText())
            && appBo.getButtonText().equalsIgnoreCase(SAVE_BUTTON)) {
          auditLogEvent = APP_DEVELOPER_CONFIGURATIONS_SAVED_OR_UPDATED;
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
  public List<AppsBo> getApps(String userId) {
    Session session = null;
    List<AppsBo> appListBean = null;
    try {

      session = hibernateTemplate.getSessionFactory().openSession();

      if (StringUtils.isNotEmpty(userId)) {

        query = session.getNamedQuery("getUserById").setString("userId", userId);
        UserBO userBO = (UserBO) query.uniqueResult();

        if (userBO.getRoleId().equals("1")) {
          appListBean = session.getNamedQuery("getApps").list();

        } else {
          query =
              session.createQuery(
                  "Select DISTINCT a"
                      + " from AppsBo a,StudyPermissionBO sp, StudyBo s"
                      + " where a.customAppId=s.appId"
                      + " AND s.id = sp.studyId"
                      + " and a.live=0 "
                      + " and sp.userId=:impValue"
                      + " and a.appStatus IN ('Active','Deactivated')"
                      + " order by a.createdOn desc");
          appListBean = query.setString(IMP_VALUE, userId).list();
        }
      }
    } catch (Exception e) {
      logger.error("AppDAOImpl - getApps() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getApps() - Ends");
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

  @SuppressWarnings("unchecked")
  @Override
  public int getStudiesByAppId(String customAppId) {

    logger.entry("begin getAppById()");
    Session session = null;
    List<StudyBo> studyBoList = null;
    int count = 0;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if (StringUtils.isNotEmpty(customAppId)) {

        studyBoList =
            session
                .getNamedQuery("StudyBo.getStudyBycustomAppId")
                .setString("customAppId", customAppId)
                .setString("status", "Deactivated")
                .list();

        count = studyBoList.size();
      }
    } catch (Exception e) {
      logger.error("AppDAOImpl - getAppById() - ERROR ", e);
    }
    logger.exit("getAppById() - Ends");
    return count;
  }

  public List<AppsBo> getAppsForStudy(String userId) {
    Session session = null;
    List<AppsBo> appListBean = null;
    try {

      session = hibernateTemplate.getSessionFactory().openSession();

      if (StringUtils.isNotEmpty(userId)) {

        query = session.getNamedQuery("getUserById").setString("userId", userId);
        UserBO userBO = (UserBO) query.uniqueResult();

        if (userBO.getRoleId().equals("1")) {
          appListBean =
              session
                  .createQuery(
                      "FROM AppsBo a WHERE a.appStatus = 'Active' AND a.live=0 AND a.customAppId NOT IN "
                          + "(SELECT s.appId FROM StudyBo s where s.type='SD' AND s.appId IS NOT NULL) "
                          + "order by a.createdOn desc ")
                  .list();

        } else {
          query =
              session.createQuery(
                  " SELECT DISTINCT a from AppsBo a,AppPermissionBO ap, UserBO user"
                      + " where a.id=ap.appId"
                      + " and a.live=0 and ap.viewPermission = '1'"
                      + " and ap.userId=:impValue"
                      + " and a.appStatus = 'Active' AND a.customAppId NOT IN "
                      + " (SELECT s.appId FROM StudyBo s where s.type='SD' AND s.appId IS NOT NULL) "
                      + " order by a.createdOn desc ");
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
  public boolean getAppPermissionByCustomAppId(String customAppId, String userId) {
    logger.entry("begin getAppPermissionByCustomAppId()");
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
        query =
            session
                .createQuery(
                    "SELECT ap From AppPermissionBO ap, AppsBo a"
                        + " WHERE a.id=ap.appId AND a.customAppId=:customAppId AND ap.userId=:userId")
                .setString("customAppId", customAppId)
                .setString("userId", userId);
        appPermissionBO = (AppPermissionBO) query.uniqueResult();

        if (appPermissionBO != null) {
          permission = appPermissionBO.isViewPermission();
        }
      }
    } catch (Exception e) {
      logger.error("AppDAOImpl - getAppPermissionByCustomAppId() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }

    logger.exit("getAppPermissionByCustomAppId() - Ends");
    return permission;
  }

  @Override
  public VersionInfoBO getVersionBycustomAppId(String customAppId) {
    logger.entry("begin getVersionBycustomAppId()");
    Session session = null;
    VersionInfoBO versionInfoBO = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if (StringUtils.isNotEmpty(customAppId)) {
        versionInfoBO =
            (VersionInfoBO)
                session
                    .getNamedQuery("getVersionByappId")
                    .setString("appId", customAppId)
                    .uniqueResult();
      }

    } catch (Exception e) {
      logger.error("AppDAOImpl - getVersionBycustomAppId() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    return versionInfoBO;
  }

  private void appDraftCreation(AppsBo app, Session session, AuditLogEventRequest auditRequest) {
    logger.info("AppDAOImpl - appDraftCreation() updateAppVersion- Starts");
    // update all studies to archive (live as 2)
    // pass customstudyId and making all study status belongs to same customstudyId
    // as 2(archive)
    query =
        session
            .getNamedQuery("updateAppVersion")
            .setString(FdahpStudyDesignerConstants.CUSTOM_APP_ID, app.getCustomAppId());
    query.executeUpdate();
    logger.info("AppDAOImpl - appDraftCreation() updateAppVersion- Ends");

    int countOfApps = getAppsByCustomAppId(app.getCustomAppId());
    // create new Study and made it draft study
    AppsBo appDraftBo = SerializationUtils.clone(app);
    if (countOfApps == 1) {
      appDraftBo.setVersion(1.0f);
    } else {
      AppsBo appLatestVersion = getAppByLatestVersion(app.getCustomAppId());
      appDraftBo.setVersion(appLatestVersion.getVersion() + 0.1f);
    }

    appDraftBo.setLive(1);
    appDraftBo.setId(null);
    session.save(appDraftBo);

    AppSequenceBo appSequenceBo =
        (AppSequenceBo)
            session
                .getNamedQuery("getAppSequenceByAppId")
                .setString("appId", app.getId())
                .uniqueResult();
    AppSequenceBo appSequenceBoDraft = SerializationUtils.clone(appSequenceBo);
    appSequenceBoDraft.setAppId(appDraftBo.getId());
    appSequenceBoDraft.setAppSequenceId(null);
    session.save(appSequenceBoDraft);
  }

  @SuppressWarnings("unchecked")
  private int getAppsByCustomAppId(String customAppId) {

    logger.entry("begin getAppsByCustomAppId()");
    Session session = null;
    List<AppsBo> appBoList = null;
    int count = 0;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if (StringUtils.isNotEmpty(customAppId)) {

        appBoList =
            session
                .getNamedQuery("AppsBo.getAppByCustomAppId")
                .setString("customAppId", customAppId)
                .list();

        count = appBoList.size();
      }
    } catch (Exception e) {
      logger.error("AppDAOImpl - getAppsByCustomAppId() - ERROR ", e);
    }
    logger.exit("getAppsByCustomAppId() - Ends");
    return count;
  }

  @SuppressWarnings("unchecked")
  @Override
  public int getStudiesCountByAppId(String customAppId) {

    logger.entry("begin getStudiesCountByAppId()");
    Session session = null;
    List<StudyBo> studyList = null;
    int count = 0;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if (StringUtils.isNotEmpty(customAppId)) {

        studyList =
            session
                .getNamedQuery("StudyBo.getStudyCountBycustomAppId")
                .setString("customAppId", customAppId)
                .list();

        count = studyList.size();
      }
    } catch (Exception e) {
      logger.error("AppDAOImpl - getStudiesCountByAppId() - ERROR ", e);
    }
    logger.exit("getStudiesCountByAppId() - Ends");
    return count;
  }

  @Override
  public AppsBo getAppByCustomAppId(String customAppId) {
    logger.entry("begin getAppByCustomAppId()");
    Session session = null;
    AppsBo app = null;

    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query = session.getNamedQuery("AppsBo.getAppByAppId").setString("customAppId", customAppId);

      app = (AppsBo) query.uniqueResult();

    } catch (Exception e) {
      logger.error("AppDAOImpl - getAppByCustomAppId() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getAppByCustomAppId() - Ends");
    return app;
  }
}
