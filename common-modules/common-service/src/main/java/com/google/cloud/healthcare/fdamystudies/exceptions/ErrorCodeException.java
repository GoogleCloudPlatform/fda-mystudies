package com.google.cloud.healthcare.fdamystudies.exceptions;

import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import javax.servlet.ServletException;
import lombok.Getter;

@Getter
public class ErrorCodeException extends ServletException {

  private final ErrorCode errorCode;

  public ErrorCodeException(ErrorCode errorCode) {
    this.errorCode = errorCode;
  }
}
