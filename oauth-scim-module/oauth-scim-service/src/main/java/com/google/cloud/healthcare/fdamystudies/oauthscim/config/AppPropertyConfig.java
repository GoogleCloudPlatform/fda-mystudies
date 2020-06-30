/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.config;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Setter
@Getter
public class AppPropertyConfig implements Serializable {

  private static final long serialVersionUID = 2189883675260389666L;

  @Value("${oauth.scim.service.password.expiry.days:90}")
  private int passwordExpiryDays;

  @Value("${oauth.scim.service.password.history.max.size:10}")
  private int passwordHistoryMaxSize;
}
