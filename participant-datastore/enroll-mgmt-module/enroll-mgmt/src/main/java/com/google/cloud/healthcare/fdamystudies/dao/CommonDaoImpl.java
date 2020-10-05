/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import java.util.List;
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

  @Autowired private SessionFactory sessionFactory;

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Override
  public UserDetailsEntity getUserInfoDetails(String userId) {
    logger.info("CommonDaoImpl getUserInfoDetails() - Ends ");
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<UserDetailsEntity> userDetailsCriteriaQuery = null;
    Root<UserDetailsEntity> userDetailsEntityRoot = null;
    Predicate[] userDetailspredicates = new Predicate[1];
    List<UserDetailsEntity> userDetailsList = null;
    UserDetailsEntity userDetailsEntity = null;
    Session session = this.sessionFactory.getCurrentSession();
    criteriaBuilder = session.getCriteriaBuilder();
    userDetailsCriteriaQuery = criteriaBuilder.createQuery(UserDetailsEntity.class);
    userDetailsEntityRoot = userDetailsCriteriaQuery.from(UserDetailsEntity.class);
    userDetailspredicates[0] =
        criteriaBuilder.equal(userDetailsEntityRoot.get(AppConstants.USER_ID), userId);
    userDetailsCriteriaQuery.select(userDetailsEntityRoot).where(userDetailspredicates);
    userDetailsList = session.createQuery(userDetailsCriteriaQuery).getResultList();
    if (!userDetailsList.isEmpty()) {
      userDetailsEntity = userDetailsList.get(0);
    }

    logger.info("CommonDaoImpl getUserInfoDetails() - Ends ");
    return userDetailsEntity;
  }

  @Override
  public String getStudyId(String customStudyId) {
    logger.info("CommonDaoImpl getStudyId() - Starts ");
    CriteriaBuilder criteriaBuilder = null;
    String studyInfoId = "";
    CriteriaQuery<StudyEntity> criteriaQuery = null;
    Root<StudyEntity> root = null;
    Predicate[] predicates = new Predicate[1];
    List<StudyEntity> studyList = null;
    StudyEntity studyInfo = null;
    Session session = this.sessionFactory.getCurrentSession();
    criteriaBuilder = session.getCriteriaBuilder();
    criteriaQuery = criteriaBuilder.createQuery(StudyEntity.class);
    root = criteriaQuery.from(StudyEntity.class);
    predicates[0] = criteriaBuilder.equal(root.get("customId"), customStudyId);
    criteriaQuery.select(root).where(predicates);
    studyList = session.createQuery(criteriaQuery).getResultList();
    if (!studyList.isEmpty()) {
      studyInfo = studyList.get(0);
      studyInfoId = studyInfo.getId();
    }

    logger.info("CommonDaoImpl getStudyId() - Ends ");
    return studyInfoId;
  }

  @Override
  public StudyEntity getStudyDetails(String customStudyId) {
    logger.info("CommonDaoImpl getStudyDetails() - Starts ");
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<StudyEntity> criteriaQuery = null;
    Root<StudyEntity> root = null;
    Predicate[] predicates = new Predicate[1];
    List<StudyEntity> studyList = null;
    StudyEntity studyInfo = null;
    Session session = this.sessionFactory.getCurrentSession();
    criteriaBuilder = session.getCriteriaBuilder();
    criteriaQuery = criteriaBuilder.createQuery(StudyEntity.class);
    root = criteriaQuery.from(StudyEntity.class);
    predicates[0] = criteriaBuilder.equal(root.get("customId"), customStudyId);
    criteriaQuery.select(root).where(predicates);
    studyList = session.createQuery(criteriaQuery).getResultList();
    if (!studyList.isEmpty()) {
      studyInfo = studyList.get(0);
    }

    return studyInfo;
  }
}
