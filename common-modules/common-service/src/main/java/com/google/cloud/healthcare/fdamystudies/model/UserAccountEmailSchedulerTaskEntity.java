/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

@ToString
@Setter
@Getter
@Entity
@Table(name = "user_account_email_scheduler_tasks")
public class UserAccountEmailSchedulerTaskEntity implements Serializable {

  private static final long serialVersionUID = 2868902173179239850L;

  @ToString.Exclude
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "user_id")
  private String userId;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "app_id", nullable = true)
  private String appId;

  @Column(name = "mobile_platform", nullable = true)
  private String mobilePlatform;

  @Column(name = "source", nullable = true)
  private String source;

  @Column(name = "correlation_id", nullable = true)
  private String correlationId;

  @Column(name = "app_version", nullable = true)
  private String appVersion;

  /** Allowed values: ACCOUNT_CREATED_EMAIL_TEMPLATE, ACCOUNT_UPDATED_EMAIL_TEMPLATE */
  @Column(name = "email_template_type")
  private String emailTemplateType;

  @Column(name = "status", columnDefinition = "TINYINT(1) default 0")
  private boolean status;
}
