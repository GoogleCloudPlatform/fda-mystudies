/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import com.google.cloud.healthcare.fdamystudies.beans.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.model.AppInfoDetailsBO;
import com.google.cloud.healthcare.fdamystudies.model.OrgInfo;
import com.google.cloud.healthcare.fdamystudies.model.UserAppDetailsBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;

@Repository
public class CommonDaoImpl implements CommonDao {

  private static Logger logger = LoggerFactory.getLogger(CommonDaoImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Override
  public String validatedUserAppDetailsByAllApi(String userId, String email, int appId, int orgId) {
    logger.info("CommonDaoImpl validatedUserAppDetailsByAllApi() - Starts ");
    Transaction transaction = null;
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<UserDetailsBO> userDetailsBoCriteria = null;
    CriteriaQuery<UserAppDetailsBO> userAppDetailsBoCriteria = null;
    Root<UserDetailsBO> userDetailsBoRoot = null;
    Root<UserAppDetailsBO> userAppDetailsBoRoot = null;
    Predicate[] predicates = new Predicate[2];
    Predicate[] userAppDetailsPredicates = new Predicate[2];
    List<UserDetailsBO> userDetailsBoList = null;
    String message = "";
    List<UserAppDetailsBO> userAppDetailsList = null;
    UserDetailsBO userDetailsBO = null;
    Integer userDetailsId = 0;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      transaction = session.beginTransaction();
      criteriaBuilder = session.getCriteriaBuilder();

      if (!StringUtils.isEmpty(email)) {
        userDetailsBoCriteria = criteriaBuilder.createQuery(UserDetailsBO.class);
        userDetailsBoRoot = userDetailsBoCriteria.from(UserDetailsBO.class);
        predicates[0] = criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.EMAIL), email);
        predicates[1] =
            criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.APPLICATION_ID), appId);
        userDetailsBoCriteria.select(userDetailsBoRoot).where(predicates);
        userDetailsBoList = session.createQuery(userDetailsBoCriteria).getResultList();
        if (!userDetailsBoList.isEmpty()) {
          userDetailsBO = userDetailsBoList.get(0);
          userDetailsId = userDetailsBO.getUserDetailsId();
        }
      }

      if (!StringUtils.isEmpty(userId)) {
        userDetailsBoCriteria = criteriaBuilder.createQuery(UserDetailsBO.class);
        userDetailsBoRoot = userDetailsBoCriteria.from(UserDetailsBO.class);
        predicates[0] =
            criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.KEY_USERID), userId);
        predicates[1] =
            criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.APPLICATION_ID), appId);
        userDetailsBoCriteria.select(userDetailsBoRoot).where(predicates);
        userDetailsBoList = session.createQuery(userDetailsBoCriteria).getResultList();
        if (!userDetailsBoList.isEmpty()) {
          userDetailsBO = userDetailsBoList.get(0);
          userDetailsId = userDetailsBO.getUserDetailsId();
          userAppDetailsBoCriteria = criteriaBuilder.createQuery(UserAppDetailsBO.class);
          userAppDetailsBoRoot = userAppDetailsBoCriteria.from(UserAppDetailsBO.class);
          userAppDetailsPredicates[0] =
              criteriaBuilder.equal(userAppDetailsBoRoot.get("userDetailsId"), userDetailsId);
          userAppDetailsPredicates[1] =
              criteriaBuilder.equal(userAppDetailsBoRoot.get(AppConstants.APPLICATION_ID), appId);
          userAppDetailsBoCriteria
              .select(userAppDetailsBoRoot)
              .where(userAppDetailsPredicates)
              .orderBy(criteriaBuilder.asc(userAppDetailsBoRoot.get("createdOn")));
          userAppDetailsList = session.createQuery(userAppDetailsBoCriteria).getResultList();
          if ((userAppDetailsList != null) && (!userAppDetailsList.isEmpty())) {
            message = MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue();
          } else {
            message = MyStudiesUserRegUtil.ErrorCodes.ORG_NOTEXIST.getValue();
          }
        } else {
          message = MyStudiesUserRegUtil.ErrorCodes.ACCOUNT_DEACTIVATE_ERROR_MSG.getValue();
        }
      } else {
        message = MyStudiesUserRegUtil.ErrorCodes.ACCOUNT_DEACTIVATE_ERROR_MSG.getValue();
      }
      transaction.commit();
    } catch (Exception e) {
      logger.error("CommonDaoImpl validatedUserAppDetailsByAllApi() - error ", e);
    }
    logger.info("CommonDaoImpl validatedUserAppDetailsByAllApi() - Ends ");
    return message;
  }

  @Override
  public AppOrgInfoBean getUserAppDetailsByAllApi(String userId, String appId, String orgId) {
    logger.info("CommonDaoImpl validatedUserAppDetailsByAllApi() - Starts ");
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<AppInfoDetailsBO> appDetailsBoCriteria = null;
    Root<AppInfoDetailsBO> appDetailsBoRoot = null;
    Predicate[] appDetailsPredicates = new Predicate[1];
    List<AppInfoDetailsBO> appDetailsList = null;
    AppInfoDetailsBO appDetailsBO = null;

    CriteriaQuery<OrgInfo> orgDetailsBoCriteria = null;
    Root<OrgInfo> orgDetailsBoRoot = null;
    Predicate[] orgDetailsBoPredicates = new Predicate[1];
    List<OrgInfo> orgDetailsBoList = null;
    OrgInfo orgDetailsBo = null;
    AppOrgInfoBean appOrgInfoBean = new AppOrgInfoBean();
    int appInfoId = 0;
    int orgInfoId = 0;

    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();

      if (!StringUtils.isEmpty(appId)) {
        appDetailsBoCriteria = criteriaBuilder.createQuery(AppInfoDetailsBO.class);
        appDetailsBoRoot = appDetailsBoCriteria.from(AppInfoDetailsBO.class);
        appDetailsPredicates[0] = criteriaBuilder.equal(appDetailsBoRoot.get("appId"), appId);
        appDetailsBoCriteria.select(appDetailsBoRoot).where(appDetailsPredicates);
        appDetailsList = session.createQuery(appDetailsBoCriteria).getResultList();
        if (!appDetailsList.isEmpty()) {
          appDetailsBO = appDetailsList.get(0);
          appInfoId = appDetailsBO.getAppInfoId();
        }
      }

      if (!StringUtils.isEmpty(orgId)) {

        orgDetailsBoCriteria = criteriaBuilder.createQuery(OrgInfo.class);
        orgDetailsBoRoot = orgDetailsBoCriteria.from(OrgInfo.class);
        orgDetailsBoPredicates[0] = criteriaBuilder.equal(orgDetailsBoRoot.get("orgId"), orgId);
        orgDetailsBoCriteria.select(orgDetailsBoRoot).where(orgDetailsBoPredicates);
        orgDetailsBoList = session.createQuery(orgDetailsBoCriteria).getResultList();

        if (!orgDetailsBoList.isEmpty()) {
          orgDetailsBo = orgDetailsBoList.get(0);
          orgInfoId = orgDetailsBo.getId();
        }
      }
      appOrgInfoBean.setAppInfoId(appInfoId);
      appOrgInfoBean.setOrgInfoId(orgInfoId);

    } catch (Exception e) {
      appOrgInfoBean.setAppInfoId(appInfoId);
      appOrgInfoBean.setOrgInfoId(orgInfoId);
      logger.error("CommonDaoImpl getUserAppDetailsByAllApi() - error ", e);
    }
    logger.info("CommonDaoImpl getUserAppDetailsByAllApi() - Ends ");
    return appOrgInfoBean;
  }

  @Override
  public Integer getUserInfoDetails(String userId) {
    logger.info("CommonDaoImpl getUserInfoDetails() - Starts ");
    CriteriaBuilder criteriaBuilder = null;
    Integer userDetailsId = null;
    CriteriaQuery<UserDetailsBO> userDetailsCriteriaQuery = null;
    Root<UserDetailsBO> userDetailsBoRoot = null;
    Predicate[] userDetailspredicates = new Predicate[1];
    List<UserDetailsBO> userDetailsBoList = null;
    UserDetailsBO userDetailsBO = null;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      userDetailsCriteriaQuery = criteriaBuilder.createQuery(UserDetailsBO.class);
      userDetailsBoRoot = userDetailsCriteriaQuery.from(UserDetailsBO.class);
      userDetailspredicates[0] =
          criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.KEY_USERID), userId);
      userDetailsCriteriaQuery.select(userDetailsBoRoot).where(userDetailspredicates);
      userDetailsBoList = session.createQuery(userDetailsCriteriaQuery).getResultList();
      if (!userDetailsBoList.isEmpty()) {
        userDetailsBO = userDetailsBoList.get(0);
        userDetailsId = userDetailsBO.getUserDetailsId();
      }
    } catch (Exception e) {
      logger.error("CommonDaoImpl getUserInfoDetails() - error ", e);
    }
    logger.info("CommonDaoImpl getUserInfoDetails() - Ends ");
    return userDetailsId;
  }
}
