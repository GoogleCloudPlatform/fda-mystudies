package com.google.cloud.healthcare.fdamystudies.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Cascade;

@Entity
@Table(name = "sites")
public class SiteBo implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "location_id", insertable = false, updatable = false)
  private LocationBo locations;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "study_id", insertable = false, updatable = false)
  private StudyInfoBO studyInfo;

  @Column(name = "status", columnDefinition = "TINYINT(1)")
  private Integer status = 1;

  @Column(name = "target_enrollment", columnDefinition = "INT(11) default 0")
  private Integer targetEnrollment;

  @Column(name = "name", columnDefinition = "VARCHAR(255)")
  private String name = "";

  @Column(name = "created", columnDefinition = "TIMESTAMP")
  private LocalDateTime created;

  @Column(name = "created_by", columnDefinition = "INT(20) default 0")
  private Integer createdBy;

  @OneToOne(mappedBy = "siteBo")
  @Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
  private SitePermission sitePermission;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public LocationBo getLocations() {
    return locations;
  }

  public void setLocations(LocationBo locations) {
    this.locations = locations;
  }

  public StudyInfoBO getStudyInfo() {
    return studyInfo;
  }

  public void setStudyInfo(StudyInfoBO studyInfo) {
    this.studyInfo = studyInfo;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public Integer getTargetEnrollment() {
    return targetEnrollment;
  }

  public void setTargetEnrollment(Integer targetEnrollment) {
    this.targetEnrollment = targetEnrollment;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public LocalDateTime getCreated() {
    return created;
  }

  public void setCreated(LocalDateTime created) {
    this.created = created;
  }

  public Integer getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(Integer createdBy) {
    this.createdBy = createdBy;
  }

  public SitePermission getSitePermission() {
    return sitePermission;
  }

  public void setSitePermission(SitePermission sitePermission) {
    this.sitePermission = sitePermission;
  }
}
