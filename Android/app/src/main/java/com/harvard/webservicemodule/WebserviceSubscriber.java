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
import com.harvard.webservicemodule.events.RegistrationServerConfigEvent;
import com.harvard.webservicemodule.events.RegistrationServerConsentConfigEvent;
import com.harvard.webservicemodule.events.RegistrationServerEnrollmentConfigEvent;
import com.harvard.webservicemodule.events.ResponseServerConfigEvent;
import com.harvard.webservicemodule.events.WcpConfigEvent;

public class WebserviceSubscriber extends BaseSubscriber {
  public void onEvent(WcpConfigEvent wcpConfigEvent) {
    String url = "";
    if (wcpConfigEvent
        .getContext()
        .getResources()
        .getString(R.string.app_stage)
        .equalsIgnoreCase("development")) {
      url = wcpConfigEvent.getDevelopmentUrl() + wcpConfigEvent.getUrl();
    } else {
      url = wcpConfigEvent.getProductionUrl() + wcpConfigEvent.getUrl();
    }
    url = url.replaceAll(" ", "%20");
    if (wcpConfigEvent.getRequestType().equalsIgnoreCase("get")) {
      ApiCall apiCall = new ApiCall(wcpConfigEvent.getContext());
      apiCall.apiCallGet(
          url,
          wcpConfigEvent.getHeaders(),
          wcpConfigEvent.getClassT(),
          wcpConfigEvent.getResponseCode(),
          wcpConfigEvent.getV(),
          wcpConfigEvent.isShowAlert(),
          "WCP");
    } else if (wcpConfigEvent.getRequestType().equalsIgnoreCase("post_object")) {
      ApiCall apiCall = new ApiCall(wcpConfigEvent.getContext());
      apiCall.apiCallPostJson(
          url,
          wcpConfigEvent.getHeaders(),
          wcpConfigEvent.getClassT(),
          wcpConfigEvent.getRequestParamsJson(),
          wcpConfigEvent.getResponseCode(),
          wcpConfigEvent.getV(),
          wcpConfigEvent.isShowAlert(),
          "WCP");
    } else if (wcpConfigEvent.getRequestType().equalsIgnoreCase("delete_object")) {
      ApiCall apiCall = new ApiCall(wcpConfigEvent.getContext());
      apiCall.apiCallDeleteJson(
          url,
          wcpConfigEvent.getHeaders(),
          wcpConfigEvent.getClassT(),
          wcpConfigEvent.getRequestParamsJson(),
          wcpConfigEvent.getResponseCode(),
          wcpConfigEvent.getV(),
          wcpConfigEvent.isShowAlert(),
          "WCP");
    } else {
      ApiCall apiCall = new ApiCall(wcpConfigEvent.getContext());
      apiCall.apiCallPostHashmap(
          url,
          wcpConfigEvent.getHeaders(),
          wcpConfigEvent.getClassT(),
          wcpConfigEvent.getRequestParams(),
          wcpConfigEvent.getResponseCode(),
          wcpConfigEvent.getV(),
          wcpConfigEvent.isShowAlert(),
          "WCP");
    }
  }

  public void onEvent(RegistrationServerConfigEvent registrationServerConfigEvent) {
    String url = "";
    if (registrationServerConfigEvent
        .getContext()
        .getResources()
        .getString(R.string.app_stage)
        .equalsIgnoreCase("development")) {
      url =
          registrationServerConfigEvent.getDevelopmentUrl()
              + registrationServerConfigEvent.getUrl();
    } else {
      url =
          registrationServerConfigEvent.getProductionUrl() + registrationServerConfigEvent.getUrl();
    }
    url = url.replaceAll(" ", "%20");
    if (registrationServerConfigEvent.getRequestType().equalsIgnoreCase("get")) {
      ApiCall apiCall = new ApiCall(registrationServerConfigEvent.getContext());
      apiCall.apiCallGet(
          url,
          registrationServerConfigEvent.getHeaders(),
          registrationServerConfigEvent.getClassT(),
          registrationServerConfigEvent.getResponseCode(),
          registrationServerConfigEvent.getV(),
          registrationServerConfigEvent.isShowAlert(),
          "RegistrationServer");
    } else if (registrationServerConfigEvent.getRequestType().equalsIgnoreCase("post_object")) {
      ApiCall apiCall = new ApiCall(registrationServerConfigEvent.getContext());
      apiCall.apiCallPostJson(
          url,
          registrationServerConfigEvent.getHeaders(),
          registrationServerConfigEvent.getClassT(),
          registrationServerConfigEvent.getRequestParamsJson(),
          registrationServerConfigEvent.getResponseCode(),
          registrationServerConfigEvent.getV(),
          registrationServerConfigEvent.isShowAlert(),
          "RegistrationServer");
    } else if (registrationServerConfigEvent.getRequestType().equalsIgnoreCase("delete_object")) {
      ApiCall apiCall = new ApiCall(registrationServerConfigEvent.getContext());
      apiCall.apiCallDeleteJson(
          url,
          registrationServerConfigEvent.getHeaders(),
          registrationServerConfigEvent.getClassT(),
          registrationServerConfigEvent.getRequestParamsJson(),
          registrationServerConfigEvent.getResponseCode(),
          registrationServerConfigEvent.getV(),
          registrationServerConfigEvent.isShowAlert(),
          "RegistrationServer");
    } else {
      ApiCall apiCall = new ApiCall(registrationServerConfigEvent.getContext());
      apiCall.apiCallPostHashmap(
          url,
          registrationServerConfigEvent.getHeaders(),
          registrationServerConfigEvent.getClassT(),
          registrationServerConfigEvent.getRequestParams(),
          registrationServerConfigEvent.getResponseCode(),
          registrationServerConfigEvent.getV(),
          registrationServerConfigEvent.isShowAlert(),
          "RegistrationServer");
    }
  }

