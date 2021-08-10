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

import com.fdahpstudydesigner.bean.AppListBean;
import com.fdahpstudydesigner.bo.AppsBo;
import com.fdahpstudydesigner.bo.StudyBo;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import java.math.BigInteger;
import java.util.List;
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

  public AppDAOImpl() {
    // Unused
  }

  @Autowired
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.hibernateTemplate = new HibernateTemplate(sessionFactory);
  }

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
                  "select new com.fdahpstudydesigner.bean.AppListBean(a.id,a.customAppId,a.name,a.appsStatus,a.type,a.createdOn)"
                      + " from AppsBo a, UserBO user"
                      + " where user.userId = a.createdBy"
                      + " and a.version=0"
                      + " order by a.createdOn desc");

        } else {
          query =
              session.createQuery(
                  "select new com.fdahpstudydesigner.bean.AppListBean(a.id,a.customAppId,a.name,a.appsStatus,a.type,a.createdOn,ap.viewPermission)"
                      + " from AppsBo a,AppPermissionBO ap, UserBO user"
                      + " where a.id=ap.appId"
                      + " and user.userId = a.createdBy"
                      + " and a.version=0"
                      + " and ap.userId=:impValue"
                      + " order by a.createdOn desc");
          query.setString(FdahpStudyDesignerConstants.IMP_VALUE, userId);
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
                          .setString(FdahpStudyDesignerConstants.IMP_VALUE, userId)
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
    logger.entry("begin getStudyById()");
    Session session = null;
    AppsBo appsBo = null;
    AppsBo liveAppsBo = null;
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
        }
      }
    } catch (Exception e) {
      logger.error("StudyDAOImpl - getStudyList() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getStudyById() - Ends");
    return appsBo;
  }
}
