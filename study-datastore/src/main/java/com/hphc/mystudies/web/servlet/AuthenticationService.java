/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package com.hphc.mystudies.web.servlet;

import com.hphc.mystudies.util.StudyMetaDataUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.internal.util.Base64;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class AuthenticationService {

  private static final XLogger LOGGER =
      XLoggerFactory.getXLogger(AuthenticationService.class.getName());

  @SuppressWarnings("unchecked")
  HashMap<String, String> authPropMap = StudyMetaDataUtil.getAuthorizationProperties();

  public boolean authenticate(String authCredentials) {
    LOGGER.entry("begin authenticate()");
    boolean authenticationStatus = false;
    String bundleIdAndAppToken = null;
    String bundleIdKey = "";
    String appTokenKey = "";
    try {
      if (StringUtils.isNotEmpty(authCredentials) && authCredentials.contains("Basic")) {
        final byte[] encodedUserPassword =
            authCredentials.replaceFirst("Basic" + " ", "").getBytes();
        byte[] decodedBytes = Base64.decode(encodedUserPassword);
        bundleIdAndAppToken = new String(decodedBytes, "UTF-8");
        if (bundleIdAndAppToken.contains(":")) {
          final StringTokenizer tokenizer = new StringTokenizer(bundleIdAndAppToken, ":");
          final String bundleId = tokenizer.nextToken();
          final String appToken = tokenizer.nextToken();

          for (Map.Entry<String, String> map : authPropMap.entrySet()) {
            if (map.getValue().equals(appToken)) {
              appTokenKey = map.getKey();
            }
            if (map.getValue().equals(bundleId)) {
              bundleIdKey = map.getKey();
            }
          }

          if (authPropMap.containsValue(bundleId)
              && authPropMap.containsValue(appToken)
              && isValidPlatformType(appTokenKey, bundleIdKey)) {
            authenticationStatus = true;
          }
        }
      }
    } catch (Exception e) {
      LOGGER.error("AuthenticationService - authenticate() :: ERROR", e);
      return authenticationStatus;
    }
    LOGGER.exit("authenticate() - Ends");
    return authenticationStatus;
  }

  private static boolean isValidPlatformType(String appTokenKey, String bundleIdKey) {

    final StringTokenizer appTokenizer = new StringTokenizer(appTokenKey, ".");
    final String appTokenPlatFormType = appTokenizer.nextToken();

    final StringTokenizer bundleIdTokenizer = new StringTokenizer(bundleIdKey, ".");
    final String bundleIdPlatformType = bundleIdTokenizer.nextToken();

    return StringUtils.equals(appTokenPlatFormType, bundleIdPlatformType);
  }
}
