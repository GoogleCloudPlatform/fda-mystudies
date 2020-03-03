/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
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
import org.springframework.util.CollectionUtils;
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
    logger.info("ParticipantStudiesInfoDaoImpl.getParticipantStudiesInfo()...Started");
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
      logger.info("(DAO)...ParticipantStudiesInfoDaoImpl.getParticipantStudiesInfo()...Started");
      return null;
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<ParticipantStudiesBO> getParticipantStudiesDetails(Integer studyInfoId)
      throws SystemException {

    List<ParticipantStudiesBO> participantStudiesList = null;
    logger.info("ParticipantStudiesInfoDaoImpl - getParticipantStudiesDetails() : starts");
    if (studyInfoId != null) {

      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        Query<ParticipantStudiesBO> query =
            session.createQuery("from ParticipantStudiesBO where studyInfo.id = :studyInfoId");
        query.setParameter("studyInfoId", studyInfoId);
        participantStudiesList = query.getResultList();
        logger.info("ParticipantStudiesInfoDaoImpl - getParticipantStudiesDetails() : ends");
        return participantStudiesList;
      } catch (Exception e) {
        logger.error("ParticipantStudiesInfoDaoImpl - getParticipantStudiesDetails() : error ", e);
        throw new SystemException();
      }

    } else {
      logger.info("ParticipantStudiesInfoDaoImpl - getParticipantStudiesDetails() : ends");
      return participantStudiesList;
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public ParticipantStudiesBO getParticipantStudiesDetails(Integer studyInfoId, Integer siteId)
      throws SystemException {
    List<ParticipantStudiesBO> participantStudiesList = null;
    logger.info("(DAO)...ParticipantStudiesInfoDaoImpl.getParticipantStudiesDetails()...Started");
    if (studyInfoId != null) {
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        Query<ParticipantStudiesBO> query =
            session.createQuery(
                "from ParticipantStudiesBO where studyInfo.id = :studyInfoId and siteBo.id = :siteId");
        query.setParameter("studyInfoId", studyInfoId);
        query.setParameter("siteId", siteId);
        participantStudiesList = query.getResultList();

        if (participantStudiesList != null && participantStudiesList.size() > 0) {
          return participantStudiesList.get(0);
        }
        return null;
      } catch (Exception e) {
        logger.error("(DAO)...UserDetailsDaoImpl.getParticipantStudiesDetails(): (ERROR) ", e);
        throw new SystemException();
      }
    } else {
      logger.info("(DAO)...ParticipantStudiesInfoDaoImpl.getParticipantStudiesDetails()...Ended");
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<ParticipantStudiesBO> getParticipantStudiesByRegistryId(List<Integer> registryId) {
    logger.info("ParticipantStudiesInfoDaoImpl.getParticipantStudiesDetails()...Started");
    List<ParticipantStudiesBO> participantStudiesList = null;
    if (!CollectionUtils.isEmpty(registryId)) {
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        Query<ParticipantStudiesBO> query =
            session.createQuery(
                "from ParticipantStudiesBO where participantRegistrySite.id in (:registryIds)");
        query.setParameter("registryIds", registryId);
        participantStudiesList = query.getResultList();
      } catch (Exception e) {
        logger.error("ParticipantStudiesInfoDaoImpl.getParticipantStudiesDetails()...Error");
        throw e;
      }
    }
    logger.info("ParticipantStudiesInfoDaoImpl.getParticipantStudiesDetails()...Ended");
    return participantStudiesList;
  }
}
