/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.beans.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserAppDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.json.JSONArray;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Repository
public class CommonDaoImpl implements CommonDao {

  private static final XLogger logger = XLoggerFactory.getXLogger(CommonDaoImpl.class.getName());

  @Autowired private SessionFactory sessionFactory;

  @Override
  public String validatedUserAppDetailsByAllApi(String userId, String email, String appId) {

    logger.entry("Begin validatedUserAppDetailsByAllApi()");
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<UserDetailsEntity> userDetailsBoCriteria = null;
    CriteriaQuery<UserAppDetailsEntity> userAppDetailsBoCriteria = null;
    Root<UserDetailsEntity> userDetailsBoRoot = null;
    Root<UserAppDetailsEntity> userAppDetailsBoRoot = null;
    Predicate[] predicates = new Predicate[2];
    Predicate[] userAppDetailsPredicates = new Predicate[2];
    List<UserDetailsEntity> userDetailsBoList = null;
    String message = "";
    List<UserAppDetailsEntity> userAppDetailsList = null;
    UserDetailsEntity userDetails = null;
    String userDetailsId = String.valueOf(0);
    Session session = this.sessionFactory.getCurrentSession();
    criteriaBuilder = session.getCriteriaBuilder();

    userDetailsBoCriteria = criteriaBuilder.createQuery(UserDetailsEntity.class);
    userDetailsBoRoot = userDetailsBoCriteria.from(UserDetailsEntity.class);
    predicates[0] = criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.EMAIL), email);
    predicates[1] =
        criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.APPLICATION_ID), appId);
    userDetailsBoCriteria.select(userDetailsBoRoot).where(predicates);
    userDetailsBoList = session.createQuery(userDetailsBoCriteria).getResultList();
    if (!userDetailsBoList.isEmpty()) {
      userDetails = userDetailsBoList.get(0);
      userDetailsId = userDetails.getId();
    }

    if (!StringUtils.isEmpty(userId)) {
      userDetailsBoCriteria = criteriaBuilder.createQuery(UserDetailsEntity.class);
      userDetailsBoRoot = userDetailsBoCriteria.from(UserDetailsEntity.class);
      predicates[0] = criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.KEY_USERID), userId);
      predicates[1] =
          criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.APPLICATION_ID), appId);
      userDetailsBoCriteria.select(userDetailsBoRoot).where(predicates);
      userDetailsBoList = session.createQuery(userDetailsBoCriteria).getResultList();
      if (!userDetailsBoList.isEmpty()) {
        userDetails = userDetailsBoList.get(0);
        userDetailsId = userDetails.getId();
        userAppDetailsBoCriteria = criteriaBuilder.createQuery(UserAppDetailsEntity.class);
        userAppDetailsBoRoot = userAppDetailsBoCriteria.from(UserAppDetailsEntity.class);
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
    logger.exit("validatedUserAppDetailsByAllApi() - Ends ");
    return message;
  }

  @Override
  public AppOrgInfoBean getUserAppDetailsByAllApi(String userId, String appId) {
    logger.entry("Begin getUserAppDetailsByAllApi()");
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<AppEntity> appDetailsBoCriteria = null;
    Root<AppEntity> appDetailsBoRoot = null;
    Predicate[] appDetailsPredicates = new Predicate[1];
    List<AppEntity> appDetailsList = null;
    AppEntity appDetails = null;

    AppOrgInfoBean appOrgInfoBean = new AppOrgInfoBean();

    Session session = this.sessionFactory.getCurrentSession();
    criteriaBuilder = session.getCriteriaBuilder();

    appDetailsBoCriteria = criteriaBuilder.createQuery(AppEntity.class);
    appDetailsBoRoot = appDetailsBoCriteria.from(AppEntity.class);
    appDetailsPredicates[0] = criteriaBuilder.equal(appDetailsBoRoot.get("appId"), appId);
    appDetailsBoCriteria.select(appDetailsBoRoot).where(appDetailsPredicates);
    appDetailsList = session.createQuery(appDetailsBoCriteria).getResultList();
    if (appDetailsList.isEmpty()) {
      throw new ErrorCodeException(ErrorCode.APP_NOT_FOUND);
    }
    appDetails = appDetailsList.get(0);
    appOrgInfoBean.setAppInfoId(appDetails.getAppId());

    logger.exit("getUserAppDetailsByAllApi() - Ends ");
    return appOrgInfoBean;
  }

  @Override
  public String getUserInfoDetails(String userId) {
    logger.entry("Begin getUserInfoDetails()");
    CriteriaBuilder criteriaBuilder = null;
    String userDetailsId = null;
    CriteriaQuery<UserDetailsEntity> userDetailsCriteriaQuery = null;
    Root<UserDetailsEntity> userDetailsBoRoot = null;
    Predicate[] userDetailspredicates = new Predicate[1];
    List<UserDetailsEntity> userDetailsBoList = null;
    UserDetailsEntity userDetails = null;
    Session session = this.sessionFactory.getCurrentSession();
    criteriaBuilder = session.getCriteriaBuilder();
    userDetailsCriteriaQuery = criteriaBuilder.createQuery(UserDetailsEntity.class);
    userDetailsBoRoot = userDetailsCriteriaQuery.from(UserDetailsEntity.class);
    userDetailspredicates[0] =
        criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.KEY_USERID), userId);
    userDetailsCriteriaQuery.select(userDetailsBoRoot).where(userDetailspredicates);
    userDetailsBoList = session.createQuery(userDetailsCriteriaQuery).getResultList();
    if (!userDetailsBoList.isEmpty()) {
      userDetails = userDetailsBoList.get(0);
      userDetailsId = userDetails.getId();
    }
    logger.exit("getUserInfoDetails() - Ends ");
    return userDetailsId;
  }

  @Override
  public List<AppEntity> getAppInfoSet(HashSet<String> appIds) {
    logger.entry("Begin getAppInfoSet()");
    List<AppEntity> appInfos = new ArrayList();
    Session session = this.sessionFactory.getCurrentSession();
    appInfos =
        session
            .createQuery("From AppEntity where appId in :appIds")
            .setParameterList("appIds", appIds)
            .getResultList();

    logger.exit("getAppInfoSet() - ends ");

    return appInfos;
  }

  @Override
  @Transactional(readOnly = true)
  public List<StudyEntity> getStudyInfoSet(HashSet<String> studyIdSet) {
    logger.entry("Begin getStudyInfoSet() - starts ");
    List<StudyEntity> studyInfos = new ArrayList();
    Session session = this.sessionFactory.getCurrentSession();
    studyInfos =
        session
            .createQuery("From StudyEntity where customId in :studyIdSet")
            .setParameterList("studyIdSet", studyIdSet)
            .getResultList();

    logger.exit("getStudyInfoSet() - ends ");

    return studyInfos;
  }

  @Override
  public Map<String, Map<String, JSONArray>> getStudyLevelDeviceToken(
      List<StudyEntity> studyInfos) {
    logger.entry("Begin getStudyLevelDeviceToken()");

    Map<String, Map<String, JSONArray>> studyDeviceTokenMap = new HashMap<>();
    Session session = this.sessionFactory.getCurrentSession();

    if (studyInfos != null && !studyInfos.isEmpty()) {

      List<String> studyInfoIds =
          studyInfos.stream().map(a -> a.getId()).distinct().collect(Collectors.toList());
      List<Object[]> rs =
          session
              .createSQLQuery(
                  "SELECT sp.study_info_id, GROUP_CONCAT(a.device_token) as device_token,GROUP_CONCAT(a.device_type) as device_type FROM participant_study_info sp, auth_info a"
                      + " where sp.user_details_id = a.user_details_id and sp.status not in('yetToEnroll','withdrawn','notEligible') and a.remote_notification_flag=1"
                      + " and sp.study_info_id in (:studyIds)  and (a.device_token is not NULL and a.device_token != '' and a.device_type is not"
                      + " NULL and a.device_type != '') GROUP BY sp.study_info_id")
              .setParameterList("studyIds", studyInfoIds)
              .getResultList();
      logger.info("CommonDaoImpl.getStudyLevelDeviceToken() ResultSet size::" + rs.size());
      if (rs != null) {
        for (Object[] objects : rs) {

          String studyId = (String) objects[0];
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

    logger.exit("getStudyLevelDeviceToken() - ends ");
    return studyDeviceTokenMap;
  }

  public String getParticipantId(String id, String customStudyId) {
    logger.entry("Begin getParticicpantId()");
    CriteriaBuilder criteriaBuilder = null;
    String participantId = null;
    CriteriaQuery<StudyEntity> criteriaQuery = null;
    Root<StudyEntity> root = null;
    Predicate[] predicates = new Predicate[1];
    List<StudyEntity> list = null;
    StudyEntity studyInfo = null;

    CriteriaQuery<ParticipantStudyEntity> criteriaQuery1 = null;
    Root<ParticipantStudyEntity> root1 = null;
    Predicate[] predicates1 = new Predicate[2];
    List<ParticipantStudyEntity> list1 = null;
    ParticipantStudyEntity participantStudyBo = null;

    Session session = this.sessionFactory.getCurrentSession();
    criteriaBuilder = session.getCriteriaBuilder();
    criteriaQuery = criteriaBuilder.createQuery(StudyEntity.class);
    root = criteriaQuery.from(StudyEntity.class);
    predicates[0] = criteriaBuilder.equal(root.get("customId"), customStudyId);
    criteriaQuery.select(root).where(predicates);
    list = session.createQuery(criteriaQuery).getResultList();
    if (!list.isEmpty()) {
      studyInfo = list.get(0);
      UserDetailsEntity userDetails = session.get(UserDetailsEntity.class, id);
      criteriaQuery1 = criteriaBuilder.createQuery(ParticipantStudyEntity.class);
      root1 = criteriaQuery1.from(ParticipantStudyEntity.class);
      predicates1[0] = criteriaBuilder.equal(root1.get("study"), studyInfo);
      predicates1[1] = criteriaBuilder.equal(root1.get("userDetails"), userDetails);
      criteriaQuery1.select(root1).where(predicates1);
      list1 = session.createQuery(criteriaQuery1).getResultList();
      if (!list1.isEmpty()) {
        participantStudyBo = list1.get(0);
        participantId = participantStudyBo.getParticipantId();
      }
    }
    logger.exit("getParticicpantId() - Ends ");
    return participantId;
  }
}
