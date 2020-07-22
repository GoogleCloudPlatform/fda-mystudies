/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.controller;

import static com.google.cloud.healthcare.fdamystudies.common.RequestParamValidator.validateRequiredParams;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.AUTHORIZATION_CODE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.CLIENT_ID;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.CODE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.CODE_VERIFIER;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.GRANT_TYPE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.REDIRECT_URI;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.REFRESH_TOKEN;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.SCOPE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.TOKEN;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.USER_ID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.cloud.healthcare.fdamystudies.beans.ValidationErrorResponse;
import com.google.cloud.healthcare.fdamystudies.oauthscim.service.OAuthService;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class OAuthController {

  private static final String STATUS_400_AND_ERRORS_LOG = "status=400 and errors=%s";

  private static final String STATUS_D_LOG = "status=%d";

  private static final String BEGIN_REQUEST_LOG = "begin %s request";

  private XLogger logger = XLoggerFactory.getXLogger(OAuthController.class.getName());

  @Autowired private OAuthService oauthService;

  @PostMapping(
      value = "/oauth2/token",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public ResponseEntity<?> getToken(
      @RequestParam MultiValueMap<String, String> paramMap,
      @RequestHeader HttpHeaders headers,
      HttpServletRequest request)
      throws JsonProcessingException {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));

    String grantType = paramMap.getFirst(GRANT_TYPE);

    // validate required params
    ValidationErrorResponse errors = null;
    if (REFRESH_TOKEN.equals(paramMap.getFirst(GRANT_TYPE))) {
      errors = validateRequiredParams(paramMap, REFRESH_TOKEN, REDIRECT_URI, USER_ID, CLIENT_ID);
    } else if (AUTHORIZATION_CODE.equals(grantType)) {
      errors = validateRequiredParams(paramMap, CODE, REDIRECT_URI, SCOPE, USER_ID, CODE_VERIFIER);
    } else {
      // client_credentials grant type
      errors = validateRequiredParams(paramMap, GRANT_TYPE, REDIRECT_URI, SCOPE);
    }

    if (errors.hasErrors()) {
      logger.exit(String.format(STATUS_400_AND_ERRORS_LOG, errors));
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // get token from hydra
    ResponseEntity<?> response = oauthService.getToken(paramMap, headers);
    logger.exit(String.format(STATUS_D_LOG, response.getStatusCodeValue()));

    return response;
  }

  @PostMapping(
      value = "/oauth2/revoke",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public ResponseEntity<?> revokeToken(
      @RequestParam MultiValueMap<String, String> paramMap,
      HttpServletRequest request,
      @RequestHeader HttpHeaders headers) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));

    // validate required params
    ValidationErrorResponse errors = validateRequiredParams(paramMap, TOKEN);
    if (errors.hasErrors()) {
      logger.exit(String.format(STATUS_400_AND_ERRORS_LOG, errors));
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // revoke tokens
    ResponseEntity<JsonNode> response = oauthService.revokeToken(paramMap, headers);
    logger.exit(String.format(STATUS_D_LOG, response.getStatusCodeValue()));
    return response;
  }

  @PostMapping(
      value = "/oauth2/introspect",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public ResponseEntity<?> introspectToken(
      @RequestParam MultiValueMap<String, String> paramMap,
      HttpServletRequest request,
      @RequestHeader HttpHeaders headers) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));

    // validate required params
    ValidationErrorResponse errors = validateRequiredParams(paramMap, TOKEN);
    if (errors.hasErrors()) {
      logger.exit(String.format(STATUS_400_AND_ERRORS_LOG, errors));
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // revoke tokens
    ResponseEntity<JsonNode> response = oauthService.introspectToken(paramMap, headers);
    logger.exit(String.format(STATUS_D_LOG, response.getStatusCodeValue()));
    return response;
  }
}
