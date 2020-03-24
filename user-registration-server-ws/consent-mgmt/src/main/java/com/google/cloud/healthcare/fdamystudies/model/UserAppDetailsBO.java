/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.model;

import java.io.Serializable;
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
@Table(name = "user_app_details")
public class UserAppDetailsBO implements Serializable {

  private static final long serialVersionUID = 4985607753888575491L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "user_app_details_id")
  private Integer userAppDetailsId;

  @Column(name = "app_info_id")
  private Integer appInfoId;

  @Column(name = "user_details_id")
  private Integer userDetailsId;

  @Column(name = "created_on")
  private Date createdOn;
}
