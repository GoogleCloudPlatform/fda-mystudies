/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.webservicemodule.apihelper;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.harvard.studyappmodule.studymodel.Study;
import com.harvard.usermodule.webservicemodel.ForgotPasswordData;
import com.harvard.utils.Urls;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.HashMap;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class ApiCallTest {
  private ApiCallSyncronizer async = null;

  @Test
  public void apiCallGetTest() {
    StringBuilder studyListUrl = new StringBuilder();
    studyListUrl.append(Urls.BASE_URL_WCP_SERVER);
    studyListUrl.append(Urls.STUDY_LIST);
    async = new ApiCallSyncronizer();
    ApiCall apiCall = new ApiCall(InstrumentationRegistry.getTargetContext());
    apiCall.apiCallGet(
        studyListUrl.toString(),
        new HashMap<String, String>(),
        Study.class,
        100,
        new ApiCall.OnAsyncRequestComplete() {
          @Override
          public <T> void asyncResponse(T response, int responseCode) {
            assertNotNull(response);
            Study study = (Study) response;
            assertNotNull(study.getStudies());
            async.doNotify();
          }

          @Override
          public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
            async.doNotify();
          }
        },
        false,
        "WCP");
    async.doWait();
  }

  @Test
  public void apiCallPostHashmapTest() {
    StringBuilder forgotPasswordUrl = new StringBuilder();
    forgotPasswordUrl.append(Urls.BASE_URL_AUTH_SERVER);
    forgotPasswordUrl.append(Urls.FORGOT_PASSWORD);
    async = new ApiCallSyncronizer();
    HashMap<String, String> params = new HashMap<>();
    params.put("email", "naveenr@grr.la");
    params.put("appId", "GCPMS001");
    ApiCall apiCall = new ApiCall(InstrumentationRegistry.getTargetContext());
    apiCall.apiCallPostHashmap(
        forgotPasswordUrl.toString(),
        new HashMap<String, String>(),
        ForgotPasswordData.class,
        params,
        100,
        new ApiCall.OnAsyncRequestComplete() {
          @Override
          public <T> void asyncResponse(T response, int responseCode) {
            assertNotNull(response);
            async.doNotify();
          }

          @Override
          public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
            async.doNotify();
          }
        },
        false,
        "AuthServer");
    async.doWait();
  }

  @Test
  public void apiCallPostJsonTest() {
    StringBuilder forgotPasswordUrl = new StringBuilder();
    forgotPasswordUrl.append(Urls.BASE_URL_AUTH_SERVER);
    forgotPasswordUrl.append(Urls.FORGOT_PASSWORD);
    async = new ApiCallSyncronizer();
    JSONObject params = new JSONObject();
    try {
      params.put("email", "naveenr@grr.la");
      params.put("appId", "GCPMS001");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    ApiCall apiCall = new ApiCall(InstrumentationRegistry.getTargetContext());
    apiCall.apiCallPostJson(
        forgotPasswordUrl.toString(),
        new HashMap<String, String>(),
        ForgotPasswordData.class,
        params,
        100,
        new ApiCall.OnAsyncRequestComplete() {
          @Override
          public <T> void asyncResponse(T response, int responseCode) {
            assertNotNull(response);
            async.doNotify();
          }

          @Override
          public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
            async.doNotify();
          }
        },
        false,
        "AuthServer");
    async.doWait();
  }
}
