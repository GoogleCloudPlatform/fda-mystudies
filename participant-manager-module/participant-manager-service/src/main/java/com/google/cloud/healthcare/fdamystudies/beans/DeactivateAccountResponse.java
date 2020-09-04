package com.google.cloud.healthcare.fdamystudies.beans;

import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeactivateAccountResponse extends BaseResponse {

  public DeactivateAccountResponse(MessageCode messageCode) {
    super(messageCode);
  }
}
