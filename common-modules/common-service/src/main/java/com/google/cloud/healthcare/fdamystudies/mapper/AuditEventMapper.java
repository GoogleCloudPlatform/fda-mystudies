package com.google.cloud.healthcare.fdamystudies.mapper;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public final class AuditEventMapper {

  private AuditEventMapper() {}

  private static final String APP_ID = "appId";

  private static final String CLIENT_ID = "clientId";

  private static final String DEVICE_TYPE = "deviceType";

  private static final String DEVICE_PLATFORM = "devicePlatform";

  private static final String CLIENT_APP_VERSION = "clientAppVersion";

  private static final String CORRELATION_ID = "correlationId";

  private static final String USER_ID = "userId";

  public static AuditLogEventRequest fromHttpServletRequest(HttpServletRequest request)
      throws UnknownHostException {
    AuditLogEventRequest aleRequest = new AuditLogEventRequest();
    aleRequest.setAppId(getValue(request, APP_ID));
    aleRequest.setClientAppVersion(getValue(request, CLIENT_APP_VERSION));
    aleRequest.setCorrelationId(getValue(request, CORRELATION_ID));
    aleRequest.setDeviceType(getValue(request, DEVICE_TYPE));
    aleRequest.setDevicePlatform(getValue(request, DEVICE_PLATFORM));
    aleRequest.setClientId(getValue(request, CLIENT_ID));
    aleRequest.setUserId(getValue(request, USER_ID));
    aleRequest.setClientIp(getClientIP(request));
    aleRequest.setRequestUri(request.getRequestURI());
    aleRequest.setSystemIp(InetAddress.getLocalHost().getHostAddress());
    return aleRequest;
  }

  private static String getValue(HttpServletRequest request, String name) {
    String value = request.getHeader(name);
    if (StringUtils.isEmpty(value)) {
      value = getCookieValue(request, name);
    }
    return value;
  }

  private static String getClientIP(HttpServletRequest request) {
    return StringUtils.defaultIfEmpty(
        request.getHeader("X-FORWARDED-FOR"), request.getRemoteAddr());
  }

  private static String getCookieValue(HttpServletRequest req, String cookieName) {
    if (req != null && req.getCookies() != null) {
      return Arrays.stream(req.getCookies())
          .filter(c -> c.getName().equals(cookieName))
          .findFirst()
          .map(Cookie::getValue)
          .orElse(null);
    }
    return null;
  }
}
