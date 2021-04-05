/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.bean;

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

public void setCorrelationId(String correlationId) {
	this.correlationId = correlationId;
}

public String getEventCode() {
	return eventCode;
}

public void setEventCode(String eventCode) {
	this.eventCode = eventCode;
}

public String getDescription() {
	return description;
}

public void setDescription(String description) {
	this.description = description;
}

public String getSource() {
	return source;
}

public void setSource(String source) {
	this.source = source;
}

public String getDestination() {
	return destination;
}

public void setDestination(String destination) {
	this.destination = destination;
}

public String getResourceServer() {
	return resourceServer;
}

public void setResourceServer(String resourceServer) {
	this.resourceServer = resourceServer;
}

public String getUserId() {
	return userId;
}

public void setUserId(String userId) {
	this.userId = userId;
}

public String getUserAccessLevel() {
	return userAccessLevel;
}

public void setUserAccessLevel(String userAccessLevel) {
	this.userAccessLevel = userAccessLevel;
}

public String getSourceApplicationVersion() {
	return sourceApplicationVersion;
}

public void setSourceApplicationVersion(String sourceApplicationVersion) {
	this.sourceApplicationVersion = sourceApplicationVersion;
}

public String getDestinationApplicationVersion() {
	return destinationApplicationVersion;
}

public void setDestinationApplicationVersion(String destinationApplicationVersion) {
	this.destinationApplicationVersion = destinationApplicationVersion;
}

public String getPlatformVersion() {
	return platformVersion;
}

public void setPlatformVersion(String platformVersion) {
	this.platformVersion = platformVersion;
}

public Timestamp getOccurred() {
	return occurred;
}

public void setOccurred(Timestamp occurred) {
	this.occurred = occurred;
}

public String getAppId() {
	return appId;
}

public void setAppId(String appId) {
	this.appId = appId;
}

public String getUserIp() {
	return userIp;
}

public void setUserIp(String userIp) {
	this.userIp = userIp;
}

public String getMobilePlatform() {
	return mobilePlatform;
}

public void setMobilePlatform(String mobilePlatform) {
	this.mobilePlatform = mobilePlatform;
}

public String getAppVersion() {
	return appVersion;
}

public void setAppVersion(String appVersion) {
	this.appVersion = appVersion;
}

public String getParticipantId() {
	return participantId;
}

public void setParticipantId(String participantId) {
	this.participantId = participantId;
}

public String getStudyId() {
	return studyId;
}

public void setStudyId(String studyId) {
	this.studyId = studyId;
}

public String getStudyVersion() {
	return studyVersion;
}

public void setStudyVersion(String studyVersion) {
	this.studyVersion = studyVersion;
}

public String getSiteId() {
	return siteId;
}

public void setSiteId(String siteId) {
	this.siteId = siteId;
}
  
  
}
