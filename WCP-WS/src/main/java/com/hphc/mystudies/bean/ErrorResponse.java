package com.hphc.mystudies.bean;


public class ErrorResponse {

  private ErrorBean error = new ErrorBean();

  public ErrorBean getError() {
    return error;
  }

  public ErrorResponse setError(ErrorBean error) {
    this.error = error;
    return this;
  }
}
