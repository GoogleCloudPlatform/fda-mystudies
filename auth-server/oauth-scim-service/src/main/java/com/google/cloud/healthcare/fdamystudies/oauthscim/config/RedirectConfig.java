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
import io.micrometer.core.instrument.util.StringUtils;
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

  public String getCallbackUrl(String mobilePlatform, String source, String deeplinkCookie) {
    PlatformComponent platformComponent = PlatformComponent.fromValue(source);
    if (PARTICIPANT_MANAGER == platformComponent) {
      return participantManagerUrl + "/#/callback";
    } else if (MOBILE_APPS == platformComponent
        && MobilePlatform.fromValue(mobilePlatform) == ANDROID) {
      return StringUtils.isNotBlank(deeplinkCookie)
          ? deeplinkCookie + "/callback"
          : androidDeeplinkUrl + "/callback";
    } else if (MOBILE_APPS == platformComponent
        && MobilePlatform.fromValue(mobilePlatform) == IOS) {
      return StringUtils.isNotBlank(deeplinkCookie)
          ? deeplinkCookie + "/callback"
          : iosDeeplinkUrl + "/callback";
    }
    return null;
  }

  public String getForgotPasswordUrl(String mobilePlatform, String source, String deeplinkCookie) {
    PlatformComponent platformComponent = PlatformComponent.fromValue(source);
    if (PARTICIPANT_MANAGER == platformComponent) {
      return participantManagerUrl + "/#/forgotPassword";
    } else if (MOBILE_APPS == platformComponent
        && MobilePlatform.fromValue(mobilePlatform) == ANDROID) {
      return StringUtils.isNotBlank(deeplinkCookie)
          ? deeplinkCookie + "/forgotPassword"
          : androidDeeplinkUrl + "/forgotPassword";
    } else if (MOBILE_APPS == platformComponent
        && MobilePlatform.fromValue(mobilePlatform) == IOS) {
      return StringUtils.isNotBlank(deeplinkCookie)
          ? deeplinkCookie + "/forgotPassword"
          : iosDeeplinkUrl + "/forgotPassword";
    }
    return null;
  }

  public String getSignupUrl(String mobilePlatform, String deeplinkCookie) {
    if (MobilePlatform.fromValue(mobilePlatform) == ANDROID) {
      return StringUtils.isNotBlank(deeplinkCookie)
          ? deeplinkCookie + "/signup"
          : androidDeeplinkUrl + "/signup";
    } else if (MobilePlatform.fromValue(mobilePlatform) == IOS) {
      return StringUtils.isNotBlank(deeplinkCookie)
          ? deeplinkCookie + "/signup"
          : iosDeeplinkUrl + "/signup";
    }
    return null;
  }

  public String getAccountActivationUrl(
      String mobilePlatform, String source, String deeplinkCookie) {
    PlatformComponent platformComponent = PlatformComponent.fromValue(source);
    if (PARTICIPANT_MANAGER == platformComponent) {
      return participantManagerUrl + "/#/activation";
    } else if (MOBILE_APPS == platformComponent
        && MobilePlatform.fromValue(mobilePlatform) == ANDROID) {
      return StringUtils.isNotBlank(deeplinkCookie)
          ? deeplinkCookie + "/activation"
          : androidDeeplinkUrl + "/activation";
    } else if (MOBILE_APPS == platformComponent
        && MobilePlatform.fromValue(mobilePlatform) == IOS) {
      return StringUtils.isNotBlank(deeplinkCookie)
          ? deeplinkCookie + "/activation"
          : iosDeeplinkUrl + "/activation";
    }
    return null;
  }

  public String getTermsUrl(String mobilePlatform, String source, String deeplinkCookie) {
    PlatformComponent platformComponent = PlatformComponent.fromValue(source);
    if (PARTICIPANT_MANAGER == platformComponent) {
      return participantManagerUrl + "/#/terms";
    } else if (MOBILE_APPS == platformComponent
        && MobilePlatform.fromValue(mobilePlatform) == ANDROID) {
      return StringUtils.isNotBlank(deeplinkCookie)
          ? deeplinkCookie + "/terms"
          : androidDeeplinkUrl + "/terms";
    } else if (MOBILE_APPS == platformComponent
        && MobilePlatform.fromValue(mobilePlatform) == IOS) {
      return StringUtils.isNotBlank(deeplinkCookie)
          ? deeplinkCookie + "/terms"
          : iosDeeplinkUrl + "/terms";
    }
    return null;
  }

  public String getPrivacyPolicyUrl(String mobilePlatform, String deeplinkCookie) {
    if (MobilePlatform.fromValue(mobilePlatform) == ANDROID) {
      return StringUtils.isNotBlank(deeplinkCookie)
          ? deeplinkCookie + "/privacyPolicy"
          : androidDeeplinkUrl + "/privacyPolicy";
    } else if (MobilePlatform.fromValue(mobilePlatform) == IOS) {
      return StringUtils.isNotBlank(deeplinkCookie)
          ? deeplinkCookie + "/privacyPolicy"
          : iosDeeplinkUrl + "/privacyPolicy";
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
