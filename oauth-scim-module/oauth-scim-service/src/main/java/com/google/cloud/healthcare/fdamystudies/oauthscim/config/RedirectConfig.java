/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.config;

import static com.google.cloud.healthcare.fdamystudies.common.DevicePlatform.ANDROID;
import static com.google.cloud.healthcare.fdamystudies.common.DevicePlatform.IOS;

import com.google.cloud.healthcare.fdamystudies.common.DevicePlatform;
import java.io.Serializable;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RedirectConfig implements Serializable {

  private static final long serialVersionUID = 2189883675260389666L;

  @Value("${participant.manager.callback-url}")
  private String participantManagerCallbackUrl;

  @Value("${mystudies.ios.app.callback-url}")
  private String myStudiesIosAppCallbackUrl;

  @Value("${mystudies.android.app.callback-url}")
  private String myStudiesAndroidAppCallbackUrl;

  public String getCallbackUrl(String devicePlatform) {
    if (DevicePlatform.fromString(devicePlatform) == ANDROID) {
      return myStudiesAndroidAppCallbackUrl;
    } else if (DevicePlatform.fromString(devicePlatform) == IOS) {
      return myStudiesIosAppCallbackUrl;
    }
    return participantManagerCallbackUrl;
  }
}
