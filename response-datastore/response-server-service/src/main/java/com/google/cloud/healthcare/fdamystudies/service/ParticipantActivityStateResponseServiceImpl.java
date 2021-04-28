/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.bean.ActivitiesBean;
import com.google.cloud.healthcare.fdamystudies.bean.ActivityRunBean;
import com.google.cloud.healthcare.fdamystudies.bean.ActivityStateRequestBean;
import com.google.cloud.healthcare.fdamystudies.bean.ParticipantActivityBean;
import com.google.cloud.healthcare.fdamystudies.dao.ParticipantActivitiesDao;
import com.google.cloud.healthcare.fdamystudies.exception.ProcessActivityStateException;
import com.google.cloud.healthcare.fdamystudies.response.model.ParticipantActivitiesEntity;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParticipantActivityStateResponseServiceImpl
    implements ParticipantActivityStateResponseService {
  @Autowired private ParticipantActivitiesDao participantActivitiesDao;

  private XLogger logger =
      XLoggerFactory.getXLogger(ParticipantActivityStateResponseServiceImpl.class.getName());

  @Override
  public ActivitiesBean getParticipantActivities(String studyId, String participantId)
      throws ProcessActivityStateException {
    logger.entry("begin getParticipantActivities()");
    List<ParticipantActivitiesEntity> participantActivityList = null;
    ActivitiesBean retActivitiesBean = new ActivitiesBean();
    retActivitiesBean.setMessage(AppConstants.FAILURE);
    participantActivityList =
        participantActivitiesDao.getParticipantActivities(studyId, participantId);
    if (!participantActivityList.isEmpty()) {
      List<ParticipantActivityBean> participantActivityBeanList = new ArrayList<>();
      for (ParticipantActivitiesEntity participantActivity : participantActivityList) {
        if (participantActivity != null) {
          ParticipantActivityBean tempParticipantActivityBean = new ParticipantActivityBean();
          ActivityRunBean tempActivityRunBean = new ActivityRunBean();
          tempParticipantActivityBean.setActivityId(participantActivity.getActivityId());
          if (!StringUtils.isBlank(participantActivity.getActivityState())) {
            tempParticipantActivityBean.setActivityState(participantActivity.getActivityState());
          }
          if (!StringUtils.isBlank(participantActivity.getActivityRunId())) {
            tempParticipantActivityBean.setActivityRunId(participantActivity.getActivityRunId());
          }
          if (!StringUtils.isBlank(participantActivity.getActivityVersion())) {
            tempParticipantActivityBean.setActivityVersion(
                participantActivity.getActivityVersion());
          }
          if (participantActivity.getBookmark() != null) {
            tempParticipantActivityBean.setBookmarked(participantActivity.getBookmark());
          }
          if (participantActivity.getCompletedCount() != null) {
            tempActivityRunBean.setCompleted(participantActivity.getCompletedCount());
          }
          if (participantActivity.getMissedCount() != null) {
            tempActivityRunBean.setMissed(participantActivity.getMissedCount());
          }
          if (participantActivity.getTotalCount() != null) {
            tempActivityRunBean.setTotal(participantActivity.getTotalCount());
          }
          tempParticipantActivityBean.setActivityRun(tempActivityRunBean);
          participantActivityBeanList.add(tempParticipantActivityBean);
        }
      }
      retActivitiesBean.setActivities(participantActivityBeanList);
      retActivitiesBean.setMessage(AppConstants.SUCCESS_MSG);
    }
    logger.exit("getParticipantActivities() - Ends ");
    return retActivitiesBean;
  }

  @Override
  public void saveParticipantActivities(ActivityStateRequestBean activityStateRequestBean)
      throws ProcessActivityStateException {
    logger.entry("begin saveParticipantActivities()");
    if (activityStateRequestBean.getStudyId() != null
        && activityStateRequestBean.getParticipantId() != null) {
      List<ParticipantActivitiesEntity> inputParticipantActivitiesList =
          this.getDtoObject(activityStateRequestBean);

      List<ParticipantActivitiesEntity> existingParticipantActivitiesList =
          participantActivitiesDao.getParticipantActivities(
              activityStateRequestBean.getStudyId(), activityStateRequestBean.getParticipantId());
      List<ParticipantActivitiesEntity> participantActivitiesListToUpdate =
          this.getConsolidatedParticipantListToUpdate(
              inputParticipantActivitiesList, existingParticipantActivitiesList);

      participantActivitiesDao.saveParticipantActivities(participantActivitiesListToUpdate);
    } else {
      throw new ProcessActivityStateException(
          "saveParticipantActivities() - error. Provided input for"
              + " studyId or participantId is null ");
    }

    logger.exit("saveParticipantActivities() - Ends ");
  }

  @Override
  public void deleteParticipantActivites(String studyId, String participantId)
      throws ProcessActivityStateException {
    participantActivitiesDao.deleteParticipantActivites(studyId, participantId);
  }

  private List<ParticipantActivitiesEntity> getConsolidatedParticipantListToUpdate(
      List<ParticipantActivitiesEntity> inputParticipantActivitiesList,
      List<ParticipantActivitiesEntity> saveOrUpdateParticipantActivitiesList) {
    if (!inputParticipantActivitiesList.isEmpty()) {
      for (ParticipantActivitiesEntity participantActivityInput : inputParticipantActivitiesList) {
        boolean isExistingRecord = false;
        if (!saveOrUpdateParticipantActivitiesList.isEmpty()) {
          for (ParticipantActivitiesEntity participantActivityExisting :
              saveOrUpdateParticipantActivitiesList) {
            if (participantActivityInput
                .getActivityId()
                .equalsIgnoreCase(participantActivityExisting.getActivityId())) {
              isExistingRecord = true;
              participantActivityExisting.setActivityVersion(
                  participantActivityInput.getActivityVersion());
              participantActivityExisting.setActivityState(
                  participantActivityInput.getActivityState());
              participantActivityExisting.setActivityRunId(
                  participantActivityInput.getActivityRunId());
              participantActivityExisting.setBookmark(participantActivityInput.getBookmark());
              participantActivityExisting.setTotalCount(participantActivityInput.getTotalCount());
              participantActivityExisting.setCompletedCount(
                  participantActivityInput.getCompletedCount());
              participantActivityExisting.setMissedCount(participantActivityInput.getMissedCount());
            }
          }
        }
        if (!isExistingRecord) {
          saveOrUpdateParticipantActivitiesList.add(participantActivityInput);
        }
      }
    }
    return saveOrUpdateParticipantActivitiesList;
  }

  private List<ParticipantActivitiesEntity> getDtoObject(
      ActivityStateRequestBean activityStateRequestBean) {
    List<ParticipantActivitiesEntity> retList = new ArrayList<>();
    if (activityStateRequestBean != null && activityStateRequestBean.getActivity() != null) {
      for (ParticipantActivityBean activityRequestBean : activityStateRequestBean.getActivity()) {
        ParticipantActivitiesEntity tempParticipantActivitiesBo = new ParticipantActivitiesEntity();
        tempParticipantActivitiesBo.setActivityId(activityRequestBean.getActivityId());
        tempParticipantActivitiesBo.setActivityRunId(activityRequestBean.getActivityRunId());
        tempParticipantActivitiesBo.setActivityState(activityRequestBean.getActivityState());
        tempParticipantActivitiesBo.setActivityVersion(activityRequestBean.getActivityVersion());
        tempParticipantActivitiesBo.setBookmark(activityRequestBean.getBookmarked());
        tempParticipantActivitiesBo.setParticipantId(activityStateRequestBean.getParticipantId());
        tempParticipantActivitiesBo.setStudyId(activityStateRequestBean.getStudyId());
        if (activityRequestBean.getActivityRun() != null) {
          tempParticipantActivitiesBo.setTotalCount(
              activityRequestBean.getActivityRun().getTotal());
          tempParticipantActivitiesBo.setMissedCount(
              activityRequestBean.getActivityRun().getMissed());
          tempParticipantActivitiesBo.setCompletedCount(
              activityRequestBean.getActivityRun().getCompleted());
        }
        retList.add(tempParticipantActivitiesBo);
      }
    }
    return retList;
  }
}
