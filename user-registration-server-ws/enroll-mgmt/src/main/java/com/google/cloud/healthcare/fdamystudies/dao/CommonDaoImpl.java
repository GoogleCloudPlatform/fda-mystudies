/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.model.ActivityLogBO;
import com.google.cloud.healthcare.fdamystudies.model.StudyInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;
import com.google.cloud.healthcare.fdamystudies.repository.ActivityLogRepository;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CommonDaoImpl implements CommonDao {

  private static Logger logger = LoggerFactory.getLogger(CommonDaoImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Autowired private ActivityLogRepository activityLogRepository;

  @Override
  public UserDetailsBO getUserInfoDetails(String userId) {
    logger.info("CommonDaoImpl getUserInfoDetails() - Ends ");
    CriteriaBuilder criteriaBuilder = null;
    Integer userDetailsId = null;
    CriteriaQuery<UserDetailsBO> userDetailsCriteriaQuery = null;
    Root<UserDetailsBO> userDetailsBoRoot = null;
    Predicate[] userDetailspredicates = new Predicate[1];
    List<UserDetailsBO> userDetailsBoList = null;
    UserDetailsBO userDetailsBO = null;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      userDetailsCriteriaQuery = criteriaBuilder.createQuery(UserDetailsBO.class);
      userDetailsBoRoot = userDetailsCriteriaQuery.from(UserDetailsBO.class);
      userDetailspredicates[0] =
          criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.KEY_USERID), userId);
      userDetailsCriteriaQuery.select(userDetailsBoRoot).where(userDetailspredicates);
      userDetailsBoList = session.createQuery(userDetailsCriteriaQuery).getResultList();
      if (!userDetailsBoList.isEmpty()) {
        userDetailsBO = userDetailsBoList.get(0);
      }
    } catch (Exception e) {
      logger.error("CommonDaoImpl getUserInfoDetails() - error ", e);
    }
    logger.info("CommonDaoImpl getUserInfoDetails() - Ends ");
    return userDetailsBO;
  }

  @Override
  public Integer getStudyId(String customStudyId) {
    logger.info("CommonDaoImpl getStudyId() - Starts ");
    CriteriaBuilder criteriaBuilder = null;
    Integer studyInfoId = null;
    CriteriaQuery<StudyInfoBO> criteriaQuery = null;
    Root<StudyInfoBO> root = null;
    Predicate[] predicates = new Predicate[1];
    List<StudyInfoBO> studyList = null;
    StudyInfoBO studyInfo = null;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      criteriaQuery = criteriaBuilder.createQuery(StudyInfoBO.class);
      root = criteriaQuery.from(StudyInfoBO.class);
      predicates[0] = criteriaBuilder.equal(root.get("customId"), customStudyId);
      criteriaQuery.select(root).where(predicates);
      studyList = session.createQuery(criteriaQuery).getResultList();
      if (!studyList.isEmpty()) {
        studyInfo = studyList.get(0);
        studyInfoId = studyInfo.getId();
      }
    } catch (Exception e) {
      logger.error("CommonDaoImpl getStudyId() - error ", e);
    }
    logger.info("CommonDaoImpl getStudyId() - Ends ");
    return studyInfoId;
  }

  @Override
  public StudyInfoBO getStudyDetails(String customStudyId) {
    logger.info("CommonDaoImpl getStudyDetails() - Starts ");
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<StudyInfoBO> criteriaQuery = null;
    Root<StudyInfoBO> root = null;
    Predicate[] predicates = new Predicate[1];
    List<StudyInfoBO> studyList = null;
    StudyInfoBO studyInfo = null;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      criteriaQuery = criteriaBuilder.createQuery(StudyInfoBO.class);
      root = criteriaQuery.from(StudyInfoBO.class);
      predicates[0] = criteriaBuilder.equal(root.get("customId"), customStudyId);
      criteriaQuery.select(root).where(predicates);
      studyList = session.createQuery(criteriaQuery).getResultList();
      if (!studyList.isEmpty()) {
        studyInfo = studyList.get(0);
      }
    } catch (Exception e) {
      logger.error("CommonDaoImpl getStudyDetails() - error ", e);
    }
    logger.info("CommonDaoImpl getStudyDetails() - Ends ");
    return studyInfo;
  }

  @Override
  public List<ActivityLogBO> createActivityLogList(
      String userId, String activityName, List<String> activityDescList) {
    logger.info("CommonDaoImpl createActivityLogList() - starts ");
    List<ActivityLogBO> activityLogList = new LinkedList<>();
    try {
      for (String activityDesc : activityDescList) {
        ActivityLogBO activityLog = new ActivityLogBO();
        activityLog.setAuthUserId(userId);
        activityLog.setActivityName(activityName);
        activityLog.setActivtyDesc(activityDesc);
        activityLog.setActivityDateTime(LocalDateTime.now());
        activityLogList.add(activityLog);
      }
      activityLogRepository.saveAll(activityLogList);
    } catch (Exception e) {
      logger.error("CommonDaoImpl createActivityLogList() - error ", e);
    }
    logger.info("CommonDaoImpl createActivityLogList() - ends ");
    return activityLogList;
  }

  @Override
  public ActivityLogBO createActivityLog(String userId, String activityName, String activityDesc) {
    logger.info("CommonDaoImpl createActivityLog() - starts ");
    ActivityLogBO activityLog = new ActivityLogBO();
    try {
      activityLog.setAuthUserId(userId);
      activityLog.setActivityName(activityName);
      activityLog.setActivtyDesc(activityDesc);
      activityLog.setActivityDateTime(LocalDateTime.now());
      activityLogRepository.save(activityLog);
    } catch (Exception e) {
      logger.error("CommonDaoImpl createActivityLog() - error ", e);
    }
    logger.info("CommonDaoImpl createActivityLog() - ends ");
    return activityLog;
  }
}
