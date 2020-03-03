package com.google.cloud.healthcare.fdamystudies.service;

public interface CommonService {

  public Integer validateAccessToken(String userId, String accessToken, String clientToken);

  public Integer getUserInfoDetails(String userId);
}
