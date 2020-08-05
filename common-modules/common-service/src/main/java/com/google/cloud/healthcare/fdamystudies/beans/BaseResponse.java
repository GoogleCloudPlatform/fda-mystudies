/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class BaseResponse {

  @JsonProperty("status")
  private Integer httpStatusCode;

  @JsonInclude(Include.NON_NULL)
  @JsonProperty("error_code")
  private String errorCode;

  @JsonInclude(Include.NON_NULL)
  @JsonProperty("error_type")
  private String errorType;

  @JsonInclude(Include.NON_NULL)
  @JsonProperty("error_description")
  private String errorDescription;

  @JsonInclude(Include.NON_NULL)
  @JsonProperty("message")
  private String message;

  @JsonInclude(Include.NON_NULL)
  @JsonProperty("code")
  private String code;

  public BaseResponse() {}

  public BaseResponse(ErrorCode errorCode) {
    this.httpStatusCode = errorCode.getStatus();
    this.errorCode = errorCode.getCode();
    this.errorType = errorCode.getErrorType();
    this.errorDescription = errorCode.getDescription();
  }

  public BaseResponse(HttpStatus httpStatus, String message) {
    this.httpStatusCode = httpStatus.value();
    this.message = message;
  }

  public BaseResponse(MessageCode messageCode) {
    this.httpStatusCode = messageCode.getHttpStatus().value();
    this.code = messageCode.getCode();
    this.message = messageCode.getMessage();
  }
}
