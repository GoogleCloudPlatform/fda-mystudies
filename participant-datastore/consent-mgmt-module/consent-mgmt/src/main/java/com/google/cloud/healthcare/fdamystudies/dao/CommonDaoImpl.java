/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.bean.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class CommonDaoImpl implements CommonDao {

  private XLogger logger = XLoggerFactory.getXLogger(CommonDaoImpl.class.getName());

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Override
  public AppOrgInfoBean getUserAppDetailsByAllApi(String userId, String appId) {
    logger.entry("Begin validatedUserAppDetailsByAllApi()");
    Transaction transaction = null;
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<AppEntity> appDetailsBoCriteria = null;
    Root<AppEntity> appDetailsBoRoot = null;
    Predicate[] appDetailsPredicates = new Predicate[1];
    List<AppEntity> appDetailsList = null;
    AppEntity appEntity = null;

    AppOrgInfoBean appOrgInfoBean = new AppOrgInfoBean();
    String message = "";
    String appInfoId = String.valueOf(0);

    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();

      if (!StringUtils.isEmpty(appId)) {
        appDetailsBoCriteria = criteriaBuilder.createQuery(AppEntity.class);
        appDetailsBoRoot = appDetailsBoCriteria.from(AppEntity.class);
        appDetailsPredicates[0] = criteriaBuilder.equal(appDetailsBoRoot.get("appId"), appId);
        appDetailsBoCriteria.select(appDetailsBoRoot).where(appDetailsPredicates);
        appDetailsList = session.createQuery(appDetailsBoCriteria).getResultList();
        if (!appDetailsList.isEmpty()) {
          appEntity = appDetailsList.get(0);
          appInfoId = appEntity.getId();
        }
      }

      appOrgInfoBean.setAppInfoId(appInfoId);
    } catch (Exception e) {
      appOrgInfoBean.setAppInfoId(appInfoId);
      logger.error("UserConsentManagementDaoImpl getUserAppDetailsByAllApi() - error ", e);
    }
    logger.exit("getUserAppDetailsByAllApi() - Ends ");
    return appOrgInfoBean;
  }

  @Override
  public String getUserDetailsId(String userId) {
    logger.entry("Begin getUserDetailsId()");
    Transaction transaction = null;
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<UserDetailsEntity> userDetailsBoCriteria = null;
    Root<UserDetailsEntity> userDetailsBoRoot = null;
    List<UserDetailsEntity> userDetailsBoList = null;
    Predicate[] userDetailspredicates = new Predicate[1];
    UserDetailsEntity userDetailsEntity = null;
    String userDetailsId = String.valueOf(0);
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      if (!StringUtils.isEmpty(userId)) {

        userDetailsBoCriteria = criteriaBuilder.createQuery(UserDetailsEntity.class);
        userDetailsBoRoot = userDetailsBoCriteria.from(UserDetailsEntity.class);

        userDetailspredicates[0] = criteriaBuilder.equal(userDetailsBoRoot.get(userId), userId);
        userDetailsBoCriteria.select(userDetailsBoRoot).where(userDetailspredicates);
        userDetailsBoList = session.createQuery(userDetailsBoCriteria).getResultList();

        if (!userDetailsBoList.isEmpty()) {
          userDetailsEntity = userDetailsBoList.get(0);
          userDetailsId = userDetailsEntity.getId();
        }
      }
    } catch (Exception e) {
      userDetailsId = String.valueOf(0);
      logger.error("UserProfileManagementDaoImpl getUserDetailsId() - error ", e);
    }

    logger.exit("getUserDetailsId() - Ends ");
    return userDetailsId;
  }
}
