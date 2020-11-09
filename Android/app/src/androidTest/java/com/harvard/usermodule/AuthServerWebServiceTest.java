/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.usermodule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.google.gson.Gson;
import com.harvard.usermodule.webservicemodel.ChangePasswordData;
import com.harvard.usermodule.webservicemodel.LoginData;
import com.harvard.usermodule.webservicemodel.TokenData;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.apihelper.ApiCallSyncronizer;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AuthServerWebServiceTest {
  private ApiCallSyncronizer async = null;
  private static final int RESULT_CODE = 100;
  private static final String RESPONSE_CODE = "200";
  private static final String PARAMS_EMAIL_KEY = "email";
  private static final String PARAMS_EMAIL_VALUE = "test@grr.la";
  private static final String PARAMS_APPID_KEY = "appId";
  private static final String PARAMS_APPID_VALUE = "GCPMS001";
  private static final String SERVER_TYPE_AUTH_SERVER = "AuthServer";
  private static final String PARAMS_CURRENT_PASS_KEY = "currentPassword";
  private static final String PARAMS_CURRENT_PASS_VALUE = "$r04T!3tEkD%";
  private static final String PARAMS_NEW_PASS_KEY = "newPassword";
  private static final String PARAMS_NEW_PASS_VALUE = "Test@b0ston_2";
  private static final String PARAMS_AOUTH_GRANT_TYPE_KEY = "grant_type";
  private static final String PARAMS_AOUTH_REDIRECT_URL_KEY = "redirect_uri";
  private static final String PARAMS_AOUTH_CLIENT_ID_KEY = "client_id";
  private static final String PARAMS_AOUTH_REFRESH_TOKEN_KEY = "refresh_token";
  private static final String PARAMS_AOUTH_USERID_KEY = "userId";
  private static final String PARAMS_AOUTH_GRANT_TYPE_VALUE = "refresh_token";
  private static final String PARAMS_AOUTH_REDIRECT_URL_VALUE =
      "https://localhost:8085/qa/oauth-scim-service/login";
  private static final String PARAMS_AOUTH_CLIENT_ID_VALUE = "oauth-scimefre-cliente-id";
  private static final String PARAMS_AOUTH_REFRESH_TOKEN_VALUE =
      "qxDWRk-0KHhfasYJxvcvsdvQTg.Ivf6lV9PXz-uxfn";
  private static final String PARAMS_AOUTH_USERID_VALUE = "2c4eghjtjj";
  MockWebServer mockWebServer = new MockWebServer();

  @Before
  public void setup() {
    try {
      mockWebServer.start(8085);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void resetPasswordTest() {
    mockWebServer.setDispatcher(
        new Dispatcher() {
          @Override
          public MockResponse dispatch(@NotNull RecordedRequest recordedRequest)
              throws InterruptedException {
            MockResponse mockResponse = new MockResponse();
            mockResponse.setResponseCode(200);
            mockResponse.setBody(
                FileReader.readStringFromFile("mockresponses/reset_password_response.json"));
            return mockResponse;
          }
        });
    StringBuilder studyListUrl = new StringBuilder();
    studyListUrl.append("oauth-scim-service/user/reset_password");
    URL url = mockWebServer.url(studyListUrl.toString()).url();
    JSONObject params = new JSONObject();
    try {
      params.put(PARAMS_EMAIL_KEY, PARAMS_EMAIL_VALUE);
      params.put(PARAMS_APPID_KEY, PARAMS_APPID_VALUE);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    async = new ApiCallSyncronizer();
    ApiCall apiCall = new ApiCall(InstrumentationRegistry.getTargetContext());
    apiCall.apiCallPostJson(
        url.toString(),
        new HashMap<String, String>(),
        ChangePasswordData.class,
        params,
        RESULT_CODE,
        new ApiCall.OnAsyncRequestComplete() {
          @Override
          public <T> void asyncResponse(T response, int responseCode) {
            ChangePasswordData forgotPasswordData = (ChangePasswordData) response;
            Gson gson = new Gson();
            String forgotPassword = gson.toJson(forgotPasswordData);
            ChangePasswordData requiredResponse =
                gson.fromJson(
                    FileReader.readStringFromFile("mockresponses/reset_password_response.json"),
                    ChangePasswordData.class);
            String requiredResponseVal = gson.toJson(requiredResponse);
            assertNotNull(response);
            assertEquals(requiredResponseVal, forgotPassword);
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
  public void logoutTest() {
    mockWebServer.setDispatcher(
        new Dispatcher() {
          @Override
          public MockResponse dispatch(@NotNull RecordedRequest recordedRequest)
              throws InterruptedException {
            MockResponse mockResponse = new MockResponse();
            mockResponse.setResponseCode(200);
            mockResponse.setBody(
                FileReader.readStringFromFile("mockresponses/logout_response.json"));
            return mockResponse;
          }
        });
    StringBuilder studyListUrl = new StringBuilder();
    studyListUrl.append(
        "/oauth-scim-service/users/GLolRAsSmIAeUEGc.Yo1Rf1VAVJlfAVb01NVCoj7kGo/logout");
    JSONObject params = new JSONObject();
    URL url = mockWebServer.url(studyListUrl.toString()).url();
    async = new ApiCallSyncronizer();
    ApiCall apiCall = new ApiCall(InstrumentationRegistry.getTargetContext());
    apiCall.apiCallPostJson(
        url.toString(),
        new HashMap<String, String>(),
        LoginData.class,
        params,
        RESULT_CODE,
        new ApiCall.OnAsyncRequestComplete() {
          @Override
          public <T> void asyncResponse(T response, int responseCode) {
            LoginData logoutModel = (LoginData) response;
            Gson gson = new Gson();
            String logout = gson.toJson(logoutModel);
            LoginData requiredResponse =
                gson.fromJson(
                    FileReader.readStringFromFile("mockresponses/logout_response.json"),
                    LoginData.class);
            String requiredResponseVal = gson.toJson(requiredResponse);
            assertNotNull(response);
            assertEquals(requiredResponseVal, logout);
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
  public void aouthTokenTest() {
    mockWebServer.setDispatcher(
        new Dispatcher() {
          @Override
          public MockResponse dispatch(@NotNull RecordedRequest recordedRequest)
              throws InterruptedException {
            MockResponse mockResponse = new MockResponse();
            mockResponse.setResponseCode(200);
            mockResponse.setBody(
                FileReader.readStringFromFile("mockresponses/aouth_token_response.json"));
            return mockResponse;
          }
        });
    StringBuilder studyListUrl = new StringBuilder();
    studyListUrl.append("oauth-scim-service/oauth2/token");
    JSONObject params = new JSONObject();
    try {
      params.put(PARAMS_AOUTH_GRANT_TYPE_KEY, PARAMS_AOUTH_GRANT_TYPE_VALUE);
      params.put(PARAMS_AOUTH_REDIRECT_URL_KEY, PARAMS_AOUTH_REDIRECT_URL_VALUE);
      params.put(PARAMS_AOUTH_CLIENT_ID_KEY, PARAMS_AOUTH_CLIENT_ID_VALUE);
      params.put(PARAMS_AOUTH_REFRESH_TOKEN_KEY, PARAMS_AOUTH_REFRESH_TOKEN_VALUE);
      params.put(PARAMS_AOUTH_USERID_KEY, PARAMS_AOUTH_USERID_VALUE);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    URL url = mockWebServer.url(studyListUrl.toString()).url();
    async = new ApiCallSyncronizer();
    ApiCall apiCall = new ApiCall(InstrumentationRegistry.getTargetContext());
    apiCall.apiCallPostJson(
        url.toString(),
        new HashMap<String, String>(),
        TokenData.class,
        params,
        RESULT_CODE,
        new ApiCall.OnAsyncRequestComplete() {
          @Override
          public <T> void asyncResponse(T response, int responseCode) {
            TokenData tokenModel = (TokenData) response;
            Gson gson = new Gson();
            String tokenresponse = gson.toJson(tokenModel);
            TokenData requiredResponse =
                gson.fromJson(
                    FileReader.readStringFromFile("mockresponses/aouth_token_response.json"),
                    TokenData.class);
            String requiredResponseVal = gson.toJson(requiredResponse);
            assertNotNull(response);
            assertEquals(requiredResponseVal, tokenresponse);
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
  public void changePasswordTest() {
    mockWebServer.setDispatcher(
        new Dispatcher() {
          @Override
          public MockResponse dispatch(@NotNull RecordedRequest recordedRequest)
              throws InterruptedException {
            MockResponse mockResponse = new MockResponse();
            mockResponse.setResponseCode(200);
            mockResponse.setBody(
                FileReader.readStringFromFile("mockresponses/change_password_response.json"));
            return mockResponse;
          }
        });
    StringBuilder studyListUrl = new StringBuilder();
    studyListUrl.append("oauth-scim-service/users/71481c9f7b0c0b65/change_password");
    URL url = mockWebServer.url(studyListUrl.toString()).url();
    JSONObject params = new JSONObject();
    try {
      params.put(PARAMS_CURRENT_PASS_KEY, PARAMS_CURRENT_PASS_VALUE);
      params.put(PARAMS_NEW_PASS_KEY, PARAMS_NEW_PASS_VALUE);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    async = new ApiCallSyncronizer();
    ApiCall apiCall = new ApiCall(InstrumentationRegistry.getTargetContext());
    apiCall.apiCallPutJson(
        url.toString(),
        new HashMap<String, String>(),
        ChangePasswordData.class,
        params,
        RESULT_CODE,
        new ApiCall.OnAsyncRequestComplete() {
          @Override
          public <T> void asyncResponse(T response, int responseCode) {
            ChangePasswordData changePasswordData = (ChangePasswordData) response;
            Gson gson = new Gson();
            String changePassword = gson.toJson(changePasswordData);
            ChangePasswordData requiredResponse =
                gson.fromJson(
                    FileReader.readStringFromFile("mockresponses/change_password_response.json"),
                    ChangePasswordData.class);
            String requiredResponseVal = gson.toJson(requiredResponse);
            assertNotNull(response);
            assertEquals(requiredResponseVal, changePassword);
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

  @After
  public void teardown() {
    try {
      mockWebServer.shutdown();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
