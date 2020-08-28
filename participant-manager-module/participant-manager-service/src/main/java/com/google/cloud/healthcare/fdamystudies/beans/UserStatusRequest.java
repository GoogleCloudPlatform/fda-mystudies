package com.google.cloud.healthcare.fdamystudies.beans;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserStatusRequest {

  @Min(0)
  @Max(1)
  @NotNull
  private Integer status;

  private String userId;
}
