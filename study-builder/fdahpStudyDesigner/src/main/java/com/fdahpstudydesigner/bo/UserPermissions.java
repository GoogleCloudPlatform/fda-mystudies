/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.bo;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "user_permissions")
public class UserPermissions {

  private String permissions;

  private Integer userRoleId;

  private Set<UserBO> users;

  public UserPermissions() {}

  public UserPermissions(Set<UserBO> users, String permissions) {
    this.setUsers(users);
    this.setPermissions(permissions);
  }

  @Column(name = "permissions", nullable = false, length = 45)
  public String getPermissions() {
    return permissions;
  }

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "permission_id", updatable = false, nullable = false)
  public Integer getUserRoleId() {
    return this.userRoleId;
  }

  @ManyToMany(fetch = FetchType.EAGER)
  public Set<UserBO> getUsers() {
    return users;
  }

  public void setPermissions(String permissions) {
    this.permissions = permissions;
  }

  public void setUserRoleId(Integer userRoleId) {
    this.userRoleId = userRoleId;
  }

  public void setUsers(Set<UserBO> users) {
    this.users = users;
  }
}
