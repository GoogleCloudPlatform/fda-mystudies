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
@Table(name = "statistic_master_images")
public class StatisticImageListBo implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "statistic_image_id", updatable = false, nullable = false)
  private String statisticImageId;

  @Column(name = "value")
  private String value;

  public String getStatisticImageId() {
    return statisticImageId;
  }

  public String getValue() {
    return value;
  }

  public void setStatisticImageId(String statisticImageId) {
    this.statisticImageId = statisticImageId;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
