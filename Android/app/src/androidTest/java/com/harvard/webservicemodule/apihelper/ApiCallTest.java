/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.webservicemodule.apihelper;

import static org.junit.Assert.assertNotNull;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.harvard.studyappmodule.studymodel.Study;
import com.harvard.usermodule.webservicemodel.ForgotPasswordData;
import com.harvard.utils.Urls;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ApiCallTest {
  private ApiCallSyncronizer async = null;
  private static final String SERVER_TYPE_STUDY_DATASTORE = "STUDY_DATASTORE";
  private static final String SERVER_TYPE_AUTH_SERVER = "AuthServer";
  private static final int RESULT_CODE = 100;
  private static final String PARAMS_EMAIL_KEY = "email";
  private static final String PARAMS_EMAIL_VALUE = "test@grr.la";
  private static final String PARAMS_APPID_KEY = "appId";
  private static final String PARAMS_APPID_VALUE = "GCPMS001";

  @Test
  public void apiCallGetTest() {
    StringBuilder studyListUrl = new StringBuilder();
    studyListUrl.append(Urls.BASE_URL_STUDY_DATASTORE_SERVER);
    studyListUrl.append(Urls.STUDY_LIST);
    async = new ApiCallSyncronizer();
    ApiCall apiCall = new ApiCall(InstrumentationRegistry.getTargetContext());
    apiCall.apiCallGet(
        studyListUrl.toString(),
        new HashMap<String, String>(),
        Study.class,
        RESULT_CODE,
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
        SERVER_TYPE_STUDY_DATASTORE);
    async.doWait();
  }

  @Test
  public void apiCallPostHashmapTest() {
    StringBuilder forgotPasswordUrl = new StringBuilder();
    forgotPasswordUrl.append(Urls.BASE_URL_AUTH_SERVER);
    forgotPasswordUrl.append(Urls.FORGOT_PASSWORD);
    async = new ApiCallSyncronizer();
    HashMap<String, String> params = new HashMap<>();
    params.put(PARAMS_EMAIL_KEY, PARAMS_EMAIL_VALUE);
    params.put(PARAMS_APPID_KEY, PARAMS_APPID_VALUE);
    ApiCall apiCall = new ApiCall(InstrumentationRegistry.getTargetContext());
    apiCall.apiCallPostHashmap(
        forgotPasswordUrl.toString(),
        new HashMap<String, String>(),
        ForgotPasswordData.class,
        params,
        RESULT_CODE,
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
        SERVER_TYPE_AUTH_SERVER);
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
      params.put(PARAMS_EMAIL_KEY, PARAMS_EMAIL_VALUE);
      params.put(PARAMS_APPID_KEY, PARAMS_APPID_VALUE);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    ApiCall apiCall = new ApiCall(InstrumentationRegistry.getTargetContext());
    apiCall.apiCallPostJson(
        forgotPasswordUrl.toString(),
        new HashMap<String, String>(),
        ForgotPasswordData.class,
        params,
        RESULT_CODE,
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
        SERVER_TYPE_AUTH_SERVER);
    async.doWait();
  }
}
