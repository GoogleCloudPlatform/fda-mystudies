/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.dao;

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
import com.google.cloud.healthcare.fdamystudies.beans.EnrollmentResponseBean;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySite;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.model.SiteBo;
import com.google.cloud.healthcare.fdamystudies.model.StudyInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.EnrollmentManagementUtil;

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
  public boolean isValidStudyToken(@NotNull String token, @NotNull String studyId) {
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
                  "from ParticipantRegistrySite PS where studyInfo.customId =:studyId and enrollmentToken=:token")
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
    ParticipantStudiesBO participants = null;
    List<Object[]> participantList = null;
    boolean hasParticipant = false;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      participantList =
          session
              .createQuery(
                  "from ParticipantStudiesBO PS,StudyInfoBO SB, ParticipantRegistrySite PR where SB.id =PS.studyInfo.id and PS.participantRegistrySite.id=PR.id and PR.enrollmentToken=:token and SB.customId=:studyId")
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

    ParticipantStudiesBO participants = new ParticipantStudiesBO();
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
              participants.setSiteBo(siteBo);
              participants.setStudyInfo(studyInfoBO);
              participants.setParticipantId(participantid);
              participants.setUserDetails(userDetail);
              participants.setParticipantRegistrySite(participantRegistry);
              participants.setStatus(AppConstants.ENROLLED);
              participants.setEnrolledDate(EnrollmentManagementUtil.getCurrentUtilDateTime());
              countAddParticipant = (Integer) session.save(participants);
            }
          }
          if (countAddParticipant > 0) {
            SiteBo sites = session.get(SiteBo.class, siteBo.getId());
            participantRegistry.setSites(sites);
            participantRegistry.setInvitationDate(
                EnrollmentManagementUtil.getCurrentUtilDateTime());
            participantRegistry.setOnboardingStatus("E");
            session.update(participantRegistry);
            isUpdated = true;
          }
        } else {

          participantregistrySite = new ParticipantRegistrySite();
          participantregistrySite.setEnrollmentToken(tokenValue);
          SiteBo sites =
              session.get(SiteBo.class, 1); // Need to check Default site for the open Study
          participantregistrySite.setSites(sites);
          participantregistrySite.setInvitationDate(
              EnrollmentManagementUtil.getCurrentUtilDateTime());
          participantregistrySite.setOnboardingStatus("E");
          participantregistrySite.setStudyInfo(studyInfoBO);
          countAddregistry = (Integer) session.save(participantregistrySite);

          participants.setSiteBo(sites);
          participants.setStudyInfo(studyInfoBO);
          participants.setParticipantId(participantid);
          participants.setUserDetails(userDetail);
          participants.setParticipantRegistrySite(participantregistrySite);
          participants.setStatus(AppConstants.ENROLLED);
          participants.setEnrolledDate(EnrollmentManagementUtil.getCurrentUtilDateTime());
          countAddParticipant = (Integer) session.save(participants);
        }

        if ((countAddParticipant > 0 && isUpdated) || countAddregistry > 0) {
          participantBeans.setAppToken(participantid);
          participantBeans.setSiteId(participants.getSiteBo().getId());
        }
      }
      transaction.commit();
    } catch (Exception e) {
      logger.error("EnrollmentTokenDaoImpl enrollParticipant() - error ", e);
      if (transaction != null) {
        try {
          transaction.rollback();
        } catch (Exception e1) {
          logger.error("UserProfileManagementDaoImpl - enrollParticipant() - error rollback", e1);
        }
      }
    }
    logger.info("EnrollmentTokenDaoImpl enrollParticipant() - Ends ");
    return participantBeans;
  }
}
