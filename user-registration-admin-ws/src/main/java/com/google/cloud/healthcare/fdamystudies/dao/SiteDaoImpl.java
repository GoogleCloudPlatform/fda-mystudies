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

import java.util.ArrayList;
import java.util.Date;
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
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySite;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.model.SiteBo;
import com.google.cloud.healthcare.fdamystudies.model.StudyConsentBO;
import com.google.cloud.healthcare.fdamystudies.model.StudyPermission;

@Repository
public class SiteDaoImpl implements SiteDao {

  private static Logger logger = LoggerFactory.getLogger(SiteDaoImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  Transaction transaction = null;

  @Override
  public List<ParticipantStudiesBO> getparticipantsEnrollment(Integer participantRegistrySiteId) {
    logger.info("SiteDAOImpl - getparticipantsEnrollment() : starts");
    List<ParticipantStudiesBO> participantsEnrollments = new ArrayList<>();
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      Query query =
          session.createQuery("from ParticipantStudiesBO where participantRegistrySite.id = :id");
      query.setParameter("id", participantRegistrySiteId);
      participantsEnrollments = query.getResultList();
    } catch (Exception e) {
      logger.info("SiteDAOImpl - getparticipantsEnrollment() : errors");
    }
    logger.info("SiteDAOImpl - getparticipantsEnrollment() : ends");
    return participantsEnrollments;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<StudyConsentBO> getStudyConsentsOfParticipantStudyIds(
      List<Integer> participantStudyIds) {
    logger.info("SiteDAOImpl - getStudyConsentsOfParticipantStudyIds() : starts");
    List<StudyConsentBO> studyConsents = new ArrayList<>();
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      Query query =
          session.createQuery(
              "from StudyConsentBO where participantStudiesBO.id in (:participantStudyIds)");
      query.setParameter("participantStudyIds", participantStudyIds);
      studyConsents = query.getResultList();
    } catch (Exception e) {
      logger.info("SiteDAOImpl - getStudyConsentsOfParticipantStudyIds() : errors");
    }
    logger.info("SiteDAOImpl - getStudyConsentsOfParticipantStudyIds() : ends");
    return studyConsents;
  }

