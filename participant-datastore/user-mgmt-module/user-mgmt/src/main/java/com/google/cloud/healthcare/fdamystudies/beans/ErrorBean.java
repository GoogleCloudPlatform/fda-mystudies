/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorBean {

  private Integer code = 0;
  private String message = "";
  private JsonNode response;

  public ErrorBean() {}

  public ErrorBean(Integer code, String msg) {
    this.code = code;
    this.message = msg;
  }

  public ErrorBean(Integer code, String message, JsonNode response) {
    super();
    this.code = code;
    this.message = message;
    this.response = response;
  }
}
