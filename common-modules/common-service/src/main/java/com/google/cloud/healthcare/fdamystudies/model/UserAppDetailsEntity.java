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
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Setter
@Getter
@Entity
@Table(name = "user_app_details")
public class UserAppDetailsEntity implements Serializable {

  private static final long serialVersionUID = 4985607753888575491L;

  @ToString.Exclude
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String userAppDetailsId;

  @ManyToOne
  @JoinColumn(name = "app_info_id", updatable = false)
  private AppEntity app;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "user_details_id")
  private UserDetailsEntity userDetails;

  @Column(name = "created_time")
  @CreationTimestamp
  private Timestamp created;
}
