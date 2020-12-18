package com.hphc.mystudies.bean;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

@Getter
@Setter
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
}
