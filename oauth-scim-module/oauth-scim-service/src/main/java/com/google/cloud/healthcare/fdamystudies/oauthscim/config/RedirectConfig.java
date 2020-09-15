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

  @Value("${participant.manager.url}")
  private String participantManagerUrl;

  @Value("${ios.deeplink.url}")
  private String iosDeeplinkUrl;

  @Value("${android.deeplink.url}")
  private String androidDeeplinkUrl;

  public String getCallbackUrl(String mobilePlatform) {
    if (MobilePlatform.fromValue(mobilePlatform) == ANDROID) {
      return androidDeeplinkUrl + "/callback";
    } else if (MobilePlatform.fromValue(mobilePlatform) == IOS) {
      return iosDeeplinkUrl + "/callback";
    }
    return participantManagerUrl + "/#/callback";
  }

  public String getForgotPasswordUrl(String mobilePlatform) {
    if (MobilePlatform.fromValue(mobilePlatform) == ANDROID) {
      return androidDeeplinkUrl + "/forgotPassword";
    } else if (MobilePlatform.fromValue(mobilePlatform) == IOS) {
      return iosDeeplinkUrl + "/forgotPassword";
    }
    return participantManagerUrl + "/#/forgotPassword";
  }

  public String getSignupUrl(String mobilePlatform) {
    if (MobilePlatform.fromValue(mobilePlatform) == ANDROID) {
      return androidDeeplinkUrl + "/signup";
    } else if (MobilePlatform.fromValue(mobilePlatform) == IOS) {
      return iosDeeplinkUrl + "/signup";
    }
    return null;
  }

  public String getAccountActivationUrl(String mobilePlatform) {
    if (MobilePlatform.fromValue(mobilePlatform) == ANDROID) {
      return androidDeeplinkUrl + "/activation";
    } else if (MobilePlatform.fromValue(mobilePlatform) == IOS) {
      return iosDeeplinkUrl + "/activation";
    }
    return participantManagerUrl + "/#/activation";
  }

  public String getTermsUrl(String mobilePlatform) {
    if (MobilePlatform.fromValue(mobilePlatform) == ANDROID) {
      return androidDeeplinkUrl + "/terms";
    } else if (MobilePlatform.fromValue(mobilePlatform) == IOS) {
      return iosDeeplinkUrl + "/terms";
    }
    return participantManagerUrl + "/#/terms";
  }

  public String getPrivacyPolicyUrl(String mobilePlatform) {
    if (MobilePlatform.fromValue(mobilePlatform) == ANDROID) {
      return androidDeeplinkUrl + "/privacyPolicy";
    } else if (MobilePlatform.fromValue(mobilePlatform) == IOS) {
      return iosDeeplinkUrl + "/privacyPolicy";
    }
    return null;
  }

  public String getAboutUrl(String mobilePlatform) {
    if (MobilePlatform.fromValue(mobilePlatform) == ANDROID
        || MobilePlatform.fromValue(mobilePlatform) == IOS) {
      return null;
    }
    return participantManagerUrl + "/#/about";
  }
}
