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
import com.google.cloud.healthcare.fdamystudies.common.CommonAuditEvent;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.ErrorResponse;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  private XLogger logger = XLoggerFactory.getXLogger(RestExceptionHandler.class.getName());

  @Autowired private AuditEventHelper auditEventHelper;

  @Value("${componentName}")
  private String componentName;

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleSystemException(Exception ex, WebRequest request) {
    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    logger.error(String.format("%s request failed with an exception", uri), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorCode.APPLICATION_ERROR);
  }

  @ExceptionHandler(RestClientResponseException.class)
  @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleRestClientResponseException(
      HttpClientErrorException ex, WebRequest request) {
    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    ErrorResponse response = new ErrorResponse(ex);
    logger.error(
        String.format(
            "%s request failed due to RestClientResponseException, response=%s", uri, response),
        ex);
    return response;
  }

  @ExceptionHandler(ErrorCodeException.class)
  public ResponseEntity<ErrorCode> handleErrorCodeException(ErrorCodeException e) {
    logger.error("request failed with ErrorCode", e);
    return ResponseEntity.status(e.getErrorCode().getStatus()).body(e.getErrorCode());
  }

  @ExceptionHandler(ResourceAccessException.class)
  public ResponseEntity<?> handleResourceAccessException(
      ResourceAccessException ex, WebRequest webRequest) {
    HttpServletRequest servletRequest =
        ((ServletWebRequest) webRequest).getNativeRequest(HttpServletRequest.class);

    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(servletRequest);
    auditRequest.setSource(componentName);

    if (StringUtils.contains(ex.getMessage(), "cannot retry due to server authentication")) {
      auditEventHelper.logEvent(CommonAuditEvent.INVALID_CLIENT_ID_OR_SECRET, auditRequest);
    } else {
      Map<String, String> placeHolders = new HashMap<>();
      placeHolders.put("uri_path", servletRequest.getRequestURI());
      placeHolders.put("status_code", String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
      auditEventHelper.logEvent(
          CommonAuditEvent.RESOURCE_ACCESS_FAILED, auditRequest, placeHolders);
    }

    logger.error(
        String.format("%s request failed with an exception", servletRequest.getRequestURI()), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorCode.APPLICATION_ERROR);
  }
}
