package com.google.cloud.healthcare.fdamystudies.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
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

  /** Refer AuditLogEventStatus enum for values. */
  @Column(name = "event_status")
  private int eventStatus;

  @Column(name = "http_status_code")
  private int httpStatusCode;

  @Column(name = "retry_count", nullable = true)
  private long retryCount;

  @Column(name = "created_timestamp")
  private long createdTimestamp;

  @Column(name = "last_modified_timestamp")
  private long lastModifiedTimestamp;

  @Column(name = "event_details", nullable = false, columnDefinition = "json")
  private String eventDetails;

  @SuppressWarnings("deprecation")
  public String getEventDetails() {
    if (StringUtils.startsWith(eventDetails, "\"")) {
      eventDetails = eventDetails.substring(1, eventDetails.length() - 1);
    }
    eventDetails = StringEscapeUtils.unescapeJava(eventDetails);
    return eventDetails;
  }
}
