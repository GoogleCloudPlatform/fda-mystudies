/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.json.JSONArray;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
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

  public NotificationBean(
      String studyId, String customStudyId, String appId, String notificationType) {
    super();
    this.studyId = studyId;
    this.customStudyId = customStudyId;
    this.appId = appId;
    this.notificationType = notificationType;
  }
}
