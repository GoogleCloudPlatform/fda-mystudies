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

import org.springframework.stereotype.Repository;

@Repository
public class AppDetailsDaoImpl implements AppDetailsDao {

  // private static final Logger logger = LoggerFactory.getLogger(AppDetailsDaoImpl.class);

  // @Autowired private EntityManagerFactory entityManagerFactory;

  /* @Override
  @SuppressWarnings("unchecked")
  public Integer getUserAdminId(String userId) throws SystemException {
    logger.info("(DAO)...AppDetailsDaoImpl.getUserAdminId()...Started");

    List<UserRegAdminUser> userRegAdminUserList = null;
    Integer adminId = null;
    if (userId != null) {
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        Query<UserRegAdminUser> query =
            session.createQuery("from UserRegAdminUser where urAdminAuthId = :authId");
        query.setParameter("authId", userId);
        userRegAdminUserList = query.getResultList();
        if (userRegAdminUserList != null && userRegAdminUserList.size() > 0) {
          for (UserRegAdminUser userRegAdminUser : userRegAdminUserList) {
            adminId = userRegAdminUser.getId();
          }
        }
        return adminId;
      } catch (Exception e) {
        logger.error("(DAO)...AppDetailsDaoImpl.getUserAdminId()...Ended: (ERROR) ", e);
        throw new SystemException();
      }
    } else {
      logger.info("(DAO)...AppDetailsDaoImpl.getUserAdminId()...Ended: null");
      return adminId;
    }
  }*/

  /*@Override
  @SuppressWarnings("unchecked")
  public AppInfoDetailsBO getAppInfoDetails(Integer appId) throws SystemException {

    logger.info("AppDetailsDaoImpl - getAppInfoDetails() : starts");
    List<AppInfoDetailsBO> appInfoDetailsList = null;
    AppInfoDetailsBO appInfoDetails = null;

    if (appId != null) {
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        Query<AppInfoDetailsBO> query =
            session.createQuery("from AppInfoDetailsBO where appInfoId = :appId");
        query.setParameter("appId", appId);
        appInfoDetailsList = query.getResultList();

        if (appInfoDetailsList != null && !appInfoDetailsList.isEmpty()) {
          appInfoDetails = appInfoDetailsList.get(0);
          logger.info("AppDetailsDaoImpl - getAppInfoDetails() : ends");
          return appInfoDetails;
        } else return null;
      } catch (Exception e) {
        logger.error("AppDetailsDaoImpl - getAppInfoDetails() : error ", e);
        throw new SystemException();
      }
    } else {
      logger.info("AppDetailsDaoImpl - getAppInfoDetails() : ends");
      return null;
    }
  }*/

  /*@Override
  @SuppressWarnings("unchecked")
  public List<StudyInfoBO> getAppsStudies(Integer appInfoId) throws SystemException {
    logger.info("(DAO)...AppDetailsDaoImpl.getAppsStudies()...Started");
    List<StudyInfoBO> appsStudies = null;
    // AppInfoDetailsBO appInfoDetails = null;

    if (appInfoId != null) {
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        Query<StudyInfoBO> query =
            session.createQuery("from StudyInfoBO where appInfo.id = :appInfoId");
        query.setParameter("appInfoId", appInfoId);
        appsStudies = query.getResultList();
        return appsStudies;
      } catch (Exception e) {
        logger.error("(DAO)...AppDetailsDaoImpl.getAppsStudies()...Ended: (ERROR) ", e);
        throw new SystemException();
      }
    } else {
      logger.info("(DAO)...AppDetailsDaoImpl.getAppsStudies()...Ended");
      return null;
    }
  }*/

  /*@Override
  @SuppressWarnings("unchecked")
  public List<ParticipantStudiesBO> getParticipantEnrollments(
      List<Integer> appsStudyInfoIds, List<Integer> userDetailsIds) throws SystemException {
    logger.info("(DAO)...AppDetailsDaoImpl.getParticipantEnrollments()...Started");
    List<ParticipantStudiesBO> participantEnrollments = null;
    // AppInfoDetailsBO appInfoDetails = null;

    if ((userDetailsIds != null && userDetailsIds.size() > 0)
        || (appsStudyInfoIds != null && appsStudyInfoIds.size() > 0)) {
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        Query<ParticipantStudiesBO> query =
            session.createQuery(
                "from ParticipantStudiesBO where studyInfo.id in (:appsStudyInfoIds) and userDetails.userDetailsId in (:userDetailsIds)");
        query
            .setParameter("appsStudyInfoIds", appsStudyInfoIds)
            .setParameter("userDetailsIds", userDetailsIds);
        participantEnrollments = query.getResultList();
        return participantEnrollments;
      } catch (Exception e) {
        logger.error("(DAO)...AppDetailsDaoImpl.getParticipantEnrollments()...Ended: (ERROR) ", e);
        throw new SystemException();
      }
    } else {
      logger.info("(DAO)...AppDetailsDaoImpl.getParticipantEnrollments()...Ended");
      return null;
    }
  }*/
}
