/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.service;

import com.fdahpstudydesigner.bean.ActiveStatisticsBean;
import com.fdahpstudydesigner.bo.ActiveTaskBo;
import com.fdahpstudydesigner.bo.ActiveTaskListBo;
import com.fdahpstudydesigner.bo.ActiveTaskMasterAttributeBo;
import com.fdahpstudydesigner.bo.ActivetaskFormulaBo;
import com.fdahpstudydesigner.bo.StatisticImageListBo;
import com.fdahpstudydesigner.util.SessionObject;
import java.util.List;

public interface StudyActiveTasksService {

  public String deleteActiveTask(
      String activeTaskInfoId, String studyId, SessionObject sesObj, String customStudyId);

  public ActiveTaskBo getActiveTaskById(String activeTaskId, String customStudyId);

  public List<ActivetaskFormulaBo> getActivetaskFormulas();

  public List<ActiveTaskMasterAttributeBo> getActiveTaskMasterAttributesByType(
      String activeTaskType);

  public List<ActiveTaskListBo> getAllActiveTaskTypes(String platformType);

  public List<StatisticImageListBo> getStatisticImages();

  public List<ActiveTaskBo> getStudyActiveTasksByStudyId(String studyId, Boolean isLive);

  public ActiveTaskBo saveOrUpdateActiveTask(
      ActiveTaskBo activeTaskBo, SessionObject sessionObject, String customStudyId);

  public ActiveTaskBo saveOrUpdateActiveTask(ActiveTaskBo activeTaskBo, String customStudyId);

  public boolean validateActiveTaskAttrById(
      String studyId,
      String activeTaskName,
      String activeTaskAttIdVal,
      String activeTaskAttIdName,
      String customStudyId);

  public List<ActiveStatisticsBean> validateActiveTaskStatIds(
      String customStudyId, List<ActiveStatisticsBean> activeStatisticsBeans);
}
