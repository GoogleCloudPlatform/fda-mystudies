/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.beans.EnrollmentResponseBean;
import com.google.cloud.healthcare.fdamystudies.common.EnrollmentStatus;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.OnboardingStatus;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.mapper.ParticipantStatusHistoryMapper;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantEnrollmentHistoryEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantEnrollmentHistoryRepository;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantRegistrySiteRepository;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class EnrollmentTokenDaoImpl implements EnrollmentTokenDao {

  private static final XLogger logger =
      XLoggerFactory.getXLogger(EnrollmentTokenDaoImpl.class.getName());

  @Autowired private SessionFactory sessionFactory;

  @Autowired private ParticipantRegistrySiteRepository participantRegistrySiteRepository;

  @Autowired private ParticipantEnrollmentHistoryRepository participantEnrollmentHistoryRepository;

  @Override
  public StudyEntity getStudyDetails(String studyId) {
    logger.entry("Begin getStudyDetails()");
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
    /*if (studyEntity != null) {
      isStudyExist = true;
    }*/

    logger.exit("getStudyDetails() - Ends ");
    return studyEntity;
  }

  @Override
  public boolean isValidStudyToken(@NotNull String token, String studyId, @NotNull String email) {
    logger.entry("Begin isValidStudyToken()");
    ParticipantRegistrySiteEntity participantRegistrySite = null;
    Session session = this.sessionFactory.getCurrentSession();

    participantRegistrySite =
        (ParticipantRegistrySiteEntity)
            session
                .createQuery(
                    "from ParticipantRegistrySiteEntity PS where study.customId =:studyId and"
                        + " upper(trim(enrollmentToken))=:token and email=:email")
                .setParameter("studyId", studyId)
                .setParameter("token", token.toUpperCase())
                .setParameter("email", email)
                .uniqueResult();

    if (participantRegistrySite == null) {
      return false;
    }

    if (participantRegistrySite.getOnboardingStatus().equals(OnboardingStatus.NEW.getCode())) {
      return false;
    }

    Timestamp now = new Timestamp(Instant.now().toEpochMilli());
    if (participantRegistrySite.getOnboardingStatus().equals(OnboardingStatus.DISABLED.getCode())
        || now.after(participantRegistrySite.getEnrollmentTokenExpiry())) {
      throw new ErrorCodeException(ErrorCode.TOKEN_EXPIRED);
    }

    logger.exit("isValidStudyToken() - Ends ");
    return true;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean hasParticipant(String studyId, @NotNull String tokenValue) {
    logger.entry("Begin hasParticipant()");
    List<Object[]> participantList = null;
    Session session = this.sessionFactory.getCurrentSession();

    participantList =
        session
            .createQuery(
                "from ParticipantStudyEntity PS,StudyEntity SB, ParticipantRegistrySiteEntity PR"
                    + " where SB.id =PS.study.id and PS.participantRegistrySite.id=PR.id"
                    + " and upper(trim(PR.enrollmentToken))=:token and SB.customId=:studyId and"
                    + " PR.enrollmentTokenUsed=:tokenUsed")
            .setParameter("token", tokenValue.toUpperCase())
            .setParameter("studyId", studyId)
            .setParameter("tokenUsed", true)
            .getResultList();

    logger.exit("hasParticipant() - Ends ");
    return participantList != null && !participantList.isEmpty();
  }

  @Override
  public boolean enrollmentTokenRequired(String studyId) {
    logger.entry("Begin enrollmentTokenRequired()");
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

    logger.exit("enrollmentTokenRequired() - Ends ");
    return isTokenRequired;
  }

  @SuppressWarnings("unchecked")
  @Override
  public EnrollmentResponseBean enrollParticipant(
      String studyId,
      String tokenValue,
      UserDetailsEntity userDetail,
      boolean isTokenRequired,
      String participantid) {
    logger.entry("Begin enrollParticipant()");
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
          participantregistrySite = participantRegistryList.get(0);

          siteList =
              session
                  .createQuery("from SiteEntity where id =:Id")
                  .setParameter("Id", participantregistrySite.getSite().getId())
                  .getResultList();
          if (!siteList.isEmpty()) {
            siteEntity = siteList.get(0);
            criteriaQuery = criteriaBuilder.createQuery(ParticipantStudyEntity.class);
            root = criteriaQuery.from(ParticipantStudyEntity.class);
            participantPredicates[0] =
                criteriaBuilder.equal(root.get("participantRegistrySite"), participantregistrySite);
            participantPredicates[1] = criteriaBuilder.equal(root.get("study"), studyEntity);
            criteriaQuery.select(root).where(participantPredicates);
            participantStudiesList = session.createQuery(criteriaQuery).getResultList();
            if (!participantStudiesList.isEmpty()) {
              participants = participantStudiesList.get(0);
              participants.setSite(siteEntity);
              participants.setStudy(studyEntity);
              participants.setParticipantId(participantid);
              participants.setUserDetails(userDetail);
              participants.setParticipantRegistrySite(participantregistrySite);
              participants.setStatus(EnrollmentStatus.ENROLLED.getStatus());
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
              participants.setParticipantRegistrySite(participantregistrySite);
              participants.setStatus(EnrollmentStatus.ENROLLED.getStatus());
              participants.setEnrolledDate(Timestamp.from(Instant.now()));
              countAddParticipant = (String) session.save(participants);
            }
            if (StringUtils.isNotEmpty(countAddParticipant)) {
              isUpdated = true;
              participantregistrySite.setEnrollmentTokenUsed(true);
              participantRegistrySiteRepository.saveAndFlush(participantregistrySite);
            }
          }
        }
        if ((StringUtils.isNotEmpty(countAddParticipant) && isUpdated)) {
          participantBeans.setAppToken(participantid);
          participantBeans.setSiteId(participants.getSite().getId());
        }
      } else {
        List<ParticipantRegistrySiteEntity> participantList =
            participantRegistrySiteRepository.findByStudyIdAndEmail(
                studyEntity.getId(), userDetail.getEmail());

        participantregistrySite =
            CollectionUtils.isEmpty(participantList)
                ? new ParticipantRegistrySiteEntity()
                : participantList.get(0);

        participantregistrySite.setEnrollmentToken(tokenValue);
        participantregistrySite.setEnrollmentTokenUsed(true);

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
        participantregistrySite.setOnboardingStatus(OnboardingStatus.INVITED.getCode());
        participantregistrySite.setStudy(studyEntity);
        countAddregistry = (String) session.save(participantregistrySite);

        criteriaQuery = criteriaBuilder.createQuery(ParticipantStudyEntity.class);
        root = criteriaQuery.from(ParticipantStudyEntity.class);
        if (CollectionUtils.isNotEmpty(participantList)) {
          participantPredicates[0] =
              criteriaBuilder.equal(root.get("participantRegistrySite"), participantregistrySite);
        } else {
          participantPredicates[0] = criteriaBuilder.equal(root.get("userDetails"), userDetail);
        }
        participantPredicates[1] = criteriaBuilder.equal(root.get("study"), studyEntity);
        criteriaQuery.select(root).where(participantPredicates);
        participantStudiesList = session.createQuery(criteriaQuery).getResultList();

        if (!participantStudiesList.isEmpty()) {
          participants = participantStudiesList.get(0);
          participants.setStudy(studyEntity);
          participants.setParticipantId(participantid);
          participants.setUserDetails(userDetail);
          participants.setParticipantRegistrySite(participantregistrySite);
          participants.setStatus(EnrollmentStatus.ENROLLED.getStatus());
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
          participants.setStatus(EnrollmentStatus.ENROLLED.getStatus());
          participants.setEnrolledDate(Timestamp.from(Instant.now()));
          countAddParticipant = (String) session.save(participants);
        }

        if (StringUtils.isNotEmpty(countAddParticipant) || !StringUtils.isEmpty(countAddregistry)) {
          participantBeans.setAppToken(participantid);
          participantBeans.setSiteId(site.getId());
        }
      }
    }

    ParticipantEnrollmentHistoryEntity participantStatusHistoryEntity =
        ParticipantStatusHistoryMapper.toParticipantStatusHistoryEntity(
            participantregistrySite, EnrollmentStatus.ENROLLED, userDetail);
    participantEnrollmentHistoryRepository.save(participantStatusHistoryEntity);
    logger.exit("enrollParticipant() - Ends ");
    return participantBeans;
  }
}
