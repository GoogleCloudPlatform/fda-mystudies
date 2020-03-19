package com.google.cloud.healthcare.fdamystudies.service;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.healthcare.fdamystudies.beans.StudiesBean;
import com.google.cloud.healthcare.fdamystudies.beans.StudyStateBean;
import com.google.cloud.healthcare.fdamystudies.beans.StudyStateRespBean;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.dao.ParticipantStudiesInfoDao;
import com.google.cloud.healthcare.fdamystudies.dao.StudyStateDao;
import com.google.cloud.healthcare.fdamystudies.dao.UserRegAdminUserDao;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidUserIdException;
import com.google.cloud.healthcare.fdamystudies.exception.NoStudyEnrolledException;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetails;
import com.google.cloud.healthcare.fdamystudies.util.BeanUtil;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;

@Service
public class StudyStateServiceImpl implements StudyStateService {
  private static final Logger logger = LoggerFactory.getLogger(StudyStateServiceImpl.class);

  @Autowired StudyStateDao studyStateDao;
  @Autowired CommonDao commonDao;

  @Autowired private UserRegAdminUserDao userRegAdminUserDao;

  @Autowired private ParticipantStudiesInfoDao participantStudiesInfoDao;

  @Override
  public List<ParticipantStudiesBO> getParticipantStudiesList(String userId) {
    logger.info("StudyStateServiceImpl getParticipantStudiesList() - Starts ");
    List<ParticipantStudiesBO> participantStudiesList = null;
    try {
      participantStudiesList = studyStateDao.getParticipantStudiesList(userId);
    } catch (Exception e) {
      logger.error("StudyStateServiceImpl getParticipantStudiesList() - error ", e);
    }
    logger.info("StudyStateServiceImpl getParticipantStudiesList() - Ends ");
    return participantStudiesList;
  }

