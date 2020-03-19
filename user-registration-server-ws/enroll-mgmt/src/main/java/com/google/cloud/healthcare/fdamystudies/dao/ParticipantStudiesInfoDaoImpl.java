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
    logger.info(
        "(DAO)...ParticipantStudiesInfoDaoImpl.getParticipantStudiesInfo()...Started with "
            + userDetailsId);
    if (userDetailsId != null) {
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        Query<ParticipantStudiesBO> query =
            session.createQuery("from ParticipantStudiesBO where userId = :userDetailsId");
        query.setParameter("userDetailsId", userDetailsId);
        participantStudiesList = query.getResultList();
        return participantStudiesList;
      } catch (Exception e) {
        logger.error("(DAO)...UserDetailsDaoImpl.getParticipantStudiesInfo(): (ERROR) ", e);
        throw new SystemException();
      }
    } else {
      logger.info("(DAO)...ParticipantStudiesInfoDaoImpl.getParticipantStudiesInfo()...Started");
      return null;
    }
  }
}
