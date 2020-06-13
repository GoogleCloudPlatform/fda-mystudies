package com.google.cloud.healthcare.fdamystudies.common;

import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.APP_ID;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.CLIENT_APP_VERSION;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.CLIENT_IP;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.CONSENT_VERSION;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.CORRELATION_ID;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.DEVICE_PLATFORM;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.DEVICE_TYPE;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.ENROLLMENT_STATUS;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.FILE_NAME;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.ORG_ID;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.PARTICIPANT_ID;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.PLACE_HOLDERS;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.PROVIDED_NOT_PROVIDED_NA;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.REQUEST_URI;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.SYSTEM_IP;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.USER_ID_HEADER;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.healthcare.fdamystudies.bean.ConsentStudyResponseBean;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.model.StudyConsentBO;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuditLogEventHelper {

  private static final Logger LOG = LoggerFactory.getLogger(AuditLogEventHelper.class);

  public static JsonNode getAuditLogEventParams(HttpRequest request) {
    ObjectNode aleParams = new ObjectMapper().createObjectNode();
    aleParams.put(CLIENT_IP, getClientIP(request));
    aleParams.put(REQUEST_URI, request.getURI().getPath());

    String[] aleFields = {
      CORRELATION_ID,
      DEVICE_TYPE,
      DEVICE_PLATFORM,
      ORG_ID,
      APP_ID,
      CLIENT_APP_VERSION,
      USER_ID_HEADER
    };
    for (String key : aleFields) {
      String value = request.getHeaders().getFirst(key);
      if (StringUtils.isBlank(value)) {
        value = getCookieValue((HttpServletRequest) request, key);
      }
      aleParams.put(key, value);
    }

    if (!aleParams.hasNonNull(CORRELATION_ID)) {
      aleParams.put(CORRELATION_ID, getCookieValue((HttpServletRequest) request, CORRELATION_ID));
    }

    try {
      aleParams.put(SYSTEM_IP, InetAddress.getLocalHost().getHostAddress());
    } catch (UnknownHostException e) {
      LOG.error("unable to get host address", e);
    }
    return aleParams;
  }

  public static ObjectNode getAllEventParams(
      HttpServletRequest request, StudyConsentBO studyConsent, ParticipantStudiesBO pStudies) {
    ObjectNode placeholders = new ObjectMapper().createObjectNode();
    placeholders.put(PARTICIPANT_ID, pStudies.getParticipantId());
    placeholders.put(ENROLLMENT_STATUS, pStudies.getStatus());
    placeholders.put(CONSENT_VERSION, studyConsent.getVersion());
    placeholders.put(PROVIDED_NOT_PROVIDED_NA, pStudies.getSharing());
    placeholders.put(FILE_NAME, studyConsent.getPdfPath());
    ObjectNode aleParams = getAuditLogEventParams(request);
    aleParams.set(PLACE_HOLDERS, placeholders);
    return aleParams;
  }

  public static ObjectNode getAllEventParams(
      HttpServletRequest request, ConsentStudyResponseBean bean) {
    ObjectNode aleParams = getAuditLogEventParams(request);
    ObjectNode placeholders = new ObjectMapper().createObjectNode();

    if (null != bean.getConsent()) {
      placeholders.put(CONSENT_VERSION, bean.getConsent().getVersion());
      placeholders.put(FILE_NAME, bean.getConsent().getPath());
      placeholders.put(AppConstants.WEB_SERVICE_NAME, request.getRequestURI());
    }
    placeholders.put(PROVIDED_NOT_PROVIDED_NA, bean.getSharing());
    aleParams.set(PLACE_HOLDERS, placeholders);
    return aleParams;
  }

  public static ObjectNode getAuditLogEventParams(HttpServletRequest request) {
    ObjectNode aleParams = new ObjectMapper().createObjectNode();
    aleParams.put(CLIENT_IP, getClientIP(request));
    aleParams.put(REQUEST_URI, request.getRequestURI());

    String[] aleFields = {
      CORRELATION_ID,
      DEVICE_TYPE,
      DEVICE_PLATFORM,
      ORG_ID,
      APP_ID,
      CLIENT_APP_VERSION,
      USER_ID_HEADER
    };
    for (String key : aleFields) {
      String value = request.getHeader(key);
      if (StringUtils.isEmpty(value)) {
        value = getCookieValue(request, key);
      }
      aleParams.put(key, value);
    }

    if (!aleParams.hasNonNull(CORRELATION_ID)) {
      aleParams.put(CORRELATION_ID, getCookieValue(request, CORRELATION_ID));
    }

    try {
      aleParams.put(SYSTEM_IP, InetAddress.getLocalHost().getHostAddress());
    } catch (UnknownHostException e) {
      LOG.error("unable to get host address", e);
    }
    return aleParams;
  }

  private static String getClientIP(HttpServletRequest request) {
    return StringUtils.defaultIfEmpty(
        request.getHeader("X-FORWARDED-FOR"), request.getRemoteAddr());
  }

  private static String getClientIP(HttpRequest request) {
    return StringUtils.defaultIfEmpty(
        request.getHeaders().getFirst("X-FORWARDED-FOR"), request.getURI().getHost());
  }

  private static String getCookieValue(HttpServletRequest req, String cookieName) {
    if (req.getCookies() != null) {
      return Arrays.stream(req.getCookies())
          .filter(c -> c.getName().equals(cookieName))
          .findFirst()
          .map(Cookie::getValue)
          .orElse(null);
    }
    return null;
  }
}
