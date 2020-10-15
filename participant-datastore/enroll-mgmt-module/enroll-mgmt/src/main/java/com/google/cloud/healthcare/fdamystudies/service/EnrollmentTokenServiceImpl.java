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
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.util.EnrollmentManagementUtil;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnrollmentTokenServiceImpl implements EnrollmentTokenService {

  @Autowired EnrollmentTokenDao enrollmentTokenDao;

  @Autowired EnrollmentManagementUtil enrollUtil;

  @Autowired CommonService commonService;

  private static final Logger logger = LoggerFactory.getLogger(EnrollmentTokenServiceImpl.class);

  @Override
  @Transactional(readOnly = true)
  public boolean enrollmentTokenRequired(@NotNull String studyId) {
    logger.info("EnrollmentTokenServiceImpl enrollmentTokenRequired() - Starts ");
    boolean isTokenRequired = enrollmentTokenDao.enrollmentTokenRequired(studyId);
    logger.info("EnrollmentTokenServiceImpl enrollmentTokenRequired() - Ends ");
    return isTokenRequired;
  }

  @Override
  @Transactional(readOnly = true)
  public boolean hasParticipant(@NotNull String studyId, @NotNull String tokenValue) {
    logger.info("EnrollmentTokenServiceImpl hasParticipant() - Starts ");
    boolean hasParticipant = enrollmentTokenDao.hasParticipant(studyId, tokenValue);
    logger.info("EnrollmentTokenServiceImpl hasParticipant() - Ends ");
    return hasParticipant;
  }

  @Override
  @Transactional(readOnly = true)
  public boolean isValidStudyToken(
      @NotNull String token, @NotNull String studyId, @NotNull String userId) {
    logger.info("EnrollmentTokenServiceImpl isValidStudyToken() - Starts ");
    // fetching registered emailid
    UserDetailsEntity userDetails = commonService.getUserInfoDetails(userId);
    boolean isValidStudyToken =
        enrollmentTokenDao.isValidStudyToken(token, studyId, userDetails.getEmail());
    logger.info("EnrollmentTokenServiceImpl isValidStudyToken() - Ends ");
    return isValidStudyToken;
  }

  @Override
  @Transactional(readOnly = true)
  public boolean studyExists(@NotNull String studyId) {
    logger.info("EnrollmentTokenServiceImpl studyExists() - Starts ");
    boolean isStudyExist = enrollmentTokenDao.studyExists(studyId);
    logger.info("EnrollmentTokenServiceImpl studyExists() - Ends ");
    return isStudyExist;
  }

  @Override
  @Transactional
  public EnrollmentResponseBean enrollParticipant(
      @NotNull String shortName,
      String tokenValue,
      String userId,
      AuditLogEventRequest auditRequest) {
    logger.info("EnrollmentTokenServiceImpl enrollParticipant() - Starts ");
    boolean isTokenRequired = enrollmentTokenDao.enrollmentTokenRequired(shortName);
    String hashedTokenValue = EnrollmentManagementUtil.getHashedValue(tokenValue);
    String participantId =
        enrollUtil.getParticipantId("", hashedTokenValue, shortName, auditRequest);
    EnrollmentResponseBean participantBean =
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
