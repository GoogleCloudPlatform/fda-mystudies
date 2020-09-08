/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.EnrollmentResponseBean;
import com.google.cloud.healthcare.fdamystudies.dao.EnrollmentTokenDao;
import com.google.cloud.healthcare.fdamystudies.util.EnrollmentManagementUtil;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnrollmentTokenServiceImpl implements EnrollmentTokenService {

  @Autowired EnrollmentTokenDao enrollmentTokenDao;

  @Autowired EnrollmentManagementUtil enrollUtil;

  @Autowired CommonService commonService;

  private static final Logger logger = LoggerFactory.getLogger(EnrollmentTokenServiceImpl.class);

  @Override
  public boolean enrollmentTokenRequired(@NotNull String studyId) {
    logger.info("EnrollmentTokenServiceImpl enrollmentTokenRequired() - Starts ");
    boolean isTokenRequired = false;
    isTokenRequired = enrollmentTokenDao.enrollmentTokenRequired(studyId);
    logger.info("EnrollmentTokenServiceImpl enrollmentTokenRequired() - Ends ");
    return isTokenRequired;
  }

  @Override
  public boolean hasParticipant(@NotNull String studyId, @NotNull String tokenValue) {
    logger.info("EnrollmentTokenServiceImpl hasParticipant() - Starts ");
    boolean hasParticipant = false;
    hasParticipant = enrollmentTokenDao.hasParticipant(studyId, tokenValue);
    logger.info("EnrollmentTokenServiceImpl hasParticipant() - Ends ");
    return hasParticipant;
  }

  @Override
  public boolean isValidStudyToken(@NotNull String token, @NotNull String studyId) {
    logger.info("EnrollmentTokenServiceImpl isValidStudyToken() - Starts ");
    boolean isValidStudyToken = false;
    isValidStudyToken = enrollmentTokenDao.isValidStudyToken(token, studyId);
    logger.info("EnrollmentTokenServiceImpl isValidStudyToken() - Ends ");
    return isValidStudyToken;
  }

  @Override
  public boolean studyExists(@NotNull String studyId) {
    logger.info("EnrollmentTokenServiceImpl studyExists() - Starts ");
    boolean isStudyExist = false;
    isStudyExist = enrollmentTokenDao.studyExists(studyId);
    logger.info("EnrollmentTokenServiceImpl studyExists() - Ends ");
    return isStudyExist;
  }

  @Override
  public EnrollmentResponseBean enrollParticipant(
      @NotNull String shortName,
      String tokenValue,
      String userId,
      AuditLogEventRequest auditRequest)
      throws Exception {
    logger.info("EnrollmentTokenServiceImpl enrollParticipant() - Starts ");
    EnrollmentResponseBean participantBean = null;
    String hashedTokenValue = "";
    boolean isTokenRequired = false;
    String participantId = "";
    isTokenRequired = enrollmentTokenDao.enrollmentTokenRequired(shortName);
    hashedTokenValue = EnrollmentManagementUtil.getHashedValue(tokenValue);
    participantId = enrollUtil.getParticipantId("", hashedTokenValue, shortName, auditRequest);
    participantBean =
        enrollmentTokenDao.enrollParticipant(
            shortName,
            tokenValue,
            commonService.getUserInfoDetails(userId),
            isTokenRequired,
            participantId);
    if (participantBean != null) {
      participantBean.setHashedToken(hashedTokenValue);
      participantBean.setParticipantId(participantId);
    }
    logger.info("EnrollmentTokenServiceImpl enrollParticipant() - Ends ");
    return participantBean;
  }
}
