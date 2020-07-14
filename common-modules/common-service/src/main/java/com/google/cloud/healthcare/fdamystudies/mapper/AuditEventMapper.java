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

  private static final String ORG_ID = "orgId";

  public static AuditLogEventRequest fromHttpServletRequest(HttpServletRequest request)
      throws UnknownHostException {
    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setOrgId(getValue(request, ORG_ID));
    auditRequest.setAppId(getValue(request, APP_ID));
    auditRequest.setClientAppVersion(getValue(request, CLIENT_APP_VERSION));
    auditRequest.setCorrelationId(getValue(request, CORRELATION_ID));
    auditRequest.setDeviceType(getValue(request, DEVICE_TYPE));
    auditRequest.setDevicePlatform(getValue(request, DEVICE_PLATFORM));
    auditRequest.setClientId(getValue(request, CLIENT_ID));
    auditRequest.setUserId(getValue(request, USER_ID));
    auditRequest.setClientIp(getClientIP(request));
    auditRequest.setRequestUri(request.getRequestURI());
    auditRequest.setSystemIp(InetAddress.getLocalHost().getHostAddress());
    return auditRequest;
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
