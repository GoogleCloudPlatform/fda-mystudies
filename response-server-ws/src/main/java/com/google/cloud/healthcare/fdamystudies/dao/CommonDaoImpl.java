/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.dao;

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
import com.google.cloud.healthcare.fdamystudies.model.ParticipantBo;

@Repository
public class CommonDaoImpl implements CommonDao {

  private static Logger logger = LoggerFactory.getLogger(CommonDaoImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Override
  public ParticipantBo getParticipantInfoDetails(String participantId) {
    logger.info("CommonDaoImpl getParticipantInfoDetails() - Ends ");
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<ParticipantBo> participantBoCriteriaQuery = null;
    Root<ParticipantBo> participantBoRoot = null;
    Predicate[] participantBoPredicates = new Predicate[1];
    List<ParticipantBo> participantBoList = null;
    ParticipantBo participantBO = null;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      participantBoCriteriaQuery = criteriaBuilder.createQuery(ParticipantBo.class);
      participantBoRoot = participantBoCriteriaQuery.from(ParticipantBo.class);
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
