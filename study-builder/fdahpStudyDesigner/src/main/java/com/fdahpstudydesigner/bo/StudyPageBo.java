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
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "study_page")
public class StudyPageBo implements Serializable {

  private static final long serialVersionUID = 3736160119532905474L;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "created_on")
  private String createdOn;

  @Column(name = "description")
  private String description;

  @Column(name = "image_path")
  private String imagePath;

  @Column(name = "modified_by")
  private String modifiedBy;

  @Column(name = "modified_on")
  private String modifiedOn;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "page_id", updatable = false, nullable = false)
  private String pageId;

  @Column(name = "study_id")
  private String studyId;

  @Column(name = "title")
  private String title;

  @Transient public String signedUrl;

  public String getCreatedBy() {
    return createdBy;
  }

  public String getCreatedOn() {
    return createdOn;
  }

  public String getDescription() {
    return description;
  }

  public String getImagePath() {
    return imagePath;
  }

  public String getModifiedBy() {
    return modifiedBy;
  }

  public String getModifiedOn() {
    return modifiedOn;
  }

  public String getPageId() {
    return pageId;
  }

  public String getStudyId() {
    return studyId;
  }

  public String getTitle() {
    return title;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public void setCreatedOn(String createdOn) {
    this.createdOn = createdOn;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setModifiedOn(String modifiedOn) {
    this.modifiedOn = modifiedOn;
  }

  public void setPageId(String pageId) {
    this.pageId = pageId;
  }

  public void setStudyId(String studyId) {
    this.studyId = studyId;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getSignedUrl() {
    return signedUrl;
  }

  public void setSignedUrl(String signedUrl) {
    this.signedUrl = signedUrl;
  }
}
