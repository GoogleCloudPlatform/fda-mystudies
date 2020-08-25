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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.LARGE_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.MEDIUM_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.SMALL_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.XS_LENGTH;

@ConditionalOnProperty(
    value = "participant.manager.entities.enabled",
    havingValue = "true",
    matchIfMissing = false)
@Setter
@Getter
@ToString
@Entity
@Table(
    name = "user_details",
    uniqueConstraints = {
      @UniqueConstraint(
          columnNames = {"user_id", "app_info_id"},
          name = "user_details_user_id_app_info_id_uidx")
    })
public class UserDetailsEntity implements Serializable {

  private static final long serialVersionUID = -6971868842609206885L;

  @ToString.Exclude
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @ToString.Exclude
  @Column(name = "first_name", length = MEDIUM_LENGTH)
  private String firstName;

  @ToString.Exclude
  @Column(name = "last_name", length = MEDIUM_LENGTH)
  private String lastName;

  @ToString.Exclude
  @Column(name = "email", length = LARGE_LENGTH)
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

  @Column(nullable = false)
  private Integer status;

  @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REMOVE})
  @JoinColumn(name = "app_info_id")
  @Index(name = "user_details_app_info_idx")
  private AppEntity app;

  @Column(length = MEDIUM_LENGTH)
  private String locale;

  @Column(name = "verification_time")
  @CreationTimestamp
  private Timestamp verificationDate;

  @Column(name = "code_expire_time")
  @CreationTimestamp
  private Timestamp codeExpireDate;

  @Column(name = "reminder_lead_time", length = SMALL_LENGTH)
  private String reminderLeadTime;

  @ToString.Exclude
  @Column(name = "security_token", length = MEDIUM_LENGTH)
  private String securityToken;

  @ToString.Exclude
  @Column(name = "email_code", length = XS_LENGTH)
  private String emailCode;

  @ToString.Exclude
  @Column(name = "user_id", length = SMALL_LENGTH)
  private String userId;

  // Use UserInstitution class to access institution.
  @Getter(AccessLevel.NONE)
  @OneToOne(mappedBy = "userDetails", fetch = FetchType.LAZY)
  private UserInstitutionEntity userInstitutionEntity;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "userDetails")
  private List<ParticipantStudyEntity> participantStudies = new ArrayList<>();

  public void addParticipantStudiesEntity(ParticipantStudyEntity participantStudiesEntity) {
    participantStudies.add(participantStudiesEntity);
    participantStudiesEntity.setUserDetails(this);
  }

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "userDetails")
  private List<AuthInfoEntity> authInfo = new ArrayList<>();

  public void addAuthInfoEntity(AuthInfoEntity authInfoEntity) {
    authInfo.add(authInfoEntity);
    authInfoEntity.setUserDetails(this);
  }

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "userDetails")
  private List<UserAppDetailsEntity> userAppDetails = new ArrayList<>();

  public void addUserAppDetailsEntity(UserAppDetailsEntity userAppDetailsEntity) {
    userAppDetails.add(userAppDetailsEntity);
    userAppDetailsEntity.setUserDetails(this);
  }
}
