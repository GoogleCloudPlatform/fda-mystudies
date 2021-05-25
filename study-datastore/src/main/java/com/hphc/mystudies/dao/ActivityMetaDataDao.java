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

import com.hphc.mystudies.bean.ActiveTaskActivityMetaDataResponse;
import com.hphc.mystudies.bean.ActiveTaskActivityStepsBean;
import com.hphc.mystudies.bean.ActiveTaskActivityStructureBean;
import com.hphc.mystudies.bean.ActivitiesBean;
import com.hphc.mystudies.bean.ActivityAnchorDateBean;
import com.hphc.mystudies.bean.ActivityAnchorEndBean;
import com.hphc.mystudies.bean.ActivityAnchorStartBean;
import com.hphc.mystudies.bean.ActivityFrequencyAnchorRunsBean;
import com.hphc.mystudies.bean.ActivityFrequencyBean;
import com.hphc.mystudies.bean.ActivityFrequencyScheduleBean;
import com.hphc.mystudies.bean.ActivityMetadataBean;
import com.hphc.mystudies.bean.ActivityResponse;
import com.hphc.mystudies.bean.DestinationBean;
import com.hphc.mystudies.bean.FetalKickCounterFormatBean;
import com.hphc.mystudies.bean.QuestionnaireActivityMetaDataResponse;
import com.hphc.mystudies.bean.QuestionnaireActivityStepsBean;
import com.hphc.mystudies.bean.QuestionnaireStepsBean;
import com.hphc.mystudies.bean.SpatialSpanMemoryFormatBean;
import com.hphc.mystudies.bean.TowerOfHanoiFormatBean;
import com.hphc.mystudies.bean.appendix.QuestionnaireActivityStructureBean;
import com.hphc.mystudies.dto.ActiveTaskAttrtibutesValuesDto;
import com.hphc.mystudies.dto.ActiveTaskCustomFrequenciesDto;
import com.hphc.mystudies.dto.ActiveTaskDto;
import com.hphc.mystudies.dto.ActiveTaskFrequencyDto;
import com.hphc.mystudies.dto.ActiveTaskListDto;
import com.hphc.mystudies.dto.ActiveTaskMasterAttributeDto;
import com.hphc.mystudies.dto.AnchorDateTypeDto;
import com.hphc.mystudies.dto.FormMappingDto;
import com.hphc.mystudies.dto.InstructionsDto;
import com.hphc.mystudies.dto.QuestionReponseTypeDto;
import com.hphc.mystudies.dto.QuestionResponseSubTypeDto;
import com.hphc.mystudies.dto.QuestionResponsetypeMasterInfoDto;
import com.hphc.mystudies.dto.QuestionnairesCustomFrequenciesDto;
import com.hphc.mystudies.dto.QuestionnairesDto;
import com.hphc.mystudies.dto.QuestionnairesFrequenciesDto;
import com.hphc.mystudies.dto.QuestionnairesStepsDto;
import com.hphc.mystudies.dto.QuestionsDto;
import com.hphc.mystudies.dto.StudyDto;
import com.hphc.mystudies.dto.StudyVersionDto;
import com.hphc.mystudies.exception.DAOException;
import com.hphc.mystudies.util.HibernateUtil;
import com.hphc.mystudies.util.StudyMetaDataConstants;
import com.hphc.mystudies.util.StudyMetaDataEnum;
import com.hphc.mystudies.util.StudyMetaDataUtil;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import javax.net.ssl.HttpsURLConnection;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class ActivityMetaDataDao {

  private static final XLogger LOGGER =
      XLoggerFactory.getXLogger(ActivityMetaDataDao.class.getName());

  @SuppressWarnings("unchecked")
  HashMap<String, String> propMap = StudyMetaDataUtil.getAppProperties();

  @SuppressWarnings("unchecked")
  HashMap<String, String> authPropMap = StudyMetaDataUtil.getAuthorizationProperties();

  SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
  Query query = null;

  @SuppressWarnings("unchecked")
  public ActivityResponse studyActivityList(String studyId, String authorization)
      throws DAOException {
    LOGGER.entry("begin studyActivityList()");
    Session session = null;
    ActivityResponse activityResponse = new ActivityResponse();
    List<ActiveTaskDto> activeTaskDtoList = null;
    List<QuestionnairesDto> questionnairesList = null;
    List<ActivitiesBean> activitiesBeanList = new ArrayList<>();
    StudyDto studyDto = null;
    StudyVersionDto studyVersionDto = null;
    String deviceType = "";
    try {
      deviceType =
          StudyMetaDataUtil.platformType(authorization, StudyMetaDataConstants.STUDY_AUTH_TYPE_OS);
      session = sessionFactory.openSession();
      studyDto =
          (StudyDto)
              session
                  .getNamedQuery("getLiveStudyIdByCustomStudyId")
                  .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                  .uniqueResult();
      if (studyDto != null) {

        studyVersionDto =
            (StudyVersionDto)
                session
                    .getNamedQuery("getLiveVersionDetailsByCustomStudyIdAndVersion")
                    .setString(
                        StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyDto.getCustomStudyId())
                    .setFloat(StudyMetaDataEnum.QF_STUDY_VERSION.value(), studyDto.getVersion())
                    .setMaxResults(1)
                    .uniqueResult();

        activeTaskDtoList =
            session
                .getNamedQuery("getActiveTaskDetailsByCustomStudyId")
                .setString(
                    StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(),
                    studyVersionDto.getCustomStudyId())
                .setInteger(StudyMetaDataEnum.QF_LIVE.value(), 1)
                .setInteger(StudyMetaDataEnum.QF_ACTIVE.value(), 0)
                .list();
        if ((null != activeTaskDtoList) && !activeTaskDtoList.isEmpty()) {
          for (ActiveTaskDto activeTaskDto : activeTaskDtoList) {
            boolean isSupporting = true;

            /** Allow spatial span memory and tower of hanoi active tasks only for iOS platform */
            if (deviceType.equalsIgnoreCase(StudyMetaDataConstants.STUDY_PLATFORM_ANDROID)
                && !activeTaskDto.getTaskTypeId().equals(String.valueOf(1))) {
              isSupporting = false;
            }

            if (isSupporting) {
              ActivitiesBean activityBean = new ActivitiesBean();
              activityBean.setTitle(
                  StringUtils.isEmpty(activeTaskDto.getDisplayName())
                      ? ""
                      : activeTaskDto.getDisplayName());
              activityBean.setType(StudyMetaDataConstants.ACTIVITY_ACTIVE_TASK);
              activityBean.setState(
                  ((activeTaskDto.getActive() != null) && (activeTaskDto.getActive() > 0))
                      ? StudyMetaDataConstants.ACTIVITY_STATUS_ACTIVE
                      : StudyMetaDataConstants.ACTIVITY_STATUS_DELETED);

              activityBean.setActivityVersion(
                  ((activeTaskDto.getVersion() == null) || (activeTaskDto.getVersion() < 1.0f))
                      ? StudyMetaDataConstants.STUDY_DEFAULT_VERSION
                      : activeTaskDto.getVersion().toString());
              activityBean.setBranching(false);
              activityBean.setLastModified(
                  StringUtils.isEmpty(activeTaskDto.getModifiedDate())
                      ? ""
                      : StudyMetaDataUtil.getFormattedDateTimeZone(
                          activeTaskDto.getModifiedDate(),
                          StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                          StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));

              ActivityFrequencyBean frequencyDetails = new ActivityFrequencyBean();
              frequencyDetails =
                  this.getFrequencyRunsDetailsForActiveTasks(
                      activeTaskDto, frequencyDetails, session);
              frequencyDetails.setType(
                  StringUtils.isEmpty(activeTaskDto.getFrequency())
                      ? ""
                      : activeTaskDto.getFrequency());
              activityBean.setFrequency(frequencyDetails);

              activityBean =
                  this.getTimeDetailsByActivityIdForActiveTask(
                      activeTaskDto, activityBean, session);

              /** For deleted task modified date time will be the end date time of active task */
              if ((activeTaskDto.getActive() == null) || activeTaskDto.getActive().equals(0)) {
                activityBean.setEndTime(
                    StudyMetaDataUtil.getFormattedDateTimeZone(
                        activeTaskDto.getModifiedDate(),
                        StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                        StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
              }

              activityBean.setActivityId(activeTaskDto.getShortTitle());

              if (null != activeTaskDto.getTaskTypeId()) {
                if ("1".equals(activeTaskDto.getTaskTypeId())) {
                  activityBean.setTaskSubType(
                      StudyMetaDataConstants.ACTIVITY_AT_FETAL_KICK_COUNTER);
                } else if ("2".equals(activeTaskDto.getTaskTypeId())) {
                  activityBean.setTaskSubType(StudyMetaDataConstants.ACTIVITY_AT_TOWER_OF_HANOI);
                } else if ("3".equals(activeTaskDto.getTaskTypeId())) {
                  activityBean.setTaskSubType(
                      StudyMetaDataConstants.ACTIVITY_AT_SPATIAL_SPAN_MEMORY);
                }
              }
              /** Phase2 a code for anchor date * */
              if ((activeTaskDto.getScheduleType() != null)
                  && !activeTaskDto.getScheduleType().isEmpty()) {
                activityBean.setSchedulingType(activeTaskDto.getScheduleType());
                if (activeTaskDto
                    .getScheduleType()
                    .equals(StudyMetaDataConstants.SCHEDULETYPE_ANCHORDATE)) {
                  activityBean =
                      this.getAnchordateDetailsByActivityIdForActivetask(
                          activeTaskDto, activityBean, session);
                }
              }
              /** Phase2a code for anchor date * */
              activitiesBeanList.add(activityBean);
            }
          }
        }

        questionnairesList =
            session
                .getNamedQuery("getQuestionnaireDetailsByCustomStudyId")
                .setString(
                    StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(),
                    studyVersionDto.getCustomStudyId())
                .setInteger(StudyMetaDataEnum.QF_LIVE.value(), 1)
                .setBoolean(StudyMetaDataEnum.QF_ACTIVE.value(), false)
                .list();
        if ((questionnairesList != null) && !questionnairesList.isEmpty()) {

          for (QuestionnairesDto questionaire : questionnairesList) {

            ActivitiesBean activityBean = new ActivitiesBean();
            activityBean.setTitle(
                StringUtils.isEmpty(questionaire.getTitle()) ? "" : questionaire.getTitle());
            activityBean.setType(StudyMetaDataConstants.ACTIVITY_QUESTIONNAIRE);
            activityBean.setState(
                questionaire.getActive()
                    ? StudyMetaDataConstants.ACTIVITY_STATUS_ACTIVE
                    : StudyMetaDataConstants.ACTIVITY_STATUS_DELETED);

            ActivityFrequencyBean frequencyDetails = new ActivityFrequencyBean();
            frequencyDetails =
                this.getFrequencyRunsDetailsForQuestionaires(
                    questionaire, frequencyDetails, session);
            frequencyDetails.setType(
                StringUtils.isEmpty(questionaire.getFrequency())
                    ? ""
                    : questionaire.getFrequency());
            activityBean.setFrequency(frequencyDetails);
            activityBean.setActivityId(questionaire.getShortTitle());
            activityBean.setActivityVersion(
                ((questionaire.getVersion() == null) || (questionaire.getVersion() < 1.0f))
                    ? StudyMetaDataConstants.STUDY_DEFAULT_VERSION
                    : questionaire.getVersion().toString());
            activityBean.setBranching(
                ((questionaire.getBranching() == null) || !questionaire.getBranching())
                    ? false
                    : true);
            activityBean.setLastModified(
                StringUtils.isEmpty(questionaire.getModifiedDate())
                    ? ""
                    : StudyMetaDataUtil.getFormattedDateTimeZone(
                        questionaire.getModifiedDate(),
                        StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                        StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
            activityBean =
                this.getTimeDetailsByActivityIdForQuestionnaire(
                    questionaire, activityBean, session);

            /** For deleted task modified date time will be the end date time of questionnaire */
            if (!questionaire.getActive()) {
              activityBean.setEndTime(
                  StudyMetaDataUtil.getFormattedDateTimeZone(
                      questionaire.getModifiedDate(),
                      StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                      StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
            }
            /** Phase2 a code for anchor date * */
            if ((questionaire.getScheduleType() != null)
                && !questionaire.getScheduleType().isEmpty()) {
              activityBean.setSchedulingType(questionaire.getScheduleType());
              if (questionaire
                  .getScheduleType()
                  .equals(StudyMetaDataConstants.SCHEDULETYPE_ANCHORDATE)) {
                activityBean =
                    this.getAnchordateDetailsByActivityIdForQuestionnaire(
                        questionaire, activityBean, session);
              }
            }
            /** Phase2a code for anchor date * */
            activitiesBeanList.add(activityBean);
          }
        }

        activityResponse.setActivities(activitiesBeanList);
        activityResponse.setMessage(StudyMetaDataConstants.SUCCESS);
      } else {
        activityResponse.setMessage(StudyMetaDataConstants.INVALID_STUDY_ID);
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - studyActivityList() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.exit("studyActivityList() :: Ends");
    return activityResponse;
  }

  public ActiveTaskActivityMetaDataResponse studyActiveTaskActivityMetadata(
      String studyId, String activityId, String activityVersion) throws DAOException {
    LOGGER.entry("begin studyActiveTaskActivityMetadata()");
    Session session = null;
    ActiveTaskActivityMetaDataResponse activeTaskActivityMetaDataResponse =
        new ActiveTaskActivityMetaDataResponse();
    ActiveTaskActivityStructureBean activeTaskactivityStructureBean =
        new ActiveTaskActivityStructureBean();
    StudyDto studyDto = null;
    try {
      session = sessionFactory.openSession();
      studyDto =
          (StudyDto)
              session
                  .getNamedQuery("getLiveStudyIdByCustomStudyId")
                  .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                  .uniqueResult();
      if (studyDto != null) {
        activeTaskactivityStructureBean =
            this.activeTaskMetadata(studyId, activityId, session, activityVersion);
        activeTaskActivityMetaDataResponse.setActivity(activeTaskactivityStructureBean);
        activeTaskActivityMetaDataResponse.setMessage(StudyMetaDataConstants.SUCCESS);
      } else {
        activeTaskActivityMetaDataResponse.setMessage(StudyMetaDataConstants.INVALID_STUDY_ID);
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - studyActiveTaskActivityMetadata() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.exit("studyActiveTaskActivityMetadata() :: Ends");
    return activeTaskActivityMetaDataResponse;
  }

  public QuestionnaireActivityMetaDataResponse studyQuestionnaireActivityMetadata(
      String studyId, String activityId, String activityVersion) throws DAOException {
    LOGGER.entry("begin studyQuestionnaireActivityMetadata()");
    Session session = null;
    QuestionnaireActivityMetaDataResponse activityMetaDataResponse =
        new QuestionnaireActivityMetaDataResponse();
    QuestionnaireActivityStructureBean activityStructureBean =
        new QuestionnaireActivityStructureBean();
    StudyDto studyDto = null;
    try {
      session = sessionFactory.openSession();
      studyDto =
          (StudyDto)
              session
                  .getNamedQuery("getLiveStudyIdByCustomStudyId")
                  .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                  .uniqueResult();
      if (studyDto != null) {
        activityStructureBean =
            this.questionnaireMetadata(studyId, activityId, session, activityVersion);
        activityMetaDataResponse.setActivity(activityStructureBean);
        activityMetaDataResponse.setMessage(StudyMetaDataConstants.SUCCESS);
      } else {
        activityMetaDataResponse.setMessage(StudyMetaDataConstants.INVALID_STUDY_ID);
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - studyQuestionnaireActivityMetadata() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.exit("studyQuestionnaireActivityMetadata() :: Ends");
    return activityMetaDataResponse;
  }

  @SuppressWarnings("unchecked")
  public ActiveTaskActivityStructureBean activeTaskMetadata(
      String studyId, String activityId, Session session, String activityVersion)
      throws DAOException {
    LOGGER.entry("begin activeTaskMetadata()");
    ActiveTaskActivityStructureBean activeTaskActivityStructureBean =
        new ActiveTaskActivityStructureBean();
    ActiveTaskDto activeTaskDto = null;
    List<ActiveTaskActivityStepsBean> steps = new ArrayList<>();
    ActiveTaskListDto taskDto = null;
    try {

      activeTaskDto =
          (ActiveTaskDto)
              session
                  .createQuery(
                      "from ActiveTaskDto ATDTO"
                          + " where ATDTO.action=true and ATDTO.customStudyId= :customStudyId"
                          + " and ATDTO.shortTitle= :shortTitle"
                          + " and ROUND(ATDTO.version, 1)= :version"
                          + " ORDER BY ATDTO.modifiedDate DESC")
                  .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                  .setString(
                      StudyMetaDataEnum.QF_SHORT_TITLE.value(),
                      StudyMetaDataUtil.replaceSingleQuotes(activityId))
                  .setFloat(StudyMetaDataEnum.QF_VERSION.value(), Float.parseFloat(activityVersion))
                  .setMaxResults(1)
                  .uniqueResult();
      if (activeTaskDto != null) {

        List<String> taskMasterAttrIdList = new ArrayList<>();
        List<ActiveTaskAttrtibutesValuesDto> activeTaskAttrtibuteValuesList;
        List<ActiveTaskMasterAttributeDto> activeTaskMaterList = null;

        activeTaskActivityStructureBean.setType(StudyMetaDataConstants.ACTIVITY_ACTIVE_TASK);

        ActivityMetadataBean metadata = new ActivityMetadataBean();
        metadata.setActivityId(activeTaskDto.getShortTitle());

        ActivitiesBean activityBean = new ActivitiesBean();
        activityBean =
            this.getTimeDetailsByActivityIdForActiveTask(activeTaskDto, activityBean, session);
        metadata.setStartDate(activityBean.getStartTime());
        metadata.setEndDate(activityBean.getEndTime());
        metadata.setLastModified(
            StringUtils.isEmpty(activeTaskDto.getModifiedDate())
                ? ""
                : StudyMetaDataUtil.getFormattedDateTimeZone(
                    activeTaskDto.getModifiedDate(),
                    StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                    StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
        metadata.setName(
            StringUtils.isEmpty(activeTaskDto.getShortTitle())
                ? ""
                : activeTaskDto.getShortTitle());
        metadata.setStudyId(studyId);
        metadata.setVersion(
            activeTaskDto.getVersion() == null
                ? StudyMetaDataConstants.STUDY_DEFAULT_VERSION
                : activeTaskDto.getVersion().toString());
        if ((activeTaskDto.getActive() == null) || (activeTaskDto.getActive() == 0)) {
          metadata.setEndDate(
              StudyMetaDataUtil.getFormattedDateTimeZone(
                  activeTaskDto.getModifiedDate(),
                  StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                  StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
        }
        activeTaskActivityStructureBean.setMetadata(metadata);

        activeTaskAttrtibuteValuesList =
            session
                .createQuery(
                    "from ActiveTaskAttrtibutesValuesDto ATAVDTO"
                        + " where ATAVDTO.activeTaskId=:activeTaskId"
                        + " and ATAVDTO.activeTaskMasterAttrId in (select ATMADTO.masterId"
                        + " from ActiveTaskMasterAttributeDto ATMADTO"
                        + " where ATMADTO.attributeType=:attributeType)"
                        + " ORDER BY ATAVDTO.activeTaskMasterAttrId")
                .setString("activeTaskId", activeTaskDto.getId())
                .setString(
                    "attributeType", StudyMetaDataConstants.ACTIVE_TASK_ATTRIBUTE_TYPE_CONFIGURE)
                .list();
        if ((activeTaskAttrtibuteValuesList != null) && !activeTaskAttrtibuteValuesList.isEmpty()) {

          for (ActiveTaskAttrtibutesValuesDto attributeDto : activeTaskAttrtibuteValuesList) {
            taskMasterAttrIdList.add(attributeDto.getActiveTaskMasterAttrId());
          }

          if (!taskMasterAttrIdList.isEmpty()) {
            activeTaskMaterList =
                session
                    .createQuery(
                        " from ActiveTaskMasterAttributeDto ATMADTO"
                            + " where ATMADTO.masterId in (:taskMasterAttrIdList)")
                    .setParameterList("taskMasterAttrIdList", taskMasterAttrIdList)
                    .list();

            if ((activeTaskMaterList != null) && !activeTaskMaterList.isEmpty()) {
              taskDto =
                  (ActiveTaskListDto)
                      session
                          .createQuery(
                              "from ActiveTaskListDto ATDTO"
                                  + " where ATDTO.activeTaskListId=:activeTaskListId")
                          .setString("activeTaskListId", activeTaskMaterList.get(0).getTaskTypeId())
                          .uniqueResult();
            }
          }
        }

        Boolean attributeListFlag =
            (activeTaskAttrtibuteValuesList != null) && !activeTaskAttrtibuteValuesList.isEmpty();
        Boolean masterAttributeListFlag =
            (activeTaskMaterList != null) && !activeTaskMaterList.isEmpty();
        Boolean taskListFlag = taskDto != null;
        if (attributeListFlag && masterAttributeListFlag && taskListFlag) {

          ActiveTaskActivityStepsBean activeTaskActiveTaskStep = new ActiveTaskActivityStepsBean();
          FetalKickCounterFormatBean fetalKickCounterFormat = new FetalKickCounterFormatBean();
          SpatialSpanMemoryFormatBean spatialSpanMemoryFormat = new SpatialSpanMemoryFormatBean();
          TowerOfHanoiFormatBean towerOfHanoiFormat = new TowerOfHanoiFormatBean();
          boolean skipLoopFlag = false;
          for (ActiveTaskAttrtibutesValuesDto attributeDto : activeTaskAttrtibuteValuesList) {
            if (!skipLoopFlag) {
              for (ActiveTaskMasterAttributeDto masterAttributeDto : activeTaskMaterList) {
                if (!skipLoopFlag
                    && attributeDto
                        .getActiveTaskMasterAttrId()
                        .equals(masterAttributeDto.getMasterId())
                    && taskDto.getActiveTaskListId().equals(masterAttributeDto.getTaskTypeId())) {
                  activeTaskActiveTaskStep.setType(StudyMetaDataConstants.ACTIVITY_ACTIVE_TASK);
                  activeTaskActiveTaskStep.setResultType(
                      StringUtils.isEmpty(taskDto.getType()) ? "" : taskDto.getType());
                  activeTaskActiveTaskStep.setKey(activeTaskDto.getShortTitle());
                  activeTaskActiveTaskStep.setText(
                      StringUtils.isEmpty(activeTaskDto.getInstruction())
                          ? ""
                          : activeTaskDto.getInstruction());

                  switch (taskDto.getType()) {
                    case StudyMetaDataConstants.ACTIVITY_AT_FETAL_KICK_COUNTER:
                      fetalKickCounterFormat =
                          (FetalKickCounterFormatBean)
                              this.fetalKickCounterDetails(
                                  attributeDto, masterAttributeDto, fetalKickCounterFormat);
                      activeTaskActiveTaskStep.setFormat(fetalKickCounterFormat);

                      if (attributeDto
                          .getActiveTaskMasterAttrId()
                          .equals(
                              activeTaskMaterList
                                  .get(activeTaskMaterList.size() - 1)
                                  .getMasterId())) {
                        skipLoopFlag = true;
                      }
                      break;
                    case StudyMetaDataConstants.ACTIVITY_AT_SPATIAL_SPAN_MEMORY:
                      spatialSpanMemoryFormat =
                          (SpatialSpanMemoryFormatBean)
                              this.spatialSpanMemoryDetails(
                                  attributeDto, masterAttributeDto, spatialSpanMemoryFormat);
                      activeTaskActiveTaskStep.setFormat(spatialSpanMemoryFormat);

                      if (attributeDto
                          .getActiveTaskMasterAttrId()
                          .equals(
                              activeTaskMaterList
                                  .get(activeTaskMaterList.size() - 1)
                                  .getMasterId())) {
                        skipLoopFlag = true;
                      }
                      break;
                    case StudyMetaDataConstants.ACTIVITY_AT_TOWER_OF_HANOI:
                      towerOfHanoiFormat.setNumberOfDisks(
                          StringUtils.isEmpty(attributeDto.getAttributeVal())
                              ? 0
                              : Integer.parseInt(attributeDto.getAttributeVal()));
                      skipLoopFlag = true;
                      activeTaskActiveTaskStep.setFormat(towerOfHanoiFormat);
                      break;
                    default:
                      break;
                  }
                }
              }
            }
          }

          steps.add(activeTaskActiveTaskStep);
          activeTaskActivityStructureBean.setSteps(steps);
        }
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - activeTaskMetadata() :: ERROR", e);
    }
    LOGGER.exit("activeTaskMetadata() :: Ends");
    return activeTaskActivityStructureBean;
  }

  @SuppressWarnings("unchecked")
  public QuestionnaireActivityStructureBean questionnaireMetadata(
      String studyId, String activityId, Session session, String activityVersion)
      throws DAOException {
    LOGGER.entry("begin questionnaireMetadata()");
    QuestionnaireActivityStructureBean activityStructureBean =
        new QuestionnaireActivityStructureBean();
    Map<String, Integer> sequenceNoMap = new HashMap<>();
    Map<String, QuestionnairesStepsDto> questionnaireStepDetailsMap = new HashMap<>();
    TreeMap<Integer, QuestionnaireActivityStepsBean> stepsSequenceTreeMap = new TreeMap<>();
    QuestionnairesDto questionnaireDto = null;
    List<QuestionnairesStepsDto> questionaireStepsList = null;
    List<QuestionnaireActivityStepsBean> steps = new ArrayList<>();
    List<QuestionResponsetypeMasterInfoDto> questionResponseTypeMasterInfoList = null;
    try {
      questionnaireDto =
          (QuestionnairesDto)
              session
                  .createQuery(
                      "from QuestionnairesDto QDTO"
                          + " where QDTO.customStudyId= :customStudyId and QDTO.shortTitle= :shortTitle"
                          + " and QDTO.status=true and ROUND(QDTO.version, 1)= :version"
                          + " ORDER BY QDTO.modifiedDate DESC")
                  .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                  .setString(
                      StudyMetaDataEnum.QF_SHORT_TITLE.value(),
                      StudyMetaDataUtil.replaceSingleQuotes(activityId))
                  .setFloat(StudyMetaDataEnum.QF_VERSION.value(), Float.parseFloat(activityVersion))
                  .setMaxResults(1)
                  .uniqueResult();
      if (questionnaireDto != null) {
        activityStructureBean.setType(StudyMetaDataConstants.ACTIVITY_QUESTIONNAIRE);

        ActivityMetadataBean metadata = new ActivityMetadataBean();
        metadata.setActivityId(questionnaireDto.getShortTitle());

        ActivitiesBean activityBean = new ActivitiesBean();
        activityBean =
            this.getTimeDetailsByActivityIdForQuestionnaire(
                questionnaireDto, activityBean, session);

        metadata.setStartDate(activityBean.getStartTime());
        metadata.setEndDate(activityBean.getEndTime());
        metadata.setLastModified(
            StringUtils.isEmpty(questionnaireDto.getModifiedDate())
                ? ""
                : StudyMetaDataUtil.getFormattedDateTimeZone(
                    questionnaireDto.getModifiedDate(),
                    StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                    StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
        metadata.setName(
            StringUtils.isEmpty(questionnaireDto.getShortTitle())
                ? ""
                : questionnaireDto.getShortTitle());
        metadata.setStudyId(studyId);
        metadata.setVersion(
            questionnaireDto.getVersion() == null
                ? StudyMetaDataConstants.STUDY_DEFAULT_VERSION
                : questionnaireDto.getVersion().toString());

        if (!questionnaireDto.getActive()) {
          metadata.setEndDate(
              StudyMetaDataUtil.getFormattedDateTimeZone(
                  questionnaireDto.getModifiedDate(),
                  StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                  StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
        }

        activityStructureBean.setMetadata(metadata);

        questionaireStepsList =
            session
                .createQuery(
                    "from QuestionnairesStepsDto QSDTO"
                        + " where QSDTO.questionnairesId=:questRespId"
                        + " and QSDTO.status=true"
                        + " ORDER BY QSDTO.sequenceNo")
                .setString("questRespId", questionnaireDto.getId())
                .list();
        if ((questionaireStepsList != null) && !questionaireStepsList.isEmpty()) {

          List<String> instructionIdList = new ArrayList<>();
          List<String> questionIdList = new ArrayList<>();
          List<String> formIdList = new ArrayList<>();

          for (int i = 0; i < questionaireStepsList.size(); i++) {
            if (!questionnaireDto.getBranching()) {
              if ((questionaireStepsList.size() - 1) == i) {
                questionaireStepsList.get(i).setDestinationStep(String.valueOf(0));
              } else {
                questionaireStepsList
                    .get(i)
                    .setDestinationStep(questionaireStepsList.get(i + 1).getStepId());
              }
            }
          }

          questionaireStepsList = this.getDestinationStepType(questionaireStepsList);
          for (QuestionnairesStepsDto questionnairesStep : questionaireStepsList) {

            switch (questionnairesStep.getStepType()) {
              case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION:
                instructionIdList.add(questionnairesStep.getInstructionFormId());
                sequenceNoMap.put(
                    String.valueOf(questionnairesStep.getInstructionFormId())
                        + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION,
                    questionnairesStep.getSequenceNo());
                questionnaireStepDetailsMap.put(
                    String.valueOf(questionnairesStep.getInstructionFormId())
                        + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION,
                    questionnairesStep);
                break;
              case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION:
                questionIdList.add(questionnairesStep.getInstructionFormId());
                sequenceNoMap.put(
                    String.valueOf(questionnairesStep.getInstructionFormId())
                        + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION,
                    questionnairesStep.getSequenceNo());
                questionnaireStepDetailsMap.put(
                    String.valueOf(questionnairesStep.getInstructionFormId())
                        + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION,
                    questionnairesStep);
                break;
              case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM:
                formIdList.add(questionnairesStep.getInstructionFormId());
                sequenceNoMap.put(
                    String.valueOf(questionnairesStep.getInstructionFormId())
                        + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM,
                    questionnairesStep.getSequenceNo());
                questionnaireStepDetailsMap.put(
                    String.valueOf(questionnairesStep.getInstructionFormId())
                        + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM,
                    questionnairesStep);
                break;
              default:
                break;
            }
          }

          questionResponseTypeMasterInfoList =
              session.createQuery("from QuestionResponsetypeMasterInfoDto").list();
          StudyDto studyDto =
              (StudyDto)
                  session
                      .getNamedQuery("getLiveStudyIdByCustomStudyId")
                      .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                      .uniqueResult();

          if (!instructionIdList.isEmpty()) {
            List<InstructionsDto> instructionsDtoList =
                session
                    .createQuery(
                        "from InstructionsDto IDTO"
                            + " where IDTO.id in (:instructionIdList) and IDTO.status=true")
                    .setParameterList("instructionIdList", instructionIdList)
                    .list();
            if ((instructionsDtoList != null) && !instructionsDtoList.isEmpty()) {

              stepsSequenceTreeMap =
                  (TreeMap<Integer, QuestionnaireActivityStepsBean>)
                      this.getStepsInfoForQuestionnaires(
                          StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION,
                          instructionsDtoList,
                          null,
                          null,
                          sequenceNoMap,
                          stepsSequenceTreeMap,
                          session,
                          questionnaireStepDetailsMap,
                          null,
                          questionaireStepsList,
                          questionnaireDto,
                          studyDto);
            }
          }

          if (!questionIdList.isEmpty()) {
            List<QuestionsDto> questionsList =
                session
                    .createQuery(
                        " from QuestionsDto QDTO"
                            + " where QDTO.id in (:questionIdList"
                            + ") and QDTO.status=true")
                    .setParameterList("questionIdList", questionIdList)
                    .list();
            if ((questionsList != null) && !questionsList.isEmpty()) {
              stepsSequenceTreeMap =
                  (TreeMap<Integer, QuestionnaireActivityStepsBean>)
                      this.getStepsInfoForQuestionnaires(
                          StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION,
                          null,
                          questionsList,
                          null,
                          sequenceNoMap,
                          stepsSequenceTreeMap,
                          session,
                          questionnaireStepDetailsMap,
                          questionResponseTypeMasterInfoList,
                          questionaireStepsList,
                          questionnaireDto,
                          studyDto);
            }
          }

          if (!formIdList.isEmpty()) {
            for (String formId : formIdList) {
              List<FormMappingDto> formList =
                  session
                      .createQuery(
                          "from FormMappingDto FMDTO"
                              + " where FMDTO.formId in (select FDTO.formId"
                              + " from FormDto FDTO"
                              + " where FDTO.formId=:formId"
                              + ") and FMDTO.active=true"
                              + " ORDER BY FMDTO.sequenceNo ")
                      .setString("formId", formId)
                      .list();
              if ((formList != null) && !formList.isEmpty()) {
                stepsSequenceTreeMap =
                    (TreeMap<Integer, QuestionnaireActivityStepsBean>)
                        this.getStepsInfoForQuestionnaires(
                            StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM,
                            null,
                            null,
                            formList,
                            sequenceNoMap,
                            stepsSequenceTreeMap,
                            session,
                            questionnaireStepDetailsMap,
                            questionResponseTypeMasterInfoList,
                            questionaireStepsList,
                            questionnaireDto,
                            studyDto);
              }
            }
          }

          for (Integer key : stepsSequenceTreeMap.keySet()) {
            steps.add(stepsSequenceTreeMap.get(key));
          }

          activityStructureBean.setSteps(steps);
        }
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - questionnaireMetadata() :: ERROR", e);
    }
    LOGGER.exit("questionnaireMetadata() :: Ends");
    return activityStructureBean;
  }

  public ActivityFrequencyBean getFrequencyRunsDetailsForActiveTasks(
      ActiveTaskDto activeTask, ActivityFrequencyBean frequencyDetails, Session session)
      throws DAOException {
    LOGGER.entry("begin getFrequencyRunsDetailsForActiveTasks()");
    List<ActivityFrequencyScheduleBean> runDetailsBean = new ArrayList<>();
    List<ActivityFrequencyAnchorRunsBean> anchorRunDetailsBean = new ArrayList<>();
    try {
      switch (activeTask.getFrequency()) {
        case StudyMetaDataConstants.FREQUENCY_TYPE_DAILY:
          runDetailsBean =
              this.getActiveTaskFrequencyDetailsForDaily(activeTask, runDetailsBean, session);
          break;
        case StudyMetaDataConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE:
          runDetailsBean =
              this.getActiveTaskFrequencyDetailsForManuallySchedule(
                  activeTask, runDetailsBean, session);
          break;
        default:
          break;
      }
      frequencyDetails.setRuns(runDetailsBean);
      /** set AnchorRuns : Phase2a code start * */
      anchorRunDetailsBean =
          this.getAcivetaskFrequencyAncorDetailsForManuallySchedule(
              activeTask, anchorRunDetailsBean, session);
      frequencyDetails.setAnchorRuns(anchorRunDetailsBean);
      /** Phase2a code End * */
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getFrequencyRunsDetailsForActiveTasks() :: ERROR", e);
    }
    LOGGER.exit("getFrequencyRunsDetailsForActiveTasks() :: Ends");
    return frequencyDetails;
  }

  public List<ActivityFrequencyScheduleBean> getActiveTaskFrequencyDetailsForOneTime(
      ActiveTaskDto activeTask, List<ActivityFrequencyScheduleBean> runDetailsBean)
      throws DAOException {
    LOGGER.entry("begin getActiveTaskFrequencyDetailsForOneTime()");
    try {
      if (activeTask != null) {
        ActivityFrequencyScheduleBean oneTimeBean = new ActivityFrequencyScheduleBean();
        oneTimeBean.setStartTime(
            StudyMetaDataUtil.getFormattedDateTimeZone(
                activeTask.getActiveTaskLifetimeStart(),
                StudyMetaDataConstants.SDF_DATE_PATTERN,
                StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
        oneTimeBean.setEndTime(
            StudyMetaDataUtil.getFormattedDateTimeZone(
                activeTask.getActiveTaskLifetimeEnd(),
                StudyMetaDataConstants.SDF_DATE_PATTERN,
                StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
        runDetailsBean.add(oneTimeBean);
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getActiveTaskFrequencyDetailsForOneTime() :: ERROR", e);
    }
    LOGGER.exit("getActiveTaskFrequencyDetailsForOneTime() :: Ends");
    return runDetailsBean;
  }

  @SuppressWarnings("unchecked")
  public List<ActivityFrequencyScheduleBean> getActiveTaskFrequencyDetailsForDaily(
      ActiveTaskDto activeTask, List<ActivityFrequencyScheduleBean> runDetailsBean, Session session)
      throws DAOException {
    LOGGER.entry("begin getActiveTaskFrequencyDetailsForDaily()");
    try {
      if ((activeTask.getScheduleType() != null)
          && !activeTask.getScheduleType().isEmpty()
          && activeTask.getScheduleType().equals(StudyMetaDataConstants.SCHEDULETYPE_ANCHORDATE)) {
        List<ActiveTaskFrequencyDto> activeTaskDailyFrequencyList =
            session
                .createQuery(
                    "from ActiveTaskFrequencyDto ATFDTO"
                        + " where ATFDTO.activeTaskId= :activeTaskId"
                        + " ORDER BY ATFDTO.frequencyTime ")
                .setString(StudyMetaDataEnum.QF_ACTIVE_TASK_ID.value(), activeTask.getId())
                .list();
        if ((activeTaskDailyFrequencyList != null) && !activeTaskDailyFrequencyList.isEmpty()) {
          for (int i = 0; i < activeTaskDailyFrequencyList.size(); i++) {
            ActivityFrequencyScheduleBean dailyBean = new ActivityFrequencyScheduleBean();
            String activeTaskStartTime;
            String activeTaskEndTime;
            activeTaskStartTime = activeTaskDailyFrequencyList.get(i).getFrequencyTime();

            if (i == (activeTaskDailyFrequencyList.size() - 1)) {
              activeTaskEndTime = StudyMetaDataConstants.DEFAULT_MAX_TIME;
            } else {
              activeTaskEndTime =
                  StudyMetaDataUtil.addSeconds(
                      StudyMetaDataUtil.getCurrentDate()
                          + " "
                          + activeTaskDailyFrequencyList.get(i + 1).getFrequencyTime(),
                      -1);
              activeTaskEndTime = activeTaskEndTime.substring(11, activeTaskEndTime.length());
            }

            dailyBean.setStartTime(activeTaskStartTime);
            dailyBean.setEndTime(activeTaskEndTime);
            runDetailsBean.add(dailyBean);
          }
        }
      } else {
        if (StringUtils.isNotEmpty(activeTask.getActiveTaskLifetimeStart())
            && StringUtils.isNotEmpty(activeTask.getActiveTaskLifetimeEnd())) {
          List<ActiveTaskFrequencyDto> activeTaskDailyFrequencyList =
              session
                  .createQuery(
                      "from ActiveTaskFrequencyDto ATFDTO"
                          + " where ATFDTO.activeTaskId= :activeTaskId"
                          + " ORDER BY ATFDTO.frequencyTime ")
                  .setString(StudyMetaDataEnum.QF_ACTIVE_TASK_ID.value(), activeTask.getId())
                  .list();
          if ((activeTaskDailyFrequencyList != null) && !activeTaskDailyFrequencyList.isEmpty()) {
            for (int i = 0; i < activeTaskDailyFrequencyList.size(); i++) {
              ActivityFrequencyScheduleBean dailyBean = new ActivityFrequencyScheduleBean();
              String activeTaskStartTime;
              String activeTaskEndTime;
              activeTaskStartTime = activeTaskDailyFrequencyList.get(i).getFrequencyTime();

              if (i == (activeTaskDailyFrequencyList.size() - 1)) {
                activeTaskEndTime = StudyMetaDataConstants.DEFAULT_MAX_TIME;
              } else {
                activeTaskEndTime =
                    StudyMetaDataUtil.addSeconds(
                        StudyMetaDataUtil.getCurrentDate()
                            + " "
                            + activeTaskDailyFrequencyList.get(i + 1).getFrequencyTime(),
                        -1);
                activeTaskEndTime = activeTaskEndTime.substring(11, activeTaskEndTime.length());
              }

              dailyBean.setStartTime(activeTaskStartTime);
              dailyBean.setEndTime(activeTaskEndTime);
              runDetailsBean.add(dailyBean);
            }
          }
        }
      }

    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getActiveTaskFrequencyDetailsForDaily() :: ERROR", e);
    }
    LOGGER.exit("getActiveTaskFrequencyDetailsForDaily() :: Ends");
    return runDetailsBean;
  }

  public List<ActivityFrequencyScheduleBean> getActiveTaskFrequencyDetailsForWeekly(
      ActiveTaskDto activeTask, List<ActivityFrequencyScheduleBean> runDetailsBean)
      throws DAOException {
    LOGGER.entry("begin getActiveTaskFrequencyDetailsForWeekly()");
    try {
      if (StringUtils.isNotEmpty(activeTask.getActiveTaskLifetimeStart())
          && StringUtils.isNotEmpty(activeTask.getActiveTaskLifetimeEnd())
          && StringUtils.isNotEmpty(activeTask.getDayOfTheWeek())) {
        Integer repeatCount =
            ((activeTask.getRepeatActiveTask() == null) || (activeTask.getRepeatActiveTask() == 0))
                ? 1
                : activeTask.getRepeatActiveTask();
        String activeTaskDay = activeTask.getDayOfTheWeek();
        String activeTaskStartDate = activeTask.getActiveTaskLifetimeStart();
        while (repeatCount > 0) {
          ActivityFrequencyScheduleBean weeklyBean = new ActivityFrequencyScheduleBean();
          String activeTaskEndDate;
          String day = "";
          String weekEndDate;
          boolean flag = false;
          boolean skipLoop = false;

          if (activeTaskDay.equalsIgnoreCase(StudyMetaDataUtil.getDayByDate(activeTaskStartDate))) {
            day = activeTaskDay;
          }

          if (!activeTaskDay.equalsIgnoreCase(day)) {
            while (!activeTaskDay.equalsIgnoreCase(day)) {
              activeTaskStartDate = StudyMetaDataUtil.addDaysToDate(activeTaskStartDate, 1);
              day = StudyMetaDataUtil.getDayByDate(activeTaskStartDate);
            }
          }

          weekEndDate = StudyMetaDataUtil.addWeeksToDate(activeTaskStartDate, 1);
          if ((StudyMetaDataConstants.SDF_DATE
                  .parse(StudyMetaDataUtil.getCurrentDate())
                  .equals(StudyMetaDataConstants.SDF_DATE.parse(weekEndDate)))
              || (StudyMetaDataConstants.SDF_DATE
                  .parse(StudyMetaDataUtil.getCurrentDate())
                  .before(StudyMetaDataConstants.SDF_DATE.parse(weekEndDate)))) {
            flag = true;
          }

          if (flag) {
            activeTaskEndDate = weekEndDate;
            if ((StudyMetaDataConstants.SDF_DATE
                    .parse(weekEndDate)
                    .equals(
                        StudyMetaDataConstants.SDF_DATE.parse(
                            activeTask.getActiveTaskLifetimeEnd())))
                || (StudyMetaDataConstants.SDF_DATE
                    .parse(weekEndDate)
                    .after(
                        StudyMetaDataConstants.SDF_DATE.parse(
                            activeTask.getActiveTaskLifetimeEnd())))) {
              activeTaskEndDate = activeTask.getActiveTaskLifetimeEnd();
              skipLoop = true;
            }

            weeklyBean.setStartTime(activeTaskStartDate);
            weeklyBean.setEndTime(activeTaskEndDate);
            runDetailsBean.add(weeklyBean);

            if (skipLoop) {
              break;
            }
          }

          activeTaskStartDate = weekEndDate;
          activeTaskDay = day;
          repeatCount--;
        }
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getActiveTaskFrequencyDetailsForWeekly() :: ERROR", e);
    }
    LOGGER.exit("getActiveTaskFrequencyDetailsForWeekly() :: Ends");
    return runDetailsBean;
  }

  public List<ActivityFrequencyScheduleBean> getActiveTaskFrequencyDetailsForMonthly(
      ActiveTaskDto activeTask, List<ActivityFrequencyScheduleBean> runDetailsBean)
      throws DAOException {
    LOGGER.entry("begin getActiveTaskFrequencyDetailsForMonthly()");
    try {
      if (StringUtils.isNotEmpty(activeTask.getActiveTaskLifetimeStart())
          && StringUtils.isNotEmpty(activeTask.getActiveTaskLifetimeEnd())) {
        Integer repeatCount =
            ((activeTask.getRepeatActiveTask() == null) || (activeTask.getRepeatActiveTask() == 0))
                ? 1
                : activeTask.getRepeatActiveTask();
        String activeTaskStartDate = activeTask.getActiveTaskLifetimeStart();
        while (repeatCount > 0) {
          ActivityFrequencyScheduleBean monthlyBean = new ActivityFrequencyScheduleBean();
          String activeTaskEndDate;
          String monthEndDate;
          boolean flag = false;
          boolean skipLoop = false;

          monthEndDate = StudyMetaDataUtil.addMonthsToDate(activeTaskStartDate, 1);
          if ((StudyMetaDataConstants.SDF_DATE
                  .parse(StudyMetaDataUtil.getCurrentDate())
                  .equals(StudyMetaDataConstants.SDF_DATE.parse(monthEndDate)))
              || (StudyMetaDataConstants.SDF_DATE
                  .parse(StudyMetaDataUtil.getCurrentDate())
                  .before(StudyMetaDataConstants.SDF_DATE.parse(monthEndDate)))) {
            flag = true;
          }

          if (flag) {
            activeTaskEndDate = monthEndDate;
            if ((StudyMetaDataConstants.SDF_DATE
                    .parse(monthEndDate)
                    .equals(
                        StudyMetaDataConstants.SDF_DATE.parse(
                            activeTask.getActiveTaskLifetimeEnd())))
                || (StudyMetaDataConstants.SDF_DATE
                    .parse(monthEndDate)
                    .after(
                        StudyMetaDataConstants.SDF_DATE.parse(
                            activeTask.getActiveTaskLifetimeEnd())))) {
              activeTaskEndDate = activeTask.getActiveTaskLifetimeEnd();
              skipLoop = true;
            }

            monthlyBean.setStartTime(activeTaskStartDate);
            monthlyBean.setEndTime(activeTaskEndDate);
            runDetailsBean.add(monthlyBean);

            if (skipLoop) {
              break;
            }
          }

          activeTaskStartDate = monthEndDate;
          repeatCount--;
        }
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getActiveTaskFrequencyDetailsForMonthly() :: ERROR", e);
    }
    LOGGER.exit("getActiveTaskFrequencyDetailsForMonthly() :: Ends");
    return runDetailsBean;
  }

  @SuppressWarnings("unchecked")
  public List<ActivityFrequencyScheduleBean> getActiveTaskFrequencyDetailsForManuallySchedule(
      ActiveTaskDto activeTask, List<ActivityFrequencyScheduleBean> runDetailsBean, Session session)
      throws DAOException {
    LOGGER.entry("begin getActiveTaskFrequencyDetailsForManuallySchedule()");
    try {
      List<ActiveTaskCustomFrequenciesDto> manuallyScheduleFrequencyList =
          session
              .createQuery(
                  "from ActiveTaskCustomFrequenciesDto ATCFDTO"
                      + " where ATCFDTO.activeTaskId=:activeTaskId"
                      + " ORDER BY frequencyStartDate ASC")
              .setString("activeTaskId", activeTask.getId())
              .list();
      if ((manuallyScheduleFrequencyList != null) && !manuallyScheduleFrequencyList.isEmpty()) {
        for (ActiveTaskCustomFrequenciesDto customFrequencyDto : manuallyScheduleFrequencyList) {
          ActivityFrequencyScheduleBean manuallyScheduleBean = new ActivityFrequencyScheduleBean();
          String startDate =
              customFrequencyDto.getFrequencyStartDate()
                  + " "
                  + customFrequencyDto.getFrequencyStartTime();
          String endDate =
              customFrequencyDto.getFrequencyEndDate()
                  + " "
                  + customFrequencyDto.getFrequencyEndTime();
          manuallyScheduleBean.setStartTime(
              StudyMetaDataUtil.getFormattedDateTimeZone(
                  startDate,
                  StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                  StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          manuallyScheduleBean.setEndTime(
              StudyMetaDataUtil.getFormattedDateTimeZone(
                  endDate,
                  StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                  StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          runDetailsBean.add(manuallyScheduleBean);
        }
      }
    } catch (Exception e) {
      LOGGER.error(
          "ActivityMetaDataDao - getActiveTaskFrequencyDetailsForManuallySchedule() :: ERROR", e);
    }
    LOGGER.exit("getActiveTaskFrequencyDetailsForManuallySchedule() :: Ends");
    return runDetailsBean;
  }

  public ActivityFrequencyBean getFrequencyRunsDetailsForQuestionaires(
      QuestionnairesDto questionaire, ActivityFrequencyBean frequencyDetails, Session session)
      throws DAOException {
    LOGGER.entry("begin getFrequencyRunsDetailsForQuestionaires()");
    List<ActivityFrequencyScheduleBean> runDetailsBean = new ArrayList<>();
    List<ActivityFrequencyAnchorRunsBean> anchorRunDetailsBean = new ArrayList<>();
    try {
      switch (questionaire.getFrequency()) {
        case StudyMetaDataConstants.FREQUENCY_TYPE_DAILY:
          runDetailsBean =
              this.getQuestionnaireFrequencyDetailsForDaily(questionaire, runDetailsBean, session);
          break;
        case StudyMetaDataConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE:
          runDetailsBean =
              this.getQuestionnaireFrequencyDetailsForManuallySchedule(
                  questionaire, runDetailsBean, session);
          /** Phase2a code start * */
          anchorRunDetailsBean =
              this.getQuestionnaireFrequencyAncorDetailsForManuallySchedule(
                  questionaire, anchorRunDetailsBean, session);
          frequencyDetails.setAnchorRuns(anchorRunDetailsBean);
          /** Phase2a code End * */
          break;
        default:
          break;
      }
      frequencyDetails.setRuns(runDetailsBean);
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getFrequencyRunsDetailsForQuestionaires() :: ERROR", e);
    }
    LOGGER.exit("getFrequencyRunsDetailsForQuestionaires() :: Ends");
    return frequencyDetails;
  }

  public List<ActivityFrequencyScheduleBean> getQuestionnaireFrequencyDetailsForOneTime(
      QuestionnairesDto questionaire, List<ActivityFrequencyScheduleBean> runDetailsBean)
      throws DAOException {
    LOGGER.entry("begin getQuestionnaireFrequencyDetailsForOneTime()");
    try {
      if (questionaire != null) {
        ActivityFrequencyScheduleBean oneTimeBean = new ActivityFrequencyScheduleBean();
        oneTimeBean.setStartTime(
            StudyMetaDataUtil.getFormattedDateTimeZone(
                questionaire.getStudyLifetimeStart(),
                StudyMetaDataConstants.SDF_DATE_PATTERN,
                StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
        oneTimeBean.setEndTime(
            StudyMetaDataUtil.getFormattedDateTimeZone(
                questionaire.getStudyLifetimeEnd(),
                StudyMetaDataConstants.SDF_DATE_PATTERN,
                StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
        runDetailsBean.add(oneTimeBean);
      }
    } catch (Exception e) {
      LOGGER.error(
          "ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForOneTime() :: ERROR", e);
    }
    LOGGER.exit("getQuestionnaireFrequencyDetailsForOneTime() :: Ends");
    return runDetailsBean;
  }

  @SuppressWarnings("unchecked")
  public List<ActivityFrequencyScheduleBean> getQuestionnaireFrequencyDetailsForDaily(
      QuestionnairesDto questionaire,
      List<ActivityFrequencyScheduleBean> runDetailsBean,
      Session session)
      throws DAOException {
    LOGGER.entry("begin getQuestionnaireFrequencyDetailsForDaily()");
    List<QuestionnairesFrequenciesDto> dailyFrequencyList = null;
    try {
      if ((questionaire.getScheduleType() != null)
          && !questionaire.getScheduleType().isEmpty()
          && questionaire
              .getScheduleType()
              .equals(StudyMetaDataConstants.SCHEDULETYPE_ANCHORDATE)) {
        dailyFrequencyList =
            session
                .createQuery(
                    "from QuestionnairesFrequenciesDto QFDTO"
                        + " where QFDTO.questionnairesId= :questRespId"
                        + " ORDER BY QFDTO.frequencyTime")
                .setString("questRespId", questionaire.getId())
                .list();
        if ((dailyFrequencyList != null) && !dailyFrequencyList.isEmpty()) {
          for (int i = 0; i < dailyFrequencyList.size(); i++) {
            ActivityFrequencyScheduleBean dailyBean = new ActivityFrequencyScheduleBean();
            String activeTaskStartTime;
            String activeTaskEndTime;
            activeTaskStartTime = dailyFrequencyList.get(i).getFrequencyTime();

            if (i == (dailyFrequencyList.size() - 1)) {
              activeTaskEndTime = StudyMetaDataConstants.DEFAULT_MAX_TIME;
            } else {
              activeTaskEndTime =
                  StudyMetaDataUtil.addSeconds(
                      StudyMetaDataUtil.getCurrentDate()
                          + " "
                          + dailyFrequencyList.get(i + 1).getFrequencyTime(),
                      -1);
              activeTaskEndTime = activeTaskEndTime.substring(11, activeTaskEndTime.length());
            }

            dailyBean.setStartTime(activeTaskStartTime);
            dailyBean.setEndTime(activeTaskEndTime);
            runDetailsBean.add(dailyBean);
          }
        }
      } else {
        if (StringUtils.isNotEmpty(questionaire.getStudyLifetimeStart())
            && StringUtils.isNotEmpty(questionaire.getStudyLifetimeEnd())) {
          dailyFrequencyList =
              session
                  .createQuery(
                      "from QuestionnairesFrequenciesDto QFDTO"
                          + " where QFDTO.questionnairesId= :questRespId"
                          + " ORDER BY QFDTO.frequencyTime")
                  .setString("questRespId", questionaire.getId())
                  .list();
          if ((dailyFrequencyList != null) && !dailyFrequencyList.isEmpty()) {
            for (int i = 0; i < dailyFrequencyList.size(); i++) {
              ActivityFrequencyScheduleBean dailyBean = new ActivityFrequencyScheduleBean();
              String activeTaskStartTime;
              String activeTaskEndTime;
              activeTaskStartTime = dailyFrequencyList.get(i).getFrequencyTime();

              if (i == (dailyFrequencyList.size() - 1)) {
                activeTaskEndTime = StudyMetaDataConstants.DEFAULT_MAX_TIME;
              } else {
                activeTaskEndTime =
                    StudyMetaDataUtil.addSeconds(
                        StudyMetaDataUtil.getCurrentDate()
                            + " "
                            + dailyFrequencyList.get(i + 1).getFrequencyTime(),
                        -1);
                activeTaskEndTime = activeTaskEndTime.substring(11, activeTaskEndTime.length());
              }

              dailyBean.setStartTime(activeTaskStartTime);
              dailyBean.setEndTime(activeTaskEndTime);
              runDetailsBean.add(dailyBean);
            }
          }
        }
      }

    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForDaily() :: ERROR", e);
    }
    LOGGER.exit("getQuestionnaireFrequencyDetailsForDaily() :: Ends");
    return runDetailsBean;
  }

  public List<ActivityFrequencyScheduleBean> getQuestionnaireFrequencyDetailsForWeekly(
      QuestionnairesDto questionaire, List<ActivityFrequencyScheduleBean> runDetailsBean)
      throws DAOException {
    LOGGER.entry("begin getQuestionnaireFrequencyDetailsForWeekly()");
    try {
      if (StringUtils.isNotEmpty(questionaire.getStudyLifetimeStart())
          && StringUtils.isNotEmpty(questionaire.getStudyLifetimeEnd())
          && StringUtils.isNotEmpty(questionaire.getDayOfTheWeek())) {
        Integer repeatCount =
            ((questionaire.getRepeatQuestionnaire() == null)
                    || (questionaire.getRepeatQuestionnaire() == 0))
                ? 1
                : questionaire.getRepeatQuestionnaire();
        String questionaireDay = questionaire.getDayOfTheWeek();
        String questionaireStartDate = questionaire.getStudyLifetimeStart();
        while (repeatCount > 0) {
          ActivityFrequencyScheduleBean weeklyBean = new ActivityFrequencyScheduleBean();
          String questionaireEndDate;
          String day = "";
          String weekEndDate;
          boolean flag = false;
          boolean skipLoop = false;

          if (questionaireDay.equalsIgnoreCase(
              StudyMetaDataUtil.getDayByDate(questionaireStartDate))) {
            day = questionaireDay;
          }

          if (!questionaireDay.equalsIgnoreCase(day)) {
            while (!questionaireDay.equalsIgnoreCase(day)) {
              questionaireStartDate = StudyMetaDataUtil.addDaysToDate(questionaireStartDate, 1);
              day = StudyMetaDataUtil.getDayByDate(questionaireStartDate);
            }
          }

          weekEndDate = StudyMetaDataUtil.addWeeksToDate(questionaireStartDate, 1);
          if ((StudyMetaDataConstants.SDF_DATE
                  .parse(StudyMetaDataUtil.getCurrentDate())
                  .equals(StudyMetaDataConstants.SDF_DATE.parse(weekEndDate)))
              || (StudyMetaDataConstants.SDF_DATE
                  .parse(StudyMetaDataUtil.getCurrentDate())
                  .before(StudyMetaDataConstants.SDF_DATE.parse(weekEndDate)))) {
            flag = true;
          }

          if (flag) {
            questionaireEndDate = weekEndDate;
            if ((StudyMetaDataConstants.SDF_DATE
                    .parse(weekEndDate)
                    .equals(
                        StudyMetaDataConstants.SDF_DATE.parse(questionaire.getStudyLifetimeEnd())))
                || (StudyMetaDataConstants.SDF_DATE
                    .parse(weekEndDate)
                    .after(
                        StudyMetaDataConstants.SDF_DATE.parse(
                            questionaire.getStudyLifetimeEnd())))) {
              questionaireEndDate = questionaire.getStudyLifetimeEnd();
              skipLoop = true;
            }

            weeklyBean.setStartTime(questionaireStartDate);
            weeklyBean.setEndTime(questionaireEndDate);
            runDetailsBean.add(weeklyBean);

            if (skipLoop) {
              break;
            }
          }

          questionaireStartDate = weekEndDate;
          questionaireDay = day;
          repeatCount--;
        }
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForWeekly() :: ERROR", e);
    }
    LOGGER.exit("getQuestionnaireFrequencyDetailsForWeekly() :: Ends");
    return runDetailsBean;
  }

  public List<ActivityFrequencyScheduleBean> getQuestionnaireFrequencyDetailsForMonthly(
      QuestionnairesDto questionaire, List<ActivityFrequencyScheduleBean> runDetailsBean)
      throws DAOException {
    LOGGER.entry("begin getQuestionnaireFrequencyDetailsForMonthly()");
    try {
      if (StringUtils.isNotEmpty(questionaire.getStudyLifetimeStart())
          && StringUtils.isNotEmpty(questionaire.getStudyLifetimeEnd())) {
        Integer repeatCount =
            ((questionaire.getRepeatQuestionnaire() == null)
                    || (questionaire.getRepeatQuestionnaire() == 0))
                ? 1
                : questionaire.getRepeatQuestionnaire();
        String questionaireStartDate = questionaire.getStudyLifetimeStart();
        while (repeatCount > 0) {
          ActivityFrequencyScheduleBean monthlyBean = new ActivityFrequencyScheduleBean();
          String questionaireEndDate;
          String monthEndDate;
          boolean flag = false;
          boolean skipLoop = false;

          monthEndDate = StudyMetaDataUtil.addMonthsToDate(questionaireStartDate, 1);
          if ((StudyMetaDataConstants.SDF_DATE
                  .parse(StudyMetaDataUtil.getCurrentDate())
                  .equals(StudyMetaDataConstants.SDF_DATE.parse(monthEndDate)))
              || (StudyMetaDataConstants.SDF_DATE
                  .parse(StudyMetaDataUtil.getCurrentDate())
                  .before(StudyMetaDataConstants.SDF_DATE.parse(monthEndDate)))) {
            flag = true;
          }

          if (flag) {
            questionaireEndDate = monthEndDate;
            if ((StudyMetaDataConstants.SDF_DATE
                    .parse(monthEndDate)
                    .equals(
                        StudyMetaDataConstants.SDF_DATE.parse(questionaire.getStudyLifetimeEnd())))
                || (StudyMetaDataConstants.SDF_DATE
                    .parse(monthEndDate)
                    .after(
                        StudyMetaDataConstants.SDF_DATE.parse(
                            questionaire.getStudyLifetimeEnd())))) {
              questionaireEndDate = questionaire.getStudyLifetimeEnd();
              skipLoop = true;
            }
            monthlyBean.setStartTime(questionaireStartDate);
            monthlyBean.setEndTime(questionaireEndDate);
            runDetailsBean.add(monthlyBean);

            if (skipLoop) {
              break;
            }
          }

          questionaireStartDate = monthEndDate;
          repeatCount--;
        }
      }
    } catch (Exception e) {
      LOGGER.error(
          "ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForMonthly() :: ERROR", e);
    }
    LOGGER.exit("getQuestionnaireFrequencyDetailsForMonthly() :: Ends");
    return runDetailsBean;
  }

  @SuppressWarnings("unchecked")
  public List<ActivityFrequencyScheduleBean> getQuestionnaireFrequencyDetailsForManuallySchedule(
      QuestionnairesDto questionaire,
      List<ActivityFrequencyScheduleBean> runDetailsBean,
      Session session)
      throws DAOException {
    LOGGER.entry("begin getQuestionnaireFrequencyDetailsForManuallySchedule()");
    try {

      List<QuestionnairesCustomFrequenciesDto> manuallyScheduleFrequencyList =
          session
              .createQuery(
                  "from QuestionnairesCustomFrequenciesDto QCFDTO"
                      + " where QCFDTO.questionnairesId=:questRespId"
                      + " ORDER BY frequencyStartDate ASC")
              .setString("questRespId", questionaire.getId())
              .list();
      if ((manuallyScheduleFrequencyList != null) && !manuallyScheduleFrequencyList.isEmpty()) {
        for (QuestionnairesCustomFrequenciesDto customFrequencyDto :
            manuallyScheduleFrequencyList) {
          ActivityFrequencyScheduleBean manuallyScheduleBean = new ActivityFrequencyScheduleBean();
          manuallyScheduleBean.setEndTime(
              StudyMetaDataUtil.getFormattedDateTimeZone(
                  customFrequencyDto.getFrequencyEndDate()
                      + " "
                      + customFrequencyDto.getFrequencyEndTime(),
                  StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                  StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          manuallyScheduleBean.setStartTime(
              StudyMetaDataUtil.getFormattedDateTimeZone(
                  customFrequencyDto.getFrequencyStartDate()
                      + " "
                      + customFrequencyDto.getFrequencyStartTime(),
                  StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                  StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          runDetailsBean.add(manuallyScheduleBean);
        }
      }
    } catch (Exception e) {
      LOGGER.error(
          "ActivityMetaDataDao - getQuestionnaireFrequencyDetailsForManuallySchedule() :: ERROR",
          e);
    }
    LOGGER.exit("getQuestionnaireFrequencyDetailsForManuallySchedule() :: Ends");
    return runDetailsBean;
  }

  public SortedMap<Integer, QuestionnaireActivityStepsBean> getStepsInfoForQuestionnaires(
      String type,
      List<InstructionsDto> instructionsDtoList,
      List<QuestionsDto> questionsDtoList,
      List<FormMappingDto> formsList,
      Map<String, Integer> sequenceNoMap,
      SortedMap<Integer, QuestionnaireActivityStepsBean> stepsSequenceTreeMap,
      Session session,
      Map<String, QuestionnairesStepsDto> questionnaireStepDetailsMap,
      List<QuestionResponsetypeMasterInfoDto> questionResponseTypeMasterInfoList,
      List<QuestionnairesStepsDto> questionaireStepsList,
      QuestionnairesDto questionnaireDto,
      StudyDto studyDto)
      throws DAOException {
    LOGGER.entry("begin getStepsInfoForQuestionnaires()");
    TreeMap<Integer, QuestionnaireActivityStepsBean> stepsOrderSequenceTreeMap = new TreeMap<>();
    try {
      switch (type) {
        case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION:
          stepsOrderSequenceTreeMap =
              (TreeMap<Integer, QuestionnaireActivityStepsBean>)
                  this.getInstructionDetailsForQuestionnaire(
                      instructionsDtoList,
                      sequenceNoMap,
                      stepsSequenceTreeMap,
                      questionnaireStepDetailsMap);
          break;
        case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION:
          stepsOrderSequenceTreeMap =
              (TreeMap<Integer, QuestionnaireActivityStepsBean>)
                  this.getQuestionDetailsForQuestionnaire(
                      questionsDtoList,
                      sequenceNoMap,
                      stepsSequenceTreeMap,
                      session,
                      questionnaireStepDetailsMap,
                      questionResponseTypeMasterInfoList,
                      questionaireStepsList,
                      questionnaireDto,
                      studyDto);
          break;
        case StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM:
          stepsOrderSequenceTreeMap =
              (TreeMap<Integer, QuestionnaireActivityStepsBean>)
                  this.getFormDetailsForQuestionnaire(
                      formsList,
                      sequenceNoMap,
                      session,
                      stepsSequenceTreeMap,
                      questionnaireStepDetailsMap,
                      questionResponseTypeMasterInfoList,
                      studyDto);
          break;
        default:
          break;
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getStepsInfoForQuestionnaires() :: ERROR", e);
    }
    LOGGER.exit("getStepsInfoForQuestionnaires() :: Ends");
    return stepsOrderSequenceTreeMap;
  }

  public SortedMap<Integer, QuestionnaireActivityStepsBean> getInstructionDetailsForQuestionnaire(
      List<InstructionsDto> instructionsDtoList,
      Map<String, Integer> sequenceNoMap,
      SortedMap<Integer, QuestionnaireActivityStepsBean> stepsSequenceTreeMap,
      Map<String, QuestionnairesStepsDto> questionnaireStepDetailsMap)
      throws DAOException {
    LOGGER.entry("begin getInstructionDetailsForQuestionnaire()");
    try {
      if ((instructionsDtoList != null) && !instructionsDtoList.isEmpty()) {
        for (InstructionsDto instructionsDto : instructionsDtoList) {
          QuestionnairesStepsDto instructionStepDetails =
              questionnaireStepDetailsMap.get(
                  (instructionsDto.getId()
                          + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION)
                      .toString());
          QuestionnaireActivityStepsBean instructionBean = new QuestionnaireActivityStepsBean();

          instructionBean.setType(
              StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION.toLowerCase());
          instructionBean.setResultType("");
          instructionBean.setKey(
              StringUtils.isEmpty(instructionStepDetails.getStepShortTitle())
                  ? ""
                  : instructionStepDetails.getStepShortTitle());
          instructionBean.setTitle(
              StringUtils.isEmpty(instructionsDto.getInstructionTitle())
                  ? ""
                  : instructionsDto.getInstructionTitle());
          instructionBean.setText(
              StringUtils.isEmpty(instructionsDto.getInstructionText())
                  ? ""
                  : instructionsDto.getInstructionText());
          instructionBean.setSkippable(
              (StringUtils.isEmpty(instructionStepDetails.getSkiappable())
                      || instructionStepDetails
                          .getSkiappable()
                          .equalsIgnoreCase(StudyMetaDataConstants.NO))
                  ? false
                  : true);
          instructionBean.setGroupName("");
          instructionBean.setRepeatable(false);
          instructionBean.setRepeatableText(
              instructionStepDetails.getRepeatableText() == null
                  ? ""
                  : instructionStepDetails.getRepeatableText());

          List<DestinationBean> destinations = new ArrayList<>();
          DestinationBean dest = new DestinationBean();
          dest.setCondition("");
          dest.setDestination(
              ((instructionStepDetails.getDestinationStepType() == null)
                      || instructionStepDetails.getDestinationStepType().isEmpty())
                  ? ""
                  : instructionStepDetails.getDestinationStepType());
          destinations.add(dest);
          instructionBean.setDestinations(destinations);

          stepsSequenceTreeMap.put(
              sequenceNoMap.get(
                  (instructionsDto.getId()
                          + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_INSTRUCTION)
                      .toString()),
              instructionBean);
        }
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getInstructionDetailsForQuestionnaire() :: ERROR", e);
    }
    LOGGER.exit("getInstructionDetailsForQuestionnaire() :: Ends");
    return stepsSequenceTreeMap;
  }

  @SuppressWarnings("unchecked")
  public SortedMap<Integer, QuestionnaireActivityStepsBean> getQuestionDetailsForQuestionnaire(
      List<QuestionsDto> questionsDtoList,
      Map<String, Integer> sequenceNoMap,
      SortedMap<Integer, QuestionnaireActivityStepsBean> stepsSequenceTreeMap,
      Session session,
      Map<String, QuestionnairesStepsDto> questionnaireStepDetailsMap,
      List<QuestionResponsetypeMasterInfoDto> questionResponseTypeMasterInfoList,
      List<QuestionnairesStepsDto> questionaireStepsList,
      QuestionnairesDto questionnaireDto,
      StudyDto studyDto)
      throws DAOException {
    LOGGER.entry("begin getQuestionDetailsForQuestionnaire()");
    List<QuestionResponseSubTypeDto> destinationConditionList = null;
    Transaction transaction = null;
    try {
      if ((questionsDtoList != null) && !questionsDtoList.isEmpty()) {
        for (QuestionsDto questionsDto : questionsDtoList) {
          QuestionnairesStepsDto questionStepDetails =
              questionnaireStepDetailsMap.get(
                  (questionsDto.getId() + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION)
                      .toString());
          QuestionnaireActivityStepsBean questionBean = new QuestionnaireActivityStepsBean();

          questionBean.setType(
              StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION.toLowerCase());
          if (questionsDto.getResponseType() != null) {
            for (QuestionResponsetypeMasterInfoDto masterInfo :
                questionResponseTypeMasterInfoList) {
              if (masterInfo.getId().equals(questionsDto.getResponseType())) {
                questionBean.setResultType(masterInfo.getResponseTypeCode());
                questionBean.setFormat(
                    this.getQuestionaireQuestionFormatByType(
                        questionsDto, masterInfo.getResponseTypeCode(), session, studyDto));
                break;
              }
            }
          } else {
            questionBean.setResultType("");
          }
          questionBean.setText(
              StringUtils.isEmpty(questionsDto.getDescription())
                  ? ""
                  : questionsDto.getDescription());
          questionBean.setKey(
              StringUtils.isEmpty(questionStepDetails.getStepShortTitle())
                  ? ""
                  : questionStepDetails.getStepShortTitle());
          questionBean.setTitle(
              StringUtils.isEmpty(questionsDto.getQuestion()) ? "" : questionsDto.getQuestion());
          questionBean.setSkippable(
              (StringUtils.isEmpty(questionStepDetails.getSkiappable())
                      || questionStepDetails
                          .getSkiappable()
                          .equalsIgnoreCase(StudyMetaDataConstants.NO))
                  ? false
                  : true);
          questionBean.setGroupName("");
          questionBean.setRepeatable(false);
          questionBean.setRepeatableText(
              questionStepDetails.getRepeatableText() == null
                  ? ""
                  : questionStepDetails.getRepeatableText());

          List<DestinationBean> destinationsList = new ArrayList<>();

          /**
           * Choice based branching allowed only for textchoice, textscale, imagechoice, boolean
           * response types
           */
          if (!questionsDto.getResponseType().equals(String.valueOf(4))) {
            destinationConditionList =
                session
                    .createQuery(
                        "from QuestionResponseSubTypeDto QRSTDTO"
                            + " where QRSTDTO.responseTypeId= :responseTypeId")
                    .setString(StudyMetaDataEnum.QF_RESPONSE_TYPE_ID.value(), questionsDto.getId())
                    .list();
            if ((destinationConditionList != null) && !destinationConditionList.isEmpty()) {
              for (QuestionResponseSubTypeDto destinationDto : destinationConditionList) {
                DestinationBean destination = new DestinationBean();
                if (questionBean
                    .getResultType()
                    .equalsIgnoreCase(StudyMetaDataConstants.QUESTION_BOOLEAN)) {
                  destination.setCondition(
                      StringUtils.isEmpty(destinationDto.getValue())
                          ? ""
                          : destinationDto.getValue().toLowerCase());
                } else {
                  destination.setCondition(
                      StringUtils.isEmpty(destinationDto.getValue())
                          ? ""
                          : destinationDto.getValue());
                }

                if (questionnaireDto.getBranching()) {
                  if ((destinationDto.getDestinationStepId() != null)
                      && (!destinationDto.getDestinationStepId().equals(String.valueOf(0)))) {
                    destination =
                        this.getDestinationStepTypeForResponseSubType(
                            destination, destinationDto, questionaireStepsList);
                  } else if ((destinationDto.getDestinationStepId() != null)
                      && destinationDto.getDestinationStepId().equals(String.valueOf(0))) {
                    destination.setDestination("");
                  } else {
                    destination.setDestination(
                        ((questionStepDetails.getDestinationStepType() == null)
                                || questionStepDetails.getDestinationStepType().isEmpty())
                            ? ""
                            : questionStepDetails.getDestinationStepType());
                  }
                } else {
                  destination.setDestination(
                      ((questionStepDetails.getDestinationStepType() == null)
                              || questionStepDetails.getDestinationStepType().isEmpty())
                          ? ""
                          : questionStepDetails.getDestinationStepType());
                }
                destinationsList.add(destination);
              }
            }
          }

          if (Arrays.asList(StudyMetaDataConstants.CB_RESPONSE_TYPE.split(","))
                  .contains(questionBean.getResultType())
              && questionnaireDto.getBranching()) {
            QuestionReponseTypeDto reponseType =
                (QuestionReponseTypeDto)
                    session
                        .createQuery(
                            "from QuestionReponseTypeDto QRTDTO"
                                + " where QRTDTO.questionsResponseTypeId=:questRespType"
                                + " ORDER BY QRTDTO.responseTypeId DESC")
                        .setString("questRespType", questionsDto.getId())
                        .setMaxResults(1)
                        .uniqueResult();
            if ((reponseType != null)
                && StringUtils.isNotEmpty(reponseType.getFormulaBasedLogic())
                && reponseType
                    .getFormulaBasedLogic()
                    .equalsIgnoreCase(StudyMetaDataConstants.YES)) {
              boolean isValueOfXSaved = false;
              if ((destinationConditionList != null)
                  && !destinationConditionList.isEmpty()
                  && (destinationConditionList.size() == 2)) {
                if (StringUtils.isNotEmpty(destinationConditionList.get(0).getValueOfX())
                    && StringUtils.isNotEmpty(destinationConditionList.get(1).getValueOfX())
                    && StringUtils.isNotEmpty(destinationConditionList.get(0).getOperator())
                    && StringUtils.isNotEmpty(destinationConditionList.get(1).getOperator())) {
                  isValueOfXSaved = true;
                  for (int i = 0; i < destinationConditionList.size(); i++) {
                    destinationsList
                        .get(i)
                        .setCondition(
                            StringUtils.isEmpty(destinationConditionList.get(i).getValueOfX())
                                ? ""
                                : destinationConditionList.get(i).getValueOfX());
                    destinationsList
                        .get(i)
                        .setOperator(
                            StringUtils.isEmpty(destinationConditionList.get(i).getOperator())
                                ? ""
                                : destinationConditionList.get(i).getOperator());
                  }
                }
              }

              if (!isValueOfXSaved) {
                destinationsList =
                    this.getConditionalBranchingDestinations(
                        reponseType, destinationsList, questionBean);

                transaction = session.beginTransaction();
                for (int i = 0; i < destinationsList.size(); i++) {
                  QuestionResponseSubTypeDto destinationDto = destinationConditionList.get(i);
                  destinationDto.setValueOfX(destinationsList.get(i).getCondition());
                  destinationDto.setOperator(destinationsList.get(i).getOperator());
                  session.save(destinationDto);
                }
                transaction.commit();
              }
            }
          }

          DestinationBean destination = new DestinationBean();
          destination.setCondition("");
          destination.setDestination(
              ((questionStepDetails.getDestinationStepType() == null)
                      || questionStepDetails.getDestinationStepType().isEmpty())
                  ? ""
                  : questionStepDetails.getDestinationStepType());
          destinationsList.add(destination);

          /** other type add destination if there start */
          QuestionReponseTypeDto otherReponseSubType =
              (QuestionReponseTypeDto)
                  session
                      .createQuery(
                          "from QuestionReponseTypeDto QRTDTO"
                              + " where QRTDTO.questionsResponseTypeId=:questRespType"
                              + " ORDER BY QRTDTO.responseTypeId DESC")
                      .setString("questRespType", questionsDto.getId())
                      .setMaxResults(1)
                      .uniqueResult();

          if ((otherReponseSubType != null)
              && (otherReponseSubType.getOtherType() != null)
              && StringUtils.isNotEmpty(otherReponseSubType.getOtherType())
              && otherReponseSubType.getOtherType().equals("on")) {
            DestinationBean otherDestination = new DestinationBean();
            otherDestination.setCondition(
                StringUtils.isEmpty(otherReponseSubType.getOtherValue())
                    ? ""
                    : otherReponseSubType.getOtherValue());

            if (questionnaireDto.getBranching()) {
              if ((otherReponseSubType.getOtherDestinationStepId() != null)
                  && (!otherReponseSubType.getOtherDestinationStepId().equals(String.valueOf(0)))) {
                for (QuestionnairesStepsDto stepsDto : questionaireStepsList) {
                  if (otherReponseSubType
                      .getOtherDestinationStepId()
                      .equals(stepsDto.getStepId())) {
                    otherDestination.setDestination(
                        StringUtils.isEmpty(stepsDto.getStepShortTitle())
                            ? ""
                            : stepsDto.getStepShortTitle());
                    break;
                  }
                }
              } else if ((otherReponseSubType.getOtherDestinationStepId() != null)
                  && otherReponseSubType.getOtherDestinationStepId().equals(String.valueOf(0))) {
                otherDestination.setDestination("");
              } else {
                otherDestination.setDestination(
                    ((questionStepDetails.getDestinationStepType() == null)
                            || questionStepDetails.getDestinationStepType().isEmpty())
                        ? ""
                        : questionStepDetails.getDestinationStepType());
              }
            } else {
              otherDestination.setDestination(
                  ((questionStepDetails.getDestinationStepType() == null)
                          || questionStepDetails.getDestinationStepType().isEmpty())
                      ? ""
                      : questionStepDetails.getDestinationStepType());
            }
            destinationsList.add(otherDestination);
          }
          /** other type add destination if there end */
          questionBean.setDestinations(destinationsList);

          questionBean.setHealthDataKey("");
          if (StringUtils.isNotEmpty(questionsDto.getAllowHealthKit())
              && StudyMetaDataConstants.YES.equalsIgnoreCase(questionsDto.getAllowHealthKit())
              && StringUtils.isNotEmpty(questionsDto.getHealthkitDatatype())) {
            questionBean.setHealthDataKey(questionsDto.getHealthkitDatatype().trim());
          }
          stepsSequenceTreeMap.put(
              sequenceNoMap.get(
                  (questionsDto.getId() + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION)
                      .toString()),
              questionBean);
        }
      }
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      LOGGER.error("ActivityMetaDataDao - getQuestionDetailsForQuestionnaire() :: ERROR", e);
    }
    LOGGER.exit("getQuestionDetailsForQuestionnaire() :: Ends");
    return stepsSequenceTreeMap;
  }

  @SuppressWarnings("unchecked")
  public SortedMap<Integer, QuestionnaireActivityStepsBean> getFormDetailsForQuestionnaire(
      List<FormMappingDto> formsList,
      Map<String, Integer> sequenceNoMap,
      Session session,
      SortedMap<Integer, QuestionnaireActivityStepsBean> stepsSequenceTreeMap,
      Map<String, QuestionnairesStepsDto> questionnaireStepDetailsMap,
      List<QuestionResponsetypeMasterInfoDto> questionResponseTypeMasterInfoList,
      StudyDto studyDto)
      throws DAOException {
    LOGGER.entry("begin getFormDetailsForQuestionnaire()");
    try {
      if ((formsList != null) && !formsList.isEmpty()) {
        List<String> formQuestionIdsList = new ArrayList<>();
        TreeMap<Integer, String> formQuestionMap = new TreeMap<>();
        for (FormMappingDto formDto : formsList) {
          formQuestionIdsList.add(formDto.getQuestionId());
          formQuestionMap.put(formDto.getSequenceNo(), formDto.getQuestionId());
        }

        if (!formQuestionIdsList.isEmpty()) {
          QuestionnairesStepsDto formStepDetails =
              questionnaireStepDetailsMap.get(
                  (formsList.get(0).getFormId()
                          + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM)
                      .toString());
          QuestionnaireActivityStepsBean formBean = new QuestionnaireActivityStepsBean();
          List<QuestionnaireStepsBean> formSteps = new ArrayList<>();
          HashMap<String, QuestionnaireStepsBean> formStepsMap = new HashMap<>();

          formBean.setType(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM.toLowerCase());
          formBean.setResultType(StudyMetaDataConstants.RESULT_TYPE_GROUPED);
          formBean.setKey(
              StringUtils.isEmpty(formStepDetails.getStepShortTitle())
                  ? ""
                  : formStepDetails.getStepShortTitle());
          formBean.setTitle("");
          formBean.setText("");
          formBean.setSkippable(
              (StringUtils.isEmpty(formStepDetails.getSkiappable())
                      || formStepDetails
                          .getSkiappable()
                          .equalsIgnoreCase(StudyMetaDataConstants.NO))
                  ? false
                  : true);
          formBean.setGroupName("");
          formBean.setRepeatable(
              ((formStepDetails.getRepeatable() == null)
                      || StudyMetaDataConstants.NO.equalsIgnoreCase(
                          formStepDetails.getRepeatable()))
                  ? false
                  : true);
          formBean.setRepeatableText(
              formStepDetails.getRepeatableText() == null
                  ? ""
                  : formStepDetails.getRepeatableText());

          List<DestinationBean> destinations = new ArrayList<>();
          DestinationBean dest = new DestinationBean();
          dest.setCondition("");
          dest.setDestination(
              ((formStepDetails.getDestinationStepType() == null)
                      || formStepDetails.getDestinationStepType().isEmpty())
                  ? ""
                  : formStepDetails.getDestinationStepType());
          destinations.add(dest);
          formBean.setDestinations(destinations);

          List<QuestionsDto> formQuestionsList;
          formQuestionsList =
              session
                  .createQuery("from QuestionsDto QDTO where QDTO.id in (:formQuestionIdsList)")
                  .setParameterList("formQuestionIdsList", formQuestionIdsList)
                  .list();
          if ((formQuestionsList != null) && !formQuestionsList.isEmpty()) {
            for (QuestionsDto formQuestionDto : formQuestionsList) {
              QuestionnaireStepsBean formQuestionBean = new QuestionnaireStepsBean();
              formQuestionBean.setType(
                  StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION.toLowerCase());
              if (formQuestionDto.getResponseType() != null) {
                for (QuestionResponsetypeMasterInfoDto masterInfo :
                    questionResponseTypeMasterInfoList) {
                  if (masterInfo.getId().equals(formQuestionDto.getResponseType())) {
                    formQuestionBean.setResultType(masterInfo.getResponseTypeCode());
                    formQuestionBean.setFormat(
                        this.getQuestionaireQuestionFormatByType(
                            formQuestionDto, masterInfo.getResponseTypeCode(), session, studyDto));
                    break;
                  }
                }
              } else {
                formQuestionBean.setResultType("");
              }
              formQuestionBean.setKey(
                  StringUtils.isEmpty(formQuestionDto.getShortTitle())
                      ? ""
                      : formQuestionDto.getShortTitle());
              formQuestionBean.setTitle(
                  StringUtils.isEmpty(formQuestionDto.getQuestion())
                      ? ""
                      : formQuestionDto.getQuestion());
              formQuestionBean.setSkippable(
                  (StringUtils.isEmpty(formQuestionDto.getSkippable())
                          || formQuestionDto
                              .getSkippable()
                              .equalsIgnoreCase(StudyMetaDataConstants.NO))
                      ? false
                      : true);
              formQuestionBean.setGroupName("");
              formQuestionBean.setRepeatable(false);
              formQuestionBean.setRepeatableText("");
              formQuestionBean.setText(
                  StringUtils.isEmpty(formQuestionDto.getDescription())
                      ? ""
                      : formQuestionDto.getDescription());
              formQuestionBean.setHealthDataKey("");

              if (StringUtils.isNotEmpty(formQuestionDto.getAllowHealthKit())
                  && StudyMetaDataConstants.YES.equalsIgnoreCase(
                      formQuestionDto.getAllowHealthKit())
                  && StringUtils.isNotEmpty(formQuestionDto.getHealthkitDatatype())) {
                formQuestionBean.setHealthDataKey(formQuestionDto.getHealthkitDatatype().trim());
              }

              formStepsMap.put(formQuestionDto.getId(), formQuestionBean);
            }
          }

          for (Integer key : formQuestionMap.keySet()) {
            formSteps.add(formStepsMap.get(formQuestionMap.get(key)));
          }
          formBean.setSteps(formSteps);

          stepsSequenceTreeMap.put(
              sequenceNoMap.get(
                  (formsList.get(0).getFormId()
                          + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM)
                      .toString()),
              formBean);
        }
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getFormDetailsForQuestionnaire() :: ERROR", e);
    }
    LOGGER.exit("getFormDetailsForQuestionnaire() :: Ends");
    return stepsSequenceTreeMap;
  }

  public Object fetalKickCounterDetails(
      ActiveTaskAttrtibutesValuesDto attributeValues,
      ActiveTaskMasterAttributeDto masterAttributeValue,
      FetalKickCounterFormatBean fetalKickCounterFormat)
      throws DAOException {
    LOGGER.entry("begin fetalKickCounterDetails()");
    try {
      if (masterAttributeValue.getOrderByTaskType().equals(1)) {
        if (StringUtils.isNotEmpty(attributeValues.getAttributeVal())) {
          if (attributeValues
              .getAttributeVal()
              .equals(StudyMetaDataConstants.FETAL_MAX_DURATION_WCP)) {
            attributeValues.setAttributeVal(StudyMetaDataConstants.FETAL_MAX_DURATION);
          } else {
            String[] durationArray = attributeValues.getAttributeVal().split(":");
            fetalKickCounterFormat.setDuration(
                (Integer.parseInt(durationArray[0]) * 3600)
                    + (Integer.parseInt(durationArray[1]) * 60));
          }
        } else {
          attributeValues.setAttributeVal(StudyMetaDataConstants.FETAL_MAX_DURATION);
        }
      } else {
        fetalKickCounterFormat.setKickCount(
            (StringUtils.isEmpty(attributeValues.getAttributeVal())
                    || "0".equals(attributeValues.getAttributeVal()))
                ? StudyMetaDataConstants.MAX_KICK_COUNT
                : Integer.parseInt(attributeValues.getAttributeVal()));
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - fetalKickCounterDetails() :: ERROR", e);
    }
    LOGGER.exit("fetalKickCounterDetails() :: Ends");
    return fetalKickCounterFormat;
  }

  public Object spatialSpanMemoryDetails(
      ActiveTaskAttrtibutesValuesDto attributeValues,
      ActiveTaskMasterAttributeDto masterAttributeValue,
      SpatialSpanMemoryFormatBean spatialSpanMemoryFormat)
      throws DAOException {
    LOGGER.entry("begin spatialSpanMemoryDetails()");
    try {
      switch (masterAttributeValue.getAttributeName().trim()) {
        case StudyMetaDataConstants.SSM_INITIAL:
          spatialSpanMemoryFormat.setInitialSpan(
              StringUtils.isEmpty(attributeValues.getAttributeVal())
                  ? 0
                  : Integer.parseInt(attributeValues.getAttributeVal()));
          break;
        case StudyMetaDataConstants.SSM_MINIMUM:
          spatialSpanMemoryFormat.setMinimumSpan(
              StringUtils.isEmpty(attributeValues.getAttributeVal())
                  ? 0
                  : Integer.parseInt(attributeValues.getAttributeVal()));
          break;
        case StudyMetaDataConstants.SSM_MAXIMUM:
          spatialSpanMemoryFormat.setMaximumSpan(
              StringUtils.isEmpty(attributeValues.getAttributeVal())
                  ? 0
                  : Integer.parseInt(attributeValues.getAttributeVal()));
          break;
        case StudyMetaDataConstants.SSM_PLAY_SPEED:
          spatialSpanMemoryFormat.setPlaySpeed(
              StringUtils.isEmpty(attributeValues.getAttributeVal())
                  ? 0f
                  : Float.parseFloat(attributeValues.getAttributeVal()));
          break;
        case StudyMetaDataConstants.SSM_MAX_TEST:
          spatialSpanMemoryFormat.setMaximumTests(
              StringUtils.isEmpty(attributeValues.getAttributeVal())
                  ? 0
                  : Integer.parseInt(attributeValues.getAttributeVal()));
          break;
        case StudyMetaDataConstants.SSM_MAX_CONSECUTIVE_FAILURES:
          spatialSpanMemoryFormat.setMaximumConsecutiveFailures(
              StringUtils.isEmpty(attributeValues.getAttributeVal())
                  ? 0
                  : Integer.parseInt(attributeValues.getAttributeVal()));
          break;
        case StudyMetaDataConstants.SSM_REQUIRE_REVERSAL:
          spatialSpanMemoryFormat.setRequireReversal(
              StringUtils.isNotEmpty(attributeValues.getAttributeVal())
                      && attributeValues
                          .getAttributeVal()
                          .equalsIgnoreCase(StudyMetaDataConstants.STUDY_SEQUENCE_Y)
                  ? true
                  : false);
          break;
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - spatialSpanMemoryDetails() :: ERROR", e);
    }
    LOGGER.exit("spatialSpanMemoryDetails() :: Ends");
    return spatialSpanMemoryFormat;
  }

  public String[] activeTaskOptions() {
    LOGGER.entry("begin activeTaskOptions()");
    String[] activeTaskOptionsArray = new String[8];
    try {
      activeTaskOptionsArray[0] = "excludeInstructions";
      activeTaskOptionsArray[1] = "excludeConclusion";
      activeTaskOptionsArray[2] = "excludeAccelerometer";
      activeTaskOptionsArray[3] = "excludeDeviceMotion";
      activeTaskOptionsArray[4] = "excludePedometer";
      activeTaskOptionsArray[5] = "excludeLocation";
      activeTaskOptionsArray[6] = "excludeHeartRate";
      activeTaskOptionsArray[7] = "excludeAudio";
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - activeTaskOptions() :: ERROR", e);
    }
    LOGGER.exit("activeTaskOptions() :: Ends");
    return activeTaskOptionsArray;
  }

  public Map<String, Object> getQuestionaireQuestionFormatByType(
      QuestionsDto questionDto, String questionResultType, Session session, StudyDto studyDto)
      throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - getQuestionaireQuestionFormatByType() :: Starts");
    Map<String, Object> questionFormat = new LinkedHashMap<>();
    QuestionReponseTypeDto reponseType = null;
    try {
      if (StringUtils.isNotEmpty(questionResultType)) {
        reponseType =
            (QuestionReponseTypeDto)
                session
                    .createQuery(
                        "from QuestionReponseTypeDto QRTDTO"
                            + " where QRTDTO.questionsResponseTypeId=:questionsResponseTypeId"
                            + " ORDER BY QRTDTO.responseTypeId DESC")
                    .setString("questionsResponseTypeId", questionDto.getId())
                    .setMaxResults(1)
                    .uniqueResult();
        switch (questionResultType) {
          case StudyMetaDataConstants.QUESTION_SCALE:
            questionFormat = this.formatQuestionScaleDetails(reponseType, studyDto);
            break;
          case StudyMetaDataConstants.QUESTION_CONTINUOUS_SCALE:
            questionFormat = this.formatQuestionContinuousScaleDetails(reponseType, studyDto);
            break;
          case StudyMetaDataConstants.QUESTION_TEXT_SCALE:
            questionFormat = this.formatQuestionTextScaleDetails(questionDto, reponseType, session);
            break;
          case StudyMetaDataConstants.QUESTION_VALUE_PICKER:
            questionFormat = this.formatQuestionValuePickerDetails(questionDto, session);
            break;
          case StudyMetaDataConstants.QUESTION_IMAGE_CHOICE:
            questionFormat = this.formatQuestionImageChoiceDetails(questionDto, session, studyDto);
            break;
          case StudyMetaDataConstants.QUESTION_TEXT_CHOICE:
            questionFormat =
                this.formatQuestionTextChoiceDetails(questionDto, reponseType, session);
            break;
          case StudyMetaDataConstants.QUESTION_NUMERIC:
            questionFormat = this.formatQuestionNumericDetails(reponseType);
            break;
          case StudyMetaDataConstants.QUESTION_DATE:
            questionFormat = this.formatQuestionDateDetails(reponseType);
            break;
          case StudyMetaDataConstants.QUESTION_TEXT:
            questionFormat = this.formatQuestionTextDetails(reponseType);
            break;
          case StudyMetaDataConstants.QUESTION_EMAIL:
            questionFormat.put(
                "placeholder",
                ((reponseType == null) || StringUtils.isEmpty(reponseType.getPlaceholder()))
                    ? ""
                    : reponseType.getPlaceholder());
            break;
          case StudyMetaDataConstants.QUESTION_TIME_INTERVAL:
            questionFormat.put(
                "default",
                ((reponseType == null) || StringUtils.isEmpty(reponseType.getDefalutTime()))
                    ? 0
                    : this.getTimeInSeconds(reponseType.getDefalutTime()));
            questionFormat.put(
                "step",
                ((reponseType == null) || (reponseType.getStep() == null))
                    ? 1
                    : this.getTimeIntervalStep(reponseType.getStep()));
            break;
          case StudyMetaDataConstants.QUESTION_HEIGHT:
            questionFormat.put(
                "measurementSystem",
                ((reponseType == null) || (reponseType.getMeasurementSystem() == null))
                    ? ""
                    : reponseType.getMeasurementSystem());
            questionFormat.put(
                "placeholder",
                ((reponseType == null) || StringUtils.isEmpty(reponseType.getPlaceholder()))
                    ? ""
                    : reponseType.getPlaceholder());
            break;
          case StudyMetaDataConstants.QUESTION_LOCATION:
            questionFormat.put(
                "useCurrentLocation",
                ((reponseType == null)
                        || ((reponseType.getUseCurrentLocation() == null)
                            || !reponseType.getUseCurrentLocation()))
                    ? false
                    : true);
            break;
          default:
            break;
        }
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getQuestionaireQuestionFormatByType() :: ERROR", e);
    }
    LOGGER.exit("getQuestionaireQuestionFormatByType() :: Ends");
    return questionFormat;
  }

  public Map<String, Object> formatQuestionScaleDetails(
      QuestionReponseTypeDto reponseType, StudyDto studyDto) throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionScaleDetails() :: Starts");
    Map<String, Object> questionFormat = new LinkedHashMap<>();
    try {
      questionFormat.put(
          "maxValue",
          ((reponseType == null) || StringUtils.isEmpty(reponseType.getMaxValue()))
              ? 10000
              : Integer.parseInt(reponseType.getMaxValue()));
      questionFormat.put(
          "minValue",
          ((reponseType == null) || StringUtils.isEmpty(reponseType.getMinValue()))
              ? -10000
              : Integer.parseInt(reponseType.getMinValue()));
      questionFormat.put(
          "step",
          ((reponseType == null) || (reponseType.getStep() == null))
              ? 1
              : this.getScaleStepSize(
                  reponseType.getStep(),
                  (Integer) questionFormat.get("maxValue"),
                  (Integer) questionFormat.get("minValue")));
      questionFormat.put(
          "default",
          ((reponseType == null) || (reponseType.getDefaultValue() == null))
              ? (Integer) questionFormat.get("minValue")
              : this.getScaleDefaultValue(
                  reponseType.getStep(),
                  (Integer) questionFormat.get("maxValue"),
                  (Integer) questionFormat.get("minValue"),
                  Integer.parseInt(reponseType.getDefaultValue())));
      questionFormat.put(
          "vertical",
          ((reponseType == null)
                  || (reponseType.getVertical() == null)
                  || !reponseType.getVertical())
              ? false
              : true);
      questionFormat.put(
          "maxDesc",
          ((reponseType == null) || StringUtils.isEmpty(reponseType.getMaxDescription()))
              ? ""
              : reponseType.getMaxDescription());
      questionFormat.put(
          "minDesc",
          ((reponseType == null) || StringUtils.isEmpty(reponseType.getMinDescription()))
              ? ""
              : reponseType.getMinDescription());
      questionFormat.put(
          "maxImage",
          ((reponseType == null) || StringUtils.isEmpty(reponseType.getMaxImage()))
              ? ""
              : this.getBase64Image(
                  StudyMetaDataUtil.getSignedUrl(
                      propMap.get("cloud.bucket.name"),
                      StudyMetaDataConstants.STUDIES
                          + "/"
                          + studyDto.getCustomStudyId()
                          + "/"
                          + propMap.get(StudyMetaDataConstants.FDA_SMD_QUESTIONNAIRE_IMAGE).trim()
                          + reponseType.getMaxImage(),
                      12)));
      questionFormat.put(
          "minImage",
          ((reponseType == null) || StringUtils.isEmpty(reponseType.getMinImage()))
              ? ""
              : this.getBase64Image(
                  StudyMetaDataUtil.getSignedUrl(
                      propMap.get("cloud.bucket.name"),
                      StudyMetaDataConstants.STUDIES
                          + "/"
                          + studyDto.getId()
                          + "/"
                          + propMap.get(StudyMetaDataConstants.FDA_SMD_QUESTIONNAIRE_IMAGE).trim()
                          + reponseType.getMinImage(),
                      12)));
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - formatQuestionScaleDetails() :: ERROR", e);
    }
    LOGGER.exit("formatQuestionScaleDetails() :: Ends");
    return questionFormat;
  }

  public Map<String, Object> formatQuestionContinuousScaleDetails(
      QuestionReponseTypeDto reponseType, StudyDto studyDto) throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionContinuousScaleDetails() :: Starts");
    Map<String, Object> questionFormat = new LinkedHashMap<>();
    try {
      questionFormat.put(
          "maxValue",
          ((reponseType == null) || StringUtils.isEmpty(reponseType.getMaxValue()))
              ? 10000
              : Double.parseDouble(reponseType.getMaxValue()));
      questionFormat.put(
          "minValue",
          ((reponseType == null) || StringUtils.isEmpty(reponseType.getMinValue()))
              ? -10000
              : Double.parseDouble(reponseType.getMinValue()));
      questionFormat.put(
          "default",
          ((reponseType == null) || (reponseType.getDefaultValue() == null))
              ? (Double) questionFormat.get("minValue")
              : Double.parseDouble(reponseType.getDefaultValue()));
      questionFormat.put(
          "maxFractionDigits",
          ((reponseType == null) || (reponseType.getMaxFractionDigits() == null))
              ? 0
              : reponseType.getMaxFractionDigits());
      questionFormat.put(
          "vertical",
          ((reponseType == null)
                  || (reponseType.getVertical() == null)
                  || !reponseType.getVertical())
              ? false
              : true);
      questionFormat.put(
          "maxDesc",
          ((reponseType == null) || StringUtils.isEmpty(reponseType.getMaxDescription()))
              ? ""
              : reponseType.getMaxDescription());
      questionFormat.put(
          "minDesc",
          ((reponseType == null) || StringUtils.isEmpty(reponseType.getMinDescription()))
              ? ""
              : reponseType.getMinDescription());
      questionFormat.put(
          "maxImage",
          ((reponseType == null) || StringUtils.isEmpty(reponseType.getMaxImage()))
              ? ""
              : this.getBase64Image(
                  StudyMetaDataUtil.getSignedUrl(
                      propMap.get("cloud.bucket.name"),
                      StudyMetaDataConstants.STUDIES
                          + "/"
                          + studyDto.getCustomStudyId()
                          + "/"
                          + propMap.get(StudyMetaDataConstants.FDA_SMD_QUESTIONNAIRE_IMAGE).trim()
                          + reponseType.getMaxImage(),
                      12)));
      questionFormat.put(
          "minImage",
          ((reponseType == null) || StringUtils.isEmpty(reponseType.getMinImage()))
              ? ""
              : this.getBase64Image(
                  StudyMetaDataUtil.getSignedUrl(
                      propMap.get("cloud.bucket.name"),
                      StudyMetaDataConstants.STUDIES
                          + "/"
                          + studyDto.getId()
                          + "/"
                          + propMap.get(StudyMetaDataConstants.FDA_SMD_QUESTIONNAIRE_IMAGE).trim()
                          + reponseType.getMinImage(),
                      12)));
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - formatQuestionContinuousScaleDetails() :: ERROR", e);
    }
    LOGGER.exit("formatQuestionContinuousScaleDetails() :: Ends");
    return questionFormat;
  }

  @SuppressWarnings("unchecked")
  public Map<String, Object> formatQuestionTextScaleDetails(
      QuestionsDto questionDto, QuestionReponseTypeDto reponseType, Session session)
      throws DAOException {
    LOGGER.entry("begin formatQuestionTextScaleDetails()");
    Map<String, Object> questionFormat = new LinkedHashMap<>();
    List<QuestionResponseSubTypeDto> responseSubTypeList = null;
    List<LinkedHashMap<String, Object>> textChoicesList = new ArrayList<>();
    try {
      responseSubTypeList =
          session
              .createQuery(
                  "from QuestionResponseSubTypeDto QRSTDTO"
                      + " where QRSTDTO.responseTypeId= :respType")
              .setString("respType", questionDto.getId())
              .list();
      if ((responseSubTypeList != null) && !responseSubTypeList.isEmpty()) {
        for (QuestionResponseSubTypeDto subType : responseSubTypeList) {
          LinkedHashMap<String, Object> textScaleMap = new LinkedHashMap<>();
          textScaleMap.put("text", StringUtils.isEmpty(subType.getText()) ? "" : subType.getText());
          textScaleMap.put(
              "value", StringUtils.isEmpty(subType.getValue()) ? "" : subType.getValue());
          textScaleMap.put(
              "detail", StringUtils.isEmpty(subType.getDetail()) ? "" : subType.getDetail());
          textScaleMap.put(
              "exclusive",
              ((subType.getExclusive() == null)
                      || subType.getExclusive().equalsIgnoreCase(StudyMetaDataConstants.YES))
                  ? true
                  : false);
          textChoicesList.add(textScaleMap);
        }
      }
      questionFormat.put("textChoices", textChoicesList);
      questionFormat.put(
          "default",
          ((reponseType == null) || (reponseType.getStep() == null)) ? 1 : reponseType.getStep());
      questionFormat.put(
          "vertical",
          ((reponseType == null)
                  || (reponseType.getVertical() == null)
                  || !reponseType.getVertical())
              ? false
              : true);
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - formatQuestionTextScaleDetails() :: ERROR", e);
    }
    LOGGER.exit("formatQuestionTextScaleDetails() :: Ends");
    return questionFormat;
  }

  @SuppressWarnings("unchecked")
  public Map<String, Object> formatQuestionValuePickerDetails(
      QuestionsDto questionDto, Session session) throws DAOException {
    LOGGER.entry("begin formatQuestionValuePickerDetails()");
    Map<String, Object> questionFormat = new LinkedHashMap<>();
    List<QuestionResponseSubTypeDto> responseSubTypeList = null;
    List<LinkedHashMap<String, Object>> valuePickerList = new ArrayList<>();
    try {
      responseSubTypeList =
          session
              .createQuery(
                  "from QuestionResponseSubTypeDto QRSTDTO"
                      + " where QRSTDTO.responseTypeId= :respType")
              .setString("respType", questionDto.getId())
              .list();
      if ((responseSubTypeList != null) && !responseSubTypeList.isEmpty()) {
        for (QuestionResponseSubTypeDto subType : responseSubTypeList) {
          LinkedHashMap<String, Object> valuePickerMap = new LinkedHashMap<>();
          valuePickerMap.put(
              "text", StringUtils.isEmpty(subType.getText()) ? "" : subType.getText());
          valuePickerMap.put(
              "value", StringUtils.isEmpty(subType.getValue()) ? "" : subType.getValue());
          valuePickerMap.put(
              "detail", StringUtils.isEmpty(subType.getDetail()) ? "" : subType.getDetail());
          valuePickerMap.put(
              "exclusive",
              (StringUtils.isEmpty(subType.getExclusive())
                      || subType.getExclusive().equalsIgnoreCase(StudyMetaDataConstants.YES))
                  ? true
                  : false);
          valuePickerList.add(valuePickerMap);
        }
      }
      questionFormat.put("textChoices", valuePickerList);
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - formatQuestionValuePickerDetails() :: ERROR", e);
    }
    LOGGER.exit("formatQuestionValuePickerDetails() :: Ends");
    return questionFormat;
  }

  @SuppressWarnings("unchecked")
  public Map<String, Object> formatQuestionImageChoiceDetails(
      QuestionsDto questionDto, Session session, StudyDto studyDto) throws DAOException {
    LOGGER.info("INFO: ActivityMetaDataDao - formatQuestionImageChoiceDetails() :: Starts");
    Map<String, Object> questionFormat = new LinkedHashMap<>();
    List<QuestionResponseSubTypeDto> responseSubTypeList = null;
    List<LinkedHashMap<String, Object>> imageChoicesList = new ArrayList<>();
    try {
      responseSubTypeList =
          session
              .createQuery(
                  "from QuestionResponseSubTypeDto QRSTDTO"
                      + " where QRSTDTO.responseTypeId=:respType")
              .setString("respType", questionDto.getId())
              .list();
      if ((responseSubTypeList != null) && !responseSubTypeList.isEmpty()) {
        for (QuestionResponseSubTypeDto subType : responseSubTypeList) {
          LinkedHashMap<String, Object> imageChoiceMap = new LinkedHashMap<>();
          imageChoiceMap.put(
              "image",
              StringUtils.isEmpty(subType.getImage())
                  ? ""
                  : this.getBase64Image(
                      StudyMetaDataUtil.getSignedUrl(
                          propMap.get("cloud.bucket.name"),
                          StudyMetaDataConstants.STUDIES
                              + "/"
                              + studyDto.getCustomStudyId()
                              + "/"
                              + propMap
                                  .get(StudyMetaDataConstants.FDA_SMD_QUESTIONNAIRE_IMAGE)
                                  .trim()
                              + subType.getImage(),
                          12)));
          imageChoiceMap.put(
              "selectedImage",
              StringUtils.isEmpty(subType.getSelectedImage())
                  ? ""
                  : this.getBase64Image(
                      StudyMetaDataUtil.getSignedUrl(
                          propMap.get("cloud.bucket.name"),
                          StudyMetaDataConstants.STUDIES
                              + "/"
                              + studyDto.getId()
                              + "/"
                              + propMap
                                  .get(StudyMetaDataConstants.FDA_SMD_QUESTIONNAIRE_IMAGE)
                                  .trim()
                              + subType.getSelectedImage(),
                          12)));
          imageChoiceMap.put(
              "text", StringUtils.isEmpty(subType.getText()) ? "" : subType.getText());
          imageChoiceMap.put(
              "value", StringUtils.isEmpty(subType.getValue()) ? "" : subType.getValue());
          imageChoicesList.add(imageChoiceMap);
        }
      }
      questionFormat.put("imageChoices", imageChoicesList);
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - formatQuestionImageChoiceDetails() :: ERROR", e);
    }
    LOGGER.exit("formatQuestionImageChoiceDetails() :: Ends");
    return questionFormat;
  }

  @SuppressWarnings("unchecked")
  public Map<String, Object> formatQuestionTextChoiceDetails(
      QuestionsDto questionDto, QuestionReponseTypeDto reponseType, Session session)
      throws DAOException {
    LOGGER.entry("begin formatQuestionTextChoiceDetails()");
    Map<String, Object> questionFormat = new LinkedHashMap<>();
    List<QuestionResponseSubTypeDto> responseSubTypeList = null;
    List<LinkedHashMap<String, Object>> textChoiceMapList = new ArrayList<>();
    try {
      responseSubTypeList =
          session
              .createQuery(
                  "from QuestionResponseSubTypeDto QRSTDTO"
                      + " where QRSTDTO.responseTypeId=:respType")
              .setString("respType", questionDto.getId())
              .list();
      if ((responseSubTypeList != null) && !responseSubTypeList.isEmpty()) {
        for (QuestionResponseSubTypeDto subType : responseSubTypeList) {
          LinkedHashMap<String, Object> textChoiceMap = new LinkedHashMap<>();
          textChoiceMap.put(
              "text", StringUtils.isEmpty(subType.getText()) ? "" : subType.getText());
          textChoiceMap.put(
              "value", StringUtils.isEmpty(subType.getValue()) ? "" : subType.getValue());
          textChoiceMap.put(
              "detail",
              StringUtils.isEmpty(subType.getDescription()) ? "" : subType.getDescription());
          textChoiceMap.put(
              "exclusive",
              ((subType.getExclusive() == null)
                      || subType.getExclusive().equalsIgnoreCase(StudyMetaDataConstants.NO))
                  ? false
                  : true);
          textChoiceMapList.add(textChoiceMap);
        }
      }
      /** other type add destination if there start */
      QuestionReponseTypeDto otherReponseSubType =
          (QuestionReponseTypeDto)
              session
                  .createQuery(
                      "from QuestionReponseTypeDto QRTDTO"
                          + " where QRTDTO.questionsResponseTypeId=:quesRespId"
                          + " ORDER BY QRTDTO.responseTypeId DESC")
                  .setString("quesRespId", questionDto.getId())
                  .setMaxResults(1)
                  .uniqueResult();

      if ((otherReponseSubType != null)
          && (otherReponseSubType.getOtherType() != null)
          && StringUtils.isNotEmpty(otherReponseSubType.getOtherType())
          && otherReponseSubType.getOtherType().equals("on")) {
        LinkedHashMap<String, Object> textChoiceMap = new LinkedHashMap<>();
        textChoiceMap.put(
            "text",
            StringUtils.isEmpty(otherReponseSubType.getOtherText())
                ? ""
                : otherReponseSubType.getOtherText());
        textChoiceMap.put(
            "value",
            StringUtils.isEmpty(otherReponseSubType.getOtherValue())
                ? ""
                : otherReponseSubType.getOtherValue());
        textChoiceMap.put(
            "detail",
            StringUtils.isEmpty(otherReponseSubType.getOtherDescription())
                ? ""
                : otherReponseSubType.getOtherDescription());
        textChoiceMap.put(
            "exclusive",
            ((otherReponseSubType.getOtherExclusive() == null)
                    || otherReponseSubType
                        .getOtherExclusive()
                        .equalsIgnoreCase(StudyMetaDataConstants.NO))
                ? false
                : true);
        if (StringUtils.isNotEmpty(otherReponseSubType.getOtherIncludeText())
            && otherReponseSubType.getOtherIncludeText().equals(StudyMetaDataConstants.YES)) {
          LinkedHashMap<String, Object> textChoiceOtherMap = new LinkedHashMap<>();
          textChoiceOtherMap.put(
              "placeholder",
              StringUtils.isEmpty(otherReponseSubType.getOtherPlaceholderText())
                  ? ""
                  : otherReponseSubType.getOtherPlaceholderText());
          textChoiceOtherMap.put(
              "isMandatory",
              ((otherReponseSubType.getOtherParticipantFill() == null)
                      || otherReponseSubType
                          .getOtherParticipantFill()
                          .equalsIgnoreCase(StudyMetaDataConstants.NO))
                  ? false
                  : true);
          textChoiceOtherMap.put(
              "textfieldReq",
              ((otherReponseSubType.getOtherIncludeText() == null)
                      || otherReponseSubType
                          .getOtherIncludeText()
                          .equalsIgnoreCase(StudyMetaDataConstants.NO))
                  ? false
                  : true);
          textChoiceMap.put("other", textChoiceOtherMap);
        } else {
          LinkedHashMap<String, Object> textChoiceOtherMap = new LinkedHashMap<>();
          textChoiceOtherMap.put("placeholder", "");
          textChoiceOtherMap.put("isMandatory", false);
          textChoiceOtherMap.put("textfieldReq", false);
          textChoiceMap.put("other", textChoiceOtherMap);
        }

        textChoiceMapList.add(textChoiceMap);
      }
      /** other type add destination if there end */
      questionFormat.put("textChoices", textChoiceMapList);
      questionFormat.put(
          "selectionStyle",
          ((reponseType == null) || StringUtils.isEmpty(reponseType.getSelectionStyle()))
              ? ""
              : reponseType.getSelectionStyle()); // Single/Multiple
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - formatQuestionTextChoiceDetails() :: ERROR", e);
    }
    LOGGER.exit("formatQuestionTextChoiceDetails() :: Ends");
    return questionFormat;
  }

  public Map<String, Object> formatQuestionNumericDetails(QuestionReponseTypeDto reponseType)
      throws DAOException {
    LOGGER.entry("begin formatQuestionNumericDetails()");
    Map<String, Object> questionFormat = new LinkedHashMap<>();
    try {
      questionFormat.put(
          "style",
          ((reponseType == null) || StringUtils.isEmpty(reponseType.getStyle()))
              ? StudyMetaDataConstants.QUESTION_NUMERIC_STYLE_INTEGER
              : reponseType.getStyle());
      questionFormat.put(
          "unit",
          ((reponseType == null) || StringUtils.isEmpty(reponseType.getUnit()))
              ? ""
              : reponseType.getUnit());
      if (questionFormat
          .get("style")
          .toString()
          .equalsIgnoreCase(StudyMetaDataConstants.QUESTION_NUMERIC_STYLE_INTEGER)) {
        questionFormat.put(
            "minValue",
            ((reponseType == null) || StringUtils.isEmpty(reponseType.getMinValue()))
                ? 0
                : Integer.parseInt(reponseType.getMinValue()));
        questionFormat.put(
            "maxValue",
            ((reponseType == null) || StringUtils.isEmpty(reponseType.getMaxValue()))
                ? 10000
                : Integer.parseInt(reponseType.getMaxValue()));
      } else {
        questionFormat.put(
            "minValue",
            ((reponseType == null) || StringUtils.isEmpty(reponseType.getMinValue()))
                ? 0d
                : Double.parseDouble(reponseType.getMinValue()));
        questionFormat.put(
            "maxValue",
            ((reponseType == null) || StringUtils.isEmpty(reponseType.getMaxValue()))
                ? 10000d
                : Double.parseDouble(reponseType.getMaxValue()));
      }
      questionFormat.put(
          "placeholder",
          ((reponseType == null) || StringUtils.isEmpty(reponseType.getPlaceholder()))
              ? ""
              : reponseType.getPlaceholder());
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - formatQuestionNumericDetails() :: ERROR", e);
    }
    LOGGER.exit("formatQuestionNumericDetails() :: Ends");
    return questionFormat;
  }

  public Map<String, Object> formatQuestionDateDetails(QuestionReponseTypeDto reponseType)
      throws DAOException {
    LOGGER.entry("begin formatQuestionDateDetails()");
    Map<String, Object> questionFormat = new LinkedHashMap<>();
    String dateFormat = "";
    try {
      questionFormat.put(
          "style",
          ((reponseType == null) || StringUtils.isEmpty(reponseType.getStyle()))
              ? ""
              : reponseType.getStyle());
      if ((reponseType != null)
          && StringUtils.isNotEmpty(reponseType.getStyle())
          && reponseType
              .getStyle()
              .equalsIgnoreCase(
                  StudyMetaDataConstants.QUESTION_RESPONSE_MASTERDATA_TYPE_DATE_DATE)) {
        dateFormat = StudyMetaDataConstants.SDF_DATE_PATTERN;
      } else {
        dateFormat = StudyMetaDataConstants.SDF_DATE_TIME_PATTERN;
      }
      questionFormat.put(
          "minDate",
          ((reponseType == null) || StringUtils.isEmpty(reponseType.getMinDate()))
              ? ""
              : StudyMetaDataUtil.getFormattedDateTimeZone(
                  reponseType.getMinDate(),
                  dateFormat,
                  StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
      questionFormat.put(
          "maxDate",
          ((reponseType == null) || StringUtils.isEmpty(reponseType.getMaxDate()))
              ? ""
              : StudyMetaDataUtil.getFormattedDateTimeZone(
                  reponseType.getMaxDate(),
                  dateFormat,
                  StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
      questionFormat.put(
          "default",
          ((reponseType == null) || StringUtils.isEmpty(reponseType.getDefaultDate()))
              ? ""
              : StudyMetaDataUtil.getFormattedDateTimeZone(
                  reponseType.getDefaultDate(),
                  dateFormat,
                  StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN)); // Date
      questionFormat.put(
          "dateRange",
          ((reponseType == null) || StringUtils.isEmpty(reponseType.getSelectionStyle())
              ? StudyMetaDataConstants.DATE_RANGE_CUSTOM
              : this.getDateRangeType(reponseType.getSelectionStyle())));
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - formatQuestionDateDetails() :: ERROR", e);
    }
    LOGGER.exit("formatQuestionDateDetails() :: Ends");
    return questionFormat;
  }

  public Map<String, Object> formatQuestionTextDetails(QuestionReponseTypeDto reponseType)
      throws DAOException {
    LOGGER.entry("begin formatQuestionTextDetails()");
    Map<String, Object> questionFormat = new LinkedHashMap<>();
    try {
      questionFormat.put(
          "maxLength",
          ((reponseType == null) || (reponseType.getMaxLength() == null))
              ? 0
              : reponseType.getMaxLength());
      questionFormat.put(
          "validationRegex",
          ((reponseType == null) || StringUtils.isEmpty(reponseType.getValidationRegex()))
              ? ""
              : reponseType.getValidationRegex());
      questionFormat.put(
          "invalidMessage",
          ((reponseType == null) || StringUtils.isEmpty(reponseType.getInvalidMessage()))
              ? "Invalid Input. Please try again."
              : reponseType.getInvalidMessage());
      questionFormat.put(
          "multipleLines",
          ((reponseType == null)
                  || (reponseType.getMultipleLines() == null)
                  || !reponseType.getMultipleLines())
              ? false
              : true);
      questionFormat.put(
          "placeholder",
          ((reponseType == null) || StringUtils.isEmpty(reponseType.getPlaceholder()))
              ? ""
              : reponseType.getPlaceholder());
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - formatQuestionTextDetails() :: ERROR", e);
    }
    LOGGER.exit("formatQuestionTextDetails() :: Ends");
    return questionFormat;
  }

  @SuppressWarnings("unchecked")
  public ActivitiesBean getTimeDetailsByActivityIdForActiveTask(
      ActiveTaskDto activeTaskDto, ActivitiesBean activityBean, Session session)
      throws DAOException {
    LOGGER.entry("begin getTimeDetailsByActivityIdForActiveTask()");
    String startDateTime = "";
    String endDateTime = "";
    try {
      startDateTime =
          activeTaskDto.getActiveTaskLifetimeStart()
              + " "
              + StudyMetaDataConstants.DEFAULT_MIN_TIME;
      endDateTime =
          StringUtils.isEmpty(activeTaskDto.getActiveTaskLifetimeEnd())
              ? ""
              : activeTaskDto.getActiveTaskLifetimeEnd()
                  + " "
                  + StudyMetaDataConstants.DEFAULT_MAX_TIME;
      if (StringUtils.isNotEmpty(activeTaskDto.getFrequency())) {
        if ((activeTaskDto
                .getFrequency()
                .equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME))
            || (activeTaskDto
                .getFrequency()
                .equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_WEEKLY))
            || (activeTaskDto
                .getFrequency()
                .equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_MONTHLY))) {

          ActiveTaskFrequencyDto activeTaskFrequency =
              (ActiveTaskFrequencyDto)
                  session
                      .createQuery(
                          "from ActiveTaskFrequencyDto ATFDTO"
                              + " where ATFDTO.activeTaskId=:activeTaskId")
                      .setString("activeTaskId", activeTaskDto.getId())
                      .uniqueResult();
          if ((activeTaskFrequency != null)
              && StringUtils.isNotEmpty(activeTaskFrequency.getFrequencyTime())) {
            if (activeTaskFrequency.isLaunchStudy() && activeTaskFrequency.isStudyLifeTime()) {
              startDateTime =
                  activeTaskDto.getActiveTaskLifetimeStart()
                      + " "
                      + activeTaskFrequency.getFrequencyTime();
            } else {
              startDateTime =
                  activeTaskFrequency.getFrequencyDate()
                      + " "
                      + activeTaskFrequency.getFrequencyTime();
            }
            if (!activeTaskDto
                    .getFrequency()
                    .equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME)
                && !activeTaskFrequency.isStudyLifeTime()) {
              endDateTime =
                  activeTaskDto.getActiveTaskLifetimeEnd()
                      + " "
                      + activeTaskFrequency.getFrequencyTime();
            }
          }

          activityBean.setStartTime(
              StudyMetaDataUtil.getFormattedDateTimeZone(
                  startDateTime,
                  StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                  StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          activityBean.setEndTime(
              StringUtils.isEmpty(endDateTime)
                  ? ""
                  : StudyMetaDataUtil.getFormattedDateTimeZone(
                      endDateTime,
                      StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                      StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          activityBean.setIsLaunchStudy(activeTaskFrequency.isLaunchStudy());
          activityBean.setIsStudyLifeTime(activeTaskFrequency.isStudyLifeTime());
        } else if (activeTaskDto
            .getFrequency()
            .equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_DAILY)) {

          List<ActiveTaskFrequencyDto> activeTaskFrequencyList =
              session
                  .createQuery(
                      "from ActiveTaskFrequencyDto ATFDTO"
                          + " where ATFDTO.activeTaskId=:activeTaskId"
                          + " ORDER BY ATFDTO.frequencyTime")
                  .setString("activeTaskId", activeTaskDto.getId())
                  .list();
          if ((activeTaskFrequencyList != null) && !activeTaskFrequencyList.isEmpty()) {
            startDateTime =
                activeTaskDto.getActiveTaskLifetimeStart()
                    + " "
                    + activeTaskFrequencyList.get(0).getFrequencyTime();
            endDateTime =
                activeTaskDto.getActiveTaskLifetimeEnd()
                    + " "
                    + StudyMetaDataConstants.DEFAULT_MAX_TIME;
          }

          activityBean.setStartTime(
              StudyMetaDataUtil.getFormattedDateTimeZone(
                  startDateTime,
                  StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                  StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          activityBean.setEndTime(
              StudyMetaDataUtil.getFormattedDateTimeZone(
                  endDateTime,
                  StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                  StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
        } else if (activeTaskDto
            .getFrequency()
            .equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {

          List<ActiveTaskCustomFrequenciesDto> activeTaskCustomFrequencyList =
              session
                  .createQuery(
                      "from ActiveTaskCustomFrequenciesDto ATCFDTO"
                          + " where ATCFDTO.activeTaskId=:activeTaskId"
                          + " ORDER BY ATCFDTO.frequencyTime")
                  .setString("activeTaskId", activeTaskDto.getId())
                  .list();
          if ((activeTaskCustomFrequencyList != null) && !activeTaskCustomFrequencyList.isEmpty()) {
            String startDate = activeTaskCustomFrequencyList.get(0).getFrequencyStartDate();
            String endDate = activeTaskCustomFrequencyList.get(0).getFrequencyEndDate();

            for (ActiveTaskCustomFrequenciesDto customFrequency : activeTaskCustomFrequencyList) {
              if (StudyMetaDataConstants.SDF_DATE
                  .parse(startDate)
                  .after(
                      StudyMetaDataConstants.SDF_DATE.parse(
                          customFrequency.getFrequencyStartDate()))) {
                startDate = customFrequency.getFrequencyStartDate();
              }

              if (StudyMetaDataConstants.SDF_DATE
                  .parse(endDate)
                  .before(
                      StudyMetaDataConstants.SDF_DATE.parse(
                          customFrequency.getFrequencyEndDate()))) {
                endDate = customFrequency.getFrequencyEndDate();
              }
            }

            startDateTime =
                startDate + " " + activeTaskCustomFrequencyList.get(0).getFrequencyStartTime();
            endDateTime =
                endDate
                    + " "
                    + activeTaskCustomFrequencyList
                        .get(activeTaskCustomFrequencyList.size() - 1)
                        .getFrequencyEndTime();
          }

          activityBean.setStartTime(
              StudyMetaDataUtil.getFormattedDateTimeZone(
                  startDateTime,
                  StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                  StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          activityBean.setEndTime(
              StudyMetaDataUtil.getFormattedDateTimeZone(
                  endDateTime,
                  StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                  StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
        }
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getTimeDetailsByActivityIdForActiveTask() :: ERROR", e);
    }
    LOGGER.exit("getTimeDetailsByActivityIdForActiveTask() :: Ends");
    return activityBean;
  }

  @SuppressWarnings("unchecked")
  public ActivitiesBean getTimeDetailsByActivityIdForQuestionnaire(
      QuestionnairesDto questionaire, ActivitiesBean activityBean, Session session)
      throws DAOException {
    LOGGER.entry("begin getTimeDetailsByActivityIdForQuestionnaire()");
    String startDateTime = "";
    String endDateTime = "";
    try {
      startDateTime =
          questionaire.getStudyLifetimeStart() + " " + StudyMetaDataConstants.DEFAULT_MIN_TIME;
      endDateTime =
          StringUtils.isEmpty(questionaire.getStudyLifetimeEnd())
              ? ""
              : questionaire.getStudyLifetimeEnd() + " " + StudyMetaDataConstants.DEFAULT_MAX_TIME;
      if (StringUtils.isNotEmpty(questionaire.getFrequency())) {
        if ((questionaire
                .getFrequency()
                .equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME))
            || (questionaire
                .getFrequency()
                .equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_WEEKLY))
            || (questionaire
                .getFrequency()
                .equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_MONTHLY))) {

          QuestionnairesFrequenciesDto questionnairesFrequency =
              (QuestionnairesFrequenciesDto)
                  session
                      .createQuery(
                          "from QuestionnairesFrequenciesDto QFDTO"
                              + " where QFDTO.questionnairesId=:quesRespId")
                      .setString("quesRespId", questionaire.getId())
                      .uniqueResult();
          if ((questionnairesFrequency != null)
              && StringUtils.isNotEmpty(questionnairesFrequency.getFrequencyTime())) {
            startDateTime =
                questionaire.getStudyLifetimeStart()
                    + " "
                    + questionnairesFrequency.getFrequencyTime();
            if (!questionaire
                    .getFrequency()
                    .equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME)
                && !questionnairesFrequency.getIsStudyLifeTime()) {
              endDateTime =
                  questionaire.getStudyLifetimeEnd()
                      + " "
                      + questionnairesFrequency.getFrequencyTime();
            }
          }

          activityBean.setStartTime(
              StudyMetaDataUtil.getFormattedDateTimeZone(
                  startDateTime,
                  StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                  StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          activityBean.setEndTime(
              StringUtils.isEmpty(endDateTime)
                  ? ""
                  : StudyMetaDataUtil.getFormattedDateTimeZone(
                      endDateTime,
                      StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                      StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          activityBean.setIsLaunchStudy(questionnairesFrequency.getIsLaunchStudy());
          activityBean.setIsStudyLifeTime(questionnairesFrequency.getIsStudyLifeTime());
        } else if (questionaire
            .getFrequency()
            .equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_DAILY)) {

          List<QuestionnairesFrequenciesDto> questionnairesFrequencyList =
              session
                  .createQuery(
                      "from QuestionnairesFrequenciesDto QFDTO"
                          + " where QFDTO.questionnairesId=:quesRespId"
                          + " ORDER BY QFDTO.frequencyTime")
                  .setString("quesRespId", questionaire.getId())
                  .list();
          if ((questionnairesFrequencyList != null) && !questionnairesFrequencyList.isEmpty()) {
            startDateTime =
                questionaire.getStudyLifetimeStart()
                    + " "
                    + questionnairesFrequencyList.get(0).getFrequencyTime();
            endDateTime =
                questionaire.getStudyLifetimeEnd() + " " + StudyMetaDataConstants.DEFAULT_MAX_TIME;
          }

          activityBean.setStartTime(
              StudyMetaDataUtil.getFormattedDateTimeZone(
                  startDateTime,
                  StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                  StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          activityBean.setEndTime(
              StudyMetaDataUtil.getFormattedDateTimeZone(
                  endDateTime,
                  StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                  StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
        } else if (questionaire
            .getFrequency()
            .equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {

          List<QuestionnairesCustomFrequenciesDto> questionnaireCustomFrequencyList =
              session
                  .createQuery(
                      "from QuestionnairesCustomFrequenciesDto QCFDTO"
                          + " where QCFDTO.questionnairesId=:quesResId"
                          + " ORDER BY QCFDTO.frequencyTime")
                  .setString("quesResId", questionaire.getId())
                  .list();
          if ((questionnaireCustomFrequencyList != null)
              && !questionnaireCustomFrequencyList.isEmpty()) {

            String startDate = questionnaireCustomFrequencyList.get(0).getFrequencyStartDate();
            String endDate = questionnaireCustomFrequencyList.get(0).getFrequencyEndDate();

            for (QuestionnairesCustomFrequenciesDto customFrequency :
                questionnaireCustomFrequencyList) {
              if (StudyMetaDataConstants.SDF_DATE
                  .parse(startDate)
                  .after(
                      StudyMetaDataConstants.SDF_DATE.parse(
                          customFrequency.getFrequencyStartDate()))) {
                startDate = customFrequency.getFrequencyStartDate();
              }

              if (StudyMetaDataConstants.SDF_DATE
                  .parse(endDate)
                  .before(
                      StudyMetaDataConstants.SDF_DATE.parse(
                          customFrequency.getFrequencyEndDate()))) {
                endDate = customFrequency.getFrequencyEndDate();
              }
            }

            startDateTime =
                startDate + " " + questionnaireCustomFrequencyList.get(0).getFrequencyStartTime();
            endDateTime =
                endDate
                    + " "
                    + questionnaireCustomFrequencyList
                        .get(questionnaireCustomFrequencyList.size() - 1)
                        .getFrequencyEndTime();
          }

          activityBean.setStartTime(
              StudyMetaDataUtil.getFormattedDateTimeZone(
                  startDateTime,
                  StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                  StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
          activityBean.setEndTime(
              StudyMetaDataUtil.getFormattedDateTimeZone(
                  endDateTime,
                  StudyMetaDataConstants.SDF_DATE_TIME_PATTERN,
                  StudyMetaDataConstants.SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN));
        }
      }
    } catch (Exception e) {
      LOGGER.error(
          "ActivityMetaDataDao - getTimeDetailsByActivityIdForQuestionnaire() :: ERROR", e);
    }
    LOGGER.exit("getTimeDetailsByActivityIdForQuestionnaire() :: Ends");
    return activityBean;
  }

  public List<QuestionnairesStepsDto> getDestinationStepType(
      List<QuestionnairesStepsDto> questionaireStepsList) throws DAOException {
    LOGGER.entry("begin getDestinationStepType()");
    List<QuestionnairesStepsDto> questionnaireStepsTypeList = new ArrayList<>();
    try {
      for (QuestionnairesStepsDto questionnaireStepsDto : questionaireStepsList) {
        for (QuestionnairesStepsDto stepsDto : questionaireStepsList) {
          if (questionnaireStepsDto.getDestinationStep().equals(stepsDto.getStepId())) {
            questionnaireStepsDto.setDestinationStepType(
                StringUtils.isEmpty(stepsDto.getStepShortTitle())
                    ? ""
                    : stepsDto.getStepShortTitle());
            break;
          }
        }
        questionnaireStepsTypeList.add(questionnaireStepsDto);
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getDestinationStepType() :: ERROR", e);
    }
    LOGGER.exit("getDestinationStepType() :: Ends");
    return questionnaireStepsTypeList;
  }

  public DestinationBean getDestinationStepTypeForResponseSubType(
      DestinationBean destinationBean,
      QuestionResponseSubTypeDto destinationDto,
      List<QuestionnairesStepsDto> questionaireStepsList)
      throws DAOException {
    LOGGER.entry("begin getDestinationStepTypeForResponseSubType()");
    try {
      for (QuestionnairesStepsDto stepsDto : questionaireStepsList) {
        if (destinationDto.getDestinationStepId().equals(stepsDto.getStepId())) {
          destinationBean.setDestination(
              StringUtils.isEmpty(stepsDto.getStepShortTitle())
                  ? ""
                  : stepsDto.getStepShortTitle());
          break;
        }
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getDestinationStepTypeForResponseSubType() :: ERROR", e);
    }
    LOGGER.exit("getDestinationStepTypeForResponseSubType() :: Ends");
    return destinationBean;
  }

  public String getBase64Image(String imagePath) throws DAOException {
    LOGGER.entry("begin getBase64Image()");
    String base64Image = "";
    byte[] imageBytes = null;
    try {
      URL url = new URL(imagePath);
      if ("https".equalsIgnoreCase(url.getProtocol())) {
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        InputStream ins = con.getInputStream();
        imageBytes = IOUtils.toByteArray(ins);
      } else {
        imageBytes = IOUtils.toByteArray(new URL(imagePath));
      }

      base64Image = Base64.getEncoder().encodeToString(imageBytes);
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getBase64Image() :: ERROR", e);
    }
    LOGGER.exit("getBase64Image() :: Ends");
    return base64Image;
  }

  public Integer getScaleStepCount(Integer step, Integer maxValue, Integer minValue)
      throws DAOException {
    LOGGER.entry("begin getScaleStepCount()");
    Integer scaleStepCount = 1;
    Integer maxStepCount = 13;
    List<Integer> stepCountList = new ArrayList<>();
    try {
      Integer diff = maxValue - minValue;
      while (maxStepCount > 0) {
        if ((diff % maxStepCount) == 0) {
          stepCountList.add(maxStepCount);
        }
        maxStepCount--;
      }
      if (stepCountList.contains(step)) {
        scaleStepCount = step;
        return scaleStepCount;
      }
      scaleStepCount = stepCountList.get(0);
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getScaleStepCount() :: ERROR", e);
    }
    LOGGER.exit("getScaleStepCount() :: Ends");
    return scaleStepCount;
  }

  public Integer getScaleStepSize(Integer step, Integer maxValue, Integer minValue)
      throws DAOException {
    LOGGER.entry("begin getScaleStepSize()");
    Integer scaleStepCount = step;
    try {
      scaleStepCount = (maxValue - minValue) / step;
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getScaleStepSize() :: ERROR", e);
    }
    LOGGER.exit("getScaleStepSize() :: Ends");
    return scaleStepCount;
  }

  public Integer getScaleDefaultValue(
      Integer step, Integer maxValue, Integer minValue, Integer defaultValue) throws DAOException {
    LOGGER.entry("begin getScaleDefaultValue()");
    Integer stepSize = (maxValue - minValue) / step;
    Integer scaleDefaultValue = minValue;
    try {
      scaleDefaultValue += (stepSize * defaultValue);
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getScaleDefaultValue() :: ERROR", e);
    }
    LOGGER.exit("getScaleDefaultValue() :: Ends");
    return scaleDefaultValue;
  }

  public Integer getContinuousScaleMaxFractionDigits(
      Integer maxValue, Integer minValue, Integer actualFractionDigits) throws DAOException {
    LOGGER.entry("begin getContinuousScaleMaxFractionDigits()");
    Integer maxFracDigits = 0;
    Integer minTemp = 0;
    Integer maxTemp = 0;
    try {

      if ((maxValue > 0) && (maxValue <= 1)) {
        maxTemp = 4;
      } else if ((maxValue > 1) && (maxValue <= 10)) {
        maxTemp = 3;
      } else if ((maxValue > 10) && (maxValue <= 100)) {
        maxTemp = 2;
      } else if ((maxValue > 100) && (maxValue <= 1000)) {
        maxTemp = 1;
      } else if ((maxValue > 1000) && (maxValue <= 10000)) {
        maxTemp = 0;
      }

      if ((minValue >= -10000) && (minValue < -1000)) {
        minTemp = 0;
      } else if ((minValue >= -1000) && (minValue < -100)) {
        minTemp = 1;
      } else if ((minValue >= -100) && (minValue < -10)) {
        minTemp = 2;
      } else if ((minValue >= -10) && (minValue < -1)) {
        minTemp = 3;
      } else if (minValue >= -1) {
        minTemp = 4;
      }
      maxFracDigits = (maxTemp > minTemp) ? minTemp : maxTemp;

      if (actualFractionDigits <= maxFracDigits) {
        maxFracDigits = actualFractionDigits;
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getContinuousScaleMaxFractionDigits() :: ERROR", e);
    }
    LOGGER.exit("getContinuousScaleMaxFractionDigits() :: Ends");
    return maxFracDigits;
  }

  public Integer getContinuousScaleDefaultValue(
      Integer maxValue, Integer minValue, Integer defaultValue) throws DAOException {
    LOGGER.entry("begin getContinuousScaleDefaultValue()");
    Integer continuousScaleDefaultValue = minValue;
    try {
      if ((defaultValue != null) && (defaultValue >= minValue) && (defaultValue <= maxValue)) {
        continuousScaleDefaultValue = defaultValue;
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getContinuousScaleDefaultValue() :: ERROR", e);
    }
    LOGGER.exit("getContinuousScaleDefaultValue() :: Ends");
    return continuousScaleDefaultValue;
  }

  public Integer getTimeIntervalStep(Integer stepValue) throws DAOException {
    LOGGER.entry("begin getTimeIntervalStep()");
    Integer step = 1;
    Integer[] stepArray = {1, 2, 3, 4, 5, 6, 10, 12, 15, 20, 30};
    try {
      for (Integer element : stepArray) {
        if (stepValue > element) {
          step = element;
        } else {
          step = element;
          break;
        }
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getTimeIntervalStep() :: ERROR", e);
    }
    LOGGER.exit("getTimeIntervalStep() :: Ends");
    return step;
  }

  public Long getTimeInSeconds(String time) throws DAOException {
    LOGGER.entry("begin getTimeInSeconds()");
    Long defaultTime = 0L;
    try {
      String[] timeArray = time.split(":");
      defaultTime += (long) (Integer.parseInt(timeArray[0]) * 3600);
      defaultTime += (long) (Integer.parseInt(timeArray[1]) * 60);
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getTimeInSeconds() :: ERROR", e);
    }
    LOGGER.exit("getTimeInSeconds() :: Ends");
    return defaultTime;
  }

  public List<DestinationBean> getConditionalBranchingDestinations(
      QuestionReponseTypeDto reponseType,
      List<DestinationBean> destinationsList,
      QuestionnaireActivityStepsBean questionBean)
      throws DAOException {
    LOGGER.entry("begin getConditionalBranchingDestinations()");
    ScriptEngineManager mgr = new ScriptEngineManager();
    ScriptEngine engine = mgr.getEngineByName("JavaScript");

    String conditionFormula = "";
    String operator = "";
    StringTokenizer tokenizer = null;
    String LHS = "";
    String RHS = "";
    String tempFormula = "";
    boolean flag = false;
    Double maxFractionDigit = 1D;
    Double minValue = 0D;
    Double maxValue = 0D;
    Double valueOfX = 0D;
    Integer digitFormat = 0;
    Map<String, Object> prerequisitesMap = new HashMap<>();
    List<DestinationBean> updatedDestinationsList = destinationsList;
    String formatXValue = "";
    boolean skipLoop = false;
    try {
      if (StringUtils.isNotEmpty(reponseType.getConditionFormula())) {
        conditionFormula = reponseType.getConditionFormula();

        /**
         * Check the expression contains '=', if yes replace it with '==' to evaluate the expression
         */
        if (!reponseType
                .getConditionFormula()
                .contains(StudyMetaDataConstants.CBO_OPERATOR_NOT_EQUAL)
            && !reponseType
                .getConditionFormula()
                .contains(StudyMetaDataConstants.CBO_OPERATOR_EQUAL)
            && reponseType.getConditionFormula().contains("=")) {
          conditionFormula =
              reponseType
                  .getConditionFormula()
                  .replaceAll("=", StudyMetaDataConstants.CBO_OPERATOR_EQUAL);
        }

        /** Get the minimum and maximum value range for response type */
        prerequisitesMap = this.conditionalBranchingPrerequisites(questionBean);
        minValue = (Double) prerequisitesMap.get("minValue");
        maxValue = (Double) prerequisitesMap.get("maxValue");
        maxFractionDigit = (Double) prerequisitesMap.get("maxFractionDigit");
        digitFormat = (Integer) prerequisitesMap.get("digitFormat");
        formatXValue = "%." + digitFormat + "f";
        valueOfX = minValue;

        /** Find position of X in the equation i.e LHS or RHS */
        operator = this.getOperatorFromConditionalFormula(conditionFormula);

        /** Evaluate the position of X in the equation */
        if (StringUtils.isNotEmpty(operator)) {
          tokenizer = new StringTokenizer(conditionFormula, operator);
          LHS = tokenizer.nextToken().trim();
          RHS = tokenizer.nextToken().trim();
        }

        /** Find minimum value of X */
        while (valueOfX <= maxValue) {
          tempFormula =
              conditionFormula.replaceAll(
                  "x", valueOfX >= 0 ? valueOfX.toString() : "(" + valueOfX.toString() + ")");
          flag = (boolean) engine.eval(tempFormula);

          if (LHS.contains("x") && !RHS.contains("x")) {
            switch (operator) {
              case StudyMetaDataConstants.CBO_OPERATOR_GREATER_THAN:
                if (flag) {
                  skipLoop = true;

                  valueOfX -= maxFractionDigit;
                  updatedDestinationsList =
                      this.getConditionalBranchingFormat(
                          destinationsList,
                          valueOfX.toString(),
                          StudyMetaDataConstants.CBO_GREATER_THAN,
                          StudyMetaDataConstants.CBO_LESSER_THAN_OR_EQUAL_TO);
                }
                break;
              case StudyMetaDataConstants.CBO_OPERATOR_LESSER_THAN:
                if (!flag) {
                  skipLoop = true;

                  updatedDestinationsList =
                      this.getConditionalBranchingFormat(
                          destinationsList,
                          valueOfX.toString(),
                          StudyMetaDataConstants.CBO_LESSER_THAN,
                          StudyMetaDataConstants.CBO_GREATER_THAN_OR_EQUAL_TO);
                }
                break;
              case StudyMetaDataConstants.CBO_OPERATOR_EQUAL:
                if (flag) {
                  skipLoop = true;

                  updatedDestinationsList =
                      this.getConditionalBranchingFormat(
                          destinationsList,
                          valueOfX.toString(),
                          StudyMetaDataConstants.CBO_EQUAL_TO,
                          StudyMetaDataConstants.CBO_NOT_EQUAL_TO);
                }
                break;
              case StudyMetaDataConstants.CBO_OPERATOR_NOT_EQUAL:
                if (!flag) {
                  skipLoop = true;

                  updatedDestinationsList =
                      this.getConditionalBranchingFormat(
                          destinationsList,
                          valueOfX.toString(),
                          StudyMetaDataConstants.CBO_NOT_EQUAL_TO,
                          StudyMetaDataConstants.CBO_EQUAL_TO);
                }
                break;
              default:
                break;
            }
          } else {
            switch (operator) {
              case StudyMetaDataConstants.CBO_OPERATOR_GREATER_THAN:
                if (!flag) {

                  skipLoop = true;
                  updatedDestinationsList =
                      this.getConditionalBranchingFormat(
                          destinationsList,
                          valueOfX.toString(),
                          StudyMetaDataConstants.CBO_LESSER_THAN,
                          StudyMetaDataConstants.CBO_GREATER_THAN_OR_EQUAL_TO);
                }
                break;
              case StudyMetaDataConstants.CBO_OPERATOR_LESSER_THAN:
                if (flag) {
                  skipLoop = true;

                  valueOfX -= maxFractionDigit;
                  updatedDestinationsList =
                      this.getConditionalBranchingFormat(
                          destinationsList,
                          valueOfX.toString(),
                          StudyMetaDataConstants.CBO_GREATER_THAN,
                          StudyMetaDataConstants.CBO_LESSER_THAN_OR_EQUAL_TO);
                }
                break;
              case StudyMetaDataConstants.CBO_OPERATOR_EQUAL:
                if (flag) {
                  skipLoop = true;

                  updatedDestinationsList =
                      this.getConditionalBranchingFormat(
                          destinationsList,
                          valueOfX.toString(),
                          StudyMetaDataConstants.CBO_EQUAL_TO,
                          StudyMetaDataConstants.CBO_NOT_EQUAL_TO);
                }
                break;
              case StudyMetaDataConstants.CBO_OPERATOR_NOT_EQUAL:
                if (!flag) {
                  skipLoop = true;

                  updatedDestinationsList =
                      this.getConditionalBranchingFormat(
                          destinationsList,
                          valueOfX.toString(),
                          StudyMetaDataConstants.CBO_NOT_EQUAL_TO,
                          StudyMetaDataConstants.CBO_EQUAL_TO);
                }
                break;
              default:
                break;
            }
          }

          if (skipLoop) {
            break;
          }

          valueOfX += maxFractionDigit;
          valueOfX = Double.parseDouble(String.format(formatXValue, valueOfX));
        }

        /** Format the value of X by type */
        updatedDestinationsList = this.formatValueOfX(updatedDestinationsList, questionBean);
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getConditionalBranchingDestinations() :: ERROR", e);
    }
    LOGGER.exit("getConditionalBranchingDestinations() :: Ends");
    return updatedDestinationsList;
  }

  public Map<String, Object> conditionalBranchingPrerequisites(
      QuestionnaireActivityStepsBean questionBean) throws DAOException {
    LOGGER.entry("begin conditionalBranchingPrerequisites()");
    Map<String, Object> prerequisitesMap = new HashMap<>();
    Double maxFractionDigit = 1D;
    Double minValue = 0D;
    Double maxValue = 0D;
    Integer digitFormat = 0;
    try {
      switch (questionBean.getResultType()) {
        case StudyMetaDataConstants.QUESTION_SCALE:
          minValue = Double.parseDouble(questionBean.getFormat().get("minValue").toString());
          maxValue = Double.parseDouble(questionBean.getFormat().get("maxValue").toString());
          maxFractionDigit = 1D;
          digitFormat = 0;
          break;
        case StudyMetaDataConstants.QUESTION_CONTINUOUS_SCALE:
          minValue = Double.parseDouble(questionBean.getFormat().get("minValue").toString());
          maxValue = Double.parseDouble(questionBean.getFormat().get("maxValue").toString());

          switch (Integer.parseInt(questionBean.getFormat().get("maxFractionDigits").toString())) {
            case 0:
              maxFractionDigit = 1D;
              digitFormat = 0;
              break;
            case 1:
              maxFractionDigit = 0.1D;
              digitFormat = 1;
              break;
            case 2:
              maxFractionDigit = 0.01D;
              digitFormat = 2;
              break;
            case 3:
              maxFractionDigit = 0.001D;
              digitFormat = 3;
              break;
            case 4:
              maxFractionDigit = 0.0001D;
              digitFormat = 4;
              break;
            default:
              break;
          }
          break;
        case StudyMetaDataConstants.QUESTION_NUMERIC:
          minValue = Double.parseDouble(questionBean.getFormat().get("minValue").toString());
          maxValue = Double.parseDouble(questionBean.getFormat().get("maxValue").toString());

          switch (questionBean.getFormat().get("style").toString()) {
            case StudyMetaDataConstants.QUESTION_NUMERIC_STYLE_INTEGER:
              maxFractionDigit = 1D;
              digitFormat = 0;
              break;
            case StudyMetaDataConstants.QUESTION_NUMERIC_STYLE_DECIMAL:
              maxFractionDigit = 0.01D;
              digitFormat = 2;
              break;
            default:
              break;
          }
          break;
        case StudyMetaDataConstants.QUESTION_TIME_INTERVAL:
          maxFractionDigit = 1D;
          minValue = 0D;
          maxValue = (double) (24 * 60);
          digitFormat = 0;
          break;
        case StudyMetaDataConstants.QUESTION_HEIGHT:
          maxFractionDigit = 1D;
          minValue = 0D;
          maxValue = 300D;
          digitFormat = 0;
          break;
        default:
          break;
      }

      prerequisitesMap.put("minValue", minValue);
      prerequisitesMap.put("maxValue", maxValue);
      prerequisitesMap.put("maxFractionDigit", maxFractionDigit);
      prerequisitesMap.put("digitFormat", digitFormat);
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - conditionalBranchingPrerequisites() :: ERROR", e);
    }
    LOGGER.exit("conditionalBranchingPrerequisites() :: Ends");
    return prerequisitesMap;
  }

  public String getOperatorFromConditionalFormula(String conditionFormula) throws DAOException {
    LOGGER.entry("begin getOperatorFromConditionalFormula()");
    String operator = "";
    try {
      if (conditionFormula.contains(StudyMetaDataConstants.CBO_OPERATOR_EQUAL)) {
        operator = StudyMetaDataConstants.CBO_OPERATOR_EQUAL;
      } else if (conditionFormula.contains(StudyMetaDataConstants.CBO_OPERATOR_NOT_EQUAL)) {
        operator = StudyMetaDataConstants.CBO_OPERATOR_NOT_EQUAL;
      } else if (conditionFormula.contains(StudyMetaDataConstants.CBO_OPERATOR_GREATER_THAN)) {
        operator = StudyMetaDataConstants.CBO_OPERATOR_GREATER_THAN;
      } else if (conditionFormula.contains(StudyMetaDataConstants.CBO_OPERATOR_LESSER_THAN)) {
        operator = StudyMetaDataConstants.CBO_OPERATOR_LESSER_THAN;
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getOperatorFromConditionalFormula() :: ERROR", e);
    }
    LOGGER.exit("getOperatorFromConditionalFormula() :: Ends");
    return operator;
  }

  public List<DestinationBean> getConditionalBranchingFormat(
      List<DestinationBean> destinationsList,
      String valueOfX,
      String trueOperator,
      String falseOperator)
      throws DAOException {
    LOGGER.entry("begin getConditionalBranchingFormat()");
    try {
      if ((destinationsList != null)
          && !destinationsList.isEmpty()
          && (destinationsList.size() >= 2)) {
        destinationsList.get(0).setCondition(valueOfX);
        destinationsList.get(0).setOperator(trueOperator);

        destinationsList.get(1).setCondition(valueOfX);
        destinationsList.get(1).setOperator(falseOperator);
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getConditionalBranchingFormat() :: ERROR", e);
    }
    LOGGER.exit("getConditionalBranchingFormat() :: Ends");
    return destinationsList;
  }

  public List<DestinationBean> formatValueOfX(
      List<DestinationBean> destinationsList, QuestionnaireActivityStepsBean questionBean)
      throws DAOException {
    LOGGER.entry("begin formatValueOfX()");
    List<DestinationBean> updatedDestinationsList = destinationsList;
    try {
      if ((destinationsList != null)
          && !destinationsList.isEmpty()
          && (destinationsList.size() >= 2)) {
        if (questionBean.getResultType().equals(StudyMetaDataConstants.QUESTION_CONTINUOUS_SCALE)) {

          switch (Integer.parseInt(questionBean.getFormat().get("maxFractionDigits").toString())) {
            case 0:
              updatedDestinationsList =
                  this.formatValueOfXByStringFormat(destinationsList, "%.0f", questionBean);
              break;
            case 1:
              updatedDestinationsList =
                  this.formatValueOfXByStringFormat(destinationsList, "%.1f", questionBean);
              break;
            case 2:
              updatedDestinationsList =
                  this.formatValueOfXByStringFormat(destinationsList, "%.2f", questionBean);
              break;
            case 3:
              updatedDestinationsList =
                  this.formatValueOfXByStringFormat(destinationsList, "%.3f", questionBean);
              break;
            case 4:
              updatedDestinationsList =
                  this.formatValueOfXByStringFormat(destinationsList, "%.4f", questionBean);
              break;
            default:
              break;
          }
        } else if (questionBean.getResultType().equals(StudyMetaDataConstants.QUESTION_NUMERIC)) {

          switch (questionBean.getFormat().get("style").toString()) {
            case StudyMetaDataConstants.QUESTION_NUMERIC_STYLE_INTEGER:
              updatedDestinationsList =
                  this.formatValueOfXByStringFormat(destinationsList, "%.0f", questionBean);
              break;
            case StudyMetaDataConstants.QUESTION_NUMERIC_STYLE_DECIMAL:
              updatedDestinationsList =
                  this.formatValueOfXByStringFormat(destinationsList, "%.4f", questionBean);
              break;
            default:
              break;
          }
        } else {
          updatedDestinationsList =
              this.formatValueOfXByStringFormat(destinationsList, "%.0f", questionBean);
        }
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - formatValueOfX() :: ERROR", e);
    }
    LOGGER.exit("formatValueOfX() :: Ends");
    return updatedDestinationsList;
  }

  public List<DestinationBean> formatValueOfXByStringFormat(
      List<DestinationBean> destinationsList,
      String stringFormat,
      QuestionnaireActivityStepsBean questionBean)
      throws DAOException {
    LOGGER.entry("begin formatValueOfXByStringFormat()");
    try {
      if ((destinationsList != null)
          && !destinationsList.isEmpty()
          && (destinationsList.size() >= 2)) {
        if (questionBean.getResultType().equals(StudyMetaDataConstants.QUESTION_TIME_INTERVAL)) {
          destinationsList
              .get(0)
              .setCondition(
                  String.format(
                      stringFormat,
                      Double.parseDouble(destinationsList.get(0).getCondition()) * 60));
          destinationsList
              .get(1)
              .setCondition(
                  String.format(
                      stringFormat,
                      Double.parseDouble(destinationsList.get(1).getCondition()) * 60));
        } else {
          destinationsList
              .get(0)
              .setCondition(
                  String.format(
                      stringFormat, Double.parseDouble(destinationsList.get(0).getCondition())));
          destinationsList
              .get(1)
              .setCondition(
                  String.format(
                      stringFormat, Double.parseDouble(destinationsList.get(1).getCondition())));
        }
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - formatValueOfXByStringFormat() :: ERROR", e);
    }
    LOGGER.exit("formatValueOfXByStringFormat() :: Ends");
    return destinationsList;
  }

  public String getDateRangeType(String dateRange) {
    LOGGER.entry("begin getDateRangeType()");
    String dateRangeType = "";
    try {
      switch (dateRange) {
        case StudyMetaDataConstants.WCP_DATE_RANGE_UNTILL_CURRENT:
          dateRangeType = StudyMetaDataConstants.DATE_RANGE_UNTILL_CURRENT;
          break;
        case StudyMetaDataConstants.WCP_DATE_RANGE_AFTER_CURRENT:
          dateRangeType = StudyMetaDataConstants.DATE_RANGE_AFTER_CURRENT;
          break;
        case StudyMetaDataConstants.WCP_DATE_RANGE_CUSTOM:
          dateRangeType = StudyMetaDataConstants.DATE_RANGE_CUSTOM;
          break;
        default:
          break;
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getDateRangeType() :: ERROR", e);
    }
    LOGGER.exit("getDateRangeType() :: Ends");
    return dateRangeType;
  }

  @SuppressWarnings("unchecked")
  public ActivitiesBean getAnchordateDetailsByActivityIdForActivetask(
      ActiveTaskDto activeTaskDto, ActivitiesBean activityBean, Session session)
      throws DAOException {
    LOGGER.entry("ActivityMetaDataDao - getAnchordateDetailsByActivityIdForQuestionnaire()");
    String searchQuery = "";
    try {
      ActivityAnchorDateBean activityAnchorDateBean = new ActivityAnchorDateBean();
      searchQuery = "from AnchorDateTypeDto a where a.id=:anchorDateId";
      AnchorDateTypeDto anchorDateTypeDto =
          (AnchorDateTypeDto)
              session
                  .createQuery(searchQuery)
                  .setString("anchorDateId", activeTaskDto.getAnchorDateId())
                  .uniqueResult();
      if (anchorDateTypeDto != null) {
        if (!anchorDateTypeDto
            .getName()
            .replace(" ", "")
            .equalsIgnoreCase(StudyMetaDataConstants.ANCHOR_TYPE_ENROLLMENTDATE)) {
          activityAnchorDateBean.setSourceType(StudyMetaDataConstants.ANCHOR_TYPE_ACTIVITYRESPONSE);
          searchQuery =
              "select s.step_short_title,qr.short_title"
                  + " from questionnaires qr,questions q, questionnaires_steps s"
                  + " where"
                  + " s.questionnaires_id=qr.id"
                  + " and s.instruction_form_id=q.id"
                  + " and s.step_type='Question'"
                  + " and qr.custom_study_id=:custStudyId"
                  + " and qr.schedule_type=:schedulerType"
                  + " and qr.frequency = :freqType"
                  + " and q.anchor_date_id=:anchorDateId";
          List<?> result =
              session
                  .createSQLQuery(searchQuery)
                  .setString("custStudyId", activeTaskDto.getCustomStudyId())
                  .setString("schedulerType", StudyMetaDataConstants.SCHEDULETYPE_REGULAR)
                  .setString("freqType", StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME)
                  .setString("anchorDateId", activeTaskDto.getAnchorDateId())
                  .list();
          if ((null != result) && !result.isEmpty()) {
            Object[] objects = (Object[]) result.get(0);
            activityAnchorDateBean.setSourceKey((String) objects[0]);
            activityAnchorDateBean.setSourceActivityId((String) objects[1]);
          } else {
            String query = "";
            query =
                "select q.shortTitle, qsf.stepShortTitle ,qq.shortTitle as questionnaireShort"
                    + " from QuestionsDto q,FormMappingDto fm,FormDto f,QuestionnairesStepsDto qsf,QuestionnairesDto qq"
                    + " where"
                    + " q.id=fm.questionId"
                    + " and f.formId=fm.formId"
                    + " and f.formId=qsf.instructionFormId"
                    + " and qsf.stepType='Form'"
                    + " and qsf.questionnairesId=qq.id"
                    + " and q.anchorDateId=:anchorDateId"
                    + " and qq.customStudyId=:custStudyId"
                    + " and qq.scheduleType=:schedulerType"
                    + " and qq.frequency = :freqType";
            List<?> result1 =
                session
                    .createQuery(query)
                    .setString("anchorDateId", activeTaskDto.getAnchorDateId())
                    .setString("custStudyId", activeTaskDto.getCustomStudyId())
                    .setString("schedulerType", StudyMetaDataConstants.SCHEDULETYPE_REGULAR)
                    .setString("freqType", StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME)
                    .list();
            if ((null != result1) && !result1.isEmpty()) {
              Object[] objects = (Object[]) result1.get(0);
              activityAnchorDateBean.setSourceKey((String) objects[0]);
              activityAnchorDateBean.setSourceFormKey((String) objects[1]);
              activityAnchorDateBean.setSourceActivityId((String) objects[2]);
            }
          }
        } else {
          activityAnchorDateBean.setSourceType(StudyMetaDataConstants.ANCHOR_TYPE_ENROLLMENTDATE);
        }
        ActivityAnchorStartBean start = new ActivityAnchorStartBean();
        ActivityAnchorEndBean end = new ActivityAnchorEndBean();
        if (activeTaskDto
            .getFrequency()
            .equals(StudyMetaDataConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {

          List<ActiveTaskCustomFrequenciesDto> manuallyScheduleFrequencyList =
              session
                  .createQuery(
                      "from ActiveTaskCustomFrequenciesDto QCFDTO"
                          + " where QCFDTO.activeTaskId=:activeTaskId"
                          + " order by QCFDTO.id")
                  .setString("activeTaskId", activeTaskDto.getId())
                  .list();
          if ((manuallyScheduleFrequencyList != null) && !manuallyScheduleFrequencyList.isEmpty()) {
            start.setAnchorDays(
                manuallyScheduleFrequencyList.get(0).isxDaysSign()
                    ? -manuallyScheduleFrequencyList.get(0).getTimePeriodFromDays()
                    : manuallyScheduleFrequencyList.get(0).getTimePeriodFromDays());
            start.setTime(manuallyScheduleFrequencyList.get(0).getFrequencyStartTime());
            end.setAnchorDays(
                manuallyScheduleFrequencyList
                        .get(manuallyScheduleFrequencyList.size() - 1)
                        .isyDaysSign()
                    ? -manuallyScheduleFrequencyList
                        .get(manuallyScheduleFrequencyList.size() - 1)
                        .getTimePeriodToDays()
                    : manuallyScheduleFrequencyList
                        .get(manuallyScheduleFrequencyList.size() - 1)
                        .getTimePeriodToDays());
            end.setTime(
                manuallyScheduleFrequencyList
                    .get(manuallyScheduleFrequencyList.size() - 1)
                    .getFrequencyEndTime());
          }
        } else if (activeTaskDto
            .getFrequency()
            .equals(StudyMetaDataConstants.FREQUENCY_TYPE_DAILY)) {
          List<ActiveTaskFrequencyDto> taskFrequencyDtoList =
              session
                  .createQuery(
                      "from ActiveTaskFrequencyDto QCFDTO"
                          + " where QCFDTO.activeTaskId=:activeTaskId"
                          + " order by QCFDTO.id")
                  .setString("activeTaskId", activeTaskDto.getId())
                  .list();

          if ((taskFrequencyDtoList != null) && (taskFrequencyDtoList.size() > 0)) {
            start.setTime(taskFrequencyDtoList.get(0).getFrequencyTime());
            end.setRepeatInterval(
                activeTaskDto.getRepeatActiveTask() == null
                    ? 0
                    : activeTaskDto.getRepeatActiveTask());
            end.setAnchorDays(0);
            end.setTime(StudyMetaDataConstants.DEFAULT_MAX_TIME);
          }

        } else {

          ActiveTaskFrequencyDto taskFrequencyDto =
              (ActiveTaskFrequencyDto)
                  session
                      .createQuery(
                          "from ActiveTaskFrequencyDto QFDTO"
                              + " where QFDTO.activeTaskId=:activeTaskId")
                      .setString("activeTaskId", activeTaskDto.getId())
                      .uniqueResult();
          if (taskFrequencyDto != null) {
            if (taskFrequencyDto.getTimePeriodFromDays() != null) {
              start.setAnchorDays(
                  taskFrequencyDto.isxDaysSign()
                      ? -taskFrequencyDto.getTimePeriodFromDays()
                      : taskFrequencyDto.getTimePeriodFromDays());
            }
            if (activeTaskDto.getFrequency().equals(StudyMetaDataConstants.FREQUENCY_TYPE_MONTHLY)
                || activeTaskDto
                    .getFrequency()
                    .equals(StudyMetaDataConstants.FREQUENCY_TYPE_WEEKLY)) {
              end.setTime(taskFrequencyDto.getFrequencyTime());
            }
            start.setTime(taskFrequencyDto.getFrequencyTime());

            end.setRepeatInterval(
                activeTaskDto.getRepeatActiveTask() == null
                    ? 0
                    : activeTaskDto.getRepeatActiveTask());
            if (activeTaskDto
                .getFrequency()
                .equals(StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME)) {
              if (taskFrequencyDto.isLaunchStudy()) {
                start = null;
              }
              if (taskFrequencyDto.isStudyLifeTime()) {
                end = null;
              } else {
                end.setAnchorDays(
                    taskFrequencyDto.isyDaysSign()
                        ? -taskFrequencyDto.getTimePeriodToDays()
                        : taskFrequencyDto.getTimePeriodToDays());
                end.setTime(StudyMetaDataConstants.DEFAULT_MAX_TIME);
              }
            }
          }
        }
        activityAnchorDateBean.setStart(start);
        activityAnchorDateBean.setEnd(end);
        activityBean.setAnchorDate(activityAnchorDateBean);
      }

    } catch (Exception e) {
      LOGGER.error(
          "ActivityMetaDataDao - getAnchordateDetailsByActivityIdForQuestionnaire() :: ERROR", e);
    }
    LOGGER.exit("getAnchordateDetailsByActivityIdForQuestionnaire() :: Ends");
    return activityBean;
  }

  @SuppressWarnings("unchecked")
  public List<ActivityFrequencyAnchorRunsBean>
      getQuestionnaireFrequencyAncorDetailsForManuallySchedule(
          QuestionnairesDto questionaire,
          List<ActivityFrequencyAnchorRunsBean> anchorRunDetailsBean,
          Session session)
          throws DAOException {
    LOGGER.entry(
        "ActivityMetaDataDao - getQuestionnaireFrequencyAncorDetailsForManuallySchedule()");
    try {
      List<QuestionnairesCustomFrequenciesDto> manuallyScheduleFrequencyList =
          session
              .createQuery(
                  "from QuestionnairesCustomFrequenciesDto QCFDTO"
                      + " where QCFDTO.questionnairesId=:quesResId")
              .setString("quesResId", questionaire.getId())
              .list();
      if ((manuallyScheduleFrequencyList != null) && !manuallyScheduleFrequencyList.isEmpty()) {
        for (QuestionnairesCustomFrequenciesDto customFrequencyDto :
            manuallyScheduleFrequencyList) {
          ActivityFrequencyAnchorRunsBean activityFrequencyAnchorRunsBean =
              new ActivityFrequencyAnchorRunsBean();
          activityFrequencyAnchorRunsBean.setStartDays(
              customFrequencyDto.isxDaysSign()
                  ? -customFrequencyDto.getTimePeriodFromDays()
                  : customFrequencyDto.getTimePeriodFromDays());

          activityFrequencyAnchorRunsBean.setStartTime(customFrequencyDto.getFrequencyStartTime());

          activityFrequencyAnchorRunsBean.setEndDays(
              customFrequencyDto.isyDaysSign()
                  ? -customFrequencyDto.getTimePeriodToDays()
                  : customFrequencyDto.getTimePeriodToDays());
          activityFrequencyAnchorRunsBean.setEndTime(customFrequencyDto.getFrequencyEndTime());
          anchorRunDetailsBean.add(activityFrequencyAnchorRunsBean);
        }
      }
    } catch (Exception e) {
      LOGGER.error(
          "ActivityMetaDataDao - getQuestionnaireFrequencyAncorDetailsForManuallySchedule() :: ERROR",
          e);
    }
    LOGGER.exit(
        "ActivityMetaDataDao - getQuestionnaireFrequencyAncorDetailsForManuallySchedule() :: Ends");
    return anchorRunDetailsBean;
  }

  @SuppressWarnings("unchecked")
  public ActivitiesBean getAnchordateDetailsByActivityIdForQuestionnaire(
      QuestionnairesDto questionaire, ActivitiesBean activityBean, Session session)
      throws DAOException {
    LOGGER.entry("ActivityMetaDataDao - getAnchordateDetailsByActivityIdForQuestionnaire()");
    String searchQuery = "";
    try {
      ActivityAnchorDateBean activityAnchorDateBean = new ActivityAnchorDateBean();
      searchQuery = "from AnchorDateTypeDto a where a.id=:id";
      AnchorDateTypeDto anchorDateTypeDto =
          (AnchorDateTypeDto)
              session
                  .createQuery(searchQuery)
                  .setParameter("id", questionaire.getAnchorDateId())
                  .uniqueResult();
      if (anchorDateTypeDto != null) {
        if (!anchorDateTypeDto
            .getName()
            .replace(" ", "")
            .equalsIgnoreCase(StudyMetaDataConstants.ANCHOR_TYPE_ENROLLMENTDATE)) {
          activityAnchorDateBean.setSourceType(StudyMetaDataConstants.ANCHOR_TYPE_ACTIVITYRESPONSE);
          searchQuery =
              "select s.step_short_title,qr.short_title"
                  + " from questionnaires qr,questions q, questionnaires_steps s"
                  + " where"
                  + " s.questionnaires_id=qr.id"
                  + " and s.instruction_form_id=q.id"
                  + " and s.step_type='Question'"
                  + " and qr.custom_study_id=:customStudyId"
                  + " and qr.schedule_type=:scheduleType"
                  + " and qr.frequency = :frequencyType"
                  + " and q.anchor_date_id=:anchorDateId";
          List<?> result =
              session
                  .createSQLQuery(searchQuery)
                  .setString("customStudyId", questionaire.getCustomStudyId())
                  .setString("scheduleType", StudyMetaDataConstants.SCHEDULETYPE_REGULAR)
                  .setString("frequencyType", StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME)
                  .setString("anchorDateId", questionaire.getAnchorDateId())
                  .list();
          if ((null != result) && !result.isEmpty()) {
            Object[] objects = (Object[]) result.get(0);
            activityAnchorDateBean.setSourceKey((String) objects[0]);
            activityAnchorDateBean.setSourceActivityId((String) objects[1]);
          } else {
            String query = "";
            query =
                "select q.shortTitle, qsf.stepShortTitle ,qq.shortTitle as questionnaireShort"
                    + " from QuestionsDto q,FormMappingDto fm,FormDto f,QuestionnairesStepsDto qsf,QuestionnairesDto qq"
                    + " where"
                    + " q.id=fm.questionId"
                    + " and f.formId=fm.formId"
                    + " and f.formId=qsf.instructionFormId"
                    + " and qsf.stepType='Form'"
                    + " and qsf.questionnairesId=qq.id"
                    + " and q.anchorDateId=:anchorDateId"
                    + " and qq.customStudyId=:customStudyId"
                    + " and qq.scheduleType=:scheduleType"
                    + " and qq.frequency = :frequencyType";
            List<?> result1 =
                session
                    .createQuery(query)
                    .setString("anchorDateId", questionaire.getAnchorDateId())
                    .setString("customStudyId", questionaire.getCustomStudyId())
                    .setString("scheduleType", StudyMetaDataConstants.SCHEDULETYPE_REGULAR)
                    .setString("frequencyType", StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME)
                    .list();
            if ((null != result1) && !result1.isEmpty()) {
              Object[] objects = (Object[]) result1.get(0);
              activityAnchorDateBean.setSourceKey((String) objects[0]);
              activityAnchorDateBean.setSourceFormKey((String) objects[1]);
              activityAnchorDateBean.setSourceActivityId((String) objects[2]);
            }
          }
        } else {
          activityAnchorDateBean.setSourceType(StudyMetaDataConstants.ANCHOR_TYPE_ENROLLMENTDATE);
        }
        ActivityAnchorStartBean start = new ActivityAnchorStartBean();
        ActivityAnchorEndBean end = new ActivityAnchorEndBean();
        if (questionaire
            .getFrequency()
            .equals(StudyMetaDataConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {

          List<QuestionnairesCustomFrequenciesDto> manuallyScheduleFrequencyList =
              session
                  .createQuery(
                      "from QuestionnairesCustomFrequenciesDto QCFDTO"
                          + " where QCFDTO.questionnairesId=:questId"
                          + " order by QCFDTO.id")
                  .setString("questId", questionaire.getId())
                  .list();
          if ((manuallyScheduleFrequencyList != null) && !manuallyScheduleFrequencyList.isEmpty()) {
            start.setAnchorDays(
                manuallyScheduleFrequencyList.get(0).isxDaysSign()
                    ? -manuallyScheduleFrequencyList.get(0).getTimePeriodFromDays()
                    : manuallyScheduleFrequencyList.get(0).getTimePeriodFromDays());
            start.setTime(manuallyScheduleFrequencyList.get(0).getFrequencyStartTime());
            end.setAnchorDays(
                manuallyScheduleFrequencyList
                        .get(manuallyScheduleFrequencyList.size() - 1)
                        .isyDaysSign()
                    ? -manuallyScheduleFrequencyList
                        .get(manuallyScheduleFrequencyList.size() - 1)
                        .getTimePeriodToDays()
                    : manuallyScheduleFrequencyList
                        .get(manuallyScheduleFrequencyList.size() - 1)
                        .getTimePeriodToDays());
            end.setTime(
                manuallyScheduleFrequencyList
                    .get(manuallyScheduleFrequencyList.size() - 1)
                    .getFrequencyEndTime());
          }
        } else if (questionaire
            .getFrequency()
            .equals(StudyMetaDataConstants.FREQUENCY_TYPE_DAILY)) {
          List<QuestionnairesFrequenciesDto> QuestionnairesFrequenciesDtoList =
              session
                  .createQuery(
                      "from QuestionnairesFrequenciesDto QCFDTO"
                          + " where QCFDTO.questionnairesId=:questId"
                          + " order by QCFDTO.id")
                  .setString("questId", questionaire.getId())
                  .list();

          if ((QuestionnairesFrequenciesDtoList != null)
              && (QuestionnairesFrequenciesDtoList.size() > 0)) {
            start.setTime(QuestionnairesFrequenciesDtoList.get(0).getFrequencyTime());
            end.setRepeatInterval(
                questionaire.getRepeatQuestionnaire() == null
                    ? 0
                    : questionaire.getRepeatQuestionnaire());
            start.setAnchorDays(
                QuestionnairesFrequenciesDtoList.get(0).isxDaysSign()
                    ? -QuestionnairesFrequenciesDtoList.get(0).getTimePeriodFromDays()
                    : QuestionnairesFrequenciesDtoList.get(0).getTimePeriodFromDays());
            end.setAnchorDays(0);
            end.setTime(StudyMetaDataConstants.DEFAULT_MAX_TIME);
          }

        } else {
          QuestionnairesFrequenciesDto questionnairesFrequency =
              (QuestionnairesFrequenciesDto)
                  session
                      .createQuery(
                          "from QuestionnairesFrequenciesDto QFDTO"
                              + " where QFDTO.questionnairesId=:questId")
                      .setString("questId", questionaire.getId())
                      .uniqueResult();
          if (questionnairesFrequency != null) {
            if (questionnairesFrequency.getTimePeriodFromDays() != null) {
              start.setAnchorDays(
                  questionnairesFrequency.isxDaysSign()
                      ? -questionnairesFrequency.getTimePeriodFromDays()
                      : questionnairesFrequency.getTimePeriodFromDays());
            }
            if (questionaire.getFrequency().equals(StudyMetaDataConstants.FREQUENCY_TYPE_MONTHLY)
                || questionaire
                    .getFrequency()
                    .equals(StudyMetaDataConstants.FREQUENCY_TYPE_WEEKLY)) {
              end.setTime(questionnairesFrequency.getFrequencyTime());
            }
            start.setTime(questionnairesFrequency.getFrequencyTime());

            end.setRepeatInterval(
                questionaire.getRepeatQuestionnaire() == null
                    ? 0
                    : questionaire.getRepeatQuestionnaire());
            if (questionaire
                .getFrequency()
                .equals(StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME)) {
              if (questionnairesFrequency.getIsLaunchStudy()) {
                start = null;
              }
              if (questionnairesFrequency.getIsStudyLifeTime()) {
                end = null;
              } else {
                end.setAnchorDays(
                    questionnairesFrequency.isyDaysSign()
                        ? -questionnairesFrequency.getTimePeriodToDays()
                        : questionnairesFrequency.getTimePeriodToDays());
                end.setTime(StudyMetaDataConstants.DEFAULT_MAX_TIME);
              }
            }
          }
        }
        activityAnchorDateBean.setStart(start);
        activityAnchorDateBean.setEnd(end);
        activityBean.setAnchorDate(activityAnchorDateBean);
      }

    } catch (Exception e) {
      LOGGER.error(
          "ActivityMetaDataDao - getAnchordateDetailsByActivityIdForQuestionnaire() :: ERROR", e);
    }
    LOGGER.exit("getAnchordateDetailsByActivityIdForQuestionnaire() :: Ends");
    return activityBean;
  }

  @SuppressWarnings("unchecked")
  public List<ActivityFrequencyAnchorRunsBean> getAcivetaskFrequencyAncorDetailsForManuallySchedule(
      ActiveTaskDto activeTaskDto,
      List<ActivityFrequencyAnchorRunsBean> anchorRunDetailsBean,
      Session session)
      throws DAOException {
    LOGGER.entry("begin getAcivetaskFrequencyAncorDetailsForManuallySchedule()");
    try {
      List<ActiveTaskCustomFrequenciesDto> manuallyScheduleFrequencyList =
          session
              .createQuery(
                  "from ActiveTaskCustomFrequenciesDto QCFDTO"
                      + " where QCFDTO.activeTaskId=:activeTaskId")
              .setString("activeTaskId", activeTaskDto.getId())
              .list();
      if ((manuallyScheduleFrequencyList != null) && !manuallyScheduleFrequencyList.isEmpty()) {
        for (ActiveTaskCustomFrequenciesDto customFrequencyDto : manuallyScheduleFrequencyList) {
          ActivityFrequencyAnchorRunsBean activityFrequencyAnchorRunsBean =
              new ActivityFrequencyAnchorRunsBean();
          activityFrequencyAnchorRunsBean.setStartDays(
              customFrequencyDto.isxDaysSign()
                  ? -customFrequencyDto.getTimePeriodFromDays()
                  : customFrequencyDto.getTimePeriodFromDays());

          activityFrequencyAnchorRunsBean.setStartTime(customFrequencyDto.getFrequencyStartTime());

          activityFrequencyAnchorRunsBean.setEndDays(
              customFrequencyDto.isyDaysSign()
                  ? -customFrequencyDto.getTimePeriodToDays()
                  : customFrequencyDto.getTimePeriodToDays());
          activityFrequencyAnchorRunsBean.setEndTime(customFrequencyDto.getFrequencyEndTime());
          anchorRunDetailsBean.add(activityFrequencyAnchorRunsBean);
        }
      }
    } catch (Exception e) {
      LOGGER.error(
          "ActivityMetaDataDao - getAcivetaskFrequencyAncorDetailsForManuallySchedule() :: ERROR",
          e);
    }
    LOGGER.exit("getAcivetaskFrequencyAncorDetailsForManuallySchedule() :: Ends");
    return anchorRunDetailsBean;
  }
}
