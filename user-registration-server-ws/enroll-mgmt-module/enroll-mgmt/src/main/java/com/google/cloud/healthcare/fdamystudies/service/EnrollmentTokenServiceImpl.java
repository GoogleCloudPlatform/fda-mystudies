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
import com.google.cloud.healthcare.fdamystudies.exception.InvalidRequestException;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.exception.UnAuthorizedRequestException;
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
    logger.info("EnrollmentTokenServiceImpl hasParticipant() - Starts ");
    boolean isTokenRequired = false;
    try {
      isTokenRequired = enrollmentTokenDao.enrollmentTokenRequired(studyId);
    } catch (Exception e) {
      logger.error("EnrollmentTokenServiceImpl enrollmentTokenRequired() - error ", e);
    }
    return isTokenRequired;
  }

  @Override
  @Transactional(readOnly = true)
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
  @Transactional(readOnly = true)
  public boolean isValidStudyToken(@NotNull String token, @NotNull String studyId) {
    logger.info("EnrollmentTokenServiceImpl isValidStudyToken() - Starts ");
    boolean isValidStudyToken = false;
    try {
      isValidStudyToken = enrollmentTokenDao.isValidStudyToken(token, studyId);
    } catch (Exception e) {
      logger.error("EnrollmentTokenServiceImpl isValidStudyToken() - error ", e);
    }
    logger.info("EnrollmentTokenServiceImpl isValidStudyToken() - Ends ");
    return isValidStudyToken;
  }

  @Override
  @Transactional(readOnly = true)
  public boolean studyExists(@NotNull String studyId) {
    logger.info("EnrollmentTokenServiceImpl studyExists() - Starts ");
    boolean isStudyExist = false;
    try {
      isStudyExist = enrollmentTokenDao.studyExists(studyId);
    } catch (Exception e) {
      logger.error("EnrollmentTokenServiceImpl studyExists() - error ", e);
    }

    logger.info("EnrollmentTokenServiceImpl studyExists() - Ends ");
    return isStudyExist;
  }

  @Override
  @Transactional(readOnly = true)
  public EnrollmentResponseBean enrollParticipant(
      @NotNull String shortName,
      String tokenValue,
      String userId,
      AuditLogEventRequest auditRequest)
      throws SystemException, InvalidRequestException, UnAuthorizedRequestException {
    logger.info("EnrollmentTokenServiceImpl enrollParticipant() - Starts ");
    EnrollmentResponseBean participantBean = null;
    String hashedTokenValue = "";
    boolean isTokenRequired = false;
    String participantId = "";
    try {
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
    } catch (InvalidRequestException | UnAuthorizedRequestException e) {
      logger.error("EnrollmentTokenServiceImpl enrollParticipant() - error ", e);
      throw e;
    } catch (Exception e) {
      logger.error("********EnrollmentTokenServiceImpl enrollParticipant() - error ", e);
      throw new SystemException();
    }
    logger.info("EnrollmentTokenServiceImpl enrollParticipant() - Ends ");
    return participantBean;
  }
}
