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

package com.harvard.webservicemodule.apihelper;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.harvard.BuildConfig;
import com.harvard.FdaApplication;
import com.harvard.R;
import com.harvard.studyappmodule.activitybuilder.model.servicemodel.ActivityInfoData;
import com.harvard.usermodule.webservicemodel.TokenData;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import com.harvard.utils.SharedPreferenceHelper;
import com.harvard.utils.Urls;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ApiCall<T, V> extends AsyncTask<T, String, String> {

  private String urlPassed;
  private HashMap<String, String> hashmapData;
  private JSONObject jsonData;
  private String webserviceType;
  private Class<T> genericClass;
  private Context context;
  private HashMap<String, String> headersData;
  private T obj;
  private OnAsyncRequestComplete onAsyncRequestComplete;
  private int resultCode;
  private Responsemodel responseModel;
  private boolean showAlert;
  private String serverType;

  public ApiCall(Context context) {
    this.context = context;
  }

  /**
   * To make a POST Hashmap request.
   *
   * @param url url path
   * @param genericClass model class to parse
   * @param headers null if no header to pass
   * @param hashMap params
   * @param resultCode call back code
   * @param v Activity context
   * @param showAlert wherever to show alert
   */
  public void apiCallPostHashmap(
      String url,
      HashMap<String, String> headers,
      Class<T> genericClass,
      HashMap<String, String> hashMap,
      int resultCode,
      V v,
      boolean showAlert,
      String serverType) {
    this.urlPassed = url;
    this.genericClass = genericClass;
    this.headersData = headers;
    this.hashmapData = hashMap;
    this.webserviceType = "post_hashmap";
    this.resultCode = resultCode;
    this.onAsyncRequestComplete = (OnAsyncRequestComplete) v;
    this.serverType = serverType;
    this.showAlert = showAlert;
    execute();
  }

  /**
   * To make a POST Json request.
   *
   * @param url url path
   * @param genericClass model class to parse
   * @param headers null if no header to pass
   * @param jsonData json object params
   * @param resultCode call back code
   * @param v activity context
   * @param showAlert wherever to show alert
   */
  public void apiCallPostJson(
      String url,
      HashMap<String, String> headers,
      Class<T> genericClass,
      JSONObject jsonData,
      int resultCode,
      V v,
      boolean showAlert,
      String serverType) {
    this.urlPassed = url;
    this.genericClass = genericClass;
    this.headersData = headers;
    this.jsonData = jsonData;
    this.webserviceType = "post_json";
    this.resultCode = resultCode;
    this.onAsyncRequestComplete = (OnAsyncRequestComplete) v;
    this.showAlert = showAlert;
    this.serverType = serverType;
    execute();
  }

  /**
   * To make a PUT Json request.
   *
   * @param url url path
   * @param genericClass model class to parse
   * @param headers null if no header to pass
   * @param jsonData json object params
   * @param resultCode call back code
   * @param v activity context
   * @param showAlert wherever to show alert
   */
  public void apiCallPutJson(
      String url,
      HashMap<String, String> headers,
      Class<T> genericClass,
      JSONObject jsonData,
      int resultCode,
      V v,
      boolean showAlert,
      String serverType) {
    this.urlPassed = url;
    this.genericClass = genericClass;
    this.headersData = headers;
    this.jsonData = jsonData;
    this.webserviceType = "put_json";
    this.resultCode = resultCode;
    this.onAsyncRequestComplete = (OnAsyncRequestComplete) v;
    this.showAlert = showAlert;
    this.serverType = serverType;
    execute();
  }

  /**
   * To make a DELETE Json request.
   *
   * @param url url path
   * @param genericClass model class to parse
   * @param headers null if no header to pass
   * @param jsonData json object params
   * @param resultCode call back code
   * @param v activity context
   * @param showAlert wherever to show alert
   */
  public void apiCallDeleteJson(
      String url,
      HashMap<String, String> headers,
      Class<T> genericClass,
      JSONObject jsonData,
      int resultCode,
      V v,
      boolean showAlert,
      String serverType) {
    this.urlPassed = url;
    this.genericClass = genericClass;
    this.headersData = headers;
    this.jsonData = jsonData;
    this.webserviceType = "delete_json";
    this.resultCode = resultCode;
    this.onAsyncRequestComplete = (OnAsyncRequestComplete) v;
    this.showAlert = showAlert;
    this.serverType = serverType;
    execute();
  }

  /**
   * To make a GET request.
   *
   * @param url url path
   * @param headers null if no header to pass
   * @param genericClass model class to parse
   * @param resultCode call back code
   * @param v activity context
   * @param showAlert wherever to show alert
   */
  public void apiCallGet(
      String url,
      HashMap<String, String> headers,
      Class<T> genericClass,
      int resultCode,
      V v,
      boolean showAlert,
      String serverType) {
    this.urlPassed = url;
    this.headersData = headers;
    this.genericClass = genericClass;
    this.webserviceType = "get";
    this.resultCode = resultCode;
    this.onAsyncRequestComplete = (OnAsyncRequestComplete) v;
    this.showAlert = showAlert;
    this.serverType = serverType;
    execute();
  }

  // Interface to be implemented by calling activity
  public interface OnAsyncRequestComplete {
    /**
     * @param response --> web service response
     * @param responseCode --> call back code
     * @param <T> --> Generic class
     */
    <T> void asyncResponse(T response, int responseCode);

    /**
     * @param responseCode --> response code call back
     * @param errormsg --> error msg
     */
    void asyncResponseFailure(int responseCode, String errormsg, String statusCode);
  }

  @SafeVarargs
  @Override
  protected final String doInBackground(T... params) {

    String responseCode;
    ConnectionDetector connectionDetector = new ConnectionDetector(context);
    String response;
    if (connectionDetector.isConnectingToInternet()) {
      switch (webserviceType) {
        case "get":
          responseModel = HttpRequest.getRequest(urlPassed, headersData, serverType);
          break;
        case "put_json":
          responseModel =
              HttpRequest.makePutRequestWithJson(urlPassed, jsonData, headersData, serverType);
          break;
        case "post_hashmap":
          responseModel =
              HttpRequest.postRequestsWithHashmap(urlPassed, hashmapData, headersData, serverType);
          break;
        case "post_json":
          responseModel =
              HttpRequest.makePostRequestWithJson(urlPassed, jsonData, headersData, serverType);
          break;
        case "delete_json":
          responseModel =
              HttpRequest.makeDeleteRequestWithJson(urlPassed, jsonData, headersData, serverType);
          break;
      }
      responseCode = responseModel.getResponseCode();
      response = responseModel.getResponseData();
      if (responseCode.equalsIgnoreCase("0") && response.equalsIgnoreCase("timeout")) {
        response = "timeout";
      } else if (responseCode.equalsIgnoreCase("0") && response.equalsIgnoreCase("")) {
        response = "error";
      } else if (Integer.parseInt(responseCode) == HttpURLConnection.HTTP_UNAUTHORIZED) {
        response = "session expired";

        if (!this.urlPassed.contains(Urls.LOGIN)) {

          HashMap<String, String> refreshTokenJsonData = new HashMap();
          refreshTokenJsonData.put(
              "refresh_token",
              AppController.getHelperSharedPreference()
                  .readPreference(context, context.getString(R.string.refreshToken), ""));
          refreshTokenJsonData.put(
              "userId",
              SharedPreferenceHelper.readPreference(
                  context, context.getString(R.string.userid), ""));
          refreshTokenJsonData.put("redirect_uri", Urls.AUTH_SERVER_REDIRECT_URL);
          refreshTokenJsonData.put("client_id", BuildConfig.HYDRA_CLIENT_ID);
          refreshTokenJsonData.put("grant_type", "refresh_token");

          HashMap<String, String> refreshTokenHeader = new HashMap<>();
          refreshTokenHeader.put("Content-Type", "application/x-www-form-urlencoded");
          refreshTokenHeader.put("mobilePlatform", "ANDROID");
          refreshTokenHeader.put("correlationId", FdaApplication.getRandomString());

          String refreshTokenUrl = Urls.BASE_URL_AUTH_SERVER + Urls.TOKENS;
          responseModel =
              HttpRequest.postRequestsWithHashmap(
                  refreshTokenUrl, refreshTokenJsonData, refreshTokenHeader, "");
          String s = checkResponse(true, responseModel, HttpURLConnection.HTTP_FORBIDDEN);
          if (s.equalsIgnoreCase("success")) {
            if (headersData != null
                && (headersData.containsKey("Authorization") || headersData.containsKey("auth"))) {
              String s1 =
                  AppController.getHelperSharedPreference()
                      .readPreference(context, context.getString(R.string.auth), "");
              headersData.put("Authorization", "Bearer " + s1);
            }
            switch (webserviceType) {
              case "get":
                responseModel = HttpRequest.getRequest(urlPassed, headersData, serverType);
                break;
              case "put_json":
                responseModel =
                    HttpRequest.makePutRequestWithJson(
                        urlPassed, jsonData, headersData, serverType);
                break;
              case "post_hashmap":
                responseModel =
                    HttpRequest.postRequestsWithHashmap(
                        urlPassed, hashmapData, headersData, serverType);
                break;
              case "post_json":
                responseModel =
                    HttpRequest.makePostRequestWithJson(
                        urlPassed, jsonData, headersData, serverType);
                break;
              case "delete_json":
                responseModel =
                    HttpRequest.makeDeleteRequestWithJson(
                        urlPassed, jsonData, headersData, serverType);
                break;
            }
            response = checkResponse(false, responseModel, HttpURLConnection.HTTP_UNAUTHORIZED);
          } else if (s.equalsIgnoreCase("session expired")
              || s.equalsIgnoreCase("Unknown error")
              || s.equalsIgnoreCase("server error")
              || s.equalsIgnoreCase("client error")
              || s.equalsIgnoreCase("")
              || s.equalsIgnoreCase("No data")) {
            responseModel.setResponseCode("401");
            responseModel.setServermsg("session expired");
          }
        }
      } else if (Integer.parseInt(responseCode) >= HttpURLConnection.HTTP_OK
          && Integer.parseInt(responseCode) < HttpURLConnection.HTTP_MULT_CHOICE
          && !response.equalsIgnoreCase("")) {
        response = "success";

        obj = parseJson(responseModel, genericClass);
        if (urlPassed.contains(Urls.BASE_URL_STUDY_DATASTORE_SERVER + "/activity")) {
          try {
            ActivityInfoData activityInfoData = (ActivityInfoData) obj;
            JSONObject jsonObject = new JSONObject(responseModel.getResponseData());
            JSONObject jsonObjectActivity = jsonObject.getJSONObject("activity");
            JSONArray jsonArray = jsonObjectActivity.getJSONArray("steps");
            for (int i = 0; i < jsonArray.length(); i++) {
              JSONObject jsonObject2 = jsonArray.getJSONObject(i);
              JSONObject format = jsonObject2.getJSONObject("format");
              String key = jsonObject2.getString("key");
              String defaultString = null;
              try {
                Object defaultValue = format.get("default");
                defaultString = "";
                if (defaultValue instanceof Integer) {
                  defaultString = "" + (int) defaultValue;
                } else if (defaultValue instanceof Double) {
                  defaultString = "" + (double) defaultValue;
                } else if (defaultValue instanceof Long) {
                  defaultString = "" + (long) defaultValue;
                } else if (defaultValue instanceof String) {
                  defaultString = "" + (String) defaultValue;
                } else {
                  defaultString = "" + defaultValue;
                }
              } catch (JSONException e) {
                defaultString = "";
                Logger.log(e);
              }
              for (int k = 0; k < activityInfoData.getActivity().getSteps().size(); k++) {
                if (activityInfoData
                    .getActivity()
                    .getSteps()
                    .get(k)
                    .getKey()
                    .equalsIgnoreCase(key)) {
                  activityInfoData
                      .getActivity()
                      .getSteps()
                      .get(k)
                      .getFormat()
                      .setDefaultValue(defaultString);
                }
              }
            }
            obj = (T) activityInfoData;

          } catch (Exception e) {
            Logger.log(e);
          }
        }
      } else if (Integer.parseInt(responseCode) >= 400 && Integer.parseInt(responseCode) < 500) {
        response = "client error";
      } else if (Integer.parseInt(responseCode) >= 500 && Integer.parseInt(responseCode) < 600) {
        response = "server error";
      } else {
        response = "";
      }
    } else {
      return "No network";
    }
    return response;
  }

  public void onPreExecute() {}

  public void onPostExecute(String response) {
    String msg;
    switch (response) {
      case "timeout":
      case "No data":
      case "Unknown error":
      case "server error":
      case "error":
      case "client error":
        msg = responseModel.getServermsg();
        setShowalert(msg);
        onAsyncRequestComplete.asyncResponseFailure(
            resultCode, msg, responseModel.getResponseCode());
        break;
      case "No network":
        msg = context.getResources().getString(R.string.check_internet);
        setShowalert(msg);
        onAsyncRequestComplete.asyncResponseFailure(resultCode, msg, "");
        break;
      case "":
        msg = context.getResources().getString(R.string.unknown_error);
        setShowalert(msg);
        onAsyncRequestComplete.asyncResponseFailure(
            resultCode, msg, responseModel.getResponseCode());
        break;
      case "session expired":
        msg = responseModel.getServermsg();
        onAsyncRequestComplete.asyncResponseFailure(
            resultCode, msg, responseModel.getResponseCode());
        break;
      case "success":
        onAsyncRequestComplete.asyncResponse(obj, resultCode);
        break;
    }
  }

  private String checkResponse(
      boolean forRefreshToken, Responsemodel responseModel, int httpUnauthorized) {
    String responseCode = responseModel.getResponseCode();
    String response = responseModel.getResponseData();

    if (responseCode.equalsIgnoreCase("0") && response.equalsIgnoreCase("timeout")) {
      response = "timeout";
    } else if (responseCode.equalsIgnoreCase("0") && response.equalsIgnoreCase("")) {
      response = "error";
    } else if (Integer.parseInt(responseCode) == httpUnauthorized) {
      response = "session expired";
    } else if (Integer.parseInt(responseCode) >= HttpURLConnection.HTTP_OK
        && Integer.parseInt(responseCode) < HttpURLConnection.HTTP_MULT_CHOICE
        && !response.equalsIgnoreCase("")) {
      response = "success";

      if (forRefreshToken) {
        obj = parseJson(responseModel, TokenData.class);
        TokenData tokenData = (TokenData) obj;
        if (tokenData != null) {
          AppController.getHelperSharedPreference()
              .writePreference(context, context.getString(R.string.auth), tokenData.getAccess_token());
          AppController.getHelperSharedPreference()
              .writePreference(
                  context,
                  context.getString(R.string.refreshToken),
                      tokenData.getRefresh_token());
        } else {
          response = "error";
        }
      } else {
        obj = parseJson(responseModel, genericClass);
        if (urlPassed.contains(Urls.BASE_URL_STUDY_DATASTORE_SERVER + "/activity")) {
          try {
            ActivityInfoData activityInfoData = (ActivityInfoData) obj;
            JSONObject jsonObject = new JSONObject(responseModel.getResponseData());
            JSONObject jsonObjectActivity = jsonObject.getJSONObject("activity");
            JSONArray jsonArray = jsonObjectActivity.getJSONArray("steps");
            for (int i = 0; i < jsonArray.length(); i++) {
              JSONObject jsonObject2 = jsonArray.getJSONObject(i);
              JSONObject format = jsonObject2.getJSONObject("format");
              String key = jsonObject2.getString("key");
              String defaultString = null;
              try {
                Object defaultValue = format.get("default");
                defaultString = "";
                if (defaultValue instanceof Integer) {
                  defaultString = "" + (int) defaultValue;
                } else if (defaultValue instanceof Double) {
                  defaultString = "" + (double) defaultValue;
                } else if (defaultValue instanceof Long) {
                  defaultString = "" + (long) defaultValue;
                } else if (defaultValue instanceof String) {
                  defaultString = "" + (String) defaultValue;
                } else {
                  defaultString = "" + defaultValue;
                }
              } catch (JSONException e) {
                defaultString = "";
                Logger.log(e);
              }
              for (int k = 0; k < activityInfoData.getActivity().getSteps().size(); k++) {
                if (activityInfoData
                    .getActivity()
                    .getSteps()
                    .get(k)
                    .getKey()
                    .equalsIgnoreCase(key)) {
                  activityInfoData
                      .getActivity()
                      .getSteps()
                      .get(k)
                      .getFormat()
                      .setDefaultValue(defaultString);
                }
              }
            }
            obj = (T) activityInfoData;

          } catch (Exception e) {
            Logger.log(e);
          }
        }
      }
    } else if (Integer.parseInt(responseCode) >= 400 && Integer.parseInt(responseCode) < 500) {
      response = "client error";
    } else if (Integer.parseInt(responseCode) >= 500 && Integer.parseInt(responseCode) < 600) {
      response = "server error";
    }
    return response;
  }

  private void setShowalert(String msg) {
    if (showAlert) {
      AlertDialog.Builder alertDialogBuilder =
          new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
      alertDialogBuilder.setTitle(
          context.getApplicationInfo().loadLabel(context.getPackageManager()).toString());

      alertDialogBuilder
          .setMessage(msg)
          .setCancelable(false)
          .setPositiveButton(
              context.getResources().getString(R.string.ok),
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                  dialog.dismiss();
                }
              });
      AlertDialog alertDialog = alertDialogBuilder.create();
      alertDialog.show();
    }
  }

  private T parseJson(Responsemodel responseModel, Class genericClass) {
    Gson gson = new Gson();
    try {
      JsonReader reader = new JsonReader(new StringReader(responseModel.getResponseData()));
      reader.setLenient(true);
      return gson.fromJson(reader, genericClass);
    } catch (Exception e) {
      Logger.log(e);
      return null;
    }
  }
}
