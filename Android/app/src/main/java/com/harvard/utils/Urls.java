/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.harvard.utils;

import com.harvard.BuildConfig;
import com.harvard.FdaApplication;

public class Urls {
  public static String BASE_URL_WCP_SERVER = BuildConfig.BASE_URL_WCP_SERVER;
  public static String BASE_URL_REGISTRATION_SERVER = BuildConfig.BASE_URL_REGISTRATION_SERVER;
  public static String BASE_URL_REGISTRATION_CONSENT_SERVER =
          BuildConfig.BASE_URL_REGISTRATION_CONSENT_SERVER;
  public static String BASE_URL_REGISTRATION_ENROLLMENT_SERVER =
          BuildConfig.BASE_URL_REGISTRATION_ENROLLMENT_SERVER;
  public static String BASE_URL_AUTH_SERVER = BuildConfig.BASE_URL_AUTH_SERVER;
  public static String BASE_URL_RESPONSE_SERVER = BuildConfig.BASE_URL_RESPONSE_SERVER;

  // Auth Server
  public static String LOGIN = "/login";

  // New Auth Server
  public static String TOKENS = "/oauth2/token";
  public static String AUTH_SERVICE = "/users";
  public static String CHANGE_PASSWORD = "/change_password";
  public static String FORGOT_PASSWORD = "/user/reset_password";
  public static String LOGOUT = "/logout";
  public static String AUTH_SERVER_REDIRECT_URL = BuildConfig.BASE_URL_AUTH_SERVER + "/callback";
  public static String LOGIN_URL = BuildConfig.BASE_URL_HYDRA_SERVER + "/auth"
          + "?client_id=" + BuildConfig.HYDRA_CLIENT_ID
          + "&scope=offline_access"
          + "&response_type=code"
          + "&appId=" + BuildConfig.APP_ID
          + "&appVersion=" + BuildConfig.VERSION_NAME + "." + BuildConfig.VERSION_CODE
          + "&mobilePlatform=ANDROID"
          + "&code_challenge_method=S256"
          + "&code_challenge=" + FdaApplication.getCodeChallenge(FdaApplication.getRandomString())
          + "&correlationId=" + FdaApplication.getRandomString()
          + "&redirect_uri=" + AUTH_SERVER_REDIRECT_URL
          + "&state=" + FdaApplication.getRandomString();

  // Registration Server
  public static String REGISTER_USER = "/register";
  public static String UPDATE_USER_PROFILE = "/updateUserProfile";
  public static String GET_USER_PROFILE = "/userProfile";
  public static String CONFIRM_REGISTER_USER = "/verifyEmailId";
  public static String RESEND_CONFIRMATION = "/resendConfirmation";
  public static String DELETE_ACCOUNT = "/deactivate";
  public static String WITHDRAW = "/withdrawfromstudy";
  public static String CONTACT_US = "/contactUs";
  public static String FEEDBACK = "/feedback";

  // Registration Enrollment Server
  public static String UPDATE_STUDY_PREFERENCE = "/updateStudyState";
  public static String STUDY_STATE = "/studyState";
  public static String VALIDATE_ENROLLMENT_ID = "/validateEnrollmentToken";
  public static String ENROLL_ID = "/enroll";

  // Registration consent Server
  public static String UPDATE_ELIGIBILITY_CONSENT = "/updateEligibilityConsentStatus";
  public static String CONSENTPDF = "/consentDocument";

  // WCP server
  public static String STUDY_INFO = "/studyInfo";
  public static String CONSENT_METADATA = "/eligibilityConsent";
  public static String ACTIVITY = "/activity";
  public static String STUDY_LIST = "/studyList";
  public static String SPECIFIC_STUDY = "/study";
  public static String STUDY_UPDATES = "/studyUpdates";
  public static String ACTIVITY_LIST = "/activityList";
  public static String RESOURCE_LIST = "/resources";
  public static String NOTIFICATIONS = "/notifications";
  public static String DASHBOARD_INFO = "/studyDashboard";
  public static String GET_CONSENT_DOC = "/consentDocument";
  public static String VERSION_INFO = "/versionInfo";

  // Response server
  public static String PROCESS_RESPONSE = "/participant/process-response";
  public static String PROCESSRESPONSEDATA = BASE_URL_RESPONSE_SERVER + "/participant/getresponse?";
  public static String UPDATE_ACTIVITY_PREFERENCE = "/participant/update-activity-state";
  public static String ACTIVITY_STATE = "/participant/get-activity-state";
}