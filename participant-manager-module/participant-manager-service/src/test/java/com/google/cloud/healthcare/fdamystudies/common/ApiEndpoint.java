/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import java.net.MalformedURLException;
import java.net.URL;

public enum ApiEndpoint {
  ADD_NEW_SITE("http://localhost:8080/participant-manager-service/sites"),

  ADD_NEW_LOCATION("http://localhost:8080/participant-manager-service/locations"),

  HEALTH("http://localhost:8080/participant-manager-service/healthCheck"),

  SET_UP_ACCOUNT("http://localhost:8080/participant-manager-service/users/"),

  PATCH_USER("http://localhost:8080/participant-manager-service/users/{userId}"),

  GET_APPS("http://localhost:8080/participant-manager-service/apps"),

  GET_STUDIES("http://localhost:8080/participant-manager-service/studies"),

  UPDATE_LOCATION("http://localhost:8080/participant-manager-service/locations/{locationId}"),

  GET_STUDY_PARTICIPANT(
      "http://localhost:8080/participant-manager-service/studies/{studyId}/participants"),

  GET_SITE_PARTICIPANTS(
      "http://localhost:8080/participant-manager-service/sites/{siteId}/participants"),

  ADD_NEW_PARTICIPANT(
      "http://localhost:8080/participant-manager-service/sites/{siteId}/participants"),

  DECOMISSION_SITE("http://localhost:8080/participant-manager-service/sites/{siteId}/decommission"),

  GET_LOCATIONS("http://localhost:8003/participant-manager-service/locations"),

  GET_LOCATION_BY_LOCATION_ID(
      "http://localhost:8003/participant-manager-service/locations/{locationId}"),

  GET_APP_PARTICIPANTS("http://localhost:8080/participant-manager-service/apps/{app}/participants"),

  ADD_NEW_USER("http://localhost:8003/participant-manager-service/users"),

  UPDATE_USER("http://localhost:8003/participant-manager-service/users/{superAdminUserId}/"),

  GET_USER_PROFILE("http://localhost:8080/participant-manager-service/users/{userId}"),

  GET_USER_DETAILS("http://localhost:8080/participant-manager-service/users"),

  GET_PARTICIPANT_DETAILS(
      "http://localhost:8080/participant-manager-service/sites/{participantRegistrySite}/participant"),

  INVITE_PARTICIPANTS(
      "http://localhost:8003/participant-manager-service/sites/{siteId}/participants/invite"),

  IMPORT_PARTICIPANT(
      "http://localhost:8003/participant-manager-service/sites/{siteId}/participants/import"),

  UPDATE_ONBOARDING_STATUS(
      "http://localhost:8003/participant-manager-service/sites/{siteId}/participants/status"),

  GET_SITES("http://localhost:8080/participant-manager-service/sites"),

  UPDATE_USER_PROFILE("http://localhost:8080/participant-manager-service/users/{userId}/profile"),

  UPDATE_TARGET_ENROLLMENT(
      "http://localhost:8080/participant-manager-service/studies/{studyId}/targetEnrollment"),

  GET_CONSENT_DOCUMENT(
      "http://localhost:8080/participant-manager-service/consents/{consentId}/consentDocument"),

  GET_USER_DETAILS_BY_SECURITY_CODE(
      "http://localhost:8080/participant-manager-service/users/securitycodes/{securityCode}"),

  GET_ADMIN_DETAILS_AND_APPS(
      "http://localhost:8080/participant-manager-service/users/admin/{adminId}"),

  GET_USERS("http://localhost:8080/participant-manager-service/users");

  private String url;

  private ApiEndpoint(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  public String getPath() throws MalformedURLException {
    return new URL(url).getPath();
  }
}
