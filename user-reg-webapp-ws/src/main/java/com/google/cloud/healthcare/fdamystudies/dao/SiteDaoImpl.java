/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.google.cloud.healthcare.fdamystudies.model.SiteBo;

@Repository
public class SiteDaoImpl implements SiteDao {

  private static final String STATUS = "status";

  private static Logger logger = LoggerFactory.getLogger(SiteDaoImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  Transaction transaction = null;

  @SuppressWarnings("unchecked")
  public List<SiteBo> getSitesForLocation(Integer locationId, Integer status) {
    logger.info("SiteDAOImpl - getSitesForLocation() - starts");
    List<SiteBo> sites = new ArrayList<>();
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      Query<SiteBo> query =
          session.createQuery("from SiteBo where locations.id = :locationId and status = :status");
      query.setParameter("locationId", locationId);
      query.setParameter(STATUS, status);
      sites = query.getResultList();
    } catch (Exception e) {
      logger.error("SiteDAOImpl - getSitesForLocation() - errors");
    }
    logger.info("SiteDAOImpl - getSitesForLocation() - ends");
    return sites;
  }
}
