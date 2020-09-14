/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.responsedatastore.model.ParticipantInfoEntity;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.ProcessResponseException;
import java.util.List;
import java.util.UUID;
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
public class ParticipantDaoImpl implements ParticipantDao {

  @Autowired private SessionFactory sessionFactory;

  private static final Logger logger = LoggerFactory.getLogger(ParticipantDaoImpl.class);

  @Override
  public String saveParticipant(ParticipantInfoEntity participantBo)
      throws ProcessResponseException {
    logger.info("ParticipantDaoImpl saveParticipant() - starts ");
    Session session = this.sessionFactory.getCurrentSession();

    CriteriaBuilder builder = session.getCriteriaBuilder();
    CriteriaQuery<ParticipantInfoEntity> participantBoCriteria =
        builder.createQuery(ParticipantInfoEntity.class);
    Root<ParticipantInfoEntity> root = participantBoCriteria.from(ParticipantInfoEntity.class);
    Predicate[] predicate = new Predicate[1];
    predicate[0] =
        builder.equal(
            root.get(AppConstants.PARTICIPANT_TOKEN_IDENTIFIER_KEY),
            participantBo.getTokenIdentifier());
    participantBoCriteria.select(root).where(predicate);
    List<ParticipantInfoEntity> resultList =
        session.createQuery(participantBoCriteria).getResultList();
    if (resultList != null && !resultList.isEmpty()) {
      throw new ProcessResponseException("Enrollment token exists, and cannot be added again");
    }

    UUID particpantUniqueIdentifier = UUID.randomUUID();
    participantBo.setParticipantIdentifier(particpantUniqueIdentifier.toString());
    session.save(participantBo);
    logger.info("ParticipantDaoImpl saveParticipant() - ends ");
    return particpantUniqueIdentifier.toString();
  }

  @Override
  public boolean isValidParticipant(ParticipantInfoEntity participantBo)
      throws ProcessResponseException {

    Session session = this.sessionFactory.getCurrentSession();

    CriteriaBuilder builder = session.getCriteriaBuilder();
    CriteriaQuery<ParticipantInfoEntity> participantBoCriteria =
        builder.createQuery(ParticipantInfoEntity.class);
    Root<ParticipantInfoEntity> root = participantBoCriteria.from(ParticipantInfoEntity.class);
    Predicate[] predicate = new Predicate[2];
    predicate[0] =
        builder.equal(
            root.get(AppConstants.PARTICIPANT_TOKEN_IDENTIFIER_KEY),
            participantBo.getTokenIdentifier());
    predicate[1] =
        builder.equal(
            root.get(AppConstants.PARTICIPANT_IDENTIFIER_KEY),
            participantBo.getParticipantIdentifier());
    participantBoCriteria.select(root).where(predicate);
    List<ParticipantInfoEntity> resultList =
        session.createQuery(participantBoCriteria).getResultList();
    if (resultList != null && resultList.size() == 1) {
      return true;
    } else {
      throw new ProcessResponseException(
          "Participant does not exist for the given participant ID and secure "
              + "enrollment token combination");
    }
  }
}
