/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller.bean;

import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Component
public class AuthInfoBean {

  private String accessToken;
  private String userId;
}
