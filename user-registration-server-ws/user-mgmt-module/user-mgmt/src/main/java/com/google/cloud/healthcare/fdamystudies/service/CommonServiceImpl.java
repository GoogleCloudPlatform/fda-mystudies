/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.beans.BodyForProvider;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.model.ActivityLog;
import com.google.cloud.healthcare.fdamystudies.repository.ActivityLogRepository;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CommonServiceImpl implements CommonService {

  @Autowired private RestTemplate restTemplate;

  @Autowired private ApplicationPropertyConfiguration appConfig;

  @Autowired private CommonDao commonDao;

  @Autowired private ActivityLogRepository activityLogRepository;

  private static Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

  @Override
  public String validatedUserAppDetailsByAllApi(
      String userId, String email, String appId, String orgId) {
    logger.info("UserManagementProfileServiceImpl validatedUserAppDetailsByAllApi() - starts");
    String message = "";
    AppOrgInfoBean appOrgInfoBean = new AppOrgInfoBean();
    try {
      appOrgInfoBean = commonDao.getUserAppDetailsByAllApi(userId, appId, orgId);
      message =
          commonDao.validatedUserAppDetailsByAllApi(
              userId, email, appOrgInfoBean.getAppInfoId(), appOrgInfoBean.getOrgInfoId());
    } catch (Exception e) {
      logger.error(
          "UserManagementProfileServiceImpl validatedUserAppDetailsByAllApi() - error ", e);
    }
    logger.info("UserManagementProfileServiceImpl validatedUserAppDetailsByAllApi() - ends");
    return message;
  }

  @Override
  public AppOrgInfoBean getUserAppDetailsByAllApi(
      String userId, String emailId, String appId, String orgId) {
    AppOrgInfoBean appOrgInfoBean = new AppOrgInfoBean();
    logger.info("MyStudiesUserRegUtil getUserAppDetailsByAllApi() - starts");
    try {
      appOrgInfoBean = commonDao.getUserAppDetailsByAllApi(userId, appId, orgId);
    } catch (Exception e) {
      logger.error("MyStudiesUserRegUtil getUserAppDetailsByAllApi() - error() ", e);
    }

    logger.info("MyStudiesUserRegUtil getUserAppDetailsByAllApi() - ends");
    return appOrgInfoBean;
  }

  @Override
  public Integer validateAccessToken(String userId, String accessToken, String clientToken) {
    logger.info("CommonServiceImpl validateAccessToken() - starts");
    Integer value = null;
    HttpHeaders headers = null;
    HttpEntity<BodyForProvider> requestBody = null;
    ResponseEntity<?> responseEntity = null;
    try {
      headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set(AppConstants.CLIENT_TOKEN, clientToken);
      headers.set(AppConstants.USER_ID, userId);
      headers.set(AppConstants.ACCESS_TOKEN, accessToken);

      requestBody = new HttpEntity<>(null, headers);
      logger.info("CommonServiceImpl validateAccessToken() " + restTemplate.hashCode());
      responseEntity =
          restTemplate.exchange(
              appConfig.getAuthServerAccessTokenValidationUrl(),
              HttpMethod.POST,
              requestBody,
              Integer.class);
      value = (Integer) responseEntity.getBody();
    } catch (Exception e) {
      logger.error("CommonServiceImpl validateAccessToken() - error ", e);
    }
    logger.info("CommonServiceImpl validateAccessToken() - ends");
    return value;
  }

  @Override
  public ActivityLog createActivityLog(String userId, String activityName, String activtyDesc) {
    logger.info("CommonServiceImpl createActivityLog() - starts");
    ActivityLog activityLog = new ActivityLog();
    try {
      activityLog.setAuthUserId(userId);
      activityLog.setActivityName(activityName);
      activityLog.setActivtyDesc(activtyDesc);
      activityLog.setActivityDateTime(LocalDateTime.now());
      activityLogRepository.save(activityLog);
    } catch (Exception e) {
      logger.error("CommonServiceImpl createActivityLog() - error ", e);
    }
    logger.info("CommonServiceImpl createActivityLog() - ends");
    return activityLog;
  }
}