  public void onEvent(RegistrationServerConsentConfigEvent registrationServerConsentConfigEvent) {
    String url = "";
    if (registrationServerConsentConfigEvent
        .getContext()
        .getResources()
        .getString(R.string.app_stage)
        .equalsIgnoreCase("development")) {
      url =
          registrationServerConsentConfigEvent.getDevelopmentUrl()
              + registrationServerConsentConfigEvent.getUrl();
    } else {
      url =
          registrationServerConsentConfigEvent.getProductionUrl()
              + registrationServerConsentConfigEvent.getUrl();
    }
    url = url.replaceAll(" ", "%20");
    if (registrationServerConsentConfigEvent.getRequestType().equalsIgnoreCase("get")) {
      ApiCall apiCall = new ApiCall(registrationServerConsentConfigEvent.getContext());
      apiCall.apiCallGet(
          url,
          registrationServerConsentConfigEvent.getHeaders(),
          registrationServerConsentConfigEvent.getClassT(),
          registrationServerConsentConfigEvent.getResponseCode(),
          registrationServerConsentConfigEvent.getV(),
          registrationServerConsentConfigEvent.isShowAlert(),
          "RegistrationServerConsent");
    } else if (registrationServerConsentConfigEvent
        .getRequestType()
        .equalsIgnoreCase("post_object")) {
      ApiCall apiCall = new ApiCall(registrationServerConsentConfigEvent.getContext());
      apiCall.apiCallPostJson(
          url,
          registrationServerConsentConfigEvent.getHeaders(),
          registrationServerConsentConfigEvent.getClassT(),
          registrationServerConsentConfigEvent.getRequestParamsJson(),
          registrationServerConsentConfigEvent.getResponseCode(),
          registrationServerConsentConfigEvent.getV(),
          registrationServerConsentConfigEvent.isShowAlert(),
          "RegistrationServerConsent");
    } else if (registrationServerConsentConfigEvent
        .getRequestType()
        .equalsIgnoreCase("delete_object")) {
      ApiCall apiCall = new ApiCall(registrationServerConsentConfigEvent.getContext());
      apiCall.apiCallDeleteJson(
          url,
          registrationServerConsentConfigEvent.getHeaders(),
          registrationServerConsentConfigEvent.getClassT(),
          registrationServerConsentConfigEvent.getRequestParamsJson(),
          registrationServerConsentConfigEvent.getResponseCode(),
          registrationServerConsentConfigEvent.getV(),
          registrationServerConsentConfigEvent.isShowAlert(),
          "RegistrationServerConsent");
    } else {
      ApiCall apiCall = new ApiCall(registrationServerConsentConfigEvent.getContext());
      apiCall.apiCallPostHashmap(
          url,
          registrationServerConsentConfigEvent.getHeaders(),
          registrationServerConsentConfigEvent.getClassT(),
          registrationServerConsentConfigEvent.getRequestParams(),
          registrationServerConsentConfigEvent.getResponseCode(),
          registrationServerConsentConfigEvent.getV(),
          registrationServerConsentConfigEvent.isShowAlert(),
          "RegistrationServerConsent");
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

  public void onEvent(ResponseServerConfigEvent responseServerConfigEvent) {
    String url = "";
    if (responseServerConfigEvent
        .getContext()
        .getResources()
        .getString(R.string.app_stage)
        .equalsIgnoreCase("development")) {
      url = responseServerConfigEvent.getDevelopmentUrl() + responseServerConfigEvent.getUrl();
    } else {
      url = responseServerConfigEvent.getProductionUrl() + responseServerConfigEvent.getUrl();
    }
    url = url.replaceAll(" ", "%20");
    if (responseServerConfigEvent.getRequestType().equalsIgnoreCase("get")) {
      ApiCall apiCall = new ApiCall(responseServerConfigEvent.getContext());
      apiCall.apiCallGet(
          url,
          responseServerConfigEvent.getHeaders(),
          responseServerConfigEvent.getClassT(),
          responseServerConfigEvent.getResponseCode(),
          responseServerConfigEvent.getV(),
          responseServerConfigEvent.isShowAlert(),
          "ResponseServer");
    } else if (responseServerConfigEvent.getRequestType().equalsIgnoreCase("post_object")) {
      ApiCall apiCall = new ApiCall(responseServerConfigEvent.getContext());
      apiCall.apiCallPostJson(
          url,
          responseServerConfigEvent.getHeaders(),
          responseServerConfigEvent.getClassT(),
          responseServerConfigEvent.getRequestParamsJson(),
          responseServerConfigEvent.getResponseCode(),
          responseServerConfigEvent.getV(),
          responseServerConfigEvent.isShowAlert(),
          "ResponseServer");
    } else {
      ApiCall apiCall = new ApiCall(responseServerConfigEvent.getContext());
      apiCall.apiCallPostHashmap(
          url,
          responseServerConfigEvent.getHeaders(),
          responseServerConfigEvent.getClassT(),
          responseServerConfigEvent.getRequestParams(),
          responseServerConfigEvent.getResponseCode(),
          responseServerConfigEvent.getV(),
          responseServerConfigEvent.isShowAlert(),
          "ResponseServer");
    }
  }

  public void onEvent(
      RegistrationServerEnrollmentConfigEvent registrationServerEnrollmentConfigEvent) {
    String url = "";
    if (registrationServerEnrollmentConfigEvent
        .getContext()
        .getResources()
        .getString(R.string.app_stage)
        .equalsIgnoreCase("development")) {
      url =
          registrationServerEnrollmentConfigEvent.getDevelopmentUrl()
              + registrationServerEnrollmentConfigEvent.getUrl();
    } else {
      url =
          registrationServerEnrollmentConfigEvent.getProductionUrl()
              + registrationServerEnrollmentConfigEvent.getUrl();
    }
    url = url.replaceAll(" ", "%20");
    if (registrationServerEnrollmentConfigEvent.getRequestType().equalsIgnoreCase("get")) {
      ApiCall apiCall = new ApiCall(registrationServerEnrollmentConfigEvent.getContext());
      apiCall.apiCallGet(
          url,
          registrationServerEnrollmentConfigEvent.getHeaders(),
          registrationServerEnrollmentConfigEvent.getClassT(),
          registrationServerEnrollmentConfigEvent.getResponseCode(),
          registrationServerEnrollmentConfigEvent.getV(),
          registrationServerEnrollmentConfigEvent.isShowAlert(),
          "RegistrationServerEnrollment");
    } else if (registrationServerEnrollmentConfigEvent
        .getRequestType()
        .equalsIgnoreCase("post_object")) {
      ApiCall apiCall = new ApiCall(registrationServerEnrollmentConfigEvent.getContext());
      apiCall.apiCallPostJson(
          url,
          registrationServerEnrollmentConfigEvent.getHeaders(),
          registrationServerEnrollmentConfigEvent.getClassT(),
          registrationServerEnrollmentConfigEvent.getRequestParamsJson(),
          registrationServerEnrollmentConfigEvent.getResponseCode(),
          registrationServerEnrollmentConfigEvent.getV(),
          registrationServerEnrollmentConfigEvent.isShowAlert(),
          "RegistrationServerEnrollment");
    } else if (registrationServerEnrollmentConfigEvent
        .getRequestType()
        .equalsIgnoreCase("delete_object")) {
      ApiCall apiCall = new ApiCall(registrationServerEnrollmentConfigEvent.getContext());
      apiCall.apiCallDeleteJson(
          url,
          registrationServerEnrollmentConfigEvent.getHeaders(),
          registrationServerEnrollmentConfigEvent.getClassT(),
          registrationServerEnrollmentConfigEvent.getRequestParamsJson(),
          registrationServerEnrollmentConfigEvent.getResponseCode(),
          registrationServerEnrollmentConfigEvent.getV(),
          registrationServerEnrollmentConfigEvent.isShowAlert(),
          "RegistrationServerEnrollment");
    } else {
      ApiCall apiCall = new ApiCall(registrationServerEnrollmentConfigEvent.getContext());
      apiCall.apiCallPostHashmap(
          url,
          registrationServerEnrollmentConfigEvent.getHeaders(),
          registrationServerEnrollmentConfigEvent.getClassT(),
          registrationServerEnrollmentConfigEvent.getRequestParams(),
          registrationServerEnrollmentConfigEvent.getResponseCode(),
          registrationServerEnrollmentConfigEvent.getV(),
          registrationServerEnrollmentConfigEvent.isShowAlert(),
          "RegistrationServerEnrollment");
    }
  }
}
