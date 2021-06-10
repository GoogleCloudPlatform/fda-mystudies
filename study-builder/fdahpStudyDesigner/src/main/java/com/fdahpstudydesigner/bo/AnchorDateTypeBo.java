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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "anchordate_type")
@NamedQueries({
  @NamedQuery(
      name = "getAnchorDateType",
      query = "SELECT ADB FROM AnchorDateTypeBo ADB WHERE ADB.studyId =:studyId"),
})
public class AnchorDateTypeBo implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "custom_study_id")
  private String customStudyId;

  @Column(name = "study_id")
  private String studyId;

  @Column(name = "name")
  private String name;

  // 0-not used and 1- used
  @Column(name = "has_anchortype_draft")
  private Integer hasAnchortypeDraft = 0;

  @Column(name = "version")
  private Float version = 0f;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCustomStudyId() {
    return customStudyId;
  }

  public void setCustomStudyId(String customStudyId) {
    this.customStudyId = customStudyId;
  }

  public String getStudyId() {
    return studyId;
  }

  public void setStudyId(String studyId) {
    this.studyId = studyId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getHasAnchortypeDraft() {
    return hasAnchortypeDraft;
  }

  public void setHasAnchortypeDraft(Integer hasAnchortypeDraft) {
    this.hasAnchortypeDraft = hasAnchortypeDraft;
  }

  public Float getVersion() {
    return version;
  }

  public void setVersion(Float version) {
    this.version = version;
  }
}
