package com.google.cloud.healthcare.fdamystudies.beans;

import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAccountStatusResponse extends BaseResponse {

  public UserAccountStatusResponse(ErrorCode errorCode) {
    super(errorCode);
  }

  public UserAccountStatusResponse(MessageCode messageCode) {
    super(messageCode);
  }
}
