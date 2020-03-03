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

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySite;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.URWebAppWSConstants;
import com.google.cloud.healthcare.fdamystudies.utils.URWebAppWSUtil;

@Repository
public class ParticipantRegistrySiteDAOImpl implements ParticipantRegistrySiteDAO {

  public static final Logger logger = LoggerFactory.getLogger(ParticipantRegistrySiteDAOImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Override
  public void saveParticipantRegistry(ParticipantRegistrySite participantRegistrySite) {
    logger.info("ParticipantRegistrySiteDAOImpl - saveParticipantRegistry - starts");
    Transaction transaction = null;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      transaction = session.beginTransaction();
      session.save(participantRegistrySite);
      transaction.commit();
      session.close();
      logger.info("ParticipantRegistrySiteDAOImpl - saveParticipantRegistry - ends");
    } catch (Exception e) {
      logger.error("ParticipantRegistrySiteDAOImpl - saveParticipantRegistry - error");
      throw e;
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<ParticipantRegistrySite> getParticipantRegistryForSite(
      String onboardingStatus, Integer siteId) {
    List<ParticipantRegistrySite> participants = null;
    logger.info("ParticipantRegistrySiteDAOImpl - getParticipantRegistryForSite - starts");
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {

      if (!URWebAppWSConstants.ONBOARDING_STATUS_ALL.equalsIgnoreCase(onboardingStatus)) {
        Query<ParticipantRegistrySite> query =
            session.createQuery(
                "from ParticipantRegistrySite where sites.id = :siteId and onboardingStatus = :onboardingStatus order by created desc");
        query.setParameter("siteId", siteId);
        query.setParameter("onboardingStatus", onboardingStatus);
        participants = query.getResultList();
      } else {
        Query<ParticipantRegistrySite> query =
            session.createQuery(
                "from ParticipantRegistrySite where sites.id = :siteId order by created desc");
        query.setParameter("siteId", siteId);
        participants = query.getResultList();
      }

    } catch (Exception e) {
      logger.error("ParticipantRegistrySiteDAOImpl - getParticipantRegistryForSite - error");
      throw e;
    }
    logger.info("ParticipantRegistrySiteDAOImpl - getParticipantRegistryForSite - ends");
    return participants;
  }

  @Override
  @SuppressWarnings("unchecked")
  public ParticipantRegistrySite getParticipantRegistry(Integer participantSiteId)
      throws SystemException {

    ParticipantRegistrySite participantRegistrySite = null;
    List<ParticipantRegistrySite> participantRegistrySiteList = null;
    if (participantSiteId != null) {
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        Query<ParticipantRegistrySite> query =
            session.createQuery("from ParticipantRegistrySite where id = :id");
        query.setParameter("id", participantSiteId);
        participantRegistrySiteList = query.getResultList();

        if (participantRegistrySiteList != null && participantRegistrySiteList.size() > 0) {
          participantRegistrySite = participantRegistrySiteList.get(0);
        }
        logger.info(
            "(DAO)...StudyInfoDaoImpl.getStudyInfoDetails()...Ended " + participantRegistrySite);
        return null;
      } catch (Exception e) {
        logger.error("(DAO)...StudyInfoDaoImpl - getStudyInfoDetails: (ERROR) ", e);
        throw new SystemException();
      }
    } else {
      logger.info(
          "(DAO)...StudyInfoDaoImpl.getStudyInfoDetails()...Ended " + participantRegistrySite);
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public Map<String, Integer> getParticipantCountByOnboardingStatus(Integer siteId) {
    logger.info("ParticipantRegistrySiteDAOImpl - getParticipantCountByOnboardingStatus - starts");
    Map<String, Integer> counts = URWebAppWSUtil.getDefaultOnboardingStatusCountMap();
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      String queryStr =
          "SELECT onboarding_status, count(distinct email) FROM participant_registry_site WHERE site_id = :siteId group by onboarding_status";
      Query query = session.createSQLQuery(queryStr);
      query.setParameter("siteId", siteId);
      List<Object[]> rows = query.getResultList();
      if (!CollectionUtils.isEmpty(rows)) {
        for (Object[] row : rows) {
          counts.put((String) row[0], ((BigInteger) row[1]).intValue());
        }
      }

    } catch (Exception e) {
      logger.error(
          "ParticipantRegistrySiteDAOImpl - getParticipantCountByOnboardingStatus - error");
      throw e;
    }
    logger.info("ParticipantRegistrySiteDAOImpl - getParticipantCountByOnboardingStatus - ends");
    return counts;
  }

  public String updateOnboardingStatus(List<Integer> ids, String status) {
    logger.info("ParticipantRegistrySiteDAOImpl - updateOnboardingStatus - starts");
    Transaction transaction = null;

    if (!CollectionUtils.isEmpty(ids)) {
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {

        transaction = session.beginTransaction();
        Query query =
            session.createQuery(
                "UPDATE ParticipantRegistrySite set onboardingStatus = :status Where id in (:ids)");
        query.setParameter("status", status);
        query.setParameter("ids", ids);
        int rowsUpdated = query.executeUpdate();
        if (rowsUpdated > 0) {
          transaction.commit();
          return AppConstants.SUCCESS;
        }
      } catch (Exception e) {
        logger.error("ParticipantRegistrySiteDAOImpl - updateOnboardingStatus - error");
        if (transaction != null) {
          try {
            transaction.rollback();
          } catch (Exception e1) {
            logger.error("ParticipantRegistrySiteDAOImpl - updateOnboardingStatus - error");
          }
        }
        return AppConstants.FAILURE;
      }
    }
    logger.info("ParticipantRegistrySiteDAOImpl - updateOnboardingStatus - ends");
    return AppConstants.SUCCESS;
  }

  @SuppressWarnings("unchecked")
  public List<ParticipantRegistrySite> getParticipantRegistry(List<Integer> participantSiteId) {
    logger.info("ParticipantRegistrySiteDAOImpl - getParticipantRegistry - ends");
    List<ParticipantRegistrySite> participantRegistrySiteList = null;
    if (participantSiteId != null) {
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        Query<ParticipantRegistrySite> query =
            session.createQuery("from ParticipantRegistrySite where id in (:id)");
        query.setParameter("id", participantSiteId);
        participantRegistrySiteList = query.getResultList();

        logger.info("ParticipantRegistrySiteDAOImpl - getParticipantRegistry - ends");
      } catch (Exception e) {
        logger.info("ParticipantRegistrySiteDAOImpl - getParticipantRegistry - error");
        throw e;
      }
    } else {
      logger.info("ParticipantRegistrySiteDAOImpl - getParticipantRegistry - ends");
    }
    return participantRegistrySiteList;
  }
}
