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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;
import com.google.cloud.healthcare.fdamystudies.service.StudyStateServiceImpl;

@Repository
public class UserRegAdminUserDaoImpl implements UserRegAdminUserDao {

  private static final Logger logger = LoggerFactory.getLogger(StudyStateServiceImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Override
  @SuppressWarnings("unchecked")
  public UserDetailsBO getRecord(String userId) throws SystemException {
    logger.info("(Service)...UserRegAdminUserDaoImpl.getRecord()...Started");
    List<UserDetailsBO> userList = null;
    UserDetailsBO user = null;

    if (userId != null) {
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        Query<UserDetailsBO> query =
            session.createQuery("from UserDetailsBO where userId = :userId");
        query.setParameter("userId", userId);
        userList = query.getResultList();

        if (userList != null && !userList.isEmpty()) {
          user = userList.get(0);
        }
        logger.info("(DAO)...UserRegAdminUserDaoImpl.getRecord()...Ended ");
        return user;
      } catch (Exception e) {
        logger.error("(DAO)...UserRegAdminUserDaoImpl - getRecord: (ERROR) ", e);
        throw new SystemException();
      }
    } else {
      logger.error("(DAO)...UserRegAdminUserDaoImpl - getRecord Ended");
      return null;
    }
  }
}
