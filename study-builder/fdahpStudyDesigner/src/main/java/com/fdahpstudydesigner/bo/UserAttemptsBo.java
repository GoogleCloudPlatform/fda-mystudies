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
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

@Entity
@Table(name = "user_attempts")
@NamedQueries({
  @NamedQuery(
      name = "getUserAttempts",
      query = "from UserAttemptsBo UABO Where UABO.userEmailId=:userEmailId"),
})
public class UserAttemptsBo implements Serializable {

  private static final long serialVersionUID = -3166967048106586712L;

  @Column(name = "attempts")
  private int attempts;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "last_modified")
  private String lastModified;

  @Column(name = "email_id")
  private String userEmailId;

  public int getAttempts() {
    return attempts;
  }

  public String getId() {
    return id;
  }

  public String getLastModified() {
    return lastModified;
  }

  public String getUserEmail() {
    return userEmailId;
  }

  public void setAttempts(int attempts) {
    this.attempts = attempts;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }

  public void setUserEmail(String userEmailId) {
    this.userEmailId = userEmailId;
  }
}
