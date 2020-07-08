/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorBean {

  private Integer appErrorCode = 0;
  private String userMessage = "";
  private String type = "";
  private String detailMessage = "";

  public ErrorBean() {}

  public ErrorBean(Integer code, String msg) {
    this.appErrorCode = code;
    this.userMessage = msg;
  }

  public ErrorBean(Integer code, String msg, String type, String detailMessage) {
    this.appErrorCode = code;
    this.userMessage = msg;
    this.type = type;
    this.detailMessage = detailMessage;
  }
}
