package com.google.cloud.healthcare.fdamystudies.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Entity
@Table(name = "app_info")
public class AppInfoDetailsBO implements Serializable {

  private static final long serialVersionUID = 4985607753888575491L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "app_info_id")
  private int appInfoId;

  @Column(name = "custom_app_id")
  private String customAppId;

  @Column(name = "org_info_id")
  private int orgInfoId;

  @Column(name = "created_on")
  private Date createdOn;

  @Column(name = "android_bundle_id")
  private String androidBundleId;

  @Column(name = "app_name")
  private String appName;

  @Column(name = "created_by", columnDefinition = "INT(20) default 0")
  private Integer createdBy;

  @Column(name = "modified_by", columnDefinition = "INT(20) default 0")
  private Integer modifiedBy;

  @Column(name = "android_server_key")
  private String androidServerKey;

  @Column(name = "app_description")
  @Type(type = "text")
  private String appDescription;

  @Column(name = "ios_certificate")
  private String iosCertificate;

  @Column(name = "ios_bundle_id")
  private String iosBundleId;

  @Column(name = "ios_certificate_password")
  private String iosCertificatePassword;

  @Column(name = "from_email_id")
  private String formEmailId;

  @Column(name = "from_email_password")
  private String fromEmailPassword;

  @Column(name = "forgot_email_body")
  private String forgotEmailBody;

  @Column(name = "forgot_email_sub")
  private String forgotEmailSub;

  @Column(name = "reg_email_body")
  private String regEmailBody;

  @Column(name = "reg_email_sub")
  private String regEmailSub;

  @Column(name = "method_handler", columnDefinition = "TINYINT(1)")
  private Boolean methodHandler;
}
