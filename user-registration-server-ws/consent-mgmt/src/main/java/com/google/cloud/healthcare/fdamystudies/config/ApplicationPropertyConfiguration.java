/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Configuration
@ToString
@Getter
@Setter
public class ApplicationPropertyConfiguration {

  @Value("${authServerAccessTokenValidationUrl}")
  private String authServerAccessTokenValidationUrl;

  @Value("${clientId}")
  private String clientId;

  @Value("${secretKey}")
  private String secretKey;

  @Value("${interceptor}")
  private String interceptorUrls;

  @Value("${bucketName}")
  private String bucketName;

  @Value("${applicationComponentName}")
  private String applicationComponentName;

  @Value("${applicationVersion}")
  private String applicationVersion;
}
