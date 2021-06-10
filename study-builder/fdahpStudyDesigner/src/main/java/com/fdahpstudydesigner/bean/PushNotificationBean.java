/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.bean;

public class PushNotificationBean {

  private String customStudyId = "";
  private String notificationId = "";
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

  public String getNotificationId() {
    return notificationId;
  }

  public void setNotificationId(String notificationId) {
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
