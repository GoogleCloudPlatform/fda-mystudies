/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.model;

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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Entity
@Table(name = "study_info")
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
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "custom_id", length = 100)
  private String customId;

  @ManyToOne
  @JoinColumn(name = "app_info_id", insertable = true, updatable = true)
  private AppEntity appInfo;

  @Column(name = "name", length = 100)
  private String name;

  @Column(name = "description", length = 100)
  @Type(type = "text")
  private String description;

  @Column(name = "type", length = 100)
  private String type;

  @Column(
      name = "created",
      insertable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp created;

  @Column(name = "created_by", length = 64)
  private String createdBy;

  @Column(
      name = "modified",
      insertable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp modified;

  @Column(name = "modified_by", length = 64)
  private String modifiedBy;

  @Column(name = "version", length = 100)
  private Float version;

  @Column(name = "status", length = 100)
  private String status;

  @Column(name = "category", length = 100)
  private String category;

  @Column(name = "tagline", length = 100)
  private String tagline;

  @Column(name = "sponsor", length = 100)
  private String sponsor;

  @Column(name = "enrolling", length = 100)
  private String enrolling;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "study")
  private List<StudyPermissionEntity> studyPermissions = new ArrayList<>();

  public void addStudyPermissionEntity(StudyPermissionEntity studyPermission) {
    studyPermissions.add(studyPermission);
    studyPermission.setStudy(this);
  }

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "study")
  private List<SiteEntity> sites = new ArrayList<>();

  public void addSiteEntity(SiteEntity site) {
    sites.add(site);
    site.setStudy(this);
  }

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "study")
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
}
