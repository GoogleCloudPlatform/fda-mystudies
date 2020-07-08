/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.bean;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Scope("prototype")
@Getter
@Setter
public class SuccessBean {

  public static final String ADD_LOCATION_SUCCESS = "New location added Successfully";

  private String message;

  private int code;

  public SuccessBean() {
    this.code = 200;
  }

  public SuccessBean(String message) {
    this.code = 200;
    this.message = message;
  }
}
