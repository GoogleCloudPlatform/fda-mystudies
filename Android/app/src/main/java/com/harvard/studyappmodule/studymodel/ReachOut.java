package com.harvard.studyappmodule.studymodel;

import io.realm.RealmObject;

public class ReachOut extends RealmObject {
  private String message;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
