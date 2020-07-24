/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
@Component
@Scope(value = "prototype")
public class AppSiteResponse {

  private String siteId;

  private String locationId;

  private String customLocationId;

  private String locationName;

  private Boolean selected = false;

  private Boolean disabled = false;

  private String permission;

  private String locationDescription;
}
