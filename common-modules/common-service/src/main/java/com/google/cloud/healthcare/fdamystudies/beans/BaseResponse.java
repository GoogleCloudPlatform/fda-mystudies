/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class BaseResponse {

  @JsonInclude(Include.NON_NULL)
  @JsonProperty("status")
  private Integer httpStatusCode;

  @JsonInclude(Include.NON_NULL)
  @JsonProperty("code")
  private String code;

  @JsonInclude(Include.NON_NULL)
  @JsonProperty("message")
  private String message;

  public BaseResponse() {}

  public BaseResponse(MessageCode messageCode) {
    this.httpStatusCode = messageCode.getHttpStatus().value();
    this.code = messageCode.getCode();
    this.message = messageCode.getMessage();
  }

  @JsonIgnore
  public boolean is2xxSuccessful() {
    return HttpStatus.valueOf(httpStatusCode).is2xxSuccessful();
  }
}
