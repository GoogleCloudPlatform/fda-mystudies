/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguratation;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminUser;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;

@Service
public class UserProfileServiceDaoImpl implements UserProfileServiceDao {

  private static final String ERROR = "error";

  private static final Logger logger = LoggerFactory.getLogger(UserProfileServiceDaoImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Autowired private ApplicationConfiguratation appConfig;

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
          userProfileDetails.setFirstName(userDetails.getFirstName());
          userProfileDetails.setLastName(userDetails.getLastName());
          userProfileDetails.setEmail(userDetails.getEmail());
          userProfileDetails.setUserId(userDetails.getId());
          userProfileDetails.setManageLocations(userDetails.getManageLocations());
          userResponseBean.setProfileRespBean(userProfileDetails);
          userResponseBean.setError(
              new ErrorBean(ErrorCode.EC_200.code(), ErrorCode.EC_200.errorMessage()));
        } else {
          userResponseBean.setError(
              new ErrorBean(
                  ErrorCode.EC_93.code(),
                  ErrorCode.EC_93.errorMessage(),
                  ERROR,
                  ErrorCode.EC_93.errorMessage()));
        }
      } else {
        userResponseBean.setError(
            new ErrorBean(
                ErrorCode.EC_61.code(),
                ErrorCode.EC_61.errorMessage(),
                ERROR,
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
              ERROR,
              ErrorCode.EC_500.errorMessage()));
    }
    logger.info("UserProfileServiceDaoImpl - getUserProfile() - ends");
    return userResponseBean;
  }
}
