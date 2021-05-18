/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.bean.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class UserConsentManagementDaoImpl implements UserConsentManagementDao {

  private XLogger logger = XLoggerFactory.getXLogger(UserConsentManagementDaoImpl.class.getName());

  @Autowired private SessionFactory sessionFactory;

  @Autowired StudyRepository studyRepository;

  @Override
  public ParticipantStudyEntity getParticipantStudies(String studyId, String userId) {
    logger.entry("Begin getParticipantStudies() ");

    Session session = this.sessionFactory.getCurrentSession();

    ParticipantStudyEntity participantStudiesEntity = null;
    CriteriaBuilder criteriaBuilder = null;

    CriteriaQuery<StudyEntity> studiesBoCriteria = null;
    Root<StudyEntity> studiesBoRoot = null;
    Predicate[] studiesBoPredicates = new Predicate[1];

    StudyEntity studyInfo = null;

    Predicate[] predicates = new Predicate[2];
    List<ParticipantStudyEntity> participantStudiesBoList = null;

    CriteriaQuery<UserDetailsEntity> userDetailsBoCriteria = null;
    Root<UserDetailsEntity> userDetailsBoRoot = null;
    List<UserDetailsEntity> userDetailsBoList = null;
    Predicate[] userDetailspredicates = new Predicate[1];
    UserDetailsEntity userDetailsEntity = null;

    criteriaBuilder = session.getCriteriaBuilder();

    studiesBoCriteria = criteriaBuilder.createQuery(StudyEntity.class);
    studiesBoRoot = studiesBoCriteria.from(StudyEntity.class);
    studiesBoPredicates[0] = criteriaBuilder.equal(studiesBoRoot.get("id"), studyId);
    studiesBoCriteria.select(studiesBoRoot).where(studiesBoPredicates);
    List<StudyEntity> studiesBoList = session.createQuery(studiesBoCriteria).getResultList();
    CriteriaQuery<ParticipantStudyEntity> participantStudiesBoCriteria =
        criteriaBuilder.createQuery(ParticipantStudyEntity.class);
    Root<ParticipantStudyEntity> participantStudiesBoRoot =
        participantStudiesBoCriteria.from(ParticipantStudyEntity.class);

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
      participantStudiesBoList = session.createQuery(participantStudiesBoCriteria).getResultList();

      if (!participantStudiesBoList.isEmpty()) {
        participantStudiesEntity = participantStudiesBoList.get(0);
      }
    }
    logger.exit("getParticipantStudies() - Ends ");
    return participantStudiesEntity;
  }

  @Override
  public String saveParticipantStudies(List<ParticipantStudyEntity> participantStudiesList) {
    logger.entry("Begin saveParticipantStudies()");

    Session session = this.sessionFactory.getCurrentSession();

    String message = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
    CriteriaBuilder criteriaBuilder = null;
    CriteriaUpdate<ParticipantStudyEntity> criteriaUpdate = null;
    Integer isSaved = 0;
    int isUpdated = 0;

    for (ParticipantStudyEntity participantStudies : participantStudiesList) {
      if (participantStudies.getStudy() != null) {
        criteriaBuilder = session.getCriteriaBuilder();
        criteriaUpdate = criteriaBuilder.createCriteriaUpdate(ParticipantStudyEntity.class);
        Root<ParticipantStudyEntity> participantStudiesBoRoot =
            criteriaUpdate.from(ParticipantStudyEntity.class);
        criteriaUpdate.set("eligibility", participantStudies.getEligibility());
        criteriaUpdate.set("sharing", participantStudies.getSharing());
        criteriaUpdate.set("bookmark", participantStudies.getBookmark());
        criteriaUpdate.set("consentStatus", participantStudies.getConsentStatus());
        criteriaUpdate.set("completion", participantStudies.getCompletion());
        criteriaUpdate.set("adherence", participantStudies.getAdherence());
        criteriaUpdate.where(
            criteriaBuilder.equal(participantStudiesBoRoot.get("id"), participantStudies.getId()));
        isUpdated = session.createQuery(criteriaUpdate).executeUpdate();
      } else {
        isSaved = (Integer) session.save(participantStudies);
      }
    }
    if ((isUpdated > 0) || (isSaved > 0)) {
      message = MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue();
    }
    logger.exit("saveParticipantStudies() - Ends ");
    return message;
  }

  @Override
  public StudyConsentEntity getStudyConsent(String userId, String studyId, String consentVersion) {
    logger.entry("Begin getStudyConsent()");
    StudyConsentEntity studyConsent = null;

    Session session = this.sessionFactory.getCurrentSession();

    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<StudyConsentEntity> criteriaQuery = null;
    Predicate[] predicates;
    List<StudyConsentEntity> studyConsentBoList = null;
    CriteriaQuery<UserDetailsEntity> userDetailsBoCriteria = null;
    Root<UserDetailsEntity> userDetailsBoRoot = null;
    List<UserDetailsEntity> userDetailsBoList = null;
    Predicate[] userDetailspredicates = new Predicate[1];
    UserDetailsEntity userDetailsEntity = null;

    Optional<StudyEntity> optStudy = studyRepository.findById(studyId);

    criteriaBuilder = session.getCriteriaBuilder();
    userDetailsBoCriteria = criteriaBuilder.createQuery(UserDetailsEntity.class);
    userDetailsBoRoot = userDetailsBoCriteria.from(UserDetailsEntity.class);
    userDetailspredicates[0] =
        criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.KEY_USERID), userId);
    userDetailsBoCriteria.select(userDetailsBoRoot).where(userDetailspredicates);
    userDetailsBoList = session.createQuery(userDetailsBoCriteria).getResultList();
    if (!userDetailsBoList.isEmpty()) {
      userDetailsEntity = userDetailsBoList.get(0);
    }

    criteriaQuery = criteriaBuilder.createQuery(StudyConsentEntity.class);
    Root<StudyConsentEntity> studyConsentBoRoot = criteriaQuery.from(StudyConsentEntity.class);
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

    logger.exit("getStudyConsent() - Ends ");
    return studyConsent;
  }

  @Override
  public String saveStudyConsent(StudyConsentEntity studyConsent) {
    logger.entry("Begin saveStudyConsent()");

    Session session = this.sessionFactory.getCurrentSession();

    String addConsentMessage = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
    String isSaved = "";

    if (null != studyConsent) {
      studyConsent.setCreated(Timestamp.from(Instant.now()));
      isSaved = (String) session.save(studyConsent);
    }
    if (!StringUtils.isEmpty(isSaved)) {
      addConsentMessage = MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue();
    }
    logger.exit("saveStudyConsent() - Ends ");
    return addConsentMessage;
  }

  @Override
  public AppOrgInfoBean getUserAppDetailsByAllApi(String userId, String appId) {
    logger.entry("Begin validatedUserAppDetailsByAllApi()");

    Session session = this.sessionFactory.getCurrentSession();

    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<AppEntity> appDetailsBoCriteria = null;
    Root<AppEntity> appDetailsBoRoot = null;
    Predicate[] appDetailsPredicates = new Predicate[1];
    List<AppEntity> appDetailsList = null;
    AppEntity appEntity = null;

    AppOrgInfoBean appOrgInfoBean = new AppOrgInfoBean();
    String appInfoId = String.valueOf(0);

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

    logger.exit("getUserAppDetailsByAllApi() - Ends ");
    return appOrgInfoBean;
  }

  @Override
  public StudyEntity getStudyInfo(String customStudyId) {
    logger.entry("Begin getStudyInfo()");

    Session session = this.sessionFactory.getCurrentSession();

    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<StudyEntity> studyInfoBoCriteria = null;
    Root<StudyEntity> studyInfoBoRoot = null;
    Predicate[] studyInfoPredicates = new Predicate[1];
    List<StudyEntity> studyInfoList = null;
    StudyEntity studyEntity = null;
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
      }
    }
    logger.exit("getStudyInfo() - Ends ");
    return studyEntity;
  }

  @Override
  public String getUserDetailsId(String userId) {
    logger.entry("Begin getUserDetailsId()");

    Session session = this.sessionFactory.getCurrentSession();

    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<UserDetailsEntity> userDetailsBoCriteria = null;
    Root<UserDetailsEntity> userDetailsBoRoot = null;
    List<UserDetailsEntity> userDetailsBoList = null;
    Predicate[] userDetailspredicates = new Predicate[1];
    UserDetailsEntity userDetailsEntity = null;
    String userDetailsId = String.valueOf(0);
    criteriaBuilder = session.getCriteriaBuilder();

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
    logger.exit("getUserDetailsId() - Ends ");
    return userDetailsId;
  }
}
