/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import org.springframework.stereotype.Component;
import lombok.ToString;

@Component
@ToString
@Entity
@Table(name = "study_info")
public class StudyInfoBO {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
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

  @Column(name = "created", columnDefinition = "TIMESTAMP")
  private Date created;

  @Column(name = "created_by", columnDefinition = "INT(20) default 0")
  private Integer createdBy;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getCustomId() {
    return customId;
  }

  public void setCustomId(String customId) {
    this.customId = customId;
  }

  public AppInfoDetailsBO getAppInfo() {
    return appInfo;
  }

  public void setAppInfo(AppInfoDetailsBO appInfo) {
    this.appInfo = appInfo;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Integer getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(Integer createdBy) {
    this.createdBy = createdBy;
  }

  //  @OneToMany
  //  @JoinColumn(name = "study_info_id")
  //  private Set<SiteBo> siteBo;
  //
  //  @OneToMany
  //  @JoinColumn(name = "study_info_id")
  //  private Set<ParticipantStudiesBO> participantStudies;
}
