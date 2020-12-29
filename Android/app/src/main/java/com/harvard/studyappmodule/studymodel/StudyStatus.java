package com.harvard.studyappmodule.studymodel;

import io.realm.RealmObject;

public class StudyStatus extends RealmObject {

  private boolean active;
  private boolean paused;
  private boolean closed;

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public boolean isPaused() {
    return paused;
  }

  public void setPaused(boolean paused) {
    this.paused = paused;
  }

  public boolean isClosed() {
    return closed;
  }

  public void setClosed(boolean closed) {
    this.closed = closed;
  }
}
