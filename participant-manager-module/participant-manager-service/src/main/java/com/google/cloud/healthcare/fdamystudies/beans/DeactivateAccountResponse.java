package com.google.cloud.healthcare.fdamystudies.beans;

import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeactivateAccountResponse extends BaseResponse {

  public DeactivateAccountResponse(ErrorCode errorCode) {
    super(errorCode);
  }

  public DeactivateAccountResponse(MessageCode messageCode) {
    super(messageCode);
  }
}
