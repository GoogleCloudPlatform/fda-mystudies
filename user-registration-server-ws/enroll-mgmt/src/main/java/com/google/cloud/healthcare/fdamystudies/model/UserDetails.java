package com.google.cloud.healthcare.fdamystudies.model;

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
import lombok.ToString;

@ToString
@Setter
@Getter
@Entity
@Table(name = "user_details")
public class UserDetails {
  @Id
  @Column(name = "user_details_id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer userDetailsId;

  @Column(name = "user_id")
  private String userId;

  @Column(name = "app_info_id")
  private Integer appInfoId;

  @Column(name = "email")
  private String email;

  @Column(name = "status")
  private Integer status;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "local_notification_flag", columnDefinition = "TINYINT(1)")
  private Boolean localNotificationFlag = false;

  @Column(name = "locale")
  private String locale;

  @Column(name = "remote_notification_flag", columnDefinition = "TINYINT(1)")
  private Boolean remoteNotificationFlag = false;

  @Column(name = "security_token")
  private String securityToken;

  @Column(name = "touch_id", columnDefinition = "TINYINT(1)")
  private Boolean touchId = false;

  @Column(name = "_ts")
  private Date _ts;

  @Column(name = "use_pass_code", columnDefinition = "TINYINT(1)")
  private Boolean usePassCode = false;

  @Column(name = "verification_date")
  private Date verificationDate;

  @Column(name = "reminder_lead_time")
  private String reminderLeadTime;

  @Column(name = "code_expire_date")
  private LocalDateTime codeExpireDate;

  @Column(name = "email_code")
  private String emailCode;

  /*
   @Column(name = "password")
   private String password;

  @Column(name = "temp_password", columnDefinition = "TINYINT(1)")
  private Boolean tempPassword = false;

  @Column(name = "reset_password")
  private String resetPassword;

  @Column(name = "temp_password_date")
  private Date tempPasswordDate;

  @Column(name = "password_updated_date")
  private Date passwordUpdatedDate;
  */
}
