/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Configuration
@PropertySource("classpath:messageResource.properties")
@ToString
@Getter
@Setter
public class ApplicationPropertyConfiguration {

  @Value("${authServerAccessTokenValidationUrl}")
  private String authServerAccessTokenValidationUrl;

  @Value("${clientId}")
  private String clientId;

  @Value("${secretKey}")
  private String secretKey;

  @Value("${interceptor}")
  private String interceptorUrls;

  @Value("${bucketName}")
  private String bucketName;

  @Value("${auditEventConsentSignName}")
  private String auditEventConsentSignName;

  @Value("${auditEventConsentSignDesc}")
  private String auditEventConsentSignDesc;

  @Value("${auditEventConsentSignFailName}")
  private String auditEventConsentSignFailName;

  @Value("${auditEventConsentSignFailDesc}")
  private String auditEventConsentSignFailDesc;

  @Value("${auditEventEnrollIntoStudyName}")
  private String auditEventEnrollIntoStudyName;

  @Value("${auditEventEnrollIntoStudyDesc}")
  private String auditEventEnrollIntoStudyDesc;

  @Value("${auditEventEnrollIntoStudyFailName}")
  private String auditEventEnrollIntoStudyFailName;

  @Value("${auditEventEnrollIntoStudyFailDesc}")
  private String auditEventEnrollIntoStudyFailDesc;

  @Value("${auditEventConsentProvidedName}")
  private String auditEventConsentProvidedName;

  @Value("${auditEventConsentProvidedDesc}")
  private String auditEventConsentProvidedDesc;

  @Value("${mobileAppClientId}")
  private String mobileAppClientId;

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

  public String getBucketName() {
    return bucketName;
  }

  public void setBucketName(String bucketName) {
    this.bucketName = bucketName;
  }
}
