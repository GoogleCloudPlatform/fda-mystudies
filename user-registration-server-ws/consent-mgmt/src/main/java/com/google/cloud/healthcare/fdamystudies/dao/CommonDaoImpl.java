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
import com.google.cloud.healthcare.fdamystudies.bean.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.model.AppInfoDetailsBO;
import com.google.cloud.healthcare.fdamystudies.model.OraganizationsInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;

@Repository
public class CommonDaoImpl implements CommonDao {

  private static Logger logger = LoggerFactory.getLogger(CommonDaoImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Override
  public AppOrgInfoBean getUserAppDetailsByAllApi(String userId, String appId, String orgId) {
    logger.info("UserConsentManagementDaoImpl validatedUserAppDetailsByAllApi() - Started ");
    Transaction transaction = null;
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
    String message = "";
    int appInfoId = 0;
    int orgInfoId = 0;

    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      //      transaction = session.beginTransaction();
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
  public Integer getUserDetailsId(String userId) {
    logger.info("UserConsentManagementDaoImpl getStudyInfoId() - Starts ");
    Transaction transaction = null;
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<UserDetailsBO> userDetailsBoCriteria = null;
    Root<UserDetailsBO> userDetailsBoRoot = null;
    List<UserDetailsBO> userDetailsBoList = null;
    Predicate[] userDetailspredicates = new Predicate[1];
    UserDetailsBO userDetailsBO = null;
    Integer userDetailsId = 0;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      //      transaction = session.beginTransaction();
      criteriaBuilder = session.getCriteriaBuilder();
      if (!StringUtils.isEmpty(userId)) {

        userDetailsBoCriteria = criteriaBuilder.createQuery(UserDetailsBO.class);
        userDetailsBoRoot = userDetailsBoCriteria.from(UserDetailsBO.class);

        userDetailspredicates[0] =
            criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.KEY_USERID), userId);
        userDetailsBoCriteria.select(userDetailsBoRoot).where(userDetailspredicates);
        userDetailsBoList = session.createQuery(userDetailsBoCriteria).getResultList();

        if (!userDetailsBoList.isEmpty()) {
          userDetailsBO = userDetailsBoList.get(0);
          userDetailsId = userDetailsBO.getUserDetailsId();
        }
      }
    } catch (Exception e) {
      userDetailsId = 0;
      logger.error("UserProfileManagementDaoImpl validatedUserAppDetailsByAllApi() - error ", e);
    }

    logger.info("UserConsentManagementDaoImpl getStudyInfoId() - Ends ");
    return userDetailsId;
  }
}
