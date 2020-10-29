/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.model;

import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.LARGE_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.SMALL_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.YES;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

@Setter
@Getter
@Entity
@Table(
    name = "locations",
    indexes = {@Index(name = "locations_status_idx", columnList = "status")})
public class LocationEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "custom_id", nullable = false, length = SMALL_LENGTH)
  private String customId;

  @Column(name = "status")
  private Integer status;

  @Column(name = "name", length = LARGE_LENGTH, unique = true)
  private String name;

  @Column(name = "description")
  @Type(type = "text")
  private String description;

  @Column(name = "is_default", nullable = false, columnDefinition = "Varchar(1) default 'N'")
  private String isDefault;

  @Column(name = "created_time")
  @CreationTimestamp
  private Timestamp created;

  @ToString.Exclude
  @Column(name = "created_by", length = LARGE_LENGTH)
  private String createdBy;

  @ToString.Exclude
  @Column(name = "modified_by", length = LARGE_LENGTH)
  private String modifiedBy;

  @Column(name = "updated_time")
  @UpdateTimestamp
  private Timestamp modified;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "location")
  private List<SiteEntity> sites = new ArrayList<>();

  public void addSiteEntity(SiteEntity site) {
    sites.add(site);
    site.setLocation(this);
  }

  @Transient
  public boolean isDefault() {
    return YES.equalsIgnoreCase(isDefault);
  }
}
