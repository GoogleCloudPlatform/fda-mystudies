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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "active_task_list")
@NamedQueries({
  @NamedQuery(name = "ActiveTaskListBo.findAll", query = "SELECT ATLB FROM ActiveTaskListBo ATLB"),
})
public class ActiveTaskListBo implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "active_task_list_id", updatable = false, nullable = false)
  private String activeTaskListId;

  @Column(name = "task_name")
  private String taskName;

  @Column(name = "type")
  private String type;

  public String getActiveTaskListId() {
    return activeTaskListId;
  }

  public String getTaskName() {
    return taskName;
  }

  public String getType() {
    return type;
  }

  public void setActiveTaskListId(String activeTaskListId) {
    this.activeTaskListId = activeTaskListId;
  }

  public void setTaskName(String taskName) {
    this.taskName = taskName;
  }

  public void setType(String type) {
    this.type = type;
  }
}
