/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.model;

import java.beans.Transient;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Entity
@Table(
    name = "study_info",
    uniqueConstraints = {
      @UniqueConstraint(
          columnNames = {"custom_id", "app_info_id"},
          name = "uk_study_info_study_app")
    })
@ConditionalOnProperty(
    value = "participant.manager.entities.enabled",
    havingValue = "true",
    matchIfMissing = false)
public class StudyEntity implements Serializable {

  private static final long serialVersionUID = 5392367043067145963L;

  @ToString.Exclude
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(
      name = "study_id",
      updatable = false,
      nullable = false,
      length = ColumnConstraints.ID_LENGTH)
  private String id;

  @Column(name = "custom_id", nullable = false, length = ColumnConstraints.XS_LENGTH)
  private String customId;

  @ManyToOne
  @JoinColumn(name = "app_info_id", insertable = true, updatable = true)
  private AppEntity app;

  @Column(name = "name", length = ColumnConstraints.SMALL_LENGTH)
  private String name;

  @Column(name = "description")
  @Type(type = "text")
  private String description;

  @Column(name = "type", length = ColumnConstraints.XS_LENGTH)
  private String type;

  @Column(
      name = "created_on",
      insertable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp created;

  @Column(name = "created_by", length = ColumnConstraints.LARGE_LENGTH)
  private String createdBy;

  @Column(
      name = "modified_date",
      insertable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp modified;

  @Column(name = "modified_by", length = ColumnConstraints.LARGE_LENGTH)
  private String modifiedBy;

  @Column(name = "version")
  private Float version;

  @Column(name = "status", length = ColumnConstraints.XS_LENGTH)
  private String status;

  @Column(name = "category", length = ColumnConstraints.SMALL_LENGTH)
  private String category;

  @Column(name = "tagline", length = ColumnConstraints.SMALL_LENGTH)
  private String tagline;

  @Column(name = "sponsor", length = ColumnConstraints.XS_LENGTH)
  private String sponsor;

  @Column(name = "enrolling", length = ColumnConstraints.TINY_LENGTH)
  private String enrolling;

  @OneToMany(
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      mappedBy = "study",
      orphanRemoval = true)
  private List<StudyPermissionEntity> studyPermissions = new ArrayList<>();

  public void addStudyPermissionEntity(StudyPermissionEntity studyPermission) {
    studyPermissions.add(studyPermission);
    studyPermission.setStudy(this);
  }

  @OneToMany(
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      mappedBy = "study",
      orphanRemoval = true)
  private List<SiteEntity> sites = new ArrayList<>();

  public void addSiteEntity(SiteEntity site) {
    sites.add(site);
    site.setStudy(this);
  }

  @OneToMany(
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      mappedBy = "study",
      orphanRemoval = true)
  private List<SitePermissionEntity> sitePermissions = new ArrayList<>();

  public void addSitePermissionEntity(SitePermissionEntity sitePermission) {
    sitePermissions.add(sitePermission);
    sitePermission.setStudy(this);
  }

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "study")
  private List<ParticipantRegistrySiteEntity> participantRegistrySites = new ArrayList<>();

  public void addParticipantRegistrySiteEntity(
      ParticipantRegistrySiteEntity participantRegistrySite) {
    participantRegistrySites.add(participantRegistrySite);
    participantRegistrySite.setStudy(this);
  }

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "study")
  private List<ParticipantStudyEntity> participantStudies = new ArrayList<>();

  public void addParticipantStudiesEntity(ParticipantStudyEntity participantStudiesEntity) {
    participantStudies.add(participantStudiesEntity);
    participantStudiesEntity.setStudy(this);
  }

  @Transient
  public String getAppId() {
    return app != null ? app.getId() : StringUtils.EMPTY;
  }
}
