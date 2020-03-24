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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;

@Repository
public class ParticipantStudiesInfoDaoImpl implements ParticipantStudiesInfoDao {

  private static final Logger logger = LoggerFactory.getLogger(ParticipantStudiesInfoDaoImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Override
  @SuppressWarnings("unchecked")
  public List<ParticipantStudiesBO> getParticipantStudiesInfo(Integer userDetailsId)
      throws SystemException {

    List<ParticipantStudiesBO> participantStudiesList = null;
    logger.info("(DAO)...ParticipantStudiesInfoDaoImpl.getParticipantStudiesInfo()...Started");
    if (userDetailsId != null) {
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        Query<ParticipantStudiesBO> query =
            session.createQuery(
                "from ParticipantStudiesBO where userDetails.userDetailsId = :userDetailsId");
        query.setParameter("userDetailsId", userDetailsId);
        participantStudiesList = query.getResultList();
        return participantStudiesList;
      } catch (Exception e) {
        logger.error("(DAO)...UserDetailsDaoImpl.getParticipantStudiesInfo(): (ERROR) ", e);
        throw new SystemException();
      }
    } else {
      logger.info("(DAO)...ParticipantStudiesInfoDaoImpl.getParticipantStudiesInfo()...Ended");
      return null;
    }
  }
}
