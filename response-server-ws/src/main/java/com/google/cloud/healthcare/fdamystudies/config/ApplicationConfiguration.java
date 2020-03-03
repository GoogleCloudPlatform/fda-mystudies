/*******************************************************************************
 * Copyright 2020 Google LLC
 * 
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 ******************************************************************************/
package com.google.cloud.healthcare.fdamystudies.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({@PropertySource("classpath:application-appConfig.properties"),
    @PropertySource("classpath:application-${spring.profiles.active}.properties")})
public class ApplicationConfiguration {
  // application-appConfig.properties properties

  @Value("${firestoreProjectId}")
  private String firestoreProjectId;

  @Value("${interceptor}")
  private String interceptorUrls;

  @Value("${authServerAccessTokenValidationUrl}")
  private String authServerAccessTokenValidationUrl;

  @Value("${serverApiUrls}")
  private String serverApiUrls;

  @Value("${auth.server.url}")
  private String authServerUrl;

  @Value("${wcpStudyActivityMetadataUrl}")
  private String wcpStudyActivityMetadataUrl;

  @Value("${responseDataFilePath}")
  private String responseDataFilePath;

  @Value("${lastResponseOnly}")
  private String lastResponseOnly;

  @Value("${responseSupportedQType}")
  private String responseSupportedQType;

  @Value("${wcpAuthorizationHeader}")
  private String wcpAuthorizationHeader;

  public String getFirestoreProjectId() {
    return firestoreProjectId;
  }

  public void setFirestoreProjectId(String firestoreProjectId) {
    this.firestoreProjectId = firestoreProjectId;
  }

  public String getInterceptorUrls() {
    return interceptorUrls;
  }

  public void setInterceptorUrls(String interceptorUrls) {
    this.interceptorUrls = interceptorUrls;
  }

  public String getAuthServerAccessTokenValidationUrl() {
    return authServerAccessTokenValidationUrl;
  }

  public void setAuthServerAccessTokenValidationUrl(String authServerAccessTokenValidationUrl) {
    this.authServerAccessTokenValidationUrl = authServerAccessTokenValidationUrl;
  }


  public String getServerApiUrls() {
    return serverApiUrls;
  }

  public void setServerApiUrls(String serverApiUrls) {
    this.serverApiUrls = serverApiUrls;
  }

  public String getAuthServerUrl() {
    return authServerUrl;
  }

  public void setAuthServerUrl(String authServerUrl) {
    this.authServerUrl = authServerUrl;
  }

  public String getWcpStudyActivityMetadataUrl() {
    return wcpStudyActivityMetadataUrl;
  }

  public void setWcpStudyActivityMetadataUrl(String wcpStudyActivityMetadataUrl) {
    this.wcpStudyActivityMetadataUrl = wcpStudyActivityMetadataUrl;
  }

  public String getResponseDataFilePath() {
    return responseDataFilePath;
  }

  public void setResponseDataFilePath(String responseDataFilePath) {
    this.responseDataFilePath = responseDataFilePath;
  }

  public String getLastResponseOnly() {
    return lastResponseOnly;
  }

  public void setLastResponseOnly(String lastResponseOnly) {
    this.lastResponseOnly = lastResponseOnly;
  }

  public String getResponseSupportedQType() {
    return responseSupportedQType;
  }

  public void setResponseSupportedQType(String responseSupportedQType) {
    this.responseSupportedQType = responseSupportedQType;
  }

  public String getWcpAuthorizationHeader() {
    return wcpAuthorizationHeader;
  }

  public void setWcpAuthorizationHeader(String wcpAuthorizationHeader) {
    this.wcpAuthorizationHeader = wcpAuthorizationHeader;
  }

}