  @Override
  public StudyStateRespBean saveParticipantStudies(
      List<StudiesBean> studiesBeenList,
      List<ParticipantStudiesBO> existParticipantStudies,
      String userId) {
    logger.info("StudyStateServiceImpl saveParticipantStudies() - Starts ");
    StudyStateRespBean studyStateRespBean = null;
    String message = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
    boolean isExists = false;
    List<ParticipantStudiesBO> addParticipantStudiesList = new ArrayList<ParticipantStudiesBO>();
    ParticipantStudiesBO participantStudyBo = new ParticipantStudiesBO();
    try {
      for (int i = 0; i < studiesBeenList.size(); i++) {
        StudiesBean studiesBean = studiesBeenList.get(i);
        Integer studyInfoId = commonDao.getStudyId(studiesBean.getStudyId());
        if (existParticipantStudies != null && !existParticipantStudies.isEmpty()) {
          for (ParticipantStudiesBO participantStudies : existParticipantStudies) {
            if (studyInfoId.equals(participantStudies.getStudyId())) {
              isExists = true;
              if (participantStudies.getStatus() != null
                  && participantStudies
                      .getStatus()
                      .equalsIgnoreCase(MyStudiesUserRegUtil.ErrorCodes.YET_TO_JOIN.getValue())) {
                participantStudies.setEnrolledDate(MyStudiesUserRegUtil.getCurrentUtilDateTime());
              }
              if (studiesBean.getStatus() != null
                  && !StringUtils.isEmpty(studiesBean.getStatus())) {
                participantStudies.setStatus(studiesBean.getStatus());
                if (studiesBean
                    .getStatus()
                    .equalsIgnoreCase(MyStudiesUserRegUtil.ErrorCodes.IN_PROGRESS.getValue())) {
                  /* List<ParticipantActivities> participantActivitiesList =
                      FdahpUserRegWSManager.get()
                          .getParticipantActivitiesList(
                              studiesBean.getStudyId(), userId, applicationId, orgId);
                  if (participantActivitiesList != null && participantActivitiesList.size() > 0) {
                    for (ParticipantActivities participantActivities : participantActivitiesList) {
                      participantActivities.setActivityVersion(null);
                      participantActivities.setActivityState(null);
                      participantActivities.setActivityRunId(null);
                      participantActivities.setBookmark(false);
                      participantActivities.setTotal(0);
                      participantActivities.setCompleted(0);
                      participantActivities.setMissed(0);
                    }
                    FdahpUserRegWSManager.get()
                        .saveParticipantActivities(participantActivitiesList);
                  }*/
                  participantStudies.setEnrolledDate(MyStudiesUserRegUtil.getCurrentUtilDateTime());
                }
                if (studiesBean.getBookmarked() != null)
                  participantStudies.setBookmark(studiesBean.getBookmarked());
                if (studiesBean.getCompletion() != null)
                  participantStudies.setCompletion(studiesBean.getCompletion());
                if (studiesBean.getAdherence() != null)
                  participantStudies.setAdherence(studiesBean.getAdherence());
                if (studiesBean.getParticipantId() != null
                    && StringUtils.isNotEmpty(studiesBean.getParticipantId()))
                  participantStudies.setParticipantId(studiesBean.getParticipantId());
                addParticipantStudiesList.add(participantStudies);
              }
            }
          }
        }
        if (!isExists) {
          if (studiesBean.getStudyId() != null && StringUtils.isNotEmpty(studiesBean.getStudyId()))
            participantStudyBo.setStudyId(studyInfoId);
          if (studiesBean.getStatus() != null && StringUtils.isNotEmpty(studiesBean.getStatus())) {
            participantStudyBo.setStatus(studiesBean.getStatus());
            if (studiesBean
                .getStatus()
                .equalsIgnoreCase(MyStudiesUserRegUtil.ErrorCodes.IN_PROGRESS.getValue())) {
              participantStudyBo.setEnrolledDate(MyStudiesUserRegUtil.getCurrentUtilDateTime());
            }
          } else {
            participantStudyBo.setStatus(MyStudiesUserRegUtil.ErrorCodes.YET_TO_JOIN.getValue());
          }
          if (studiesBean.getBookmarked() != null)
            participantStudyBo.setBookmark(studiesBean.getBookmarked());
          if (userId != null && StringUtils.isNotEmpty(userId))
            participantStudyBo.setUserId(commonDao.getUserInfoDetails(userId));
          if (studiesBean.getCompletion() != null)
            participantStudyBo.setCompletion(studiesBean.getCompletion());
          if (studiesBean.getAdherence() != null)
            participantStudyBo.setAdherence(studiesBean.getAdherence());
          if (studiesBean.getParticipantId() != null
              && StringUtils.isNotEmpty(studiesBean.getParticipantId()))
            participantStudyBo.setParticipantId(studiesBean.getParticipantId());
          addParticipantStudiesList.add(participantStudyBo);
        }
      }
      message = studyStateDao.saveParticipantStudies(addParticipantStudiesList);
      if (message.equalsIgnoreCase(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue())) {
        studyStateRespBean = new StudyStateRespBean();
        studyStateRespBean.setMessage(
            MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
      }
    } catch (Exception e) {
      logger.error("StudyStateServiceImpl saveParticipantStudies() - error ", e);
    }

    logger.info("StudyStateServiceImpl saveParticipantStudies() - Ends ");
    return studyStateRespBean;
  }

  @Override
  public List<StudyStateBean> getStudiesState(String userId)
      throws SystemException, InvalidUserIdException, NoStudyEnrolledException {
    logger.info("(Service)...StudyStateServiceImpl.getStudiesState()...Started");

    List<StudyStateBean> serviceResponseList = new ArrayList<>();

    if (userId != null) {
      try {
        UserDetails userDetails = userRegAdminUserDao.getRecord(userId);
        logger.info("(Service)...userDetails: " + userDetails);
        if (userDetails != null) {
          // TODO: get List of studies from participantStudyInfo table

          List<ParticipantStudiesBO> participantStudiesList =
              participantStudiesInfoDao.getParticipantStudiesInfo(userDetails.getUserDetailsId());
          logger.info("PARTICIPANT Studies List SIZE: " + participantStudiesList.size());
          if (participantStudiesList != null && participantStudiesList.size() > 0) {
            for (ParticipantStudiesBO participantStudiesBO : participantStudiesList) {
              logger.info("PARTICIPANT Study: " + participantStudiesBO);
              StudyStateBean studyStateBean = BeanUtil.getBean(StudyStateBean.class);
              studyStateBean.setStudyId(participantStudiesBO.getStudyId());
              studyStateBean.setStatus(participantStudiesBO.getStatus());
              studyStateBean.setParticipantId(participantStudiesBO.getParticipantId());
              studyStateBean.setCompletion(participantStudiesBO.getCompletion());

              studyStateBean.setBookmarked(participantStudiesBO.getBookmark());
              studyStateBean.setAdherence(participantStudiesBO.getAdherence());
              serviceResponseList.add(studyStateBean);
            }
            return serviceResponseList;
          } else {
            throw new NoStudyEnrolledException();
          }
        } else {
          throw new InvalidUserIdException();
        }
      } catch (NoStudyEnrolledException e) {
        logger.error("(Service)...StudyStateServiceImpl.getStudiesState(): (ERROR) ", e);
        throw e;
      } catch (InvalidUserIdException e) {
        logger.error("(Service)...StudyStateServiceImpl.getStudiesState(): (ERROR) ", e);
        throw e;
      } catch (SystemException e) {
        logger.error("(Service)...StudyStateServiceImpl.getStudiesState(): (ERROR) ", e);
        throw e;
      } catch (Exception e) {
        logger.error("(Service)...StudyStateServiceImpl.getStudiesState(): (ERROR) ", e);
        throw new SystemException();
      }
    } else {
      logger.info("(Service)...StudyStateServiceImpl.getStudiesState()...Ended");
      return null;
    }
  }
}
