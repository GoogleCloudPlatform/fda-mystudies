/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.exception.ProcessActivityStateException;
import com.google.cloud.healthcare.fdamystudies.responsedatastore.model.ParticipantActivitiesEntity;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ParticipantActivitiesDaoImpl implements ParticipantActivitiesDao {

  private static final Logger logger = LoggerFactory.getLogger(ParticipantActivitiesDaoImpl.class);

  @Autowired private SessionFactory sessionFactory;

  @Override
  @SuppressWarnings("unchecked")
  public List<ParticipantActivitiesEntity> getParticipantActivities(
      String studyId, String participantId) throws ProcessActivityStateException {
    logger.debug("getParticipantActivities()...start");

    if (studyId != null && participantId != null) {
      Session session = this.sessionFactory.getCurrentSession();

      Query<ParticipantActivitiesEntity> query =
          session.createQuery(
              "from ParticipantActivitiesEntity "
                  + "where studyId = :studyId and participantId =:participantId");

      query.setParameter("studyId", studyId);
      query.setParameter("participantId", participantId);
      List<ParticipantActivitiesEntity> participantActivitiesList = query.getResultList();

      logger.debug("getParticipantActivities()...end " + participantActivitiesList);
      return participantActivitiesList;
    } else {
      throw new ProcessActivityStateException("Required input parameter is null");
    }
  }

  @Override
  public void saveParticipantActivities(List<ParticipantActivitiesEntity> participantActivitiesList)
      throws ProcessActivityStateException {
    logger.debug("saveParticipantActivities() - Starts ");

    Session session = this.sessionFactory.getCurrentSession();
    for (ParticipantActivitiesEntity participantActivities : participantActivitiesList) {
      session.saveOrUpdate(participantActivities);
    }

    logger.debug("saveParticipantActivities() - Ends ");
  }

  @Override
  public void deleteParticipantActivites(String studyId, String participantId)
      throws ProcessActivityStateException {
    logger.debug("deleteParticipantActivites()...start");
    if (studyId != null && participantId != null) {
      Session session = this.sessionFactory.getCurrentSession();

      session
          .createQuery(
              "delete from ParticipantActivitiesEntity "
                  + "where participantId = :participantId and studyId = :studyId")
          .setParameter("participantId", participantId)
          .setParameter("studyId", studyId)
          .executeUpdate();
      logger.debug("deleteParticipantActivites()...end ");
    } else {
      throw new ProcessActivityStateException("Required input parameter is null");
    }
  }
}
