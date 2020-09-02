package com.google.cloud.healthcare.fdamystudies.beans;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserStatusRequest {

  @NotNull private Integer status;

  private String userId;
}
