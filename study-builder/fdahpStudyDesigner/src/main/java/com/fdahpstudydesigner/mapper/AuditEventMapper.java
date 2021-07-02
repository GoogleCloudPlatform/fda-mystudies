/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
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
import java.util.UUID;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

public final class AuditEventMapper {

  private AuditEventMapper() {}

  private static final String APP_ID = "appId";

  private static final String MOBILE_PLATFORM = "mobilePlatform";

  private static final String CORRELATION_ID = "correlationId";

  private static final String USER_ID = "userId";

  private static final String APP_VERSION = "appVersion";

  private static final String SOURCE = "source";

  public static void addAuditEventHeaderParams(
      HttpHeaders headers, AuditLogEventRequest auditRequest) {
    if (!headers.containsKey(USER_ID)) {
      headers.set(USER_ID, auditRequest.getUserId());
    }
    if (!headers.containsKey(APP_VERSION)) {
      headers.set(APP_VERSION, auditRequest.getAppVersion());
    }
    if (!headers.containsKey(SOURCE)) {
      headers.set(SOURCE, auditRequest.getSource());
    }
    if (!headers.containsKey(CORRELATION_ID)) {
      headers.set(CORRELATION_ID, auditRequest.getCorrelationId());
    }
    if (!headers.containsKey(MOBILE_PLATFORM)) {
      headers.set(MOBILE_PLATFORM, auditRequest.getMobilePlatform());
    }
    if (!headers.containsKey(APP_ID)) {
      headers.set(APP_ID, auditRequest.getAppId());
    }
  }

  public static AuditLogEventRequest fromHttpServletRequest(HttpServletRequest request) {
    Map<String, String> map = FdahpStudyDesignerUtil.getAppProperties();
    AuditLogEventRequest auditRequest = new AuditLogEventRequest();

    auditRequest.setAppId(PlatformComponent.STUDY_BUILDER.getValue());
    auditRequest.setAppVersion(map.get("applicationVersion"));
    if (request.getSession() != null) {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      if (sesObj != null) {
        auditRequest.setUserAccessLevel(sesObj.getAccessLevel());
        auditRequest.setCorrelationId(sesObj.getCorrelationId());
        auditRequest.setUserId(String.valueOf(sesObj.getUserId()));
      }
    }

    auditRequest.setCorrelationId(
        StringUtils.defaultIfEmpty(auditRequest.getCorrelationId(), UUID.randomUUID().toString()));

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
    auditRequest.setOccurred(new Timestamp(Instant.now().toEpochMilli()));
    return auditRequest;
  }
}
