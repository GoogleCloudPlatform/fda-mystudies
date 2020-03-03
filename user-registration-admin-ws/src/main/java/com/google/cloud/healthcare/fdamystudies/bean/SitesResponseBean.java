/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.bean;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Component
@Scope(value = "prototype")
public class SitesResponseBean {

  private Integer siteId;
  private Integer locationId;
  private String customLocationId;
  private String locationName;
  private Boolean selected = false;
  private Boolean disabled = false;
  private Integer permission = 0;
  private String locationDescription;
}
