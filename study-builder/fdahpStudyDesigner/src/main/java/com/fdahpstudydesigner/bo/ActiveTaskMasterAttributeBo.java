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
import org.hibernate.annotations.Type;

@Entity
@Table(name = "active_task_master_attribute")
public class ActiveTaskMasterAttributeBo implements Serializable {

  private static final long serialVersionUID = 1L;

  @Column(name = "add_to_dashboard")
  @Type(type = "yes_no")
  private boolean addToDashboard = false;

  @Column(name = "attribute_data_type")
  private String attributeDataType;

  @Column(name = "attribute_name")
  private String attributeName;

  @Column(name = "attribute_type")
  private String attributeType;

  @Column(name = "display_name")
  private String displayName;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "active_task_master_attr_id", updatable = false, nullable = false)
  private String masterId;

  @Column(name = "order_by")
  private Integer orderByTaskType;

  @Column(name = "task_type_id")
  private String taskTypeId;

  public String getAttributeDataType() {
    return attributeDataType;
  }

  public String getAttributeName() {
    return attributeName;
  }

  public String getAttributeType() {
    return attributeType;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getMasterId() {
    return masterId;
  }

  public Integer getOrderByTaskType() {
    return orderByTaskType;
  }

  public String getTaskTypeId() {
    return taskTypeId;
  }

  public boolean isAddToDashboard() {
    return addToDashboard;
  }

  public void setAddToDashboard(boolean addToDashboard) {
    this.addToDashboard = addToDashboard;
  }

  public void setAttributeDataType(String attributeDataType) {
    this.attributeDataType = attributeDataType;
  }

  public void setAttributeName(String attributeName) {
    this.attributeName = attributeName;
  }

  public void setAttributeType(String attributeType) {
    this.attributeType = attributeType;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public void setMasterId(String masterId) {
    this.masterId = masterId;
  }

  public void setOrderByTaskType(Integer orderByTaskType) {
    this.orderByTaskType = orderByTaskType;
  }

  public void setTaskTypeId(String taskTypeId) {
    this.taskTypeId = taskTypeId;
  }
}
