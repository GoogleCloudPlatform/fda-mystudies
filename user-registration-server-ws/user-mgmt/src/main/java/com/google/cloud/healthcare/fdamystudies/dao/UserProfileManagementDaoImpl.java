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
import java.util.stream.Collectors;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.google.cloud.healthcare.fdamystudies.beans.DeactivateAcctBean;
import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.model.AppInfoDetailsBO;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.LoginAttemptsBO;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantActivities;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.model.StudyInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.UserAppDetailsBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetails;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;

@Repository
public class UserProfileManagementDaoImpl implements UserProfileManagementDao {

  private static final Logger logger = LoggerFactory.getLogger(UserProfileManagementDaoImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Autowired CommonDao commonDao;

  @Override
  public UserDetails getParticipantInfoDetails(String userId) {
    logger.info("UserProfileManagementDaoImpl getParticipantInfoDetails() - Starts ");
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<UserDetails> criteriaQuery = null;
    Root<UserDetails> userDetailsBoRoot = null;
    Predicate[] predicates = new Predicate[1];
    List<UserDetails> userDetailsBoList = null;
    UserDetails userDetails = null;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      criteriaQuery = criteriaBuilder.createQuery(UserDetails.class);
      userDetailsBoRoot = criteriaQuery.from(UserDetails.class);
      predicates[0] = criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.KEY_USERID), userId);
      criteriaQuery.select(userDetailsBoRoot).where(predicates);
      userDetailsBoList = session.createQuery(criteriaQuery).getResultList();
      if (!userDetailsBoList.isEmpty()) {
        userDetails = userDetailsBoList.get(0);
      }
    } catch (Exception e) {
      logger.error("UserProfileManagementDaoImpl getParticipantInfoDetails() - error ", e);
    }
    logger.info("UserProfileManagementDaoImpl getParticipantInfoDetails() - Ends ");
    return userDetails;
  }

  @Override
  public AuthInfoBO getAuthInfo(Integer userDetailsId) {
    logger.info("UserProfileManagementDaoImpl getAuthInfo() - Starts ");
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<AuthInfoBO> criteriaQuery = null;
    Root<AuthInfoBO> authInfoBoRoot = null;
    Predicate[] predicates = new Predicate[1];
    List<AuthInfoBO> authInfoBoList = null;
    AuthInfoBO authInfo = null;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      criteriaQuery = criteriaBuilder.createQuery(AuthInfoBO.class);
      authInfoBoRoot = criteriaQuery.from(AuthInfoBO.class);
      predicates[0] =
          criteriaBuilder.equal(authInfoBoRoot.get(AppConstants.KEY_USERID), userDetailsId);
      criteriaQuery.select(authInfoBoRoot).where(predicates);
      authInfoBoList = session.createQuery(criteriaQuery).getResultList();
      if (!authInfoBoList.isEmpty()) {
        authInfo = authInfoBoList.get(0);
      }
    } catch (Exception e) {
      logger.error("UserProfileManagementDaoImpl getAuthInfo() - error ", e);
    }
    logger.info("UserProfileManagementDaoImpl getAuthInfo() - Ends ");
    return authInfo;
  }

  @Override
  public ErrorBean updateUserProfile(String userId, UserDetails userDetail, AuthInfoBO authInfo) {
    logger.info("UserProfileManagementDaoImpl updateUserProfile() - Starts ");
    Transaction transaction = null;
    ErrorBean errorBean = null;
    Boolean isUpdatedAuthInfo = false;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      transaction = session.beginTransaction();

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
      transaction.commit();
    } catch (Exception e) {
      logger.error("UserProfileManagementDaoImpl updateUserProfile() - error ", e);
      errorBean = new ErrorBean(ErrorCode.EC_34.code(), ErrorCode.EC_34.errorMessage());
      if (transaction != null) {
        try {
          transaction.rollback();
        } catch (Exception e1) {
          logger.error("UserProfileManagementDaoImpl - updateUserProfile() - error rollback", e1);
        }
      }
    }
    logger.info("UserProfileManagementDaoImpl updateUserProfile() - Starts ");
    return errorBean;
  }

  @Override
  public UserDetails getParticipantDetailsByEmail(
      String email, Integer appInfoId, Integer orgInfoId) {
    logger.info("UserProfileManagementDaoImpl getParticipantDetailsByEmail() - Starts ");
    UserDetails userDetails = null;
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<UserDetails> criteriaQuery = null;
    Root<UserDetails> userDetailsBoRoot = null;
    Predicate[] predicates = new Predicate[2];
    List<UserDetails> userDetailsBoList = null;

    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      criteriaQuery = criteriaBuilder.createQuery(UserDetails.class);
      userDetailsBoRoot = criteriaQuery.from(UserDetails.class);
      predicates[0] = criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.EMAIL), email);
      predicates[1] = criteriaBuilder.equal(userDetailsBoRoot.get("appInfoId"), appInfoId);
      criteriaQuery.select(userDetailsBoRoot).where(predicates);
      userDetailsBoList = session.createQuery(criteriaQuery).getResultList();
      if (!userDetailsBoList.isEmpty()) {
        userDetails = userDetailsBoList.get(0);
      }
    } catch (Exception e) {
      logger.error("UserProfileManagementDaoImpl getParticipantInfoDetails() - error ", e);
    }
    logger.info("UserProfileManagementDaoImpl getParticipantDetailsByEmail() - Ends ");
    return userDetails;
  }

  @Override
  public LoginAttemptsBO getLoginAttempts(String email) {
    logger.info("UserProfileManagementDaoImpl getParticipantDetailsByEmail() - Starts ");

    LoginAttemptsBO loginAttempt = null;
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<LoginAttemptsBO> criteriaQuery = null;
    Root<LoginAttemptsBO> loginAttemptRoot = null;
    Predicate[] predicates = new Predicate[1];
    List<LoginAttemptsBO> loginAttemptList = null;

    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      criteriaQuery = criteriaBuilder.createQuery(LoginAttemptsBO.class);
      loginAttemptRoot = criteriaQuery.from(LoginAttemptsBO.class);
      predicates[0] = criteriaBuilder.equal(loginAttemptRoot.get(AppConstants.EMAIL), email);
      criteriaQuery.select(loginAttemptRoot).where(predicates);
      loginAttemptList = session.createQuery(criteriaQuery).getResultList();
      if (!loginAttemptList.isEmpty()) {
        loginAttempt = loginAttemptList.get(0);
      }
    } catch (Exception e) {
      logger.error("UserProfileManagementDaoImpl getParticipantInfoDetails() - error ", e);
    }
    logger.info("UserProfileManagementDaoImpl getParticipantDetailsByEmail() - Ends ");
    return loginAttempt;
  }

  @Override
  public UserDetails saveParticipant(UserDetails participant) {
    logger.info("UserProfileManagementDaoImpl saveParticipant() - Starts ");
    Transaction transaction = null;
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<UserDetails> criteriaQuery = null;
    Predicate[] predicates = new Predicate[1];
    Root<UserDetails> userDetailsRoot = null;
    List<UserDetails> userDetailsList = null;
    UserDetails userDetails = null;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      transaction = session.beginTransaction();
      criteriaQuery = criteriaBuilder.createQuery(UserDetails.class);
      userDetailsRoot = criteriaQuery.from(UserDetails.class);
      predicates[0] =
          criteriaBuilder.equal(
              userDetailsRoot.get(AppConstants.USER_DETAILS_ID), participant.getUserDetailsId());
      criteriaQuery.select(userDetailsRoot).where(predicates);
      userDetailsList = session.createQuery(criteriaQuery).getResultList();
      if (!userDetailsList.isEmpty()) {
        userDetails = userDetailsList.get(0);
        userDetails.setEmailCode(participant.getEmailCode());
        userDetails.setCodeExpireDate(participant.getCodeExpireDate());
        session.update(userDetails);
      }
      transaction.commit();
    } catch (Exception e) {
      logger.error("UserProfileManagementDaoImpl - saveParticipant() - error ", e);
      if (transaction != null) {
        try {
          transaction.rollback();
        } catch (Exception e1) {
          logger.error(
              "UserProfileManagementDaoImpl - getUserDetailsForPasswordReset() - error rollback",
              e1);
        }
      }
    }
    logger.info("UserProfileManagementDaoImpl saveParticipant() - Ends ");
    return userDetails;
  }

  @Override
  public void resetLoginAttempts(String email) {
    CriteriaBuilder criteriaBuilder = null;
    CriteriaDelete<LoginAttemptsBO> criteriaDelete = null;
    Root<LoginAttemptsBO> attemptRoot = null;
    Transaction transaction = null;
    logger.info("UserProfileManagementDaoImpl - resetLoginAttempts() - starts");
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      transaction = session.beginTransaction();
      criteriaDelete = criteriaBuilder.createCriteriaDelete(LoginAttemptsBO.class);
      attemptRoot = criteriaDelete.from(LoginAttemptsBO.class);
      criteriaDelete.where(criteriaBuilder.equal(attemptRoot.get("userEmailId"), email));
      session.createQuery(criteriaDelete).executeUpdate();
      transaction.commit();
    } catch (Exception e) {
      logger.error("UserProfileManagementDaoImpl - resetLoginAttempts() - error ", e);
      if (transaction != null) {
        try {
          transaction.rollback();
        } catch (Exception e1) {
          logger.error("UserProfileManagementDaoImpl - resetLoginAttempts() - error rollback", e1);
        }
      }
    }

    logger.info("UserProfileManagementDaoImpl - resetLoginAttempts() - end");
  }

  @Override
  public UserDetails getParticipantDetails(String userId) {
    logger.info("UserProfileManagementDaoImpl getParticipantDetails() - Starts ");
    UserDetails userDetails = null;
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<UserDetails> criteriaQuery = null;
    Root<UserDetails> userDetailsBoRoot = null;
    Predicate[] predicates = new Predicate[1];
    List<UserDetails> userDetailsBoList = null;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      criteriaQuery = criteriaBuilder.createQuery(UserDetails.class);
      userDetailsBoRoot = criteriaQuery.from(UserDetails.class);
      predicates[0] = criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.KEY_USERID), userId);
      criteriaQuery.select(userDetailsBoRoot).where(predicates);
      userDetailsBoList = session.createQuery(criteriaQuery).getResultList();
      if (!userDetailsBoList.isEmpty()) {
        userDetails = userDetailsBoList.get(0);
      }
    } catch (Exception e) {
      logger.error("UserProfileManagementDaoImpl getParticipantDetails() - error ", e);
    }
    logger.info("UserProfileManagementDaoImpl getParticipantDetails() - Ends ");
    return userDetails;
  }

  @Override
  public boolean deActivateAcct(
      String userId, DeactivateAcctBean deactivateAcctBean, Integer userDetailsId) {
    logger.info("UserProfileManagementDaoImpl deActivateAcct() - Starts ");
    Transaction transaction = null;
    CriteriaBuilder criteriaBuilder = null;

    CriteriaDelete<UserAppDetailsBO> criteriaUserAppDetailsDelete = null;
    Root<UserAppDetailsBO> userAppDetailsRoot = null;
    CriteriaDelete<AuthInfoBO> criteriaAuthInfoDelete = null;
    Root<AuthInfoBO> authInfoRoot = null;

    CriteriaDelete<UserDetails> criteriaUserDetailsDelete = null;
    Root<UserDetails> userDetailsRoot = null;
    CriteriaUpdate<UserDetails> criteriaUserDetailsUpdate = null;
    Root<UserDetails> userDetailsRootUpdate = null;

    CriteriaDelete<ParticipantActivities> criteriaParticipantActivitiesDelete = null;
    Root<ParticipantActivities> participantActivitiesRoot = null;

    CriteriaDelete<ParticipantStudiesBO> criteriaParticipantStudiesDelete = null;
    Root<ParticipantStudiesBO> participantStudiesRootDelete = null;

    CriteriaUpdate<ParticipantStudiesBO> CriteriaParticipantStudiesUpdate = null;
    Root<ParticipantStudiesBO> participantStudiesRoot = null;
    CriteriaQuery<ParticipantStudiesBO> participantStudiesCriteriaQuery = null;
    List<Predicate> studyIdPredicates = new ArrayList<Predicate>();
    Predicate[] studyInfoIdPredicates = new Predicate[1];
    Expression<String> studyIdExpression = null;
    Predicate[] predicatesAuthInfo = new Predicate[1];
    Predicate[] predicatesUserDetails = new Predicate[1];
    Predicate[] predicatesUserAppDetails = new Predicate[1];
    Predicate[] predicatesParticiapntActivities = new Predicate[3];
    List<ParticipantStudiesBO> participantStudiesBoList = null;
    ParticipantStudiesBO participantStudiesBO = null;
    CriteriaQuery<StudyInfoBO> studyInfoQuery = null;
    Root<StudyInfoBO> root_studyBO = null;
    List<StudyInfoBO> studyInfoBoList = null;
    StudyInfoBO studyInfoBO = null;
    List<Integer> studyInfoIdList = new ArrayList<>();
    int isUpdated = 0;
    int count = 0;
    boolean returnVal = false;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      transaction = session.beginTransaction();
      criteriaBuilder = session.getCriteriaBuilder();
      if (!deactivateAcctBean.getDeleteData().isEmpty()) {
        studyInfoQuery = criteriaBuilder.createQuery(StudyInfoBO.class);
        root_studyBO = studyInfoQuery.from(StudyInfoBO.class);
        studyIdExpression = root_studyBO.get("customStudyId");
        studyInfoIdPredicates[0] = studyIdExpression.in(deactivateAcctBean.getDeleteData());
        studyInfoQuery.select(root_studyBO).where(studyInfoIdPredicates);
        studyInfoBoList = session.createQuery(studyInfoQuery).getResultList();
        studyInfoIdList =
            studyInfoBoList.stream().map(StudyInfoBO::getStudyInfoId).collect(Collectors.toList());
        CriteriaParticipantStudiesUpdate =
            criteriaBuilder.createCriteriaUpdate(ParticipantStudiesBO.class);
        participantStudiesRoot = CriteriaParticipantStudiesUpdate.from(ParticipantStudiesBO.class);
        CriteriaParticipantStudiesUpdate.set("status", "Withdrawn");
        CriteriaParticipantStudiesUpdate.set("participantId", "NULL");
        studyIdPredicates.add(
            criteriaBuilder.equal(participantStudiesRoot.get("userDetailsId"), userDetailsId));
        studyIdExpression = participantStudiesRoot.get("studyInfoId");
        studyIdPredicates.add(studyIdExpression.in(studyInfoIdList));
        CriteriaParticipantStudiesUpdate.where(
            studyIdPredicates.toArray(new Predicate[studyIdPredicates.size()]));
        isUpdated = session.createQuery(CriteriaParticipantStudiesUpdate).executeUpdate();
      }

      /*criteriaParticipantActivitiesDelete =
          criteriaBuilder.createCriteriaDelete(ParticipantActivities.class);
      participantActivitiesRoot =
          criteriaParticipantActivitiesDelete.from(ParticipantActivities.class);
      predicatesParticiapntActivities[0] =
          criteriaBuilder.equal(participantActivitiesRoot.get("userId"), userId);
      predicatesParticiapntActivities[1] =
          criteriaBuilder.equal(participantActivitiesRoot.get("applicationId"), applicationId);
      predicatesParticiapntActivities[2] =
          criteriaBuilder.equal(participantActivitiesRoot.get("orgId"), orgId);
      criteriaParticipantActivitiesDelete.where(predicatesParticiapntActivities);
      session.createQuery(criteriaParticipantActivitiesDelete).executeUpdate();*/

      criteriaAuthInfoDelete = criteriaBuilder.createCriteriaDelete(AuthInfoBO.class);
      authInfoRoot = criteriaAuthInfoDelete.from(AuthInfoBO.class);
      predicatesAuthInfo[0] = criteriaBuilder.equal(authInfoRoot.get("userId"), userDetailsId);
      criteriaAuthInfoDelete.where(predicatesAuthInfo);
      session.createQuery(criteriaAuthInfoDelete).executeUpdate();

      criteriaUserAppDetailsDelete = criteriaBuilder.createCriteriaDelete(UserAppDetailsBO.class);
      userAppDetailsRoot = criteriaUserAppDetailsDelete.from(UserAppDetailsBO.class);
      predicatesUserAppDetails[0] =
          criteriaBuilder.equal(userAppDetailsRoot.get("userDetailsId"), userDetailsId);
      criteriaUserAppDetailsDelete.where(predicatesUserAppDetails);
      session.createQuery(criteriaUserAppDetailsDelete).executeUpdate();

      /*criteriaUserDetailsDelete = criteriaBuilder.createCriteriaDelete(UserDetails.class);
      userDetailsRoot = criteriaUserDetailsDelete.from(UserDetails.class);
      predicatesUserDetails[0] = criteriaBuilder.equal(userDetailsRoot.get("userId"), userId);
      predicatesUserDetails[1] =
          criteriaBuilder.equal(
              userDetailsRoot.get(AppConstants.APPLICATION_ID), appOrgInfoBean.getAppInfoId());
      criteriaUserDetailsDelete.where(predicatesUserDetails);
      count = session.createQuery(criteriaUserDetailsDelete).executeUpdate();*/

      criteriaUserDetailsUpdate = criteriaBuilder.createCriteriaUpdate(UserDetails.class);
      userDetailsRootUpdate = criteriaUserDetailsUpdate.from(UserDetails.class);
      criteriaUserDetailsUpdate.set("status", 3);
      predicatesUserDetails[0] = criteriaBuilder.equal(userDetailsRootUpdate.get("userId"), userId);
      criteriaUserDetailsUpdate.where(predicatesUserDetails);
      count = session.createQuery(criteriaUserDetailsUpdate).executeUpdate();
      if (count > 0) {
        returnVal = true;
      }
      transaction.commit();
    } catch (Exception e) {
      logger.error("UserProfileManagementDaoImpl deActivateAcct() - error ", e);
      if (transaction != null) {
        try {
          transaction.rollback();
        } catch (Exception e1) {
          logger.error("UserProfileManagementDaoImpl - deActivateAcct() - error rollback", e1);
        }
      }
    }
    logger.info("UserProfileManagementDaoImpl deActivateAcct() - Ends ");
    return returnVal;
  }

  @Override
  public AppInfoDetailsBO getAppPropertiesDetailsByAppId(Integer appId) {
    AppInfoDetailsBO appPropertiesDetails = null;
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<AppInfoDetailsBO> criteriaQuery = null;
    Root<AppInfoDetailsBO> appDetailsRoot = null;
    List<AppInfoDetailsBO> appPropetiesDetailList = null;
    logger.info("UserProfileManagementDaoImpl - resetLoginAttempts() - starts");
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      criteriaQuery = criteriaBuilder.createQuery(AppInfoDetailsBO.class);
      appDetailsRoot = criteriaQuery.from(AppInfoDetailsBO.class);
      criteriaQuery.where(
          criteriaBuilder.equal(appDetailsRoot.get(AppConstants.APPLICATION_ID), appId));
      appPropetiesDetailList = session.createQuery(criteriaQuery).getResultList();

      if (!appPropetiesDetailList.isEmpty()) {
        appPropertiesDetails = appPropetiesDetailList.get(0);
      }
    } catch (Exception e) {
      logger.error("UserProfileManagementDaoImpl - resetLoginAttempts() - error ", e);
    }
    return appPropertiesDetails;
  }
}
