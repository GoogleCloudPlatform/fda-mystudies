/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
import com.google.cloud.healthcare.fdamystudies.bean.BodyForProvider;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguratation;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDaoImpl;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySite;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.model.SiteBo;
import com.google.cloud.healthcare.fdamystudies.model.SitePermission;

@Service
public class CommonServiceImpl implements CommonService {

  private static Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

  @Autowired private RestTemplate restTemplate;

  @Autowired private ApplicationConfiguratation appConfig;

  @Autowired private CommonDaoImpl commonDaoImpl;

  public Integer validateAccessToken(
      String userId, String accessToken, String clientId, String secretKey) {
    logger.info("CommonServiceImpl validateAccessToken() - starts ");
    Integer value = null;
    HttpHeaders headers = null;
    BodyForProvider providerBody = null;
    HttpEntity<BodyForProvider> requestBody = null;
    ResponseEntity<?> responseEntity = null;
    try {
      headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("clientId", clientId);
      headers.set("secretKey", secretKey);
      providerBody = new BodyForProvider();
      providerBody.setUserId(userId);
      providerBody.setAccessToken(accessToken);

      requestBody = new HttpEntity<>(providerBody, headers);

      responseEntity =
          restTemplate.exchange(
              appConfig.getAuthServerAccessTokenValidationUrl(),
              HttpMethod.POST,
              requestBody,
              Integer.class);

      value = (Integer) responseEntity.getBody();
    } catch (Exception e) {
      logger.error("CommonServiceImpl validateAccessToken() - error ", e);
    }
    logger.info("CommonServiceImpl validateAccessToken() - ends ");
    return value;
  }

  @Override
  public List<SitePermission> getSitePermissionsOfUser(Integer userId) {
    logger.info(" CommonServiceImpl - getSites():starts");
    List<SitePermission> sitePermission = new ArrayList<>();
    try {
      sitePermission = commonDaoImpl.getSitePermissions(userId);
    } catch (Exception e) {
      logger.info("CommonServiceImpl - getSites() : error", e);
    }

    logger.info("CommonServiceImpl - getSites() : ends");

    return sitePermission;
  }

  public List<SiteBo> getAllSite() {
    logger.info(" CommonServiceImpl - getAllSite():starts");
    List<SiteBo> sites = new ArrayList<>();
    try {
      sites = commonDaoImpl.getAllSite();
    } catch (Exception e) {
      logger.info("CommonServiceImpl - getAllSite() : error", e);
    }

    logger.info("CommonServiceImpl - getAllSite() : ends");

    return sites;
  }

  @Override
  public ParticipantStudiesBO getParticipantStudiesBOs(Integer participantRegistryId) {
    logger.info("CommonServiceImpl - getParticipantStudiesBOs() : starts");
    ParticipantStudiesBO participantBo = null;
    commonDaoImpl.getParticipantStudiesBOs(participantRegistryId);
    try {
      participantBo = commonDaoImpl.getParticipantStudiesBOs(participantRegistryId);
    } catch (Exception e) {
      logger.info("CommonServiceImpl - getParticipantStudiesBOs() : error");
    }
    logger.info("CommonServiceImpl - getParticipantStudiesBOs() : ends");
    return participantBo;
  }

  @Override
  public List<ParticipantRegistrySite> getParticipantRegistry(Integer studyId, String email) {
    logger.info("CommonServiceImpl - getParticipantRegistry() : starts");
    List<ParticipantRegistrySite> registry = new LinkedList<>();
    try {
      registry = commonDaoImpl.getParticipantRegistry(studyId, email);
    } catch (Exception e) {
      logger.info("CommonServiceImpl - getParticipantRegistry() : error");
      throw e;
    }
    logger.info("CommonServiceImpl - getParticipantRegistry() : ends");
    return registry;
  }
}
