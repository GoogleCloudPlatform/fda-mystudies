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
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "users")
public class DaoUserBO implements Serializable {

  private static final long serialVersionUID = 2777766985141875224L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Column(name = "email")
  private String emailId;

  @Column(name = "user_id")
  private String userId;

  @Column(name = "org_id")
  private String orgId;

  @Column(name = "app_id")
  private String appId;

  @Column(name = "app_code")
  private String appCode;

  @Column(name = "salt")
  private String salt;

  @Column(name = "account_status")
  private String accountStatus;

  @Column(name = "email_verification_status")
  private String emailVerificationStatus;

  @JsonIgnore
  @Column(name = "password")
  private String password;

  @Column(name = "password_expire_date")
  private LocalDateTime passwordExpireDate;

  @Column(name = "reset_password")
  private String resetPassword;

  @Column(name = "temp_password", columnDefinition = "TINYINT(1)")
  private Boolean tempPassword = false;

  @Column(name = "temp_password_date")
  private Date tempPasswordDate;

  @Column(name = "password_updated_date")
  private Date passwordUpdatedDate;

  @Column(name = "reminder_lead_time")
  private String reminderLeadTime;

  // @Column(columnDefinition = "DATETIME")
  @Column(name = "created", columnDefinition = "TIMESTAMP")
  private LocalDateTime createdOn;
}
