/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.dao;

import com.fdahpstudydesigner.bean.ActiveStatisticsBean;
import com.fdahpstudydesigner.bo.ActiveTaskAtrributeValuesBo;
import com.fdahpstudydesigner.bo.ActiveTaskBo;
import com.fdahpstudydesigner.bo.ActiveTaskCustomScheduleBo;
import com.fdahpstudydesigner.bo.ActiveTaskFrequencyBo;
import com.fdahpstudydesigner.bo.ActiveTaskListBo;
import com.fdahpstudydesigner.bo.ActiveTaskMasterAttributeBo;
import com.fdahpstudydesigner.bo.ActivetaskFormulaBo;
import com.fdahpstudydesigner.bo.StatisticImageListBo;
import com.fdahpstudydesigner.util.SessionObject;
import java.util.List;

public interface StudyActiveTasksDAO {

  public String deleteActiveTask(
      ActiveTaskBo activeTaskBo, SessionObject sesObj, String customStudyId);

  public ActiveTaskBo getActiveTaskById(String activeTaskId, String customStudyId);

  public List<ActivetaskFormulaBo> getActivetaskFormulas();

  public List<ActiveTaskMasterAttributeBo> getActiveTaskMasterAttributesByType(
      String activeTaskType);

  public List<ActiveTaskListBo> getAllActiveTaskTypes(String platformType);

  public List<StatisticImageListBo> getStatisticImages();

  public List<ActiveTaskBo> getStudyActiveTasksByStudyId(String studyId, Boolean isLive);

  public ActiveTaskBo saveOrUpdateActiveTask(ActiveTaskBo addActiveTaskeBo, String customStudyId);

  public ActiveTaskBo saveOrUpdateActiveTaskInfo(
      ActiveTaskBo activeTaskBo, SessionObject sesObj, String customStudyId);

  public boolean validateActiveTaskAttrById(
      String studyId,
      String activeTaskName,
      String activeTaskAttIdVal,
      String activeTaskAttIdName,
      String customStudyId);

  public List<ActiveStatisticsBean> validateActiveTaskStatIds(
      String customStudyId, List<ActiveStatisticsBean> activeStatisticsBeans);

  public List<ActiveTaskBo> getStudyActiveTaskByStudyId(String studyId);

  public List<ActiveTaskAtrributeValuesBo> getActiveTaskAtrributeValuesByActiveTaskId(
      List<String> activeTaskId);

  public List<ActiveTaskCustomScheduleBo> getActiveTaskCustomScheduleBoList(
      List<String> activeTaskId);

  public List<ActiveTaskFrequencyBo> getActiveTaskFrequencyBoList(List<String> activeTaskId);

  public List<ActiveTaskMasterAttributeBo> getActiveTaskMasterAttributesByType(
      List<String> activeTaskType);

  public List<ActiveTaskCustomScheduleBo> getActivetaskCustomFrequencies(String activetaskId);
}
