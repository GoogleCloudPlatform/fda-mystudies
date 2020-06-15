/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.LocationBo;

@Repository
public class LocationsDaoImpl implements LocationsDao {

  private static final Logger logger = LoggerFactory.getLogger(LocationsDaoImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Override
  public List<LocationBo> getLocations(Integer locationId) {
    logger.info("LocationsDAOImpl - getLocations - starts");
    List<LocationBo> locations = Collections.emptyList();
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      CriteriaBuilder builder = session.getCriteriaBuilder();
      CriteriaQuery<LocationBo> query = builder.createQuery(LocationBo.class);
      Root<LocationBo> locationRoot = query.from(LocationBo.class);

      CriteriaQuery<LocationBo> select = query.select(locationRoot);

      if (locationId != null) {
        Predicate[] predicates = new Predicate[1];
        predicates[0] = builder.equal(locationRoot.get("id"), locationId);
        select = select.where(predicates);
      }

      locations = session.createQuery(select).getResultList();
    } catch (NoResultException e) {
      return locations;
    } catch (Exception e) {
      logger.error("LocationsDAOImpl - getLocations - error");
      throw e;
    }
    logger.info("LocationsDAOImpl - getLocations - ends");
    return locations;
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public Map<Integer, List<String>> getStudiesForLocations(List<Integer> locationIds) {
    logger.info("LocationsDAOImpl - getStudiesForLocations - starts");
    Map<Integer, List<String>> locationStudies = null;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      String queryString =
          "SELECT s.location_id, GROUP_CONCAT(DISTINCT si.name SEPARATOR ',') "
              + "from sites s, study_info si"
              + " where s.study_id=si.id AND "
              + "s.location_id in (:locationIds) GROUP BY s.location_id";
      Query query = session.createSQLQuery(queryString);
      query.setParameter("locationIds", locationIds);

      List<Object[]> rows = query.list();

      if (!CollectionUtils.isEmpty(rows)) {

        locationStudies = new HashMap<>();
        for (Object[] row : rows) {
          Integer locationId = (Integer) row[0];
          String studiesString = (String) row[1];

          if (!StringUtils.isBlank(studiesString)) {
            List<String> studies = Arrays.asList(studiesString.split(","));
            locationStudies.put(locationId, studies);
          }
        }
      }
    } catch (NoResultException e) {
      return locationStudies;
    } catch (Exception e) {
      logger.error("LocationsDAOImpl - getStudiesForLocations - error");
      throw e;
    }
    logger.info("LocationsDAOImpl - getStudiesForLocations - ends");
    return locationStudies;
  }

  @Override
  public Map<Integer, Integer> getStudiesCountForLocations(List<Integer> locationIds) {
    Map<Integer, Integer> studiesCount = new HashMap<>();
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      Query query1 =
          session.createSQLQuery(
              "SELECT location_id, COUNT(distinct(study_id)) FROM sites "
                  + "WHERE location_id IN (:locationIds) GROUP BY location_id");
      query1.setParameter("locationIds", locationIds);
      List<Object[]> rows = query1.list();

      if (!CollectionUtils.isEmpty(rows)) {
        for (Object[] row : rows) {
          studiesCount.put((Integer) row[0], ((BigInteger) row[1]).intValue());
        }
      }
    }
    return studiesCount;
  }

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

  @Override
  @Transactional
  public void updateLocation(LocationBo locationBo) {
    logger.info("LocationsDAOImpl - updateLocation - starts");
    Transaction transaction = null;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      transaction = session.beginTransaction();
      CriteriaBuilder builder = session.getCriteriaBuilder();
      CriteriaUpdate<LocationBo> updateLocation = builder.createCriteriaUpdate(LocationBo.class);
      Root<LocationBo> root = updateLocation.from(LocationBo.class);

      updateLocation.set("name", locationBo.getName());
      updateLocation.set("description", locationBo.getDescription());
      updateLocation.set("status", locationBo.getStatus());

      updateLocation.where(builder.equal(root.get("id"), locationBo.getId()));
      int rowsUpdated = session.createQuery(updateLocation).executeUpdate();
      if (rowsUpdated == 1) {
        transaction.commit();
      } else {
        transaction.rollback();
      }
    } catch (Exception e) {
      logger.error("LocationsDAOImpl - updateLocation - error", e);
    }
    logger.info("LocationsDAOImpl - updateLocation - ends");
  }

  @Override
  @SuppressWarnings("unchecked")
  public LocationBo getLocationInfo(Integer locationId) throws SystemException {

    logger.info("LocationsDAOImpl.getLocationInfo()...Started");
    LocationBo location = null;
    List<LocationBo> locationBoList = null;

    if (locationId != null) {
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        Query<LocationBo> query = session.createQuery("from LocationBo where id = :locationId");
        query.setParameter("locationId", locationId);
        locationBoList = query.getResultList();
        if (locationBoList != null && !locationBoList.isEmpty()) {
          location = locationBoList.get(0);
        }
        logger.info("LocationsDAOImpl.getLocationInfo()...Ended");
        return location;
      } catch (Exception e) {
        logger.error("LocationsDAOImpl - getSitePermissionForUser - (error) ", e);
        throw new SystemException();
      }
    } else {
      logger.info("LocationsDAOImpl.getSiteIdList()...Ended with null");
      return null;
    }
  }
}
