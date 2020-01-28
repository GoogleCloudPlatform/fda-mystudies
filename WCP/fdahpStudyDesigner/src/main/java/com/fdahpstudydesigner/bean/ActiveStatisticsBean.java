package com.fdahpstudydesigner.bean;

public class ActiveStatisticsBean {

  String dbVal = "";
  String id = "";
  String idName = "";
  String idVal = "";
  boolean type = false;

  public String getDbVal() {
    return dbVal;
  }

  public String getId() {
    return id;
  }

  public String getIdName() {
    return idName;
  }

  public void setIdName(String idName) {
    this.idName = idName;
  }

  public String getIdVal() {
    return idVal;
  }

  public boolean isType() {
    return type;
  }

  public void setDbVal(String dbVal) {
    this.dbVal = dbVal;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setIdVal(String idVal) {
    this.idVal = idVal;
  }

  public void setType(boolean type) {
    this.type = type;
  }
}
