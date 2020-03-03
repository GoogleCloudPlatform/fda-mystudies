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
import javax.persistence.EntityManagerFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.SitePermission;

@Repository
public class SitePermissionDAOImpl implements SitePermissionDAO {

  private static final Logger logger = LoggerFactory.getLogger(SitePermissionDAO.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  @SuppressWarnings("unchecked")
  @Override
  public SitePermission getSitePermissionForUser(Integer siteId, Integer userId) {
    logger.info("SitePermissionDAOImpl - getSitePermissionForUser - starts");
    SitePermission sitePermission = null;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      Query<SitePermission> query =
          session.createQuery(
              "from SitePermission where urAdminUser.id = :userId and siteBo.id = :siteId");
      query.setParameter("userId", userId);
      query.setParameter("siteId", siteId);
      List<SitePermission> sitePermissions = query.getResultList();
      sitePermission = sitePermissions.get(0);

    } catch (Exception e) {
      logger.error("SitePermissionDAOImpl - getSitePermissionForUser - error", e);
      throw e;
    }
    logger.info("SitePermissionDAOImpl - getSitePermissionForUser - ends");
    return sitePermission;
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Integer> getSiteIdList(Integer adminUserId, Integer appInfoId, Integer studyId)
      throws SystemException {
    logger.info("SitePermissionDAOImpl.getSiteIdList()...Started");

    List<SitePermission> sitePermissionList = null;
    List<Integer> siteIdList = null;

    if (adminUserId != null && appInfoId != null && studyId != null) {
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        Query<SitePermission> query =
            session.createQuery(
                "from SitePermission where urAdminUser.id = :userId and appInfo.appInfoId = :appInfoId and studyInfo.id = :studyId");
        query.setParameter("userId", adminUserId);
        query.setParameter("appInfoId", appInfoId);
        query.setParameter("studyId", studyId);
        sitePermissionList = query.getResultList();
        if (sitePermissionList != null && sitePermissionList.size() > 0) {

          siteIdList = new ArrayList<>();
          for (SitePermission sitePermission : sitePermissionList) {
            logger.info("sitePermission: " + sitePermission);
            logger.info("siteId::::::>>> " + sitePermission.getSiteBo().getId());
            siteIdList.add(sitePermission.getSiteBo().getId());
          }
        }
        logger.info("SitePermissionDAOImpl.getSiteIdList()...Ended " + siteIdList);
        return siteIdList;
      } catch (Exception e) {
        logger.error("SitePermissionDAOImpl - getSiteIdList - (ERROR) ", e);
        throw new SystemException();
      }
    } else {
      logger.info("SitePermissionDAOImpl.getSiteIdList()...Ended " + siteIdList);
      return null;
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<SitePermission> getSiteIdListDetails(
      Integer adminUserId, Integer appInfoId, Integer studyId) throws SystemException {
    logger.info("SitePermissionDAOImpl.getSiteIdListDetails()...Started");

    List<SitePermission> sitePermissionList = null;

    if (adminUserId != null && appInfoId != null && studyId != null) {
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        Query<SitePermission> query =
            session.createQuery(
                "from SitePermission where urAdminUser.id = :userId and appInfo.appInfoId = :appInfoId and studyInfo.id = :studyId");
        query.setParameter("userId", adminUserId);
        query.setParameter("appInfoId", appInfoId);
        query.setParameter("studyId", studyId);
        sitePermissionList = query.getResultList();

        logger.info("SitePermissionDAOImpl.getSiteIdListDetails()...Ended " + sitePermissionList);
        return sitePermissionList;
      } catch (Exception e) {
        logger.error("SitePermissionDAOImpl - getSiteIdListDetails - (ERROR) ", e);
        throw new SystemException();
      }
    } else {
      logger.info("SitePermissionDAOImpl.getSiteIdListDetails()...Ended " + sitePermissionList);
      return null;
    }
  }
}
