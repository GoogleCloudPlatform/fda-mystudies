/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.webservicemodule.apihelper;

import android.support.test.runner.AndroidJUnit4;
import com.harvard.utils.Urls;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.HashMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class HttpRequestInstrumentedTest {
  private static final String FAILED_MESSAGE = "API failed for status 200";

  @Test
  public void getRequestTest() {
    StringBuilder studyListUrl = new StringBuilder();
    studyListUrl.append(Urls.BASE_URL_WCP_SERVER);
    studyListUrl.append(Urls.STUDY_LIST);
    Responsemodel responsemodel = HttpRequest.getRequest(studyListUrl.toString(), null, "WCP");
    assertThat(FAILED_MESSAGE, responsemodel.getResponseCode(), is("200"));
  }

  @Test
  public void postRequestsWithHashmapTest() {
    HashMap<String, String> params = new HashMap<>();
    params.put("appId", "GCPMS001");
    params.put("email", "naveenr@grr.la");
    HashMap<String, String> headersData = new HashMap<>();
    headersData.put("correlationId", "d16a2aff-6636-4023-a9da-e414db40eecb");
    headersData.put("mobilePlatform", "ANDROID");
    StringBuilder forgotPasswordUrl = new StringBuilder();
    forgotPasswordUrl.append(Urls.BASE_URL_AUTH_SERVER);
    forgotPasswordUrl.append(Urls.FORGOT_PASSWORD);
    Responsemodel responsemodel =
        HttpRequest.postRequestsWithHashmap(
            forgotPasswordUrl.toString(), params, headersData, "auth");
    assertThat(FAILED_MESSAGE, responsemodel.getResponseCode(), is("200"));
  }

  @Test
  public void postRequestWithJsonTest() {
    HashMap<String, String> headersData = new HashMap<>();
    headersData.put("correlationId", "d16a2aff-6636-4023-a9da-e414db40eecb");
    headersData.put("mobilePlatform", "ANDROID");
    JSONObject params = new JSONObject();
    try {
      params.put("appId", "GCPMS001");
      params.put("email", "naveenr@grr.la");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    StringBuilder forgotPasswordUrl = new StringBuilder();
    forgotPasswordUrl.append(Urls.BASE_URL_AUTH_SERVER);
    forgotPasswordUrl.append(Urls.FORGOT_PASSWORD);
    Responsemodel responsemodel =
        HttpRequest.makePostRequestWithJson(
            forgotPasswordUrl.toString(), params, headersData, "auth");
    assertThat(FAILED_MESSAGE, responsemodel.getResponseCode(), is("200"));
  }
}
