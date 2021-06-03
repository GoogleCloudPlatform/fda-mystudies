/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
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
