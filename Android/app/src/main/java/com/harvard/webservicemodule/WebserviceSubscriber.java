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
import com.harvard.webservicemodule.events.WCPConfigEvent;

public class WebserviceSubscriber extends BaseSubscriber {
  public void onEvent(WCPConfigEvent wcpConfigEvent) {
    String url = "";
    if (wcpConfigEvent
        .getmContext()
        .getResources()
        .getString(R.string.app_stage)
        .equalsIgnoreCase("development")) {
      url = wcpConfigEvent.getDevelopmentUrl() + wcpConfigEvent.getmUrl();
    } else {
      url = wcpConfigEvent.getProductionUrl() + wcpConfigEvent.getmUrl();
    }
    url = url.replaceAll(" ", "%20");
    if (wcpConfigEvent.getmRequestType().equalsIgnoreCase("get")) {
      ApiCall apiCall = new ApiCall(wcpConfigEvent.getmContext());
      apiCall.apiCallGet(
          url,
          wcpConfigEvent.getmHeaders(),
          wcpConfigEvent.gettClass(),
          wcpConfigEvent.getmResponseCode(),
          wcpConfigEvent.getV(),
          wcpConfigEvent.ismShowAlert(),
          "WCP");
    } else if (wcpConfigEvent.getmRequestType().equalsIgnoreCase("post_object")) {
      ApiCall apiCall = new ApiCall(wcpConfigEvent.getmContext());
      apiCall.apiCallPostJson(
          url,
          wcpConfigEvent.getmHeaders(),
          wcpConfigEvent.gettClass(),
          wcpConfigEvent.getmRequestParamsJson(),
          wcpConfigEvent.getmResponseCode(),
          wcpConfigEvent.getV(),
          wcpConfigEvent.ismShowAlert(),
          "WCP");
    } else if (wcpConfigEvent.getmRequestType().equalsIgnoreCase("delete")) {
      ApiCall apiCall = new ApiCall(wcpConfigEvent.getmContext());
      apiCall.apiCallDeleteHashmap(
          url,
          wcpConfigEvent.getmHeaders(),
          wcpConfigEvent.gettClass(),
          wcpConfigEvent.getmRequestParams(),
          wcpConfigEvent.getmResponseCode(),
          wcpConfigEvent.getV(),
          wcpConfigEvent.ismShowAlert(),
          "WCP");
    } else if (wcpConfigEvent.getmRequestType().equalsIgnoreCase("delete_object")) {
      ApiCall apiCall = new ApiCall(wcpConfigEvent.getmContext());
      apiCall.apiCallDeleteJson(
          url,
          wcpConfigEvent.getmHeaders(),
          wcpConfigEvent.gettClass(),
          wcpConfigEvent.getmRequestParamsJson(),
          wcpConfigEvent.getmResponseCode(),
          wcpConfigEvent.getV(),
          wcpConfigEvent.ismShowAlert(),
          "WCP");
    } else {
      ApiCall apiCall = new ApiCall(wcpConfigEvent.getmContext());
      apiCall.apiCallPostHashmap(
          url,
          wcpConfigEvent.getmHeaders(),
          wcpConfigEvent.gettClass(),
          wcpConfigEvent.getmRequestParams(),
          wcpConfigEvent.getmResponseCode(),
          wcpConfigEvent.getV(),
          wcpConfigEvent.ismShowAlert(),
          "WCP");
    }
  }

