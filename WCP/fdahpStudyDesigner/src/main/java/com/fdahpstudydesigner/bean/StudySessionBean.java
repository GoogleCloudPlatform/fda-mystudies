/** */
package com.fdahpstudydesigner.bean;

/** @author BTC */
public class StudySessionBean {

  private String isLive;
  private String permission;
  private Integer sessionStudyCount;
  private String studyId;

  public String getIsLive() {
    return isLive;
  }

  public void setIsLive(String isLive) {
    this.isLive = isLive;
  }

  public String getPermission() {
    return permission;
  }

  public void setPermission(String permission) {
    this.permission = permission;
  }

  public Integer getSessionStudyCount() {
    return sessionStudyCount;
  }

  public void setSessionStudyCount(Integer sessionStudyCount) {
    this.sessionStudyCount = sessionStudyCount;
  }

  public String getStudyId() {
    return studyId;
  }

  public void setStudyId(String studyId) {
    this.studyId = studyId;
  }
}
