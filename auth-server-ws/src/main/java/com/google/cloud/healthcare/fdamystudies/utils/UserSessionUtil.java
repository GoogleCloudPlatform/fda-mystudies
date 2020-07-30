package com.google.cloud.healthcare.fdamystudies.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;

@Component
public class UserSessionUtil {
  private static final Logger logger = LoggerFactory.getLogger(UserSessionUtil.class);

  @Autowired private RestTemplate restTemplate;

  @Autowired private ApplicationPropertyConfiguration appPropertyConfiguration;

  public void removeDeviceToken(String userId) {
    logger.info("UserSessionUtil removeDeviceToken() - starts ");
    HttpHeaders headers = null;
    try {
      headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set(AppConstants.USER_ID, userId);
      HttpEntity<?> request = new HttpEntity<>(null, headers);
      restTemplate.exchange(
          appPropertyConfiguration.getUserRegistrationServerRemoveDeviceTokenUrl(),
          HttpMethod.PUT,
          request,
          String.class);

    } catch (Exception e) {
      logger.error("UserSessionUtil removeDeviceToken() - error ", e);
    }
    logger.info("UserSessionUtil removeDeviceToken() - ends ");
  }
}
