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
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
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
    logger.entry("begin getStudyList()");
    Session session = null;
    List<AppListBean> appListBean = null;
    String name = "";
    AppsBo appBo = null;
    try {

      session = hibernateTemplate.getSessionFactory().openSession();

      if (StringUtils.isNotEmpty(userId)) {

        query = session.getNamedQuery("getUserById").setString("userId", userId);
        UserBO userBO = (UserBO) query.uniqueResult();

        if (userBO.getRoleId().equals("1")) {
          query =
              session.createQuery(
                  "select new com.fdahpstudydesigner.bean.AppListBean(a.id,a.customAppId,a.name,a.status,a.type,a.createdOn)"
                      + " from AppsBo a, UserBO user"
                      + " where user.userId = a.createdBy"
                      + " and a.version=0"
                      + " order by a.createdOn desc");

        } else {
          query =
              session.createQuery(
                  "select new com.fdahpstudydesigner.bean.AppListBean(a.id,a.customAppId,a.name,a.status,a.type,a.createdOn)"
                      + " from AppsBo a,AppPermissionBO ap, UserBO user"
                      + " where a.id=ap.appId"
                      + " and user.userId = a.createdBy"
                      + " and a.version=0"
                      + " and ap.userId=:impValue"
                      + " order by a.createdOn desc");
          query.setString(FdahpStudyDesignerConstants.IMP_VALUE, userId);
        }
        appListBean = query.list();
      }

    } catch (Exception e) {
      logger.error("AppDAOImpl - getStudyList() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getStudyList() - Ends");
    return appListBean;
  }
}
