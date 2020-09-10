/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.beans.EnrollmentResponseBean;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySite;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.model.SiteBo;
import com.google.cloud.healthcare.fdamystudies.model.StudyInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.EnrollmentManagementUtil;
import com.google.cloud.healthcare.fdamystudies.util.ParticipantStudyStateStatus;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
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
    CriteriaQuery<StudyInfoBO> studyInfoBoCriteria = null;
    Root<StudyInfoBO> studyInfoBoRoot = null;
    Predicate[] predicates = new Predicate[1];
    List<StudyInfoBO> studyInfoBoList = null;
    StudyInfoBO studyInfoBO = null;
    boolean isStudyExist = false;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      studyInfoBoCriteria = criteriaBuilder.createQuery(StudyInfoBO.class);
      studyInfoBoRoot = studyInfoBoCriteria.from(StudyInfoBO.class);
      predicates[0] = criteriaBuilder.equal(studyInfoBoRoot.get("customId"), studyId);
      studyInfoBoCriteria.select(studyInfoBoRoot).where(predicates);
      studyInfoBoList = session.createQuery(studyInfoBoCriteria).getResultList();

      if (!studyInfoBoList.isEmpty()) {
        studyInfoBO = studyInfoBoList.get(0);
      }
      if (studyInfoBO != null) {
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
  public boolean isEnrollmentTokenValid(
      @NotNull String token, @NotNull String studyId, @NotNull String email) {
    logger.info("EnrollmentTokenDaoImpl isValidStudyToken() - Started ");
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<StudyInfoBO> studyInfoBoCriteria = null;
    Root<StudyInfoBO> studyInfoBoRoot = null;
    Predicate[] predicates = new Predicate[1];
    List<StudyInfoBO> studyInfoBoList = null;
    StudyInfoBO studyInfoBO = null;
    List<ParticipantRegistrySite> participantRegistrySite = new ArrayList<>();
    Integer siteId = 0;
    ParticipantRegistrySite participantRegistrySiteDetails = null;
    boolean isValidStudyToken = false;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      participantRegistrySite =
          session
              .createQuery(
                  "from ParticipantRegistrySite PS where studyInfo.customId =:studyId and"
                      + " enrollmentToken=:token and email=:email")
              .setParameter("studyId", studyId)
              .setParameter("token", token)
              .setParameter("email", email)
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
    ParticipantStudiesBO participants = null;
    List<Object[]> participantList = null;
    boolean hasParticipant = false;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      List studyStateStatus = new ArrayList();
      studyStateStatus.add(ParticipantStudyStateStatus.ENROLLED.getValue());
      studyStateStatus.add(ParticipantStudyStateStatus.WITHDRAWN.getValue());
      studyStateStatus.add(ParticipantStudyStateStatus.INPROGRESS.getValue());
      participantList =
          session
              .createQuery(
                  "from ParticipantStudiesBO PS,StudyInfoBO SB, ParticipantRegistrySite PR"
                      + " where SB.id =PS.studyInfo.id and PS.participantRegistrySite.id=PR.id"
                      + " and PS.status in (:studyStateStatus) "
                      + "and PR.enrollmentToken=:token and SB.customId=:studyId")
              .setParameter("studyStateStatus", studyStateStatus)
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
    CriteriaQuery<StudyInfoBO> studyInfoBoCriteria = null;
    Root<StudyInfoBO> studyInfoBoRoot = null;
    Predicate[] predicates = new Predicate[1];
    List<StudyInfoBO> studyInfoBoList = null;
    StudyInfoBO studyInfoBO = null;
    boolean isTokenRequired = false;

    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      studyInfoBoCriteria = criteriaBuilder.createQuery(StudyInfoBO.class);
      studyInfoBoRoot = studyInfoBoCriteria.from(StudyInfoBO.class);
      predicates[0] = criteriaBuilder.equal(studyInfoBoRoot.get("customId"), studyId);
      studyInfoBoCriteria.select(studyInfoBoRoot).where(predicates);
      studyInfoBoList = session.createQuery(studyInfoBoCriteria).getResultList();
      if (!studyInfoBoList.isEmpty()) {
        studyInfoBO = studyInfoBoList.get(0);
      }
      if (studyInfoBO != null && studyInfoBO.getType().equalsIgnoreCase(AppConstants.CLOSE_STUDY)) {
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
      UserDetailsBO userDetail,
      boolean isTokenRequired,
      String participantid) {
    logger.info("EnrollmentTokenDaoImpl enrollParticipant() - Started ");
    Transaction transaction = null;
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<StudyInfoBO> studyInfoBoCriteria = null;
    Root<StudyInfoBO> studyInfoBoRoot = null;
    Predicate[] predicates = new Predicate[1];
    List<StudyInfoBO> studyInfoBoList = null;
    StudyInfoBO studyInfoBO = null;

    CriteriaQuery<ParticipantRegistrySite> participantRegistryCriteria = null;
    Root<ParticipantRegistrySite> participantRegistryRoot = null;
    Predicate[] participantRegistryPredicates = new Predicate[1];
    List<ParticipantRegistrySite> participantRegistryList = null;
    ParticipantRegistrySite participantRegistry = null;

    List<SiteBo> siteBoList = null;
    SiteBo siteBo = null;
    Integer countAddParticipant = 0;

    CriteriaQuery<ParticipantStudiesBO> criteriaQuery = null;
    Root<ParticipantStudiesBO> root = null;
    Predicate[] participantPredicates = new Predicate[2];
    List<ParticipantStudiesBO> participantStudiesList = null;

    CriteriaQuery<SiteBo> siteCriteria = null;
    Root<SiteBo> siteRoot = null;
    Predicate[] sitePredicates = new Predicate[1];
    List<SiteBo> siteList = null;
    SiteBo site = null;

    ParticipantStudiesBO participants = null;
    ParticipantRegistrySite participantregistrySite = null;
    EnrollmentResponseBean participantBeans = new EnrollmentResponseBean();
    boolean isUpdated = false;
    Integer countAddregistry = 0;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      transaction = session.beginTransaction();
      criteriaBuilder = session.getCriteriaBuilder();
      studyInfoBoCriteria = criteriaBuilder.createQuery(StudyInfoBO.class);
      studyInfoBoRoot = studyInfoBoCriteria.from(StudyInfoBO.class);
      predicates[0] = criteriaBuilder.equal(studyInfoBoRoot.get("customId"), studyId);
      studyInfoBoCriteria.select(studyInfoBoRoot).where(predicates);
      studyInfoBoList = session.createQuery(studyInfoBoCriteria).getResultList();
      if (!studyInfoBoList.isEmpty()) {
        studyInfoBO = studyInfoBoList.get(0);
        if (isTokenRequired) {
          participantRegistryCriteria = criteriaBuilder.createQuery(ParticipantRegistrySite.class);
          participantRegistryRoot = participantRegistryCriteria.from(ParticipantRegistrySite.class);
          participantRegistryPredicates[0] =
              criteriaBuilder.equal(participantRegistryRoot.get("enrollmentToken"), tokenValue);
          participantRegistryCriteria
              .select(participantRegistryRoot)
              .where(participantRegistryPredicates);
          participantRegistryList =
              session.createQuery(participantRegistryCriteria).getResultList();
          if (!participantRegistryList.isEmpty()) {
            participantRegistry = participantRegistryList.get(0);

            siteBoList =
                session
                    .createQuery("from SiteBo where id =:Id")
                    .setParameter("Id", participantRegistry.getSites().getId())
                    .getResultList();
            if (!siteBoList.isEmpty()) {
              siteBo = siteBoList.get(0);
              criteriaQuery = criteriaBuilder.createQuery(ParticipantStudiesBO.class);
              root = criteriaQuery.from(ParticipantStudiesBO.class);
              participantPredicates[0] = criteriaBuilder.equal(root.get("userDetails"), userDetail);
              participantPredicates[1] = criteriaBuilder.equal(root.get("studyInfo"), studyInfoBO);
              criteriaQuery.select(root).where(participantPredicates);
              participantStudiesList = session.createQuery(criteriaQuery).getResultList();
              if (!participantStudiesList.isEmpty()) {
                participants = participantStudiesList.get(0);
                participants.setSiteBo(siteBo);
                participants.setStudyInfo(studyInfoBO);
                participants.setParticipantId(participantid);
                participants.setUserDetails(userDetail);
                participants.setParticipantRegistrySite(participantRegistry);
                participants.setStatus(AppConstants.ENROLLED);
                participants.setEnrolledDate(EnrollmentManagementUtil.getCurrentUtilDateTime());
                session.update(participants);
                countAddParticipant = 1;
              } else {
                participants = new ParticipantStudiesBO();
                participants.setSiteBo(siteBo);
                participants.setStudyInfo(studyInfoBO);
                participants.setParticipantId(participantid);
                participants.setUserDetails(userDetail);
                participants.setParticipantRegistrySite(participantRegistry);
                participants.setStatus(AppConstants.ENROLLED);
                participants.setEnrolledDate(EnrollmentManagementUtil.getCurrentUtilDateTime());
                countAddParticipant = (Integer) session.save(participants);
              }
              if (countAddParticipant > 0) {
                isUpdated = true;
              }
            }
          }
          if ((countAddParticipant > 0 && isUpdated)) {
            participantBeans.setAppToken(participantid);
            participantBeans.setSiteId(participants.getSiteBo().getId());
          }
        } else {
          participantregistrySite = new ParticipantRegistrySite();
          participantregistrySite.setEnrollmentToken(tokenValue);

          siteCriteria = criteriaBuilder.createQuery(SiteBo.class);
          siteRoot = siteCriteria.from(SiteBo.class);
          sitePredicates[0] = criteriaBuilder.equal(siteRoot.get("studyInfo"), studyInfoBO);
          siteCriteria.select(siteRoot).where(sitePredicates);
          siteList = session.createQuery(siteCriteria).getResultList();

          if (!siteList.isEmpty()) {
            site = siteList.get(0);
            participantregistrySite.setSites(site);
          }
          participantregistrySite.setInvitationDate(
              EnrollmentManagementUtil.getCurrentUtilDateTime());
          participantregistrySite.setOnboardingStatus("E");
          participantregistrySite.setStudyInfo(studyInfoBO);
          countAddregistry = (Integer) session.save(participantregistrySite);

          criteriaQuery = criteriaBuilder.createQuery(ParticipantStudiesBO.class);
          root = criteriaQuery.from(ParticipantStudiesBO.class);
          participantPredicates[0] = criteriaBuilder.equal(root.get("userDetails"), userDetail);
          participantPredicates[1] = criteriaBuilder.equal(root.get("studyInfo"), studyInfoBO);
          criteriaQuery.select(root).where(participantPredicates);
          participantStudiesList = session.createQuery(criteriaQuery).getResultList();

          if (!participantStudiesList.isEmpty()) {
            participants = participantStudiesList.get(0);
            participants.setStudyInfo(studyInfoBO);
            participants.setParticipantId(participantid);
            participants.setUserDetails(userDetail);
            participants.setParticipantRegistrySite(participantregistrySite);
            participants.setStatus(AppConstants.ENROLLED);
            participants.setEnrolledDate(EnrollmentManagementUtil.getCurrentUtilDateTime());
            participants.setSiteBo(site);
            session.update(participants);
            countAddParticipant = 1;
          } else {
            participants = new ParticipantStudiesBO();
            participants.setSiteBo(site);
            participants.setStudyInfo(studyInfoBO);
            participants.setParticipantId(participantid);
            participants.setUserDetails(userDetail);
            participants.setParticipantRegistrySite(participantregistrySite);
            participants.setStatus(AppConstants.ENROLLED);
            participants.setEnrolledDate(EnrollmentManagementUtil.getCurrentUtilDateTime());
            countAddParticipant = (Integer) session.save(participants);
          }

          if (countAddParticipant > 0 || countAddregistry > 0) {
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
