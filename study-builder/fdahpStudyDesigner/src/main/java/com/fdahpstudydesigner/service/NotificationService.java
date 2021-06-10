/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.service;

import com.fdahpstudydesigner.bo.NotificationBO;
import com.fdahpstudydesigner.bo.NotificationHistoryBO;
import com.fdahpstudydesigner.util.SessionObject;
import java.util.List;

public interface NotificationService {

  public String deleteNotification(
      String notificationId, SessionObject sessionObject, String notificationType);

  public NotificationBO getNotification(String notificationId);

  public List<NotificationHistoryBO> getNotificationHistoryListNoDateTime(String notificationId);

  public List<NotificationBO> getNotificationList(String studyId, String type);

  public String saveOrUpdateOrResendNotification(
      NotificationBO notificationBO,
      String notificationType,
      String buttonType,
      SessionObject sessionObject,
      String customStudyId);

  public List<String> getGatwayAppList();
}
