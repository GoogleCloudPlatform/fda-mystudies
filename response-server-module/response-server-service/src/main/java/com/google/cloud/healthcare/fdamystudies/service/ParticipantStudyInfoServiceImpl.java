/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.bean.ParticipantStudyInformation;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguration;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.ProcessResponseException;
import com.google.cloud.healthcare.fdamystudies.utils.ResponseServerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger logger =
      LoggerFactory.getLogger(ParticipantStudyInfoServiceImpl.class);

  @Override
  public ParticipantStudyInformation getParticipantStudyInfo(String studyId, String participantId)
      throws ProcessResponseException {
    logger.debug("getParticipantStudyInfo() - starts ");
    HttpHeaders headers = null;

    ResponseEntity<?> responseEntity = null;
    try {
      headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set(AppConstants.CLIENT_ID_PARAM, appConfig.getRegServerClientId());
      headers.set(
          AppConstants.CLIENT_SECRET_PARAM,
          ResponseServerUtil.getHashedValue(appConfig.getRegServerClientSecret()));

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

      logger.debug("getStudyActivityMetadata() - ends");
      return partStudyInfo;

    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new ProcessResponseException(e.getMessage());
    }
  }
}
