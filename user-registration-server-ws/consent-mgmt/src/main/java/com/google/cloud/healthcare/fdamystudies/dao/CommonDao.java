package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.bean.AppOrgInfoBean;

public interface CommonDao {

  public AppOrgInfoBean getUserAppDetailsByAllApi(String userId, String appId, String orgId);

  public Integer getUserDetailsId(String userId);
}
