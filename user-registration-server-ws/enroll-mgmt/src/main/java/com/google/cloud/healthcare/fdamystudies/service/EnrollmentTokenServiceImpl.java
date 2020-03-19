package com.google.cloud.healthcare.fdamystudies.service;

import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.healthcare.fdamystudies.beans.EnrollmentResponseBean;
import com.google.cloud.healthcare.fdamystudies.dao.EnrollmentTokenDao;
import com.google.cloud.healthcare.fdamystudies.util.EnrollmentManagementUtil;

@Service
public class EnrollmentTokenServiceImpl implements EnrollmentTokenService {

  @Autowired EnrollmentTokenDao enrollmentTokenDao;

  @Autowired EnrollmentManagementUtil enrollUtil;

  @Autowired CommonService commonService;

  private static final Logger logger = LoggerFactory.getLogger(EnrollmentTokenServiceImpl.class);

  @Override
  public boolean enrollmentTokenRequired(@NotNull String studyId) {
    logger.info("EnrollmentTokenServiceImpl hasParticipant() - Starts ");
    boolean isTokenRequired = false;
    try {
      isTokenRequired = enrollmentTokenDao.enrollmentTokenRequired(studyId);
    } catch (Exception e) {
      logger.error("EnrollmentTokenServiceImpl enrollmentTokenRequired() - error ", e);
    }
    //    return isTokenRequired;
    return true;
  }

  @Override
  public boolean hasParticipant(@NotNull String studyId, @NotNull String tokenValue) {
    logger.info("EnrollmentTokenServiceImpl hasParticipant() - Starts ");
    boolean hasParticipant = false;
    try {
      hasParticipant = enrollmentTokenDao.hasParticipant(studyId, tokenValue);
    } catch (Exception e) {
      logger.error("EnrollmentTokenServiceImpl hasParticipant() - error ", e);
    }
    logger.info("EnrollmentTokenServiceImpl hasParticipant() - Ends ");
    return hasParticipant;
  }

  @Override
  public boolean isValidStudyToken(@NotNull String token, @NotNull String studyId) {
    logger.info("EnrollmentTokenServiceImpl isValidStudyToken() - Starts ");
    boolean isValidStudyToken = false;
    try {
      isValidStudyToken = enrollmentTokenDao.isValidStudyToken(token, studyId);
    } catch (Exception e) {
      logger.error("EnrollmentTokenServiceImpl isValidStudyToken() - error ", e);
    }
    logger.info("EnrollmentTokenServiceImpl isValidStudyToken() - Ends ");
    //    return isValidStudyToken;
    return true;
  }

  @Override
  public boolean studyExists(@NotNull String studyId) {
    logger.info("EnrollmentTokenServiceImpl studyExists() - Starts ");
    boolean isStudyExist = false;
    try {
      isStudyExist = enrollmentTokenDao.studyExists(studyId);
    } catch (Exception e) {
      logger.error("EnrollmentTokenServiceImpl studyExists() - error ", e);
    }

    logger.info("EnrollmentTokenServiceImpl studyExists() - Ends ");
    //    return isStudyExist;
    return true;
  }

  @Override
  public EnrollmentResponseBean enrollParticipant(
      @NotNull String shortName, String tokenValue, String userId) {
    logger.info("EnrollmentTokenServiceImpl enrollParticipant() - Starts ");
    EnrollmentResponseBean participantBean = null;
    String hashedTokenValue = "";
    boolean isTokenRequired = false;
    try {
      isTokenRequired = enrollmentTokenDao.enrollmentTokenRequired(shortName);
      hashedTokenValue = EnrollmentManagementUtil.getHashedValue(tokenValue);
      participantBean =
          enrollmentTokenDao.enrollParticipant(
              shortName,
              tokenValue,
              commonService.getUserInfoDetails(userId),
              isTokenRequired,
              enrollUtil.getParticipantId("", "", "", hashedTokenValue, shortName));
      if (participantBean != null) {
        participantBean.setHashedToken(hashedTokenValue);
      }
    } catch (Exception e) {
      logger.error("EnrollmentTokenServiceImpl enrollParticipant() - error ", e);
    }
    logger.info("EnrollmentTokenServiceImpl enrollParticipant() - Ends ");
    return participantBean;
  }
}
