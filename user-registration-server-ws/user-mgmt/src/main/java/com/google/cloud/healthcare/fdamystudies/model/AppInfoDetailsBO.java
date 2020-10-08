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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "app_info")
public class AppInfoDetailsBO implements Serializable {

  private static final long serialVersionUID = 4985607753888575491L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "app_info_id")
  private int appInfoId;

  @Column(name = "custom_app_id", unique = true)
  private String appId;

  @Column(name = "app_name", columnDefinition = "VARCHAR(255)")
  private String appName;

  @Column(name = "app_description", columnDefinition = "VARCHAR(255)")
  private String appDescription;

  @ManyToOne
  @JoinColumn(name = "org_info_id")
  private OrgInfo orgInfo;

  @Column(name = "ios_bundle_id")
  private String iosBundleId;

  @Column(name = "android_bundle_id")
  private String androidBundleId;

  @Column(name = "ios_certificate", columnDefinition = "VARCHAR(5000)")
  private String iosCertificate;

  @Column(name = "ios_certificate_password")
  private String iosCertificatePassword;

  @Column(name = "android_server_key")
  private String androidServerKey;

  @Column(name = "from_email_id")
  private String formEmailId;

  @Column(name = "from_email_password")
  private String fromEmailPassword;

  @Column(name = "reg_email_sub")
  private String regEmailSub;

  @Column(name = "reg_email_body")
  private String regEmailBody;

  @Column(name = "forgot_email_sub")
  private String forgotEmailSub;

  @Column(name = "forgot_email_body")
  private String forgotEmailBody;

  @Column(name = "method_handler", columnDefinition = "TINYINT(1)")
  private Integer methodHandler;

  @Column(name = "created_by", columnDefinition = "INT(20)")
  private Integer createdBy;

  @Column(name = "modified_by", columnDefinition = "INT(20)")
  private Integer modifiedBy;

  @Column(
      name = "modified_date",
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
  private Date modifiedDate;

  @Column(name = "created_on", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Date createdOn;
}
