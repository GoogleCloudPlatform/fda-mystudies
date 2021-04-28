/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.exception.ProcessActivityStateException;
import com.google.cloud.healthcare.fdamystudies.response.model.ParticipantActivitiesEntity;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ParticipantActivitiesDaoImpl implements ParticipantActivitiesDao {

  private XLogger logger = XLoggerFactory.getXLogger(ParticipantActivitiesDaoImpl.class.getName());
  @Autowired private EntityManagerFactory entityManagerFactory;

  @Override
  @SuppressWarnings("unchecked")
  public List<ParticipantActivitiesEntity> getParticipantActivities(
      String studyId, String participantId) throws ProcessActivityStateException {
    logger.entry("begin getParticipantActivities()");
    List<ParticipantActivitiesEntity> participantActivitiesList = null;

    if (studyId != null && participantId != null) {
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {

        Query<ParticipantActivitiesEntity> query =
            session.createQuery(
                "from ParticipantActivitiesEntity "
                    + "where studyId = :studyId and participantId =:participantId");

        query.setParameter("studyId", studyId);
        query.setParameter("participantId", participantId);
        participantActivitiesList = query.getResultList();

        logger.exit("getParticipantActivities() end " + participantActivitiesList);
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
  public void saveParticipantActivities(List<ParticipantActivitiesEntity> participantActivitiesList)
      throws ProcessActivityStateException {
    logger.entry("begin saveParticipantActivities()");

    Transaction transaction = null;
    Session session = null;

    try {
      session = entityManagerFactory.unwrap(SessionFactory.class).openSession();
      transaction = session.beginTransaction();

      for (ParticipantActivitiesEntity participantActivities : participantActivitiesList) {
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
      logger.exit("saveParticipantActivities() - Ends ");
    }
  }

  @Override
  public void deleteParticipantActivites(String studyId, String participantId)
      throws ProcessActivityStateException {
    logger.entry("begin deleteParticipantActivites()");
    Transaction transaction = null;
    Session session = null;
    if (studyId != null && participantId != null) {
      try {
        session = entityManagerFactory.unwrap(SessionFactory.class).openSession();
        transaction = session.beginTransaction();

        session
            .createQuery(
                "delete from ParticipantActivitiesEntity "
                    + "where participantId = :participantId and studyId = :studyId")
            .setParameter("participantId", participantId)
            .setParameter("studyId", studyId)
            .executeUpdate();
        logger.exit("deleteParticipantActivites()...end ");
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
        logger.exit("deleteParticipantActivites() - Ends ");
      }
    } else {
      throw new ProcessActivityStateException("Required input parameter is null");
    }
  }
}
