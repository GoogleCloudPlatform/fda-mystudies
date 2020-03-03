package com.google.cloud.healthcare.fdamystudies.bean;

public class ConsentDocumentBean {
  private ErrorBean error = new ErrorBean();
  private String version;
  private String type;
  private String content;

  public ErrorBean getError() {
    return error;
  }

  public void setError(ErrorBean error) {
    this.error = error;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
