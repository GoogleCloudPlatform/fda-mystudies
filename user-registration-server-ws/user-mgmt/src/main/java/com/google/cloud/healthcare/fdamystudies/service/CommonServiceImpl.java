/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.service;

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
import com.google.cloud.healthcare.fdamystudies.beans.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.beans.BodyForProvider;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;

@Service
public class CommonServiceImpl implements CommonService {

  @Autowired private RestTemplate restTemplate;

  @Autowired private ApplicationPropertyConfiguration appConfig;

  @Autowired CommonDao commonDao;

  private static Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

  @Override
  public String validatedUserAppDetailsByAllApi(
      String userId, String email, String appId, String orgId) {
    logger.info("UserManagementProfileServiceImpl validatedUserAppDetailsByAllApi() - Started ");
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
    logger.info("UserManagementProfileServiceImpl validatedUserAppDetailsByAllApi() - Ends ");
    return message;
  }

  @Override
  public AppOrgInfoBean getUserAppDetailsByAllApi(
      String userId, String emailId, String appId, String orgId) {
    AppOrgInfoBean appOrgInfoBean = new AppOrgInfoBean();
    logger.info("MyStudiesUserRegUtil - getUserAppDetailsByAllApi() Start");
    try {
      appOrgInfoBean = commonDao.getUserAppDetailsByAllApi(userId, appId, orgId);
    } catch (Exception e) {
      logger.error("MyStudiesUserRegUtil - getUserAppDetailsByAllApi() - error() ", e);
    }

    logger.info("MyStudiesUserRegUtil - getUserAppDetailsByAllApi() Ends");
    return appOrgInfoBean;
  }

  @Override
  public Integer validateAccessToken(String userId, String accessToken, String clientToken) {
    logger.info("CommonServiceImpl validateAccessToken() - starts ");
    Integer value = null;
    HttpHeaders headers = null;
    BodyForProvider providerBody = null;
    HttpEntity<BodyForProvider> requestBody = null;
    ResponseEntity<?> responseEntity = null;
    try {
      headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("clientToken", clientToken);
      headers.set("userId", userId);
      headers.set("accessToken", accessToken);

      /*providerBody = new BodyForProvider();
      providerBody.setUserId(userId);
      providerBody.setAccessToken(accessToken);*/

      requestBody = new HttpEntity<BodyForProvider>(null, headers);
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
    logger.info("CommonServiceImpl validateAccessToken() - ends ");
    return value;
  }
}
