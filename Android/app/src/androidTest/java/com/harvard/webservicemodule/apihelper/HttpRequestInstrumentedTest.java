/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.webservicemodule.apihelper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import android.support.test.runner.AndroidJUnit4;
import com.harvard.utils.Urls;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class HttpRequestInstrumentedTest {
  private static final String FAILED_MESSAGE = "API failed for status 200";
  private static final String PARAMS_EMAIL_KEY = "email";
  private static final String PARAMS_EMAIL_VALUE = "test@grr.la";
  private static final String PARAMS_APPID_KEY = "appId";
  private static final String PARAMS_APPID_VALUE = "GCPMS001";
  private static final String HEADER_CORELATIONID_KEY = "correlationId";
  private static final String HEADER_CORELATIONID_VALUE = "d16a2aff-6636-4023-a9da-e414db40eecb";
  private static final String HEADER_MOBILE_PLATFORM_KEY = "mobilePlatform";
  private static final String HEADER_MOBILE_PLATFORM_VALUE = "ANDROID";
  private static final String SERVER_TYPE_WCP_SERVER = "WCP";
  private static final String SERVER_TYPE_AUTH_SERVER = "auth";
  private static final String ASSERT_THAT_IS = "200";

  @Test
  public void getRequestTest() {
    StringBuilder studyListUrl = new StringBuilder();
    studyListUrl.append(Urls.BASE_URL_WCP_SERVER);
    studyListUrl.append(Urls.STUDY_LIST);
    Responsemodel responsemodel =
        HttpRequest.getRequest(studyListUrl.toString(), null, SERVER_TYPE_WCP_SERVER);
    assertThat(FAILED_MESSAGE, responsemodel.getResponseCode(), is(ASSERT_THAT_IS));
  }

  @Test
  public void postRequestsWithHashmapTest() {
    HashMap<String, String> params = new HashMap<>();
    params.put(PARAMS_APPID_KEY, PARAMS_APPID_VALUE);
    params.put(PARAMS_EMAIL_KEY, PARAMS_EMAIL_VALUE);
    HashMap<String, String> headersData = new HashMap<>();
    headersData.put(HEADER_CORELATIONID_KEY, HEADER_CORELATIONID_VALUE);
    headersData.put(HEADER_MOBILE_PLATFORM_KEY, HEADER_MOBILE_PLATFORM_VALUE);
    StringBuilder forgotPasswordUrl = new StringBuilder();
    forgotPasswordUrl.append(Urls.BASE_URL_AUTH_SERVER);
    forgotPasswordUrl.append(Urls.FORGOT_PASSWORD);
    Responsemodel responsemodel =
        HttpRequest.postRequestsWithHashmap(
            forgotPasswordUrl.toString(), params, headersData, SERVER_TYPE_AUTH_SERVER);
    assertThat(FAILED_MESSAGE, responsemodel.getResponseCode(), is(ASSERT_THAT_IS));
  }

  @Test
  public void postRequestWithJsonTest() {
    HashMap<String, String> headersData = new HashMap<>();
    headersData.put(HEADER_CORELATIONID_KEY, HEADER_CORELATIONID_VALUE);
    headersData.put(HEADER_MOBILE_PLATFORM_KEY, HEADER_MOBILE_PLATFORM_VALUE);
    JSONObject params = new JSONObject();
    try {
      params.put(PARAMS_APPID_KEY, PARAMS_APPID_VALUE);
      params.put(PARAMS_EMAIL_KEY, PARAMS_EMAIL_VALUE);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    StringBuilder forgotPasswordUrl = new StringBuilder();
    forgotPasswordUrl.append(Urls.BASE_URL_AUTH_SERVER);
    forgotPasswordUrl.append(Urls.FORGOT_PASSWORD);
    Responsemodel responsemodel =
        HttpRequest.makePostRequestWithJson(
            forgotPasswordUrl.toString(), params, headersData, SERVER_TYPE_AUTH_SERVER);
    assertThat(FAILED_MESSAGE, responsemodel.getResponseCode(), is(ASSERT_THAT_IS));
  }
}
