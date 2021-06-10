/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.dao;

import com.fdahpstudydesigner.bean.PushNotificationBean;
import com.fdahpstudydesigner.bo.NotificationBO;
import com.fdahpstudydesigner.bo.NotificationHistoryBO;
import com.fdahpstudydesigner.util.SessionObject;
import java.util.List;

public interface NotificationDAO {

  public String deleteNotification(
      String notificationIdForDelete, SessionObject sessionObject, String notificationType);

  public NotificationBO getNotification(String notificationId);

  public List<NotificationHistoryBO> getNotificationHistoryListNoDateTime(String notificationId);

  public List<NotificationBO> getNotificationList(String studyId, String type);

  public List<NotificationBO> getNotificationList(String studyId);

  public List<PushNotificationBean> getPushNotificationList(String scheduledTimestamp);

  public String saveOrUpdateOrResendNotification(
      NotificationBO notificationBO,
      String notificationType,
      String buttonType,
      SessionObject sessionObject);

  public List<String> getGatwayAppList();

  public void saveNotification(NotificationBO notificationBO);
}
