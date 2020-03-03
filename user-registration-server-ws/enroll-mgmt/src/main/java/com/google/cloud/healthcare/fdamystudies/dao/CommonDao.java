package com.google.cloud.healthcare.fdamystudies.dao;

import org.springframework.stereotype.Repository;

@Repository
public interface CommonDao {

  public Integer getUserInfoDetails(String userId);

  public Integer getStudyId(String customStudyId);
}
