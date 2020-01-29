package com.harvard.webservicemodule.apihelper;

public class Responsemodel {
  private String responseCode;
  private String response;
  private String servermsg;
  private String responseData;

  public String getResponseData() {
    return responseData;
  }

  void setResponseData(String responseData) {
    this.responseData = responseData;
  }

  String getServermsg() {
    return servermsg;
  }

  void setServermsg(String servermsg) {
    this.servermsg = servermsg;
  }

  public String getResponseCode() {
    return responseCode;
  }

  void setResponseCode(String responseCode) {
    this.responseCode = responseCode;
  }

  public String getResponse() {
    return response;
  }

  void setResponse(String response) {
    this.response = response;
  }
}
