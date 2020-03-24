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
import javax.persistence.criteria.CriteriaUpdate;
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
import com.google.cloud.healthcare.fdamystudies.bean.StudyInfoBean;
import com.google.cloud.healthcare.fdamystudies.model.AppInfoDetailsBO;
import com.google.cloud.healthcare.fdamystudies.model.OrgInfo;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.model.StudyConsentBO;
import com.google.cloud.healthcare.fdamystudies.model.StudyInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.MyStudiesUserRegUtil;

@Repository
public class UserConsentManagementDaoImpl implements UserConsentManagementDao {

  private static final Logger logger = LoggerFactory.getLogger(UserConsentManagementDaoImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Override
  public ParticipantStudiesBO getParticipantStudies(Integer studyId, String userId) {
    logger.info("UserConsentManagementDaoImpl getParticipantStudies() - Started ");
    ParticipantStudiesBO participantStudiesBO = null;
    CriteriaBuilder criteriaBuilder = null;

    CriteriaQuery<StudyInfoBO> studiesBoCriteria = null;
    Root<StudyInfoBO> studiesBoRoot = null;
    Predicate[] studiesBoPredicates = new Predicate[1];

    List<StudyInfoBO> studiesBoList = null;
    StudyInfoBO studyInfo = null;

    CriteriaQuery<ParticipantStudiesBO> participantStudiesBoCriteria = null;
    Root<ParticipantStudiesBO> participantStudiesBoRoot = null;
    Predicate[] predicates = new Predicate[2];
    List<ParticipantStudiesBO> participantStudiesBoList = null;

    CriteriaQuery<UserDetailsBO> userDetailsBoCriteria = null;
    Root<UserDetailsBO> userDetailsBoRoot = null;
    List<UserDetailsBO> userDetailsBoList = null;
    Predicate[] userDetailspredicates = new Predicate[1];
    UserDetailsBO userDetailsBO = null;

    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();

      studiesBoCriteria = criteriaBuilder.createQuery(StudyInfoBO.class);
      studiesBoRoot = studiesBoCriteria.from(StudyInfoBO.class);
      studiesBoPredicates[0] = criteriaBuilder.equal(studiesBoRoot.get("id"), studyId);
      studiesBoCriteria.select(studiesBoRoot).where(studiesBoPredicates);
      studiesBoList = session.createQuery(studiesBoCriteria).getResultList();
      participantStudiesBoCriteria = criteriaBuilder.createQuery(ParticipantStudiesBO.class);
      participantStudiesBoRoot = participantStudiesBoCriteria.from(ParticipantStudiesBO.class);

      if (!StringUtils.isEmpty(userId)) {
        userDetailsBoCriteria = criteriaBuilder.createQuery(UserDetailsBO.class);
        userDetailsBoRoot = userDetailsBoCriteria.from(UserDetailsBO.class);
        userDetailspredicates[0] =
            criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.KEY_USERID), userId);
        userDetailsBoCriteria.select(userDetailsBoRoot).where(userDetailspredicates);
        userDetailsBoList = session.createQuery(userDetailsBoCriteria).getResultList();

        if (!userDetailsBoList.isEmpty() && !studiesBoList.isEmpty()) {
          userDetailsBO = userDetailsBoList.get(0);
          studyInfo = studiesBoList.get(0);
          predicates[0] =
              criteriaBuilder.equal(participantStudiesBoRoot.get("userDetails"), userDetailsBO);
          predicates[1] =
              criteriaBuilder.equal(participantStudiesBoRoot.get("studyInfo"), studyInfo);
          participantStudiesBoCriteria.select(participantStudiesBoRoot).where(predicates);
          participantStudiesBoList =
              session.createQuery(participantStudiesBoCriteria).getResultList();

          if (!participantStudiesBoList.isEmpty()) {
            participantStudiesBO = participantStudiesBoList.get(0);
          }
        }
      }
    } catch (Exception e) {
      logger.error("UserConsentManagementDaoImpl getParticipantStudies() - error ", e);
    }
    logger.info("UserConsentManagementDaoImpl getParticipantStudies() - Ends ");
    return participantStudiesBO;
  }

  @Override
  public String saveParticipantStudies(List<ParticipantStudiesBO> participantStudiesList) {
    logger.info("UserConsentManagementDaoImpl saveParticipantStudies() - Started ");
    String message = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
    Transaction transaction = null;
    CriteriaBuilder criteriaBuilder = null;
    CriteriaUpdate<ParticipantStudiesBO> criteriaUpdate = null;
    Root<ParticipantStudiesBO> participantStudiesBoRoot = null;
    Integer isSaved = 0;
    int isUpdated = 0;

    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {

      transaction = session.beginTransaction();

      for (ParticipantStudiesBO participantStudies : participantStudiesList) {
        if (participantStudies.getParticipantStudyInfoId() != null) {
          criteriaBuilder = session.getCriteriaBuilder();
          criteriaUpdate = criteriaBuilder.createCriteriaUpdate(ParticipantStudiesBO.class);
          participantStudiesBoRoot = criteriaUpdate.from(ParticipantStudiesBO.class);
          criteriaUpdate.set("eligibility", participantStudies.getEligibility());
          criteriaUpdate.set("sharing", participantStudies.getSharing());
          criteriaUpdate.set("bookmark", participantStudies.getBookmark());
          criteriaUpdate.set("consentStatus", participantStudies.getConsentStatus());
          criteriaUpdate.set("completion", participantStudies.getCompletion());
          criteriaUpdate.set("adherence", participantStudies.getAdherence());
          criteriaUpdate.where(
              criteriaBuilder.equal(
                  participantStudiesBoRoot.get("participantStudyInfoId"),
                  participantStudies.getParticipantStudyInfoId()));
          isUpdated = session.createQuery(criteriaUpdate).executeUpdate();
        } else {
          isSaved = (Integer) session.save(participantStudies);
        }
      }
      if ((isUpdated > 0) || (isSaved > 0)) {
        message = MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue();
      }
      transaction.commit();
    } catch (Exception e) {
      logger.error("UserConsentManagementDaoImpl saveParticipantStudies() - error ", e);
      if (transaction != null) {
        try {
          transaction.rollback();
        } catch (Exception e1) {
          logger.error(
              "UserConsentManagementDaoImpl - saveParticipantStudies() - error rollback", e1);
        }
      }
    }
    logger.info("UserConsentManagementDaoImpl saveParticipantStudies() - Ends ");
    return message;
  }

  @Override
  public StudyConsentBO getStudyConsent(String userId, Integer studyId, String consentVersion) {
    logger.info("UserConsentManagementDaoImpl getStudyConsent() - Started ");
    StudyConsentBO studyConsent = null;
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<StudyConsentBO> criteriaQuery = null;
    Root<StudyConsentBO> studyConsentBoRoot = null;
    Predicate[] predicates;
    List<StudyConsentBO> studyConsentBoList = null;
    CriteriaQuery<UserDetailsBO> userDetailsBoCriteria = null;
    Root<UserDetailsBO> userDetailsBoRoot = null;
    List<UserDetailsBO> userDetailsBoList = null;
    Predicate[] UserDetailspredicates = new Predicate[1];
    UserDetailsBO userDetailsBO = null;
    Integer userDetailsId = 0;

    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      if (!StringUtils.isEmpty(userId)) {
        userDetailsBoCriteria = criteriaBuilder.createQuery(UserDetailsBO.class);
        userDetailsBoRoot = userDetailsBoCriteria.from(UserDetailsBO.class);
        UserDetailspredicates[0] =
            criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.KEY_USERID), userId);
        userDetailsBoCriteria.select(userDetailsBoRoot).where(UserDetailspredicates);
        userDetailsBoList = session.createQuery(userDetailsBoCriteria).getResultList();
        if (!userDetailsBoList.isEmpty()) {
          userDetailsBO = userDetailsBoList.get(0);
          userDetailsId = userDetailsBO.getUserDetailsId();
        }
      }
      criteriaQuery = criteriaBuilder.createQuery(StudyConsentBO.class);
      studyConsentBoRoot = criteriaQuery.from(StudyConsentBO.class);
      if ((consentVersion != null) && !StringUtils.isEmpty(consentVersion)) {
        predicates = new Predicate[3];
        predicates[0] =
            criteriaBuilder.equal(studyConsentBoRoot.get(AppConstants.KEY_USERID), userDetailsId);
        predicates[1] =
            criteriaBuilder.equal(studyConsentBoRoot.get(AppConstants.STUDY_INFO_ID), studyId);
        predicates[2] = criteriaBuilder.equal(studyConsentBoRoot.get("version"), consentVersion);
      } else {
        predicates = new Predicate[2];
        predicates[0] =
            criteriaBuilder.equal(studyConsentBoRoot.get(AppConstants.KEY_USERID), userDetailsId);
        predicates[1] =
            criteriaBuilder.equal(studyConsentBoRoot.get(AppConstants.STUDY_INFO_ID), studyId);
      }
      criteriaQuery.select(studyConsentBoRoot).where(predicates);
      if ((consentVersion != null) && !StringUtils.isEmpty(consentVersion)) {
        studyConsentBoList = session.createQuery(criteriaQuery).getResultList();
      } else {
        criteriaQuery.orderBy(criteriaBuilder.desc(studyConsentBoRoot.get("ts")));
        studyConsentBoList = session.createQuery(criteriaQuery).setMaxResults(1).getResultList();
      }
      if (!studyConsentBoList.isEmpty()) {
        studyConsent = studyConsentBoList.get(0);
      }
    } catch (Exception e) {
      logger.error("UserConsentManagementDaoImpl getStudyConsent() - error ", e);
    }
    logger.info("UserConsentManagementDaoImpl getStudyConsent() - Ends ");
    return studyConsent;
  }

  @Override
  public String saveStudyConsent(StudyConsentBO studyConsent) {
    logger.info("UserConsentManagementDaoImpl saveStudyConsent() - Started ");
    String addOrUpdateConsentMessage = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
    Transaction transaction = null;
    CriteriaBuilder criteriaBuilder = null;
    CriteriaUpdate<StudyConsentBO> criteriaUpdate = null;
    Root<StudyConsentBO> studyConsentBoRoot = null;
    Predicate[] predicates = new Predicate[1];
    List<StudyConsentBO> studyConsentBoList = null;
    Integer isUpdated = 0;
    Integer isSaved = 0;

    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      transaction = session.beginTransaction();
      if (null != studyConsent) {
        if (studyConsent.getId() != null) {
          criteriaBuilder = session.getCriteriaBuilder();
          criteriaUpdate = criteriaBuilder.createCriteriaUpdate(StudyConsentBO.class);
          studyConsentBoRoot = criteriaUpdate.from(StudyConsentBO.class);
          criteriaUpdate.set("version", studyConsent.getVersion());
          criteriaUpdate.set("status", studyConsent.getStatus());
          criteriaUpdate.set("pdf", studyConsent.getPdf());
          criteriaUpdate.set("pdfPath", studyConsent.getPdfPath());
          criteriaUpdate.set("pdfStorage", studyConsent.getPdfStorage());
          criteriaUpdate.where(
              criteriaBuilder.equal(studyConsentBoRoot.get("id"), studyConsent.getId()));
          isUpdated = session.createQuery(criteriaUpdate).executeUpdate();
        } else {
          studyConsent.setTs(
              MyStudiesUserRegUtil.getCurrentDateTime(AppConstants.SDF_DATE_TIME_FORMAT));
          isSaved = (Integer) session.save(studyConsent);
        }
      }
      if ((isUpdated > 0) || (isSaved > 0)) {
        addOrUpdateConsentMessage = MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue();
      }
      transaction.commit();
    } catch (Exception e) {
      logger.error("UserConsentManagementDaoImpl saveStudyConsent() - error ", e);
      if (transaction != null) {
        try {
          transaction.rollback();
        } catch (Exception e1) {
          logger.error("UserConsentManagementDaoImpl - saveStudyConsent() - error rollback", e1);
        }
      }
    }
    logger.info("UserConsentManagementDaoImpl saveStudyConsent() - Ends ");
    return addOrUpdateConsentMessage;
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

    CriteriaQuery<OrgInfo> orgDetailsBoCriteria = null;
    Root<OrgInfo> orgDetailsBoRoot = null;
    Predicate[] orgDetailsBoPredicates = new Predicate[1];
    List<OrgInfo> orgDetailsBoList = null;
    OrgInfo orgDetailsBo = null;
    AppOrgInfoBean appOrgInfoBean = new AppOrgInfoBean();
    String message = "";
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
      logger.error("UserConsentManagementDaoImpl getUserAppDetailsByAllApi() - error ", e);
    }
    logger.info("UserConsentManagementDaoImpl getUserAppDetailsByAllApi() - Ends ");
    return appOrgInfoBean;
  }

  @Override
  public StudyInfoBean getStudyInfoId(String customStudyId) {
    logger.info("UserConsentManagementDaoImpl getStudyInfoId() - Starts ");
    StudyInfoBean studyInfoBean = new StudyInfoBean();
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<StudyInfoBO> studyInfoBoCriteria = null;
    Root<StudyInfoBO> studyInfoBoRoot = null;
    Predicate[] studyInfoPredicates = new Predicate[1];
    List<StudyInfoBO> studyInfoList = null;
    StudyInfoBO studyInfoBO = null;
    Integer studyInfoId = 0;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      if (!StringUtils.isEmpty(customStudyId)) {
        studyInfoBoCriteria = criteriaBuilder.createQuery(StudyInfoBO.class);
        studyInfoBoRoot = studyInfoBoCriteria.from(StudyInfoBO.class);
        studyInfoPredicates[0] =
            criteriaBuilder.equal(studyInfoBoRoot.get("customId"), customStudyId);
        studyInfoBoCriteria.select(studyInfoBoRoot).where(studyInfoPredicates);
        studyInfoList = session.createQuery(studyInfoBoCriteria).getResultList();
        if (!studyInfoList.isEmpty()) {
          studyInfoBO = studyInfoList.get(0);
          studyInfoId = studyInfoBO.getId();
        }
        if (studyInfoId != 0) {
          studyInfoBean.setStudyInfoId(studyInfoId);
        }
      }
    } catch (Exception e) {
      studyInfoBean.setStudyInfoId(studyInfoId);
      logger.error("UserProfileManagementDaoImpl validatedUserAppDetailsByAllApi() - error ", e);
    }
    logger.info("UserConsentManagementDaoImpl getStudyInfoId() - Ends ");
    return studyInfoBean;
  }

  @Override
  public Integer getUserDetailsId(String userId) {
    logger.info("UserConsentManagementDaoImpl getStudyInfoId() - Starts ");
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<UserDetailsBO> userDetailsBoCriteria = null;
    Root<UserDetailsBO> userDetailsBoRoot = null;
    List<UserDetailsBO> userDetailsBoList = null;
    Predicate[] userDetailspredicates = new Predicate[1];
    UserDetailsBO userDetailsBO = null;
    Integer userDetailsId = 0;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
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
