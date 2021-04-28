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
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.util.EnrollmentManagementUtil;
import javax.validation.constraints.NotNull;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnrollmentTokenServiceImpl implements EnrollmentTokenService {

  @Autowired EnrollmentTokenDao enrollmentTokenDao;

  @Autowired EnrollmentManagementUtil enrollUtil;

  @Autowired CommonService commonService;

  private static final XLogger logger =
      XLoggerFactory.getXLogger(EnrollmentTokenServiceImpl.class.getName());

  @Override
  @Transactional(readOnly = true)
  public boolean enrollmentTokenRequired(String studyId) {
    logger.entry("Begin enrollmentTokenRequired()");
    boolean isTokenRequired = enrollmentTokenDao.enrollmentTokenRequired(studyId);
    logger.exit("enrollmentTokenRequired() - Ends ");
    return isTokenRequired;
  }

  @Override
  @Transactional(readOnly = true)
  public boolean hasParticipant(String studyId, @NotNull String tokenValue) {
    logger.entry("Begin hasParticipant()");
    boolean hasParticipant = enrollmentTokenDao.hasParticipant(studyId, tokenValue);
    logger.exit("hasParticipant() - Ends ");
    return hasParticipant;
  }

  @Override
  @Transactional(readOnly = true)
  public boolean isValidStudyToken(@NotNull String token, String studyId, String userId) {
    logger.entry("Begin isValidStudyToken()");
    // fetching registered emailid
    UserDetailsEntity userDetails = commonService.getUserInfoDetails(userId);
    boolean isValidStudyToken =
        enrollmentTokenDao.isValidStudyToken(token, studyId, userDetails.getEmail());
    logger.exit("isValidStudyToken() - Ends ");
    return isValidStudyToken;
  }

  @Override
  @Transactional(readOnly = true)
  public StudyEntity getStudyDetails(String studyId) {
    logger.entry("Begin getStudyDetails()");
    StudyEntity studyDetails = enrollmentTokenDao.getStudyDetails(studyId);
    logger.exit("getStudyDetails() - Ends ");
    return studyDetails;
  }

  @Override
  @Transactional
  public EnrollmentResponseBean enrollParticipant(
      String shortName,
      String tokenValue,
      String userId,
      Float studyVersion,
      AuditLogEventRequest auditRequest) {
    logger.entry("Begin enrollParticipant()");
    boolean isTokenRequired = enrollmentTokenDao.enrollmentTokenRequired(shortName);
    String hashedTokenValue = EnrollmentManagementUtil.getHashedValue(tokenValue.toUpperCase());
    String participantId =
        enrollUtil.getParticipantId("", hashedTokenValue, shortName, studyVersion, auditRequest);
    EnrollmentResponseBean participantBean =
        enrollmentTokenDao.enrollParticipant(
            shortName,
            tokenValue.toUpperCase(),
            commonService.getUserInfoDetails(userId),
            isTokenRequired,
            participantId);
    if (participantBean != null) {
      participantBean.setHashedToken(hashedTokenValue);
      participantBean.setParticipantId(participantId);
    }
    logger.exit("enrollParticipant() - Ends ");
    return participantBean;
  }
}
