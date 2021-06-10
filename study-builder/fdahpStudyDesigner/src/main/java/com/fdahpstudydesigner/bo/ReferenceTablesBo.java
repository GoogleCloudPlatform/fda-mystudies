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

@Entity
@Table(name = "reference_tables")
public class ReferenceTablesBo implements Serializable {

  private static final long serialVersionUID = 5124001773679736751L;

  @Column(name = "category")
  private String category;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "type")
  private String type;

  @Column(name = "str_value")
  private String value;

  public String getCategory() {
    return category;
  }

  public String getId() {
    return id;
  }

  public String getType() {
    return type;
  }

  public String getValue() {
    return value;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
