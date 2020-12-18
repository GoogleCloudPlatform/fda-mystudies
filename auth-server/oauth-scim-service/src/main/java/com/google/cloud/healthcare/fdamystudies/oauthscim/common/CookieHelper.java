/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.common;

import com.google.cloud.healthcare.fdamystudies.oauthscim.config.AppPropertyConfig;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.WebUtils;

@Component
public class CookieHelper {

  @Autowired private AppPropertyConfig appConfig;

  private static final String COOKIE_PREFIX = "mystudies_";

  public void addCookies(
      HttpServletResponse response, MultiValueMap<String, String> params, String... cookieNames)
      throws UnsupportedEncodingException {
    String paramName = null;
    for (String cookieName : cookieNames) {
      paramName =
          StringUtils.startsWith(cookieName, COOKIE_PREFIX)
              ? cookieName.substring(cookieName.indexOf('_') + 1)
              : cookieName;
      addCookie(response, cookieName, params.getFirst(paramName));
    }
  }

  public void addCookie(HttpServletResponse response, String cookieName, String cookieValue)
      throws UnsupportedEncodingException {
    // URLEncoder.encode used to fix this error:
    // An invalid character [32] (space) was present in the Cookie value
    cookieValue = StringUtils.defaultString(cookieValue);
    Cookie cookie = new Cookie(cookieName, URLEncoder.encode(cookieValue, "UTF-8"));
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

  public String getCookieValue(HttpServletRequest request, String cookieName)
      throws UnsupportedEncodingException {
    Cookie cookie = WebUtils.getCookie(request, cookieName);
    return cookie != null ? URLDecoder.decode(cookie.getValue(), "UTF-8") : null;
  }
}
