/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.exceptions;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.AuditEventHelper;
import com.google.cloud.healthcare.fdamystudies.common.AuditLogEvent;
import com.google.cloud.healthcare.fdamystudies.common.CommonAuditEvent;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.jayway.jsonpath.JsonPath;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

@Component
public class RestResponseErrorHandler implements ResponseErrorHandler {
  private XLogger logger = XLoggerFactory.getXLogger(RestResponseErrorHandler.class.getName());

  @Autowired private AuditEventHelper auditEventHelper;

  private @Autowired HttpServletRequest request;

  @Value("${component.name}")
  private String componentName;

  @Override
  public boolean hasError(ClientHttpResponse response) throws IOException {
    return new DefaultResponseErrorHandler().hasError(response);
  }

  @Override
  public void handleError(ClientHttpResponse response) throws IOException {
    ErrorCode errorCode = null;
    HttpHeaders headers = response.getHeaders();
    String responseBody = IOUtils.toString(response.getBody(), StandardCharsets.UTF_8.name());
    Map<String, String> placeHolders = new HashMap<>();
    if (response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
      // handle 5xx errors
      errorCode = ErrorCode.APPLICATION_ERROR;
      placeHolders.put("uri_path", request.getRequestURI());
      placeHolders.put("status_code", String.valueOf(response.getRawStatusCode()));
      logAuditEvent(CommonAuditEvent.RESOURCE_ACCESS_FAILED, placeHolders);
    } else if (StringUtils.contains(responseBody, "invalid_grant")) {
      logAuditEvent(CommonAuditEvent.INVALID_GRANT_OR_INVALID_REFRESH_TOKEN, placeHolders);
    } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
      errorCode = ErrorCode.UNAUTHORIZED;
    } else if (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR
        && StringUtils.containsIgnoreCase(headers.getFirst("Content-Type"), "json")
        && StringUtils.contains(responseBody, "error_code")
        && StringUtils.contains(responseBody, "error_description")) {
      // handle 4xx errors
      String code = JsonPath.read(responseBody, "$.error_code");
      String description = JsonPath.read(responseBody, "$.error_description");
      errorCode = ErrorCode.fromCodeAndDescription(code, description);
    }

    errorCode = errorCode == null ? ErrorCode.APPLICATION_ERROR : errorCode;

    logger.error(
        String.format(
            "HTTP request using the RestTemplate failed with status=%d, Content-Type=%s, errorCode=%s and responseBody=%s",
            response.getRawStatusCode(),
            headers.getFirst("Content-Type"),
            errorCode,
            responseBody));
    throw new ErrorCodeException(errorCode);
  }

  private void logAuditEvent(AuditLogEvent event, Map<String, String> placeHolders) {
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    if (StringUtils.isEmpty(auditRequest.getSource())) {
      auditRequest.setSource(componentName);
    }

    if (placeHolders.isEmpty()) {
      auditEventHelper.logEvent(event, auditRequest);
    } else {
      auditEventHelper.logEvent(event, auditRequest, placeHolders);
    }
  }
}
