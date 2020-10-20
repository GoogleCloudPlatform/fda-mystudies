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

package com.harvard.webservicemodule;

import com.harvard.R;
import com.harvard.base.BaseSubscriber;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.events.AuthServerConfigEvent;
import com.harvard.webservicemodule.events.ParticipantDatastoreServerConfigEvent;
import com.harvard.webservicemodule.events.ParticipantDatastoreServerConsentConfigEvent;
import com.harvard.webservicemodule.events.ParticipantDatastoreServerEnrollmentConfigEvent;
import com.harvard.webservicemodule.events.ResponseDatastoreServerConfigEvent;
import com.harvard.webservicemodule.events.StudyDatastoreConfigEvent;

public class WebserviceSubscriber extends BaseSubscriber {
  public void onEvent(StudyDatastoreConfigEvent studyDatastoreConfigEvent) {
    String url = "";
    if (studyDatastoreConfigEvent
        .getContext()
        .getResources()
        .getString(R.string.app_stage)
        .equalsIgnoreCase("development")) {
      url = studyDatastoreConfigEvent.getDevelopmentUrl() + studyDatastoreConfigEvent.getUrl();
    } else {
      url = studyDatastoreConfigEvent.getProductionUrl() + studyDatastoreConfigEvent.getUrl();
    }
    url = url.replaceAll(" ", "%20");
    if (studyDatastoreConfigEvent.getRequestType().equalsIgnoreCase("get")) {
      ApiCall apiCall = new ApiCall(studyDatastoreConfigEvent.getContext());
      apiCall.apiCallGet(
          url,
              studyDatastoreConfigEvent.getHeaders(),
              studyDatastoreConfigEvent.getClassT(),
              studyDatastoreConfigEvent.getResponseCode(),
              studyDatastoreConfigEvent.getV(),
              studyDatastoreConfigEvent.isShowAlert(),
          "STUDY_DATASTORE");
    } else if (studyDatastoreConfigEvent.getRequestType().equalsIgnoreCase("post_object")) {
      ApiCall apiCall = new ApiCall(studyDatastoreConfigEvent.getContext());
      apiCall.apiCallPostJson(
          url,
              studyDatastoreConfigEvent.getHeaders(),
              studyDatastoreConfigEvent.getClassT(),
              studyDatastoreConfigEvent.getRequestParamsJson(),
              studyDatastoreConfigEvent.getResponseCode(),
              studyDatastoreConfigEvent.getV(),
              studyDatastoreConfigEvent.isShowAlert(),
          "STUDY_DATASTORE");
    } else if (studyDatastoreConfigEvent.getRequestType().equalsIgnoreCase("delete_object")) {
      ApiCall apiCall = new ApiCall(studyDatastoreConfigEvent.getContext());
      apiCall.apiCallDeleteJson(
          url,
              studyDatastoreConfigEvent.getHeaders(),
              studyDatastoreConfigEvent.getClassT(),
              studyDatastoreConfigEvent.getRequestParamsJson(),
              studyDatastoreConfigEvent.getResponseCode(),
          studyDatastoreConfigEvent.getV(),
          studyDatastoreConfigEvent.isShowAlert(),
          "STUDY_DATASTORE");
    } else {
      ApiCall apiCall = new ApiCall(studyDatastoreConfigEvent.getContext());
      apiCall.apiCallPostHashmap(
          url,
          studyDatastoreConfigEvent.getHeaders(),
          studyDatastoreConfigEvent.getClassT(),
          studyDatastoreConfigEvent.getRequestParams(),
          studyDatastoreConfigEvent.getResponseCode(),
          studyDatastoreConfigEvent.getV(),
          studyDatastoreConfigEvent.isShowAlert(),
          "STUDY_DATASTORE");
    }
  }

