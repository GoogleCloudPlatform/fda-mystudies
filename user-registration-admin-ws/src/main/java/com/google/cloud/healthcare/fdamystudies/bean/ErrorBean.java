/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.bean;

public class ErrorBean {

  private Integer app_error_code = 0;
  private String userMessage = "";
  private String type = "";
  private String detailMessage = "";

  public ErrorBean() {}

  public ErrorBean setMessage(String message) {
    this.userMessage = message;
    return this;
  }

  public ErrorBean(Integer code, String msg) {
    this.app_error_code = code;
    this.userMessage = msg;
  }

  public ErrorBean(Integer code, String msg, String type, String detailMessage) {
    this.app_error_code = code;
    this.userMessage = msg;
    this.type = type;
    this.detailMessage = detailMessage;
  }

  public Integer getApp_error_code() {
    return app_error_code;
  }

  public void setApp_error_code(Integer app_error_code) {
    this.app_error_code = app_error_code;
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
