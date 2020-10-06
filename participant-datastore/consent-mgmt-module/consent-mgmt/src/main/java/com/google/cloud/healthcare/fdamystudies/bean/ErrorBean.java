/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.bean;

public class ErrorBean {

  private Integer code = 0;
  private String message = "";

  public Integer getCode() {

    return code;
  }

  public ErrorBean setCode(Integer code) {
    this.code = code;
    return this;
  }

  public String getMessage() {
    return message;
  }

  public ErrorBean setMessage(String message) {
    this.message = message;
    return this;
  }

  public ErrorBean() {}

  public ErrorBean(Integer code, String msg) {
    this.code = code;
    this.message = msg;
  }
}
