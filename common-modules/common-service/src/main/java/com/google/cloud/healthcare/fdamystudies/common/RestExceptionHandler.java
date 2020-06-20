package com.google.cloud.healthcare.fdamystudies.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(RestExceptionHandler.class);

  @SuppressWarnings("rawtypes")
  @ExceptionHandler(Exception.class)
  public ResponseEntity handleSystemException(Exception ex, WebRequest request) {
    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    LOG.error(String.format("%s request failed with an exception", uri), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorCode.APPLICATION_ERROR);
  }

  @SuppressWarnings("rawtypes")
  @ExceptionHandler(RestClientResponseException.class)
  public ResponseEntity handleRestClientResponseException(
      HttpClientErrorException ex, WebRequest request) {
    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    ErrorResponse response = new ErrorResponse(ex);
    LOG.error(
        String.format(
            "%s request failed due to RestClientResponseException, response=%s", uri, response),
        ex);
    return ResponseEntity.status(ex.getRawStatusCode()).body(response);
  }
}
