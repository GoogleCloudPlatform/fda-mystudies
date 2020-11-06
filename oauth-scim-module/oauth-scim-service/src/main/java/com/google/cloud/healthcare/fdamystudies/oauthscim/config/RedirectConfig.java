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
import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.MOBILE_APPS;
import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.PARTICIPANT_MANAGER;

import com.google.cloud.healthcare.fdamystudies.common.MobilePlatform;
import com.google.cloud.healthcare.fdamystudies.common.PlatformComponent;
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

  public String getCallbackUrl(String mobilePlatform, String source) {
    PlatformComponent platformComponent = PlatformComponent.fromValue(source);
    if (PARTICIPANT_MANAGER == platformComponent) {
      return participantManagerUrl + "/#/callback";
    } else if (MOBILE_APPS == platformComponent
        && MobilePlatform.fromValue(mobilePlatform) == ANDROID) {
      return androidDeeplinkUrl + "/callback";
    } else if (MOBILE_APPS == platformComponent
        && MobilePlatform.fromValue(mobilePlatform) == IOS) {
      return iosDeeplinkUrl + "/callback";
    }
    return null;
  }

  public String getForgotPasswordUrl(String mobilePlatform, String source) {
    PlatformComponent platformComponent = PlatformComponent.fromValue(source);
    if (PARTICIPANT_MANAGER == platformComponent) {
      return participantManagerUrl + "/#/forgotPassword";
    } else if (MOBILE_APPS == platformComponent
        && MobilePlatform.fromValue(mobilePlatform) == ANDROID) {
      return androidDeeplinkUrl + "/forgotPassword";
    } else if (MOBILE_APPS == platformComponent
        && MobilePlatform.fromValue(mobilePlatform) == IOS) {
      return iosDeeplinkUrl + "/forgotPassword";
    }
    return null;
  }

  public String getSignupUrl(String mobilePlatform) {
    if (MobilePlatform.fromValue(mobilePlatform) == ANDROID) {
      return androidDeeplinkUrl + "/signup";
    } else if (MobilePlatform.fromValue(mobilePlatform) == IOS) {
      return iosDeeplinkUrl + "/signup";
    }
    return null;
  }

  public String getAccountActivationUrl(String mobilePlatform, String source) {
    PlatformComponent platformComponent = PlatformComponent.fromValue(source);
    if (PARTICIPANT_MANAGER == platformComponent) {
      return participantManagerUrl + "/#/activation";
    } else if (MOBILE_APPS == platformComponent
        && MobilePlatform.fromValue(mobilePlatform) == ANDROID) {
      return androidDeeplinkUrl + "/activation";
    } else if (MOBILE_APPS == platformComponent
        && MobilePlatform.fromValue(mobilePlatform) == IOS) {
      return iosDeeplinkUrl + "/activation";
    }
    return null;
  }

  public String getTermsUrl(String mobilePlatform, String source) {
    PlatformComponent platformComponent = PlatformComponent.fromValue(source);
    if (PARTICIPANT_MANAGER == platformComponent) {
      return participantManagerUrl + "/#/terms";
    } else if (MOBILE_APPS == platformComponent
        && MobilePlatform.fromValue(mobilePlatform) == ANDROID) {
      return androidDeeplinkUrl + "/terms";
    } else if (MOBILE_APPS == platformComponent
        && MobilePlatform.fromValue(mobilePlatform) == IOS) {
      return iosDeeplinkUrl + "/terms";
    }
    return null;
  }

  public String getPrivacyPolicyUrl(String mobilePlatform) {
    if (MobilePlatform.fromValue(mobilePlatform) == ANDROID) {
      return androidDeeplinkUrl + "/privacyPolicy";
    } else if (MobilePlatform.fromValue(mobilePlatform) == IOS) {
      return iosDeeplinkUrl + "/privacyPolicy";
    }
    return null;
  }

  public String getAboutUrl(String source) {
    PlatformComponent platformComponent = PlatformComponent.fromValue(source);
    if (PARTICIPANT_MANAGER == platformComponent) {
      return participantManagerUrl + "/#/about";
    }
    return null;
  }
}
