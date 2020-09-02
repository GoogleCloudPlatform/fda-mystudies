/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;
import java.util.ArrayList;
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

@Repository
public class StudyStateDaoImpl implements StudyStateDao {

  private static final Logger logger = LoggerFactory.getLogger(StudyStateDaoImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Override
  public List<ParticipantStudyEntity> getParticipantStudiesList(UserDetailsEntity user) {
    logger.info("StudyStateDaoImpl getParticipantStudiesList() - Starts ");
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<ParticipantStudyEntity> criteriaQuery = null;
    Root<ParticipantStudyEntity> root = null;
    Predicate[] predicates = new Predicate[1];
    List<ParticipantStudyEntity> participantStudiesList = null;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      criteriaQuery = criteriaBuilder.createQuery(ParticipantStudyEntity.class);
      root = criteriaQuery.from(ParticipantStudyEntity.class);
      predicates[0] = criteriaBuilder.equal(root.get("userDetails"), user);
      criteriaQuery.select(root).where(predicates);
      participantStudiesList = session.createQuery(criteriaQuery).getResultList();
    } catch (Exception e) {
      logger.error("StudyStateDaoImpl getParticipantStudiesList() - error ", e);
    }
    logger.info("StudyStateDaoImpl getParticipantStudiesList() - Ends ");
    return participantStudiesList;
  }

  @Override
  public String saveParticipantStudies(List<ParticipantStudyEntity> participantStudiesList) {
    logger.info("StudyStateDaoImpl saveParticipantStudies() - Starts ");
    String message = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
    Transaction transaction = null;
    boolean isUpdated = false;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      transaction = session.beginTransaction();
      for (ParticipantStudyEntity participantStudies : participantStudiesList) {
        session.saveOrUpdate(participantStudies);
        isUpdated = true;
      }

      if (isUpdated && !participantStudiesList.isEmpty()) {
        message = MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue();
      }
      transaction.commit();
    } catch (Exception e) {
      logger.error("StudyStateDaoImpl saveParticipantStudies() - error ", e);
      if (transaction != null) {
        try {
          transaction.rollback();
        } catch (Exception e1) {
          logger.error("StudyStateDaoImpl - saveParticipantStudies() - error rollback", e1);
        }
      }
    }
    logger.info("StudyStateDaoImpl saveParticipantStudies() - Ends ");
    return message;
  }

  @Override
  public String getEnrollTokenForParticipant(String participantRegistryId) {
    logger.info("StudyStateDaoImpl getEnrollTokenForParticipant() - Starts ");
    String enrolledToken = "";
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<ParticipantRegistrySiteEntity> criteriaQuery = null;
    Root<ParticipantRegistrySiteEntity> root = null;
    Predicate[] predicates = new Predicate[1];
    List<ParticipantRegistrySiteEntity> participantRegistryList = null;
    ParticipantRegistrySiteEntity participantRegistrySite = null;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      criteriaQuery = criteriaBuilder.createQuery(ParticipantRegistrySiteEntity.class);
      root = criteriaQuery.from(ParticipantRegistrySiteEntity.class);
      predicates[0] = criteriaBuilder.equal(root.get("id"), participantRegistryId);
      criteriaQuery.select(root).where(predicates);
      participantRegistryList = session.createQuery(criteriaQuery).getResultList();
      if (!participantRegistryList.isEmpty()) {
        participantRegistrySite = participantRegistryList.get(0);
        enrolledToken = participantRegistrySite.getEnrollmentToken();
      }

    } catch (Exception e) {
      logger.error("StudyStateDaoImpl - getEnrollTokenForParticipant() - error rollback", e);
    }
    logger.info("StudyStateDaoImpl getEnrollTokenForParticipant() - Ends ");
    return enrolledToken;
  }

  @Override
  public String withdrawFromStudy(String participantId, String studyId, boolean delete) {
    logger.info("StudyStateDaoImpl withdrawFromStudy() - Ends ");
    String message = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
    Transaction transaction = null;
    CriteriaBuilder criteriaBuilder = null;

    CriteriaQuery<StudyEntity> studiesBoCriteria = null;
    Root<StudyEntity> studiesBoRoot = null;
    Predicate[] studiesBoPredicates = new Predicate[1];
    List<StudyEntity> studiesBoList = null;
    StudyEntity studyInfo = null;

    CriteriaUpdate<ParticipantStudyEntity> criteriaUpdate = null;
    Root<ParticipantStudyEntity> participantStudiesBoRoot = null;
    List<Predicate> predicates = new ArrayList<>();
    int isUpdated = 0;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {

      transaction = session.beginTransaction();
      criteriaBuilder = session.getCriteriaBuilder();

      studiesBoCriteria = criteriaBuilder.createQuery(StudyEntity.class);
      studiesBoRoot = studiesBoCriteria.from(StudyEntity.class);
      studiesBoPredicates[0] = criteriaBuilder.equal(studiesBoRoot.get("customId"), studyId);
      studiesBoCriteria.select(studiesBoRoot).where(studiesBoPredicates);
      studiesBoList = session.createQuery(studiesBoCriteria).getResultList();
      if (!studiesBoList.isEmpty()) {
        studyInfo = studiesBoList.get(0);
        criteriaUpdate = criteriaBuilder.createCriteriaUpdate(ParticipantStudyEntity.class);
        participantStudiesBoRoot = criteriaUpdate.from(ParticipantStudyEntity.class);
        criteriaUpdate.set("status", AppConstants.WITHDRAWN);
        predicates.add(
            criteriaBuilder.equal(participantStudiesBoRoot.get("participantId"), participantId));
        predicates.add(criteriaBuilder.equal(participantStudiesBoRoot.get("study"), studyInfo));
        criteriaUpdate.where(predicates.toArray(new Predicate[predicates.size()]));
        isUpdated = session.createQuery(criteriaUpdate).executeUpdate();
        if (isUpdated > 0) {
          message = MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue();
        }
      }
      transaction.commit();
    } catch (Exception e) {
      logger.error("StudyStateDaoImpl withdrawFromStudy() - error ", e);
      if (transaction != null) {
        try {
          transaction.rollback();
        } catch (Exception e1) {
          logger.error("StudyStateDaoImpl - withdrawFromStudy() - error rollback", e1);
        }
      }
    }
    logger.info("StudyStateDaoImpl withdrawFromStudy() - Ends ");
    return message;
  }
}
