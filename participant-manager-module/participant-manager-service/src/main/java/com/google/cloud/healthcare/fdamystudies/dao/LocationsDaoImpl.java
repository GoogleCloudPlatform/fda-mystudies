/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import javax.persistence.EntityManagerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.cloud.healthcare.fdamystudies.model.LocationBo;

@Repository
public class LocationsDaoImpl implements LocationsDao {

  private static final Logger logger = LoggerFactory.getLogger(LocationsDaoImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Override
  @Transactional
  public void addNewLocation(LocationBo locationBo) {
    logger.info("LocationsDAOImpl - addNewLocation - starts");
    Transaction transaction = null;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      transaction = session.beginTransaction();
      session.save(locationBo);
      transaction.commit();
    } catch (ConstraintViolationException e) {
      logger.error("LocationsDAOImpl - addNewLocation - error");
      throw e;
    } catch (Exception e) {
      logger.error("LocationsDAOImpl - addNewLocation - error", e);
    }
    logger.info("LocationsDAOImpl - addNewLocation - ends");
  }
}