  public void onEvent(ParticipantDatastoreServerConfigEvent participantDatastoreServerConfigEvent) {
    String url = "";
    if (participantDatastoreServerConfigEvent
        .getContext()
        .getResources()
        .getString(R.string.app_stage)
        .equalsIgnoreCase("development")) {
      url =
          participantDatastoreServerConfigEvent.getDevelopmentUrl()
              + participantDatastoreServerConfigEvent.getUrl();
    } else {
      url =
          participantDatastoreServerConfigEvent.getProductionUrl() + participantDatastoreServerConfigEvent.getUrl();
    }
    url = url.replaceAll(" ", "%20");
    if (participantDatastoreServerConfigEvent.getRequestType().equalsIgnoreCase("get")) {
      ApiCall apiCall = new ApiCall(participantDatastoreServerConfigEvent.getContext());
      apiCall.apiCallGet(
          url,
          participantDatastoreServerConfigEvent.getHeaders(),
          participantDatastoreServerConfigEvent.getClassT(),
          participantDatastoreServerConfigEvent.getResponseCode(),
          participantDatastoreServerConfigEvent.getV(),
          participantDatastoreServerConfigEvent.isShowAlert(),
          "ParticipantDatastoreServer");
    } else if (participantDatastoreServerConfigEvent.getRequestType().equalsIgnoreCase("post_object")) {
      ApiCall apiCall = new ApiCall(participantDatastoreServerConfigEvent.getContext());
      apiCall.apiCallPostJson(
          url,
          participantDatastoreServerConfigEvent.getHeaders(),
          participantDatastoreServerConfigEvent.getClassT(),
          participantDatastoreServerConfigEvent.getRequestParamsJson(),
          participantDatastoreServerConfigEvent.getResponseCode(),
          participantDatastoreServerConfigEvent.getV(),
          participantDatastoreServerConfigEvent.isShowAlert(),
          "ParticipantDatastoreServer");
    } else if (participantDatastoreServerConfigEvent.getRequestType().equalsIgnoreCase("delete_object")) {
      ApiCall apiCall = new ApiCall(participantDatastoreServerConfigEvent.getContext());
      apiCall.apiCallDeleteJson(
          url,
          participantDatastoreServerConfigEvent.getHeaders(),
          participantDatastoreServerConfigEvent.getClassT(),
          participantDatastoreServerConfigEvent.getRequestParamsJson(),
          participantDatastoreServerConfigEvent.getResponseCode(),
          participantDatastoreServerConfigEvent.getV(),
          participantDatastoreServerConfigEvent.isShowAlert(),
          "ParticipantDatastoreServer");
    } else {
      ApiCall apiCall = new ApiCall(participantDatastoreServerConfigEvent.getContext());
      apiCall.apiCallPostHashmap(
          url,
          participantDatastoreServerConfigEvent.getHeaders(),
          participantDatastoreServerConfigEvent.getClassT(),
          participantDatastoreServerConfigEvent.getRequestParams(),
          participantDatastoreServerConfigEvent.getResponseCode(),
          participantDatastoreServerConfigEvent.getV(),
          participantDatastoreServerConfigEvent.isShowAlert(),
          "ParticipantDatastoreServer");
    }
  }

