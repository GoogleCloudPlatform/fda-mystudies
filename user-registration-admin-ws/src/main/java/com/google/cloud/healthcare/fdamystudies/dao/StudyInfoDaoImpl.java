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
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.StudyInfoBO;

@Repository
public class StudyInfoDaoImpl implements StudyInfoDao {

  private static final Logger logger = LoggerFactory.getLogger(StudyInfoDaoImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Override
  @SuppressWarnings("unchecked")
  public StudyInfoBO getStudyInfoDetails(Integer studyId) throws SystemException {
    logger.info("StudyInfoDaoImpl - getStudyInfoDetails() : starts");
    StudyInfoBO studyInfoBO = null;
    List<StudyInfoBO> studyInfoBOList = null;
    if (studyId != null) {
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        Query<StudyInfoBO> query = session.createQuery("from StudyInfoBO where id = :id");
        query.setParameter("id", studyId);
        studyInfoBOList = query.getResultList();

        if (studyInfoBOList != null && !studyInfoBOList.isEmpty()) {
          studyInfoBO = studyInfoBOList.get(0);
        }
        logger.info("StudyInfoDaoImpl - getStudyInfoDetails() : ends");
        return studyInfoBO;
      } catch (Exception e) {
        logger.error("StudyInfoDaoImpl - getStudyInfoDetails() : error ", e);
        throw new SystemException();
      }
    } else {
      logger.info("StudyInfoDaoImpl - getStudyInfoDetails() : ends");
      return studyInfoBO;
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<StudyInfoBO> getStudies(List<Integer> appInfoIdList) throws SystemException {
    logger.info("StudyInfoDaoImpl - getStudies() : Starts");
    List<StudyInfoBO> studyList = null;

    if ((appInfoIdList != null && !appInfoIdList.isEmpty())) {
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        Query<StudyInfoBO> query =
            session.createQuery("from StudyInfoBO where appInfo.appInfoId in (:appInfoIdList)");
        query.setParameter("appInfoIdList", appInfoIdList);
        studyList = query.getResultList();
        return studyList;
      } catch (Exception e) {
        logger.error("StudyInfoDaoImpl - getStudies() : error ", e);
        throw new SystemException();
      }
    } else {
      logger.info("StudyInfoDaoImpl - getStudies() : ends");
      return studyList;
    }
  }
}
