/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
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
