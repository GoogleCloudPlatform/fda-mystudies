/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {

  public static final String WEB_SERVICE_NAME = "web_service_name";
  public static final String STATUS_COMPLETED = "Completed";
  public static final String SERVER_TIMEZONE = "America/New_York";
  public static final String USER_EMAILID = "userEmail";
  public static final String APPLICATION_ID = "appInfoId";
  public static final String CUSTOM_APPLICATION_ID = "appId";
  public static final String ORGANIZATION_ID = "orgId";
  public static final String KEY_USERID = "userId";
  public static final String STUDY_INFO_ID = "studyInfoId";
  public static final String EMAIL = "email";
  public static final String SDF_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

  public static final String EVENT_INFO = "event_info";
  public static final String DESCRIPTION = "description";
  public static final String APP_ID = "app_id";
  public static final String ORG_ID = "org_id";
  public static final String APPLICATION_VERSION = "application_version";
  public static final String EVENT_DETAIL = "event_detail";
  public static final String APPLICATION_COMPONENT_NAME = "application_component_name";
  public static final String RESOURCE_SERVER = "resource_server";
  public static final String CLIENT_ID = "client_id";
  public static final String CLIENT_ACCESS_LEVEL = "client_access_level";
  public static final String ACCESS_LEVEL = "access_level";
  public static final String CORRELATION_ID = "correlation_id";
  public static final String EVENT_NAME = "event_name";
  public static final String EVENT_TIMESTAMP = "event_timestamp";
  public static final String USER_ID_HEADER = "userId";
  public static final String USER_ID = "user_id";
  public static final String ALERT = "alert";
  public static final String SYSTEM_ID = "system_id";
  public static final String STATUS = "status";
  public static final String ERROR_TYPE = "error_type";
  public static final String ERROR_MESSAGE = "error_message";
  public static final String ERROR_CODE = "error_code";
  public static final String ROLLBACK = "rollback";
  public static final String PATH = "path";
  public static final String TIMESTAMP = "timestamp";
  public static final String ERROR = "error";
  public static final String MESSAGE = "message";
  public static final String CLIENT_APP_VERSION = "client_app_version";
  public static final String DEVICE_PLATFORM = "device_platform";
  public static final String DEVICE_TYPE = "device_type";
  public static final String REQUEST_URI = "request_uri";
  public static final String SYSTEM_IP = "system_ip";
  public static final String ENROLLMENT_STATUS = "enrollment_status";
  public static final String PARTICIPANT_ID = "participant_id";
  public static final String CLIENT_IP = "client_ip";
  public static final String CONSENT_VERSION = "consent_version";
  public static final String PROVIDED_NOT_PROVIDED_NA =
      "provided_or_not_provided_or_not_applicable";
  public static final String DATA_SHARING_CONSENT = "data_sharing_consent";
  public static final String FILE_NAME = "file_name";

  public static final String ACCESS_TOKEN = "access_token";
  public static final String CODE = "code";
  public static final String CODE_VERIFIER = "code_verifier";
  public static final String CONSENT_CHALLENGE = "consent_challenge";
  public static final String GRANT_SCOPE = "grant_scope";
  public static final String GRANT_TYPE = "grant_type";
  public static final String ID_TOKEN = "id_token";
  public static final String LOGIN_CHALLENGE = "login_challenge";
  public static final String REDIRECT_URI = "redirect_uri";
  public static final String REFRESH_TOKEN = "refresh_token";
  public static final String REMEMBER = "remember";
  public static final String REMEMBER_FOR = "remember_for";
  public static final String SCOPE = "scope";
  public static final String SESSION = "session";
  public static final String SUBJECT = "subject";
  public static final String TOKEN = "token";
  public static final String VAL = "val";
  public static final String PLACE_HOLDERS = "place_holders";
  public static final String RESOURCE_REQUESTING_ENTITY_SYSTM_ID =
      "resource_requesting_entity_system_id";

  public static final String AUDIT_EVENT_UPDATE_ELIGIBILITY_CONSENT_NAME =
      "Update eligibility consent status";
  public static final String AUDIT_EVENT_UPDATE_ELIGIBILITY_CONSENT_DESC =
      "Eligibility consent has been updated for study %s";
}
