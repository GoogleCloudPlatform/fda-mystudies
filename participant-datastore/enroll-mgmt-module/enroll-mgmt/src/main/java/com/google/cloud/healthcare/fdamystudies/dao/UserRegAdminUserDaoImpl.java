/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.service.StudyStateServiceImpl;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRegAdminUserDaoImpl implements UserRegAdminUserDao {

  private static final XLogger logger =
      XLoggerFactory.getXLogger(StudyStateServiceImpl.class.getName());

  @Autowired private SessionFactory sessionFactory;

  @Override
  @SuppressWarnings("unchecked")
  public UserDetailsEntity getRecord(String userId) {
    logger.entry("Begin getRecord()");
    List<UserDetailsEntity> userList = null;
    UserDetailsEntity user = null;

    Session session = this.sessionFactory.getCurrentSession();
    Query<UserDetailsEntity> query =
        session.createQuery("from UserDetailsEntity where userId = :userId");
    query.setParameter("userId", userId);
    userList = query.getResultList();

    if (userList != null && !userList.isEmpty()) {
      user = userList.get(0);
    }
    logger.exit("getRecord() Ended ");
    return user;
  }

  @Override
  public void updateUserDetails(UserDetailsEntity userDetailsEntity) {
    try {
      Session session = this.sessionFactory.getCurrentSession();
      session
          .createSQLQuery(
              "UPDATE user_details set device_type=:device_type ,device_os=:device_os ,mobile_platform=:mobile_platform  WHERE id=:id")
          .setParameter(
              "device_type",
              StringUtils.isNotEmpty(userDetailsEntity.getDeviceType())
                  ? userDetailsEntity.getDeviceType()
                  : "")
          .setParameter(
              "device_os",
              StringUtils.isNotEmpty(userDetailsEntity.getDeviceOS())
                  ? userDetailsEntity.getDeviceOS()
                  : "")
          .setParameter(
              "mobile_platform",
              StringUtils.isNotEmpty(userDetailsEntity.getMobilePlatform())
                  ? userDetailsEntity.getMobilePlatform()
                  : "")
          .setParameter("id", userDetailsEntity.getId())
          .executeUpdate();
    } catch (Exception e) {
      logger.error("updateUserDetails - updateUserDetails() - Error", e);
    }
    logger.exit("updateUserDetails() - Ends");
  }
}
