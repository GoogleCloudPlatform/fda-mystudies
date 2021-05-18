/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.response.model.ParticipantInfoEntity;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.ProcessResponseException;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ParticipantDaoImpl implements ParticipantDao {
  @Autowired private EntityManagerFactory entityManagerFactory;

  private XLogger logger = XLoggerFactory.getXLogger(ParticipantDaoImpl.class.getName());

  @Override
  public String saveParticipant(ParticipantInfoEntity participantBo)
      throws ProcessResponseException {
    logger.entry("begin saveParticipant()");
    Transaction transaction = null;
    Session session = null;

    try {
      session = entityManagerFactory.unwrap(SessionFactory.class).openSession();

      CriteriaBuilder builder = session.getCriteriaBuilder();
      CriteriaQuery<ParticipantInfoEntity> participantBoCriteria =
          builder.createQuery(ParticipantInfoEntity.class);
      Root<ParticipantInfoEntity> root = participantBoCriteria.from(ParticipantInfoEntity.class);
      Predicate[] predicate = new Predicate[1];
      predicate[0] =
          builder.equal(
              root.get(AppConstants.PARTICIPANT_TOKEN_IDENTIFIER_KEY), participantBo.getTokenId());
      participantBoCriteria.select(root).where(predicate);
      List<ParticipantInfoEntity> resultList =
          session.createQuery(participantBoCriteria).getResultList();
      if (resultList != null && !resultList.isEmpty()) {
        throw new ProcessResponseException("Enrollment token exists, and cannot be added again");
      }

      UUID particpantUniqueIdentifier = UUID.randomUUID();
      participantBo.setParticipantId(particpantUniqueIdentifier.toString());
      transaction = session.beginTransaction();
      session.save(participantBo);
      transaction.commit();
      logger.exit("saveParticipant() - ends ");
      return particpantUniqueIdentifier.toString();
    } catch (PersistenceException e) {
      logger.error("saveParticipant - error " + e.getMessage());
      Throwable t = e.getCause();
      while ((t != null) && !(t instanceof ConstraintViolationException)) {
        t = t.getCause();
      }
      if (t instanceof ConstraintViolationException) {
        // Try again, likely because the UUID exists
        if (transaction != null) {
          transaction.rollback();
        }
        UUID particpantUniqueIdentifier = UUID.randomUUID();
        participantBo.setParticipantId(particpantUniqueIdentifier.toString());
        transaction = session.beginTransaction();
        session.save(participantBo);
        transaction.commit();
        return particpantUniqueIdentifier.toString();
      }
      throw e;
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new ProcessResponseException(e.getMessage());
    } finally {
      if (transaction != null) {
        transaction.rollback();
      }
      if (session != null) {
        session.close();
      }
    }
  }

  @Override
  public boolean isValidParticipant(ParticipantInfoEntity participantBo)
      throws ProcessResponseException {
    logger.entry("begin isValidParticipant()");
    Session session = null;
    try {
      session = entityManagerFactory.unwrap(SessionFactory.class).openSession();

      CriteriaBuilder builder = session.getCriteriaBuilder();
      CriteriaQuery<ParticipantInfoEntity> participantBoCriteria =
          builder.createQuery(ParticipantInfoEntity.class);
      Root<ParticipantInfoEntity> root = participantBoCriteria.from(ParticipantInfoEntity.class);
      Predicate[] predicate = new Predicate[2];
      predicate[0] =
          builder.equal(
              root.get(AppConstants.PARTICIPANT_TOKEN_IDENTIFIER_KEY), participantBo.getTokenId());
      predicate[1] =
          builder.equal(
              root.get(AppConstants.PARTICIPANT_IDENTIFIER_KEY), participantBo.getParticipantId());
      participantBoCriteria.select(root).where(predicate);
      List<ParticipantInfoEntity> resultList =
          session.createQuery(participantBoCriteria).getResultList();
      if (resultList != null && resultList.size() == 1) {
        logger.exit("isValidParticipant() - ends ");
        return true;
      } else {
        throw new ProcessResponseException(
            "Participant does not exist for the given participant ID and secure "
                + "enrollment token combination");
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new ProcessResponseException(e.getMessage());
    } finally {
      if (session != null) {
        session.close();
      }
    }
  }
}
