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
import com.google.cloud.healthcare.fdamystudies.model.AppPermission;

@Repository
public class AppPermissionDaoImpl implements AppPermissionDao {

  public static final Logger logger = LoggerFactory.getLogger(AppPermissionDaoImpl.class);
  @Autowired private EntityManagerFactory entityManagerFactory;

  @Override
  @SuppressWarnings("unchecked")
  public AppPermission checkPermission(Integer adminId, Integer appId) throws SystemException {
    logger.info("(DAO)...AppPermissionDaoImpl.checkPermission()...Started");
    List<AppPermission> appPermissionList = null;
    AppPermission appPermission = null;
    if (adminId != null) {
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {

        Query<AppPermission> query =
            session.createQuery(
                "from AppPermission where urAdminUser.id = :adminId and appInfo.appInfoId = :appId");
        query.setParameter("adminId", adminId);
        query.setParameter("appId", appId);
        appPermissionList = query.getResultList();

        if (appPermissionList != null && appPermissionList.size() > 0) {
          appPermission = appPermissionList.get(0);
          logger.info(
              "(DAO)...AppPermissionDaoImpl.checkPermission()...Ended with: " + appPermission);
          return appPermission;
        } else return null;
      } catch (Exception e) {
        logger.error("(DAO)...AppPermissionDaoImpl.checkPermission: (ERROR) ", e);
        throw new SystemException();
      }
    } else return appPermission;
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<AppPermission> checkPermission(Integer adminId) throws SystemException {
    logger.info("(DAO)...AppPermissionDaoImpl.checkPermission()...Started");
    List<AppPermission> appPermissionList = null;
    if (adminId != null) {
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {

        Query<AppPermission> query =
            session.createQuery("from AppPermission where urAdminUser.id = :adminId");
        query.setParameter("adminId", adminId);
        appPermissionList = query.getResultList();
        logger.info(
            "(DAO)...AppPermissionDaoImpl.checkPermission()...Ended with: " + appPermissionList);
        return appPermissionList;
      } catch (Exception e) {
        logger.error("(DAO)...AppPermissionDaoImpl.checkPermission: (ERROR) ", e);
        throw new SystemException();
      }
    } else return appPermissionList;
  }
}
