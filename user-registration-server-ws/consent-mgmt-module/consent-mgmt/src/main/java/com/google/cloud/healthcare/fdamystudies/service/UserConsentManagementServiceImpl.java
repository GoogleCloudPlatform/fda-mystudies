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
import com.google.cloud.healthcare.fdamystudies.common.DataSharingStatus;
import com.google.cloud.healthcare.fdamystudies.dao.UserConsentManagementDao;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyConsentEntity;
import com.google.cloud.healthcare.fdamystudies.utils.MyStudiesUserRegUtil;
import com.google.cloud.storage.StorageException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
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
  public ParticipantStudyEntity getParticipantStudies(String studyId, String userId) {
    logger.info("UserConsentManagementServiceImpl getParticipantStudies() - Started ");
    ParticipantStudyEntity participantStudiesEntity = null;
    try {
      participantStudiesEntity = userConsentManagementDao.getParticipantStudies(studyId, userId);
    } catch (Exception e) {
      logger.error("UserConsentManagementServiceImpl getParticipantStudies() - error ", e);
    }
    return participantStudiesEntity;
  }

  @Override
  @Transactional
  public String saveParticipantStudies(List<ParticipantStudyEntity> participantStudiesList) {
    logger.info("UserConsentManagementServiceImpl saveParticipantStudies() - Started ");
    String message = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
    try {
      message = userConsentManagementDao.saveParticipantStudies(participantStudiesList);
    } catch (Exception e) {
      logger.error("UserConsentManagementServiceImpl saveParticipantStudies() - error ", e);
    }

    return message;
  }

  @Override
  @Transactional(readOnly = true)
  public StudyConsentEntity getStudyConsent(String userId, String studyId, String consentVersion) {
    logger.info("UserConsentManagementServiceImpl getStudyConsent() - Started ");
    StudyConsentEntity studyConsent = null;
    try {
      studyConsent = userConsentManagementDao.getStudyConsent(userId, studyId, consentVersion);
    } catch (Exception e) {
      logger.error("UserConsentManagementServiceImpl getStudyConsent() - error ", e);
    }
    logger.info("UserConsentManagementServiceImpl getStudyConsent() - Ends ");
    return studyConsent;
  }

  @Override
  @Transactional
  public String saveStudyConsent(StudyConsentEntity studyConsent) {
    logger.info("UserConsentManagementServiceImpl saveStudyConsent() - Started ");
    String addOrUpdateConsentMessage = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
    try {

      addOrUpdateConsentMessage = userConsentManagementDao.saveStudyConsent(studyConsent);

    } catch (Exception e) {
      logger.error("UserConsentManagementServiceImpl saveStudyConsent() - error ", e);
    }
    logger.info("UserConsentManagementServiceImpl saveStudyConsent() - Ends ");
    return addOrUpdateConsentMessage;
  }

  @Override
  @Transactional(readOnly = true)
  public ConsentStudyResponseBean getStudyConsentDetails(
      String userId, String studyId, String consentVersion, AuditLogEventRequest auditRequest) {

    logger.info("UserConsentManagementServiceImpl getStudyConsentDetails() - Started ");
    StudyConsentEntity studyConsent = null;
    ParticipantStudyEntity participantStudiesEntity = null;
    ConsentStudyResponseBean consentStudyResponseBean = new ConsentStudyResponseBean();
    try {

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
        participantStudiesEntity = userConsentManagementDao.getParticipantStudies(studyId, userId);
        if (participantStudiesEntity != null) {
          String dataSharingStatus =
              StringUtils.defaultIfEmpty(
                  participantStudiesEntity.getSharing(), DataSharingStatus.UNDEFINED.value());
          consentStudyResponseBean.setSharing(dataSharingStatus);
        }
      }

    } catch (Exception e) {
      logger.error("UserConsentManagementServiceImpl getStudyConsentDetails() - error ", e);
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
    StudyInfoBean studyInfoBean = null;
    try {
      studyInfoBean = userConsentManagementDao.getStudyInfoId(customStudyId);
    } catch (Exception e) {
      logger.error("UserConsentManagementServiceImpl getStudyInfoId() - error ", e);
    }

    logger.info("UserConsentManagementServiceImpl getStudyInfoId() - Ends ");
    return studyInfoBean;
  }

  @Override
  @Transactional(readOnly = true)
  public String getUserDetailsId(String userId) {
    logger.info("UserConsentManagementServiceImpl getUserDetailsId() - Starts ");
    String userDetailId = null;
    try {
      userDetailId = userConsentManagementDao.getUserDetailsId(userId);
    } catch (Exception e) {
      logger.error("UserConsentManagementServiceImpl getStudyInfoId() - error ", e);
    }

    logger.info("UserConsentManagementServiceImpl getUserDetailsId() - Ends ");
    return userDetailId;
  }
}
