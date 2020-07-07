/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service.bean;

import com.google.cloud.healthcare.fdamystudies.model.DaoUserBO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ServiceRegistrationSuccessResponse {

  private DaoUserBO daoUser;
  private String accessToken;
  private String refreshToken;
  private String clientToken;
}
