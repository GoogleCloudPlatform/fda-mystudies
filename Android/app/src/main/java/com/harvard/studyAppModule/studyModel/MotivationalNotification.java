package com.harvard.studyappmodule.studymodel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class MotivationalNotification extends RealmObject {
  @PrimaryKey private String studyId;
  private boolean fiftyPc;
  private boolean hundredPc;
  private int missed;

  public String getStudyId() {
    return studyId;
  }

  public void setStudyId(String studyId) {
    this.studyId = studyId;
  }

  public boolean isFiftyPc() {
    return fiftyPc;
  }

  public void setFiftyPc(boolean fiftyPc) {
    this.fiftyPc = fiftyPc;
  }

  public boolean isHundredPc() {
    return hundredPc;
  }

  public void setHundredPc(boolean hundredPc) {
    this.hundredPc = hundredPc;
  }

  public int getMissed() {
    return missed;
  }

  public void setMissed(int missed) {
    this.missed = missed;
  }
}
