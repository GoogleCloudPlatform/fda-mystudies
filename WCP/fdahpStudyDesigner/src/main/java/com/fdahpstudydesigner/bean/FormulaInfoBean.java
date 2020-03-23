package com.fdahpstudydesigner.bean;

public class FormulaInfoBean {

  String lhsData = "";
  String message = "failure";
  String outPutData = "";
  String rhsData = "";
  String statusMessage = "";

  public String getLhsData() {
    return lhsData;
  }

  public void setLhsData(String lhsData) {
    this.lhsData = lhsData;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getOutPutData() {
    return outPutData;
  }

  public void setOutPutData(String outPutData) {
    this.outPutData = outPutData;
  }

  public String getRhsData() {
    return rhsData;
  }

  public void setRhsData(String rhsData) {
    this.rhsData = rhsData;
  }

  public String getStatusMessage() {
    return statusMessage;
  }

  public void setStatusMessage(String statusMessage) {
    this.statusMessage = statusMessage;
  }
}
