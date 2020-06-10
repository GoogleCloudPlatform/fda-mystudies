package com.google.cloud.healthcare.fdamystudies.bean;

import lombok.Data;

@Data
public class ApiError {
  private final int code;
  private final String message;
}
