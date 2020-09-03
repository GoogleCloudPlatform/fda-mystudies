package com.google.cloud.healthcare.fdamystudies.beans;

import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatchUserResponse extends BaseResponse {

  public PatchUserResponse(ErrorCode errorCode) {
    super(errorCode);
  }

  public PatchUserResponse(MessageCode messageCode) {
    super(messageCode);
  }
}
