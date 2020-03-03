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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.bean.ProfileRespBean;
import com.google.cloud.healthcare.fdamystudies.bean.UserDetailsResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.UserProfileRespBean;
import com.google.cloud.healthcare.fdamystudies.bean.UserProfileUpdateBean;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguratation;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminUser;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.utils.URWebAppWSUtil;

@Service
public class UserProfileServiceDaoImpl implements UserProfileServiceDao {

  private static final Logger logger = LoggerFactory.getLogger(UserProfileServiceDaoImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Autowired private ApplicationConfiguratation appConfig;

  @Autowired private URWebAppWSUtil uRWebAppWSUtil;

  @Override
  public UserDetailsResponseBean getUserProfile(String authUserId) {
    Transaction transaction = null;
    logger.info("UserProfileServiceDaoImpl - getUserProfile() - starts");
    CriteriaBuilder builder = null;
    CriteriaQuery<UserRegAdminUser> userCriteria = null;
    Root<UserRegAdminUser> userRoot = null;
    Predicate[] predicate = new Predicate[1];
    List<UserRegAdminUser> userList = null;
    UserDetailsResponseBean userResponseBean = new UserDetailsResponseBean();
    UserProfileRespBean userProfileRespBean = new UserProfileRespBean();
    ProfileRespBean userProfileDetails = null;
    UserRegAdminUser userDetails = null;

    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      transaction = session.beginTransaction();
      builder = session.getCriteriaBuilder();
      userCriteria = builder.createQuery(UserRegAdminUser.class);
      userRoot = userCriteria.from(UserRegAdminUser.class);
      predicate[0] = builder.equal(userRoot.get(AppConstants.AUTH_USERID), authUserId);
      userCriteria.select(userRoot).where(predicate);
      userList = session.createQuery(userCriteria).getResultList();

      if (!userList.isEmpty()) {
        userDetails = userList.get(0);
        if (userDetails.getStatus() == 1) {
          userProfileDetails = new ProfileRespBean();
          System.out.println("userDetails.getFirstName()->" + userDetails.getFirstName());
          userProfileDetails.setFirstName(userDetails.getFirstName());
          userProfileDetails.setLastName(userDetails.getLastName());
          userProfileDetails.setEmail(userDetails.getEmail());
          userProfileDetails.setUserId(userDetails.getId());
          //          userProfileDetails.setManageLocations(userDetails.getManageLocations());
          //          userProfileRespBean.setProfile(userProfileDetails);
          //          userResponseBean.setUserProfileRespBean(userProfileRespBean);
          userResponseBean.setProfileRespBean(userProfileDetails);
          userResponseBean.setError(
              new ErrorBean(ErrorCode.EC_200.code(), ErrorCode.EC_200.errorMessage()));
        } else {
          userResponseBean.setError(
              new ErrorBean(
                  ErrorCode.EC_93.code(),
                  ErrorCode.EC_93.errorMessage(),
                  "error",
                  ErrorCode.EC_93.errorMessage()));
        }
      } else {
        userResponseBean.setError(
            new ErrorBean(
                ErrorCode.EC_61.code(),
                ErrorCode.EC_61.errorMessage(),
                "error",
                ErrorCode.EC_61.errorMessage()));
      }
      transaction.commit();
    } catch (Exception e) {
      logger.error("UserProfileServiceDaoImpl - getUserProfile() - error() ", e);
      if (null != transaction) {
        try {
          transaction.rollback();
        } catch (Exception e1) {
          logger.error("UserProfileServiceDaoImpl - getUserProfile() - error() rollback", e1);
        }
      }
      userResponseBean.setError(
          new ErrorBean(
              ErrorCode.EC_500.code(),
              ErrorCode.EC_500.errorMessage(),
              "error",
              ErrorCode.EC_500.errorMessage()));
    }
    logger.info("UserProfileServiceDaoImpl - getUserProfile() - ends");
    return userResponseBean;
  }

