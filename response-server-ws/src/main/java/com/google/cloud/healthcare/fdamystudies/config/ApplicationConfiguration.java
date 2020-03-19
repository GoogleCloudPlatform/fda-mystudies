/**
 * ***************************************************************************** Copyright 2020
 * Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * ****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({
  @PropertySource("classpath:application-appConfig.properties"),
  @PropertySource("classpath:application-${spring.profiles.active}.properties")
})
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

  @Value("${supportStringResponse}")
  private String supportStringResponse;

  @Value("${responseSupportedQTypeDouble}")
  private String responseSupportedQTypeDouble;

  @Value("${responseSupportedQTypeDate}")
  private String responseSupportedQTypeDate;

  @Value("${responseSupportedQTypeString}")
  private String responseSupportedQTypeString;

  @Value("${saveRawResponseData}")
  private String saveRawResponseData;

  @Value("${wcpAuthorizationHeader}")
  private String wcpAuthorizationHeader;

  @Value("${regServerPartStudyInfoUrl}")
  private String regServerPartStudyInfoUrl;

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

  public String getSupportStringResponse() {
    return supportStringResponse;
  }

  public void setSupportStringResponse(String supportStringResponse) {
    this.supportStringResponse = supportStringResponse;
  }

  public String getResponseSupportedQTypeDouble() {
    return responseSupportedQTypeDouble;
  }

  public void setResponseSupportedQTypeDouble(String responseSupportedQTypeDouble) {
    this.responseSupportedQTypeDouble = responseSupportedQTypeDouble;
  }

  public String getResponseSupportedQTypeDate() {
    return responseSupportedQTypeDate;
  }

  public void setResponseSupportedQTypeDate(String responseSupportedQTypeDate) {
    this.responseSupportedQTypeDate = responseSupportedQTypeDate;
  }

  public String getResponseSupportedQTypeString() {
    return responseSupportedQTypeString;
  }

  public void setResponseSupportedQTypeString(String responseSupportedQTypeString) {
    this.responseSupportedQTypeString = responseSupportedQTypeString;
  }

  public String getSaveRawResponseData() {
    return saveRawResponseData;
  }

  public void setSaveRawResponseData(String saveRawResponseData) {
    this.saveRawResponseData = saveRawResponseData;
  }

  public String getWcpAuthorizationHeader() {
    return wcpAuthorizationHeader;
  }

  public void setWcpAuthorizationHeader(String wcpAuthorizationHeader) {
    this.wcpAuthorizationHeader = wcpAuthorizationHeader;
  }

  public String getRegServerPartStudyInfoUrl() {
    return regServerPartStudyInfoUrl;
  }

  public void setRegServerPartStudyInfoUrl(String regServerPartStudyInfoUrl) {
    this.regServerPartStudyInfoUrl = regServerPartStudyInfoUrl;
  }
}
