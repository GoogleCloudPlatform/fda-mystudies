/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ConditionalOnProperty(
    value = "participant.manager.entities.enabled",
    havingValue = "true",
    matchIfMissing = false)
@Setter
@Getter
@ToString
@Entity
@Table(name = "user_details")
public class UserDetailsEntity implements Serializable {

  private static final long serialVersionUID = -6971868842609206885L;

  @ToString.Exclude
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @ToString.Exclude
  @Column(name = "first_name")
  private String firstName;

  @ToString.Exclude
  @Column(name = "last_name")
  private String lastName;

  @ToString.Exclude
  @Column(name = "email")
  private String email;

  @ToString.Exclude
  @Column(name = "use_pass_code")
  private Boolean usePassCode;

  @Column(name = "touch_id")
  private Boolean touchId;

  @Column(name = "local_notification_flag")
  private Boolean localNotificationFlag;

  @Column(name = "remote_notification_flag")
  private Boolean remoteNotificationFlag;

  @Column(name = "status", nullable = false)
  private Integer status;

  @ToString.Exclude
  @Column(name = "password")
  private String password;

  @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REMOVE})
  @JoinColumn(name = "app_info_id")
  private AppEntity appInfo;

  @ToString.Exclude
  @Column(name = "temp_password")
  private Boolean tempPassword = false;

  @Column(name = "locale")
  private String locale;

  @ToString.Exclude
  @Column(name = "reset_password")
  private String resetPassword;

  @Column(
      name = "verification_date",
      insertable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp verificationDate;

  @Column(
      name = "temp_password_date",
      insertable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp tempPasswordDate;

  @Column(
      name = "password_updated_date",
      insertable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp passwordUpdatedDate;

  @Column(name = "reminder_lead_time")
  private String reminderLeadTime;

  @ToString.Exclude
  @Column(name = "security_token")
  private String securityToken;

  @ToString.Exclude
  @Column(name = "email_code")
  private String emailCode;

  @ToString.Exclude
  @Column(name = "user_id")
  private String userId;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "userDetails")
  private List<ParticipantStudyEntity> participantStudies = new ArrayList<>();

  public void addParticipantStudiesEntity(ParticipantStudyEntity participantStudiesEntity) {
    participantStudies.add(participantStudiesEntity);
    participantStudiesEntity.setUserDetails(this);
  }
}