  @Override
  public ParticipantRegistrySite getParticipantSiteRegistry(Integer participantRegistrySiteId) {
    logger.info("SiteDAOImpl - getParticipantSiteRegistry() : starts");
    ParticipantRegistrySite participantRegistry = null;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      Query<ParticipantRegistrySite> query =
          session.createQuery("from ParticipantRegistrySite where id = :id");
      query.setParameter("id", participantRegistrySiteId);
      participantRegistry = query.uniqueResult();
    } catch (Exception e) {
      logger.info("SiteDAOImpl - getParticipantSiteRegistry() : errors");
    }
    logger.info("SiteDAOImpl - getParticipantSiteRegistry() : ends");
    return participantRegistry;
  }

  @SuppressWarnings("unchecked")
  public List<StudyPermission> getStudyPermissionsOfUserByStudyIds(
      List<Integer> userStudyIds, Integer userId) {
    logger.info(" SiteDaoImpl - getStudyPermissionsOfStudyIds():starts");
    List<StudyPermission> studyPermissions = new ArrayList<>();
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      studyPermissions =
          session
              .createQuery(
                  "from StudyPermission where studyInfo.id in (:studyIds) and urAdminUser.id=:userId")
              .setParameterList("studyIds", userStudyIds)
              .setParameter("userId", userId)
              .getResultList();
    } catch (Exception e) {
      logger.info("SiteDaoImpl - getStudyPermissionsOfStudyIds() : error", e);
    }

    logger.info("SiteDaoImpl - getStudyPermissionsOfStudyIds() : ends");
    return studyPermissions;
  }

  @Override
  public SiteBo getSiteDetails(Integer siteId) {
    logger.info("SiteDaoImpl - getSiteDetails - starts");
    SiteBo site = null;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      site =
          (SiteBo)
              session
                  .createQuery("from SiteBo where id = :siteId")
                  .setParameter("siteId", siteId)
                  .getSingleResult();
    } catch (Exception e) {
      logger.error("SiteDaoImpl - getSiteDetails - error");
      throw e;
    }
    logger.info("SiteDaoImpl - getSiteDetails - ends");
    return site;
  }

  @SuppressWarnings("unchecked")
  public List<SiteBo> getSitesForLocation(Integer locationId, Integer status) {
    logger.info("SiteDAOImpl - getSitesForLocation() : starts");
    List<SiteBo> sites = new ArrayList<>();
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      Query<SiteBo> query =
          session.createQuery("from SiteBo where locations.id = :locationId and status = :status");
      query.setParameter("locationId", locationId);
      query.setParameter("status", status);
      sites = query.getResultList();
    } catch (Exception e) {
      logger.info("SiteDAOImpl - getSitesForLocation() : errors");
    }
    logger.info("SiteDAOImpl - getSitesForLocation() : ends");
    return sites;
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<SiteBo> getSiteDetailsList(Integer studyId) throws SystemException {
    logger.info("(DAO)...SiteDAOImpl.getStudyInfoDetails()...Started");
    List<SiteBo> siteList = null;
    if (studyId != null) {
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        Query<SiteBo> query = session.createQuery("from SiteBo where studyInfo.id = :studyId");
        query.setParameter("studyId", studyId);
        siteList = query.getResultList();
        logger.info("(DAO)...SiteDAOImpl.getStudyInfoDetails()...Ended " + siteList);
        return siteList;
      } catch (Exception e) {
        logger.error("(DAO)...SiteDAOImpl - getStudyInfoDetails: (ERROR) ", e);
        throw new SystemException();
      }
    } else {
      logger.info("(DAO)...SiteDAOImpl.getStudyInfoDetails()...Ended " + siteList);
      return siteList;
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void updateEnrollment(String enrollment, Integer id, String currentStatus) {
    logger.info("SiteDAOImpl - updateEnrollment() : starts");
    Transaction transaction = null;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      String sqlString = "";
      transaction = session.beginTransaction();
      if ("I".equals(currentStatus)) {
        sqlString =
            "UPDATE participant_registry_site set enrollment_token = :enrollmentToken, invitation_date = CURRENT_TIMESTAMP, enrollment_token_expiry = :expiry where id = :id";
      } else if ("N".equals(currentStatus)) {
        sqlString =
            "UPDATE participant_registry_site set enrollment_token = :enrollmentToken, invitation_date = CURRENT_TIMESTAMP, invitation_count = (invitation_count + 1), enrollment_token_expiry = :expiry, onboarding_status = 'I' where id = :id";
      }
      Query<ParticipantRegistrySite> query = session.createSQLQuery(sqlString);
      query.setParameter("id", id);
      query.setParameter("enrollmentToken", enrollment);
      Date date = new Date(System.currentTimeMillis() + 48 * 60 * 60 * 1000L);
      query.setParameter("expiry", date);
      int rowsUpdated = query.executeUpdate();
      transaction.commit();
    } catch (Exception e) {
      logger.info("SiteDAOImpl - updateEnrollment() : errors");
      if (transaction != null) {
        transaction.rollback();
      }
    }
    logger.info("SiteDAOImpl - updateEnrollment() : ends");
    return;
  }

  @Override
  public StudyConsentBO getstudyConsentBO(Integer consentId) {
    logger.info(" SiteDaoImpl - getstudyConsentBO():starts");
    StudyConsentBO studyConsentBO = new StudyConsentBO();
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      studyConsentBO =
          (StudyConsentBO)
              session
                  .createQuery("from StudyConsentBO where id =:consentId")
                  .setParameter("consentId", consentId)
                  .getSingleResult();
    } catch (Exception e) {
      logger.info("SiteDaoImpl - getstudyConsentBO() : error", e);
    }

    logger.info("SiteDaoImpl - getstudyConsentBO() : ends");
    return studyConsentBO;
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<SiteBo> getSites(List<Integer> studyIdList) throws SystemException {
    logger.info("SiteDaoImpl - getSites() : starts");
    List<SiteBo> studyList = null;

    if ((studyIdList != null && !studyIdList.isEmpty())) {
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        Query<SiteBo> query =
            session.createQuery("from SiteBo where studyInfo.id in (:studyIdList)");
        query.setParameter("studyIdList", studyIdList);
        studyList = query.getResultList();
        return studyList;
      } catch (Exception e) {
        logger.error("SiteDaoImpl - getSites() : error ", e);
        throw new SystemException();
      }
    } else {
      logger.info("SiteDaoImpl - getSites() : ends");
      return studyList;
    }
  }
}
