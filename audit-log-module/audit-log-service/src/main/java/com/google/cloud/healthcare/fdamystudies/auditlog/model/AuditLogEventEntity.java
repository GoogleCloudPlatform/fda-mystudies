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
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "correlation_id", nullable = false, length = 36)
  private String correlationId;

  @Column(name = "event_name", nullable = false, length = 40)
  private String eventName;

  @Column(name = "system_id", nullable = false, length = 30)
  private String systemId;

  @Column(name = "occurred", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  private Timestamp occurred;

  @Column(
      name = "created",
      insertable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp created;

  @Column(name = "alert", nullable = false, length = 1)
  private boolean alert;

  @Column(name = "app_id", nullable = true, length = 100)
  private String appId;

  @Column(name = "org_id", nullable = true, length = 100)
  private String orgId;

  @ToString.Exclude
  @Column(name = "user_id", nullable = true, length = 100)
  private String userId;

  @ToString.Exclude
  @Column(name = "system_ip", nullable = false, length = 39)
  private String systemIp;

  @ToString.Exclude
  @Column(name = "description", nullable = false, length = 255)
  private String description;

  @ToString.Exclude
  @Column(name = "event_detail", nullable = false, length = 255)
  private String eventDetail;

  @Column(name = "application_version", nullable = false, length = 60)
  private String applicationVersion;

  @Column(name = "application_component_name", nullable = false, length = 100)
  private String applicationComponentName;

  @Column(name = "client_id", nullable = true, length = 100)
  private String clientId;

  @Column(name = "device_type", nullable = true, length = 10)
  private String deviceType;

  @ToString.Exclude
  @Column(name = "request_uri", nullable = true, length = 255)
  private String requestUri;

  @Column(name = "access_level", nullable = true, length = 10)
  private String accessLevel;

  @Column(name = "device_platform", nullable = true, length = 100)
  private String devicePlatform;

  @Column(name = "resource_server", nullable = true, length = 40)
  private String resourceServer;

  @Column(name = "client_app_version", nullable = true, length = 20)
  private String clientAppVersion;

  @Column(name = "client_access_level", nullable = true, length = 20)
  private String clientAccessLevel;

  @Column(name = "platform_version", nullable = true, length = 20)
  private String platformVersion;
}
