/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.config;

import static com.google.cloud.healthcare.fdamystudies.common.MobilePlatform.ANDROID;
import static com.google.cloud.healthcare.fdamystudies.common.MobilePlatform.IOS;

import com.google.cloud.healthcare.fdamystudies.common.MobilePlatform;
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

  @Value("${mystudies.ios.app.account-activation-url}")
  private String myStudiesIosAppAccountActivationUrl;

  @Value("${mystudies.android.app.account-activation-url}")
  private String myStudiesAndroidAppAccountActivationUrl;

  @Value("${participant.manager.account-activation-url}")
  private String participantManagerAccountActivationUrl;

  @Value("${mystudies.ios.app.terms-url}")
  private String myStudiesIosAppTermsUrl;

  @Value("${mystudies.android.app.terms-url}")
  private String myStudiesAndroidAppTermsUrl;

  @Value("${participant.manager.terms-url}")
  private String participantManagerTermsUrl;

  @Value("${mystudies.ios.app.privacy-policy-url}")
  private String myStudiesIosAppPrivacyPolicyUrl;

  @Value("${mystudies.android.app.privacy-policy-url}")
  private String myStudiesAndroidAppPrivacyPolicyUrl;

  @Value("${participant.manager.about-url}")
  private String participantManagerAboutUrl;

  public String getCallbackUrl(String mobilePlatform) {
    if (MobilePlatform.fromValue(mobilePlatform) == ANDROID) {
      return myStudiesAndroidAppCallbackUrl;
    } else if (MobilePlatform.fromValue(mobilePlatform) == IOS) {
      return myStudiesIosAppCallbackUrl;
    }
    return participantManagerCallbackUrl;
  }

  public String getForgotPasswordUrl(String mobilePlatform) {
    if (MobilePlatform.fromValue(mobilePlatform) == ANDROID) {
      return myStudiesAndroidAppForgotPasswordUrl;
    } else if (MobilePlatform.fromValue(mobilePlatform) == IOS) {
      return myStudiesIosAppForgotPasswordUrl;
    }
    return participantManagerForgotPasswordUrl;
  }

  public String getSignupUrl(String mobilePlatform) {
    if (MobilePlatform.fromValue(mobilePlatform) == ANDROID) {
      return myStudiesAndroidAppSignupUrl;
    } else if (MobilePlatform.fromValue(mobilePlatform) == IOS) {
      return myStudiesIosAppSignupUrl;
    }
    return null;
  }

  public String getAccountActivationUrl(String mobilePlatform) {
    if (MobilePlatform.fromValue(mobilePlatform) == ANDROID) {
      return myStudiesAndroidAppAccountActivationUrl;
    } else if (MobilePlatform.fromValue(mobilePlatform) == IOS) {
      return myStudiesIosAppAccountActivationUrl;
    }
    return participantManagerAccountActivationUrl;
  }

  public String getTermsUrl(String mobilePlatform) {
    if (MobilePlatform.fromValue(mobilePlatform) == ANDROID) {
      return myStudiesAndroidAppTermsUrl;
    } else if (MobilePlatform.fromValue(mobilePlatform) == IOS) {
      return myStudiesIosAppTermsUrl;
    }
    return participantManagerTermsUrl;
  }

  public String getPrivacyPolicyUrl(String mobilePlatform) {
    if (MobilePlatform.fromValue(mobilePlatform) == ANDROID) {
      return myStudiesAndroidAppPrivacyPolicyUrl;
    } else if (MobilePlatform.fromValue(mobilePlatform) == IOS) {
      return myStudiesIosAppPrivacyPolicyUrl;
    }
    return null;
  }

  public String getAboutUrl(String mobilePlatform) {
    if (MobilePlatform.fromValue(mobilePlatform) == ANDROID
        || MobilePlatform.fromValue(mobilePlatform) == IOS) {
      return null;
    }
    return participantManagerAboutUrl;
  }
}
