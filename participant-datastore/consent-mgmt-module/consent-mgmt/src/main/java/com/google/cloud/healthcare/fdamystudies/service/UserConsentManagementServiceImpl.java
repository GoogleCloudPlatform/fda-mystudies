/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.CONSENT_TYPE;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.DATA_SHARING;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.PDF_PATH;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.PRIMARY;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.SHARING;
import static com.google.cloud.healthcare.fdamystudies.common.ConsentManagementEnum.READ_OPERATION_FAILED_FOR_SIGNED_CONSENT_DOCUMENT;
import static com.google.cloud.healthcare.fdamystudies.common.ConsentManagementEnum.READ_OPERATION_SUCCEEDED_FOR_SIGNED_CONSENT_DOCUMENT;

import com.google.api.services.healthcare.v1.model.Consent;
import com.google.api.services.healthcare.v1.model.ConsentArtifact;
import com.google.cloud.healthcare.fdamystudies.bean.ConsentStudyResponseBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.ConsentAuditHelper;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.dao.UserConsentManagementDao;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.mapper.ConsentManagementAPIs;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyConsentEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.storage.StorageException;
import java.util.ArrayList;
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

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Autowired ConsentManagementAPIs consentApis;

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
  public String saveStudyConsent(
      StudyConsentEntity studyConsent,
      ParticipantStudyEntity participantStudyEntity,
      String filePath,
      String dataSharingPath) {
    return userConsentManagementDao.saveStudyConsent(
        studyConsent, participantStudyEntity, filePath, dataSharingPath);
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
          //to get dataSharingImage Content
          if(studyConsent.getDataSharingConsentArtifactPath()!=null) {
          String imagePath=studyConsent.getDataSharingConsentArtifactPath();
          downloadDataSharingScreenshot(imagePath, consentStudyResponseBean, userId, auditRequest);
          }
          //end here
        }
      }
    }

    logger.exit("getStudyConsentDetails() - Ends ");
    return consentStudyResponseBean;
  }

  private void downloadDataSharingScreenshot(
	      String filepath,
	      ConsentStudyResponseBean consentStudyResponseBean,
	      String userId,
	      AuditLogEventRequest auditRequest) {
	    try {
	      auditRequest.setUserId(userId);
	      Map<String, String> map = Collections.singletonMap("file_name", filepath);
	      String documentContent = cloudStorageService.getDocumentContent(filepath);
	      consentStudyResponseBean.setDataSharingScreenShot(documentContent);
	      consentAuditHelper.logEvent(
	          READ_OPERATION_SUCCEEDED_FOR_SIGNED_CONSENT_DOCUMENT, auditRequest, map);
	    } catch (StorageException e) {
	      consentAuditHelper.logEvent(READ_OPERATION_FAILED_FOR_SIGNED_CONSENT_DOCUMENT, auditRequest);
	      logger.error("Download consent document from cloud storage failed", e);
	      throw new ErrorCodeException(ErrorCode.APPLICATION_ERROR);
	    }
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

  @Override
  @Transactional(readOnly = true)
  public ConsentStudyResponseBean getStudyConsentDetailsFromConsentStore(
      String userId, String studyId,String customId, String consentVersion, AuditLogEventRequest auditRequest) {
    logger.entry("Begin getStudyConsentDetailsFromConsentStore() ");

    ConsentStudyResponseBean consentStudyResponseBean = new ConsentStudyResponseBean();

    ParticipantStudyEntity participantStudiesEntity =
        userConsentManagementDao.getParticipantStudies(studyId, userId);

    if (participantStudiesEntity != null && participantStudiesEntity.getParticipantId() != null) {
      String participantId = participantStudiesEntity.getParticipantId();
      ConsentArtifact consentArtifact = getStudyConsentFromConsentStore(participantId, customId);
      //forImagePath
      ConsentArtifact consentArtifactforImage = getImageConsentFromConsentStore(participantId, customId);
      if (consentArtifactforImage != null && consentArtifactforImage.getConsentContentVersion() != null) {
          consentStudyResponseBean
          .setDataSharingScreenShot(consentArtifactforImage.getConsentContentScreenshots().get(0).getRawBytes());
      }
      //ends here
      if (consentArtifact != null && consentArtifact.getConsentContentVersion() != null) {
        consentStudyResponseBean
            .getConsent()
            .setVersion(consentArtifact.getConsentContentVersion());

        consentStudyResponseBean.getConsent().setType("application/pdf");
        consentStudyResponseBean.setSharing(consentArtifact.getMetadata().get(DATA_SHARING));
        consentStudyResponseBean
            .getConsent()
            .setContent(consentArtifact.getConsentContentScreenshots().get(0).getRawBytes());
        auditRequest.setParticipantId(participantStudiesEntity.getParticipantId());
        Map<String, String> map =
            Collections.singletonMap("file_name", consentArtifact.getMetadata().get(PDF_PATH));
        consentAuditHelper.logEvent(
            READ_OPERATION_SUCCEEDED_FOR_SIGNED_CONSENT_DOCUMENT, auditRequest, map);
      }
    }

    logger.exit("getStudyConsentDetailsFromConsentStore() - Ends ");
    return consentStudyResponseBean;
  }

  private ConsentArtifact getStudyConsentFromConsentStore(
	      String participantId, String consentStoreId) {
	    logger.entry("Begin getStudyConsentFromConsentStore()");

	    String parentName =
	        String.format(
	            "projects/%s/locations/%s/datasets/%s/consentStores/%s",
	            appConfig.getProjectId(),
	            appConfig.getRegionId(),
	            consentStoreId ,
	            "CONSENT_" + consentStoreId);

	    ConsentArtifact consentArtifact = null;
	    String filter1 = "user_id=\"" + participantId + "\"";
	    String filter2 = "Metadata(\"" + CONSENT_TYPE + "\")=\"" + PRIMARY + "\"";
	    List<Consent> consents = new ArrayList<>();

	    consents = consentApis.getListOfConsents(filter1 + " AND " + filter2, parentName);

	    if (consents != null) {
	      consentArtifact = consentApis.getConsentArtifact(consents.get(0).getConsentArtifact());
	    }

	    logger.exit("getStudyConsentFromConsentStore() - Ends ");
	    return consentArtifact;
	  }

/**
   * Fetches study consent details from consent store
   *
   * @param userId
   * @param studyId
   * @param consentVersion
   * @return
   */
  private ConsentArtifact getImageConsentFromConsentStore(
      String participantId, String consentStoreId) {
    logger.entry("Begin getStudyConsentFromConsentStore()");

    String parentName =
        String.format(
            "projects/%s/locations/%s/datasets/%s/consentStores/%s",
            appConfig.getProjectId(),
            appConfig.getRegionId(),
            consentStoreId,
            "CONSENT_" + consentStoreId);

    ConsentArtifact consentArtifact = null;
    String filter1 = "user_id=\"" + participantId + "\"";
    String filter2 = "Metadata(\"" + CONSENT_TYPE + "\")=\"" + SHARING + "\"";
    List<Consent> consents = new ArrayList<>();

    consents = consentApis.getListOfConsents(filter1 + " AND " + filter2, parentName);

    if (consents != null) {
      consentArtifact = consentApis.getConsentArtifact(consents.get(0).getConsentArtifact());
    }

    logger.exit("getStudyConsentFromConsentStore() - Ends ");
    return consentArtifact;
  }

@Override
public StudyConsentEntity getExistStudyConsent(String userId, String studyId ,String participanStudyId) {
	 return userConsentManagementDao.getExistStudyConsent(userId,studyId,participanStudyId);
}
}
