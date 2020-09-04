/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.beans.EnrollmentResponseBean;
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
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class EnrollmentTokenDaoImpl implements EnrollmentTokenDao {

  private static final Logger logger = LoggerFactory.getLogger(EnrollmentTokenDaoImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

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
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
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
    } catch (Exception e) {
      logger.error("EnrollmentTokenDaoImpl studyExists() - error ", e);
    }
    logger.info("EnrollmentTokenDaoImpl studyExists() - Ends ");
    return isStudyExist;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean isValidStudyToken(@NotNull String token, @NotNull String studyId) {
    logger.info("EnrollmentTokenDaoImpl isValidStudyToken() - Started ");
    List<ParticipantRegistrySiteEntity> participantRegistrySite = new ArrayList<>();
    ParticipantRegistrySiteEntity participantRegistrySiteDetails = null;
    boolean isValidStudyToken = false;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      participantRegistrySite =
          session
              .createQuery(
                  "from ParticipantRegistrySiteEntity PS where study.customId =:studyId and"
                      + " enrollmentToken=:token")
              .setParameter("studyId", studyId)
              .setParameter("token", token)
              .getResultList();

      if (!participantRegistrySite.isEmpty()) {
        participantRegistrySiteDetails = participantRegistrySite.get(0);
      }
      if (participantRegistrySiteDetails != null) {
        isValidStudyToken = true;
      }
    } catch (Exception e) {
      logger.error("EnrollmentTokenDaoImpl isValidStudyToken() - error ", e);
    }
    logger.info("EnrollmentTokenDaoImpl isValidStudyToken() - Ends ");
    return isValidStudyToken;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean hasParticipant(@NotNull String studyId, @NotNull String tokenValue) {
    logger.info("EnrollmentTokenDaoImpl hasParticipant() - Started ");
    List<Object[]> participantList = null;
    boolean hasParticipant = false;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      participantList =
          session
              .createQuery(
                  "from ParticipantStudyEntity PS,StudyEntity SB, ParticipantRegistrySiteEntity PR"
                      + " where SB.id =PS.study.id and PS.participantRegistrySite.id=PR.id"
                      + " and PS.status='Enrolled' and PR.enrollmentToken=:token and SB.customId=:studyId")
              .setParameter("token", tokenValue)
              .setParameter("studyId", studyId)
              .getResultList();
      if (!participantList.isEmpty()) {
        hasParticipant = true;
      }
    } catch (Exception e) {
      logger.error("EnrollmentTokenDaoImpl hasParticipant() - error ", e);
    }
    logger.info("EnrollmentTokenDaoImpl hasParticipant() - Ends ");
    return hasParticipant;
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

    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
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
    } catch (Exception e) {
      logger.error("EnrollmentTokenDaoImpl enrollmentTokenRequired() - error ", e);
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
    Transaction transaction = null;
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
    Integer countAddParticipant = 0;

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
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      transaction = session.beginTransaction();
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
              criteriaBuilder.equal(participantRegistryRoot.get("enrollmentToken"), tokenValue);
          participantRegistryCriteria
              .select(participantRegistryRoot)
              .where(participantRegistryPredicates);
          participantRegistryList =
              session.createQuery(participantRegistryCriteria).getResultList();
          if (!participantRegistryList.isEmpty()) {
            participantRegistry = participantRegistryList.get(0);

            siteList =
                session
                    .createQuery("from Site where id =:Id")
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
                session.update(participants);
                countAddParticipant = 1;
              } else {
                participants = new ParticipantStudyEntity();
                participants.setSite(siteEntity);
                participants.setStudy(studyEntity);
                participants.setParticipantId(participantid);
                participants.setUserDetails(userDetail);
                participants.setParticipantRegistrySite(participantRegistry);
                participants.setStatus(AppConstants.ENROLLED);
                participants.setEnrolledDate(Timestamp.from(Instant.now()));
                countAddParticipant = (Integer) session.save(participants);
              }
              if (countAddParticipant > 0) {
                isUpdated = true;
              }
            }
          }
          if ((countAddParticipant > 0 && isUpdated)) {
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
            session.update(participants);
            countAddParticipant = 1;
          } else {
            participants = new ParticipantStudyEntity();
            participants.setSite(site);
            participants.setStudy(studyEntity);
            participants.setParticipantId(participantid);
            participants.setUserDetails(userDetail);
            participants.setParticipantRegistrySite(participantregistrySite);
            participants.setStatus(AppConstants.ENROLLED);
            participants.setEnrolledDate(Timestamp.from(Instant.now()));
            countAddParticipant = (Integer) session.save(participants);
          }

          if (countAddParticipant > 0 || !StringUtils.isEmpty(countAddregistry)) {
            participantBeans.setAppToken(participantid);
            participantBeans.setSiteId(site.getId());
          }
        }
      }
      transaction.commit();
    } catch (Exception e) {
      logger.error("EnrollmentTokenDaoImpl enrollParticipant() - error ", e);
      if (transaction != null) {
        try {
          transaction.rollback();
        } catch (Exception e1) {
          logger.error("EnrollmentTokenDaoImpl - enrollParticipant() - error rollback", e1);
        }
      }
    }
    logger.info("EnrollmentTokenDaoImpl enrollParticipant() - Ends ");
    return participantBeans;
  }
}
