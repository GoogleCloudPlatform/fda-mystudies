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

import com.hphc.mystudies.bean.ChartDataSourceBean;
import com.hphc.mystudies.bean.ChartsBean;
import com.hphc.mystudies.bean.DashboardActivityBean;
import com.hphc.mystudies.bean.DashboardBean;
import com.hphc.mystudies.bean.StatisticsBean;
import com.hphc.mystudies.bean.StatisticsDataSourceBean;
import com.hphc.mystudies.bean.StudyDashboardResponse;
import com.hphc.mystudies.dto.ActiveTaskAttrtibutesValuesDto;
import com.hphc.mystudies.dto.ActiveTaskCustomFrequenciesDto;
import com.hphc.mystudies.dto.ActiveTaskDto;
import com.hphc.mystudies.dto.ActiveTaskFormulaDto;
import com.hphc.mystudies.dto.ActiveTaskFrequencyDto;
import com.hphc.mystudies.dto.ActiveTaskMasterAttributeDto;
import com.hphc.mystudies.dto.FormDto;
import com.hphc.mystudies.dto.FormMappingDto;
import com.hphc.mystudies.dto.QuestionnairesCustomFrequenciesDto;
import com.hphc.mystudies.dto.QuestionnairesDto;
import com.hphc.mystudies.dto.QuestionnairesFrequenciesDto;
import com.hphc.mystudies.dto.QuestionnairesStepsDto;
import com.hphc.mystudies.dto.QuestionsDto;
import com.hphc.mystudies.dto.StatisticImageListDto;
import com.hphc.mystudies.dto.StudyDto;
import com.hphc.mystudies.dto.StudyVersionDto;
import com.hphc.mystudies.exception.DAOException;
import com.hphc.mystudies.util.HibernateUtil;
import com.hphc.mystudies.util.StudyMetaDataConstants;
import com.hphc.mystudies.util.StudyMetaDataEnum;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class DashboardMetaDataDao {

  private static final XLogger LOGGER =
      XLoggerFactory.getXLogger(DashboardMetaDataDao.class.getName());

  SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
  Query query = null;

  @SuppressWarnings("unchecked")
  public StudyDashboardResponse studyDashboardInfo(String studyId) throws DAOException {
    LOGGER.entry("begin studyDashboardInfo()");
    Session session = null;
    StudyDashboardResponse studyDashboardResponse = new StudyDashboardResponse();
    DashboardBean dashboard = new DashboardBean();
    List<ChartsBean> chartsList = new ArrayList<>();
    List<StatisticsBean> statisticsList = new ArrayList<>();
    Map<String, Object> activityMap = new LinkedHashMap<>();
    Map<String, Object> questionnaireMap = new LinkedHashMap<>();
    List<String> activeTaskIdsList = new ArrayList<>();
    List<String> questionnaireIdsList = new ArrayList<>();
    List<ActiveTaskDto> activeTaskList = null;
    List<QuestionnairesDto> questionnaireList = null;
    List<ActiveTaskAttrtibutesValuesDto> activeTaskValuesList = null;
    List<QuestionnairesStepsDto> questionnaireStepsList = null;
    List<String> questionIdsList = new ArrayList<>();
    List<String> formIdsList = new ArrayList<>();
    List<ActiveTaskFormulaDto> formulaDtoList = null;
    List<StatisticImageListDto> statisticImageList = null;
    StudyDto studyDto = null;
    StudyVersionDto studyVersionDto = null;
    try {
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

        activeTaskList =
            session
                .getNamedQuery("getActiveTaskDetailsByCustomStudyId")
                .setString(
                    StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(),
                    studyVersionDto.getCustomStudyId())
                .setInteger(StudyMetaDataEnum.QF_LIVE.value(), 1)
                .setInteger(StudyMetaDataEnum.QF_ACTIVE.value(), 0)
                .list();
        if ((activeTaskList != null) && !activeTaskList.isEmpty()) {
          for (ActiveTaskDto activeTask : activeTaskList) {
            boolean addToDashboardFlag = false;
            activeTask = this.getTimeDetailsByActivityIdForActiveTask(activeTask, session);
            if ((activeTask.getActive() != null) && (activeTask.getActive() == 1)) {
              addToDashboardFlag = true;
            } else {
              if (StringUtils.isNotEmpty(activeTask.getActiveTaskLifetimeStart())
                  && StudyMetaDataConstants.SDF_DATE
                      .parse(activeTask.getModifiedDate())
                      .after(
                          StudyMetaDataConstants.SDF_DATE.parse(
                              activeTask.getActiveTaskLifetimeStart()))) {
                addToDashboardFlag = true;
              }
            }

            if (addToDashboardFlag) {
              activityMap.put(
                  StudyMetaDataConstants.ACTIVITY_TYPE_ACTIVE_TASK + "-" + activeTask.getId(),
                  activeTask);
              activeTaskIdsList.add(activeTask.getId());
            }
          }
        }

        questionnaireList =
            session
                .getNamedQuery("getQuestionnaireDetailsByCustomStudyId")
                .setString(
                    StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(),
                    studyVersionDto.getCustomStudyId())
                .setInteger(StudyMetaDataEnum.QF_LIVE.value(), 1)
                .setBoolean(StudyMetaDataEnum.QF_ACTIVE.value(), false)
                .list();
        if ((questionnaireList != null) && !questionnaireList.isEmpty()) {
          for (QuestionnairesDto questionnaire : questionnaireList) {
            boolean addToDashboardFlag = false;
            QuestionnairesDto questionnaireDto =
                this.getTimeDetailsByActivityIdForQuestionnaire(questionnaire, session);
            if (questionnaireDto.getActive()) {
              addToDashboardFlag = true;
            } else {
              if (questionnaire.getStudyLifetimeStart() == null) {
                if (StudyMetaDataConstants.SDF_DATE
                    .parse(questionnaireDto.getModifiedDate())
                    .after(
                        StudyMetaDataConstants.SDF_DATE.parse(
                            questionnaireDto.getStudyLifetimeStart()))) {
                  addToDashboardFlag = true;
                }
              }
            }

            if (addToDashboardFlag) {
              questionnaireMap.put(
                  StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE
                      + "-"
                      + questionnaireDto.getId(),
                  questionnaireDto);
              questionnaireIdsList.add(questionnaireDto.getId());
            }
          }

          if ((questionnaireIdsList != null) && !questionnaireIdsList.isEmpty()) {

            questionnaireStepsList =
                session
                    .createQuery(
                        "from QuestionnairesStepsDto QSDTO"
                            + " where QSDTO.questionnairesId in (:questionnaireIdsList)"
                            + " and QSDTO.stepType in (:QuesStepTypeQue,:QuesStepTypeForm)"
                            + " and QSDTO.status=true"
                            + " ORDER BY QSDTO.questionnairesId, QSDTO.sequenceNo")
                    .setParameterList("questionnaireIdsList", questionnaireIdsList)
                    .setString(
                        "QuesStepTypeQue", StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION)
                    .setString(
                        "QuesStepTypeForm", StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM)
                    .list();
            if ((questionnaireStepsList != null) && !questionnaireStepsList.isEmpty()) {
              for (QuestionnairesStepsDto questionnaireSteps : questionnaireStepsList) {

                if (questionnaireSteps
                    .getStepType()
                    .equalsIgnoreCase(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION)) {
                  questionIdsList.add(questionnaireSteps.getInstructionFormId());
                }

                if (questionnaireSteps
                    .getStepType()
                    .equalsIgnoreCase(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM)) {
                  formIdsList.add(questionnaireSteps.getInstructionFormId());
                }
                activityMap.put(
                    StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE
                        + "-"
                        + questionnaireSteps.getInstructionFormId()
                        + "-"
                        + questionnaireSteps.getStepType(),
                    questionnaireSteps);
              }
            }
          }
        }

        formulaDtoList = session.createQuery("from ActiveTaskFormulaDto ATCDTO").list();

        statisticImageList = session.createQuery(" from StatisticImageListDto STDTO").list();

        if ((activeTaskIdsList != null) && !activeTaskIdsList.isEmpty()) {
          activeTaskValuesList =
              session
                  .createQuery(
                      "from ActiveTaskAttrtibutesValuesDto ATAVDTO"
                          + " where ATAVDTO.addToLineChart=true or ATAVDTO.useForStatistic=true"
                          + " and ATAVDTO.activeTaskId in (:activeTaskIdsList)")
                  .setParameterList("activeTaskIdsList", activeTaskIdsList)
                  .list();
          if ((activeTaskValuesList != null) && !activeTaskValuesList.isEmpty()) {
            int taskTypeId = 0;
            Map<String, String> activeTaskMasterAttrIdsMap = new HashMap<>();
            Map<String, String> activeTaskMasterAttrIdNameMap = new HashMap<>();
            List<String> activeTaskMasterAttrIdList = new ArrayList<>();

            for (ActiveTaskAttrtibutesValuesDto activeTaskAttrDto : activeTaskValuesList) {
              activeTaskMasterAttrIdsMap.put(
                  activeTaskAttrDto.getActiveTaskMasterAttrId(),
                  activeTaskAttrDto.getActiveTaskMasterAttrId());
            }

            for (String activeTaskMasterAttrId : activeTaskMasterAttrIdsMap.keySet()) {
              activeTaskMasterAttrIdList.add(activeTaskMasterAttrId);
            }

            if (!activeTaskMasterAttrIdList.isEmpty()) {
              List<ActiveTaskMasterAttributeDto> activeTaskMasterAttributeList =
                  session
                      .createQuery(
                          "from ActiveTaskMasterAttributeDto ATMADTO"
                              + " where ATMADTO.masterId IN (:activeTaskMasterAttrIdList)")
                      .setParameterList("activeTaskMasterAttrIdList", activeTaskMasterAttrIdList)
                      .list();
              if ((null != activeTaskMasterAttributeList)
                  && !activeTaskMasterAttributeList.isEmpty()) {
                for (ActiveTaskMasterAttributeDto atmt : activeTaskMasterAttributeList) {
                  activeTaskMasterAttrIdNameMap.put(atmt.getMasterId(), atmt.getDisplayName());
                }
              }
            }

            for (ActiveTaskAttrtibutesValuesDto activeTaskAttrDto : activeTaskValuesList) {
              ActiveTaskDto activeTaskDto = null;
              taskTypeId = 0;
              activeTaskDto =
                  (ActiveTaskDto)
                      activityMap.get(
                          StudyMetaDataConstants.ACTIVITY_TYPE_ACTIVE_TASK
                              + "-"
                              + activeTaskAttrDto.getActiveTaskId());
              if (activeTaskDto != null) {
                if ((null != activeTaskDto.getTaskTypeId())
                    && ("3".equals(activeTaskDto.getTaskTypeId()))) {
                  taskTypeId = 3;
                }
                activeTaskAttrDto.setActivityType(StudyMetaDataConstants.DASHBOARD_ACTIVE_TASK);
                activeTaskAttrDto.setActivityStepKey(
                    StringUtils.isEmpty(activeTaskDto.getShortTitle())
                        ? ""
                        : activeTaskDto.getShortTitle());
                activeTaskAttrDto.setActivityVersion(
                    activeTaskDto.getVersion() == null
                        ? StudyMetaDataConstants.STUDY_DEFAULT_VERSION
                        : activeTaskDto.getVersion().toString());
                activeTaskAttrDto.setActivityId(activeTaskDto.getShortTitle());
                if (activeTaskAttrDto.isAddToLineChart()) {
                  chartsList =
                      this.getChartDetails(
                          StudyMetaDataConstants.ACTIVITY_TYPE_ACTIVE_TASK,
                          activeTaskAttrDto,
                          null,
                          chartsList,
                          activeTaskDto.getShortTitle(),
                          taskTypeId,
                          activeTaskMasterAttrIdNameMap);
                }

                if (activeTaskAttrDto.isUseForStatistic()) {
                  statisticsList =
                      this.getStatisticsDetails(
                          StudyMetaDataConstants.ACTIVITY_TYPE_ACTIVE_TASK,
                          activeTaskAttrDto,
                          null,
                          statisticsList,
                          formulaDtoList,
                          statisticImageList,
                          taskTypeId,
                          activeTaskMasterAttrIdNameMap);
                }
              }
            }
          }
        }

        if ((questionIdsList != null) && !questionIdsList.isEmpty()) {
          List<QuestionsDto> questionsList;
          questionsList =
              session
                  .createQuery(
                      " from QuestionsDto QDTO"
                          + " where QDTO.id in (:questionIdsList) and QDTO.status=true"
                          + " and QDTO.addLineChart=:addLineChart"
                          + " or QDTO.useStasticData=:useStasticData")
                  .setParameterList("questionIdsList", questionIdsList)
                  .setString("addLineChart", StudyMetaDataConstants.YES)
                  .setString("useStasticData", StudyMetaDataConstants.YES)
                  .list();
          for (QuestionsDto questionDto : questionsList) {
            QuestionnairesStepsDto questionnaireSteps =
                (QuestionnairesStepsDto)
                    activityMap.get(
                        StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE
                            + "-"
                            + questionDto.getId()
                            + "-"
                            + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION);
            if (questionnaireSteps != null) {
              QuestionnairesDto questionnaireDto =
                  (QuestionnairesDto)
                      questionnaireMap.get(
                          StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE
                              + "-"
                              + questionnaireSteps.getQuestionnairesId());
              if (questionnaireDto != null) {
                questionDto.setActivityType(StudyMetaDataConstants.DASHBOARD_QUESTIONNAIRE);
                questionDto.setActivityStepKey(
                    StringUtils.isEmpty(questionnaireSteps.getStepShortTitle())
                        ? ""
                        : questionnaireSteps.getStepShortTitle());
                questionDto.setActivityVersion(
                    questionnaireDto.getVersion() == null
                        ? StudyMetaDataConstants.STUDY_DEFAULT_VERSION
                        : questionnaireDto.getVersion().toString());
                questionDto.setActivityId(questionnaireDto.getShortTitle());
                if (questionDto.getAddLineChart().equalsIgnoreCase(StudyMetaDataConstants.YES)) {
                  chartsList =
                      this.getChartDetails(
                          StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE,
                          null,
                          questionDto,
                          chartsList,
                          questionnaireSteps.getStepShortTitle(),
                          0,
                          null);
                }

                if (questionDto.getUseStasticData().equalsIgnoreCase(StudyMetaDataConstants.YES)) {
                  statisticsList =
                      this.getStatisticsDetails(
                          StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE,
                          null,
                          questionDto,
                          statisticsList,
                          formulaDtoList,
                          statisticImageList,
                          0,
                          null);
                }
              }
            }
          }
        }

        if ((formIdsList != null) && !formIdsList.isEmpty()) {
          List<FormDto> formDtoList = null;
          formDtoList =
              session
                  .createQuery("from FormDto FDTO where FDTO.formId in (:formIdsList)")
                  .setParameterList("formIdsList", formIdsList)
                  .list();
          if ((formDtoList != null) && !formDtoList.isEmpty()) {
            for (FormDto form : formDtoList) {
              List<String> formQuestionIdsList = new ArrayList<>();
              List<FormMappingDto> formMappingDtoList;
              formMappingDtoList =
                  session
                      .createQuery(
                          "from FormMappingDto FMDTO"
                              + " where FMDTO.formId=:formId"
                              + " order by FMDTO.sequenceNo")
                      .setString("formId", form.getFormId())
                      .list();
              if ((formMappingDtoList != null) && !formMappingDtoList.isEmpty()) {
                for (FormMappingDto formMappingDto : formMappingDtoList) {
                  formQuestionIdsList.add(formMappingDto.getQuestionId());
                }
              }

              if (!formQuestionIdsList.isEmpty()) {
                List<QuestionsDto> formQuestionDtoList = null;
                formQuestionDtoList =
                    session
                        .createQuery(
                            "from QuestionsDto FQDTO"
                                + " where FQDTO.id in (:formQuestionIdsList)"
                                + " and FQDTO.status=true"
                                + " and FQDTO.addLineChart=:addLineChart"
                                + " or FQDTO.useStasticData=:useStasticData")
                        .setParameterList("formQuestionIdsList", formQuestionIdsList)
                        .setString("addLineChart", StudyMetaDataConstants.YES)
                        .setString("useStasticData", StudyMetaDataConstants.YES)
                        .list();
                if ((formQuestionDtoList != null) && !formQuestionDtoList.isEmpty()) {
                  for (QuestionsDto questionDto : formQuestionDtoList) {
                    if (formQuestionIdsList.contains(questionDto.getId())) {
                      QuestionnairesStepsDto questionnaireSteps =
                          (QuestionnairesStepsDto)
                              activityMap.get(
                                  StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE
                                      + "-"
                                      + form.getFormId()
                                      + "-"
                                      + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM);
                      if (questionnaireSteps != null) {
                        QuestionnairesDto questionnaireDto =
                            (QuestionnairesDto)
                                questionnaireMap.get(
                                    StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE
                                        + "-"
                                        + questionnaireSteps.getQuestionnairesId());
                        if (questionnaireDto != null) {
                          questionDto.setActivityType(
                              StudyMetaDataConstants.DASHBOARD_QUESTIONNAIRE);
                          questionDto.setActivityStepKey(
                              StringUtils.isEmpty(questionDto.getShortTitle())
                                  ? ""
                                  : questionDto.getShortTitle());
                          questionDto.setActivityVersion(
                              questionnaireDto.getVersion() == null
                                  ? StudyMetaDataConstants.STUDY_DEFAULT_VERSION
                                  : questionnaireDto.getVersion().toString());
                          questionDto.setActivityId(questionnaireDto.getShortTitle());
                          if (questionDto
                              .getAddLineChart()
                              .equalsIgnoreCase(StudyMetaDataConstants.YES)) {
                            chartsList =
                                this.getChartDetails(
                                    StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE,
                                    null,
                                    questionDto,
                                    chartsList,
                                    questionDto.getShortTitle(),
                                    0,
                                    null);
                          }

                          if (questionDto
                              .getUseStasticData()
                              .equalsIgnoreCase(StudyMetaDataConstants.YES)) {
                            statisticsList =
                                this.getStatisticsDetails(
                                    StudyMetaDataConstants.ACTIVITY_TYPE_QUESTIONAIRE,
                                    null,
                                    questionDto,
                                    statisticsList,
                                    formulaDtoList,
                                    statisticImageList,
                                    0,
                                    null);
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

        dashboard.setCharts(chartsList);
        dashboard.setStatistics(statisticsList);
        studyDashboardResponse.setDashboard(dashboard);
        studyDashboardResponse.setMessage(StudyMetaDataConstants.SUCCESS);
      }
    } catch (Exception e) {
      LOGGER.error("DashboardMetaDataDao - studyDashboardInfo() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.exit("studyDashboardInfo() :: Ends");
    return studyDashboardResponse;
  }

  @SuppressWarnings("rawtypes")
  public List<ChartsBean> getChartDetails(
      String activityType,
      ActiveTaskAttrtibutesValuesDto activeTask,
      QuestionsDto question,
      List<ChartsBean> chartsList,
      String chartTitle,
      int taskTypeId,
      Map activeTaskMasterAttrIdNameMap)
      throws DAOException {
    LOGGER.entry("begin getChartDetails()");
    ChartsBean chart = new ChartsBean();
    ChartDataSourceBean dataSource = new ChartDataSourceBean();
    DashboardActivityBean activity = new DashboardActivityBean();
    try {
      if (activityType.equalsIgnoreCase(StudyMetaDataConstants.ACTIVITY_TYPE_ACTIVE_TASK)) {
        chart.setTitle(chartTitle);
        chart.setDisplayName(
            StringUtils.isEmpty(activeTask.getTitleChat()) ? "" : activeTask.getTitleChat());
        chart.setType(StudyMetaDataConstants.CHART_TYPE_LINE);
        chart.setScrollable(
            (StringUtils.isNotEmpty(activeTask.getRollbackChat())
                    && activeTask.getRollbackChat().equalsIgnoreCase(StudyMetaDataConstants.YES))
                ? true
                : false);

        chart.setConfiguration(this.singleBarChartDetails());

        dataSource.setType(activeTask.getActivityType());

        if ((3 == taskTypeId)
            && (null != activeTaskMasterAttrIdNameMap)
            && (null
                != activeTaskMasterAttrIdNameMap.get(activeTask.getActiveTaskMasterAttrId()))) {
          dataSource.setKey(
              String.valueOf(
                      activeTaskMasterAttrIdNameMap.get(activeTask.getActiveTaskMasterAttrId()))
                  .replaceAll(" ", ""));
        } else {
          dataSource.setKey(activeTask.getActivityStepKey());
        }

        activity.setActivityId(activeTask.getActivityId());
        activity.setVersion(activeTask.getActivityVersion());
        dataSource.setActivity(activity);

        dataSource.setTimeRangeType(
            StringUtils.isEmpty(activeTask.getTimeRangeChart())
                ? ""
                : this.getTimeRangeType(activeTask.getTimeRangeChart()));
        dataSource.setStartTime("");
        dataSource.setEndTime("");

        chart.setDataSource(dataSource);
      } else {
        chart.setTitle(chartTitle);
        chart.setDisplayName(
            StringUtils.isEmpty(question.getChartTitle()) ? "" : question.getChartTitle());
        chart.setType(StudyMetaDataConstants.CHART_TYPE_LINE);
        chart.setScrollable(
            (StringUtils.isNotEmpty(question.getAllowRollbackChart())
                    && question
                        .getAllowRollbackChart()
                        .equalsIgnoreCase(StudyMetaDataConstants.YES))
                ? true
                : false);
        chart.setConfiguration(this.singleBarChartDetails());

        dataSource.setType(question.getActivityType());
        dataSource.setKey(question.getActivityStepKey());

        activity.setActivityId(question.getActivityId());
        activity.setVersion(question.getActivityVersion());
        dataSource.setActivity(activity);

        dataSource.setTimeRangeType(
            StringUtils.isEmpty(question.getLineChartTimeRange())
                ? ""
                : this.getTimeRangeType(question.getLineChartTimeRange()));
        dataSource.setStartTime("");
        dataSource.setEndTime("");

        chart.setDataSource(dataSource);
      }

      chartsList.add(chart);
    } catch (Exception e) {
      LOGGER.error("DashboardMetaDataDao - getChartDetails() :: ERROR", e);
    }
    LOGGER.exit("getChartDetails() :: Ends");
    return chartsList;
  }

  @SuppressWarnings("rawtypes")
  public List<StatisticsBean> getStatisticsDetails(
      String activityType,
      ActiveTaskAttrtibutesValuesDto activeTask,
      QuestionsDto question,
      List<StatisticsBean> statisticsList,
      List<ActiveTaskFormulaDto> formulaDtoList,
      List<StatisticImageListDto> statisticImageList,
      int taskTypeId,
      Map activeTaskMasterAttrIdNameMap)
      throws DAOException {
    LOGGER.entry("begin getStatisticsDetails()");
    StatisticsBean statistics = new StatisticsBean();
    StatisticsDataSourceBean dataSource = new StatisticsDataSourceBean();
    DashboardActivityBean activity = new DashboardActivityBean();
    try {
      if (activityType.equalsIgnoreCase(StudyMetaDataConstants.ACTIVITY_TYPE_ACTIVE_TASK)) {
        statistics.setTitle(
            StringUtils.isEmpty(activeTask.getIdentifierNameStat())
                ? ""
                : activeTask.getIdentifierNameStat());
        statistics.setDisplayName(
            StringUtils.isEmpty(activeTask.getDisplayNameStat())
                ? ""
                : activeTask.getDisplayNameStat());
        statistics.setStatType(
            StringUtils.isEmpty(activeTask.getUploadTypeStat())
                ? ""
                : this.getStatisticsType(activeTask.getUploadTypeStat(), statisticImageList));
        statistics.setUnit(
            StringUtils.isEmpty(activeTask.getDisplayUnitStat())
                ? ""
                : activeTask.getDisplayUnitStat());
        statistics.setCalculation(
            StringUtils.isEmpty(activeTask.getFormulaAppliedStat())
                ? ""
                : this.getFormulaType(activeTask.getFormulaAppliedStat(), formulaDtoList));

        activity.setActivityId(activeTask.getActivityId());
        activity.setVersion(activeTask.getActivityVersion());
        dataSource.setActivity(activity);

        if ((3 == taskTypeId)
            && (null != activeTaskMasterAttrIdNameMap)
            && (null
                != activeTaskMasterAttrIdNameMap.get(activeTask.getActiveTaskMasterAttrId()))) {
          dataSource.setKey(
              String.valueOf(
                      activeTaskMasterAttrIdNameMap.get(activeTask.getActiveTaskMasterAttrId()))
                  .replaceAll(" ", ""));
        } else {
          dataSource.setKey(activeTask.getActivityStepKey());
        }

        dataSource.setType(activeTask.getActivityType());
        statistics.setDataSource(dataSource);
      } else {
        statistics.setTitle(
            StringUtils.isEmpty(question.getStatShortName()) ? "" : question.getStatShortName());
        statistics.setDisplayName(
            StringUtils.isEmpty(question.getStatDisplayName())
                ? ""
                : question.getStatDisplayName());
        statistics.setStatType(
            question.getStatType() == null
                ? ""
                : this.getStatisticsType(question.getStatType(), statisticImageList));
        statistics.setUnit(
            StringUtils.isEmpty(question.getStatDisplayUnits())
                ? ""
                : question.getStatDisplayUnits());
        statistics.setCalculation(
            question.getStatFormula() == null
                ? ""
                : this.getFormulaType(question.getStatFormula(), formulaDtoList));

        dataSource.setType(question.getActivityType());
        dataSource.setKey(question.getActivityStepKey());
        activity.setActivityId(question.getActivityId());
        activity.setVersion(question.getActivityVersion());
        dataSource.setActivity(activity);

        statistics.setDataSource(dataSource);
      }

      statisticsList.add(statistics);
    } catch (Exception e) {
      LOGGER.error("DashboardMetaDataDao - getStatisticsDetails() :: ERROR", e);
    }
    LOGGER.exit("getStatisticsDetails() :: Ends");
    return statisticsList;
  }

  public String getTimeRangeType(String timeRange) throws DAOException {
    LOGGER.entry("begin getTimeRangeType()");
    String type = timeRange;
    try {
      switch (timeRange) {
        case StudyMetaDataConstants.DAYS_OF_THE_CURRENT_WEEK:
          type = StudyMetaDataConstants.CHART_DAY_OF_WEEK;
          break;
        case StudyMetaDataConstants.DAYS_OF_THE_CURRENT_MONTH:
          type = StudyMetaDataConstants.CHART_DAYS_OF_MONTH;
          break;
        case StudyMetaDataConstants.WEEKS_OF_THE_CURRENT_MONTH:
          type = StudyMetaDataConstants.CHART_WEEK_OF_MONTH;
          break;
        case StudyMetaDataConstants.MONTHS_OF_THE_CURRENT_YEAR:
          type = StudyMetaDataConstants.CHART_MONTHS_OF_YEAR;
          break;
        case StudyMetaDataConstants.RUN_BASED:
          type = StudyMetaDataConstants.CHART_RUNS;
          break;
        case StudyMetaDataConstants.MULTIPLE_TIMES_A_DAY:
          type = StudyMetaDataConstants.CHART_HOURS_A_DAY;
          break;
        default:
          break;
      }
    } catch (Exception e) {
      LOGGER.error("DashboardMetaDataDao - getTimeRangeType() :: ERROR", e);
    }
    LOGGER.exit("getTimeRangeType() :: Ends");
    return type;
  }

  public String getStatisticsType(
      String statisticTypeId, List<StatisticImageListDto> statisticImageList) throws DAOException {
    LOGGER.entry("begin getStatisticsType()");
    String statisticType = "";
    try {
      if ((statisticImageList != null) && !statisticImageList.isEmpty()) {
        for (StatisticImageListDto statistic : statisticImageList) {
          if (statisticTypeId.equals(statistic.getStatisticImageId())) {
            statisticType = statistic.getValue();
            break;
          }
        }
      }
    } catch (Exception e) {
      LOGGER.error("DashboardMetaDataDao - getStatisticsType() :: ERROR", e);
    }
    LOGGER.exit("getStatisticsType() :: Ends");
    return statisticType;
  }

  public String getFormulaType(String formulaTypeId, List<ActiveTaskFormulaDto> formulaDtoList)
      throws DAOException {
    LOGGER.entry("begin getFormulaType()");
    String formulaType = "";
    try {
      if ((formulaDtoList != null) && !formulaDtoList.isEmpty()) {
        for (ActiveTaskFormulaDto formulaDto : formulaDtoList) {
          if (formulaTypeId.equals(formulaDto.getActivetaskFormulaId())) {
            formulaType = formulaDto.getFormula();
            break;
          }
        }
      }
    } catch (Exception e) {
      LOGGER.error("DashboardMetaDataDao - getFormulaType() :: ERROR", e);
    }
    LOGGER.exit("getFormulaType() :: Ends");
    return formulaType;
  }

  @SuppressWarnings("unchecked")
  public ActiveTaskDto getTimeDetailsByActivityIdForActiveTask(
      ActiveTaskDto activeTaskDto, Session session) throws DAOException {
    LOGGER.entry("ActivityMetaDataDao - getTimeDetailsByActivityIdForActiveTask()");
    String startDateTime = "";
    String endDateTime = "";
    String startDate = "";
    String endDate = "";
    String time = StudyMetaDataConstants.DEFAULT_MIN_TIME;
    try {
      startDateTime = activeTaskDto.getActiveTaskLifetimeStart() + " " + time;
      endDateTime =
          StringUtils.isEmpty(activeTaskDto.getActiveTaskLifetimeEnd())
              ? ""
              : activeTaskDto.getActiveTaskLifetimeEnd() + " " + time;

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
            startDateTime =
                activeTaskDto.getActiveTaskLifetimeStart()
                    + " "
                    + activeTaskFrequency.getFrequencyTime();
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

          if (StringUtils.isNotEmpty(activeTaskDto.getActiveTaskLifetimeStart())) {
            activeTaskDto.setActiveTaskLifetimeStart(startDateTime);
          }
          if (StringUtils.isNotEmpty(activeTaskDto.getActiveTaskLifetimeEnd())) {
            activeTaskDto.setActiveTaskLifetimeEnd(endDateTime);
          }
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

          if (StringUtils.isNotEmpty(activeTaskDto.getActiveTaskLifetimeStart())) {
            activeTaskDto.setActiveTaskLifetimeStart(startDateTime);
          }
          if (StringUtils.isNotEmpty(activeTaskDto.getActiveTaskLifetimeEnd())) {
            activeTaskDto.setActiveTaskLifetimeEnd(endDateTime);
          }
        } else if (activeTaskDto
            .getFrequency()
            .equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {

          List<ActiveTaskCustomFrequenciesDto> activeTaskCustomFrequencyList =
              session
                  .createQuery(
                      "from ActiveTaskCustomFrequenciesDto ATCFDTO"
                          + " where ATCFDTO.activeTaskId=:activeTaskId"
                          + " ORDER BY ATCFDTO.frequencyStartTime")
                  .setString("activeTaskId", activeTaskDto.getId())
                  .list();
          if ((activeTaskCustomFrequencyList != null) && !activeTaskCustomFrequencyList.isEmpty()) {
            startDate = activeTaskCustomFrequencyList.get(0).getFrequencyStartDate();
            endDate = activeTaskCustomFrequencyList.get(0).getFrequencyEndDate();
            for (ActiveTaskCustomFrequenciesDto customFrequency : activeTaskCustomFrequencyList) {

              if (StringUtils.isNotEmpty(startDate)) {
                if (StudyMetaDataConstants.SDF_DATE
                    .parse(startDate)
                    .after(
                        StudyMetaDataConstants.SDF_DATE.parse(
                            customFrequency.getFrequencyStartDate()))) {
                  startDate = customFrequency.getFrequencyStartDate();
                }
              }

              if (StringUtils.isNotEmpty(endDate)) {
                if (StudyMetaDataConstants.SDF_DATE
                    .parse(endDate)
                    .before(
                        StudyMetaDataConstants.SDF_DATE.parse(
                            customFrequency.getFrequencyEndDate()))) {
                  endDate = customFrequency.getFrequencyEndDate();
                }
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

          if (StringUtils.isNotEmpty(startDate)) {
            activeTaskDto.setActiveTaskLifetimeStart(startDateTime);
          }
          if (StringUtils.isNotEmpty(endDate)) {
            activeTaskDto.setActiveTaskLifetimeEnd(endDateTime);
          }
        }
      }
    } catch (Exception e) {
      LOGGER.error("ActivityMetaDataDao - getTimeDetailsByActivityIdForActiveTask() :: ERROR", e);
    }
    LOGGER.exit("ActivityMetaDataDao - getTimeDetailsByActivityIdForActiveTask() :: Ends");
    return activeTaskDto;
  }

  @SuppressWarnings("unchecked")
  public QuestionnairesDto getTimeDetailsByActivityIdForQuestionnaire(
      QuestionnairesDto questionaire, Session session) throws DAOException {
    LOGGER.entry("ActivityMetaDataDao - getTimeDetailsByActivityIdForQuestionnaire()");
    String startDateTime = "";
    String endDateTime = "";
    String time = StudyMetaDataConstants.DEFAULT_MIN_TIME;
    try {
      startDateTime = questionaire.getStudyLifetimeStart() + " " + time;
      endDateTime =
          StringUtils.isEmpty(questionaire.getStudyLifetimeEnd())
              ? ""
              : questionaire.getStudyLifetimeEnd() + " " + time;

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
                              + " where QFDTO.questionnairesId=:questRespId")
                      .setString("questRespId", questionaire.getId())
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

          questionaire.setStudyLifetimeStart(startDateTime);
          questionaire.setStudyLifetimeEnd(endDateTime);
        } else if (questionaire
            .getFrequency()
            .equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_DAILY)) {

          List<QuestionnairesFrequenciesDto> questionnairesFrequencyList =
              session
                  .createQuery(
                      "from QuestionnairesFrequenciesDto QFDTO"
                          + " where QFDTO.questionnairesId=:questRespId"
                          + " ORDER BY QFDTO.frequencyTime")
                  .setString("questRespId", questionaire.getId())
                  .list();
          if ((questionnairesFrequencyList != null) && !questionnairesFrequencyList.isEmpty()) {
            startDateTime =
                questionaire.getStudyLifetimeStart()
                    + " "
                    + questionnairesFrequencyList.get(0).getFrequencyTime();
            endDateTime =
                questionaire.getStudyLifetimeEnd() + " " + StudyMetaDataConstants.DEFAULT_MAX_TIME;
          }

          questionaire.setStudyLifetimeStart(startDateTime);
          questionaire.setStudyLifetimeEnd(endDateTime);
        } else if (questionaire
            .getFrequency()
            .equalsIgnoreCase(StudyMetaDataConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {

          List<QuestionnairesCustomFrequenciesDto> questionnaireCustomFrequencyList =
              session
                  .createQuery(
                      "from QuestionnairesCustomFrequenciesDto QCFDTO"
                          + " where QCFDTO.questionnairesId=:questRespId"
                          + " ORDER BY QCFDTO.frequencyStartDate, QCFDTO.frequencyStartTime")
                  .setString("questRespId", questionaire.getId())
                  .list();
          if ((questionnaireCustomFrequencyList != null)
              && !questionnaireCustomFrequencyList.isEmpty()) {
            String startDate = questionnaireCustomFrequencyList.get(0).getFrequencyStartDate();
            String endDate = questionnaireCustomFrequencyList.get(0).getFrequencyEndDate();
            for (QuestionnairesCustomFrequenciesDto customFrequency :
                questionnaireCustomFrequencyList) {

              if (StringUtils.isNotEmpty(startDate)) {
                if (StudyMetaDataConstants.SDF_DATE
                    .parse(startDate)
                    .after(
                        StudyMetaDataConstants.SDF_DATE.parse(
                            customFrequency.getFrequencyStartDate()))) {
                  startDate = customFrequency.getFrequencyStartDate();
                }
              } else {
                startDate = "";
              }

              if (StringUtils.isNotEmpty(endDate)) {
                if (StudyMetaDataConstants.SDF_DATE
                    .parse(endDate)
                    .before(
                        StudyMetaDataConstants.SDF_DATE.parse(
                            customFrequency.getFrequencyEndDate()))) {
                  endDate = customFrequency.getFrequencyEndDate();
                }
              } else {
                endDate = "";
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

          questionaire.setStudyLifetimeStart(startDateTime);
          questionaire.setStudyLifetimeEnd(endDateTime);
        }
      }
    } catch (Exception e) {
      LOGGER.error(
          "ActivityMetaDataDao - getTimeDetailsByActivityIdForQuestionnaire() :: ERROR", e);
    }
    LOGGER.exit("ActivityMetaDataDao - getTimeDetailsByActivityIdForQuestionnaire() :: Ends");
    return questionaire;
  }

  public Map<String, Object> singleLineChartDetails() throws DAOException {
    LOGGER.entry("begin singleLineChartDetails()");
    Map<String, Object> configuration = new LinkedHashMap<>();
    try {
      configuration.put("subType", "single");
      configuration.put("gridlines", false);
      configuration.put("animated", false);
      configuration.put("scaling", 0);

      Map<String, Object> axisColor = new LinkedHashMap<>();
      axisColor.put("x-axis", "#fff");
      axisColor.put("y-axis", "#000");
      configuration.put("axisColor", axisColor);

      configuration.put("max", 0.0d);
      configuration.put("min", 0.0d);

      List<String> titles = new ArrayList<>();
      configuration.put("titles", titles);
      configuration.put("defaultText", "");

      /** single setting */
      List<Map<String, Object>> settingsList = new ArrayList<>();
      Map<String, Object> settings = new LinkedHashMap<>();
      settings.put("numberOfPoints", 1);
      List<Double> pointValues = new ArrayList<>();
      settings.put("pointValues", pointValues);
      settings.put("lineColor", "#d3d3d3");
      settingsList.add(settings);
      configuration.put("settings", settingsList);
    } catch (Exception e) {
      LOGGER.error("DashboardMetaDataDao - singleLineChartDetails() :: ERROR", e);
    }
    LOGGER.exit("singleLineChartDetails() :: Ends");
    return configuration;
  }

  public Map<String, Object> multipleLineChartDetails() throws DAOException {
    LOGGER.entry("begin multipleLineChartDetails()");
    Map<String, Object> configuration = new LinkedHashMap<>();
    try {
      configuration.put("subType", "multiple");
      configuration.put("gridlines", false);
      configuration.put("animated", false);
      configuration.put("scaling", 0);

      Map<String, Object> axisColor = new LinkedHashMap<>();
      axisColor.put("x-axis", "#fff");
      axisColor.put("y-axis", "#000");
      configuration.put("axisColor", axisColor);

      configuration.put("max", 0.0d);
      configuration.put("min", 0.0d);

      List<String> titles = new ArrayList<>();
      configuration.put("titles", titles);
      configuration.put("defaultText", "");

      /** mutiple setting */
      List<Map<String, Object>> settingsList = new ArrayList<>();
      Map<String, Object> settings = new LinkedHashMap<>();
      settings.put("numberOfPoints", 1);
      List<Double> pointValues = new ArrayList<>();
      settings.put("pointValues", pointValues);
      settings.put("lineColor", "#d3d3d3");
      settingsList.add(settings);
      configuration.put("settings", settingsList);
    } catch (Exception e) {
      LOGGER.error("DashboardMetaDataDao - multipleLineChartDetails() :: ERROR", e);
    }
    LOGGER.exit("multipleLineChartDetails() :: Ends");
    return configuration;
  }

  public Map<String, Object> uniquePieChartDetails() throws DAOException {
    LOGGER.entry("begin uniquePieChartDetails()");
    Map<String, Object> configuration = new LinkedHashMap<>();
    try {
      configuration.put("subType", "unique-responses");
      configuration.put("numberOfSegments", 0);

      List<Double> values = new ArrayList<>();
      configuration.put("values", values);

      List<String> colors = new ArrayList<>();
      configuration.put("colors", colors);

      List<String> titles = new ArrayList<>();
      configuration.put("titles", titles);
    } catch (Exception e) {
      LOGGER.error("DashboardMetaDataDao - uniquePieChartDetails() :: ERROR", e);
    }
    LOGGER.exit("uniquePieChartDetails() :: Ends");
    return configuration;
  }

  public Map<String, Object> rangePieChartDetails() throws DAOException {
    LOGGER.entry("begin rangePieChartDetails()");
    Map<String, Object> configuration = new LinkedHashMap<>();
    try {
      configuration.put("subType", "range-responses");
      configuration.put("numberOfSegments", 5);

      List<Double> values = new ArrayList<>();
      configuration.put("values", values);

      List<String> colors = new ArrayList<>();
      configuration.put("colors", colors);

      List<String> titles = new ArrayList<>();
      configuration.put("titles", titles);
    } catch (Exception e) {
      LOGGER.error("DashboardMetaDataDao - rangePieChartDetails() :: ERROR", e);
    }
    LOGGER.exit("rangePieChartDetails() :: Ends");
    return configuration;
  }

  public Map<String, Object> singleBarChartDetails() throws DAOException {
    LOGGER.entry("begin singleBarChartDetails()");
    Map<String, Object> configuration = new LinkedHashMap<>();
    try {
      configuration.put("subType", "single");

      List<String> titles = new ArrayList<>();
      configuration.put("titles", titles);

      /** single setting */
      List<Map<String, Object>> settingsList = new ArrayList<>();
      Map<String, Object> settings = new LinkedHashMap<>();
      settings.put("numberOfPoints", 1);
      List<Double> pointValues = new ArrayList<>();
      settings.put("pointValues", pointValues);
      settings.put("barColor", "#d3d3d3");
      settingsList.add(settings);
      configuration.put("settings", settingsList);
    } catch (Exception e) {
      LOGGER.error("DashboardMetaDataDao - singleBarChartDetails() :: ERROR", e);
    }
    LOGGER.exit("singleBarChartDetails() :: Ends");
    return configuration;
  }

  public Map<String, Object> multipleBarChartDetails() throws DAOException {
    LOGGER.entry("begin multipleBarChartDetails()");
    Map<String, Object> configuration = new LinkedHashMap<>();
    try {
      configuration.put("subType", "multiple");

      List<String> titles = new ArrayList<>();
      configuration.put("titles", titles);

      /** multiple setting */
      List<Map<String, Object>> settingsList = new ArrayList<>();
      Map<String, Object> settings = new LinkedHashMap<>();
      settings.put("numberOfPoints", 1);
      List<Double> pointValues = new ArrayList<>();
      settings.put("pointValues", pointValues);
      settings.put("barColor", "#d3d3d3");
      settingsList.add(settings);
      configuration.put("settings", settingsList);
    } catch (Exception e) {
      LOGGER.error("DashboardMetaDataDao - multipleBarChartDetails() :: ERROR", e);
    }
    LOGGER.exit("multipleBarChartDetails() :: Ends");
    return configuration;
  }

  public String getChartDataSourceNameByMasterId(Integer masterId, String displayName)
      throws DAOException {
    LOGGER.entry("begin getChartDataSourceNameByMasterId()");
    String dataSourceName = "";
    try {
      switch (masterId) {
        case 14:
          dataSourceName = StudyMetaDataConstants.CHART_DATA_SOURCE_SCORE;
          break;
        case 15:
          dataSourceName = StudyMetaDataConstants.CHART_DATA_SOURCE_NO_OF_GAMES;
          break;
        case 16:
          dataSourceName = StudyMetaDataConstants.CHART_DATA_SOURCE_NO_OF_FAILURES;
          break;
        default:
          dataSourceName = displayName;
          break;
      }
    } catch (Exception e) {
      LOGGER.error("DashboardMetaDataDao - getChartDataSourceNameByMasterId() :: ERROR", e);
    }
    LOGGER.exit("getChartDataSourceNameByMasterId() :: Ends");
    return dataSourceName;
  }
}
