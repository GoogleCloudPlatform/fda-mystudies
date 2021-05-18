/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.bean.ParticipantStudyInformation;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguration;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.ProcessResponseException;
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
}
