/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.utils;

public class AppConstants {

  private AppConstants() {}

  public static final String SERVER_TIMEZONE = "America/New_York";
  public static final String USER_EMAILID = "userEmail";
  public static final String APPLICATION_ID = "appInfoId";
  public static final String CUSTOM_APPLICATION_ID = "appId";
  public static final String ORGANIZATION_ID = "orgId";
  public static final String KEY_USERID = "userId";
  public static final String STUDY_INFO_ID = "studyInfoId";
  public static final String EMAIL = "email";
  public static final String SDF_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

  public static final String AUDIT_EVENT_UPDATE_ELIGIBILITY_CONSENT_NAME =
      "Signed Consent Document saved";
  public static final String AUDIT_EVENT_UPDATE_ELIGIBILITY_CONSENT_DESC =
      "Consent Document e-signed by the participant, saved successfully into Cloud Storage. Consent Document file name \"%s\", Enrolment Status: \"%s\",  Consent Version: %s, Primary Consent: Provided, Data-Sharing Consent: %s. ";

  public static final String AUDIT_EVENT_ENROLL_INTO_STUDY_SUCCESS_NAME =
      "Enrolment into study successful";
  public static final String AUDIT_EVENT_ENROLL_INTO_STUDY_SUCCESS_DESC =
      "App user was enrolled into study successfully as a Participant. Enrolment Status updated to \"%s\", Participant ID: %s";

  public static final String AUDIT_EVENT_ENROLL_INTO_STUDY_FAILED_NAME =
      "Enrolment into study failed";
  public static final String AUDIT_EVENT_ENROLL_INTO_STUDY_FAILED_DESC =
      "App user could not be enrolled into study as a Participant.";

  public static final String AUDIT_EVENT_UPDATE_ELIGIBILITY_CONSENT_FAIL_NAME =
      "Signed Consent Document could not be saved";
  public static final String AUDIT_EVENT_UPDATE_ELIGIBILITY_CONSENT_FAIL_DESC =
      "Consent Document e-signed by the participant, could not be saved  into Cloud Storage. Consent Document file name \"%s\", Enrolment Status: \"%s\",  Consent Version: %s, Primary Consent: Provided, Data-Sharing Consent: %s";

  public static final String AUDIT_EVNT_INFORM_CONSNT_PROVIDED_NAME =
      "Informed Consent provided for study";
  public static final String AUDIT_EVNT_INFORM_CONSNT_PROVIDED_DESC =
      "App user provided informed consent for the study and Consent information saved for the participant is as follows: Consent Version: %s, Primary Consent: Provided, Data-Sharing Consent: %s";

  public static final String MOBILE_APP_CLIENT_ID = "FMSGCMOBAPP";
  public static final String NOT_APPLICABLE = "NA";

  public static final String PARTICIPANT_LEVEL_ACCESS = "Participant";
  public static final String APP_LEVEL_ACCESS = "App User";
}
