/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "sites")
public class SiteBo implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "location_id", insertable = true, updatable = true)
  private LocationBo locations;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "study_id", insertable = true, updatable = true)
  private StudyInfoBO studyInfo;

  @Column(name = "status", columnDefinition = "TINYINT(1)")
  private Integer status = 1;

  @Column(name = "target_enrollment", columnDefinition = "INT(11) default 0")
  private Integer targetEnrollment;

  @Column(name = "name", columnDefinition = "VARCHAR(255)")
  private String name = "";

  @Column(name = "created", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Date created;

  @Column(name = "created_by", columnDefinition = "INT(20)")
  private Integer createdBy;

  @Column(
      name = "modified_date",
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
  private Date modifiedDate;

  @Column(name = "modified_by", columnDefinition = "INT(20)")
  private Integer modifiedBy;
}
