/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.bean.FHIRQuestionnaireResponseBean;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.response.model.FHIRresponseEntity;
import com.google.cloud.healthcare.fdamystudies.response.model.ParticipantInfoEntity;
import com.google.gson.Gson;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CommonDaoImpl implements CommonDao {

  private XLogger logger = XLoggerFactory.getXLogger(CommonDaoImpl.class.getName());

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Override
  public ParticipantInfoEntity getParticipantInfoDetails(String participantId) {
    logger.entry("begin getParticipantInfoDetails()");
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<ParticipantInfoEntity> participantBoCriteriaQuery = null;
    Root<ParticipantInfoEntity> participantBoRoot = null;
    Predicate[] participantBoPredicates = new Predicate[1];
    List<ParticipantInfoEntity> participantBoList = null;
    ParticipantInfoEntity participantBO = null;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      participantBoCriteriaQuery = criteriaBuilder.createQuery(ParticipantInfoEntity.class);
      participantBoRoot = participantBoCriteriaQuery.from(ParticipantInfoEntity.class);
      participantBoPredicates[0] =
          criteriaBuilder.equal(participantBoRoot.get("participantIdentifier"), participantId);
      participantBoCriteriaQuery.select(participantBoRoot).where(participantBoPredicates);
      participantBoList = session.createQuery(participantBoCriteriaQuery).getResultList();
      if (!participantBoList.isEmpty()) {
        participantBO = participantBoList.get(0);
      }
    } catch (Exception e) {
      logger.error("CommonDaoImpl getParticipantInfoDetails() - error ", e);
    }
    logger.exit("getParticipantInfoDetails() - Ends ");
    return participantBO;
  }

  @Override
  public StudyEntity getStudyDetails(String customStudyId) {
    logger.entry("begin getStudyDetails() - Starts ");
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<StudyEntity> criteriaQuery = null;
    Root<StudyEntity> root = null;
    Predicate[] predicates = new Predicate[1];
    List<StudyEntity> studyList = null;
    StudyEntity studyInfo = null;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      criteriaQuery = criteriaBuilder.createQuery(StudyEntity.class);
      root = criteriaQuery.from(StudyEntity.class);
      predicates[0] = criteriaBuilder.equal(root.get("customId"), customStudyId);
      criteriaQuery.select(root).where(predicates);
      studyList = session.createQuery(criteriaQuery).getResultList();
      if (!studyList.isEmpty()) {
        studyInfo = studyList.get(0);
      }
    } catch (Exception e) {
      logger.error("CommonDaoImpl getStudyDetails() - error ", e);
    }

    return studyInfo;
  }

  @Override
  public void saveToFHIREntity(String getFhirJson, String studyId) {
    logger.entry("begin saveParticipantActivities()");

    Transaction transaction = null;
    Session session = null;

    try {
      session = entityManagerFactory.unwrap(SessionFactory.class).openSession();
      transaction = session.beginTransaction();
      FHIRQuestionnaireResponseBean fhirQuestionnaireResponseBean =
          new Gson().fromJson(getFhirJson, FHIRQuestionnaireResponseBean.class);
      FHIRresponseEntity fhiRresponseEntity = new FHIRresponseEntity();
      fhiRresponseEntity.setPatientReference(
          fhirQuestionnaireResponseBean.getSource().getReference());
      fhiRresponseEntity.setQuestionnaireReference(
          fhirQuestionnaireResponseBean.getResourceType()
              + '/'
              + fhirQuestionnaireResponseBean.getId());
      fhiRresponseEntity.setStudyId(studyId);

      session.saveOrUpdate(fhiRresponseEntity);
      transaction.commit();

    } catch (Exception e) {
      logger.error("CommonDaoImpl getParticipantInfoDetails() - error ", e);
    }
    logger.exit("getParticipantInfoDetails() - Ends ");
  }

  @Override
  public void updateDidStatus(String questionnaireReference) {
    logger.entry("begin updateDidStatus() - Starts ");
    Transaction transaction = null;
    Session session = null;
    try {
      session = entityManagerFactory.unwrap(SessionFactory.class).openSession();
      transaction = session.beginTransaction();
      session
          .createQuery(
              "update FHIRresponseEntity set didStatus = :didStatus "
                  + "where questionnaireReference = :questionnaireReference")
          .setParameter("didStatus", Boolean.TRUE)
          .setParameter("questionnaireReference", questionnaireReference)
          .executeUpdate();
      transaction.commit();
      logger.exit("deleteParticipantActivites()...end ");
    } catch (Exception e) {
      logger.error("deleteParticipantActivites: (ERROR) ", e);
    } finally {
      if (transaction != null) {
        transaction.rollback();
      }
      if (session != null) {
        session.close();
      }
      logger.exit("deleteParticipantActivites() - Ends ");
    }
  }

  @Override
  public List<FHIRresponseEntity> getFhirDetails(Boolean didStatus) {
    logger.entry("Begin getFhirDetails()");
    List<FHIRresponseEntity> fhirList = null;

    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<FHIRresponseEntity> criteriaQuery = null;
    Root<FHIRresponseEntity> root = null;
    Predicate[] predicates = new Predicate[1];

    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      criteriaQuery = criteriaBuilder.createQuery(FHIRresponseEntity.class);
      root = criteriaQuery.from(FHIRresponseEntity.class);
      predicates[0] = criteriaBuilder.equal(root.get("didStatus"), didStatus);
      criteriaQuery.select(root).where(predicates);
      fhirList = session.createQuery(criteriaQuery).getResultList();
    } catch (Exception e) {
      logger.error("CommonDaoImpl getFhirDetails() - error ", e);
    }
    return fhirList;
  }
}
