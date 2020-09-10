/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.cloud.healthcare.fdamystudies.common.JsonNodeConverter;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

@Setter
@Getter
@Entity
@Table(name = "users")
public class UserEntity {

  @ToString.Exclude
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @ToString.Exclude
  @Column(name = "user_id", updatable = false, length = 64)
  private String userId;

  @Column(
      name = "created",
      insertable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp created;

  @ToString.Exclude
  @Column(name = "tempRegId", nullable = true, length = 64)
  private String tempRegId;

  @ToString.Exclude
  @Column(name = "email", nullable = false, length = 320)
  private String email;

  @Column(name = "app_id", nullable = false, length = 100)
  private String appId;

  @ToString.Exclude
  @Column(name = "user_info", nullable = false, columnDefinition = "json")
  @Convert(converter = JsonNodeConverter.class)
  private JsonNode userInfo;

  /** Refer UserAccountStatus enum for values. */
  @Column(name = "status", nullable = false)
  private Integer status;
}
