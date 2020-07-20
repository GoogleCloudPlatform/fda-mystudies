/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.service;

import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.AUTHORIZATION;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.GRANT_TYPE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.REFRESH_TOKEN;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.cloud.healthcare.fdamystudies.service.BaseServiceImpl;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
class HydraOAuthServiceImpl extends BaseServiceImpl implements OAuthService {

  @Value("${security.oauth2.hydra.token_endpoint}")
  private String tokenEndpoint;

  @Value("${security.oauth2.hydra.client.client-id}")
  private String clientId;

  @Value("${security.oauth2.hydra.client.client-secret}")
  private String clientSecret;

  private String encodedAuthorization;

  @PostConstruct
  public void init() {
    encodedAuthorization = getEncodedAuthorization(clientId, clientSecret);
  }

  public ResponseEntity<JsonNode> getToken(
      MultiValueMap<String, String> paramMap, HttpHeaders headers) {

    if (REFRESH_TOKEN.equals(paramMap.getFirst(GRANT_TYPE))) {
      headers.set(AUTHORIZATION, encodedAuthorization);
    }

    return getRestTemplate().postForEntity(tokenEndpoint, paramMap, JsonNode.class);
  }
}
