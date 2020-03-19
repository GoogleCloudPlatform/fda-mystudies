/**
 * Copyright 2020 Google LLC
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions: The above copyright notice and this
 * permission notice shall be included in all copies or substantial portions of the Software.THE
 * SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.google.cloud.healthcare.fdamystudies.dao;

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
import com.google.cloud.healthcare.fdamystudies.exception.ProcessActivityStateException;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantActivitiesBo;

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
                "from ParticipantActivitiesBo where studyId = :studyId and participantId =:participantId");

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
}
