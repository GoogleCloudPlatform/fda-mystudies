package com.google.cloud.healthcare.fdamystudies.common;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(RestExceptionHandler.class);

  private static final String REQUEST_FAILED_WITH_AN_EXCEPTION =
      "%s request failed with an exception";

  @ExceptionHandler(Exception.class)
  public ResponseEntity<JsonNode> handleSystemException(Exception ex, WebRequest request) {
    HttpServletRequest httpRequest = ((ServletWebRequest) request).getRequest();

    if (LOG.isErrorEnabled()) {
      LOG.error(String.format(REQUEST_FAILED_WITH_AN_EXCEPTION, httpRequest.getRequestURI()), ex);
    }

    ObjectNode response = (ObjectNode) ErrorCode.APPLICATION_ERROR.toJson();
    response.put("path", httpRequest.getPathInfo());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }
}
