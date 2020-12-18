/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UserSiteDetails {

  private String siteId;

  private String locationId;

  private String customLocationId;

  private String locationName;

  private boolean selected;

  private Integer permission;

  private String locationDescription;
}
