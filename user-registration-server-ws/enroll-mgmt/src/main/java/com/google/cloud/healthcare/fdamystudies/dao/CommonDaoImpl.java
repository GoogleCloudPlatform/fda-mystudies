package com.google.cloud.healthcare.fdamystudies.dao;

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
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.model.StudyInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetails;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;

@Repository
public class CommonDaoImpl implements CommonDao {

  private static Logger logger = LoggerFactory.getLogger(CommonDaoImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Override
  public Integer getUserInfoDetails(String userId) {
    logger.info("CommonDaoImpl getUserInfoDetails() - Ends ");
    CriteriaBuilder criteriaBuilder = null;
    Integer userDetailsId = null;
    CriteriaQuery<UserDetails> userDetailsCriteriaQuery = null;
    Root<UserDetails> userDetailsBoRoot = null;
    Predicate[] userDetailspredicates = new Predicate[1];
    List<UserDetails> userDetailsBoList = null;
    UserDetails userDetails = null;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      userDetailsCriteriaQuery = criteriaBuilder.createQuery(UserDetails.class);
      userDetailsBoRoot = userDetailsCriteriaQuery.from(UserDetails.class);
      userDetailspredicates[0] =
          criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.KEY_USERID), userId);
      userDetailsCriteriaQuery.select(userDetailsBoRoot).where(userDetailspredicates);
      userDetailsBoList = session.createQuery(userDetailsCriteriaQuery).getResultList();
      if (!userDetailsBoList.isEmpty()) {
        userDetails = userDetailsBoList.get(0);
        userDetailsId = userDetails.getUserDetailsId();
      }
    } catch (Exception e) {
      logger.error("CommonDaoImpl getUserInfoDetails() - error ", e);
    }
    logger.info("CommonDaoImpl getUserInfoDetails() - Ends ");
    return userDetailsId;
  }

  @Override
  public Integer getStudyId(String customStudyId) {
    logger.info("CommonDaoImpl getStudyId() - Ends ");
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
      predicates[0] = criteriaBuilder.equal(root.get(AppConstants.CUSTOM_STUDY_ID), customStudyId);
      criteriaQuery.select(root).where(predicates);
      studyList = session.createQuery(criteriaQuery).getResultList();
      if (!studyList.isEmpty()) {
        studyInfo = studyList.get(0);
        studyInfoId = studyInfo.getStudyInfoId();
      }
    } catch (Exception e) {
      logger.error("CommonDaoImpl getStudyId() - error ", e);
    }
    logger.info("CommonDaoImpl getStudyId() - Ends ");
    return studyInfoId;
  }
}
