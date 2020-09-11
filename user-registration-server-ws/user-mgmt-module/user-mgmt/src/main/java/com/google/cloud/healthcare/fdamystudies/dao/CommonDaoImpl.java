/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.beans.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.AppInfoDetailsBO;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.StudyInfoBO;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.UserAppDetailsBO;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.UserDetailsBO;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class CommonDaoImpl implements CommonDao {

  private static Logger logger = LoggerFactory.getLogger(CommonDaoImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Override
  public String validatedUserAppDetailsByAllApi(String userId, String email, int appId) {
    logger.info("CommonDaoImpl validatedUserAppDetailsByAllApi() - Starts ");
    Transaction transaction = null;
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<UserDetailsBO> userDetailsBoCriteria = null;
    CriteriaQuery<UserAppDetailsBO> userAppDetailsBoCriteria = null;
    Root<UserDetailsBO> userDetailsBoRoot = null;
    Root<UserAppDetailsBO> userAppDetailsBoRoot = null;
    Predicate[] predicates = new Predicate[2];
    Predicate[] userAppDetailsPredicates = new Predicate[2];
    List<UserDetailsBO> userDetailsBoList = null;
    String message = "";
    List<UserAppDetailsBO> userAppDetailsList = null;
    UserDetailsBO userDetailsBO = null;
    Integer userDetailsId = 0;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      transaction = session.beginTransaction();
      criteriaBuilder = session.getCriteriaBuilder();

      if (!StringUtils.isEmpty(email)) {
        userDetailsBoCriteria = criteriaBuilder.createQuery(UserDetailsBO.class);
        userDetailsBoRoot = userDetailsBoCriteria.from(UserDetailsBO.class);
        predicates[0] = criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.EMAIL), email);
        predicates[1] =
            criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.APPLICATION_ID), appId);
        userDetailsBoCriteria.select(userDetailsBoRoot).where(predicates);
        userDetailsBoList = session.createQuery(userDetailsBoCriteria).getResultList();
        if (!userDetailsBoList.isEmpty()) {
          userDetailsBO = userDetailsBoList.get(0);
          userDetailsId = userDetailsBO.getUserDetailsId();
        }
      }

      if (!StringUtils.isEmpty(userId)) {
        userDetailsBoCriteria = criteriaBuilder.createQuery(UserDetailsBO.class);
        userDetailsBoRoot = userDetailsBoCriteria.from(UserDetailsBO.class);
        predicates[0] =
            criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.KEY_USERID), userId);
        predicates[1] =
            criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.APPLICATION_ID), appId);
        userDetailsBoCriteria.select(userDetailsBoRoot).where(predicates);
        userDetailsBoList = session.createQuery(userDetailsBoCriteria).getResultList();
        if (!userDetailsBoList.isEmpty()) {
          userDetailsBO = userDetailsBoList.get(0);
          userDetailsId = userDetailsBO.getUserDetailsId();
          userAppDetailsBoCriteria = criteriaBuilder.createQuery(UserAppDetailsBO.class);
          userAppDetailsBoRoot = userAppDetailsBoCriteria.from(UserAppDetailsBO.class);
          userAppDetailsPredicates[0] =
              criteriaBuilder.equal(userAppDetailsBoRoot.get("userDetailsId"), userDetailsId);
          userAppDetailsPredicates[1] =
              criteriaBuilder.equal(userAppDetailsBoRoot.get(AppConstants.APPLICATION_ID), appId);
          userAppDetailsBoCriteria
              .select(userAppDetailsBoRoot)
              .where(userAppDetailsPredicates)
              .orderBy(criteriaBuilder.asc(userAppDetailsBoRoot.get("createdOn")));
          userAppDetailsList = session.createQuery(userAppDetailsBoCriteria).getResultList();
          if ((userAppDetailsList != null) && (!userAppDetailsList.isEmpty())) {
            message = MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue();
          } else {
            message = MyStudiesUserRegUtil.ErrorCodes.ORG_NOTEXIST.getValue();
          }
        } else {
          message = MyStudiesUserRegUtil.ErrorCodes.ACCOUNT_DEACTIVATE_ERROR_MSG.getValue();
        }
      } else {
        message = MyStudiesUserRegUtil.ErrorCodes.ACCOUNT_DEACTIVATE_ERROR_MSG.getValue();
      }
      transaction.commit();
    } catch (Exception e) {
      logger.error("CommonDaoImpl validatedUserAppDetailsByAllApi() - error ", e);
    }
    logger.info("CommonDaoImpl validatedUserAppDetailsByAllApi() - Ends ");
    return message;
  }

  @Override
  public AppOrgInfoBean getUserAppDetailsByAllApi(String userId, String appId) {
    logger.info("CommonDaoImpl validatedUserAppDetailsByAllApi() - Starts ");
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<AppInfoDetailsBO> appDetailsBoCriteria = null;
    Root<AppInfoDetailsBO> appDetailsBoRoot = null;
    Predicate[] appDetailsPredicates = new Predicate[1];
    List<AppInfoDetailsBO> appDetailsList = null;
    AppInfoDetailsBO appDetailsBO = null;

    AppOrgInfoBean appOrgInfoBean = new AppOrgInfoBean();
    int appInfoId = 0;

    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();

      if (!StringUtils.isEmpty(appId)) {
        appDetailsBoCriteria = criteriaBuilder.createQuery(AppInfoDetailsBO.class);
        appDetailsBoRoot = appDetailsBoCriteria.from(AppInfoDetailsBO.class);
        appDetailsPredicates[0] = criteriaBuilder.equal(appDetailsBoRoot.get("appId"), appId);
        appDetailsBoCriteria.select(appDetailsBoRoot).where(appDetailsPredicates);
        appDetailsList = session.createQuery(appDetailsBoCriteria).getResultList();
        if (!appDetailsList.isEmpty()) {
          appDetailsBO = appDetailsList.get(0);
          appInfoId = appDetailsBO.getAppInfoId();
        }
      }

      appOrgInfoBean.setAppInfoId(appInfoId);

    } catch (Exception e) {
      appOrgInfoBean.setAppInfoId(appInfoId);
      logger.error("CommonDaoImpl getUserAppDetailsByAllApi() - error ", e);
    }
    logger.info("CommonDaoImpl getUserAppDetailsByAllApi() - Ends ");
    return appOrgInfoBean;
  }

  @Override
  public Integer getUserInfoDetails(String userId) {
    logger.info("CommonDaoImpl getUserInfoDetails() - Starts ");
    CriteriaBuilder criteriaBuilder = null;
    Integer userDetailsId = null;
    CriteriaQuery<UserDetailsBO> userDetailsCriteriaQuery = null;
    Root<UserDetailsBO> userDetailsBoRoot = null;
    Predicate[] userDetailspredicates = new Predicate[1];
    List<UserDetailsBO> userDetailsBoList = null;
    UserDetailsBO userDetailsBO = null;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      userDetailsCriteriaQuery = criteriaBuilder.createQuery(UserDetailsBO.class);
      userDetailsBoRoot = userDetailsCriteriaQuery.from(UserDetailsBO.class);
      userDetailspredicates[0] =
          criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.KEY_USERID), userId);
      userDetailsCriteriaQuery.select(userDetailsBoRoot).where(userDetailspredicates);
      userDetailsBoList = session.createQuery(userDetailsCriteriaQuery).getResultList();
      if (!userDetailsBoList.isEmpty()) {
        userDetailsBO = userDetailsBoList.get(0);
        userDetailsId = userDetailsBO.getUserDetailsId();
      }
    } catch (Exception e) {
      logger.error("CommonDaoImpl getUserInfoDetails() - error ", e);
    }
    logger.info("CommonDaoImpl getUserInfoDetails() - Ends ");
    return userDetailsId;
  }

  @Override
  public List<AppInfoDetailsBO> getAppInfoSet(HashSet<String> appIds) {
    logger.info("CommonDaoImpl getAppInfoIds() - start ");
    List<AppInfoDetailsBO> appInfos = new ArrayList();
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      appInfos =
          session
              .createQuery("From AppInfoDetailsBO where appId in :appIds")
              .setParameterList("appIds", appIds)
              .getResultList();

    } catch (Exception e) {
      logger.error("CommonDaoImpl getAppInfoIds() - error ", e);
    }
    logger.info("CommonDaoImpl getAppInfoIds() - ends ");

    return appInfos;
  }

  @Override
  public List<StudyInfoBO> getStudyInfoSet(HashSet<String> studyIdSet) {
    logger.info("CommonDaoImpl getStudyInfoIds() - starts ");
    List<StudyInfoBO> studyInfos = new ArrayList();
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      studyInfos =
          session
              .createQuery("From StudyInfoBO where customId in :studyIdSet")
              .setParameterList("studyIdSet", studyIdSet)
              .getResultList();

    } catch (Exception e) {
      logger.error("CommonDaoImpl getStudyInfoIds() - error ", e);
    }
    logger.info("CommonDaoImpl getStudyInfoIds() - ends ");

    return studyInfos;
  }

  @Override
  public Map<Integer, Map<String, JSONArray>> getStudyLevelDeviceToken(
      List<StudyInfoBO> studyInfos) {
    logger.info("CommonDaoImpl.getStudyLevelDeviceToken() - starts");

    Map<Integer, Map<String, JSONArray>> studyDeviceTokenMap = new HashMap<>();
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {

      if (studyInfos != null && !studyInfos.isEmpty()) {

        List<Integer> studyInfoIds =
            studyInfos.stream().map(a -> a.getId()).distinct().collect(Collectors.toList());
        List<Object[]> rs =
            session
                .createSQLQuery(
                    "SELECT sp.study_info_id, GROUP_CONCAT(a.device_token) as device_token,GROUP_CONCAT(a.device_type) as device_type FROM participant_study_info sp, auth_info a"
                        + " where sp.user_details_id = a.user_details_id and sp.status not in('yetToJoin','withdrawn','notEligible') and a.remote_notification_flag=1"
                        + " and sp.study_info_id in (:studyIds)  and (a.device_token is not NULL and a.device_token != '' and a.device_type is not"
                        + " NULL and a.device_type != '') GROUP BY sp.study_info_id")
                .setParameterList("studyIds", studyInfoIds)
                .getResultList();
        logger.info("CommonDaoImpl.getStudyLevelDeviceToken() ResultSet size::" + rs.size());
        if (rs != null) {
          for (Object[] objects : rs) {

            Integer studyId = (Integer) objects[0];
            String deviceToken = (String) objects[1];
            String deviceType = (String) objects[2];
            if (deviceToken != null) {
              String[] deviceTokens = deviceToken.split(",");
              String[] deviceTypes = deviceType.split(",");

              if (((deviceTokens != null && deviceTokens.length > 0)
                      && (deviceType != null && deviceTypes.length > 0))
                  && (deviceTokens.length == deviceTypes.length)) {

                JSONArray jsonArray = new JSONArray();
                JSONArray iosJsonArray = new JSONArray();
                Map<String, JSONArray> deviceMap = new HashMap<>();
                for (int i = 0; i < deviceTokens.length; i++) {
                  if (deviceTypes[i] != null
                      && deviceTypes[i].equalsIgnoreCase(AppConstants.DEVICE_ANDROID)) {
                    jsonArray.put(deviceTokens[i].trim());
                  } else if (deviceTypes[i] != null
                      && deviceTypes[i].equalsIgnoreCase(AppConstants.DEVICE_IOS)) {
                    iosJsonArray.put(deviceTokens[i].trim());
                  } else {
                    logger.error("Invalid Device Type");
                  }
                }
                deviceMap.put(AppConstants.DEVICE_ANDROID, jsonArray);
                deviceMap.put(AppConstants.DEVICE_IOS, iosJsonArray);

                studyDeviceTokenMap.put(studyId, deviceMap);
              }
            }
          }
        }
      }

    } catch (Exception e) {
      logger.error("CommonDaoImpl.getStudyLevelDeviceToken() - error ", e);
    }
    logger.info("CommonDaoImpl.getStudyLevelDeviceToken() - ends ");
    return studyDeviceTokenMap;
  }

  public String getParticicpantId(Integer id, String customStudyId) {
    logger.info("CommonDaoImpl getParticicpantId() - Starts ");
    CriteriaBuilder criteriaBuilder = null;
    String participantId = null;
    CriteriaQuery<StudyInfoBO> criteriaQuery = null;
    Root<StudyInfoBO> root = null;
    Predicate[] predicates = new Predicate[1];
    List<StudyInfoBO> list = null;
    StudyInfoBO studyInfo = null;

    CriteriaQuery<ParticipantStudiesBO> criteriaQuery1 = null;
    Root<ParticipantStudiesBO> root1 = null;
    Predicate[] predicates1 = new Predicate[2];
    List<ParticipantStudiesBO> list1 = null;
    ParticipantStudiesBO participantStudyBo = null;

    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      criteriaQuery = criteriaBuilder.createQuery(StudyInfoBO.class);
      root = criteriaQuery.from(StudyInfoBO.class);
      predicates[0] = criteriaBuilder.equal(root.get("customId"), customStudyId);
      criteriaQuery.select(root).where(predicates);
      list = session.createQuery(criteriaQuery).getResultList();
      if (!list.isEmpty()) {
        studyInfo = list.get(0);
        UserDetailsBO userDetails = session.get(UserDetailsBO.class, id);
        criteriaQuery1 = criteriaBuilder.createQuery(ParticipantStudiesBO.class);
        root1 = criteriaQuery1.from(ParticipantStudiesBO.class);
        predicates1[0] = criteriaBuilder.equal(root1.get("studyInfo"), studyInfo);
        predicates1[1] = criteriaBuilder.equal(root1.get("userDetails"), userDetails);
        criteriaQuery1.select(root1).where(predicates1);
        list1 = session.createQuery(criteriaQuery1).getResultList();
        if (!list.isEmpty()) {
          participantStudyBo = list1.get(0);
          participantId = participantStudyBo.getParticipantId();
        }
      }
    } catch (Exception e) {
      logger.error("CommonDaoImpl getParticicpantId() - error ", e);
    }
    logger.info("CommonDaoImpl getParticicpantId() - Ends ");
    return participantId;
  }
}
