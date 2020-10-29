/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.bean;

import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DestinationBean {

  private String condition = AppConstants.EMPTY_STR;
  private String operator = AppConstants.EMPTY_STR;
  private String destination = AppConstants.EMPTY_STR;

  @Override
  public String toString() {
    return "DestinationBean [condition="
        + condition
        + ", operator="
        + operator
        + ", destination="
        + destination
        + "]";
  }
}
