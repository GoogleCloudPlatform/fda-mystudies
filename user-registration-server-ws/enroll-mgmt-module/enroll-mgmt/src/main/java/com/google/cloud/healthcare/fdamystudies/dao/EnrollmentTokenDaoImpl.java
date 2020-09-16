/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.beans.EnrollmentResponseBean;
import com.google.cloud.healthcare.fdamystudies.enroll.model.ParticipantRegistrySite;
import com.google.cloud.healthcare.fdamystudies.enroll.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.enroll.model.SiteBo;
import com.google.cloud.healthcare.fdamystudies.enroll.model.StudyInfoBO;
import com.google.cloud.healthcare.fdamystudies.enroll.model.UserDetailsBO;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.EnrollmentManagementUtil;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
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
    CriteriaQuery<StudyInfoBO> studyInfoBoCriteria = null;
    Root<StudyInfoBO> studyInfoBoRoot = null;
    Predicate[] predicates = new Predicate[1];
    List<StudyInfoBO> studyInfoBoList = null;
    StudyInfoBO studyInfoBO = null;
    boolean isStudyExist = false;

    Session session = this.sessionFactory.getCurrentSession();
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

    logger.info("EnrollmentTokenDaoImpl studyExists() - Ends ");
    return isStudyExist;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean isValidStudyToken(@NotNull String token, @NotNull String studyId) {
    logger.info("EnrollmentTokenDaoImpl isValidStudyToken() - Started ");
    List<ParticipantRegistrySite> participantRegistrySite = new ArrayList<>();

    ParticipantRegistrySite participantRegistrySiteDetails = null;
    boolean isValidStudyToken = false;
    Session session = this.sessionFactory.getCurrentSession();

    participantRegistrySite =
        session
            .createQuery(
                "from ParticipantRegistrySite PS where studyInfo.customId =:studyId and"
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

    logger.info("EnrollmentTokenDaoImpl isValidStudyToken() - Ends ");
    return isValidStudyToken;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean hasParticipant(@NotNull String studyId, @NotNull String tokenValue) {
    logger.info("EnrollmentTokenDaoImpl hasParticipant() - Started ");
    List<Object[]> participantList = null;
    boolean hasParticipant = false;
    Session session = this.sessionFactory.getCurrentSession();

    participantList =
        session
            .createQuery(
                "from ParticipantStudiesBO PS,StudyInfoBO SB, ParticipantRegistrySite PR"
                    + " where SB.id =PS.studyInfo.id and PS.participantRegistrySite.id=PR.id"
                    + " and PS.status='Enrolled' and PR.enrollmentToken=:token and SB.customId=:studyId")
            .setParameter("token", tokenValue)
            .setParameter("studyId", studyId)
            .getResultList();
    if (!participantList.isEmpty()) {
      hasParticipant = true;
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

    Session session = this.sessionFactory.getCurrentSession();
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

    Session session = this.sessionFactory.getCurrentSession();
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
        participantRegistryList = session.createQuery(participantRegistryCriteria).getResultList();
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

    logger.info("EnrollmentTokenDaoImpl enrollParticipant() - Ends ");
    return participantBeans;
  }
}
