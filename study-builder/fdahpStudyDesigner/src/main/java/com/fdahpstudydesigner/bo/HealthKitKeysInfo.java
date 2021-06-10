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
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

@Entity
@Table(name = "health_kit_keys_info")
@NamedQueries({
  @NamedQuery(name = "getHealthKitKeyInfo", query = "from HealthKitKeysInfo HKIBO"),
})
public class HealthKitKeysInfo implements Serializable {

  private static final long serialVersionUID = -9161839022108816141L;

  @Column(name = "category")
  private String category;

  @Column(name = "display_name")
  private String displayName;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "key_text")
  private String key;

  @Column(name = "result_type")
  private String resultType;

  public String getCategory() {
    return category;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getId() {
    return id;
  }

  public String getKey() {
    return key;
  }

  public String getResultType() {
    return resultType;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public void setResultType(String resultType) {
    this.resultType = resultType;
  }
}
