/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.bean.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.bean.StudyInfoBean;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.OrgInfoEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyConsentEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.repository.StudyRepository;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.MyStudiesUserRegUtil;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
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

@Repository
public class UserConsentManagementDaoImpl implements UserConsentManagementDao {

  private static final Logger logger = LoggerFactory.getLogger(UserConsentManagementDaoImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Autowired StudyRepository studyRepository;

  @Override
  public ParticipantStudyEntity getParticipantStudies(String studyId, String userId) {
    logger.info("UserConsentManagementDaoImpl getParticipantStudies() - Started ");
    ParticipantStudyEntity participantStudiesEntity = null;
    CriteriaBuilder criteriaBuilder = null;

    CriteriaQuery<StudyEntity> studiesBoCriteria = null;
    Root<StudyEntity> studiesBoRoot = null;
    Predicate[] studiesBoPredicates = new Predicate[1];

    List<StudyEntity> studiesBoList = null;
    StudyEntity studyInfo = null;

    CriteriaQuery<ParticipantStudyEntity> participantStudiesBoCriteria = null;
    Root<ParticipantStudyEntity> participantStudiesBoRoot = null;
    Predicate[] predicates = new Predicate[2];
    List<ParticipantStudyEntity> participantStudiesBoList = null;

    CriteriaQuery<UserDetailsEntity> userDetailsBoCriteria = null;
    Root<UserDetailsEntity> userDetailsBoRoot = null;
    List<UserDetailsEntity> userDetailsBoList = null;
    Predicate[] userDetailspredicates = new Predicate[1];
    UserDetailsEntity userDetailsEntity = null;

    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();

      studiesBoCriteria = criteriaBuilder.createQuery(StudyEntity.class);
      studiesBoRoot = studiesBoCriteria.from(StudyEntity.class);
      studiesBoPredicates[0] = criteriaBuilder.equal(studiesBoRoot.get("id"), studyId);
      studiesBoCriteria.select(studiesBoRoot).where(studiesBoPredicates);
      studiesBoList = session.createQuery(studiesBoCriteria).getResultList();
      participantStudiesBoCriteria = criteriaBuilder.createQuery(ParticipantStudyEntity.class);
      participantStudiesBoRoot = participantStudiesBoCriteria.from(ParticipantStudyEntity.class);

      if (!StringUtils.isEmpty(userId)) {
        userDetailsBoCriteria = criteriaBuilder.createQuery(UserDetailsEntity.class);
        userDetailsBoRoot = userDetailsBoCriteria.from(UserDetailsEntity.class);
        userDetailspredicates[0] =
            criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.KEY_USERID), userId);
        userDetailsBoCriteria.select(userDetailsBoRoot).where(userDetailspredicates);
        userDetailsBoList = session.createQuery(userDetailsBoCriteria).getResultList();

        if (!userDetailsBoList.isEmpty() && !studiesBoList.isEmpty()) {
          userDetailsEntity = userDetailsBoList.get(0);
          studyInfo = studiesBoList.get(0);
          predicates[0] =
              criteriaBuilder.equal(participantStudiesBoRoot.get("userDetails"), userDetailsEntity);
          predicates[1] = criteriaBuilder.equal(participantStudiesBoRoot.get("study"), studyInfo);
          participantStudiesBoCriteria.select(participantStudiesBoRoot).where(predicates);
          participantStudiesBoList =
              session.createQuery(participantStudiesBoCriteria).getResultList();

          if (!participantStudiesBoList.isEmpty()) {
            participantStudiesEntity = participantStudiesBoList.get(0);
          }
        }
      }
    } catch (Exception e) {
      logger.error("UserConsentManagementDaoImpl getParticipantStudies() - error ", e);
    }
    logger.info("UserConsentManagementDaoImpl getParticipantStudies() - Ends ");
    return participantStudiesEntity;
  }

  @Override
  public String saveParticipantStudies(List<ParticipantStudyEntity> participantStudiesList) {
    logger.info("UserConsentManagementDaoImpl saveParticipantStudies() - Started ");
    String message = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
    Transaction transaction = null;
    CriteriaBuilder criteriaBuilder = null;
    CriteriaUpdate<ParticipantStudyEntity> criteriaUpdate = null;
    Root<ParticipantStudyEntity> participantStudiesBoRoot = null;
    Integer isSaved = 0;
    int isUpdated = 0;

    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {

      transaction = session.beginTransaction();

      for (ParticipantStudyEntity participantStudies : participantStudiesList) {
        if (participantStudies.getStudy() != null) {
          criteriaBuilder = session.getCriteriaBuilder();
          criteriaUpdate = criteriaBuilder.createCriteriaUpdate(ParticipantStudyEntity.class);
          participantStudiesBoRoot = criteriaUpdate.from(ParticipantStudyEntity.class);
          criteriaUpdate.set("eligibility", participantStudies.getEligibility());
          criteriaUpdate.set("sharing", participantStudies.getSharing());
          criteriaUpdate.set("bookmark", participantStudies.getBookmark());
          criteriaUpdate.set("consentStatus", participantStudies.getConsentStatus());
          criteriaUpdate.set("completion", participantStudies.getCompletion());
          criteriaUpdate.set("adherence", participantStudies.getAdherence());
          criteriaUpdate.where(
              criteriaBuilder.equal(
                  participantStudiesBoRoot.get("id"), participantStudies.getId()));
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
  public StudyConsentEntity getStudyConsent(String userId, String studyId, String consentVersion) {
    logger.info("UserConsentManagementDaoImpl getStudyConsent() - Started ");
    StudyConsentEntity studyConsent = null;
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<StudyConsentEntity> criteriaQuery = null;
    Root<StudyConsentEntity> studyConsentBoRoot = null;
    Predicate[] predicates;
    List<StudyConsentEntity> studyConsentBoList = null;
    CriteriaQuery<UserDetailsEntity> userDetailsBoCriteria = null;
    Root<UserDetailsEntity> userDetailsBoRoot = null;
    List<UserDetailsEntity> userDetailsBoList = null;
    Predicate[] userDetailspredicates = new Predicate[1];
    UserDetailsEntity userDetailsEntity = null;

    Optional<StudyEntity> optStudy = studyRepository.findById(studyId);

    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      if (!StringUtils.isEmpty(userId)) {
        userDetailsBoCriteria = criteriaBuilder.createQuery(UserDetailsEntity.class);
        userDetailsBoRoot = userDetailsBoCriteria.from(UserDetailsEntity.class);
        userDetailspredicates[0] =
            criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.KEY_USERID), userId);
        userDetailsBoCriteria.select(userDetailsBoRoot).where(userDetailspredicates);
        userDetailsBoList = session.createQuery(userDetailsBoCriteria).getResultList();
        if (!userDetailsBoList.isEmpty()) {
          userDetailsEntity = userDetailsBoList.get(0);
        }
      }
      criteriaQuery = criteriaBuilder.createQuery(StudyConsentEntity.class);
      studyConsentBoRoot = criteriaQuery.from(StudyConsentEntity.class);
      if ((consentVersion != null) && !StringUtils.isEmpty(consentVersion)) {
        predicates = new Predicate[3];
        predicates[0] =
            criteriaBuilder.equal(studyConsentBoRoot.get("userDetails"), userDetailsEntity);
        if (optStudy.isPresent()) {
          predicates[1] = criteriaBuilder.equal(studyConsentBoRoot.get("study"), optStudy.get());
        }
        predicates[2] = criteriaBuilder.equal(studyConsentBoRoot.get("version"), consentVersion);
      } else {
        predicates = new Predicate[2];
        predicates[0] =
            criteriaBuilder.equal(studyConsentBoRoot.get("userDetails"), userDetailsEntity);
        if (optStudy.isPresent()) {
          predicates[1] = criteriaBuilder.equal(studyConsentBoRoot.get("study"), optStudy.get());
        }
      }
      criteriaQuery.select(studyConsentBoRoot).where(predicates);
      if ((consentVersion != null) && !StringUtils.isEmpty(consentVersion)) {
        studyConsentBoList = session.createQuery(criteriaQuery).getResultList();
      } else {
        criteriaQuery.orderBy(criteriaBuilder.desc(studyConsentBoRoot.get("created")));
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
  public String saveStudyConsent(StudyConsentEntity studyConsent) {
    logger.info("UserConsentManagementDaoImpl saveStudyConsent() - Started ");
    String addOrUpdateConsentMessage = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
    Transaction transaction = null;
    CriteriaBuilder criteriaBuilder = null;
    CriteriaUpdate<StudyConsentEntity> criteriaUpdate = null;
    Root<StudyConsentEntity> studyConsentBoRoot = null;
    Predicate[] predicates = new Predicate[1];
    List<StudyConsentEntity> studyConsentBoList = null;
    Integer isUpdated = 0;
    String isSaved = "";

    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      transaction = session.beginTransaction();
      if (null != studyConsent) {
        if (studyConsent.getId() != null) {
          criteriaBuilder = session.getCriteriaBuilder();
          criteriaUpdate = criteriaBuilder.createCriteriaUpdate(StudyConsentEntity.class);
          studyConsentBoRoot = criteriaUpdate.from(StudyConsentEntity.class);
          criteriaUpdate.set("version", studyConsent.getVersion());
          criteriaUpdate.set("status", studyConsent.getStatus());
          criteriaUpdate.set("pdf", studyConsent.getPdf());
          criteriaUpdate.set("pdfPath", studyConsent.getPdfPath());
          criteriaUpdate.set("pdfStorage", studyConsent.getPdfStorage());
          criteriaUpdate.where(
              criteriaBuilder.equal(studyConsentBoRoot.get("id"), studyConsent.getId()));
          isUpdated = session.createQuery(criteriaUpdate).executeUpdate();
        } else {
          studyConsent.setCreated(Timestamp.from(Instant.now()));
          isSaved = (String) session.save(studyConsent);
        }
      }
      if ((isUpdated > 0) || (!StringUtils.isEmpty(isSaved))) {
        addOrUpdateConsentMessage = MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue();
      }
      transaction.commit();
    } catch (Exception e) {
      logger.error("UserConsentManagementDaoImpl saveStudyConsent() - error ", e);
      addOrUpdateConsentMessage = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
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
    CriteriaQuery<AppEntity> appDetailsBoCriteria = null;
    Root<AppEntity> appDetailsBoRoot = null;
    Predicate[] appDetailsPredicates = new Predicate[1];
    List<AppEntity> appDetailsList = null;
    AppEntity appEntity = null;

    CriteriaQuery<OrgInfoEntity> orgDetailsBoCriteria = null;
    Root<OrgInfoEntity> orgDetailsBoRoot = null;
    Predicate[] orgDetailsBoPredicates = new Predicate[1];
    List<OrgInfoEntity> orgDetailsBoList = null;
    OrgInfoEntity orgDetailsBo = null;
    AppOrgInfoBean appOrgInfoBean = new AppOrgInfoBean();
    String message = "";
    String appInfoId = String.valueOf(0);
    String orgInfoId = String.valueOf(0);

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
      if (!StringUtils.isEmpty(orgId)) {

        orgDetailsBoCriteria = criteriaBuilder.createQuery(OrgInfoEntity.class);
        orgDetailsBoRoot = orgDetailsBoCriteria.from(OrgInfoEntity.class);
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
    CriteriaQuery<StudyEntity> studyInfoBoCriteria = null;
    Root<StudyEntity> studyInfoBoRoot = null;
    Predicate[] studyInfoPredicates = new Predicate[1];
    List<StudyEntity> studyInfoList = null;
    StudyEntity studyEntity = null;
    String studyInfoId = String.valueOf(0);
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      if (!StringUtils.isEmpty(customStudyId)) {
        studyInfoBoCriteria = criteriaBuilder.createQuery(StudyEntity.class);
        studyInfoBoRoot = studyInfoBoCriteria.from(StudyEntity.class);
        studyInfoPredicates[0] =
            criteriaBuilder.equal(studyInfoBoRoot.get("customId"), customStudyId);
        studyInfoBoCriteria.select(studyInfoBoRoot).where(studyInfoPredicates);
        studyInfoList = session.createQuery(studyInfoBoCriteria).getResultList();
        if (!studyInfoList.isEmpty()) {
          studyEntity = studyInfoList.get(0);
          studyInfoId = studyEntity.getId();
        }
        if (!StringUtils.isEmpty(studyInfoId)) {
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
  public String getUserDetailsId(String userId) {
    logger.info("UserConsentManagementDaoImpl getStudyInfoId() - Starts ");
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
        userDetailspredicates[0] =
            criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.KEY_USERID), userId);
        userDetailsBoCriteria.select(userDetailsBoRoot).where(userDetailspredicates);
        userDetailsBoList = session.createQuery(userDetailsBoCriteria).getResultList();
        if (!userDetailsBoList.isEmpty()) {
          userDetailsEntity = userDetailsBoList.get(0);
          userDetailsId = userDetailsEntity.getId();
        }
      }
    } catch (Exception e) {
      userDetailsId = String.valueOf(0);
      logger.error("UserProfileManagementDaoImpl validatedUserAppDetailsByAllApi() - error ", e);
    }
    logger.info("UserConsentManagementDaoImpl getStudyInfoId() - Ends ");
    return userDetailsId;
  }
}
