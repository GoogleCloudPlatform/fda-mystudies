package com.harvard.studyappmodule.studymodel;

import io.realm.RealmObject;

public class ParticipationStatus extends RealmObject {

  private boolean enrolled;
  private boolean yetToEnroll;
  private boolean completed;
  private boolean withdrawn;
  private boolean notEligible;

  public boolean isEnrolled() {
    return enrolled;
  }

  public void setEnrolled(boolean enrolled) {
    this.enrolled = enrolled;
  }

  public boolean isYetToEnroll() {
    return yetToEnroll;
  }

  public void setYetToEnroll(boolean yetToEnroll) {
    this.yetToEnroll = yetToEnroll;
  }

  public boolean isCompleted() {
    return completed;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  public boolean isWithdrawn() {
    return withdrawn;
  }

  public void setWithdrawn(boolean withdrawn) {
    this.withdrawn = withdrawn;
  }

  public boolean isNotEligible() {
    return notEligible;
  }

  public void setNotEligible(boolean notEligible) {
    this.notEligible = notEligible;
  }
}
