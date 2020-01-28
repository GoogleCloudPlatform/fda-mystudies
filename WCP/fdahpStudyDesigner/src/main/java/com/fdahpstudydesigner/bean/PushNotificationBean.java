package com.fdahpstudydesigner.bean;

/** @author BTC */
public class PushNotificationBean {

  private String customStudyId = "";
  private Integer notificationId = 0;
  private String notificationSubType = "Announcement";
  private String notificationText = "";
  private String notificationTitle = "";
  private String notificationType = "ST";
  private String appId = "";

  public String getCustomStudyId() {
    return customStudyId;
  }

  public void setCustomStudyId(String customStudyId) {
    this.customStudyId = customStudyId;
  }

  public Integer getNotificationId() {
    return notificationId;
  }

  public void setNotificationId(Integer notificationId) {
    this.notificationId = notificationId;
  }

  public String getNotificationSubType() {
    return notificationSubType;
  }

  public void setNotificationSubType(String notificationSubType) {
    this.notificationSubType = notificationSubType;
  }

  public String getNotificationText() {
    return notificationText;
  }

  public void setNotificationText(String notificationText) {
    this.notificationText = notificationText;
  }

  public String getNotificationTitle() {
    return notificationTitle;
  }

  public void setNotificationTitle(String notificationTitle) {
    this.notificationTitle = notificationTitle;
  }

  public String getNotificationType() {
    return notificationType;
  }

  public void setNotificationType(String notificationType) {
    this.notificationType = notificationType;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  @Override
  public String toString() {
    return "PushNotificationBean [customStudyId="
        + customStudyId
        + ", notificationId="
        + notificationId
        + ", notificationSubType="
        + notificationSubType
        + ", notificationText="
        + notificationText
        + ", notificationTitle="
        + notificationTitle
        + ", notificationType="
        + notificationType
        + ", appId="
        + appId
        + "]";
  }
}
