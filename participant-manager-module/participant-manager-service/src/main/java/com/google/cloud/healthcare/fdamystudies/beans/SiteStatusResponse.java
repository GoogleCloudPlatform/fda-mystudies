package com.google.cloud.healthcare.fdamystudies.beans;

import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class SiteStatusResponse extends BaseResponse {

  private String siteId;

  private Integer siteStatus;

  public SiteStatusResponse(String siteId, Integer status, MessageCode messageCode) {
    super(messageCode);
    this.siteId = siteId;
  }
}