  public void onEvent(ParticipantDatastoreServerConsentConfigEvent participantDatastoreServerConsentConfigEvent) {
    String url = "";
    if (participantDatastoreServerConsentConfigEvent
        .getContext()
        .getResources()
        .getString(R.string.app_stage)
        .equalsIgnoreCase("development")) {
      url =
          participantDatastoreServerConsentConfigEvent.getDevelopmentUrl()
              + participantDatastoreServerConsentConfigEvent.getUrl();
    } else {
      url =
          participantDatastoreServerConsentConfigEvent.getProductionUrl()
              + participantDatastoreServerConsentConfigEvent.getUrl();
    }
    url = url.replaceAll(" ", "%20");
    if (participantDatastoreServerConsentConfigEvent.getRequestType().equalsIgnoreCase("get")) {
      ApiCall apiCall = new ApiCall(participantDatastoreServerConsentConfigEvent.getContext());
      apiCall.apiCallGet(
          url,
          participantDatastoreServerConsentConfigEvent.getHeaders(),
          participantDatastoreServerConsentConfigEvent.getClassT(),
          participantDatastoreServerConsentConfigEvent.getResponseCode(),
          participantDatastoreServerConsentConfigEvent.getV(),
          participantDatastoreServerConsentConfigEvent.isShowAlert(),
          "ParticipantDatastoreServerConsent");
    } else if (participantDatastoreServerConsentConfigEvent
        .getRequestType()
        .equalsIgnoreCase("post_object")) {
      ApiCall apiCall = new ApiCall(participantDatastoreServerConsentConfigEvent.getContext());
      apiCall.apiCallPostJson(
          url,
          participantDatastoreServerConsentConfigEvent.getHeaders(),
          participantDatastoreServerConsentConfigEvent.getClassT(),
          participantDatastoreServerConsentConfigEvent.getRequestParamsJson(),
          participantDatastoreServerConsentConfigEvent.getResponseCode(),
          participantDatastoreServerConsentConfigEvent.getV(),
          participantDatastoreServerConsentConfigEvent.isShowAlert(),
          "ParticipantDatastoreServerConsent");
    } else if (participantDatastoreServerConsentConfigEvent
        .getRequestType()
        .equalsIgnoreCase("delete_object")) {
      ApiCall apiCall = new ApiCall(participantDatastoreServerConsentConfigEvent.getContext());
      apiCall.apiCallDeleteJson(
          url,
          participantDatastoreServerConsentConfigEvent.getHeaders(),
          participantDatastoreServerConsentConfigEvent.getClassT(),
          participantDatastoreServerConsentConfigEvent.getRequestParamsJson(),
          participantDatastoreServerConsentConfigEvent.getResponseCode(),
          participantDatastoreServerConsentConfigEvent.getV(),
          participantDatastoreServerConsentConfigEvent.isShowAlert(),
          "ParticipantDatastoreServerConsent");
    } else {
      ApiCall apiCall = new ApiCall(participantDatastoreServerConsentConfigEvent.getContext());
      apiCall.apiCallPostHashmap(
          url,
          participantDatastoreServerConsentConfigEvent.getHeaders(),
          participantDatastoreServerConsentConfigEvent.getClassT(),
          participantDatastoreServerConsentConfigEvent.getRequestParams(),
          participantDatastoreServerConsentConfigEvent.getResponseCode(),
          participantDatastoreServerConsentConfigEvent.getV(),
          participantDatastoreServerConsentConfigEvent.isShowAlert(),
          "ParticipantDatastoreServerConsent");
    }
  }

