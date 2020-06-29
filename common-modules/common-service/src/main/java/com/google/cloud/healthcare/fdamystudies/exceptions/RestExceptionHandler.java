package com.google.cloud.healthcare.fdamystudies.exceptions;

import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.ErrorResponse;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  private XLogger logger = XLoggerFactory.getXLogger(RestExceptionHandler.class.getName());

  @SuppressWarnings("rawtypes")
  @ExceptionHandler(Exception.class)
  public ResponseEntity handleSystemException(Exception ex, WebRequest request) {
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
}
