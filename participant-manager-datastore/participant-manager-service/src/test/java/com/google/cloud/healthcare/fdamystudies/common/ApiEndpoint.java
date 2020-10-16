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
  ADD_NEW_SITE("http://localhost:8080/participant-manager-datastore/sites"),

  ADD_NEW_LOCATION("http://localhost:8080/participant-manager-datastore/locations"),

  HEALTH("http://localhost:8080/participant-manager-datastore/healthCheck"),

  SET_UP_ACCOUNT("http://localhost:8080/participant-manager-datastore/users/setUpAccount"),

  PATCH_USER("http://localhost:8080/participant-manager-datastore/users/{userId}"),

  GET_APPS("http://localhost:8080/participant-manager-datastore/apps"),

  GET_STUDIES("http://localhost:8080/participant-manager-datastore/studies"),

  UPDATE_LOCATION("http://localhost:8080/participant-manager-datastore/locations/{locationId}"),

  GET_STUDY_PARTICIPANT(
      "http://localhost:8080/participant-manager-datastore/studies/{studyId}/participants"),

  GET_SITE_PARTICIPANTS(
      "http://localhost:8080/participant-manager-datastore/sites/{siteId}/participants"),

  ADD_NEW_PARTICIPANT(
      "http://localhost:8080/participant-manager-datastore/sites/{siteId}/participants"),

  DECOMISSION_SITE(
      "http://localhost:8080/participant-manager-datastore/sites/{siteId}/decommission"),

  GET_LOCATIONS("http://localhost:8003/participant-manager-datastore/locations"),

  GET_LOCATION_BY_LOCATION_ID(
      "http://localhost:8003/participant-manager-datastore/locations/{locationId}"),

  GET_APP_PARTICIPANTS(
      "http://localhost:8080/participant-manager-datastore/apps/{app}/participants"),

  ADD_NEW_USER("http://localhost:8003/participant-manager-datastore/users"),

  UPDATE_USER("http://localhost:8003/participant-manager-datastore/users/{superAdminUserId}/"),

  GET_USER_PROFILE("http://localhost:8080/participant-manager-datastore/users/{userId}"),

  GET_USER_DETAILS("http://localhost:8080/participant-manager-datastore/users"),

  GET_PARTICIPANT_DETAILS(
      "http://localhost:8080/participant-manager-datastore/sites/{participantRegistrySite}/participant"),

  INVITE_PARTICIPANTS(
      "http://localhost:8003/participant-manager-datastore/sites/{siteId}/participants/invite"),

  IMPORT_PARTICIPANT(
      "http://localhost:8003/participant-manager-datastore/sites/{siteId}/participants/import"),

  UPDATE_ONBOARDING_STATUS(
      "http://localhost:8003/participant-manager-datastore/sites/{siteId}/participants/status"),

  GET_SITES("http://localhost:8080/participant-manager-datastore/sites"),

  UPDATE_USER_PROFILE("http://localhost:8080/participant-manager-datastore/users/{userId}/profile"),

  UPDATE_TARGET_ENROLLMENT(
      "http://localhost:8080/participant-manager-datastore/studies/{studyId}/targetEnrollment"),

  GET_CONSENT_DOCUMENT(
      "http://localhost:8080/participant-manager-datastore/consents/{consentId}/consentDocument"),

  GET_USER_DETAILS_BY_SECURITY_CODE(
      "http://localhost:8080/participant-manager-datastore/users/securitycodes/{securityCode}"),

  GET_ADMIN_DETAILS_AND_APPS(
      "http://localhost:8080/participant-manager-datastore/users/admin/{adminId}"),

  GET_USERS("http://localhost:8080/participant-manager-datastore/users");

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
