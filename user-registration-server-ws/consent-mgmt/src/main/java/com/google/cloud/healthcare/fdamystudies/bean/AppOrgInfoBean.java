package com.google.cloud.healthcare.fdamystudies.bean;

import lombok.Getter;
import lombok.Setter;

public class AppOrgInfoBean {
  private int appInfoId;
  private int orgInfoId;
public int getAppInfoId() {
	return appInfoId;
}
public void setAppInfoId(int appInfoId) {
	this.appInfoId = appInfoId;
}
public int getOrgInfoId() {
	return orgInfoId;
}
public void setOrgInfoId(int orgInfoId) {
	this.orgInfoId = orgInfoId;
}
}
