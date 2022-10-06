/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.CONSENT_TYPE;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.PRIMARY;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.SHARING;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.STUDY_ID;

import com.google.api.services.healthcare.v1.model.Consent;
import com.google.cloud.healthcare.fdamystudies.bean.ParticipantStudyInformation;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.DataSharingStatus;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguration;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.mapper.ConsentManagementAPIs;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.ProcessResponseException;
import java.io.IOException;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ParticipantStudyInfoServiceImpl implements ParticipantStudyInfoService {
  @Autowired private RestTemplate restTemplate;
  @Autowired private ApplicationConfiguration appConfig;
  @Autowired private OAuthService oauthService;
  @Autowired private ConsentManagementAPIs consentManagementAPIs;

  private XLogger logger =
      XLoggerFactory.getXLogger(ParticipantStudyInfoServiceImpl.class.getName());

  @Override
  public ParticipantStudyInformation getParticipantStudyInfo(
      String studyId, String participantId, AuditLogEventRequest auditRequest)
      throws ProcessResponseException {
    logger.entry("begin getParticipantStudyInfo()");
    HttpHeaders headers = null;

    ResponseEntity<?> responseEntity = null;
    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("Authorization", "Bearer " + oauthService.getAccessToken());
    AuditEventMapper.addAuditEventHeaderParams(headers, auditRequest);

    UriComponentsBuilder getPartInfoUriBuilder =
        UriComponentsBuilder.fromHttpUrl(appConfig.getRegServerPartStudyInfoUrl())
            .queryParam(AppConstants.STUDY_ID_PARAM, studyId)
            .queryParam(AppConstants.PARTICIPANT_ID_KEY, participantId);
    responseEntity =
        restTemplate.exchange(
            getPartInfoUriBuilder.toUriString(),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            ParticipantStudyInformation.class);
    ParticipantStudyInformation partStudyInfo =
        (ParticipantStudyInformation) responseEntity.getBody();

    logger.exit("getParticipantStudyInfo() - ends");
    return partStudyInfo;
  }

  @Override
  public ParticipantStudyInformation getParticipantStudyInfoFromConsent(
      String studyId, String participantId, AuditLogEventRequest auditRequest)
      throws ProcessResponseException, IOException {
    logger.info("begin getParticipantStudyInfo()");
    ParticipantStudyInformation partStudyInfo = new ParticipantStudyInformation();

    String parentName =
        String.format(
            "projects/%s/locations/%s/datasets/%s/consentStores/%s",
            appConfig.getProjectId(), appConfig.getRegionId(), studyId, "CONSENT_" + studyId);
    logger.info("parentName" + parentName);

    String customStudyId = "Metadata(\"" + STUDY_ID + "\")=\"" + studyId + "\"";
    String userId = "user_id=\"" + participantId + "\"";
    String primary = "Metadata(\"" + CONSENT_TYPE + "\")=\"" + PRIMARY + "\"";
    String sharing = "Metadata(\"" + CONSENT_TYPE + "\")=\"" + SHARING + "\"";
    String consentFilter = customStudyId + " AND " + userId;

    List<Consent> primaryConsentList =
        consentManagementAPIs.getListOfConsents(consentFilter + " AND " + primary, parentName);
    List<Consent> dataSharingConsentList =
        consentManagementAPIs.getListOfConsents(consentFilter + " AND " + sharing, parentName);
    if (CollectionUtils.isEmpty(primaryConsentList)) {
      // below code has been added to support old studies mystudies_participant_datastore
      partStudyInfo = getParticipantStudyInfo(studyId, participantId, auditRequest);
    } else {
      Consent primaryConsent = primaryConsentList.get(0);
      String withdrawn = primaryConsent.getState().equals("REVOKED") ? "Withdrawn" : "";
      partStudyInfo.setWithdrawal(withdrawn);
      if (!CollectionUtils.isEmpty(dataSharingConsentList)) {
        Consent SharingConsent = dataSharingConsentList.get(0);
        if (SharingConsent.getState().equals("REJECTED")) {
          partStudyInfo.setSharing(DataSharingStatus.NOT_PROVIDED.value());
        } else if (SharingConsent.getState().equals("ACTIVE")) {
          partStudyInfo.setSharing(DataSharingStatus.PROVIDED.value());
        }
      } else {
        partStudyInfo.setSharing(DataSharingStatus.NOT_APPLICABLE.value());
      }
    }

    logger.exit("getParticipantStudyInfo() - ends");
    return partStudyInfo;
  }
}