  public void onEvent(RegistrationServerConfigEvent registrationServerConfigEvent) {
    String url = "";
    if (registrationServerConfigEvent
        .getmContext()
        .getResources()
        .getString(R.string.app_stage)
        .equalsIgnoreCase("development")) {
      url =
          registrationServerConfigEvent.getDevelopmentUrl()
              + registrationServerConfigEvent.getmUrl();
    } else {
      url =
          registrationServerConfigEvent.getProductionUrl()
              + registrationServerConfigEvent.getmUrl();
    }
    url = url.replaceAll(" ", "%20");
    if (registrationServerConfigEvent.getmRequestType().equalsIgnoreCase("get")) {
      ApiCall apiCall = new ApiCall(registrationServerConfigEvent.getmContext());
      apiCall.apiCallGet(
          url,
          registrationServerConfigEvent.getmHeaders(),
          registrationServerConfigEvent.gettClass(),
          registrationServerConfigEvent.getmResponseCode(),
          registrationServerConfigEvent.getV(),
          registrationServerConfigEvent.ismShowAlert(),
          "RegistrationServer");
    } else if (registrationServerConfigEvent.getmRequestType().equalsIgnoreCase("post_object")) {
      ApiCall apiCall = new ApiCall(registrationServerConfigEvent.getmContext());
      apiCall.apiCallPostJson(
          url,
          registrationServerConfigEvent.getmHeaders(),
          registrationServerConfigEvent.gettClass(),
          registrationServerConfigEvent.getmRequestParamsJson(),
          registrationServerConfigEvent.getmResponseCode(),
          registrationServerConfigEvent.getV(),
          registrationServerConfigEvent.ismShowAlert(),
          "RegistrationServer");
    } else if (registrationServerConfigEvent.getmRequestType().equalsIgnoreCase("delete")) {
      ApiCall apiCall = new ApiCall(registrationServerConfigEvent.getmContext());
      apiCall.apiCallDeleteHashmap(
          url,
          registrationServerConfigEvent.getmHeaders(),
          registrationServerConfigEvent.gettClass(),
          registrationServerConfigEvent.getmRequestParams(),
          registrationServerConfigEvent.getmResponseCode(),
          registrationServerConfigEvent.getV(),
          registrationServerConfigEvent.ismShowAlert(),
          "RegistrationServer");
    } else if (registrationServerConfigEvent.getmRequestType().equalsIgnoreCase("delete_object")) {
      ApiCall apiCall = new ApiCall(registrationServerConfigEvent.getmContext());
      apiCall.apiCallDeleteJson(
          url,
          registrationServerConfigEvent.getmHeaders(),
          registrationServerConfigEvent.gettClass(),
          registrationServerConfigEvent.getmRequestParamsJson(),
          registrationServerConfigEvent.getmResponseCode(),
          registrationServerConfigEvent.getV(),
          registrationServerConfigEvent.ismShowAlert(),
          "RegistrationServer");
    } else if (registrationServerConfigEvent.getmRequestType().equalsIgnoreCase("delete_array")) {
      ApiCall apiCall = new ApiCall(registrationServerConfigEvent.getmContext());
      apiCall.apiCallDeleteJsonArray(
          url,
          registrationServerConfigEvent.getmHeaders(),
          registrationServerConfigEvent.gettClass(),
          registrationServerConfigEvent.getmRequestParamsJsonArray(),
          registrationServerConfigEvent.getmResponseCode(),
          registrationServerConfigEvent.getV(),
          registrationServerConfigEvent.ismShowAlert(),
          "RegistrationServer");
    } else {
      ApiCall apiCall = new ApiCall(registrationServerConfigEvent.getmContext());
      apiCall.apiCallPostHashmap(
          url,
          registrationServerConfigEvent.getmHeaders(),
          registrationServerConfigEvent.gettClass(),
          registrationServerConfigEvent.getmRequestParams(),
          registrationServerConfigEvent.getmResponseCode(),
          registrationServerConfigEvent.getV(),
          registrationServerConfigEvent.ismShowAlert(),
          "RegistrationServer");
    }
  }

