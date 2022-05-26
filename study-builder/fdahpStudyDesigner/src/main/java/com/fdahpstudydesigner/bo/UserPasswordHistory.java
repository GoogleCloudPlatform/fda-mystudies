/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "users_password_history")
@NamedQueries({
  @NamedQuery(
      name = "getPaswordHistoryByUserId",
      query = "From UserPasswordHistory UPH WHERE UPH.userId =:userId ORDER BY UPH.createdDate")
})
public class UserPasswordHistory {
  @Column(name = "created_date")
  private String createdDate;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "password_history_id", updatable = false, nullable = false)
  private String passwordHistoryId;

  @Column(name = "user_id")
  private String userId;

  @Column(name = "password")
  private String userPassword;

  public String getCreatedDate() {
    return createdDate;
  }

  public String getPasswordHistoryId() {
    return passwordHistoryId;
  }

  public String getUserId() {
    return userId;
  }

  public String getUserPassword() {
    return userPassword;
  }

  public void setCreatedDate(String createdDate) {
    this.createdDate = createdDate;
  }

  public void setPasswordHistoryId(String passwordHistoryId) {
    this.passwordHistoryId = passwordHistoryId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public void setUserPassword(String userPassword) {
    this.userPassword = userPassword;
  }
}
