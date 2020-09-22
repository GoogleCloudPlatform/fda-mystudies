/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.model;

import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.SMALL_LENGTH;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

@Setter
@Getter
@Entity
@Table(
    name = "auth_info",
    indexes = {
      @Index(name = "user_details_id_index", columnList = "user_details_id", unique = true),
      @Index(
          name = "remote_notification_flag_index",
          columnList = "remote_notification_flag",
          unique = false),
      @Index(name = "device_token_index", columnList = "device_token", unique = true),
      @Index(name = "device_type_index", columnList = "device_type")
    })
public class AuthInfoEntity implements Serializable {

  private static final long serialVersionUID = 4985607753888575491L;

  @ToString.Exclude
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", nullable = false)
  private String authId;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "app_info_id", nullable = false)
  private AppEntity app;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "user_details_id", nullable = false)
  private UserDetailsEntity userDetails;

  @Column(name = "auth_key", length = SMALL_LENGTH)
  private String authKey;

  @Column(name = "device_token")
  @Type(type = "text")
  private String deviceToken;

  @Column(name = "device_type", length = SMALL_LENGTH)
  private String deviceType;

  @Column(name = "android_app_version", length = SMALL_LENGTH)
  private String androidAppVersion;

  @Column(name = "ios_app_version", length = SMALL_LENGTH)
  private String iosAppVersion;

  @Column(name = "created_time")
  @CreationTimestamp
  private Timestamp created;

  @Column(name = "updated_time")
  @UpdateTimestamp
  private Timestamp modified;

  @Column(name = "remote_notification_flag")
  private Boolean remoteNotificationFlag = false;
}
