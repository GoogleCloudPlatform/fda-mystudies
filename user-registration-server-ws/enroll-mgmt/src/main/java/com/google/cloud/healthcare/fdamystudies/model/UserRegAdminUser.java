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
@Table(name = "ur_admin_user")
public class UserRegAdminUser implements Serializable {

  private static final long serialVersionUID = -8807472639054757047L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @Column(name = "email", columnDefinition = "VARCHAR(100)")
  private String email = "";

  @Column(name = "ur_admin_auth_id", columnDefinition = "VARCHAR(255)")
  private String urAdminAuthId = "";

  @Column(name = "first_name", columnDefinition = "VARCHAR(100)")
  private String firstName = "";

  @Column(name = "last_name", columnDefinition = "VARCHAR(100)")
  private String lastName = "";

  @Column(name = "phone_number", columnDefinition = "VARCHAR(20)")
  private String phoneNumber = "";

  @Column(name = "email_changed", columnDefinition = "TINYINT(1)")
  private Integer emailChanged = 0;

  @Column(name = "status", columnDefinition = "TINYINT(1)")
  private Integer status = 0;

  @Column(name = "manage_users", columnDefinition = "TINYINT(1)")
  private Integer manageUsers = 0;

  @Column(name = "manage_locations", columnDefinition = "TINYINT(1)")
  private Integer manageLocations = 0;

  @Column(name = "created", columnDefinition = "TIMESTAMP")
  private LocalDateTime created;

  @Column(name = "created_by", columnDefinition = "INT(20) default 0")
  private Integer createdBy;

  @Column(name = "code_expire_date")
  private LocalDateTime codeExpireDate;

  @Column(name = "email_code")
  private String emailCode;
}
