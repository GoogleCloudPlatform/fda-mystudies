/*
 * Copyright 2021 Google LLC
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Setter
@Getter
@Entity
@Table(name = "app_permission")
@NamedQueries({
  @NamedQuery(
      name = "getAppPermission",
      query = " From AppPermissionBO WHERE appId =:appId and userId =:userId")
})
public class AppPermissionBO implements Serializable {

  private static final long serialVersionUID = 1L;

  @Column(name = "app_id")
  private String appId;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String appPermissionId;

  @Transient private String userFullName;

  @Column(name = "user_id")
  private String userId;

  @Column(name = "view_permission", length = 1)
  private boolean viewPermission;
}
