/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.model;

import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.LARGE_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.MEDIUM_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.SMALL_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.TINY_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.XS_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.EMAIL_LENGTH;

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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "study_info",
    uniqueConstraints = {
      @UniqueConstraint(
          columnNames = {"custom_id", "app_info_id"},
          name = "study_info_custom_id_app_info_id_uidx")
    },
    indexes = {
      @Index(name = "study_info_name_idx", columnList = "name"),
    })
public class StudyEntity implements Serializable {

  private static final long serialVersionUID = 5392367043067145963L;

  @ToString.Exclude
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id")
  private String id;

  @Column(name = "custom_id", nullable = false, length = XS_LENGTH)
  private String customId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "app_info_id")
  private AppEntity app;

  @Column(length = SMALL_LENGTH)
  private String name;

  @Type(type = "text")
  private String description;

  @Column(length = XS_LENGTH)
  private String type;

  @Column(name = "created_time")
  @CreationTimestamp
  private Timestamp created;

  @Column(name = "created_by", length = LARGE_LENGTH)
  private String createdBy;

  @Column(name = "updated_time")
  @UpdateTimestamp
  private Timestamp modified;

  @Column(name = "modified_by", length = LARGE_LENGTH)
  private String modifiedBy;

  @Column(name = "version")
  private Float version;

  @Column(length = XS_LENGTH)
  private String status;

  @Column(length = SMALL_LENGTH)
  private String category;

  @Column(length = MEDIUM_LENGTH)
  private String tagline;

  @Column(length = MEDIUM_LENGTH)
  private String sponsor;

  @Column(length = TINY_LENGTH)
  private String enrolling;

  @Column(name = "logo_image_url", length = LARGE_LENGTH)
  private String logoImageUrl;

  @ToString.Exclude
  @Column(name = "contact_email", length = EMAIL_LENGTH)
  private String contactEmail;

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

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(id).toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof StudyEntity) {
      final StudyEntity study = (StudyEntity) obj;
      return new EqualsBuilder().append(id, study.id).isEquals();
    } else {
      return false;
    }
  }
}
