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
import org.hibernate.annotations.Type;

@Setter
@Getter
@Entity
@Table(name = "study_info")
public class StudyInfoBO implements Serializable {

  private static final long serialVersionUID = 5392367043067145963L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @Column(name = "custom_id", columnDefinition = "VARCHAR(255)")
  private String customId;

  @ManyToOne
  @JoinColumn(name = "app_info_id", insertable = true, updatable = true)
  private AppInfoDetailsBO appInfo;

  @Column(name = "name", columnDefinition = "VARCHAR(255)")
  private String name;

  @Column(name = "description")
  @Type(type = "text")
  private String description;

  @Column(name = "type")
  private String type;

  @Column(name = "created_on", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Date createdOn;

  @Column(name = "created_by", columnDefinition = "INT(20)")
  private Integer createdBy;

  @Column(
      name = "modified_date",
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
  private Date modifiedDate;

  @Column(name = "modified_by", columnDefinition = "INT(20)")
  private Integer modifiedBy;

  @Column(name = "version")
  private Float version;

  @Column(name = "status")
  private String status;

  @Column(name = "category")
  private String category;

  @Column(name = "tagline")
  private String tagline;

  @Column(name = "sponsor")
  private String sponsor;

  @Column(name = "enrolling")
  private String enrolling;
}
