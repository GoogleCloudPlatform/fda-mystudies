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

  @Value("${participant.manager.forgot-password-url}")
  private String participantManagerForgotPasswordUrl;

  @Value("${mystudies.ios.app.forgot-password-url}")
  private String myStudiesIosAppForgotPasswordUrl;

  @Value("${mystudies.android.app.forgot-password-url}")
  private String myStudiesAndroidAppForgotPasswordUrl;

  @Value("${mystudies.ios.app.signup-url}")
  private String myStudiesIosAppSignupUrl;

  @Value("${mystudies.android.app.signup-url}")
  private String myStudiesAndroidAppSignupUrl;

  @Value("${participant.manager.signup-url}")
  private String participantManagerSignupUrl;

  @Value("${mystudies.ios.app.error-url}")
  private String myStudiesIosAppErrorUrl;

  @Value("${mystudies.android.app.error-url}")
  private String myStudiesAndroidAppErrorUrl;

  @Value("${participant.manager.error-url}")
  private String participantManagerErrorUrl;

  @Value("${mystudies.ios.app.account-activation-url}")
  private String myStudiesIosAppAccountActivationUrl;

  @Value("${mystudies.android.app.account-activation-url}")
  private String myStudiesAndroidAppAccountActivationUrl;

  @Value("${participant.manager.account-activation-url}")
  private String participantManagerAccountActivationUrl;

  public String getCallbackUrl(String devicePlatform) {
    if (DevicePlatform.fromString(devicePlatform) == ANDROID) {
      return myStudiesAndroidAppCallbackUrl;
    } else if (DevicePlatform.fromString(devicePlatform) == IOS) {
      return myStudiesIosAppCallbackUrl;
    }
    return participantManagerCallbackUrl;
  }

  public String getForgotPasswordUrl(String devicePlatform) {
    if (DevicePlatform.fromString(devicePlatform) == ANDROID) {
      return myStudiesAndroidAppForgotPasswordUrl;
    } else if (DevicePlatform.fromString(devicePlatform) == IOS) {
      return myStudiesIosAppForgotPasswordUrl;
    }
    return participantManagerForgotPasswordUrl;
  }

  public String getSignupUrl(String devicePlatform) {
    if (DevicePlatform.fromString(devicePlatform) == ANDROID) {
      return myStudiesAndroidAppSignupUrl;
    } else if (DevicePlatform.fromString(devicePlatform) == IOS) {
      return myStudiesIosAppSignupUrl;
    }
    return participantManagerSignupUrl;
  }

  public String getErrorUrl(String devicePlatform) {
    if (DevicePlatform.fromString(devicePlatform) == ANDROID) {
      return myStudiesAndroidAppErrorUrl;
    } else if (DevicePlatform.fromString(devicePlatform) == IOS) {
      return myStudiesIosAppErrorUrl;
    }
    return participantManagerErrorUrl;
  }

  public String getAccountActivationUrl(String devicePlatform) {
    if (DevicePlatform.fromString(devicePlatform) == ANDROID) {
      return myStudiesAndroidAppAccountActivationUrl;
    } else if (DevicePlatform.fromString(devicePlatform) == IOS) {
      return myStudiesIosAppAccountActivationUrl;
    }
    return participantManagerAccountActivationUrl;
  }

  public String getDefaultErrorUrl() {
    return getErrorUrl(null);
  }
}
