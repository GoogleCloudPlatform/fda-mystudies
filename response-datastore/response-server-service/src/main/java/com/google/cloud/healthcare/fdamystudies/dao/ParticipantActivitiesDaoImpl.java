/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.exception.ProcessActivityStateException;
import com.google.cloud.healthcare.fdamystudies.response.model.ParticipantActivitiesBo;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ParticipantActivitiesDaoImpl implements ParticipantActivitiesDao {

  private static final Logger logger = LoggerFactory.getLogger(ParticipantActivitiesDaoImpl.class);
  @Autowired private EntityManagerFactory entityManagerFactory;

  @Override
  @SuppressWarnings("unchecked")
  public List<ParticipantActivitiesBo> getParticipantActivities(
      String studyId, String participantId) throws ProcessActivityStateException {
    logger.debug("getParticipantActivities()...start");
    List<ParticipantActivitiesBo> participantActivitiesList = null;

    if (studyId != null && participantId != null) {
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {

        Query<ParticipantActivitiesBo> query =
            session.createQuery(
                "from ParticipantActivitiesBo "
                    + "where studyId = :studyId and participantId =:participantId");

        query.setParameter("studyId", studyId);
        query.setParameter("participantId", participantId);
        participantActivitiesList = query.getResultList();

        logger.debug("getParticipantActivities()...end " + participantActivitiesList);
        return participantActivitiesList;
      } catch (Exception e) {
        logger.error("getParticipantActivities: (ERROR) ", e);
        throw new ProcessActivityStateException(
            "Exception getting activity state data" + e.getMessage());
      }
    } else {
      throw new ProcessActivityStateException("Required input parameter is null");
    }
  }

  @Override
  public void saveParticipantActivities(List<ParticipantActivitiesBo> participantActivitiesList)
      throws ProcessActivityStateException {
    logger.debug("saveParticipantActivities() - Starts ");

    Transaction transaction = null;
    Session session = null;

    try {
      session = entityManagerFactory.unwrap(SessionFactory.class).openSession();
      transaction = session.beginTransaction();

      for (ParticipantActivitiesBo participantActivities : participantActivitiesList) {
        session.saveOrUpdate(participantActivities);
      }
      transaction.commit();

    } catch (Exception e) {
      logger.error("saveParticipantActivities - error ", e.getMessage());
      throw new ProcessActivityStateException(
          "Exception save activity state data" + e.getMessage());
    } finally {
      if (transaction != null) {
        transaction.rollback();
      }
      if (session != null) {
        session.close();
      }
      logger.debug("saveParticipantActivities() - Ends ");
    }
  }

  @Override
  public void deleteParticipantActivites(String studyId, String participantId)
      throws ProcessActivityStateException {
    logger.debug("deleteParticipantActivites()...start");
    Transaction transaction = null;
    Session session = null;
    if (studyId != null && participantId != null) {
      try {
        session = entityManagerFactory.unwrap(SessionFactory.class).openSession();
        transaction = session.beginTransaction();

        session
            .createQuery(
                "delete from ParticipantActivitiesBo "
                    + "where participantId = :participantId and studyId = :studyId")
            .setParameter("participantId", participantId)
            .setParameter("studyId", studyId)
            .executeUpdate();
        logger.debug("deleteParticipantActivites()...end ");
      } catch (Exception e) {
        logger.error("deleteParticipantActivites: (ERROR) ", e);
        throw new ProcessActivityStateException(
            "Exception deleting activity state data" + e.getMessage());
      } finally {
        if (transaction != null) {
          transaction.rollback();
        }
        if (session != null) {
          session.close();
        }
        logger.debug("deleteParticipantActivites() - Ends ");
      }
    } else {
      throw new ProcessActivityStateException("Required input parameter is null");
    }
  }
}
