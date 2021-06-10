/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.bean;

public class StudyIdBean {

  String activetaskStudyId = null;
  Integer activityStudyId = null;
  String consentStudyId = null;
  String questionnarieStudyId = null;

  public String getActivetaskStudyId() {
    return activetaskStudyId;
  }

  public void setActivetaskStudyId(String activetaskStudyId) {
    this.activetaskStudyId = activetaskStudyId;
  }

  public Integer getActivityStudyId() {
    return activityStudyId;
  }

  public void setActivityStudyId(Integer activityStudyId) {
    this.activityStudyId = activityStudyId;
  }

  public String getConsentStudyId() {
    return consentStudyId;
  }

  public void setConsentStudyId(String consentStudyId) {
    this.consentStudyId = consentStudyId;
  }

  public String getQuestionnarieStudyId() {
    return questionnarieStudyId;
  }

  public void setQuestionnarieStudyId(String questionnarieStudyId) {
    this.questionnarieStudyId = questionnarieStudyId;
  }
}