  public void onEvent(RegistrationServerConsentConfigEvent registrationServerConsentConfigEvent) {
    String url = "";
    if (registrationServerConsentConfigEvent
        .getmContext()
        .getResources()
        .getString(R.string.app_stage)
        .equalsIgnoreCase("development")) {
      url =
          registrationServerConsentConfigEvent.getDevelopmentUrl()
              + registrationServerConsentConfigEvent.getmUrl();
    } else {
      url =
          registrationServerConsentConfigEvent.getProductionUrl()
              + registrationServerConsentConfigEvent.getmUrl();
    }
    url = url.replaceAll(" ", "%20");
    if (registrationServerConsentConfigEvent.getmRequestType().equalsIgnoreCase("get")) {
      ApiCall apiCall = new ApiCall(registrationServerConsentConfigEvent.getmContext());
      apiCall.apiCallGet(
          url,
          registrationServerConsentConfigEvent.getmHeaders(),
          registrationServerConsentConfigEvent.gettClass(),
          registrationServerConsentConfigEvent.getmResponseCode(),
          registrationServerConsentConfigEvent.getV(),
          registrationServerConsentConfigEvent.ismShowAlert(),
          "RegistrationServerConsent");
    } else if (registrationServerConsentConfigEvent
        .getmRequestType()
        .equalsIgnoreCase("post_object")) {
      ApiCall apiCall = new ApiCall(registrationServerConsentConfigEvent.getmContext());
      apiCall.apiCallPostJson(
          url,
          registrationServerConsentConfigEvent.getmHeaders(),
          registrationServerConsentConfigEvent.gettClass(),
          registrationServerConsentConfigEvent.getmRequestParamsJson(),
          registrationServerConsentConfigEvent.getmResponseCode(),
          registrationServerConsentConfigEvent.getV(),
          registrationServerConsentConfigEvent.ismShowAlert(),
          "RegistrationServerConsent");
    } else if (registrationServerConsentConfigEvent.getmRequestType().equalsIgnoreCase("delete")) {
      ApiCall apiCall = new ApiCall(registrationServerConsentConfigEvent.getmContext());
      apiCall.apiCallDeleteHashmap(
          url,
          registrationServerConsentConfigEvent.getmHeaders(),
          registrationServerConsentConfigEvent.gettClass(),
          registrationServerConsentConfigEvent.getmRequestParams(),
          registrationServerConsentConfigEvent.getmResponseCode(),
          registrationServerConsentConfigEvent.getV(),
          registrationServerConsentConfigEvent.ismShowAlert(),
          "RegistrationServerConsent");
    } else if (registrationServerConsentConfigEvent
        .getmRequestType()
        .equalsIgnoreCase("delete_object")) {
      ApiCall apiCall = new ApiCall(registrationServerConsentConfigEvent.getmContext());
      apiCall.apiCallDeleteJson(
          url,
          registrationServerConsentConfigEvent.getmHeaders(),
          registrationServerConsentConfigEvent.gettClass(),
          registrationServerConsentConfigEvent.getmRequestParamsJson(),
          registrationServerConsentConfigEvent.getmResponseCode(),
          registrationServerConsentConfigEvent.getV(),
          registrationServerConsentConfigEvent.ismShowAlert(),
          "RegistrationServerConsent");
    } else if (registrationServerConsentConfigEvent
        .getmRequestType()
        .equalsIgnoreCase("delete_array")) {
      ApiCall apiCall = new ApiCall(registrationServerConsentConfigEvent.getmContext());
      apiCall.apiCallDeleteJsonArray(
          url,
          registrationServerConsentConfigEvent.getmHeaders(),
          registrationServerConsentConfigEvent.gettClass(),
          registrationServerConsentConfigEvent.getmRequestParamsJsonArray(),
          registrationServerConsentConfigEvent.getmResponseCode(),
          registrationServerConsentConfigEvent.getV(),
          registrationServerConsentConfigEvent.ismShowAlert(),
          "RegistrationServerConsent");
    } else {
      ApiCall apiCall = new ApiCall(registrationServerConsentConfigEvent.getmContext());
      apiCall.apiCallPostHashmap(
          url,
          registrationServerConsentConfigEvent.getmHeaders(),
          registrationServerConsentConfigEvent.gettClass(),
          registrationServerConsentConfigEvent.getmRequestParams(),
          registrationServerConsentConfigEvent.getmResponseCode(),
          registrationServerConsentConfigEvent.getV(),
          registrationServerConsentConfigEvent.ismShowAlert(),
          "RegistrationServerConsent");
    }
  }

