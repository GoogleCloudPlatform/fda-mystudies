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

  @Value("${participant.manager.forgot-password-url}")
  private String participantManagerForgotPasswordUrl;

  @Value("${participant.manager.signup-url}")
  private String participantManagerSignupUrl;

  @Value("${mystudies.ios.app.callback-url}")
  private String myStudiesIosAppCallbackUrl;

  @Value("${mystudies.ios.app.forgot-password-url}")
  private String myStudiesIosAppForgotPasswordUrl;

  @Value("${mystudies.ios.app.signup-url}")
  private String myStudiesIosAppSignupUrl;

  @Value("${mystudies.android.app.callback-url}")
  private String myStudiesAndroidAppCallbackUrl;

  @Value("${mystudies.android.app.forgot-password-url}")
  private String myStudiesAndroidAppForgotPasswordUrl;

  @Value("${mystudies.android.app.signup-url}")
  private String myStudiesAndroidAppSignupUrl;

  public String getCallbackUrl(String devicePlatform) {
    if (ANDROID.getValue().equalsIgnoreCase(devicePlatform)) {
      return myStudiesAndroidAppCallbackUrl;
    } else if (IOS.getValue().equalsIgnoreCase(devicePlatform)) {
      return myStudiesIosAppCallbackUrl;
    }
    return participantManagerCallbackUrl;
  }

  public String getForgotPasswordUrl(String devicePlatform) {
    if (ANDROID.getValue().equalsIgnoreCase(devicePlatform)) {
      return myStudiesAndroidAppForgotPasswordUrl;
    } else if (IOS.getValue().equalsIgnoreCase(devicePlatform)) {
      return myStudiesIosAppForgotPasswordUrl;
    }
    return participantManagerForgotPasswordUrl;
  }

  public String getSignupUrl(String devicePlatform) {
    if (ANDROID.getValue().equalsIgnoreCase(devicePlatform)) {
      return myStudiesAndroidAppSignupUrl;
    } else if (IOS.getValue().equalsIgnoreCase(devicePlatform)) {
      return myStudiesIosAppSignupUrl;
    }
    return participantManagerSignupUrl;
  }
}
