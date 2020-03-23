package com.google.cloud.healthcare.fdamystudies.beans;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WithdrawFromStudyBodyProvider {
  private String participantId;
  private String customStudyId;
}
