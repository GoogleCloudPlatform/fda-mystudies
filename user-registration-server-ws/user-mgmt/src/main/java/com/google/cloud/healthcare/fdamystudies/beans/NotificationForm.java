/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import java.util.List;

public class NotificationForm {
  public List<NotificationBean> notifications;

  public List<NotificationBean> getNotifications() {
    return notifications;
  }

  public void setNotifications(List<NotificationBean> notifications) {
    this.notifications = notifications;
  }
}
