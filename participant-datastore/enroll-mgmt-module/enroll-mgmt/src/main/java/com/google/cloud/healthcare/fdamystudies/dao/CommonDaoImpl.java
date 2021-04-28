/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

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
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CommonDaoImpl implements CommonDao {

  private static final XLogger logger = XLoggerFactory.getXLogger(CommonDaoImpl.class.getName());

  @Autowired private SessionFactory sessionFactory;

  @Override
  public UserDetailsEntity getUserInfoDetails(String userId) {
    logger.entry("Begin getUserInfoDetails()");
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

    logger.exit("getUserInfoDetails() - Ends ");
    return userDetailsEntity;
  }

  @Override
  public String getStudyId(String customStudyId) {
    logger.entry("Begin getStudyId()");
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

    logger.exit("getStudyId() - Ends ");
    return studyInfoId;
  }

  @Override
  public StudyEntity getStudyDetails(String customStudyId) {
    logger.entry("Begin getStudyDetails()");
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
