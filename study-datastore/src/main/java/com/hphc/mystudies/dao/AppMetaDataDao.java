/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package com.hphc.mystudies.dao;

import com.hphc.mystudies.bean.AppUpdatesResponse;
import com.hphc.mystudies.bean.NotificationsBean;
import com.hphc.mystudies.bean.NotificationsResponse;
import com.hphc.mystudies.bean.StudyUpdatesBean;
import com.hphc.mystudies.bean.StudyUpdatesResponse;
import com.hphc.mystudies.bean.TermsPolicyResponse;
import com.hphc.mystudies.dto.AppVersionDto;
import com.hphc.mystudies.dto.AppVersionInfo;
import com.hphc.mystudies.dto.NotificationDto;
import com.hphc.mystudies.dto.ResourcesDto;
import com.hphc.mystudies.dto.StudyDto;
import com.hphc.mystudies.dto.StudyVersionDto;
import com.hphc.mystudies.exception.DAOException;
import com.hphc.mystudies.util.HibernateUtil;
import com.hphc.mystudies.util.StudyMetaDataConstants;
import com.hphc.mystudies.util.StudyMetaDataEnum;
import com.hphc.mystudies.util.StudyMetaDataUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class AppMetaDataDao {

  private static final XLogger LOGGER = XLoggerFactory.getXLogger(AppMetaDataDao.class.getName());

  @SuppressWarnings("unchecked")
  HashMap<String, String> propMap = StudyMetaDataUtil.getAppProperties();

  @SuppressWarnings("unchecked")
  HashMap<String, String> authPropMap = StudyMetaDataUtil.getAuthorizationProperties();

  SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
  Query query = null;

  public TermsPolicyResponse termsPolicy() throws DAOException {
    LOGGER.entry("begin termsPolicy()");
    TermsPolicyResponse termsPolicyResponse = new TermsPolicyResponse();
    try {
      termsPolicyResponse.setMessage(StudyMetaDataConstants.SUCCESS);
      termsPolicyResponse.setPrivacy(
          propMap.get(StudyMetaDataConstants.FDA_SMD_PRIVACY_POLICY) == null
              ? ""
              : propMap.get(StudyMetaDataConstants.FDA_SMD_PRIVACY_POLICY));
      termsPolicyResponse.setTerms(
          propMap.get(StudyMetaDataConstants.FDA_SMD_TERMS) == null
              ? ""
              : propMap.get(StudyMetaDataConstants.FDA_SMD_TERMS));
    } catch (Exception e) {
      LOGGER.error("AppMetaDataDao - termsPolicy() :: ERROR", e);
    }
    LOGGER.exit("termsPolicy() :: Ends");
    return termsPolicyResponse;
  }

  @SuppressWarnings("unchecked")
  public NotificationsResponse notifications(String skip, String authorization, String appId)
      throws DAOException {
    LOGGER.entry("begin notifications()");
    Session session = null;
    NotificationsResponse notificationsResponse = new NotificationsResponse();
    List<NotificationDto> notificationList = null;
    String bundleIdType = "";
    String platformType = "";
    List<NotificationsBean> notifyList = new ArrayList<>();
    AppVersionDto appVersion = null;
    String notificationStudyTypeQuery = "";
    String customStudyQuery = "";
    String deviceType = "";
    String scheduledDate = "";
    String scheduledTime = "";
    try {
      bundleIdType =
          StudyMetaDataUtil.platformType(
              authorization, StudyMetaDataConstants.STUDY_AUTH_TYPE_BUNDLE_ID);
      deviceType =
          StudyMetaDataUtil.platformType(authorization, StudyMetaDataConstants.STUDY_AUTH_TYPE_OS);
      if (StringUtils.isNotEmpty(bundleIdType) && StringUtils.isNotEmpty(deviceType)) {
        platformType = deviceType.substring(0, 1).toUpperCase();
        session = sessionFactory.openSession();

        List<String> notificationTypeList =
            Arrays.asList(
                StudyMetaDataConstants.NOTIFICATION_SUBTYPE_GENERAL,
                StudyMetaDataConstants.NOTIFICATION_SUBTYPE_STUDY,
                StudyMetaDataConstants.NOTIFICATION_SUBTYPE_ACTIVITY,
                StudyMetaDataConstants.NOTIFICATION_SUBTYPE_RESOURCE,
                StudyMetaDataConstants.NOTIFICATION_SUBTYPE_STUDY_EVENT);

        notificationStudyTypeQuery =
            "from NotificationDto NDTO"
                + " where NDTO.notificationSubType in (:notificationTypeList) "
                + " and (NDTO.appId=:appId"
                + " or NDTO.appId is null) and NDTO.notificationSent=true"
                + " ORDER BY NDTO.scheduleDate DESC";

        notificationList =
            session
                .createQuery(notificationStudyTypeQuery)
                .setParameterList("notificationTypeList", notificationTypeList)
                .setParameter("appId", appId)
                .setFirstResult(Integer.parseInt(skip))
                .setMaxResults(20)
                .list();
        if ((notificationList != null) && !notificationList.isEmpty()) {
          Map<Integer, NotificationsBean> notificationTreeMap = new HashMap<>();
          HashMap<Integer, String> hashMap = new HashMap<>();
          List<Integer> notificationIdsList = new ArrayList<>();
          List<String> scheduleDateTimes = new ArrayList<>();
          for (NotificationDto notificationDto : notificationList) {
            NotificationsBean notifyBean = new NotificationsBean();
            notifyBean.setNotificationId(notificationDto.getNotificationId().toString());
            if (notificationDto
                .getNotificationType()
                .equalsIgnoreCase(StudyMetaDataConstants.NOTIFICATION_TYPE_GT)) {
              notifyBean.setType(StudyMetaDataConstants.NOTIFICATION_GATEWAY);
              notifyBean.setAudience(StudyMetaDataConstants.NOTIFICATION_AUDIENCE_ALL);
            } else {
              notifyBean.setType(StudyMetaDataConstants.NOTIFICATION_STANDALONE);
              notifyBean.setAudience(
                  notificationDto.isAnchorDate()
                      ? StudyMetaDataConstants.NOTIFICATION_AUDIENCE_LIMITED
                      : StudyMetaDataConstants.NOTIFICATION_AUDIENCE_PARTICIPANTS);
            }

            // notification subType
            if (notificationDto
                .getNotificationSubType()
                .equalsIgnoreCase(StudyMetaDataConstants.NOTIFICATION_SUBTYPE_STUDY_EVENT)) {
              notifyBean.setSubtype(
                  StringUtils.isEmpty(notificationDto.getNotificationSubType())
                      ? ""
                      : StudyMetaDataConstants.NOTIFICATION_SUBTYPE_GENERAL);
            } else {
              notifyBean.setSubtype(
                  StringUtils.isEmpty(notificationDto.getNotificationSubType())
                      ? ""
                      : notificationDto.getNotificationSubType());
            }

            notifyBean.setTitle(
                propMap.get(StudyMetaDataConstants.FDA_SMD_NOTIFICATION_TITLE) == null
                    ? ""
                    : propMap.get(StudyMetaDataConstants.FDA_SMD_NOTIFICATION_TITLE));
            notifyBean.setMessage(
                StringUtils.isEmpty(notificationDto.getNotificationText())
                    ? ""
                    : notificationDto.getNotificationText());
            notifyBean.setStudyId(
                StringUtils.isEmpty(notificationDto.getCustomStudyId())
                    ? ""
                    : notificationDto.getCustomStudyId());
            scheduledDate =
                notificationDto.isAnchorDate()
                    ? StudyMetaDataUtil.getCurrentDate()
                    : notificationDto.getScheduleDate();
            scheduledTime =
                StringUtils.isEmpty(notificationDto.getScheduleTime())
                    ? StudyMetaDataConstants.DEFAULT_MIN_TIME
                    : notificationDto.getScheduleTime();
            notifyBean.setDate(
                StudyMetaDataUtil.getFormattedDateTimeZone(
                    scheduledDate + " " + scheduledTime,
                    StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                    StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));

            notificationIdsList.add(notificationDto.getNotificationId());
            notificationTreeMap.put(notificationDto.getNotificationId(), notifyBean);
            hashMap.put(notificationDto.getNotificationId(), scheduledDate + " " + scheduledTime);
          }

          LinkedHashMap<Integer, String> sortedMap = sortHashMapByValues(hashMap);
          for (Integer id : sortedMap.keySet()) {
            notifyList.add(notificationTreeMap.get(id));
          }
        }
      }

      notificationsResponse.setNotifications(notifyList);
      notificationsResponse.setMessage(StudyMetaDataConstants.SUCCESS);
    } catch (Exception e) {
      LOGGER.error("AppMetaDataDao - notifications() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.exit("notifications() :: Ends");
    return notificationsResponse;
  }

  public LinkedHashMap<Integer, String> sortHashMapByValues(HashMap<Integer, String> passedMap) {
    List<Integer> mapKeys = new ArrayList<>(passedMap.keySet());
    List<String> mapValues = new ArrayList<>(passedMap.values());
    LinkedHashMap<Integer, String> sortedMap = new LinkedHashMap<>();
    Iterator<String> valueIt = null;
    Iterator<Integer> keyIt = null;

    Collections.sort(mapKeys, Collections.reverseOrder());

    Collections.sort(
        mapValues,
        new Comparator<String>() {

          @Override
          public int compare(String dateTimeOne, String dateTimeTwo) {
            return dateTimeTwo.compareTo(dateTimeOne);
          }
        });

    valueIt = mapValues.iterator();
    while (valueIt.hasNext()) {
      String val = valueIt.next();
      keyIt = mapKeys.iterator();

      while (keyIt.hasNext()) {
        Integer key = keyIt.next();
        String comp1 = passedMap.get(key);
        String comp2 = val;

        if (comp1.equals(comp2)) {
          keyIt.remove();
          sortedMap.put(key, val);
          break;
        }
      }
    }
    return sortedMap;
  }

  public AppUpdatesResponse appUpdates(String appVersion, String authCredentials)
      throws DAOException {
    LOGGER.entry("begin appUpdates()");
    Session session = null;
    AppUpdatesResponse appUpdates = new AppUpdatesResponse();
    AppVersionDto appVersionDto = null;
    String os = "";
    String bundleId = "";
    try {
      os =
          StudyMetaDataUtil.platformType(
              authCredentials, StudyMetaDataConstants.STUDY_AUTH_TYPE_OS);
      bundleId = StudyMetaDataUtil.getBundleIdFromAuthorization(authCredentials);
      if (StringUtils.isNotEmpty(os)) {
        session = sessionFactory.openSession();
        appVersionDto =
            (AppVersionDto)
                session
                    .getNamedQuery("AppVersionDto.findByBundleIdOsTypeAppVersion")
                    .setString(StudyMetaDataEnum.QF_BUNDLE_ID.value(), bundleId)
                    .setString(StudyMetaDataEnum.QF_OS_TYPE.value(), os)
                    .setMaxResults(1)
                    .uniqueResult();
        if (appVersionDto != null) {
          if (Float.compare(
                  Float.parseFloat(appVersion), appVersionDto.getAppVersion().floatValue())
              < 0) {
            appUpdates.setForceUpdate(
                appVersionDto.getForceUpdate().intValue() == 0 ? false : true);
            appUpdates.setCurrentVersion(appVersionDto.getAppVersion().toString());
            appUpdates.setMessage(
                StringUtils.isEmpty(appVersionDto.getMessage()) ? "" : appVersionDto.getMessage());
          } else {
            appUpdates.setForceUpdate(false);
            appUpdates.setCurrentVersion(appVersionDto.getAppVersion().toString());
            appUpdates.setMessage(
                StringUtils.isEmpty(appVersionDto.getMessage()) ? "" : appVersionDto.getMessage());
          }
        }
      }

      if (appVersionDto == null) {
        appUpdates.setForceUpdate(false);
        appUpdates.setCurrentVersion(appVersion);
        appUpdates.setMessage("");
      }
    } catch (Exception e) {
      LOGGER.error("AppMetaDataDao - appUpdates() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.exit("appUpdates() :: Ends");
    return appUpdates;
  }

  @SuppressWarnings("unchecked")
  public StudyUpdatesResponse studyUpdates(String studyId, String studyVersion)
      throws DAOException {
    LOGGER.entry("begin studyUpdates()");
    Session session = null;
    StudyUpdatesResponse studyUpdates = new StudyUpdatesResponse();
    StudyUpdatesBean updates = new StudyUpdatesBean();
    List<StudyVersionDto> studyVersionList = null;
    StudyVersionDto currentVersion = null;
    StudyVersionDto latestVersion = null;
    List<ResourcesDto> resourcesList = null;
    StudyDto studyDto = null;
    StudyDto studyActivityStatus = null;
    try {
      session = sessionFactory.openSession();
      studyVersionList =
          session
              .getNamedQuery("getStudyUpdatesDetailsByCurrentVersion")
              .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
              .setFloat(StudyMetaDataEnum.QF_STUDY_VERSION.value(), Float.valueOf(studyVersion))
              .list();
      if ((studyVersionList != null) && !studyVersionList.isEmpty()) {
        currentVersion = studyVersionList.get(0);
        latestVersion = studyVersionList.get(studyVersionList.size() - 1);
        updates.setConsent(
            latestVersion.getConsentVersion().floatValue()
                    > currentVersion.getConsentVersion().floatValue()
                ? true
                : false);

        // check whether activityUpdated or not
        studyActivityStatus =
            (StudyDto)
                session
                    .getNamedQuery("getActivityUpdatedOrNotByStudyIdAndVersion")
                    .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                    .setFloat(StudyMetaDataEnum.QF_VERSION.value(), latestVersion.getStudyVersion())
                    .uniqueResult();
        if ((studyActivityStatus != null)
            && (studyActivityStatus.getHasActivetaskDraft() != null)
            && (studyActivityStatus.getHasQuestionnaireDraft() != null)) {
          if ((studyActivityStatus.getHasActivetaskDraft().intValue() == 0)
              && (studyActivityStatus.getHasQuestionnaireDraft().intValue() == 0)) {
            updates.setActivities(false);
          } else {
            updates.setActivities(
                latestVersion.getStudyVersion().floatValue()
                        > currentVersion.getStudyVersion().floatValue()
                    ? true
                    : false);
          }
        }
        updates.setResources(
            latestVersion.getStudyVersion().floatValue()
                    > currentVersion.getStudyVersion().floatValue()
                ? true
                : false);

        // check whether resources are available for the latest version
        // or not
        resourcesList =
            session
                .createQuery(
                    "from ResourcesDto RDTO"
                        + " where RDTO.studyId in (select SDTO.id"
                        + " from StudyDto SDTO"
                        + " where SDTO.customStudyId= :customStudyId and ROUND(SDTO.version, 1)= :version)")
                .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                .setFloat(StudyMetaDataEnum.QF_VERSION.value(), latestVersion.getStudyVersion())
                .list();
        if ((resourcesList == null) || resourcesList.isEmpty()) {
          updates.setResources(false);
        }
        updates.setInfo(
            latestVersion.getStudyVersion().floatValue()
                    > currentVersion.getStudyVersion().floatValue()
                ? true
                : false);
        studyUpdates.setCurrentVersion(latestVersion.getStudyVersion().toString());
      }

      // get the status of the latest study
      studyDto =
          (StudyDto)
              session
                  .createQuery(
                      "from StudyDto SDTO"
                          + " where SDTO.customStudyId= :customStudyId"
                          + " ORDER BY SDTO.id DESC")
                  .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                  .setMaxResults(1)
                  .uniqueResult();
      if (studyDto != null) {
        switch (studyDto.getStatus()) {
          case StudyMetaDataConstants.STUDY_STATUS_ACTIVE:
            updates.setStatus(StudyMetaDataConstants.STUDY_ACTIVE);
            break;
          case StudyMetaDataConstants.STUDY_STATUS_PAUSED:
            updates.setStatus(StudyMetaDataConstants.STUDY_PAUSED);
            break;
          case StudyMetaDataConstants.STUDY_STATUS_PRE_PUBLISH:
            updates.setStatus(StudyMetaDataConstants.STUDY_UPCOMING);
            break;
          case StudyMetaDataConstants.STUDY_STATUS_DEACTIVATED:
            updates.setStatus(StudyMetaDataConstants.STUDY_CLOSED);
            break;
          default:
            break;
        }

        // get the latest version of study
        if (StringUtils.isEmpty(studyUpdates.getCurrentVersion())) {
          studyUpdates.setCurrentVersion(studyDto.getVersion().toString());
        }
      }

      studyUpdates.setUpdates(updates);
      studyUpdates.setMessage(StudyMetaDataConstants.SUCCESS);
    } catch (Exception e) {
      LOGGER.error("AppMetaDataDao - studyUpdates() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.exit("studyUpdates() :: Ends");
    return studyUpdates;
  }

  @SuppressWarnings("unchecked")
  public String updateAppVersionDetails(
      String forceUpdate,
      String osType,
      String appVersion,
      String bundleId,
      String customStudyId,
      String message)
      throws DAOException {
    LOGGER.entry("begin updateAppVersionDetails()");
    Session session = null;
    Transaction transaction = null;
    String updateAppVersionResponse = "OOPS! Something went wrong.";
    List<AppVersionDto> appVersionDtoList = null;
    Boolean updateFlag = false;
    AppVersionDto appVersionDto = new AppVersionDto();
    try {
      session = sessionFactory.openSession();
      appVersionDtoList =
          session
              .getNamedQuery("AppVersionDto.findByBundleIdOsTypeAppVersion")
              .setString(StudyMetaDataEnum.QF_BUNDLE_ID.value(), bundleId)
              .setString(StudyMetaDataEnum.QF_OS_TYPE.value(), osType)
              .list();
      if ((appVersionDtoList != null) && !appVersionDtoList.isEmpty()) {
        if (Float.compare(
                Float.parseFloat(appVersion), appVersionDtoList.get(0).getAppVersion().floatValue())
            == 0) {
          if (Integer.parseInt(forceUpdate)
              == appVersionDtoList.get(0).getForceUpdate().intValue()) {
            updateAppVersionResponse =
                "v" + appVersion + " is already available for os " + osType + "";
          } else {
            updateFlag = true;
            appVersionDto = appVersionDtoList.get(0);
          }
        } else {
          for (AppVersionDto avDto : appVersionDtoList) {
            if (Float.parseFloat(appVersion) > avDto.getAppVersion().floatValue()) {
              updateFlag = true;
              break;
            }

            if (Float.compare(Float.parseFloat(appVersion), avDto.getAppVersion().floatValue())
                == 0) {
              if (Integer.parseInt(forceUpdate) == avDto.getForceUpdate().intValue()) {
                updateAppVersionResponse =
                    "v" + appVersion + " is already available for os " + osType + "";
                break;
              } else {
                updateFlag = true;
                appVersionDto = avDto;
              }
            }
          }
        }
      } else {
        updateFlag = true;
      }

      // Save new app version details
      if (updateFlag) {
        transaction = session.beginTransaction();
        appVersionDto.setAppVersion(Float.parseFloat(appVersion));
        appVersionDto.setForceUpdate(Integer.parseInt(forceUpdate));
        appVersionDto.setOsType(osType);
        appVersionDto.setCreatedOn(StudyMetaDataUtil.getCurrentDateTime());
        appVersionDto.setBundleId(bundleId);
        appVersionDto.setCustomStudyId(customStudyId);
        appVersionDto.setMessage(message);

        session.saveOrUpdate(appVersionDto);

        transaction.commit();
        updateAppVersionResponse =
            "App Version was successfully updated to v" + appVersion + " for " + osType + " os.";
      }
    } catch (Exception e) {
      LOGGER.error("AppMetaDataDao - updateAppVersionDetails() :: ERROR", e);
      if (transaction != null) {
        transaction.rollback();
      }
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.exit("updateAppVersionDetails() :: Ends");
    return updateAppVersionResponse;
  }

  public AppVersionInfo getAppVersionInfo(String appId) {
    LOGGER.entry("begin getAppVersionInfo()");
    Session session = null;
    AppVersionInfo appVersionInfo = null;
    try {
      session = sessionFactory.openSession();
      appVersionInfo =
          (AppVersionInfo)
              session
                  .getNamedQuery("AppVersionInfo.findAll")
                  .setParameter("appId", appId)
                  .uniqueResult();
    } catch (Exception e) {
      LOGGER.error("ERROR: AppMetaDataDao - getAppVersionInfo()", e);
    }
    LOGGER.exit("getAppVersionInfo() :: Ends");
    return appVersionInfo;
  }
}
