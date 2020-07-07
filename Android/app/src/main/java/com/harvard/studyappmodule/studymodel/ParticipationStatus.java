package com.harvard.studyappmodule.studymodel;

import io.realm.RealmObject;

public class ParticipationStatus extends RealmObject {

  private boolean inProgress;
  private boolean yetToJoin;
  private boolean completed;
  private boolean withdrawn;
  private boolean notEligible;

  public boolean isInProgress() {
    return inProgress;
  }

  public void setInProgress(boolean inProgress) {
    this.inProgress = inProgress;
  }

  public boolean isYetToJoin() {
    return yetToJoin;
  }

  public void setYetToJoin(boolean yetToJoin) {
    this.yetToJoin = yetToJoin;
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
