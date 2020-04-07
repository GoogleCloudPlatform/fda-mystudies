/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.model;

import java.io.Serializable;
import java.time.LocalDateTime;
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
@Table(name = "activity_log")
public class ActivityLog implements Serializable {

  private static final long serialVersionUID = -3019529323339411129L;

  @Id
  @Column(name = "log_id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer logId;

  @Column(name = "auth_user_id")
  private String authUserId;

  @Column(name = "system_id")
  private String systemId;

  @Column(name = "event")
  private String event;

  @Column(name = "description")
  private String description;

  @Column(name = "created_date_time")
  private LocalDateTime createdDateTime;

  @Column(name = "application_component")
  private String applicationComponent;

  @Column(name = "application_version")
  private String applicationVersion;

  @Column(name = "alert_flag", columnDefinition = "BIT(1)")
  private boolean alertFlag = false;

  @Column(name = "event_date_time")
  private LocalDateTime eventdDateTime;

  @Column(name = "access_level")
  private String accessLevel;

  @Column(name = "app_id")
  private String appId;

  @Column(name = "system_access_level")
  private String systemAcessLevel;

  @Column(name = "location_or_system_ip")
  private String locationOrSystemIP;

  @Column(name = "participant_id")
  private String participantId;

  @Column(name = "study_id")
  private String studyId;

  @Column(name = "sofyware_in_use")
  private String sofywareInUse;

  @Column(name = "platform")
  private String platform;
}
