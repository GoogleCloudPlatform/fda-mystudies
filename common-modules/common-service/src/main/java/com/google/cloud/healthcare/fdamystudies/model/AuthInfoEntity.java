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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Entity
@Table(name = "auth_info")
@ConditionalOnProperty(
    value = "participant.datastore.entities.enabled",
    havingValue = "true",
    matchIfMissing = false)
public class AuthInfoEntity implements Serializable {

  private static final long serialVersionUID = 4985607753888575491L;

  @ToString.Exclude
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "auth_id", nullable = false, length = ColumnConstraints.ID_LENGTH)
  private Integer authId;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "app_info_id", nullable = false)
  private AppEntity appInfo;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "user_details_id", nullable = false)
  private UserDetailsEntity userDetails;

  @Column(name = "auth_key", length = ColumnConstraints.SMALL_LENGTH)
  private String authKey;

  @Column(name = "device_token")
  @Type(type = "text")
  private String deviceToken;

  @Column(name = "device_type", length = ColumnConstraints.SMALL_LENGTH)
  private String deviceType;

  @Column(name = "android_app_version", length = ColumnConstraints.SMALL_LENGTH)
  private String androidAppVersion;

  @Column(name = "ios_app_version", length = ColumnConstraints.SMALL_LENGTH)
  private String iosAppVersion;

  @Column(
      name = "created_on",
      insertable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp created;

  @Column(
      name = "modified_date",
      insertable = false,
      updatable = true,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp modified;

  @Column(name = "remote_notification_flag", length = ColumnConstraints.TINY_LENGTH)
  private Boolean remoteNotificationFlag = false;

  @Column(
      name = "timestamp",
      insertable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp timestamp;
}
