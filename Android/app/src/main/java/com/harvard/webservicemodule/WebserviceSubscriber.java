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
import com.harvard.webservicemodule.events.ParticipantDatastoreConfigEvent;
import com.harvard.webservicemodule.events.ParticipantConsentDatastoreConfigEvent;
import com.harvard.webservicemodule.events.ParticipantEnrollmentDatastoreConfigEvent;
import com.harvard.webservicemodule.events.ResponseDatastoreConfigEvent;
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

  public void onEvent(ParticipantDatastoreConfigEvent participantDatastoreConfigEvent) {
    String url = "";
    if (participantDatastoreConfigEvent
        .getContext()
        .getResources()
        .getString(R.string.app_stage)
        .equalsIgnoreCase("development")) {
      url =
          participantDatastoreConfigEvent.getDevelopmentUrl()
              + participantDatastoreConfigEvent.getUrl();
    } else {
      url =
          participantDatastoreConfigEvent.getProductionUrl() + participantDatastoreConfigEvent.getUrl();
    }
    url = url.replaceAll(" ", "%20");
    if (participantDatastoreConfigEvent.getRequestType().equalsIgnoreCase("get")) {
      ApiCall apiCall = new ApiCall(participantDatastoreConfigEvent.getContext());
      apiCall.apiCallGet(
          url,
          participantDatastoreConfigEvent.getHeaders(),
          participantDatastoreConfigEvent.getClassT(),
          participantDatastoreConfigEvent.getResponseCode(),
          participantDatastoreConfigEvent.getV(),
          participantDatastoreConfigEvent.isShowAlert(),
          "ParticipantDatastoreServer");
    } else if (participantDatastoreConfigEvent.getRequestType().equalsIgnoreCase("post_object")) {
      ApiCall apiCall = new ApiCall(participantDatastoreConfigEvent.getContext());
      apiCall.apiCallPostJson(
          url,
          participantDatastoreConfigEvent.getHeaders(),
          participantDatastoreConfigEvent.getClassT(),
          participantDatastoreConfigEvent.getRequestParamsJson(),
          participantDatastoreConfigEvent.getResponseCode(),
          participantDatastoreConfigEvent.getV(),
          participantDatastoreConfigEvent.isShowAlert(),
          "ParticipantDatastoreServer");
    } else if (participantDatastoreConfigEvent.getRequestType().equalsIgnoreCase("delete_object")) {
      ApiCall apiCall = new ApiCall(participantDatastoreConfigEvent.getContext());
      apiCall.apiCallDeleteJson(
          url,
          participantDatastoreConfigEvent.getHeaders(),
          participantDatastoreConfigEvent.getClassT(),
          participantDatastoreConfigEvent.getRequestParamsJson(),
          participantDatastoreConfigEvent.getResponseCode(),
          participantDatastoreConfigEvent.getV(),
          participantDatastoreConfigEvent.isShowAlert(),
          "ParticipantDatastoreServer");
    } else {
      ApiCall apiCall = new ApiCall(participantDatastoreConfigEvent.getContext());
      apiCall.apiCallPostHashmap(
          url,
          participantDatastoreConfigEvent.getHeaders(),
          participantDatastoreConfigEvent.getClassT(),
          participantDatastoreConfigEvent.getRequestParams(),
          participantDatastoreConfigEvent.getResponseCode(),
          participantDatastoreConfigEvent.getV(),
          participantDatastoreConfigEvent.isShowAlert(),
          "ParticipantDatastoreServer");
    }
  }

  public void onEvent(ParticipantConsentDatastoreConfigEvent participantConsentDatastoreConfigEvent) {
    String url = "";
    if (participantConsentDatastoreConfigEvent
        .getContext()
        .getResources()
        .getString(R.string.app_stage)
        .equalsIgnoreCase("development")) {
      url =
          participantConsentDatastoreConfigEvent.getDevelopmentUrl()
              + participantConsentDatastoreConfigEvent.getUrl();
    } else {
      url =
          participantConsentDatastoreConfigEvent.getProductionUrl()
              + participantConsentDatastoreConfigEvent.getUrl();
    }
    url = url.replaceAll(" ", "%20");
    if (participantConsentDatastoreConfigEvent.getRequestType().equalsIgnoreCase("get")) {
      ApiCall apiCall = new ApiCall(participantConsentDatastoreConfigEvent.getContext());
      apiCall.apiCallGet(
          url,
          participantConsentDatastoreConfigEvent.getHeaders(),
          participantConsentDatastoreConfigEvent.getClassT(),
          participantConsentDatastoreConfigEvent.getResponseCode(),
          participantConsentDatastoreConfigEvent.getV(),
          participantConsentDatastoreConfigEvent.isShowAlert(),
          "ParticipantConsentDatastore");
    } else if (participantConsentDatastoreConfigEvent
        .getRequestType()
        .equalsIgnoreCase("post_object")) {
      ApiCall apiCall = new ApiCall(participantConsentDatastoreConfigEvent.getContext());
      apiCall.apiCallPostJson(
          url,
          participantConsentDatastoreConfigEvent.getHeaders(),
          participantConsentDatastoreConfigEvent.getClassT(),
          participantConsentDatastoreConfigEvent.getRequestParamsJson(),
          participantConsentDatastoreConfigEvent.getResponseCode(),
          participantConsentDatastoreConfigEvent.getV(),
          participantConsentDatastoreConfigEvent.isShowAlert(),
          "ParticipantConsentDatastore");
    } else if (participantConsentDatastoreConfigEvent
        .getRequestType()
        .equalsIgnoreCase("delete_object")) {
      ApiCall apiCall = new ApiCall(participantConsentDatastoreConfigEvent.getContext());
      apiCall.apiCallDeleteJson(
          url,
          participantConsentDatastoreConfigEvent.getHeaders(),
          participantConsentDatastoreConfigEvent.getClassT(),
          participantConsentDatastoreConfigEvent.getRequestParamsJson(),
          participantConsentDatastoreConfigEvent.getResponseCode(),
          participantConsentDatastoreConfigEvent.getV(),
          participantConsentDatastoreConfigEvent.isShowAlert(),
          "ParticipantConsentDatastore");
    } else {
      ApiCall apiCall = new ApiCall(participantConsentDatastoreConfigEvent.getContext());
      apiCall.apiCallPostHashmap(
          url,
          participantConsentDatastoreConfigEvent.getHeaders(),
          participantConsentDatastoreConfigEvent.getClassT(),
          participantConsentDatastoreConfigEvent.getRequestParams(),
          participantConsentDatastoreConfigEvent.getResponseCode(),
          participantConsentDatastoreConfigEvent.getV(),
          participantConsentDatastoreConfigEvent.isShowAlert(),
          "ParticipantConsentDatastore");
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

  public void onEvent(ResponseDatastoreConfigEvent responseDatastoreConfigEvent) {
    String url = "";
    if (responseDatastoreConfigEvent
        .getContext()
        .getResources()
        .getString(R.string.app_stage)
        .equalsIgnoreCase("development")) {
      url = responseDatastoreConfigEvent.getDevelopmentUrl() + responseDatastoreConfigEvent.getUrl();
    } else {
      url = responseDatastoreConfigEvent.getProductionUrl() + responseDatastoreConfigEvent.getUrl();
    }
    url = url.replaceAll(" ", "%20");
    if (responseDatastoreConfigEvent.getRequestType().equalsIgnoreCase("get")) {
      ApiCall apiCall = new ApiCall(responseDatastoreConfigEvent.getContext());
      apiCall.apiCallGet(
          url,
          responseDatastoreConfigEvent.getHeaders(),
          responseDatastoreConfigEvent.getClassT(),
          responseDatastoreConfigEvent.getResponseCode(),
          responseDatastoreConfigEvent.getV(),
          responseDatastoreConfigEvent.isShowAlert(),
          "ResponseDatastore");
    } else if (responseDatastoreConfigEvent.getRequestType().equalsIgnoreCase("post_object")) {
      ApiCall apiCall = new ApiCall(responseDatastoreConfigEvent.getContext());
      apiCall.apiCallPostJson(
          url,
          responseDatastoreConfigEvent.getHeaders(),
          responseDatastoreConfigEvent.getClassT(),
          responseDatastoreConfigEvent.getRequestParamsJson(),
          responseDatastoreConfigEvent.getResponseCode(),
          responseDatastoreConfigEvent.getV(),
          responseDatastoreConfigEvent.isShowAlert(),
          "ResponseDatastore");
    } else {
      ApiCall apiCall = new ApiCall(responseDatastoreConfigEvent.getContext());
      apiCall.apiCallPostHashmap(
          url,
          responseDatastoreConfigEvent.getHeaders(),
          responseDatastoreConfigEvent.getClassT(),
          responseDatastoreConfigEvent.getRequestParams(),
          responseDatastoreConfigEvent.getResponseCode(),
          responseDatastoreConfigEvent.getV(),
          responseDatastoreConfigEvent.isShowAlert(),
          "ResponseDatastore");
    }
  }

  public void onEvent(
      ParticipantEnrollmentDatastoreConfigEvent participantEnrollmentDatastoreConfigEvent) {
    String url = "";
    if (participantEnrollmentDatastoreConfigEvent
        .getContext()
        .getResources()
        .getString(R.string.app_stage)
        .equalsIgnoreCase("development")) {
      url =
          participantEnrollmentDatastoreConfigEvent.getDevelopmentUrl()
              + participantEnrollmentDatastoreConfigEvent.getUrl();
    } else {
      url =
          participantEnrollmentDatastoreConfigEvent.getProductionUrl()
              + participantEnrollmentDatastoreConfigEvent.getUrl();
    }
    url = url.replaceAll(" ", "%20");
    if (participantEnrollmentDatastoreConfigEvent.getRequestType().equalsIgnoreCase("get")) {
      ApiCall apiCall = new ApiCall(participantEnrollmentDatastoreConfigEvent.getContext());
      apiCall.apiCallGet(
          url,
          participantEnrollmentDatastoreConfigEvent.getHeaders(),
          participantEnrollmentDatastoreConfigEvent.getClassT(),
          participantEnrollmentDatastoreConfigEvent.getResponseCode(),
          participantEnrollmentDatastoreConfigEvent.getV(),
          participantEnrollmentDatastoreConfigEvent.isShowAlert(),
          "ParticipantEnrollmentDatastore");
    } else if (participantEnrollmentDatastoreConfigEvent
        .getRequestType()
        .equalsIgnoreCase("post_object")) {
      ApiCall apiCall = new ApiCall(participantEnrollmentDatastoreConfigEvent.getContext());
      apiCall.apiCallPostJson(
          url,
          participantEnrollmentDatastoreConfigEvent.getHeaders(),
          participantEnrollmentDatastoreConfigEvent.getClassT(),
          participantEnrollmentDatastoreConfigEvent.getRequestParamsJson(),
          participantEnrollmentDatastoreConfigEvent.getResponseCode(),
          participantEnrollmentDatastoreConfigEvent.getV(),
          participantEnrollmentDatastoreConfigEvent.isShowAlert(),
          "ParticipantEnrollmentDatastore");
    } else if (participantEnrollmentDatastoreConfigEvent
        .getRequestType()
        .equalsIgnoreCase("delete_object")) {
      ApiCall apiCall = new ApiCall(participantEnrollmentDatastoreConfigEvent.getContext());
      apiCall.apiCallDeleteJson(
          url,
          participantEnrollmentDatastoreConfigEvent.getHeaders(),
          participantEnrollmentDatastoreConfigEvent.getClassT(),
          participantEnrollmentDatastoreConfigEvent.getRequestParamsJson(),
          participantEnrollmentDatastoreConfigEvent.getResponseCode(),
          participantEnrollmentDatastoreConfigEvent.getV(),
          participantEnrollmentDatastoreConfigEvent.isShowAlert(),
          "ParticipantEnrollmentDatastore");
    } else {
      ApiCall apiCall = new ApiCall(participantEnrollmentDatastoreConfigEvent.getContext());
      apiCall.apiCallPostHashmap(
          url,
          participantEnrollmentDatastoreConfigEvent.getHeaders(),
          participantEnrollmentDatastoreConfigEvent.getClassT(),
          participantEnrollmentDatastoreConfigEvent.getRequestParams(),
          participantEnrollmentDatastoreConfigEvent.getResponseCode(),
          participantEnrollmentDatastoreConfigEvent.getV(),
          participantEnrollmentDatastoreConfigEvent.isShowAlert(),
          "ParticipantEnrollmentDatastore");
    }
  }
}