  public void onEvent(AuthServerConfigEvent authServerConfigEvent) {
    String url = "";
    if (authServerConfigEvent
        .getmContext()
        .getResources()
        .getString(R.string.app_stage)
        .equalsIgnoreCase("development")) {
      url = authServerConfigEvent.getDevelopmentUrl() + authServerConfigEvent.getmUrl();
    } else {
      url = authServerConfigEvent.getProductionUrl() + authServerConfigEvent.getmUrl();
    }
    url = url.replaceAll(" ", "%20");
    if (authServerConfigEvent.getmRequestType().equalsIgnoreCase("get")) {
      ApiCall apiCall = new ApiCall(authServerConfigEvent.getmContext());
      apiCall.apiCallGet(
          url,
          authServerConfigEvent.getmHeaders(),
          authServerConfigEvent.gettClass(),
          authServerConfigEvent.getmResponseCode(),
          authServerConfigEvent.getV(),
          authServerConfigEvent.ismShowAlert(),
          "AuthServer");
    } else if (authServerConfigEvent.getmRequestType().equalsIgnoreCase("post_object")) {
      ApiCall apiCall = new ApiCall(authServerConfigEvent.getmContext());
      apiCall.apiCallPostJson(
          url,
          authServerConfigEvent.getmHeaders(),
          authServerConfigEvent.gettClass(),
          authServerConfigEvent.getmRequestParamsJson(),
          authServerConfigEvent.getmResponseCode(),
          authServerConfigEvent.getV(),
          authServerConfigEvent.ismShowAlert(),
          "AuthServer");
    } else if (authServerConfigEvent.getmRequestType().equalsIgnoreCase("delete")) {
      ApiCall apiCall = new ApiCall(authServerConfigEvent.getmContext());
      apiCall.apiCallDeleteHashmap(
          url,
          authServerConfigEvent.getmHeaders(),
          authServerConfigEvent.gettClass(),
          authServerConfigEvent.getmRequestParams(),
          authServerConfigEvent.getmResponseCode(),
          authServerConfigEvent.getV(),
          authServerConfigEvent.ismShowAlert(),
          "AuthServer");
    } else if (authServerConfigEvent.getmRequestType().equalsIgnoreCase("delete_object")) {
      ApiCall apiCall = new ApiCall(authServerConfigEvent.getmContext());
      apiCall.apiCallDeleteJson(
          url,
          authServerConfigEvent.getmHeaders(),
          authServerConfigEvent.gettClass(),
          authServerConfigEvent.getmRequestParamsJson(),
          authServerConfigEvent.getmResponseCode(),
          authServerConfigEvent.getV(),
          authServerConfigEvent.ismShowAlert(),
          "AuthServer");
    } else if (authServerConfigEvent.getmRequestType().equalsIgnoreCase("delete_array")) {
      ApiCall apiCall = new ApiCall(authServerConfigEvent.getmContext());
      apiCall.apiCallDeleteJsonArray(
          url,
          authServerConfigEvent.getmHeaders(),
          authServerConfigEvent.gettClass(),
          authServerConfigEvent.getmRequestParamsJsonArray(),
          authServerConfigEvent.getmResponseCode(),
          authServerConfigEvent.getV(),
          authServerConfigEvent.ismShowAlert(),
          "AuthServer");
    } else {
      ApiCall apiCall = new ApiCall(authServerConfigEvent.getmContext());
      apiCall.apiCallPostHashmap(
          url,
          authServerConfigEvent.getmHeaders(),
          authServerConfigEvent.gettClass(),
          authServerConfigEvent.getmRequestParams(),
          authServerConfigEvent.getmResponseCode(),
          authServerConfigEvent.getV(),
          authServerConfigEvent.ismShowAlert(),
          "AuthServer");
    }
  }

