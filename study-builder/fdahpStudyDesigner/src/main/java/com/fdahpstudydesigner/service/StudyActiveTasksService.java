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
