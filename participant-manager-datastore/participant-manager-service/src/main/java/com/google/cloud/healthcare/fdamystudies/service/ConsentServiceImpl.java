/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.CONSENT_DOCUMENT_DOWNLOADED;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ConsentDocumentResponse;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerAuditLogHelper;
import com.google.cloud.healthcare.fdamystudies.config.AppPropertyConfig;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.SitePermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyConsentEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.repository.SitePermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyConsentRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserRegAdminRepository;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsentServiceImpl implements ConsentService {

  private XLogger logger = XLoggerFactory.getXLogger(ConsentServiceImpl.class.getName());

  @Autowired private SitePermissionRepository sitePermissionRepository;

  @Autowired private StudyConsentRepository studyConsentRepository;

  @Autowired private Storage storageService;

  @Autowired AppPropertyConfig appConfig;

  @Autowired private ParticipantManagerAuditLogHelper participantManagerHelper;

  @Autowired private UserRegAdminRepository userRegAdminRepository;

  @Override
  @Transactional(readOnly = true)
  public ConsentDocumentResponse getConsentDocument(
      String consentId, String userId, AuditLogEventRequest auditRequest) {
    logger.entry("begin getConsentDocument(consentId,userId)");

    Optional<StudyConsentEntity> optStudyConsent = studyConsentRepository.findById(consentId);

    if (!optStudyConsent.isPresent()
        || optStudyConsent.get().getParticipantStudy() == null
        || optStudyConsent.get().getParticipantStudy().getSite() == null) {
      throw new ErrorCodeException(ErrorCode.CONSENT_DATA_NOT_AVAILABLE);
    }

    StudyConsentEntity studyConsentEntity = optStudyConsent.get();
    Optional<UserRegAdminEntity> optUserRegAdminEntity = userRegAdminRepository.findById(userId);
    if (!optUserRegAdminEntity.isPresent()) {
      throw new ErrorCodeException(ErrorCode.USER_NOT_FOUND);
    }

    if (!optUserRegAdminEntity.get().isSuperAdmin()) {
      Optional<SitePermissionEntity> optSitePermission =
          sitePermissionRepository.findByUserIdAndSiteId(
              userId, studyConsentEntity.getParticipantStudy().getSite().getId());

      if (!optSitePermission.isPresent()) {
        throw new ErrorCodeException(ErrorCode.SITE_PERMISSION_ACCESS_DENIED);
      }
    }

    String document = null;
    if (StringUtils.isNotBlank(studyConsentEntity.getPdfPath())) {
      Blob blob =
          storageService.get(BlobId.of(appConfig.getBucketName(), studyConsentEntity.getPdfPath()));
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      blob.downloadTo(outputStream);
      document = new String(Base64.getEncoder().encode(blob.getContent()));
    }

    SiteEntity site = studyConsentEntity.getParticipantStudy().getSite();
    auditRequest.setSiteId(site.getLocation().getCustomId());
    auditRequest.setParticipantId(studyConsentEntity.getParticipantStudy().getId());
    auditRequest.setAppId(site.getStudy().getApp().getAppId());
    auditRequest.setStudyId(site.getStudy().getCustomId());
    auditRequest.setUserId(userId);
    auditRequest.setStudyVersion(String.valueOf(site.getStudy().getVersion()));

    Map<String, String> map = new HashedMap<>();
    map.put("site_id", site.getLocation().getCustomId());
    map.put("participant_id", studyConsentEntity.getParticipantStudy().getId());
    map.put("consent_version", studyConsentEntity.getVersion());
    participantManagerHelper.logEvent(CONSENT_DOCUMENT_DOWNLOADED, auditRequest, map);

    return new ConsentDocumentResponse(
        MessageCode.GET_CONSENT_DOCUMENT_SUCCESS,
        studyConsentEntity.getVersion(),
        MediaType.APPLICATION_PDF_VALUE,
        document);
  }
}
