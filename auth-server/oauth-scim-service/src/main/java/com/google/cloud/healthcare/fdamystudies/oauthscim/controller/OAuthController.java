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
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.USER_ID;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.NEW_ACCESS_TOKEN_GENERATED;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ValidationErrorResponse;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimAuditHelper;
import com.google.cloud.healthcare.fdamystudies.oauthscim.service.OAuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Collections;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
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

@Api(
    tags = "Get Token",
    value = "Get access token and refresh token based on grant type",
    description = "Get access token and refresh token based on grant type")
@CrossOrigin
@RestController
public class OAuthController {

  private static final String STATUS_400_AND_ERRORS_LOG = "status=400 and errors=%s";

  private static final String STATUS_D_LOG = "status=%d";

  private static final String BEGIN_REQUEST_LOG = "begin %s request";

  private XLogger logger = XLoggerFactory.getXLogger(OAuthController.class.getName());

  @Autowired private OAuthService oauthService;

  @Autowired private AuthScimAuditHelper auditHelper;

  @ApiOperation(
      notes =
          "Refer [The OAuth 2.0 Token Endpoint](https://www.ory.sh/hydra/docs/reference/api/#the-oauth-20-token-endpoint) for request and response details ",
      value =
          "Get access token and refresh token based on grant type. Refer ORY Hydra REST API documentation for request and response details")
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
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    String grantType = StringUtils.defaultString(paramMap.getFirst(GRANT_TYPE));
    // validate required params
    ValidationErrorResponse errors = null;
    switch (grantType) {
      case REFRESH_TOKEN:
        errors = validateRequiredParams(paramMap, REFRESH_TOKEN, REDIRECT_URI, CLIENT_ID, USER_ID);

        break;
      case AUTHORIZATION_CODE:
        errors =
            validateRequiredParams(paramMap, CODE, REDIRECT_URI, SCOPE, USER_ID, CODE_VERIFIER);

        break;
      default:
        // client_credentials grant type
        errors = validateRequiredParams(paramMap, GRANT_TYPE, REDIRECT_URI, SCOPE);
    }

    if (errors.hasErrors()) {
      logger.exit(String.format(STATUS_400_AND_ERRORS_LOG, errors));
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // get token from hydra
    ResponseEntity<?> response = oauthService.getToken(paramMap, headers, auditRequest);

    Map<String, String> values = Collections.singletonMap("grant_type", grantType);
    auditHelper.logEvent(NEW_ACCESS_TOKEN_GENERATED, auditRequest, values);

    logger.exit(String.format(STATUS_D_LOG, response.getStatusCodeValue()));

    return response;
  }
}
