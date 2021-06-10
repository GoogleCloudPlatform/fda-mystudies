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
@Table(name = "roles")
@NamedQueries({
  @NamedQuery(
      name = "getUserRoleByRoleId",
      query = "SELECT RBO FROM RoleBO RBO WHERE RBO.roleId =:roleId"),
})
public class RoleBO implements Serializable {

  private static final long serialVersionUID = -7663912527828944778L;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "role_id", updatable = false, nullable = false)
  private String roleId;

  @Column(name = "role_name")
  private String roleName;

  public String getRoleId() {
    return roleId;
  }

  public String getRoleName() {
    return roleName;
  }

  public void setRoleId(String roleId) {
    this.roleId = roleId;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }
}
