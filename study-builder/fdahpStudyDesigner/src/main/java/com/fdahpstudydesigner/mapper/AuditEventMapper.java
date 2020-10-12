/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.mapper;

import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.fdahpstudydesigner.common.BadRequestException;
import com.fdahpstudydesigner.common.MobilePlatform;
import com.fdahpstudydesigner.common.PlatformComponent;
import com.fdahpstudydesigner.common.StudyBuilderAuditEvent;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public final class AuditEventMapper {

  private AuditEventMapper() {}

  private static final String APP_ID = "appId";

  private static final String MOBILE_PLATFORM = "mobilePlatform";

  private static final String CORRELATION_ID = "correlationId";

  private static final String USER_ID = "userId";

  private static final String APP_VERSION = "appVersion";

  private static final String SOURCE = "source";

  public static AuditLogEventRequest fromHttpServletRequest(HttpServletRequest request) {
    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setAppId(getValue(request, APP_ID));
    auditRequest.setAppVersion(getValue(request, APP_VERSION));
    auditRequest.setCorrelationId(getValue(request, CORRELATION_ID));
    auditRequest.setUserId(getValue(request, USER_ID));
    SessionObject sesObj =
        (SessionObject)
            request.getSession(false).getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
    if (sesObj != null) {
      auditRequest.setUserAccessLevel(sesObj.getAccessLevel());
    }

    String source = getValue(request, SOURCE);
    if (StringUtils.isNotEmpty(source)) {
      PlatformComponent platformComponent = PlatformComponent.fromValue(source);
      if (platformComponent == null) {
        throw new BadRequestException(String.format("Invalid '%s' value.", SOURCE));
      }
      auditRequest.setSource(platformComponent.getValue());
    }

    auditRequest.setUserIp(getUserIP(request));

    String mobilePlatform = getValue(request, MOBILE_PLATFORM);
    if (StringUtils.isNotEmpty(mobilePlatform)) {
      MobilePlatform mobilePlatformEnum = MobilePlatform.fromValue(mobilePlatform);
      if (mobilePlatformEnum == null) {
        throw new BadRequestException(String.format("Invalid '%s' value.", MOBILE_PLATFORM));
      }
      auditRequest.setMobilePlatform(mobilePlatform);
    }

    return auditRequest;
  }

  private static String getValue(HttpServletRequest request, String name) {
    String value = request.getHeader(name);
    if (StringUtils.isEmpty(value)) {
      value = getCookieValue(request, name);
    }
    return value;
  }

  private static String getUserIP(HttpServletRequest request) {
    return StringUtils.defaultIfEmpty(
        request.getHeader("X-FORWARDED-FOR"), request.getRemoteAddr());
  }

  private static String getCookieValue(HttpServletRequest req, String cookieName) {
    if (req != null && req.getCookies() != null) {
      for (Cookie cookie : req.getCookies()) {
        if (cookie.getName().equals(cookieName)) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  public static AuditLogEventRequest fromAuditLogEventEnumAndCommonPropConfig(
      StudyBuilderAuditEvent eventEnum, AuditLogEventRequest auditRequest) {
    auditRequest.setEventCode(eventEnum.getEventCode());
    // Use enum value where specified, otherwise, use 'source' header value.
    if (eventEnum.getSource() != null) {
      auditRequest.setSource(eventEnum.getSource().getValue());
    }

    auditRequest.setDestination(eventEnum.getDestination().getValue());
    if (eventEnum.getResourceServer() != null) {
      auditRequest.setResourceServer(eventEnum.getResourceServer().getValue());
    }

    Map<String, String> map = FdahpStudyDesignerUtil.getAppProperties();
    String applicationVersion = map.get("applicationVersion");
    auditRequest.setSourceApplicationVersion(applicationVersion);
    auditRequest.setDestinationApplicationVersion(applicationVersion);
    auditRequest.setPlatformVersion(applicationVersion);
    auditRequest.setOccured(new Timestamp(Instant.now().toEpochMilli()));
    return auditRequest;
  }
}
