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

package com.fdahpstudydesigner.service;

import com.fdahpstudydesigner.bean.ActiveStatisticsBean;
import com.fdahpstudydesigner.bo.ActiveTaskBo;
import com.fdahpstudydesigner.bo.ActiveTaskCustomScheduleBo;
import com.fdahpstudydesigner.bo.ActiveTaskFrequencyBo;
import com.fdahpstudydesigner.bo.ActiveTaskListBo;
import com.fdahpstudydesigner.bo.ActiveTaskMasterAttributeBo;
import com.fdahpstudydesigner.bo.ActivetaskFormulaBo;
import com.fdahpstudydesigner.bo.StatisticImageListBo;
import com.fdahpstudydesigner.dao.StudyActiveTasksDAO;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudyActiveTasksServiceImpl implements StudyActiveTasksService {

  private static XLogger logger =
      XLoggerFactory.getXLogger(StudyActiveTasksServiceImpl.class.getName());

  @Autowired private StudyActiveTasksDAO studyActiveTasksDAO;

  @Override
  public String deleteActiveTask(
      Integer activeTaskInfoId, Integer studyId, SessionObject sesObj, String customStudyId) {
    logger.entry("begin deleteActiveTask()");
    String message = null;
    ActiveTaskBo activeTaskBo = null;
    try {
      activeTaskBo = studyActiveTasksDAO.getActiveTaskById(activeTaskInfoId, customStudyId);
      if (activeTaskBo != null) {
        message = studyActiveTasksDAO.deleteActiveTask(activeTaskBo, sesObj, customStudyId);
      }
    } catch (Exception e) {
      logger.error("StudyServiceImpl - deleteActiveTask() - Error", e);
    }
    logger.exit("deleteActiveTask() - Ends");
    return message;
  }

  @Override
  public ActiveTaskBo getActiveTaskById(Integer ativeTaskId, String customStudyId) {
    logger.entry("begin getActiveTaskById()");
    ActiveTaskBo activeTask = null;
    try {
      activeTask = studyActiveTasksDAO.getActiveTaskById(ativeTaskId, customStudyId);
      if (activeTask != null) {
        if ((activeTask.getActiveTaskCustomScheduleBo() != null)
            && !activeTask.getActiveTaskCustomScheduleBo().isEmpty()) {
          for (ActiveTaskCustomScheduleBo activeTaskCustomScheduleBo :
              activeTask.getActiveTaskCustomScheduleBo()) {

            if (StringUtils.isNotBlank(activeTaskCustomScheduleBo.getFrequencyStartDate())) {
              activeTaskCustomScheduleBo.setFrequencyStartDate(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      activeTaskCustomScheduleBo.getFrequencyStartDate(),
                      FdahpStudyDesignerConstants.DB_SDF_DATE,
                      FdahpStudyDesignerConstants.UI_SDF_DATE));
            }
            if (StringUtils.isNotBlank(activeTaskCustomScheduleBo.getFrequencyEndDate())) {
              activeTaskCustomScheduleBo.setFrequencyEndDate(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      activeTaskCustomScheduleBo.getFrequencyEndDate(),
                      FdahpStudyDesignerConstants.DB_SDF_DATE,
                      FdahpStudyDesignerConstants.UI_SDF_DATE));
            }
            if (StringUtils.isNotBlank(activeTaskCustomScheduleBo.getFrequencyStartTime())) {
              activeTaskCustomScheduleBo.setFrequencyStartTime(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      activeTaskCustomScheduleBo.getFrequencyStartTime(),
                      FdahpStudyDesignerConstants.UI_SDF_TIME,
                      FdahpStudyDesignerConstants.SDF_TIME));
            }

            if (StringUtils.isNotBlank(activeTaskCustomScheduleBo.getFrequencyEndTime())) {
              activeTaskCustomScheduleBo.setFrequencyEndTime(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      activeTaskCustomScheduleBo.getFrequencyEndTime(),
                      FdahpStudyDesignerConstants.UI_SDF_TIME,
                      FdahpStudyDesignerConstants.SDF_TIME));
            }
          }
        }
        if ((activeTask.getActiveTaskFrequenciesList() != null)
            && !activeTask.getActiveTaskFrequenciesList().isEmpty()) {
          for (ActiveTaskFrequencyBo activeTaskFrequencyBo :
              activeTask.getActiveTaskFrequenciesList()) {
            if (StringUtils.isNotBlank(activeTaskFrequencyBo.getFrequencyDate())) {
              activeTaskFrequencyBo.setFrequencyDate(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      activeTaskFrequencyBo.getFrequencyDate(),
                      FdahpStudyDesignerConstants.DB_SDF_DATE,
                      FdahpStudyDesignerConstants.UI_SDF_DATE));
            }
            if (StringUtils.isNotBlank(activeTaskFrequencyBo.getFrequencyTime())) {
              activeTaskFrequencyBo.setFrequencyTime(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      activeTaskFrequencyBo.getFrequencyTime(),
                      FdahpStudyDesignerConstants.UI_SDF_TIME,
                      FdahpStudyDesignerConstants.SDF_TIME));
            }
          }
        }
        if ((activeTask.getActiveTaskFrequenciesBo() != null)
            && StringUtils.isNotBlank(activeTask.getActiveTaskFrequenciesBo().getFrequencyDate())) {
          activeTask
              .getActiveTaskFrequenciesBo()
              .setFrequencyDate(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      activeTask.getActiveTaskFrequenciesBo().getFrequencyDate(),
                      FdahpStudyDesignerConstants.DB_SDF_DATE,
                      FdahpStudyDesignerConstants.UI_SDF_DATE));
        }
        if ((activeTask.getActiveTaskFrequenciesBo() != null)
            && StringUtils.isNotBlank(activeTask.getActiveTaskFrequenciesBo().getFrequencyTime())) {
          activeTask
              .getActiveTaskFrequenciesBo()
              .setFrequencyTime(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      activeTask.getActiveTaskFrequenciesBo().getFrequencyTime(),
                      FdahpStudyDesignerConstants.UI_SDF_TIME,
                      FdahpStudyDesignerConstants.SDF_TIME));
        }
        if (StringUtils.isNotBlank(activeTask.getActiveTaskLifetimeEnd())) {
          activeTask.setActiveTaskLifetimeEnd(
              FdahpStudyDesignerUtil.getFormattedDate(
                  activeTask.getActiveTaskLifetimeEnd(),
                  FdahpStudyDesignerConstants.DB_SDF_DATE,
                  FdahpStudyDesignerConstants.UI_SDF_DATE));
        }
        if (StringUtils.isNotBlank(activeTask.getActiveTaskLifetimeStart())) {
          activeTask.setActiveTaskLifetimeStart(
              FdahpStudyDesignerUtil.getFormattedDate(
                  activeTask.getActiveTaskLifetimeStart(),
                  FdahpStudyDesignerConstants.DB_SDF_DATE,
                  FdahpStudyDesignerConstants.UI_SDF_DATE));
        }
      }
    } catch (Exception e) {
      logger.error("StudyActiveTasksServiceImpl - getActiveTaskById() - ERROR ", e);
    }
    logger.exit("getActiveTaskById() - Ends");
    return activeTask;
  }

  @Override
  public List<ActivetaskFormulaBo> getActivetaskFormulas() {
    logger.entry("begin getActivetaskFormulas()");
    List<ActivetaskFormulaBo> activetaskFormulaList = new ArrayList<>();
    try {
      activetaskFormulaList = studyActiveTasksDAO.getActivetaskFormulas();
    } catch (Exception e) {
      logger.error("StudyActiveTasksServiceImpl - getActivetaskFormulas() - ERROR ", e);
    }
    logger.exit("getActivetaskFormulas() - Ends");
    return activetaskFormulaList;
  }

  @Override
  public List<ActiveTaskMasterAttributeBo> getActiveTaskMasterAttributesByType(
      String activeTaskType) {
    logger.entry("begin getActiveTaskMasterAttributesByType()");
    List<ActiveTaskMasterAttributeBo> taskMasterAttributeBos = new ArrayList<>();
    try {
      taskMasterAttributeBos =
          studyActiveTasksDAO.getActiveTaskMasterAttributesByType(activeTaskType);
    } catch (Exception e) {
      logger.error(
          "StudyActiveTasksServiceImpl - getActiveTaskMasterAttributesByType() - ERROR ", e);
    }
    logger.exit("getActiveTaskMasterAttributesByType() - Ends");
    return taskMasterAttributeBos;
  }

  @Override
  public List<ActiveTaskListBo> getAllActiveTaskTypes(String platformType) {
    logger.entry("begin getAllActiveTaskTypes()");
    List<ActiveTaskListBo> activeTaskListBos = new ArrayList<>();
    try {
      activeTaskListBos = studyActiveTasksDAO.getAllActiveTaskTypes(platformType);
    } catch (Exception e) {
      logger.error("StudyActiveTasksServiceImpl - getAllActiveTaskTypes() - ERROR ", e);
    }
    logger.exit("getAllActiveTaskTypes() - Ends");
    return activeTaskListBos;
  }

  @Override
  public List<StatisticImageListBo> getStatisticImages() {
    logger.entry("begin getStatisticImages()");
    List<StatisticImageListBo> statisticImageListBos = new ArrayList<>();
    try {
      statisticImageListBos = studyActiveTasksDAO.getStatisticImages();
    } catch (Exception e) {
      logger.error("StudyActiveTasksServiceImpl - getStatisticImages() - ERROR ", e);
    }
    logger.exit("getStatisticImages() - Ends");
    return statisticImageListBos;
  }

  @Override
  public List<ActiveTaskBo> getStudyActiveTasksByStudyId(String studyId, Boolean isLive) {
    logger.entry("begin getStudyActiveTasksByStudyId()");
    List<ActiveTaskBo> activeTasks = null;
    try {
      activeTasks = studyActiveTasksDAO.getStudyActiveTasksByStudyId(studyId, isLive);
    } catch (Exception e) {
      logger.error("StudyActiveTasksServiceImpl - getStudyActiveTasksByStudyId() - ERROR ", e);
    }
    logger.exit("getStudyActiveTasksByStudyId() - Ends");
    return activeTasks;
  }

  @Override
  public ActiveTaskBo saveOrUpdateActiveTask(
      ActiveTaskBo activeTaskBo, SessionObject sessionObject, String customStudyId) {
    logger.entry("begin saveOrUpdateActiveTask()");
    ActiveTaskBo updateActiveTaskBo = null;
    try {
      if (activeTaskBo != null) {
        if (activeTaskBo.getId() != null) {
          updateActiveTaskBo =
              studyActiveTasksDAO.getActiveTaskById(activeTaskBo.getId(), customStudyId);
          updateActiveTaskBo.setModifiedBy(sessionObject.getUserId());
          updateActiveTaskBo.setModifiedDate(FdahpStudyDesignerUtil.getCurrentDateTime());
          if (updateActiveTaskBo.getIsDuplicate() != null) {
            updateActiveTaskBo.setIsDuplicate(updateActiveTaskBo.getIsDuplicate());
          }
        } else {
          updateActiveTaskBo = new ActiveTaskBo();
          updateActiveTaskBo.setStudyId(activeTaskBo.getStudyId());
          updateActiveTaskBo.setTaskTypeId(activeTaskBo.getTaskTypeId());
          updateActiveTaskBo.setCreatedBy(sessionObject.getUserId());
          updateActiveTaskBo.setCreatedDate(FdahpStudyDesignerUtil.getCurrentDateTime());
          updateActiveTaskBo.setDisplayName(
              StringUtils.isEmpty(activeTaskBo.getDisplayName())
                  ? ""
                  : activeTaskBo.getDisplayName());
          updateActiveTaskBo.setShortTitle(
              StringUtils.isEmpty(activeTaskBo.getShortTitle())
                  ? ""
                  : activeTaskBo.getShortTitle());
          updateActiveTaskBo.setInstruction(
              StringUtils.isEmpty(activeTaskBo.getInstruction())
                  ? ""
                  : activeTaskBo.getInstruction());
          updateActiveTaskBo.setTaskAttributeValueBos(activeTaskBo.getTaskAttributeValueBos());
        }
        updateActiveTaskBo.setStudyId(activeTaskBo.getStudyId());
        updateActiveTaskBo.setTaskTypeId(activeTaskBo.getTaskTypeId());
        updateActiveTaskBo.setDisplayName(
            StringUtils.isEmpty(activeTaskBo.getDisplayName())
                ? ""
                : activeTaskBo.getDisplayName());
        updateActiveTaskBo.setShortTitle(
            StringUtils.isEmpty(activeTaskBo.getShortTitle()) ? "" : activeTaskBo.getShortTitle());
        updateActiveTaskBo.setInstruction(
            StringUtils.isEmpty(activeTaskBo.getInstruction())
                ? ""
                : activeTaskBo.getInstruction());
        updateActiveTaskBo.setTaskAttributeValueBos(activeTaskBo.getTaskAttributeValueBos());
        updateActiveTaskBo.setAction(activeTaskBo.isAction());
        updateActiveTaskBo.setButtonText(activeTaskBo.getButtonText());
        if (activeTaskBo
            .getButtonText()
            .equalsIgnoreCase(FdahpStudyDesignerConstants.COMPLETED_BUTTON)) {
          updateActiveTaskBo.setIsChange(1);
        } else {
          updateActiveTaskBo.setIsChange(0);
        }
        updateActiveTaskBo.setActive(1);
        updateActiveTaskBo =
            studyActiveTasksDAO.saveOrUpdateActiveTaskInfo(
                updateActiveTaskBo, sessionObject, customStudyId);
      }
    } catch (Exception e) {
      logger.error("StudyActiveTasksServiceImpl - saveOrUpdateActiveTask() - Error", e);
    }
    logger.exit("saveOrUpdateActiveTask() - Ends");
    return updateActiveTaskBo;
  }

  @Override
  public ActiveTaskBo saveOrUpdateActiveTask(ActiveTaskBo activeTaskBo, String customStudyId) {
    logger.entry("begin saveOrUpdateActiveTask()");
    ActiveTaskBo addActiveTaskeBo = null;
    try {
      if (null != activeTaskBo) {
        if (activeTaskBo.getId() != null) {
          addActiveTaskeBo =
              studyActiveTasksDAO.getActiveTaskById(activeTaskBo.getId(), customStudyId);
        } else {
          addActiveTaskeBo = new ActiveTaskBo();
        }
        if (activeTaskBo.getStudyId() != null) {
          addActiveTaskeBo.setStudyId(activeTaskBo.getStudyId());
        }
        if (activeTaskBo.getFrequency() != null) {
          addActiveTaskeBo.setFrequency(activeTaskBo.getFrequency());
        }
        if (activeTaskBo.getScheduleType() != null) {
          addActiveTaskeBo.setScheduleType(activeTaskBo.getScheduleType());
        }
        addActiveTaskeBo.setAnchorDateId(activeTaskBo.getAnchorDateId());
        if ((activeTaskBo.getFrequency() != null)
            && !activeTaskBo
                .getFrequency()
                .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME)) {
          if (StringUtils.isNotBlank(activeTaskBo.getActiveTaskLifetimeStart())
              && !("NA").equalsIgnoreCase(activeTaskBo.getActiveTaskLifetimeStart())) {
            addActiveTaskeBo.setActiveTaskLifetimeStart(
                FdahpStudyDesignerUtil.getFormattedDate(
                    activeTaskBo.getActiveTaskLifetimeStart(),
                    FdahpStudyDesignerConstants.UI_SDF_DATE,
                    FdahpStudyDesignerConstants.SD_DATE_FORMAT));
          } else {
            addActiveTaskeBo.setActiveTaskLifetimeStart(null);
          }
          if (StringUtils.isNotBlank(activeTaskBo.getActiveTaskLifetimeEnd())
              && !("NA").equalsIgnoreCase(activeTaskBo.getActiveTaskLifetimeEnd())) {
            addActiveTaskeBo.setActiveTaskLifetimeEnd(
                FdahpStudyDesignerUtil.getFormattedDate(
                    activeTaskBo.getActiveTaskLifetimeEnd(),
                    FdahpStudyDesignerConstants.UI_SDF_DATE,
                    FdahpStudyDesignerConstants.SD_DATE_FORMAT));
          } else {
            addActiveTaskeBo.setActiveTaskLifetimeEnd(null);
          }
        }
        if (activeTaskBo.getTitle() != null) {
          addActiveTaskeBo.setTitle(activeTaskBo.getTitle());
        }
        if (activeTaskBo.getCreatedDate() != null) {
          addActiveTaskeBo.setCreatedDate(activeTaskBo.getCreatedDate());
        }
        if (activeTaskBo.getCreatedBy() != null) {
          addActiveTaskeBo.setCreatedBy(activeTaskBo.getCreatedBy());
        }
        if (activeTaskBo.getModifiedDate() != null) {
          addActiveTaskeBo.setModifiedDate(activeTaskBo.getModifiedDate());
        }
        if (activeTaskBo.getModifiedBy() != null) {
          addActiveTaskeBo.setModifiedBy(activeTaskBo.getModifiedBy());
        }
        if (activeTaskBo.getRepeatActiveTask() != null) {
          addActiveTaskeBo.setRepeatActiveTask(activeTaskBo.getRepeatActiveTask());
        }
        if (activeTaskBo.getDayOfTheWeek() != null) {
          addActiveTaskeBo.setDayOfTheWeek(activeTaskBo.getDayOfTheWeek());
        }
        if (activeTaskBo.getType() != null) {
          addActiveTaskeBo.setType(activeTaskBo.getType());
        }
        if ((activeTaskBo.getScheduleType() != null) && !activeTaskBo.getScheduleType().isEmpty()) {
          addActiveTaskeBo.setScheduleType(activeTaskBo.getScheduleType());
        }
        if (activeTaskBo.getFrequency() != null) {
          if (!activeTaskBo.getFrequency().equalsIgnoreCase(activeTaskBo.getPreviousFrequency())) {
            addActiveTaskeBo.setActiveTaskCustomScheduleBo(
                activeTaskBo.getActiveTaskCustomScheduleBo());
            addActiveTaskeBo.setActiveTaskFrequenciesList(
                activeTaskBo.getActiveTaskFrequenciesList());
            addActiveTaskeBo.setActiveTaskFrequenciesBo(activeTaskBo.getActiveTaskFrequenciesBo());
            if (activeTaskBo
                    .getFrequency()
                    .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME)
                && (activeTaskBo.getActiveTaskFrequenciesBo() != null)) {
              if (!activeTaskBo.getActiveTaskFrequenciesBo().getIsStudyLifeTime()) {
                if (StringUtils.isNotBlank(activeTaskBo.getActiveTaskLifetimeStart())
                    && !("NA").equalsIgnoreCase(activeTaskBo.getActiveTaskLifetimeStart())) {
                  addActiveTaskeBo.setActiveTaskLifetimeStart(
                      FdahpStudyDesignerUtil.getFormattedDate(
                          activeTaskBo.getActiveTaskLifetimeStart(),
                          FdahpStudyDesignerConstants.UI_SDF_DATE,
                          FdahpStudyDesignerConstants.SD_DATE_FORMAT));
                } else {
                  addActiveTaskeBo.setActiveTaskLifetimeStart(null);
                }

                if (StringUtils.isNotBlank(activeTaskBo.getActiveTaskLifetimeEnd())
                    && !("NA").equalsIgnoreCase(activeTaskBo.getActiveTaskLifetimeEnd())) {
                  addActiveTaskeBo.setActiveTaskLifetimeEnd(
                      FdahpStudyDesignerUtil.getFormattedDate(
                          activeTaskBo.getActiveTaskLifetimeEnd(),
                          FdahpStudyDesignerConstants.UI_SDF_DATE,
                          FdahpStudyDesignerConstants.SD_DATE_FORMAT));
                } else {
                  addActiveTaskeBo.setActiveTaskLifetimeEnd(null);
                }
              }
            }
          } else {
            if ((activeTaskBo.getActiveTaskCustomScheduleBo() != null)
                && !activeTaskBo.getActiveTaskCustomScheduleBo().isEmpty()) {
              addActiveTaskeBo.setActiveTaskCustomScheduleBo(
                  activeTaskBo.getActiveTaskCustomScheduleBo());
            }
            if ((activeTaskBo.getActiveTaskFrequenciesList() != null)
                && !activeTaskBo.getActiveTaskFrequenciesList().isEmpty()) {
              addActiveTaskeBo.setActiveTaskFrequenciesList(
                  activeTaskBo.getActiveTaskFrequenciesList());
            }
            if (activeTaskBo.getActiveTaskFrequenciesBo() != null) {
              if (activeTaskBo
                  .getFrequency()
                  .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME)) {
                if (!activeTaskBo.getActiveTaskFrequenciesBo().getIsStudyLifeTime()) {
                  if (StringUtils.isNotBlank(activeTaskBo.getActiveTaskLifetimeStart())
                      && !("NA").equalsIgnoreCase(activeTaskBo.getActiveTaskLifetimeStart())) {
                    addActiveTaskeBo.setActiveTaskLifetimeStart(
                        FdahpStudyDesignerUtil.getFormattedDate(
                            activeTaskBo.getActiveTaskLifetimeStart(),
                            FdahpStudyDesignerConstants.UI_SDF_DATE,
                            FdahpStudyDesignerConstants.SD_DATE_FORMAT));
                  } else {
                    addActiveTaskeBo.setActiveTaskLifetimeStart(null);
                  }

                  if (StringUtils.isNotBlank(activeTaskBo.getActiveTaskLifetimeEnd())
                      && !("NA").equalsIgnoreCase(activeTaskBo.getActiveTaskLifetimeEnd())) {
                    addActiveTaskeBo.setActiveTaskLifetimeEnd(
                        FdahpStudyDesignerUtil.getFormattedDate(
                            activeTaskBo.getActiveTaskLifetimeEnd(),
                            FdahpStudyDesignerConstants.UI_SDF_DATE,
                            FdahpStudyDesignerConstants.SD_DATE_FORMAT));
                  } else {
                    addActiveTaskeBo.setActiveTaskLifetimeEnd(null);
                  }
                }
              }
              addActiveTaskeBo.setActiveTaskFrequenciesBo(
                  activeTaskBo.getActiveTaskFrequenciesBo());
            }
          }
        }
        if (activeTaskBo.getPreviousFrequency() != null) {
          addActiveTaskeBo.setPreviousFrequency(activeTaskBo.getPreviousFrequency());
        }
        addActiveTaskeBo.setActive(1);
        addActiveTaskeBo =
            studyActiveTasksDAO.saveOrUpdateActiveTask(addActiveTaskeBo, customStudyId);
      }
    } catch (Exception e) {
      logger.error("StudyActiveTaskServiceImpl - saveORUpdateQuestionnaire - Error", e);
    }
    logger.exit("saveOrUpdateActiveTask() - Ends");
    return addActiveTaskeBo;
  }

  @Override
  public boolean validateActiveTaskAttrById(
      Integer studyId,
      String activeTaskAttName,
      String activeTaskAttIdVal,
      String activeTaskAttIdName,
      String customStudyId) {
    logger.entry("begin validateActiveTaskAttrById()");
    boolean valid = false;
    try {
      if ((studyId != null)
          && StringUtils.isNotEmpty(activeTaskAttName)
          && StringUtils.isNotEmpty(activeTaskAttIdVal)
          && StringUtils.isNotEmpty(activeTaskAttIdName)) {
        valid =
            studyActiveTasksDAO.validateActiveTaskAttrById(
                studyId, activeTaskAttName, activeTaskAttIdVal, activeTaskAttIdName, customStudyId);
      }
    } catch (Exception e) {
      logger.error("StudyActiveTasksServiceImpl - validateActiveTaskAttrById() - ERROR ", e);
    }

    logger.exit("validateActiveTaskAttrById()  - Ends");
    return valid;
  }

  @Override
  public List<ActiveStatisticsBean> validateActiveTaskStatIds(
      String customStudyId, List<ActiveStatisticsBean> activeStatisticsBeans) {
    logger.entry("begin validateActiveTaskStatIds()");
    List<ActiveStatisticsBean> statisticsBeans = null;
    try {
      statisticsBeans =
          studyActiveTasksDAO.validateActiveTaskStatIds(customStudyId, activeStatisticsBeans);
    } catch (Exception e) {
      logger.error("StudyActiveTasksServiceImpl - validateActiveTaskStatIds() - ERROR ", e);
    }
    logger.exit("validateActiveTaskStatIds() - Ends");
    return statisticsBeans;
  }
}
