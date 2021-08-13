package com.google.cloud.healthcare.fdamystudies.beans;

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

  public AppContactEmailsResponse(
      MessageCode messageCode, String contactUsEmail, String fromEmail) {
    super(messageCode);
    this.contactUsEmail = contactUsEmail;
    this.fromEmail = fromEmail;
  }
}
