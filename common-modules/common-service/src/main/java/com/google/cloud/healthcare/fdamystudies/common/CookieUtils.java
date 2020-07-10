/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.MultiValueMap;

public final class CookieUtils {

  private CookieUtils() {}

  public static void addCookies(
      HttpServletRequest request,
      HttpServletResponse response,
      MultiValueMap<String, String> params,
      String... cookieNames) {
    for (String cookieName : cookieNames) {
      addCookie(request, response, cookieName, params.getFirst(cookieName));
    }
  }

  public static void addCookie(
      HttpServletRequest request,
      HttpServletResponse response,
      String cookieName,
      String cookieValue) {
    Cookie cookie = new Cookie(cookieName, cookieValue);
    cookie.setMaxAge(600);
    if (StringUtils.equalsIgnoreCase("https", request.getScheme())) {
      cookie.setSecure(true);
    }
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    response.addCookie(cookie);
  }
}
