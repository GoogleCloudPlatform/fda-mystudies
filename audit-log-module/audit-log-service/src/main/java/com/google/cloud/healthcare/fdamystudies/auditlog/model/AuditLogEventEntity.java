/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.auditlog.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "audit_log_events")
public class AuditLogEventEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false)
  private long id;

  @Column(name = "correlation_id", nullable = false, length = 255)
  private String correlationId;

  @Column(name = "event_name", nullable = false, length = 255)
  private String eventName;

  @Column(name = "system_id", nullable = false, length = 255)
  private String systemId;

  @Column(name = "event_timestamp", nullable = false, length = 20)
  private long eventTimestamp;

  @Column(name = "created_timestamp", nullable = false, length = 20)
  private long createdTimestamp;

  @Column(name = "alert", nullable = false, length = 1)
  private boolean alert;

  @Column(name = "app_id", nullable = true, length = 255)
  private String appId;

  @Column(name = "org_id", nullable = true, length = 255)
  private String orgId;

  @Column(name = "user_id", nullable = true, length = 255)
  private String userId;

  @Column(name = "event_info", nullable = false, columnDefinition = "json")
  private String eventInfo;
}