  public void onEvent(ResponseServerConfigEvent responseServerConfigEvent) {
    String url = "";
    if (responseServerConfigEvent
        .getmContext()
        .getResources()
        .getString(R.string.app_stage)
        .equalsIgnoreCase("development")) {
      url = responseServerConfigEvent.getDevelopmentUrl() + responseServerConfigEvent.getmUrl();
    } else {
      url = responseServerConfigEvent.getProductionUrl() + responseServerConfigEvent.getmUrl();
    }
    url = url.replaceAll(" ", "%20");
    if (responseServerConfigEvent.getmRequestType().equalsIgnoreCase("get")) {
      ApiCall apiCall = new ApiCall(responseServerConfigEvent.getmContext());
      apiCall.apiCallGet(
          url,
          responseServerConfigEvent.getmHeaders(),
          responseServerConfigEvent.gettClass(),
          responseServerConfigEvent.getmResponseCode(),
          responseServerConfigEvent.getV(),
          responseServerConfigEvent.ismShowAlert(),
          "ResponseServer");
    } else if (responseServerConfigEvent.getmRequestType().equalsIgnoreCase("post_object")) {
      ApiCall apiCall = new ApiCall(responseServerConfigEvent.getmContext());
      apiCall.apiCallPostJson(
          url,
          responseServerConfigEvent.getmHeaders(),
          responseServerConfigEvent.gettClass(),
          responseServerConfigEvent.getmRequestParamsJson(),
          responseServerConfigEvent.getmResponseCode(),
          responseServerConfigEvent.getV(),
          responseServerConfigEvent.ismShowAlert(),
          "ResponseServer");
    } else if (responseServerConfigEvent.getmRequestType().equalsIgnoreCase("delete")) {
      ApiCall apiCall = new ApiCall(responseServerConfigEvent.getmContext());
      apiCall.apiCallDeleteHashmap(
          url,
          responseServerConfigEvent.getmHeaders(),
          responseServerConfigEvent.gettClass(),
          responseServerConfigEvent.getmRequestParams(),
          responseServerConfigEvent.getmResponseCode(),
          responseServerConfigEvent.getV(),
          responseServerConfigEvent.ismShowAlert(),
          "ResponseServer");
    } else {
      ApiCall apiCall = new ApiCall(responseServerConfigEvent.getmContext());
      apiCall.apiCallPostHashmap(
          url,
          responseServerConfigEvent.getmHeaders(),
          responseServerConfigEvent.gettClass(),
          responseServerConfigEvent.getmRequestParams(),
          responseServerConfigEvent.getmResponseCode(),
          responseServerConfigEvent.getV(),
          responseServerConfigEvent.ismShowAlert(),
          "ResponseServer");
    }
  }

