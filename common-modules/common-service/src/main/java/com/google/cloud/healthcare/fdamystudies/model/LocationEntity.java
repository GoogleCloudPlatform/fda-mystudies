/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.model;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Setter
@Getter
@Entity
@Table(name = "locations")
@ConditionalOnProperty(
    value = "participant.manager.entities.enabled",
    havingValue = "true",
    matchIfMissing = false)
public class LocationEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "custom_id", length = 200)
  private String customId;

  @Column(name = "status", length = 1)
  private Integer status;

  @Column(name = "name", length = 200)
  private String name;

  @Column(name = "description", length = 500)
  private String description;

  @Column(name = "is_default", length = 1)
  private String isDefault;

  @Column(
      name = "created",
      insertable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp created;

  @ToString.Exclude
  @Column(name = "created_by", length = 64)
  private String createdBy;

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
