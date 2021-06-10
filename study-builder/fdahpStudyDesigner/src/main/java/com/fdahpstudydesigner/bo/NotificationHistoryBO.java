/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.bo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "notification_history")
public class NotificationHistoryBO implements Serializable {

  private static final long serialVersionUID = 3634540541782531200L;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "history_id", updatable = false, nullable = false)
  private String historyId;

  @Column(name = "notification_id")
  private String notificationId;

  @Column(name = "notification_sent_date_time")
  private String notificationSentDateTime;

  @Transient private String notificationSentdtTime;

  public String getHistoryId() {
    return historyId;
  }

  public String getNotificationId() {
    return notificationId;
  }

  public String getNotificationSentDateTime() {
    return notificationSentDateTime;
  }

  public String getNotificationSentdtTime() {
    return notificationSentdtTime;
  }

  public void setHistoryId(String historyId) {
    this.historyId = historyId;
  }

  public void setNotificationId(String notificationId) {
    this.notificationId = notificationId;
  }

  public void setNotificationSentDateTime(String notificationSentDateTime) {
    this.notificationSentDateTime = notificationSentDateTime;
  }

  public void setNotificationSentdtTime(String notificationSentdtTime) {
    this.notificationSentdtTime = notificationSentdtTime;
  }
}
