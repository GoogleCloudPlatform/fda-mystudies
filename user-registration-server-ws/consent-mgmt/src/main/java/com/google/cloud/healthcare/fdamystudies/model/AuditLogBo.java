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
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "audit_log")
public class AuditLogBo implements Serializable {

  private static final long serialVersionUID = -3019529323339411129L;

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Column(name = "auth_user_id")
  private String authUserId;

  @Column(name = "actvity_name")
  private String activityName;

  @Column(name = "activity_description", columnDefinition = "VARCHAR(2000)")
  private String activtyDesc;

  @Column(name = "activity_date_time")
  private LocalDateTime activityDateTime;

  @Column(name = "participant_id")
  private String participantId = AppConstants.NOT_APPLICABLE;

  @Column(name = "study_id")
  private String studyId = AppConstants.NOT_APPLICABLE;

  @Column(name = "access_level")
  private String accessLevel = AppConstants.NOT_APPLICABLE;

  @Column(name = "server_client_id")
  private String serverClientId;
}
