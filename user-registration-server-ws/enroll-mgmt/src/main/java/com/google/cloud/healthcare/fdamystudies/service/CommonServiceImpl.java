package com.google.cloud.healthcare.fdamystudies.service;

import org.apache.commons.lang3.StringUtils;
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

      requestBody = new HttpEntity<>(null, headers);
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

  @Override
  public Integer getUserInfoDetails(String userId) {
    logger.info("CommonServiceImpl getUserInfoDetails() - Starts ");
    Integer userDetailsId = 0;
    try {
      if (!StringUtils.isEmpty(userId)) {
        userDetailsId = commonDao.getUserInfoDetails(userId);
      }
    } catch (Exception e) {
      logger.error("CommonServiceImpl getUserInfoDetails() - error ", e);
    }
    logger.info("CommonServiceImpl getUserInfoDetails() - Ends ");
    return userDetailsId;
  }
}
