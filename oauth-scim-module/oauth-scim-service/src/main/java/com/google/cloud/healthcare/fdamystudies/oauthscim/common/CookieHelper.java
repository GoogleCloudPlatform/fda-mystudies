/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.common;

import com.google.cloud.healthcare.fdamystudies.oauthscim.config.AppPropertyConfig;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

@Component
public class CookieHelper {

  @Autowired private AppPropertyConfig appConfig;

  public void addCookies(
      HttpServletResponse response, MultiValueMap<String, String> params, String... cookieNames) {
    for (String cookieName : cookieNames) {
      addCookie(response, cookieName, params.getFirst(cookieName));
    }
  }

  public void addCookie(HttpServletResponse response, String cookieName, String cookieValue) {
    Cookie cookie = new Cookie(cookieName, cookieValue);
    cookie.setMaxAge(600);
    cookie.setSecure(appConfig.isSecureCookie());
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    response.addCookie(cookie);
  }

  public void deleteCookie(HttpServletResponse response, String cookieName) {
    Cookie cookie = new Cookie(cookieName, null);
    cookie.setMaxAge(0);
    cookie.setSecure(appConfig.isSecureCookie());
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    response.addCookie(cookie);
  }
}
