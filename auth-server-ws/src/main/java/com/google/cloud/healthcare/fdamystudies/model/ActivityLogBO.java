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
public class ActivityLogBO implements Serializable {

  private static final long serialVersionUID = -2704684231429666459L;

  @Id
  @Column(name = "activity_log_id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Column(name = "auth_user_id")
  private String authUserId;

  @Column(name = "actvity_name")
  private String activityName;

  @Column(name = "activity_description")
  private String activtyDesc;

  @Column(name = "activity_date_time")
  private LocalDateTime activityDateTime;
}
