/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.json.JSONArray;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NotificationBean {
  private String studyId;
  private String customStudyId;
  private String notificationText;
  private String notificationTitle;
  private String notificationType;
  private String notificationSubType;
  private Integer notificationId;
  private String deviceType;
  private JSONArray deviceToken;
  private String appId;

  public String getStudyId() {
    return studyId;
  }

  public void setStudyId(String studyId) {
    this.studyId = studyId;
  }

  public String getCustomStudyId() {
    return customStudyId;
  }

  public void setCustomStudyId(String customStudyId) {
    this.customStudyId = customStudyId;
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

  public String getNotificationSubType() {
    return notificationSubType;
  }

  public void setNotificationSubType(String notificationSubType) {
    this.notificationSubType = notificationSubType;
  }

  public Integer getNotificationId() {
    return notificationId;
  }

  public void setNotificationId(Integer notificationId) {
    this.notificationId = notificationId;
  }

  public String getDeviceType() {
    return deviceType;
  }

  public void setDeviceType(String deviceType) {
    this.deviceType = deviceType;
  }

  public JSONArray getDeviceToken() {
    return deviceToken;
  }

  public void setDeviceToken(JSONArray deviceToken) {
    this.deviceToken = deviceToken;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public NotificationBean(
      String studyId, String customStudyId, String appId, String notificationType) {
    super();
    this.studyId = studyId;
    this.customStudyId = customStudyId;
    this.appId = appId;
    this.notificationType = notificationType;
  }
}
