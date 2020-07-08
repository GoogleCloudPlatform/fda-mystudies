/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.bean;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LocationBean {

  private Integer id;

  private String customId = "";

  private String name = "";

  private String description = "";

  private String status = "";

  private Integer studiesCount = 0;

  private List<String> studies = new LinkedList<>();

  private ErrorBean errorBean;

  private SuccessBean successBean;

  public LocationBean(String customId, String name, String description) {
    super();
    this.customId = customId;
    this.name = name;
    this.description = description;
  }

  public LocationBean(String customId, String status) {
    super();
    this.customId = customId;
    this.status = status;
  }
}
