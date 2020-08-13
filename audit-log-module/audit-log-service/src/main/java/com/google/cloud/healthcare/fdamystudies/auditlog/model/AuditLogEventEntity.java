/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.auditlog.model;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

@Setter
@Getter
@Entity
@Table(name = "audit_log_events")
public class AuditLogEventEntity {

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "log_id", updatable = false, nullable = false)
  private String logId;

  @Column(name = "correlation_id", nullable = false, length = 36)
  private String correlationId;

  @Column(name = "event_code", nullable = false, length = 40)
  private String eventCode;

  @Column(name = "occurred", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  private Timestamp occurred;

  @Column(
      name = "created",
      insertable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp created;

  @Column(name = "app_id", nullable = true, length = 64)
  private String appId;

  @ToString.Exclude
  @Column(name = "user_id", nullable = true, length = 64)
  private String userId;

  @ToString.Exclude
  @Column(name = "user_ip", nullable = true, length = 39)
  private String userIp;

  @ToString.Exclude
  @Column(name = "description", nullable = false, length = 255)
  private String description;

  @Column(name = "app_version", nullable = true, length = 20)
  private String appVersion;

  @Column(name = "destination", nullable = false, length = 50)
  private String destination;

  @Column(name = "source", nullable = false, length = 50)
  private String source;

  @Column(name = "resource_server", nullable = true, length = 50)
  private String resourceServer;

  @Column(name = "mobile_platform", nullable = false, length = 20)
  private String mobilePlatform;

  @Column(name = "source_application_version", nullable = false, length = 20)
  private String sourceApplicationVersion;

  @Column(name = "destination_application_version", nullable = false, length = 20)
  private String destinationApplicationVersion;

  @Column(name = "user_access_level", nullable = true, length = 20)
  private String userAccessLevel;

  @Column(name = "platform_version", nullable = false, length = 20)
  private String platformVersion;

  @Column(name = "participant_id", nullable = true, length = 64)
  private String participantId;

  @Column(name = "study_id", nullable = true, length = 64)
  private String studyId;

  @Column(name = "study_version", nullable = true, length = 20)
  private String studyVersion;
}
