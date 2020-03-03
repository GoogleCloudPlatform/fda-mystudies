package com.google.cloud.healthcare.fdamystudies.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import lombok.ToString;

@Configuration
// @PropertySource("classpath:application-${spring.profiles.active}.properties")
@PropertySource("classpath:messageResource.properties")
@ToString
public class ApplicationPropertyConfiguration {

  @Value("${authServerAccessTokenValidationUrl}")
  private String authServerAccessTokenValidationUrl;

  @Value("${clientId}")
  private String clientId;

  @Value("${secretKey}")
  private String secretKey;

  @Value("${interceptor}")
  private String interceptorUrls;

  public String getAuthServerAccessTokenValidationUrl() {
    return authServerAccessTokenValidationUrl;
  }

  public void setAuthServerAccessTokenValidationUrl(String authServerAccessTokenValidationUrl) {
    this.authServerAccessTokenValidationUrl = authServerAccessTokenValidationUrl;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getSecretKey() {
    return secretKey;
  }

  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }

  public String getInterceptorUrls() {
    return interceptorUrls;
  }

  public void setInterceptorUrls(String interceptorUrls) {
    this.interceptorUrls = interceptorUrls;
  }
}
