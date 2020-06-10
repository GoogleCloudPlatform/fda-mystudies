package com.google.cloud.healthcare.fdamystudies.controller;

import com.google.cloud.healthcare.fdamystudies.bean.ApiError;
import com.google.cloud.healthcare.fdamystudies.exception.DuplicateUserRegistrationException;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.exception.UnauthorizedException;
import com.google.cloud.healthcare.fdamystudies.utils.MyStudiesUserRegUtil;
import com.google.cloud.healthcare.fdamystudies.utils.MyStudiesUserRegUtil.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
  private final Logger log = LoggerFactory.getLogger(ResponseEntityExceptionHandler.class);

  @ExceptionHandler(IllegalArgumentException.class)
  protected ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex) {
    ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(UnauthorizedException.class)
  protected ResponseEntity<ApiError> handleUnauthorizedException(UnauthorizedException ex) {
    ApiError error = new ApiError(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
  }

  @ExceptionHandler(DuplicateUserRegistrationException.class)
  protected ResponseEntity<ApiError> handleDuplicateUserRegistrationException(DuplicateUserRegistrationException _ex) {
    ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), MyStudiesUserRegUtil.ErrorCodes.EMAIL_EXISTS.getValue());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(SystemException.class)
  protected ResponseEntity<ApiError> handleSystemException(SystemException ex) {
    log.error("received system exception:", ex);
    ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ErrorCodes.SYSTEM_ERROR_FOUND.getValue());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }
}
