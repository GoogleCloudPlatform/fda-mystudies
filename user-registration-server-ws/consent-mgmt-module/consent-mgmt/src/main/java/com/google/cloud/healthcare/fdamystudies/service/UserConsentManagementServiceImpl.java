/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import static com.google.cloud.healthcare.fdamystudies.common.ConsentManagementEnum.READ_OPERATION_FAILED_FOR_SIGNED_CONSENT_DOCUMENT;
import static com.google.cloud.healthcare.fdamystudies.common.ConsentManagementEnum.READ_OPERATION_SUCCEEDED_FOR_SIGNED_CONSENT_DOCUMENT;

import com.google.cloud.healthcare.fdamystudies.bean.ConsentStudyResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.StudyInfoBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.ConsentAuditHelper;
import com.google.cloud.healthcare.fdamystudies.consent.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.consent.model.StudyConsentBO;
import com.google.cloud.healthcare.fdamystudies.dao.UserConsentManagementDao;
import com.google.cloud.storage.StorageException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserConsentManagementServiceImpl implements UserConsentManagementService {
  @Autowired UserConsentManagementDao userConsentManagementDao;

  @Autowired FileStorageService cloudStorageService;

  @Autowired private ConsentAuditHelper consentAuditHelper;

  private static final Logger logger =
      LoggerFactory.getLogger(UserConsentManagementServiceImpl.class);

  @Override
  @Transactional(readOnly = true)
  public ParticipantStudiesBO getParticipantStudies(Integer studyId, String userId) {
    logger.info("UserConsentManagementServiceImpl getParticipantStudies() - Started ");

    ParticipantStudiesBO participantStudiesBO =
        userConsentManagementDao.getParticipantStudies(studyId, userId);
    logger.info("UserConsentManagementServiceImpl getParticipantStudies() - Ends ");
    return participantStudiesBO;
  }

  @Override
  @Transactional
  public String saveParticipantStudies(List<ParticipantStudiesBO> participantStudiesList) {
    logger.info("UserConsentManagementServiceImpl saveParticipantStudies() - Started ");

    String message = userConsentManagementDao.saveParticipantStudies(participantStudiesList);
    logger.info("UserConsentManagementServiceImpl saveParticipantStudies() - Ends ");
    return message;
  }

  @Override
  @Transactional(readOnly = true)
  public StudyConsentBO getStudyConsent(String userId, Integer studyId, String consentVersion) {
    logger.info("UserConsentManagementServiceImpl getStudyConsent() - Started ");

    StudyConsentBO studyConsent =
        userConsentManagementDao.getStudyConsent(userId, studyId, consentVersion);

    logger.info("UserConsentManagementServiceImpl getStudyConsent() - Ends ");
    return studyConsent;
  }

  @Override
  @Transactional
  public String saveStudyConsent(StudyConsentBO studyConsent) {
    logger.info("UserConsentManagementServiceImpl saveStudyConsent() - Started ");

    String addOrUpdateConsentMessage = userConsentManagementDao.saveStudyConsent(studyConsent);

    logger.info("UserConsentManagementServiceImpl saveStudyConsent() - Ends ");
    return addOrUpdateConsentMessage;
  }

  @Override
  @Transactional(readOnly = true)
  public ConsentStudyResponseBean getStudyConsentDetails(
      String userId, Integer studyId, String consentVersion, AuditLogEventRequest auditRequest) {

    logger.info("UserConsentManagementServiceImpl getStudyConsentDetails() - Started ");
    StudyConsentBO studyConsent = null;
    ParticipantStudiesBO participantStudiesBO = null;
    ConsentStudyResponseBean consentStudyResponseBean = new ConsentStudyResponseBean();

    studyConsent = userConsentManagementDao.getStudyConsent(userId, studyId, consentVersion);

    if (studyConsent != null) {
      if (studyConsent.getVersion() != null) {
        consentStudyResponseBean.getConsent().setVersion(studyConsent.getVersion());
      }
      if (studyConsent.getPdf() != null) {
        consentStudyResponseBean.getConsent().setContent(studyConsent.getPdf());
      }

      if (studyConsent.getPdfStorage() == 1) {
        String path = studyConsent.getPdfPath();

        downloadConsentDocument(path, consentStudyResponseBean, userId, auditRequest);
      }
      consentStudyResponseBean.getConsent().setType("application/pdf");
      participantStudiesBO = userConsentManagementDao.getParticipantStudies(studyId, userId);
      if (participantStudiesBO != null) {
        consentStudyResponseBean.setSharing(participantStudiesBO.getSharing());
      }
    }

    logger.info("UserConsentManagementServiceImpl getStudyConsentDetails() - Ends ");
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
    }
  }

  @Override
  @Transactional(readOnly = true)
  public StudyInfoBean getStudyInfoId(String customStudyId) {
    logger.info("UserConsentManagementServiceImpl getStudyInfoId() - Starts ");

    StudyInfoBean studyInfoBean = userConsentManagementDao.getStudyInfoId(customStudyId);

    logger.info("UserConsentManagementServiceImpl getStudyInfoId() - Ends ");
    return studyInfoBean;
  }

  @Override
  @Transactional(readOnly = true)
  public Integer getUserDetailsId(String userId) {
    logger.info("UserConsentManagementServiceImpl getUserDetailsId() - Starts ");

    Integer userDetailId = userConsentManagementDao.getUserDetailsId(userId);

    logger.info("UserConsentManagementServiceImpl getUserDetailsId() - Ends ");
    return userDetailId;
  }
}
