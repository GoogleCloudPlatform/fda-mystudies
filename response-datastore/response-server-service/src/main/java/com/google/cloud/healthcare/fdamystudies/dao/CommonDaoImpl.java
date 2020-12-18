/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.response.model.ParticipantInfoEntity;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CommonDaoImpl implements CommonDao {

  private static Logger logger = LoggerFactory.getLogger(CommonDaoImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Override
  public ParticipantInfoEntity getParticipantInfoDetails(String participantId) {
    logger.info("CommonDaoImpl getParticipantInfoDetails() - Ends ");
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
    logger.info("CommonDaoImpl getParticipantInfoDetails() - Ends ");
    return participantBO;
  }
}
