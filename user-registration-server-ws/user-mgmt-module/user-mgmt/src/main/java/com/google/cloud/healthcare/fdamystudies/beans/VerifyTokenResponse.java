/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VerifyTokenResponse {

  private String code;
  private String message;

  private String accessToken;
  private boolean isVerified;
  private LocalDateTime expireDateTime;
}
