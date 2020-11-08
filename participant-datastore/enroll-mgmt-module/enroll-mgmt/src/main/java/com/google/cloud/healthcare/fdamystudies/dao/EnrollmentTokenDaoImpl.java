/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.beans.EnrollmentResponseBean;
import com.google.cloud.healthcare.fdamystudies.common.OnboardingStatus;
import com.google.cloud.healthcare.fdamystudies.common.ParticipantStudyStateStatus;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class EnrollmentTokenDaoImpl implements EnrollmentTokenDao {

  private static final Logger logger = LoggerFactory.getLogger(EnrollmentTokenDaoImpl.class);

  @Autowired private SessionFactory sessionFactory;

  @Override
  public boolean studyExists(@NotNull String studyId) {
    logger.info("EnrollmentTokenDaoImpl studyExists() - Started ");
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<StudyEntity> studyInfoBoCriteria = null;
    Root<StudyEntity> studyInfoRoot = null;
    Predicate[] predicates = new Predicate[1];
    List<StudyEntity> studyInfoList = null;
    StudyEntity studyEntity = null;
    boolean isStudyExist = false;
    Session session = this.sessionFactory.getCurrentSession();
    criteriaBuilder = session.getCriteriaBuilder();
    studyInfoBoCriteria = criteriaBuilder.createQuery(StudyEntity.class);
    studyInfoRoot = studyInfoBoCriteria.from(StudyEntity.class);
    predicates[0] = criteriaBuilder.equal(studyInfoRoot.get("customId"), studyId);
    studyInfoBoCriteria.select(studyInfoRoot).where(predicates);
    studyInfoList = session.createQuery(studyInfoBoCriteria).getResultList();

    if (!studyInfoList.isEmpty()) {
      studyEntity = studyInfoList.get(0);
    }
    if (studyEntity != null) {
      isStudyExist = true;
    }

    logger.info("EnrollmentTokenDaoImpl studyExists() - Ends ");
    return isStudyExist;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean isValidStudyToken(
      @NotNull String token, @NotNull String studyId, @NotNull String email) {
    logger.info("EnrollmentTokenDaoImpl isValidStudyToken() - Started ");
    List<ParticipantRegistrySiteEntity> participantRegistrySite = null;
    ParticipantRegistrySiteEntity participantRegistrySiteDetails = null;
    boolean isValidStudyToken = false;
    Session session = this.sessionFactory.getCurrentSession();
    participantRegistrySite =
        session
            .createQuery(
                "from ParticipantRegistrySiteEntity PS where study.customId =:studyId and"
                    + " upper(trim(enrollmentToken))=:token and email=:email and"
                    + " onboardingStatus != :onboardingStatus")
            .setParameter("studyId", studyId)
            .setParameter("token", token.toUpperCase())
            .setParameter("email", email)
            .setParameter("onboardingStatus", "D")
            .getResultList();

    if (participantRegistrySite != null && !participantRegistrySite.isEmpty()) {
      participantRegistrySiteDetails = participantRegistrySite.get(0);
    }
    if (participantRegistrySiteDetails != null) {
      isValidStudyToken = true;
    }

    logger.info("EnrollmentTokenDaoImpl isValidStudyToken() - Ends");
    return isValidStudyToken;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean hasParticipant(@NotNull String studyId, @NotNull String tokenValue) {
    logger.info("EnrollmentTokenDaoImpl hasParticipant() - Started ");
    List<Object[]> participantList = null;
    Session session = this.sessionFactory.getCurrentSession();
    List<String> studyStateStatus = new ArrayList<>();
    studyStateStatus.add(ParticipantStudyStateStatus.ENROLLED.getValue());
    studyStateStatus.add(ParticipantStudyStateStatus.WITHDRAWN.getValue());
    studyStateStatus.add(ParticipantStudyStateStatus.INPROGRESS.getValue());

    List<String> onboardingStatus = new ArrayList<>();
    onboardingStatus.add(OnboardingStatus.INVITED.getCode());
    onboardingStatus.add(OnboardingStatus.ENROLLED.getCode());
    onboardingStatus.add(OnboardingStatus.DISABLED.getCode());
    participantList =
        session
            .createQuery(
                "from ParticipantStudyEntity PS,StudyEntity SB, ParticipantRegistrySiteEntity PR"
                    + " where SB.id =PS.study.id and PS.participantRegistrySite.id=PR.id"
                    + " and PS.status in (:studyStateStatus) "
                    + " and PR.onboardingStatus in (:onboardingStatus)"
                    + " and upper(trim(PR.enrollmentToken))=:token and SB.customId=:studyId")
            .setParameter("studyStateStatus", studyStateStatus)
            .setParameter("onboardingStatus", onboardingStatus)
            .setParameter("token", tokenValue.toUpperCase())
            .setParameter("studyId", studyId)
            .getResultList();

    logger.info("EnrollmentTokenDaoImpl hasParticipant() - Ends ");
    return participantList != null && !participantList.isEmpty();
  }

  @Override
  public boolean enrollmentTokenRequired(@NotNull String studyId) {
    logger.info("EnrollmentTokenDaoImpl enrollmentTokenRequired() - Started ");
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<StudyEntity> studyEntityCriteria = null;
    Root<StudyEntity> studyEntityRoot = null;
    Predicate[] predicates = new Predicate[1];
    List<StudyEntity> studyList = null;
    StudyEntity studyEntity = null;
    boolean isTokenRequired = false;

    Session session = this.sessionFactory.getCurrentSession();
    criteriaBuilder = session.getCriteriaBuilder();
    studyEntityCriteria = criteriaBuilder.createQuery(StudyEntity.class);
    studyEntityRoot = studyEntityCriteria.from(StudyEntity.class);
    predicates[0] = criteriaBuilder.equal(studyEntityRoot.get("customId"), studyId);
    studyEntityCriteria.select(studyEntityRoot).where(predicates);
    studyList = session.createQuery(studyEntityCriteria).getResultList();
    if (!studyList.isEmpty()) {
      studyEntity = studyList.get(0);
    }
    if (studyEntity != null && studyEntity.getType().equalsIgnoreCase(AppConstants.CLOSE_STUDY)) {
      isTokenRequired = true;
    }

    logger.info("EnrollmentTokenDaoImpl enrollmentTokenRequired() - Ends ");
    return isTokenRequired;
  }

  @SuppressWarnings("unchecked")
  @Override
  public EnrollmentResponseBean enrollParticipant(
      @NotNull String studyId,
      String tokenValue,
      UserDetailsEntity userDetail,
      boolean isTokenRequired,
      String participantid) {
    logger.info("EnrollmentTokenDaoImpl enrollParticipant() - Started ");
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<StudyEntity> studyEntityCriteria = null;
    Root<StudyEntity> studyEntityRoot = null;
    Predicate[] predicates = new Predicate[1];
    List<StudyEntity> studyList = null;
    StudyEntity studyEntity = null;

    CriteriaQuery<ParticipantRegistrySiteEntity> participantRegistryCriteria = null;
    Root<ParticipantRegistrySiteEntity> participantRegistryRoot = null;
    Predicate[] participantRegistryPredicates = new Predicate[1];
    List<ParticipantRegistrySiteEntity> participantRegistryList = null;
    ParticipantRegistrySiteEntity participantRegistry = null;

    List<SiteEntity> sitesList = null;
    SiteEntity siteEntity = null;
    String countAddParticipant = String.valueOf(0);

    CriteriaQuery<ParticipantStudyEntity> criteriaQuery = null;
    Root<ParticipantStudyEntity> root = null;
    Predicate[] participantPredicates = new Predicate[2];
    List<ParticipantStudyEntity> participantStudiesList = null;

    CriteriaQuery<SiteEntity> siteCriteria = null;
    Root<SiteEntity> siteRoot = null;
    Predicate[] sitePredicates = new Predicate[1];
    List<SiteEntity> siteList = null;
    SiteEntity site = null;

    ParticipantStudyEntity participants = null;
    ParticipantRegistrySiteEntity participantregistrySite = null;
    EnrollmentResponseBean participantBeans = new EnrollmentResponseBean();
    boolean isUpdated = false;
    String countAddregistry = "";
    Session session = this.sessionFactory.getCurrentSession();
    criteriaBuilder = session.getCriteriaBuilder();
    studyEntityCriteria = criteriaBuilder.createQuery(StudyEntity.class);
    studyEntityRoot = studyEntityCriteria.from(StudyEntity.class);
    predicates[0] = criteriaBuilder.equal(studyEntityRoot.get("customId"), studyId);
    studyEntityCriteria.select(studyEntityRoot).where(predicates);
    studyList = session.createQuery(studyEntityCriteria).getResultList();
    if (!studyList.isEmpty()) {
      studyEntity = studyList.get(0);
      if (isTokenRequired) {
        participantRegistryCriteria =
            criteriaBuilder.createQuery(ParticipantRegistrySiteEntity.class);
        participantRegistryRoot =
            participantRegistryCriteria.from(ParticipantRegistrySiteEntity.class);
        participantRegistryPredicates[0] =
            criteriaBuilder.equal(
                criteriaBuilder.upper(
                    criteriaBuilder.trim(participantRegistryRoot.get("enrollmentToken"))),
                tokenValue);
        participantRegistryCriteria
            .select(participantRegistryRoot)
            .where(participantRegistryPredicates);
        participantRegistryList = session.createQuery(participantRegistryCriteria).getResultList();
        if (!participantRegistryList.isEmpty()) {
          participantRegistry = participantRegistryList.get(0);

          siteList =
              session
                  .createQuery("from SiteEntity where id =:Id")
                  .setParameter("Id", participantRegistry.getSite().getId())
                  .getResultList();
          if (!siteList.isEmpty()) {
            siteEntity = siteList.get(0);
            criteriaQuery = criteriaBuilder.createQuery(ParticipantStudyEntity.class);
            root = criteriaQuery.from(ParticipantStudyEntity.class);
            participantPredicates[0] = criteriaBuilder.equal(root.get("userDetails"), userDetail);
            participantPredicates[1] = criteriaBuilder.equal(root.get("study"), studyEntity);
            criteriaQuery.select(root).where(participantPredicates);
            participantStudiesList = session.createQuery(criteriaQuery).getResultList();
            if (!participantStudiesList.isEmpty()) {
              participants = participantStudiesList.get(0);
              participants.setSite(siteEntity);
              participants.setStudy(studyEntity);
              participants.setParticipantId(participantid);
              participants.setUserDetails(userDetail);
              participants.setParticipantRegistrySite(participantRegistry);
              participants.setStatus(AppConstants.ENROLLED);
              participants.setEnrolledDate(Timestamp.from(Instant.now()));
              participants.setWithdrawalDate(null);
              session.update(participants);
              countAddParticipant = String.valueOf(1);
            } else {
              participants = new ParticipantStudyEntity();
              participants.setSite(siteEntity);
              participants.setStudy(studyEntity);
              participants.setParticipantId(participantid);
              participants.setUserDetails(userDetail);
              participants.setParticipantRegistrySite(participantRegistry);
              participants.setStatus(AppConstants.ENROLLED);
              participants.setEnrolledDate(Timestamp.from(Instant.now()));
              countAddParticipant = (String) session.save(participants);
            }
            if (StringUtils.isNotEmpty(countAddParticipant)) {
              isUpdated = true;
            }
          }
        }
        if ((StringUtils.isNotEmpty(countAddParticipant) && isUpdated)) {
          participantBeans.setAppToken(participantid);
          participantBeans.setSiteId(participants.getSite().getId());
        }
      } else {
        participantregistrySite = new ParticipantRegistrySiteEntity();
        participantregistrySite.setEnrollmentToken(tokenValue);

        siteCriteria = criteriaBuilder.createQuery(SiteEntity.class);
        siteRoot = siteCriteria.from(SiteEntity.class);
        sitePredicates[0] = criteriaBuilder.equal(siteRoot.get("study"), studyEntity);
        siteCriteria.select(siteRoot).where(sitePredicates);
        siteList = session.createQuery(siteCriteria).getResultList();
        if (!siteList.isEmpty()) {
          site = siteList.get(0);
          participantregistrySite.setSite(site);
        }

        participantregistrySite.setInvitationDate(Timestamp.from(Instant.now()));
        participantregistrySite.setEmail(userDetail.getEmail());
        participantregistrySite.setOnboardingStatus("E");
        participantregistrySite.setStudy(studyEntity);
        countAddregistry = (String) session.save(participantregistrySite);

        criteriaQuery = criteriaBuilder.createQuery(ParticipantStudyEntity.class);
        root = criteriaQuery.from(ParticipantStudyEntity.class);
        participantPredicates[0] = criteriaBuilder.equal(root.get("userDetails"), userDetail);
        participantPredicates[1] = criteriaBuilder.equal(root.get("study"), studyEntity);
        criteriaQuery.select(root).where(participantPredicates);
        participantStudiesList = session.createQuery(criteriaQuery).getResultList();

        if (!participantStudiesList.isEmpty()) {
          participants = participantStudiesList.get(0);
          participants.setStudy(studyEntity);
          participants.setParticipantId(participantid);
          participants.setUserDetails(userDetail);
          participants.setParticipantRegistrySite(participantregistrySite);
          participants.setStatus(AppConstants.ENROLLED);
          participants.setEnrolledDate(Timestamp.from(Instant.now()));
          participants.setSite(site);
          participants.setWithdrawalDate(null);
          session.update(participants);
          countAddParticipant = String.valueOf(1);
        } else {
          participants = new ParticipantStudyEntity();
          participants.setSite(site);
          participants.setStudy(studyEntity);
          participants.setParticipantId(participantid);
          participants.setUserDetails(userDetail);
          participants.setParticipantRegistrySite(participantregistrySite);
          participants.setStatus(AppConstants.ENROLLED);
          participants.setEnrolledDate(Timestamp.from(Instant.now()));
          countAddParticipant = (String) session.save(participants);
        }

        if (StringUtils.isNotEmpty(countAddParticipant) || !StringUtils.isEmpty(countAddregistry)) {
          participantBeans.setAppToken(participantid);
          participantBeans.setSiteId(site.getId());
        }
      }
    }

    logger.info("EnrollmentTokenDaoImpl enrollParticipant() - Ends ");
    return participantBeans;
  }
}
