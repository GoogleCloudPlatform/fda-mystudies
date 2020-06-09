/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.bean;

import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;

public class ErrorBean {
  private Integer appErrorCode = 0;
  private String userMessage = AppConstants.EMPTY_STR;
  private String type = AppConstants.EMPTY_STR;
  private String detailMessage = AppConstants.EMPTY_STR;

  public ErrorBean() {}

  public ErrorBean setMessage(String message) {
    this.userMessage = message;
    return this;
  }

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

  public Integer getAppErrorCode() {
    return appErrorCode;
  }

  public void setAppErrorCode(Integer appErrorCode) {
    this.appErrorCode = appErrorCode;
  }

  public String getUserMessage() {
    return userMessage;
  }

  public void setUserMessage(String userMessage) {
    this.userMessage = userMessage;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getDetailMessage() {
    return detailMessage;
  }

  public void setDetailMessage(String detailMessage) {
    this.detailMessage = detailMessage;
  }
}
