package com.hphc.mystudies.bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class AuditLogEventRequest implements Serializable {
  private static final long serialVersionUID = 1L;

  private String correlationId;

  private String eventCode;

  private String description;

  private String source;

  private String destination;

  private String resourceServer;

  private String userId;

  private String userAccessLevel;

  private String sourceApplicationVersion;

  private String destinationApplicationVersion;

  private String platformVersion;

  private Timestamp occurred;

  private String appId;

  private String userIp;

  private String mobilePlatform;

  private String appVersion;

  private String participantId;

  private String studyId;

  private String studyVersion;

  private String siteId;

  public String getCorrelationId() {
    return correlationId;
  }

  public String getEventCode() {
    return eventCode;
  }

  public String getDescription() {
    return description;
  }

  public String getSource() {
    return source;
  }

  public String getDestination() {
    return destination;
  }

  public String getResourceServer() {
    return resourceServer;
  }

  public String getUserId() {
    return userId;
  }

  public String getUserAccessLevel() {
    return userAccessLevel;
  }

  public String getSourceApplicationVersion() {
    return sourceApplicationVersion;
  }

  public String getDestinationApplicationVersion() {
    return destinationApplicationVersion;
  }

  public String getPlatformVersion() {
    return platformVersion;
  }

  public Timestamp getOccurred() {
    return occurred;
  }

  public String getAppId() {
    return appId;
  }

  public String getUserIp() {
    return userIp;
  }

  public String getMobilePlatform() {
    return mobilePlatform;
  }

  public String getAppVersion() {
    return appVersion;
  }

  public String getParticipantId() {
    return participantId;
  }

  public String getStudyId() {
    return studyId;
  }

  public String getStudyVersion() {
    return studyVersion;
  }

  public String getSiteId() {
    return siteId;
  }

  public void setCorrelationId(String correlationId) {
    this.correlationId = correlationId;
  }

  public void setEventCode(String eventCode) {
    this.eventCode = eventCode;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public void setDestination(String destination) {
    this.destination = destination;
  }

  public void setResourceServer(String resourceServer) {
    this.resourceServer = resourceServer;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public void setUserAccessLevel(String userAccessLevel) {
    this.userAccessLevel = userAccessLevel;
  }

  public void setSourceApplicationVersion(String sourceApplicationVersion) {
    this.sourceApplicationVersion = sourceApplicationVersion;
  }

  public void setDestinationApplicationVersion(String destinationApplicationVersion) {
    this.destinationApplicationVersion = destinationApplicationVersion;
  }

  public void setPlatformVersion(String platformVersion) {
    this.platformVersion = platformVersion;
  }

  public void setOccurred(Timestamp occurred) {
    this.occurred = occurred;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public void setUserIp(String userIp) {
    this.userIp = userIp;
  }

  public void setMobilePlatform(String mobilePlatform) {
    this.mobilePlatform = mobilePlatform;
  }

  public void setAppVersion(String appVersion) {
    this.appVersion = appVersion;
  }

  public void setParticipantId(String participantId) {
    this.participantId = participantId;
  }

  public void setStudyId(String studyId) {
    this.studyId = studyId;
  }

  public void setStudyVersion(String studyVersion) {
    this.studyVersion = studyVersion;
  }

  public void setSiteId(String siteId) {
    this.siteId = siteId;
  }
}
