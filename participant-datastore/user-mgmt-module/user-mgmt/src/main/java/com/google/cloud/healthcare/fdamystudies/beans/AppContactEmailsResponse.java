package com.google.cloud.healthcare.fdamystudies.beans;

import com.google.cloud.healthcare.fdamystudies.bean.AppDetailsVersionBean;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Setter
@Getter
@ToString
@Component
@Scope(value = "prototype")
public class AppContactEmailsResponse extends BaseResponse {
  private String contactUsEmail;
  private String fromEmail;
  private String appName;
  private String termsUrl;
  private String privacyPolicyUrl;
  private String appWebsite;
  private String supportEmail;
  private AppDetailsVersionBean version;

  public AppContactEmailsResponse(
      MessageCode messageCode,
      String contactUsEmail,
      String fromEmail,
      String appName,
      String termsUrl,
      String privacyPolicyUrl,
      String appWebsite,
      String supportEmail,
      AppDetailsVersionBean version) {
    super(messageCode);
    this.contactUsEmail = contactUsEmail;
    this.fromEmail = fromEmail;
    this.appName = appName;
    this.termsUrl = termsUrl;
    this.privacyPolicyUrl = privacyPolicyUrl;
    this.appWebsite = appWebsite;
    this.supportEmail = supportEmail;
    this.version = version;
  }
}
