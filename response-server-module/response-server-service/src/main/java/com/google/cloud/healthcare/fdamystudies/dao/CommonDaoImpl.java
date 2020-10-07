/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.responsedatastore.model.ParticipantInfoEntity;
import java.util.List;
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

  @Autowired private SessionFactory sessionFactory;

  @Override
  public ParticipantInfoEntity getParticipantInfoDetails(String participantId) {
    logger.info("CommonDaoImpl getParticipantInfoDetails() - Ends ");
    Predicate[] participantBoPredicates = new Predicate[1];

    Session session = this.sessionFactory.getCurrentSession();
    CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
    CriteriaQuery<ParticipantInfoEntity> participantBoCriteriaQuery =
        criteriaBuilder.createQuery(ParticipantInfoEntity.class);
    Root<ParticipantInfoEntity> participantBoRoot =
        participantBoCriteriaQuery.from(ParticipantInfoEntity.class);
    participantBoPredicates[0] =
        criteriaBuilder.equal(participantBoRoot.get("participantId"), participantId);
    participantBoCriteriaQuery.select(participantBoRoot).where(participantBoPredicates);
    List<ParticipantInfoEntity> participantBoList =
        session.createQuery(participantBoCriteriaQuery).getResultList();
    ParticipantInfoEntity participantBO = null;
    if (!participantBoList.isEmpty()) {
      participantBO = participantBoList.get(0);
    }

    logger.info("CommonDaoImpl getParticipantInfoDetails() - Ends ");
    return participantBO;
  }
}
