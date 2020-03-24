/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.google.cloud.healthcare.fdamystudies.bean.BodyForProvider;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;

@Component
public class ConsentManagementUtil {

  private static final Logger logger = LoggerFactory.getLogger(ConsentManagementUtil.class);

  @Autowired private RestTemplate restTemplate;

  @Autowired private ApplicationPropertyConfiguration appConfig;

  /*
   * This method is used to validate the user
   * and user's current session through a REST API
   * call to AuthServer.
   */
  public Integer validateAccessToken(
      String userId,
      String accessToken,
      String applicationId,
      String orgId,
      String clientId,
      String secretkey) {
    logger.info("ConsentManagementUtil validateAccessToken() - starts ");
    Integer value = null;
    HttpHeaders headers = null;
    BodyForProvider providerBody = null;
    HttpEntity<BodyForProvider> requestBody = null;
    ResponseEntity<?> responseEntity = null;
    try {
      headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("appId", applicationId);
      headers.set("orgId", orgId);
      headers.set("clientId", clientId);
      headers.set("secretKey", secretkey);

      providerBody = new BodyForProvider();
      providerBody.setUserId(userId);
      providerBody.setAccessToken(accessToken);

      requestBody = new HttpEntity<BodyForProvider>(providerBody, headers);

      responseEntity =
          restTemplate.exchange(
              appConfig.getAuthServerAccessTokenValidationUrl(),
              HttpMethod.POST,
              requestBody,
              Integer.class);

      value = (Integer) responseEntity.getBody();
    } catch (Exception e) {
      logger.error("ConsentManagementUtil validateAccessToken() - error " + e);
    }
    logger.info("ConsentManagementUtil validateAccessToken() - ends ");
    return value;
  }
}