  public void onEvent(AuthServerConfigEvent authServerConfigEvent) {
    String url = "";
    if (authServerConfigEvent
        .getContext()
        .getResources()
        .getString(R.string.app_stage)
        .equalsIgnoreCase("development")) {
      url = authServerConfigEvent.getDevelopmentUrl() + authServerConfigEvent.getUrl();
    } else {
      url = authServerConfigEvent.getProductionUrl() + authServerConfigEvent.getUrl();
    }
    url = url.replaceAll(" ", "%20");
    if (authServerConfigEvent.getRequestType().equalsIgnoreCase("get")) {
      ApiCall apiCall = new ApiCall(authServerConfigEvent.getContext());
      apiCall.apiCallGet(
          url,
          authServerConfigEvent.getHeaders(),
          authServerConfigEvent.getClassT(),
          authServerConfigEvent.getResponseCode(),
          authServerConfigEvent.getV(),
          authServerConfigEvent.isShowAlert(),
          "AuthServer");
    } else if (authServerConfigEvent.getRequestType().equalsIgnoreCase("post_object")) {
      ApiCall apiCall = new ApiCall(authServerConfigEvent.getContext());
      apiCall.apiCallPostJson(
          url,
          authServerConfigEvent.getHeaders(),
          authServerConfigEvent.getClassT(),
          authServerConfigEvent.getRequestParamsJson(),
          authServerConfigEvent.getResponseCode(),
          authServerConfigEvent.getV(),
          authServerConfigEvent.isShowAlert(),
          "AuthServer");
    } else if (authServerConfigEvent.getRequestType().equalsIgnoreCase("delete_object")) {
      ApiCall apiCall = new ApiCall(authServerConfigEvent.getContext());
      apiCall.apiCallDeleteJson(
          url,
          authServerConfigEvent.getHeaders(),
          authServerConfigEvent.getClassT(),
          authServerConfigEvent.getRequestParamsJson(),
          authServerConfigEvent.getResponseCode(),
          authServerConfigEvent.getV(),
          authServerConfigEvent.isShowAlert(),
          "AuthServer");
    } else if (authServerConfigEvent.getRequestType().equalsIgnoreCase("put")) {
      ApiCall apiCall = new ApiCall(authServerConfigEvent.getContext());
      apiCall.apiCallPutJson(
          url,
          authServerConfigEvent.getHeaders(),
          authServerConfigEvent.getClassT(),
          authServerConfigEvent.getRequestParamsJson(),
          authServerConfigEvent.getResponseCode(),
          authServerConfigEvent.getV(),
          authServerConfigEvent.isShowAlert(),
          "AuthServer");
    } else {
      ApiCall apiCall = new ApiCall(authServerConfigEvent.getContext());
      apiCall.apiCallPostHashmap(
          url,
          authServerConfigEvent.getHeaders(),
          authServerConfigEvent.getClassT(),
          authServerConfigEvent.getRequestParams(),
          authServerConfigEvent.getResponseCode(),
          authServerConfigEvent.getV(),
          authServerConfigEvent.isShowAlert(),
          "AuthServer");
    }
  }

  public void onEvent(ResponseDatastoreServerConfigEvent responseDatastoreServerConfigEvent) {
    String url = "";
    if (responseDatastoreServerConfigEvent
        .getContext()
        .getResources()
        .getString(R.string.app_stage)
        .equalsIgnoreCase("development")) {
      url = responseDatastoreServerConfigEvent.getDevelopmentUrl() + responseDatastoreServerConfigEvent.getUrl();
    } else {
      url = responseDatastoreServerConfigEvent.getProductionUrl() + responseDatastoreServerConfigEvent.getUrl();
    }
    url = url.replaceAll(" ", "%20");
    if (responseDatastoreServerConfigEvent.getRequestType().equalsIgnoreCase("get")) {
      ApiCall apiCall = new ApiCall(responseDatastoreServerConfigEvent.getContext());
      apiCall.apiCallGet(
          url,
          responseDatastoreServerConfigEvent.getHeaders(),
          responseDatastoreServerConfigEvent.getClassT(),
          responseDatastoreServerConfigEvent.getResponseCode(),
          responseDatastoreServerConfigEvent.getV(),
          responseDatastoreServerConfigEvent.isShowAlert(),
          "ResponseDatastoreServer");
    } else if (responseDatastoreServerConfigEvent.getRequestType().equalsIgnoreCase("post_object")) {
      ApiCall apiCall = new ApiCall(responseDatastoreServerConfigEvent.getContext());
      apiCall.apiCallPostJson(
          url,
          responseDatastoreServerConfigEvent.getHeaders(),
          responseDatastoreServerConfigEvent.getClassT(),
          responseDatastoreServerConfigEvent.getRequestParamsJson(),
          responseDatastoreServerConfigEvent.getResponseCode(),
          responseDatastoreServerConfigEvent.getV(),
          responseDatastoreServerConfigEvent.isShowAlert(),
          "ResponseDatastoreServer");
    } else {
      ApiCall apiCall = new ApiCall(responseDatastoreServerConfigEvent.getContext());
      apiCall.apiCallPostHashmap(
          url,
          responseDatastoreServerConfigEvent.getHeaders(),
          responseDatastoreServerConfigEvent.getClassT(),
          responseDatastoreServerConfigEvent.getRequestParams(),
          responseDatastoreServerConfigEvent.getResponseCode(),
          responseDatastoreServerConfigEvent.getV(),
          responseDatastoreServerConfigEvent.isShowAlert(),
          "ResponseDatastoreServer");
    }
  }

