/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.common.CommonConstants;
import com.google.cloud.healthcare.fdamystudies.common.OnboardingStatus;
import com.google.cloud.healthcare.fdamystudies.common.UserStatus;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoEntity;
import com.google.cloud.healthcare.fdamystudies.model.LoginAttemptsEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserAppDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.repository.UserDetailsRepository;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserProfileManagementDaoImpl implements UserProfileManagementDao {

  private static final Logger logger = LoggerFactory.getLogger(UserProfileManagementDaoImpl.class);

  @Autowired private SessionFactory sessionFactory;

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Autowired UserDetailsRepository userDetailsRepository;

  @Autowired CommonDao commonDao;

  @Override
  public UserDetailsEntity getParticipantInfoDetails(String userId) {

    logger.info("UserProfileManagementDaoImpl getParticipantInfoDetails() - Starts ");
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<UserDetailsEntity> criteriaQuery = null;
    Root<UserDetailsEntity> userDetailsBoRoot = null;
    Predicate[] predicates = new Predicate[1];
    List<UserDetailsEntity> userDetailsBoList = null;
    UserDetailsEntity userDetails = null;
    Session session = this.sessionFactory.getCurrentSession();
    criteriaBuilder = session.getCriteriaBuilder();
    criteriaQuery = criteriaBuilder.createQuery(UserDetailsEntity.class);
    userDetailsBoRoot = criteriaQuery.from(UserDetailsEntity.class);
    predicates[0] = criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.KEY_USERID), userId);
    criteriaQuery.select(userDetailsBoRoot).where(predicates);
    userDetailsBoList = session.createQuery(criteriaQuery).getResultList();
    if (!userDetailsBoList.isEmpty()) {
      userDetails = userDetailsBoList.get(0);
    }
    logger.info("UserProfileManagementDaoImpl getParticipantInfoDetails() - Ends ");
    return userDetails;
  }

  @Override
  public AuthInfoEntity getAuthInfo(UserDetailsEntity userDetailsId) {
    logger.info("UserProfileManagementDaoImpl getAuthInfo() - Starts ");
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<AuthInfoEntity> criteriaQuery = null;
    Root<AuthInfoEntity> authInfoBoRoot = null;
    Predicate[] predicates = new Predicate[1];
    List<AuthInfoEntity> authInfoBoList = null;
    AuthInfoEntity authInfo = null;
    Session session = this.sessionFactory.getCurrentSession();
    criteriaBuilder = session.getCriteriaBuilder();
    criteriaQuery = criteriaBuilder.createQuery(AuthInfoEntity.class);
    authInfoBoRoot = criteriaQuery.from(AuthInfoEntity.class);
    predicates[0] = criteriaBuilder.equal(authInfoBoRoot.get("userDetails"), userDetailsId);
    criteriaQuery.select(authInfoBoRoot).where(predicates);
    authInfoBoList = session.createQuery(criteriaQuery).getResultList();
    if (!authInfoBoList.isEmpty()) {
      authInfo = authInfoBoList.get(0);
    }
    logger.info("UserProfileManagementDaoImpl getAuthInfo() - Ends ");
    return authInfo;
  }

  @Override
  public ErrorBean updateUserProfile(
      String userId, UserDetailsEntity userDetail, AuthInfoEntity authInfo) {
    logger.info("UserProfileManagementDaoImpl updateUserProfile() - Starts ");
    ErrorBean errorBean = null;
    Boolean isUpdatedAuthInfo = false;
    Session session = this.sessionFactory.getCurrentSession();

    if (null != userDetail) {
      session.saveOrUpdate(userDetail);
      errorBean = new ErrorBean(ErrorCode.EC_200.code(), ErrorCode.EC_200.errorMessage());
      if (null != authInfo) {
        session.saveOrUpdate(authInfo);
        isUpdatedAuthInfo = true;
      }
    } else {
      errorBean = new ErrorBean(ErrorCode.EC_61.code(), ErrorCode.EC_61.errorMessage());
    }
    logger.info("UserProfileManagementDaoImpl updateUserProfile() - Starts ");
    return errorBean;
  }

  @Override
  public UserDetailsEntity getParticipantDetailsByEmail(String email, AppEntity app) {

    logger.info("UserProfileManagementDaoImpl getParticipantDetailsByEmail() - Starts ");
    UserDetailsEntity userDetails = null;
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<UserDetailsEntity> criteriaQuery = null;
    Root<UserDetailsEntity> userDetailsBoRoot = null;
    List<Predicate> userDetailsPredicates = new ArrayList<>();
    List<UserDetailsEntity> userDetailsBoList = null;

    Session session = this.sessionFactory.getCurrentSession();
    criteriaBuilder = session.getCriteriaBuilder();
    criteriaQuery = criteriaBuilder.createQuery(UserDetailsEntity.class);
    userDetailsBoRoot = criteriaQuery.from(UserDetailsEntity.class);

    userDetailsPredicates.add(
        criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.EMAIL), email));
    userDetailsPredicates.add(criteriaBuilder.equal(userDetailsBoRoot.get("app"), app));
    userDetailsPredicates.add(criteriaBuilder.notEqual(userDetailsBoRoot.get("emailCode"), "Null"));
    criteriaQuery
        .select(userDetailsBoRoot)
        .where(userDetailsPredicates.toArray(new Predicate[userDetailsPredicates.size()]));
    userDetailsBoList = session.createQuery(criteriaQuery).getResultList();
    if (!userDetailsBoList.isEmpty()) {
      userDetails = userDetailsBoList.get(0);
    }
    logger.info("UserProfileManagementDaoImpl getParticipantDetailsByEmail() - Ends ");
    return userDetails;
  }

  @Override
  public LoginAttemptsEntity getLoginAttempts(String email) {
    logger.info("UserProfileManagementDaoImpl getParticipantDetailsByEmail() - Starts ");

    LoginAttemptsEntity loginAttempt = null;
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<LoginAttemptsEntity> criteriaQuery = null;
    Root<LoginAttemptsEntity> loginAttemptRoot = null;
    Predicate[] predicates = new Predicate[1];
    List<LoginAttemptsEntity> loginAttemptList = null;

    Session session = this.sessionFactory.getCurrentSession();
    criteriaBuilder = session.getCriteriaBuilder();
    criteriaQuery = criteriaBuilder.createQuery(LoginAttemptsEntity.class);
    loginAttemptRoot = criteriaQuery.from(LoginAttemptsEntity.class);
    predicates[0] = criteriaBuilder.equal(loginAttemptRoot.get(AppConstants.EMAIL), email);
    criteriaQuery.select(loginAttemptRoot).where(predicates);
    loginAttemptList = session.createQuery(criteriaQuery).getResultList();
    if (!loginAttemptList.isEmpty()) {
      loginAttempt = loginAttemptList.get(0);
    }
    logger.info("UserProfileManagementDaoImpl getParticipantDetailsByEmail() - Ends ");
    return loginAttempt;
  }

  @Override
  public UserDetailsEntity saveParticipant(UserDetailsEntity participant) {
    logger.info("UserProfileManagementDaoImpl saveParticipant() - Starts ");
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<UserDetailsEntity> criteriaQuery = null;
    Predicate[] predicates = new Predicate[1];
    Root<UserDetailsEntity> userDetailsRoot = null;
    List<UserDetailsEntity> userDetailsList = null;
    UserDetailsEntity userDetails = null;
    Session session = this.sessionFactory.getCurrentSession();
    criteriaBuilder = session.getCriteriaBuilder();
    criteriaQuery = criteriaBuilder.createQuery(UserDetailsEntity.class);
    userDetailsRoot = criteriaQuery.from(UserDetailsEntity.class);
    predicates[0] =
        criteriaBuilder.equal(
            userDetailsRoot.get(AppConstants.USER_DETAILS_ID), participant.getId());
    criteriaQuery.select(userDetailsRoot).where(predicates);
    userDetailsList = session.createQuery(criteriaQuery).getResultList();
    if (!userDetailsList.isEmpty()) {
      userDetails = userDetailsList.get(0);
      userDetails.setEmailCode(participant.getEmailCode());
      userDetails.setCodeExpireDate(participant.getCodeExpireDate());
      session.update(userDetails);
    }
    logger.info("UserProfileManagementDaoImpl saveParticipant() - Ends ");
    return userDetails;
  }

  @Override
  public void resetLoginAttempts(String email) {
    CriteriaBuilder criteriaBuilder = null;
    CriteriaDelete<LoginAttemptsEntity> criteriaDelete = null;
    Root<LoginAttemptsEntity> attemptRoot = null;
    logger.info("UserProfileManagementDaoImpl - resetLoginAttempts() - starts");
    Session session = this.sessionFactory.getCurrentSession();
    criteriaBuilder = session.getCriteriaBuilder();
    criteriaDelete = criteriaBuilder.createCriteriaDelete(LoginAttemptsEntity.class);
    attemptRoot = criteriaDelete.from(LoginAttemptsEntity.class);
    criteriaDelete.where(criteriaBuilder.equal(attemptRoot.get("email"), email));
    session.createQuery(criteriaDelete).executeUpdate();

    logger.info("UserProfileManagementDaoImpl - resetLoginAttempts() - end");
  }

  @Override
  public UserDetailsEntity getParticipantDetails(String userId) {
    logger.info("UserProfileManagementDaoImpl getParticipantDetails() - Starts ");
    UserDetailsEntity userDetails = null;
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<UserDetailsEntity> criteriaQuery = null;
    Root<UserDetailsEntity> userDetailsBoRoot = null;
    Predicate[] predicates = new Predicate[1];
    List<UserDetailsEntity> userDetailsBoList = null;
    Session session = this.sessionFactory.getCurrentSession();
    criteriaBuilder = session.getCriteriaBuilder();
    criteriaQuery = criteriaBuilder.createQuery(UserDetailsEntity.class);
    userDetailsBoRoot = criteriaQuery.from(UserDetailsEntity.class);
    predicates[0] = criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.KEY_USERID), userId);
    criteriaQuery.select(userDetailsBoRoot).where(predicates);
    userDetailsBoList = session.createQuery(criteriaQuery).getResultList();
    if (!userDetailsBoList.isEmpty()) {
      userDetails = userDetailsBoList.get(0);
    }
    logger.info("UserProfileManagementDaoImpl getParticipantDetails() - Ends ");
    return userDetails;
  }

  @Override
  public void deactivateAcct(String userId, List<String> deleteData, String userDetailsId) {
    logger.info("UserProfileManagementDaoImpl deActivateAcct() - Starts ");
    CriteriaBuilder criteriaBuilder = null;

    CriteriaDelete<UserAppDetailsEntity> criteriaUserAppDetailsDelete = null;
    Root<UserAppDetailsEntity> userAppDetailsRoot = null;
    CriteriaDelete<AuthInfoEntity> criteriaAuthInfoDelete = null;
    Root<AuthInfoEntity> authInfoRoot = null;

    CriteriaUpdate<ParticipantStudyEntity> criteriaParticipantStudiesUpdate = null;
    Root<ParticipantStudyEntity> participantStudiesRoot = null;
    List<Predicate> studyIdPredicates = new ArrayList<>();
    Predicate[] studyInfoIdPredicates = new Predicate[1];
    Expression<String> studyIdExpression = null;
    Predicate[] predicatesAuthInfo = new Predicate[1];
    Predicate[] predicatesUserAppDetails = new Predicate[1];
    CriteriaQuery<StudyEntity> studyInfoQuery = null;
    Root<StudyEntity> rootStudy = null;
    List<StudyEntity> studyInfoBoList = null;
    UserDetailsEntity userDetails = null;
    Session session = this.sessionFactory.getCurrentSession();
    criteriaBuilder = session.getCriteriaBuilder();
    if (deleteData != null && !deleteData.isEmpty()) {
      studyInfoQuery = criteriaBuilder.createQuery(StudyEntity.class);
      rootStudy = studyInfoQuery.from(StudyEntity.class);
      studyIdExpression = rootStudy.get("customId");
      studyInfoIdPredicates[0] = studyIdExpression.in(deleteData);
      studyInfoQuery.select(rootStudy).where(studyInfoIdPredicates);
      studyInfoBoList = session.createQuery(studyInfoQuery).getResultList();
      studyInfoBoList.stream().map(StudyEntity::getId).collect(Collectors.toList());
      criteriaParticipantStudiesUpdate =
          criteriaBuilder.createCriteriaUpdate(ParticipantStudyEntity.class);
      participantStudiesRoot = criteriaParticipantStudiesUpdate.from(ParticipantStudyEntity.class);
      criteriaParticipantStudiesUpdate.set("status", "Withdrawn");
      criteriaParticipantStudiesUpdate.set("participantId", null);
      criteriaParticipantStudiesUpdate.set(
          "withdrawalDate", new Timestamp(Instant.now().toEpochMilli()));
      userDetails = session.get(UserDetailsEntity.class, userDetailsId);
      studyIdPredicates.add(
          criteriaBuilder.equal(participantStudiesRoot.get("userDetails"), userDetails));
      studyIdExpression = participantStudiesRoot.get("study");
      studyIdPredicates.add(studyIdExpression.in(studyInfoBoList));
      criteriaParticipantStudiesUpdate.where(
          studyIdPredicates.toArray(new Predicate[studyIdPredicates.size()]));
      session.createQuery(criteriaParticipantStudiesUpdate).executeUpdate();

      session
          .createSQLQuery(
              "UPDATE participant_registry_site SET onboarding_status=:onboardingStatus, "
                  + "disabled_time=:disabledDate WHERE "
                  + "id IN (SELECT participant_registry_site_id FROM participant_study_info where "
                  + "user_details_id=:userDetailsId and study_info_id IN (:studyIds))")
          .setParameter("onboardingStatus", OnboardingStatus.DISABLED.getCode())
          .setParameter("disabledDate", new Timestamp(Instant.now().toEpochMilli()))
          .setParameter("userDetailsId", userDetails)
          .setParameter("studyIds", studyInfoBoList)
          .executeUpdate();
    }

    criteriaAuthInfoDelete = criteriaBuilder.createCriteriaDelete(AuthInfoEntity.class);
    authInfoRoot = criteriaAuthInfoDelete.from(AuthInfoEntity.class);
    predicatesAuthInfo[0] = criteriaBuilder.equal(authInfoRoot.get("userDetails"), userDetails);
    criteriaAuthInfoDelete.where(predicatesAuthInfo);
    session.createQuery(criteriaAuthInfoDelete).executeUpdate();

    criteriaUserAppDetailsDelete = criteriaBuilder.createCriteriaDelete(UserAppDetailsEntity.class);
    userAppDetailsRoot = criteriaUserAppDetailsDelete.from(UserAppDetailsEntity.class);
    predicatesUserAppDetails[0] =
        criteriaBuilder.equal(userAppDetailsRoot.get("userDetails"), userDetails);
    criteriaUserAppDetailsDelete.where(predicatesUserAppDetails);
    session.createQuery(criteriaUserAppDetailsDelete).executeUpdate();

    logger.info("UserProfileManagementDaoImpl deActivateAcct() - Ends ");
  }

  @Override
  @Transactional(readOnly = true)
  public AppEntity getAppPropertiesDetailsByAppId(String appId) {
    logger.info("UserProfileManagementDaoImpl - resetLoginAttempts() - starts");
    AppEntity appPropertiesDetails = null;
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<AppEntity> criteriaQuery = null;
    Root<AppEntity> appDetailsRoot = null;
    List<AppEntity> appPropetiesDetailList = null;
    Session session = this.sessionFactory.getCurrentSession();
    criteriaBuilder = session.getCriteriaBuilder();
    criteriaQuery = criteriaBuilder.createQuery(AppEntity.class);
    appDetailsRoot = criteriaQuery.from(AppEntity.class);
    criteriaQuery.where(
        criteriaBuilder.equal(appDetailsRoot.get(AppConstants.APPLICATION_ID), appId));
    appPropetiesDetailList = session.createQuery(criteriaQuery).getResultList();

    if (!appPropetiesDetailList.isEmpty()) {
      appPropertiesDetails = appPropetiesDetailList.get(0);
    }
    return appPropertiesDetails;
  }

  @Override
  public void deactivateUserAccount(String userId) {
    Optional<UserDetailsEntity> optUserDetails = userDetailsRepository.findByUserId(userId);
    UserDetailsEntity userDetailsEntity = optUserDetails.get();
    String alteredEmail =
        userDetailsEntity.getEmail() + "_DEACTIVATED_" + Instant.now().toEpochMilli();
    if (alteredEmail.length() > CommonConstants.EMAIL_LENGTH) {
      alteredEmail = alteredEmail.substring(0, CommonConstants.EMAIL_LENGTH);
    }
    userDetailsEntity.setStatus(UserStatus.DEACTIVATED.getValue());
    userDetailsEntity.setEmail(alteredEmail);
    userDetailsRepository.saveAndFlush(userDetailsEntity);
  }
}
