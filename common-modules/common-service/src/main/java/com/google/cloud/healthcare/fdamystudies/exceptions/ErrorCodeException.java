package com.google.cloud.healthcare.fdamystudies.exceptions;

import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import lombok.Getter;

@Getter
public class ErrorCodeException extends RuntimeException {

  private final ErrorCode errorCode;

  public ErrorCodeException(ErrorCode errorCode) {
    this.errorCode = errorCode;
  }

  @Override
  public String getMessage() {
    return errorCode.getCode() + ": " + errorCode.getDescription();
  }
}
