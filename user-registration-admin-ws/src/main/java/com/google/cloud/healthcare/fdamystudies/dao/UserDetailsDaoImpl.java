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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.UserDetails;

@Repository
public class UserDetailsDaoImpl implements UserDetailsDao {

  private static final Logger logger = LoggerFactory.getLogger(UserDetailsDaoImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Override
  @SuppressWarnings("unchecked")
  public List<UserDetails> getUserDetais(Integer appInfoId) throws SystemException {
    logger.info("(DAO)...UserDetailsDaoImpl.getUserDetais()...Started");
    List<UserDetails> userDetailsList = null;

    if (appInfoId != null) {
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        Query<UserDetails> query =
            session.createQuery("from UserDetails where appInfo.appInfoId = :appInfoId");
        query.setParameter("appInfoId", appInfoId);
        userDetailsList = query.getResultList();
        return userDetailsList;
      } catch (Exception e) {
        logger.error("(DAO)...UserDetailsDaoImpl.getUserDetais(): (ERROR) ", e);
        throw new SystemException();
      }
    } else {
      logger.info("(DAO)...UserDetailsDaoImpl.getUserDetais()...Ended");
      return null;
    }
  }
}
