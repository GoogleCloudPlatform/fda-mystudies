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
package com.hphc.mystudies.dto;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "active_task_master_attribute")
@NamedQueries(
    value = {
      @NamedQuery(
          name = "getActiveTaskMasterListFromIds",
          query =
              "from ActiveTaskMasterAttributeDto ATMADTO"
                  + " where ATMADTO.masterId IN (:taskMasterAttrIdList)"),
    })
public class ActiveTaskMasterAttributeDto implements Serializable {

  private static final long serialVersionUID = 3945410061495684065L;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "active_task_master_attr_id", updatable = false, nullable = false)
  private String masterId;

  @Column(name = "task_type_id")
  private String taskTypeId;

  @Column(name = "order_by")
  private Integer orderByTaskType;

  @Column(name = "attribute_type")
  private String attributeType;

  @Column(name = "attribute_name")
  private String attributeName;

  @Column(name = "display_name")
  private String displayName;

  @Column(name = "attribute_data_type")
  private String attributeDataType;

  @Column(name = "add_to_dashboard")
  @Type(type = "yes_no")
  private boolean addToDashboard = false;

  @Column(name = "study_version")
  private Integer studyVersion = 1;

  public String getMasterId() {
    return masterId;
  }

  public void setMasterId(String masterId) {
    this.masterId = masterId;
  }

  public String getTaskTypeId() {
    return taskTypeId;
  }

  public void setTaskTypeId(String taskTypeId) {
    this.taskTypeId = taskTypeId;
  }

  public Integer getOrderByTaskType() {
    return orderByTaskType;
  }

  public void setOrderByTaskType(Integer orderByTaskType) {
    this.orderByTaskType = orderByTaskType;
  }

  public String getAttributeType() {
    return attributeType;
  }

  public void setAttributeType(String attributeType) {
    this.attributeType = attributeType;
  }

  public String getAttributeName() {
    return attributeName;
  }

  public void setAttributeName(String attributeName) {
    this.attributeName = attributeName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getAttributeDataType() {
    return attributeDataType;
  }

  public void setAttributeDataType(String attributeDataType) {
    this.attributeDataType = attributeDataType;
  }

  public boolean isAddToDashboard() {
    return addToDashboard;
  }

  public void setAddToDashboard(boolean addToDashboard) {
    this.addToDashboard = addToDashboard;
  }

  public Integer getStudyVersion() {
    return studyVersion;
  }

  public void setStudyVersion(Integer studyVersion) {
    this.studyVersion = studyVersion;
  }
}
