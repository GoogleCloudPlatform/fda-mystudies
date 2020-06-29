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
import java.util.Date;
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
@Table(name = "auth_info")
public class AuthInfoBO implements Serializable {

  private static final long serialVersionUID = 4985607753888575491L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "auth_id")
  private Integer authId;

  @Column(name = "app_info_id")
  private Integer appId;

  @Column(name = "user_details_id")
  private Integer userId;

  @Column(name = "auth_key")
  private String authKey;

  @Column(name = "device_token")
  private String deviceToken;

  @Column(name = "device_type")
  private String deviceType;

  @Column(name = "android_app_version")
  private String androidAppVersion;

  @Column(name = "ios_app_version")
  private String iosAppVersion;

  @Column(name = "created_on")
  private LocalDateTime createdOn;

  @Column(name = "modified_on")
  private Date modifiedOn;

  @Column(name = "remote_notification_flag", columnDefinition = "TINYINT(1)")
  private Boolean remoteNotificationFlag = false;

  @Column(name = "_ts")
  private Date _ts;
}
