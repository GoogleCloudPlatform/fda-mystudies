/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.response.model.ParticipantInfoEntity;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
}
