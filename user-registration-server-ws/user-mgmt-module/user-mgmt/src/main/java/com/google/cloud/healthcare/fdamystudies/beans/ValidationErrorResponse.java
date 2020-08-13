package com.google.cloud.healthcare.fdamystudies.beans;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ValidationErrorResponse {
  List<Violation> violations = new ArrayList<>();

  @Data
  @AllArgsConstructor
  public static class Violation {
    String path;
    String message;
  }
}