  public void onEvent(
      RegistrationServerEnrollmentConfigEvent registrationServerEnrollmentConfigEvent) {
    String url = "";
    if (registrationServerEnrollmentConfigEvent
        .getmContext()
        .getResources()
        .getString(R.string.app_stage)
        .equalsIgnoreCase("development")) {
      url =
          registrationServerEnrollmentConfigEvent.getDevelopmentUrl()
              + registrationServerEnrollmentConfigEvent.getmUrl();
    } else {
      url =
          registrationServerEnrollmentConfigEvent.getProductionUrl()
              + registrationServerEnrollmentConfigEvent.getmUrl();
    }
    url = url.replaceAll(" ", "%20");
    if (registrationServerEnrollmentConfigEvent.getmRequestType().equalsIgnoreCase("get")) {
      ApiCall apiCall = new ApiCall(registrationServerEnrollmentConfigEvent.getmContext());
      apiCall.apiCallGet(
          url,
          registrationServerEnrollmentConfigEvent.getmHeaders(),
          registrationServerEnrollmentConfigEvent.gettClass(),
          registrationServerEnrollmentConfigEvent.getmResponseCode(),
          registrationServerEnrollmentConfigEvent.getV(),
          registrationServerEnrollmentConfigEvent.ismShowAlert(),
          "RegistrationServerEnrollment");
    } else if (registrationServerEnrollmentConfigEvent
        .getmRequestType()
        .equalsIgnoreCase("post_object")) {
      ApiCall apiCall = new ApiCall(registrationServerEnrollmentConfigEvent.getmContext());
      apiCall.apiCallPostJson(
          url,
          registrationServerEnrollmentConfigEvent.getmHeaders(),
          registrationServerEnrollmentConfigEvent.gettClass(),
          registrationServerEnrollmentConfigEvent.getmRequestParamsJson(),
          registrationServerEnrollmentConfigEvent.getmResponseCode(),
          registrationServerEnrollmentConfigEvent.getV(),
          registrationServerEnrollmentConfigEvent.ismShowAlert(),
          "RegistrationServerEnrollment");
    } else if (registrationServerEnrollmentConfigEvent
        .getmRequestType()
        .equalsIgnoreCase("delete")) {
      ApiCall apiCall = new ApiCall(registrationServerEnrollmentConfigEvent.getmContext());
      apiCall.apiCallDeleteHashmap(
          url,
          registrationServerEnrollmentConfigEvent.getmHeaders(),
          registrationServerEnrollmentConfigEvent.gettClass(),
          registrationServerEnrollmentConfigEvent.getmRequestParams(),
          registrationServerEnrollmentConfigEvent.getmResponseCode(),
          registrationServerEnrollmentConfigEvent.getV(),
          registrationServerEnrollmentConfigEvent.ismShowAlert(),
          "RegistrationServerEnrollment");
    } else if (registrationServerEnrollmentConfigEvent
        .getmRequestType()
        .equalsIgnoreCase("delete_object")) {
      ApiCall apiCall = new ApiCall(registrationServerEnrollmentConfigEvent.getmContext());
      apiCall.apiCallDeleteJson(
          url,
          registrationServerEnrollmentConfigEvent.getmHeaders(),
          registrationServerEnrollmentConfigEvent.gettClass(),
          registrationServerEnrollmentConfigEvent.getmRequestParamsJson(),
          registrationServerEnrollmentConfigEvent.getmResponseCode(),
          registrationServerEnrollmentConfigEvent.getV(),
          registrationServerEnrollmentConfigEvent.ismShowAlert(),
          "RegistrationServerEnrollment");
    } else if (registrationServerEnrollmentConfigEvent
        .getmRequestType()
        .equalsIgnoreCase("delete_array")) {
      ApiCall apiCall = new ApiCall(registrationServerEnrollmentConfigEvent.getmContext());
      apiCall.apiCallDeleteJsonArray(
          url,
          registrationServerEnrollmentConfigEvent.getmHeaders(),
          registrationServerEnrollmentConfigEvent.gettClass(),
          registrationServerEnrollmentConfigEvent.getmRequestParamsJsonArray(),
          registrationServerEnrollmentConfigEvent.getmResponseCode(),
          registrationServerEnrollmentConfigEvent.getV(),
          registrationServerEnrollmentConfigEvent.ismShowAlert(),
          "RegistrationServerEnrollment");
    } else {
      ApiCall apiCall = new ApiCall(registrationServerEnrollmentConfigEvent.getmContext());
      apiCall.apiCallPostHashmap(
          url,
          registrationServerEnrollmentConfigEvent.getmHeaders(),
          registrationServerEnrollmentConfigEvent.gettClass(),
          registrationServerEnrollmentConfigEvent.getmRequestParams(),
          registrationServerEnrollmentConfigEvent.getmResponseCode(),
          registrationServerEnrollmentConfigEvent.getV(),
          registrationServerEnrollmentConfigEvent.ismShowAlert(),
          "RegistrationServerEnrollment");
    }
  }
}
