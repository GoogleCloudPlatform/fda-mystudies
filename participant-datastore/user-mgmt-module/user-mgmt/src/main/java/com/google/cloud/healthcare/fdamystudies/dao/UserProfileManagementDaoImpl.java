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
import com.google.cloud.healthcare.fdamystudies.common.EnrollmentStatus;
import com.google.cloud.healthcare.fdamystudies.common.OnboardingStatus;
import com.google.cloud.healthcare.fdamystudies.common.UserStatus;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoEntity;
import com.google.cloud.healthcare.fdamystudies.model.LoginAttemptsEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserAppDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.repository.AuthInfoRepository;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantEnrollmentHistoryRepository;
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
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserProfileManagementDaoImpl implements UserProfileManagementDao {

  private static final XLogger logger =
      XLoggerFactory.getXLogger(UserProfileManagementDaoImpl.class.getName());

  @Autowired private SessionFactory sessionFactory;

  @Autowired UserDetailsRepository userDetailsRepository;

  @Autowired private AuthInfoRepository authInfoRepository;

  @Autowired CommonDao commonDao;

  @Autowired private ParticipantEnrollmentHistoryRepository participantEnrollmentHistoryRepository;

  @Override
  public UserDetailsEntity getParticipantInfoDetails(String userId) {

    logger.entry("Begin getParticipantInfoDetails()");
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
    logger.exit("getParticipantInfoDetails() - Ends ");
    return userDetails;
  }

  @Override
  public AuthInfoEntity getAuthInfo(UserDetailsEntity userDetailsId) {
    logger.entry("Begin getAuthInfo()");
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
    logger.exit("getAuthInfo() - Ends ");
    return authInfo;
  }

  @Override
  public ErrorBean updateUserProfile(
      String userId, UserDetailsEntity userDetail, AuthInfoEntity authInfo) {
    logger.entry("Begin updateUserProfile()");
    ErrorBean errorBean = null;

    if (null != userDetail) {
      userDetailsRepository.save(userDetail);
      errorBean = new ErrorBean(ErrorCode.EC_200.code(), ErrorCode.EC_200.errorMessage());
      if (null != authInfo) {
        List<AuthInfoEntity> authInfoList =
            authInfoRepository.findByDeviceToken(authInfo.getDeviceToken());
        if (CollectionUtils.isNotEmpty(authInfoList)) {
          authInfoRepository.deleteByDeviceTokenAndUserId(
              authInfo.getDeviceToken(), userDetail.getId());
        }

        authInfoRepository.save(authInfo);
      }

    } else {
      errorBean = new ErrorBean(ErrorCode.EC_61.code(), ErrorCode.EC_61.errorMessage());
    }

    logger.exit("updateUserProfile() - ends ");
    return errorBean;
  }

  @Override
  public UserDetailsEntity getParticipantDetailsByEmail(String email, AppEntity app) {

    logger.entry("Begin getParticipantDetailsByEmail()");
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
    criteriaQuery
        .select(userDetailsBoRoot)
        .where(userDetailsPredicates.toArray(new Predicate[userDetailsPredicates.size()]));
    userDetailsBoList = session.createQuery(criteriaQuery).getResultList();
    if (!userDetailsBoList.isEmpty()) {
      userDetails = userDetailsBoList.get(0);
    }
    logger.exit("getParticipantDetailsByEmail() - Ends ");
    return userDetails;
  }

  @Override
  public LoginAttemptsEntity getLoginAttempts(String email) {
    logger.entry("Begin getLoginAttempts()");

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
    logger.exit("getLoginAttempts() - Ends ");
    return loginAttempt;
  }

  @Override
  public UserDetailsEntity saveParticipant(UserDetailsEntity participant) {
    logger.entry("Begin saveParticipant()");
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
    logger.exit("saveParticipant() - Ends ");
    return userDetails;
  }

  @Override
  public void resetLoginAttempts(String email) {
    CriteriaBuilder criteriaBuilder = null;
    CriteriaDelete<LoginAttemptsEntity> criteriaDelete = null;
    Root<LoginAttemptsEntity> attemptRoot = null;
    logger.entry("Begin resetLoginAttempts()");
    Session session = this.sessionFactory.getCurrentSession();
    criteriaBuilder = session.getCriteriaBuilder();
    criteriaDelete = criteriaBuilder.createCriteriaDelete(LoginAttemptsEntity.class);
    attemptRoot = criteriaDelete.from(LoginAttemptsEntity.class);
    criteriaDelete.where(criteriaBuilder.equal(attemptRoot.get("email"), email));
    session.createQuery(criteriaDelete).executeUpdate();

    logger.exit("resetLoginAttempts() - ends");
  }

  @Override
  public UserDetailsEntity getParticipantDetails(String userId) {
    logger.entry("Begin getParticipantDetails()");
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
    logger.exit("getParticipantDetails() - Ends ");
    return userDetails;
  }

  @Override
  public void deactivateAcct(String userId, List<String> deleteData, String userDetailsId) {
    logger.entry("Begin deActivateAcct()");
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
      participantEnrollmentHistoryRepository.updateWithdrawalDateAndStatusForDeactivatedUser(
          userDetailsId, EnrollmentStatus.WITHDRAWN.getStatus());

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
      criteriaParticipantStudiesUpdate.set("status", EnrollmentStatus.WITHDRAWN.getStatus());
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

    logger.exit("deActivateAcct() - Ends ");
  }

  @Override
  @Transactional(readOnly = true)
  public AppEntity getAppPropertiesDetailsByAppId(String appId) {
    logger.entry("Begin getAppPropertiesDetailsByAppId()");
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
