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
      System.out.println("studyId->" + studyId);
      criteriaBuilder = session.getCriteriaBuilder();
      studyInfoBoCriteria = criteriaBuilder.createQuery(StudyInfoBO.class);
      studyInfoBoRoot = studyInfoBoCriteria.from(StudyInfoBO.class);
      predicates[0] =
          criteriaBuilder.equal(studyInfoBoRoot.get(AppConstants.CUSTOM_STUDY_ID), studyId);
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
    List<SiteBo> siteBo = new ArrayList<>();
    List<ParticipantRegistrySite> participantRegistrySite = new ArrayList<>();
    Integer siteId = 0;
    SiteBo siteDetails = null;
    ParticipantRegistrySite participantRegistrySiteDetails = null;
    boolean isValidStudyToken = false;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      siteBo =
          session
              .createQuery("from SiteBo where studyInfo.customStudyId =:studyId")
              //              .createQuery("from SiteBo where studyId =:studyId")
              .setParameter("studyId", studyId)
              .getResultList();

      if (!siteBo.isEmpty()) {
        siteDetails = siteBo.get(0);
        siteId = siteDetails.getId();
      }

      participantRegistrySite =
          session
              .createQuery(
                  "from ParticipantRegistrySite where sites.id =:siteId and enrollmentToken=:token")
              .setParameter("siteId", siteId)
              .setParameter("token", token)
              .getResultList();

      if (!participantRegistrySite.isEmpty()) {
        participantRegistrySiteDetails = participantRegistrySite.get(0);
      }
      if (participantRegistrySiteDetails != null) {
        isValidStudyToken = true;
      }

    } catch (Exception e) {
      logger.error("EnrollmentTokenDaoImpl hasParticipant() - error ", e);
    }
    logger.info("EnrollmentTokenDaoImpl isValidStudyToken() - Ends ");
    return isValidStudyToken;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean hasParticipant(@NotNull String studyId, @NotNull String tokenValue) {
    logger.info("EnrollmentTokenDaoImpl hasParticipant() - Started ");
    ParticipantStudiesBO participants = null;
    List<ParticipantStudiesBO> participantList = null;
    boolean hasParticipant = false;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      participantList =
          session
              .createQuery(
                  "from ParticipantStudiesBO PS,StudyInfoBO SB, ParticipantRegistrySite PR where SB.studyInfoId =PS.studyId and PR.id=PS.parfticipantRegistrySiteId and PR.enrollmentToken=:token and SB.customStudyId=:studyId")
              .setParameter("token", tokenValue)
              .setParameter("studyId", studyId)
              .getResultList();
      if (!participantList.isEmpty()) {
        participants = participantList.get(0);
      }
      if (participants != null) {
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
      predicates[0] =
          criteriaBuilder.equal(studyInfoBoRoot.get(AppConstants.CUSTOM_STUDY_ID), studyId);
      studyInfoBoCriteria.select(studyInfoBoRoot).where(predicates);
      studyInfoBoList = session.createQuery(studyInfoBoCriteria).getResultList();

      if (!studyInfoBoList.isEmpty()) {
        studyInfoBO = studyInfoBoList.get(0);
      }

      if (studyInfoBO != null && studyInfoBO.getStudyType().equalsIgnoreCase("Closed")) {
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
      Integer userDetailsId,
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
      predicates[0] =
          criteriaBuilder.equal(studyInfoBoRoot.get(AppConstants.CUSTOM_STUDY_ID), studyId);
      studyInfoBoCriteria.select(studyInfoBoRoot).where(predicates);
      studyInfoBoList = session.createQuery(studyInfoBoCriteria).getResultList();

      if (!studyInfoBoList.isEmpty()) {
        studyInfoBO = studyInfoBoList.get(0);

        /* siteBoCriteria = criteriaBuilder.createQuery(SiteBo.class);
        siteBoRoot = siteBoCriteria.from(SiteBo.class);
        siteBoPredicates[0] =
            criteriaBuilder.equal(
                siteBoRoot.get(AppConstants.STUDY_ID), studyInfoBO.getStudyInfoId());
        siteBoCriteria.select(siteBoRoot).where(siteBoPredicates);
         siteBoList = session.createQuery(siteBoCriteria).getResultList();
         */

        siteBoList =
            session
                .createQuery("from SiteBo where studyInfo.studyInfoId =:Id")
                .setParameter("Id", studyInfoBO.getStudyInfoId())
                .getResultList();

        if (!siteBoList.isEmpty()) {
          siteBo = siteBoList.get(0);
          participants.setSiteId(siteBo.getId());
          participants.setStudyId(studyInfoBO.getStudyInfoId());
          // participants.setParticipantId(EnrollmentManagementUtil.randomString(32));
          participants.setParticipantId(participantid);
          participants.setUserId(userDetailsId);
          participants.setStatus(AppConstants.ENROLLED);
          participants.setEnrolledDate(EnrollmentManagementUtil.getCurrentUtilDateTime());
          countAddParticipant = (Integer) session.save(participants);
        }
      }

      participantRegistryCriteria = criteriaBuilder.createQuery(ParticipantRegistrySite.class);
      participantRegistryRoot = participantRegistryCriteria.from(ParticipantRegistrySite.class);
      participantRegistryPredicates[0] =
          criteriaBuilder.equal(participantRegistryRoot.get("enrollmentToken"), tokenValue);
      participantRegistryCriteria
          .select(participantRegistryRoot)
          .where(participantRegistryPredicates);
      participantRegistryList = session.createQuery(participantRegistryCriteria).getResultList();

      if (!participantRegistryList.isEmpty()) {
        participantRegistry = participantRegistryList.get(0);
      }
      if (countAddParticipant > 0 && participantRegistry != null) {
        SiteBo sites = session.get(SiteBo.class, siteBo.getId());
        participantRegistry.setSites(sites);
        participantRegistry.setEmail("");
        participantRegistry.setInvitationDate(EnrollmentManagementUtil.getCurrentUtilDateTime());
        participantRegistry.setOnboardingStatus("E");
        session.update(participantRegistry);
        isUpdated = true;
      } else if (countAddParticipant > 0 && !isTokenRequired) {
        participantregistrySite = new ParticipantRegistrySite();
        tokenValue = EnrollmentManagementUtil.randomString(32);
        participantregistrySite.setEnrollmentToken(tokenValue);
        SiteBo sites = session.get(SiteBo.class, siteBo.getId());
        participantregistrySite.setSites(sites);
        participantregistrySite.setEmail("");
        participantregistrySite.setInvitationDate(
            EnrollmentManagementUtil.getCurrentUtilDateTime());
        participantregistrySite.setOnboardingStatus("E");
        countAddregistry = (Integer) session.save(participantregistrySite);
      }

      if ((countAddParticipant > 0 && isUpdated)
          || (countAddParticipant > 0 && countAddregistry > 0)) {
        participantBeans.setAppToken(participantid);
        participantBeans.setSiteId(participants.getSiteId());
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
