/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import static com.google.cloud.healthcare.fdamystudies.common.ConsentManagementEnum.READ_OPERATION_FAILED_FOR_SIGNED_CONSENT_DOCUMENT;
import static com.google.cloud.healthcare.fdamystudies.common.ConsentManagementEnum.READ_OPERATION_SUCCEEDED_FOR_SIGNED_CONSENT_DOCUMENT;

import com.google.cloud.healthcare.fdamystudies.bean.ConsentStudyResponseBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.ConsentAuditHelper;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.dao.UserConsentManagementDao;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyConsentEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.storage.StorageException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserConsentManagementServiceImpl implements UserConsentManagementService {
  @Autowired UserConsentManagementDao userConsentManagementDao;

  @Autowired FileStorageService cloudStorageService;

  @Autowired private ConsentAuditHelper consentAuditHelper;

  private XLogger logger =
      XLoggerFactory.getXLogger(UserConsentManagementServiceImpl.class.getName());

  @Override
  @Transactional(readOnly = true)
  public ParticipantStudyEntity getParticipantStudies(String studyId, String userId) {
    return userConsentManagementDao.getParticipantStudies(studyId, userId);
  }

  @Override
  @Transactional
  public String saveParticipantStudies(List<ParticipantStudyEntity> participantStudiesList) {
    return userConsentManagementDao.saveParticipantStudies(participantStudiesList);
  }

  @Override
  @Transactional(readOnly = true)
  public StudyConsentEntity getStudyConsent(String userId, String studyId, String consentVersion) {
    return userConsentManagementDao.getStudyConsent(userId, studyId, consentVersion);
  }

  @Override
  @Transactional
  public String saveStudyConsent(StudyConsentEntity studyConsent) {
    return userConsentManagementDao.saveStudyConsent(studyConsent);
  }

  @Override
  @Transactional(readOnly = true)
  public ConsentStudyResponseBean getStudyConsentDetails(
      String userId, String studyId, String consentVersion, AuditLogEventRequest auditRequest) {

    logger.entry("Begin getStudyConsentDetails() ");
    StudyConsentEntity studyConsent = null;
    ParticipantStudyEntity participantStudiesEntity = null;
    ConsentStudyResponseBean consentStudyResponseBean = new ConsentStudyResponseBean();

    studyConsent = userConsentManagementDao.getStudyConsent(userId, studyId, consentVersion);

    if (studyConsent != null) {
      if (studyConsent.getVersion() != null) {
        consentStudyResponseBean.getConsent().setVersion(studyConsent.getVersion());
      }
      if (studyConsent.getPdf() != null) {
        consentStudyResponseBean.getConsent().setContent(studyConsent.getPdf());
      }

      consentStudyResponseBean.getConsent().setType("application/pdf");
      participantStudiesEntity = userConsentManagementDao.getParticipantStudies(studyId, userId);
      if (participantStudiesEntity != null) {
        consentStudyResponseBean.setSharing(participantStudiesEntity.getSharing());
        if (studyConsent.getPdfStorage() == 1) {
          String path = studyConsent.getPdfPath();

          if (participantStudiesEntity.getParticipantId() != null) {
            auditRequest.setParticipantId(participantStudiesEntity.getParticipantId());
          }

          downloadConsentDocument(path, consentStudyResponseBean, userId, auditRequest);
        }
      }
    }

    logger.exit("getStudyConsentDetails() - Ends ");
    return consentStudyResponseBean;
  }

  private void downloadConsentDocument(
      String filepath,
      ConsentStudyResponseBean consentStudyResponseBean,
      String userId,
      AuditLogEventRequest auditRequest) {
    try {
      auditRequest.setUserId(userId);
      Map<String, String> map = Collections.singletonMap("file_name", filepath);
      String documentContent = cloudStorageService.getDocumentContent(filepath);
      consentStudyResponseBean.getConsent().setContent(documentContent);
      consentAuditHelper.logEvent(
          READ_OPERATION_SUCCEEDED_FOR_SIGNED_CONSENT_DOCUMENT, auditRequest, map);
    } catch (StorageException e) {
      consentAuditHelper.logEvent(READ_OPERATION_FAILED_FOR_SIGNED_CONSENT_DOCUMENT, auditRequest);
      logger.error("Download consent document from cloud storage failed", e);
      throw new ErrorCodeException(ErrorCode.APPLICATION_ERROR);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public StudyEntity getStudyInfo(String customStudyId) {
    return userConsentManagementDao.getStudyInfo(customStudyId);
  }

  @Override
  @Transactional(readOnly = true)
  public String getUserDetailsId(String userId) {
    return userConsentManagementDao.getUserDetailsId(userId);
  }
}