  @Override
  public UserDetailsResponseBean getUserProfileById(Integer userId) {
    Transaction transaction = null;
    logger.info("UserProfileServiceDaoImpl - getUserProfile() - starts");
    CriteriaBuilder builder = null;
    CriteriaQuery<UserRegAdminUser> userCriteria = null;
    Root<UserRegAdminUser> userRoot = null;
    Predicate[] predicate = new Predicate[1];
    List<UserRegAdminUser> userList = null;
    UserDetailsResponseBean userResponseBean = new UserDetailsResponseBean();
    UserProfileRespBean userProfileRespBean = new UserProfileRespBean();
    ProfileRespBean userProfileDetails = null;
    UserRegAdminUser userDetails = null;

    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      transaction = session.beginTransaction();
      builder = session.getCriteriaBuilder();
      userCriteria = builder.createQuery(UserRegAdminUser.class);
      userRoot = userCriteria.from(UserRegAdminUser.class);
      predicate[0] = builder.equal(userRoot.get(AppConstants.ID), userId);
      userCriteria.select(userRoot).where(predicate);
      userList = session.createQuery(userCriteria).getResultList();

      if (!userList.isEmpty()) {
        userDetails = userList.get(0);
        if (userDetails.getStatus() == 1) {
          userProfileDetails = new ProfileRespBean();
          System.out.println("userDetails.getFirstName()->" + userDetails.getFirstName());
          userProfileDetails.setFirstName(userDetails.getFirstName());
          userProfileDetails.setLastName(userDetails.getLastName());
          userProfileDetails.setEmail(userDetails.getEmail());
          userProfileDetails.setUserId(userDetails.getId());
          //          userProfileDetails.setManageLocations(userDetails.getManageLocations());
          //          userProfileRespBean.setProfile(userProfileDetails);
          //          userResponseBean.setUserProfileRespBean(userProfileRespBean);
          userResponseBean.setProfileRespBean(userProfileDetails);
          userResponseBean.setError(
              new ErrorBean(ErrorCode.EC_200.code(), ErrorCode.EC_200.errorMessage()));
        } else {
          userResponseBean.setError(
              new ErrorBean(
                  ErrorCode.EC_93.code(),
                  ErrorCode.EC_93.errorMessage(),
                  "error",
                  ErrorCode.EC_93.errorMessage()));
        }
      } else {
        userResponseBean.setError(
            new ErrorBean(
                ErrorCode.EC_61.code(),
                ErrorCode.EC_61.errorMessage(),
                "error",
                ErrorCode.EC_61.errorMessage()));
      }
      transaction.commit();
    } catch (Exception e) {
      logger.error("UserProfileServiceDaoImpl - getUserProfile() - error() ", e);
      if (null != transaction) {
        try {
          transaction.rollback();
        } catch (Exception e1) {
          logger.error("UserProfileServiceDaoImpl - getUserProfile() - error() rollback", e1);
        }
      }
      userResponseBean.setError(
          new ErrorBean(
              ErrorCode.EC_500.code(),
              ErrorCode.EC_500.errorMessage(),
              "error",
              ErrorCode.EC_500.errorMessage()));
    }
    logger.info("UserProfileServiceDaoImpl - getUserProfile() - ends");
    return userResponseBean;
  }

  @Override
  public ErrorBean updateUserProfile(String userId, UserProfileUpdateBean userReqBean) {
    Transaction transaction = null;
    logger.info("UserProfileServiceDaoImpl - updateUserProfile() - starts");
    CriteriaBuilder builder = null;
    CriteriaQuery<UserRegAdminUser> userCriteria = null;
    Root<UserRegAdminUser> userRoot = null;
    Predicate[] predicate = new Predicate[1];
    List<UserRegAdminUser> userList = null;
    UserRegAdminUser userBo = null;
    ErrorBean errorBean = null;
    CriteriaUpdate<UserRegAdminUser> userCriteriaUpdate = null;
    Root<UserRegAdminUser> userUpdateRoot = null;
    int isUpdatedUser = 0;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      transaction = session.beginTransaction();
      builder = session.getCriteriaBuilder();
      userCriteria = builder.createQuery(UserRegAdminUser.class);
      userRoot = userCriteria.from(UserRegAdminUser.class);
      predicate[0] = builder.equal(userRoot.get(AppConstants.AUTH_USERID), userId);
      userCriteria.select(userRoot).where(predicate);
      userList = session.createQuery(userCriteria).getResultList();

      if (!userList.isEmpty()) {
        userBo = userList.get(0);
        if (userBo.getStatus() == 1) {
          userCriteriaUpdate = builder.createCriteriaUpdate(UserRegAdminUser.class);
          userUpdateRoot = userCriteriaUpdate.from(UserRegAdminUser.class);
          userCriteriaUpdate.set(AppConstants.KEY_FIRSTNAME, userReqBean.getFirstName());
          userCriteriaUpdate.set(AppConstants.KEY_LASTNAME, userReqBean.getLastName());
          userCriteriaUpdate.set(AppConstants.KEY_USER_EMAILID, userReqBean.getEmail());
          userCriteriaUpdate.where(builder.equal(userUpdateRoot.get("id"), userBo.getId()));
          isUpdatedUser = session.createQuery(userCriteriaUpdate).executeUpdate();
          if (isUpdatedUser != 0) {
            errorBean = new ErrorBean(ErrorCode.EC_200.code(), ErrorCode.EC_200.errorMessage());
          } else {
            errorBean = new ErrorBean(ErrorCode.EC_34.code(), ErrorCode.EC_34.errorMessage());
          }
        } else {
          errorBean = new ErrorBean(ErrorCode.EC_93.code(), ErrorCode.EC_93.errorMessage());
        }
      } else {
        errorBean = new ErrorBean(ErrorCode.EC_61.code(), ErrorCode.EC_61.errorMessage());
      }
      transaction.commit();
    } catch (HibernateException he) {
      logger.error("UserProfileServiceDaoImpl - updateUserProfile()- error ", he);
      if (null != transaction) {
        try {
          transaction.rollback();
        } catch (Exception e2) {
          logger.error(
              "UserProfileServiceDaoImpl - updateUserProfile() rollback hiber- error ", e2);
        }
      }
      errorBean =
          new ErrorBean(
              ErrorCode.EC_500.code(),
              ErrorCode.EC_500.errorMessage(),
              "error",
              ErrorCode.EC_500.errorMessage());
    } catch (Exception e) {
      logger.error("UserProfileServiceDaoImpl - updateUserProfile()- error ", e);
      if (null != transaction) {
        try {
          transaction.rollback();
        } catch (Exception e1) {
          logger.error("UserProfileServiceDaoImpl - updateUserProfile() rollback- error ", e1);
        }
      }
      errorBean =
          new ErrorBean(
              ErrorCode.EC_500.code(),
              ErrorCode.EC_500.errorMessage(),
              "error",
              ErrorCode.EC_500.errorMessage());
    }
    logger.info("UserProfileServiceDaoImpl - updateUserProfile() - ends");
    return errorBean;
  }
}
