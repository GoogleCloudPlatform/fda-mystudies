package com.harvard.studyappmodule.consent.model;

import io.realm.RealmObject;

public class CorrectAnswerString extends RealmObject {
  private String answer;

  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }
}
