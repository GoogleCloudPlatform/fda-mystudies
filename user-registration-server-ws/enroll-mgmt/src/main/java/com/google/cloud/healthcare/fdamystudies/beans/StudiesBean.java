package com.google.cloud.healthcare.fdamystudies.beans;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class StudiesBean {
  private String studyId = "";
  private String status = "";
  private Boolean bookmarked;
  private String enrolledDate = "";
  private Integer completion;
  private Integer adherence;
  private String participantId;
}
