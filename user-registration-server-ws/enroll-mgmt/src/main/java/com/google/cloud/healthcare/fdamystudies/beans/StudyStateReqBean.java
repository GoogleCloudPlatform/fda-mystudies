package com.google.cloud.healthcare.fdamystudies.beans;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class StudyStateReqBean {
  private List<StudiesBean> studies;
  private List<ActivitiesBean> activity;
  private String studyId;
}
