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
import com.google.cloud.healthcare.fdamystudies.model.StudyPermission;

@Repository
public class StudyPermissionDaoImpl implements StudyPermissionDao {

  public static final Logger logger = LoggerFactory.getLogger(StudyPermissionDaoImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Override
  @SuppressWarnings("unchecked")
  public StudyPermission getStudyPermissionForUser(Integer studyId, Integer userId)
      throws SystemException {

    logger.info("StudyPermissionDaoImpl - getStudyPermissionForUser() : starts");

    if (userId != null && studyId != null) {
      List<StudyPermission> studyPermissionList = null;
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        Query<StudyPermission> query =
            session.createQuery(
                "from StudyPermission where studyInfo.id = :studyId and urAdminUser.id= :userId");
        query.setParameter("studyId", studyId);
        query.setParameter("userId", userId);
        studyPermissionList = query.getResultList();
        if (studyPermissionList != null && !studyPermissionList.isEmpty()) {
          return studyPermissionList.get(0);
        } else return null;
      } catch (Exception e) {
        logger.error("StudyPermissionDaoImpl - getStudyPermissionForUser() : error ", e);
        throw new SystemException();
      }
    } else {
      logger.info("StudyPermissionDaoImpl - getStudyPermissionForUser() : ends");
      return null;
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Integer> getStudyPermission(Integer adminUserId, Integer appInfoId)
      throws SystemException {

    List<Integer> studyPermissionIdList = null;

    logger.info("StudyPermissionDaoImpl.getStudyPermission()...Started");
    if (adminUserId != null && appInfoId != null) {

      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        List<StudyPermission> studyPermissionList = null;
        studyPermissionIdList = new ArrayList<>();
        Query<StudyPermission> query =
            session.createQuery(
                "from StudyPermission where urAdminUser.id = :adminId and appInfo.appInfoId = :appInfoId");
        query.setParameter("adminId", adminUserId);
        query.setParameter("appInfoId", appInfoId);
        studyPermissionList = query.getResultList();

        if (studyPermissionList != null && studyPermissionList.size() > 0) {
          for (StudyPermission studyPermission : studyPermissionList) {
            Integer studyId = studyPermission.getStudyInfo().getId();
            studyPermissionIdList.add(studyId);
          }
        }

        logger.info(
            "StudyPermissionDaoImpl.getStudyPermission()...Ended with: " + studyPermissionIdList);
        return studyPermissionIdList;
      } catch (Exception e) {
        logger.error("StudyPermissionDaoImpl - getStudyPermission: (ERROR) ", e);
        throw new SystemException();
      }
    } else {
      logger.info("StudyPermissionDaoImpl.getStudyPermission()...Ended");
      return null;
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<StudyPermission> getStudyPermissionDetails(Integer adminUserId, Integer appInfoId)
      throws SystemException {
    logger.info("StudyPermissionDaoImpl.getStudyPermissionDetails()...Started");
    if (adminUserId != null && appInfoId != null) {

      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        List<StudyPermission> studyPermissionList = null;
        Query<StudyPermission> query =
            session.createQuery(
                "from StudyPermission where urAdminUser.id = :adminId and appInfo.appInfoId = :appInfoId");
        query.setParameter("adminId", adminUserId);
        query.setParameter("appInfoId", appInfoId);
        studyPermissionList = query.getResultList();

        logger.info(
            "AppPermissionDaoImpl.getStudyPermissionDetails()...Ended with: "
                + studyPermissionList);
        return studyPermissionList;
      } catch (Exception e) {
        logger.error("ParticipantRegistrySiteDAOImpl - getStudyPermissionDetails: (ERROR) ", e);
        throw new SystemException();
      }
    } else {
      logger.info("StudyPermissionDaoImpl.getStudyPermissionDetails()...Ended");
      return null;
    }
  }
}
