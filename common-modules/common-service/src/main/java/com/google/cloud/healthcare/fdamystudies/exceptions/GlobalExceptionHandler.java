/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.exceptions;

import com.google.cloud.healthcare.fdamystudies.beans.ValidationErrorResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ValidationErrorResponse.Violation;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

@ControllerAdvice
@Order(0)
public class GlobalExceptionHandler {

  private XLogger logger = XLoggerFactory.getXLogger(GlobalExceptionHandler.class.getName());

  @ExceptionHandler(BadRequest.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public void handleBadRequest() {}

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ValidationErrorResponse handleConstraintValidationException(
      ConstraintViolationException e) {
    logger.trace("request failed with ConstraintViolationException", e);
    ValidationErrorResponse error = new ValidationErrorResponse();
    for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
      error
          .getViolations()
          .add(new Violation(violation.getPropertyPath().toString(), violation.getMessage()));
    }
    return error;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ValidationErrorResponse handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    logger.error("request failed with MethodArgumentNotValidException", e);
    ValidationErrorResponse error = new ValidationErrorResponse();
    for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
      error
          .getViolations()
          .add(new Violation(fieldError.getField(), fieldError.getDefaultMessage()));
    }
    return error;
  }

  @ExceptionHandler(MissingRequestHeaderException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ValidationErrorResponse handleMissingRequestHeaderException(
      MissingRequestHeaderException e) {
    logger.error("request failed with MissingRequestHeaderException", e);
    ValidationErrorResponse error = new ValidationErrorResponse();
    error.getViolations().add(new Violation(e.getHeaderName(), "header is required"));
    return error;
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ValidationErrorResponse handleHttpMessageNotReadableException(
      HttpMessageNotReadableException e) {
    logger.error("request failed with HttpMessageNotReadableException", e);
    ValidationErrorResponse error = new ValidationErrorResponse();
    error.getViolations().add(new Violation("", "request body is required"));
    return error;
  }

  @ExceptionHandler(ErrorCodeException.class)
  public ResponseEntity<ErrorCode> handleErrorCodeException(ErrorCodeException e) {
    logger.error("request failed with ErrorCode", e);
    return ResponseEntity.status(e.getErrorCode().getStatus()).body(e.getErrorCode());
  }
}
