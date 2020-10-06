/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthRegistrationResponseBean {

  private String httpStatusCode;
  private String code;
  private String title;
  private String message;
  private String appCode;

  private String userId;
  private String accessToken;
  private String clientToken;
  private String refreshToken;
}
