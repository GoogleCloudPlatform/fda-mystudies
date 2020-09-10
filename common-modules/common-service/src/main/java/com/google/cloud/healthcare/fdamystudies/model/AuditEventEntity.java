/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.model;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

@Setter
@Getter
@Entity
@Table(name = "audit_events")
public class AuditEventEntity {

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  /** Refer AuditLogEventStatus enum for values. */
  private int status;

  @Column(name = "http_status_code")
  private int httpStatusCode;

  @Column(name = "retry_count", nullable = true)
  private long retryCount;

  @Column(name = "created_time")
  @CreationTimestamp
  private Timestamp created;

  @Column(name = "updated_time")
  @UpdateTimestamp
  private Timestamp modified;

  @Column(name = "event_request", nullable = false, columnDefinition = "json")
  private String eventRequest;

  public String getEventRequest() {
    if (StringUtils.startsWith(eventRequest, "\"")) {
      eventRequest = eventRequest.substring(1, eventRequest.length() - 1);
    }
    return StringEscapeUtils.unescapeJava(eventRequest);
  }
}