  public void onEvent(
      ParticipantDatastoreServerEnrollmentConfigEvent participantDatastoreServerEnrollmentConfigEvent) {
    String url = "";
    if (participantDatastoreServerEnrollmentConfigEvent
        .getContext()
        .getResources()
        .getString(R.string.app_stage)
        .equalsIgnoreCase("development")) {
      url =
          participantDatastoreServerEnrollmentConfigEvent.getDevelopmentUrl()
              + participantDatastoreServerEnrollmentConfigEvent.getUrl();
    } else {
      url =
          participantDatastoreServerEnrollmentConfigEvent.getProductionUrl()
              + participantDatastoreServerEnrollmentConfigEvent.getUrl();
    }
    url = url.replaceAll(" ", "%20");
    if (participantDatastoreServerEnrollmentConfigEvent.getRequestType().equalsIgnoreCase("get")) {
      ApiCall apiCall = new ApiCall(participantDatastoreServerEnrollmentConfigEvent.getContext());
      apiCall.apiCallGet(
          url,
          participantDatastoreServerEnrollmentConfigEvent.getHeaders(),
          participantDatastoreServerEnrollmentConfigEvent.getClassT(),
          participantDatastoreServerEnrollmentConfigEvent.getResponseCode(),
          participantDatastoreServerEnrollmentConfigEvent.getV(),
          participantDatastoreServerEnrollmentConfigEvent.isShowAlert(),
          "ParticipantDatastoreServerEnrollment");
    } else if (participantDatastoreServerEnrollmentConfigEvent
        .getRequestType()
        .equalsIgnoreCase("post_object")) {
      ApiCall apiCall = new ApiCall(participantDatastoreServerEnrollmentConfigEvent.getContext());
      apiCall.apiCallPostJson(
          url,
          participantDatastoreServerEnrollmentConfigEvent.getHeaders(),
          participantDatastoreServerEnrollmentConfigEvent.getClassT(),
          participantDatastoreServerEnrollmentConfigEvent.getRequestParamsJson(),
          participantDatastoreServerEnrollmentConfigEvent.getResponseCode(),
          participantDatastoreServerEnrollmentConfigEvent.getV(),
          participantDatastoreServerEnrollmentConfigEvent.isShowAlert(),
          "ParticipantDatastoreServerEnrollment");
    } else if (participantDatastoreServerEnrollmentConfigEvent
        .getRequestType()
        .equalsIgnoreCase("delete_object")) {
      ApiCall apiCall = new ApiCall(participantDatastoreServerEnrollmentConfigEvent.getContext());
      apiCall.apiCallDeleteJson(
          url,
          participantDatastoreServerEnrollmentConfigEvent.getHeaders(),
          participantDatastoreServerEnrollmentConfigEvent.getClassT(),
          participantDatastoreServerEnrollmentConfigEvent.getRequestParamsJson(),
          participantDatastoreServerEnrollmentConfigEvent.getResponseCode(),
          participantDatastoreServerEnrollmentConfigEvent.getV(),
          participantDatastoreServerEnrollmentConfigEvent.isShowAlert(),
          "ParticipantDatastoreServerEnrollment");
    } else {
      ApiCall apiCall = new ApiCall(participantDatastoreServerEnrollmentConfigEvent.getContext());
      apiCall.apiCallPostHashmap(
          url,
          participantDatastoreServerEnrollmentConfigEvent.getHeaders(),
          participantDatastoreServerEnrollmentConfigEvent.getClassT(),
          participantDatastoreServerEnrollmentConfigEvent.getRequestParams(),
          participantDatastoreServerEnrollmentConfigEvent.getResponseCode(),
          participantDatastoreServerEnrollmentConfigEvent.getV(),
          participantDatastoreServerEnrollmentConfigEvent.isShowAlert(),
          "ParticipantDatastoreServerEnrollment");
    }
  }
}
