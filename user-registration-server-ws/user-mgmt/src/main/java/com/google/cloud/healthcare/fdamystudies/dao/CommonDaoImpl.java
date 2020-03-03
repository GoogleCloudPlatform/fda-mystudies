/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
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
import com.google.cloud.healthcare.fdamystudies.model.OraganizationsInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.UserAppDetailsBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetails;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;

@Repository
public class CommonDaoImpl implements CommonDao {

  private static Logger logger = LoggerFactory.getLogger(CommonDaoImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Override
  public String validatedUserAppDetailsByAllApi(String userId, String email, int appId, int orgId) {
    logger.info("UserConsentManagementDaoImpl validatedUserAppDetailsByAllApi() - Started ");
    Transaction transaction = null;
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<UserDetails> userDetailsBoCriteria = null;
    CriteriaQuery<UserAppDetailsBO> userAppDetailsBoCriteria = null;
    Root<UserDetails> userDetailsBoRoot = null;
    Root<UserAppDetailsBO> userAppDetailsBoRoot = null;
    Predicate[] predicates = new Predicate[2];
    Predicate[] userAppDetailsPredicates = new Predicate[2];
    List<UserDetails> userDetailsBoList = null;
    String message = "";
    List<UserAppDetailsBO> userAppDetailsList = null;
    UserDetails userDetails = null;
    Integer userDetailsId = 0;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      transaction = session.beginTransaction();
      criteriaBuilder = session.getCriteriaBuilder();

      if (!StringUtils.isEmpty(email)) {
        userDetailsBoCriteria = criteriaBuilder.createQuery(UserDetails.class);
        userDetailsBoRoot = userDetailsBoCriteria.from(UserDetails.class);
        predicates[0] = criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.EMAIL), email);
        predicates[1] =
            criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.APPLICATION_ID), appId);
        userDetailsBoCriteria.select(userDetailsBoRoot).where(predicates);
        userDetailsBoList = session.createQuery(userDetailsBoCriteria).getResultList();
        if (!userDetailsBoList.isEmpty()) {
          userDetails = userDetailsBoList.get(0);
          userDetailsId = userDetails.getUserDetailsId();
        }
      }

      if (!StringUtils.isEmpty(userId)) {
        userDetailsBoCriteria = criteriaBuilder.createQuery(UserDetails.class);
        userDetailsBoRoot = userDetailsBoCriteria.from(UserDetails.class);
        predicates[0] =
            criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.KEY_USERID), userId);
        predicates[1] =
            criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.APPLICATION_ID), appId);
        userDetailsBoCriteria.select(userDetailsBoRoot).where(predicates);
        userDetailsBoList = session.createQuery(userDetailsBoCriteria).getResultList();
        if (!userDetailsBoList.isEmpty()) {
          userDetails = userDetailsBoList.get(0);
          userDetailsId = userDetails.getUserDetailsId();
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
          if ((userAppDetailsList != null) && (userAppDetailsList.size() > 0)) {
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
      logger.error("UserConsentManagementDaoImpl validatedUserAppDetailsByAllApi() - error ", e);
    }
    logger.info("UserConsentManagementDaoImpl validatedUserAppDetailsByAllApi() - Ends ");
    return message;
  }

  @Override
  public AppOrgInfoBean getUserAppDetailsByAllApi(String userId, String appId, String orgId) {
    logger.info("UserConsentManagementDaoImpl validatedUserAppDetailsByAllApi() - Started ");
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<AppInfoDetailsBO> appDetailsBoCriteria = null;
    Root<AppInfoDetailsBO> appDetailsBoRoot = null;
    Predicate[] appDetailsPredicates = new Predicate[1];
    List<AppInfoDetailsBO> appDetailsList = null;
    AppInfoDetailsBO appDetailsBO = null;

    CriteriaQuery<OraganizationsInfoBO> orgDetailsBoCriteria = null;
    Root<OraganizationsInfoBO> orgDetailsBoRoot = null;
    Predicate[] orgDetailsBoPredicates = new Predicate[1];
    List<OraganizationsInfoBO> orgDetailsBoList = null;
    OraganizationsInfoBO orgDetailsBo = null;
    AppOrgInfoBean appOrgInfoBean = new AppOrgInfoBean();
    int appInfoId = 0;
    int orgInfoId = 0;

    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();

      if (!StringUtils.isEmpty(appId)) {
        appDetailsBoCriteria = criteriaBuilder.createQuery(AppInfoDetailsBO.class);
        appDetailsBoRoot = appDetailsBoCriteria.from(AppInfoDetailsBO.class);
        appDetailsPredicates[0] =
            criteriaBuilder.equal(appDetailsBoRoot.get(AppConstants.CUSTOM_APPLICATION_ID), appId);
        appDetailsBoCriteria.select(appDetailsBoRoot).where(appDetailsPredicates);
        appDetailsList = session.createQuery(appDetailsBoCriteria).getResultList();
        if (!appDetailsList.isEmpty()) {
          appDetailsBO = appDetailsList.get(0);
          appInfoId = appDetailsBO.getAppInfoId();
        }
      }

      if (!StringUtils.isEmpty(orgId)) {

        orgDetailsBoCriteria = criteriaBuilder.createQuery(OraganizationsInfoBO.class);
        orgDetailsBoRoot = orgDetailsBoCriteria.from(OraganizationsInfoBO.class);
        orgDetailsBoPredicates[0] =
            criteriaBuilder.equal(orgDetailsBoRoot.get("customOrgId"), orgId);
        orgDetailsBoCriteria.select(orgDetailsBoRoot).where(orgDetailsBoPredicates);
        orgDetailsBoList = session.createQuery(orgDetailsBoCriteria).getResultList();

        if (!orgDetailsBoList.isEmpty()) {
          orgDetailsBo = orgDetailsBoList.get(0);
          orgInfoId = orgDetailsBo.getOrgInfoId();
        }
      }
      appOrgInfoBean.setAppInfoId(appInfoId);
      appOrgInfoBean.setOrgInfoId(orgInfoId);

    } catch (Exception e) {
      appOrgInfoBean.setAppInfoId(appInfoId);
      appOrgInfoBean.setOrgInfoId(orgInfoId);
      logger.error("UserConsentManagementDaoImpl getUserAppDetailsByAllApi() - error ", e);
    }
    logger.info("UserConsentManagementDaoImpl getUserAppDetailsByAllApi() - Ends ");
    return appOrgInfoBean;
  }

  @Override
  public Integer getUserInfoDetails(String userId) {
    logger.info("UserProfileManagementDaoImpl getUserInfoDetails() - Ends ");
    CriteriaBuilder criteriaBuilder = null;
    Integer userDetailsId = null;
    CriteriaQuery<UserDetails> userDetailsCriteriaQuery = null;
    Root<UserDetails> userDetailsBoRoot = null;
    Predicate[] userDetailspredicates = new Predicate[1];
    List<UserDetails> userDetailsBoList = null;
    UserDetails userDetails = null;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      userDetailsCriteriaQuery = criteriaBuilder.createQuery(UserDetails.class);
      userDetailsBoRoot = userDetailsCriteriaQuery.from(UserDetails.class);
      userDetailspredicates[0] =
          criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.KEY_USERID), userId);
      userDetailsCriteriaQuery.select(userDetailsBoRoot).where(userDetailspredicates);
      userDetailsBoList = session.createQuery(userDetailsCriteriaQuery).getResultList();
      if (!userDetailsBoList.isEmpty()) {
        userDetails = userDetailsBoList.get(0);
        userDetailsId = userDetails.getUserDetailsId();
      }
    } catch (Exception e) {
      logger.error("UserProfileManagementDaoImpl getUserInfoDetails() - error ", e);
    }
    logger.info("UserProfileManagementDaoImpl getUserInfoDetails() - Ends ");
    return userDetailsId;
  }
}
