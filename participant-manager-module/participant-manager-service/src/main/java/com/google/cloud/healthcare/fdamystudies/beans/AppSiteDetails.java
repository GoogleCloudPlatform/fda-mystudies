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

@Setter
@Getter
@ToString
@Component
@Scope(value = "prototype")
public class AppSiteDetails {

  private String siteId;

  private String customSiteId;

  private String siteName;

  private String enrollmentDate;

  private String withdrawlDate;

  private String siteStatus;
}
