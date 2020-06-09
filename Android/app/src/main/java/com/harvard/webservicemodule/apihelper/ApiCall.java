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
import com.harvard.AppConfig;
import com.harvard.R;
import com.harvard.studyappmodule.activitybuilder.model.servicemodel.ActivityInfoData;
import com.harvard.usermodule.webservicemodel.RefreshToken;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import com.harvard.utils.SharedPreferenceHelper;
import com.harvard.utils.Urls;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
  private JSONArray jsonArray;
  private String webserviceType;
  private Class<T> genericClass;
  private Context context;
  private HashMap<String, String> headersData;
  private HashMap<String, String> formData;
  private HashMap<String, File> filesData;
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

  public void apiCallDeleteJsonArray(
      String url,
      HashMap<String, String> headers,
      Class<T> genericClass,
      JSONArray jsonArray,
      int resultCode,
      V v,
      boolean showAlert,
      String serverType) {
    this.urlPassed = url;
    this.genericClass = genericClass;
    this.headersData = headers;
    this.jsonArray = jsonArray;
    this.webserviceType = "delete_jsonArray";
    this.resultCode = resultCode;
    this.onAsyncRequestComplete = (OnAsyncRequestComplete) v;
    this.showAlert = showAlert;
    this.serverType = serverType;
    execute();
  }

  /**
   * To make a POST Multi-part request.
   *
   * @param url url path
   * @param genericClass model class to parse
   * @param headers null if no header to pass
   * @param formData null if no form data
   * @param files null if no files to upload
   * @param resultCode call back code
   * @param v activity context
   * @param showAlert wherever to show alert
   */
  public void apiCallMultipart(
      String url,
      Class<T> genericClass,
      HashMap<String, String> headers,
      HashMap<String, String> formData,
      HashMap<String, File> files,
      int resultCode,
      V v,
      boolean showAlert,
      String serverType) {
    this.urlPassed = url;
    this.genericClass = genericClass;
    this.headersData = headers;
    this.formData = formData;
    this.filesData = files;
    this.webserviceType = "post_multi";
    this.resultCode = resultCode;
    this.onAsyncRequestComplete = (OnAsyncRequestComplete) v;
    this.serverType = serverType;
    this.showAlert = showAlert;
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

  /**
   * To make a DELETE Hashmap request.
   *
   * @param url url path
   * @param genericClass model class to parse
   * @param headers null if no header to pass
   * @param hashMap params
   * @param resultCode call back code
   * @param v Activity context
   * @param showAlert wherever to show alert
   */
  public void apiCallDeleteHashmap(
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
    this.webserviceType = "delete_hashmap";
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
      if (headersData != null
          && (headersData.containsKey("accessToken") || headersData.containsKey("auth"))) {
        headersData.put(
            AppConfig.CLIENT_TOKEN,
            AppController.getHelperSharedPreference()
                .readPreference(context, context.getString(R.string.clientToken), ""));
      }

      switch (webserviceType) {
        case "get":
          responseModel = HttpRequest.getRequest(urlPassed, headersData, serverType);
          break;
        case "post_hashmap":
          responseModel =
              HttpRequest.postRequestsWithHashmap(
                  urlPassed, hashmapData, headersData, serverType);
          break;
        case "post_json":
          responseModel =
              HttpRequest.makePostRequestWithJson(urlPassed, jsonData, headersData, serverType);
          break;
        case "post_multi":
          responseModel =
              HttpRequest.postRequestMultipart(
                  urlPassed, headersData, formData, filesData, serverType);
          break;
        case "delete_hashmap":
          responseModel =
              HttpRequest.deleteRequestsWithHashmap(
                  urlPassed, hashmapData, headersData, serverType);
          break;
        case "delete_json":
          responseModel =
              HttpRequest.makeDeleteRequestWithJson(
                  urlPassed, jsonData, headersData, serverType);
          break;
        case "delete_jsonArray":
          responseModel =
              HttpRequest.makeDeleteRequestWithJsonArray(
                  urlPassed, jsonArray, headersData, serverType);
          break;
      }
      String url = "";
      if (url.equalsIgnoreCase(urlPassed)) {
        responseCode = "200";
        response = "";
        try {
          JSONObject jsonObject = new JSONObject(loadJsonFromAsset());
          response = jsonObject.toString();
          responseModel = new Responsemodel();
          responseModel.setResponse(response);
          responseModel.setResponseCode(responseCode);
          responseModel.setResponseData(response);
          responseModel.setServermsg("success");
        } catch (JSONException e) {
          Logger.log(e);
        }
      } else {
        responseCode = responseModel.getResponseCode();
        response = responseModel.getResponse();
      }
      if (responseCode.equalsIgnoreCase("0") && response.equalsIgnoreCase("timeout")) {
        response = "timeout";
      } else if (responseCode.equalsIgnoreCase("0") && response.equalsIgnoreCase("")) {
        response = "error";
      } else if (Integer.parseInt(responseCode) >= 201
          && Integer.parseInt(responseCode) < 300
          && response.equalsIgnoreCase("")) {
        response = "No data";
      } else if (Integer.parseInt(responseCode) >= 400
          && Integer.parseInt(responseCode) < 500
          && response.equalsIgnoreCase("http_not_ok")) {
        response = "client error";
      } else if (Integer.parseInt(responseCode) >= 500
          && Integer.parseInt(responseCode) < 600
          && response.equalsIgnoreCase("http_not_ok")) {
        response = "server error";
      } else if (response.equalsIgnoreCase("http_not_ok")) {
        response = "Unknown error";
      } else if (Integer.parseInt(responseCode) == HttpURLConnection.HTTP_UNAUTHORIZED) {
        response = "session expired";

        if (!this.urlPassed.contains(Urls.LOGIN)) {
          String refreshTokenUrl = Urls.REFRESH_TOKEN;
          JSONObject refreshTokenJsonData = new JSONObject();
          try {
            refreshTokenJsonData.put(
                "refreshToken",
                AppController.getHelperSharedPreference()
                    .readPreference(context, context.getString(R.string.refreshToken), ""));
          } catch (JSONException e) {
            Logger.log(e);
          }
          HashMap<String, String> refreshTokenHeader = new HashMap<>();
          refreshTokenHeader.put(
              "userId",
              SharedPreferenceHelper.readPreference(
                  context, context.getString(R.string.userid), ""));

          responseModel =
              HttpRequest.makePostRequestWithJsonRefreshToken(
                  refreshTokenUrl, refreshTokenJsonData, refreshTokenHeader, "");
          String s = checkResponse(true, responseModel, HttpURLConnection.HTTP_FORBIDDEN);
          if (s.equalsIgnoreCase("success")) {
            if (headersData != null
                && (headersData.containsKey("accessToken") || headersData.containsKey("auth"))) {
              String s1 =
                  AppController.getHelperSharedPreference()
                      .readPreference(context, context.getString(R.string.auth), "");
              headersData.put("accessToken", "" + s1);
            }
            if (headersData != null
                && (headersData.containsKey("accessToken") || headersData.containsKey("auth"))) {
              headersData.put(
                  AppConfig.CLIENT_TOKEN,
                  AppController.getHelperSharedPreference()
                      .readPreference(context, context.getString(R.string.clientToken), ""));
            }
            switch (webserviceType) {
              case "get":
                responseModel = HttpRequest.getRequest(urlPassed, headersData, serverType);
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
              case "post_multi":
                responseModel =
                    HttpRequest.postRequestMultipart(
                        urlPassed, headersData, formData, filesData, serverType);
                break;
              case "delete_hashmap":
                responseModel =
                    HttpRequest.deleteRequestsWithHashmap(
                        urlPassed, hashmapData, headersData, serverType);
                break;
              case "delete_json":
                responseModel =
                    HttpRequest.makeDeleteRequestWithJson(
                        urlPassed, jsonData, headersData, serverType);
                break;
              case "delete_jsonArray":
                responseModel =
                    HttpRequest.makeDeleteRequestWithJsonArray(
                        urlPassed, jsonArray, headersData, serverType);
                break;
            }
            response = checkResponse(false, responseModel, HttpURLConnection.HTTP_UNAUTHORIZED);
          } else if (s.equalsIgnoreCase("session expired")
              || s.equalsIgnoreCase("Unknown error")
              || s.equalsIgnoreCase("server error")
              || s.equalsIgnoreCase("client error")
              || s.equalsIgnoreCase("No data")) {
            responseModel.setResponseCode("401");
            responseModel.setServermsg("session expired");
          }
        }
      } else if (Integer.parseInt(responseCode) == HttpURLConnection.HTTP_OK
          && !response.equalsIgnoreCase("")) {
        response = "success";

        obj = parseJson(responseModel, genericClass);
        if (urlPassed.contains(Urls.BASE_URL_WCP_SERVER + "activity")) {
          try {
            ActivityInfoData activityInfoData = (ActivityInfoData) obj;
            JSONObject jsonObject = new JSONObject(responseModel.getResponse());
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
      } else {
        response = "";
      }
    } else {
      return "No network";
    }
    return response;
  }

  public void onPreExecute() {}

  private String loadJsonFromAsset() {
    String json = null;
    try {
      InputStream is = context.getAssets().open("Activity_Question.json");
      int size = is.available();
      byte[] buffer = new byte[size];
      is.read(buffer);
      is.close();
      json = new String(buffer, "UTF-8");
    } catch (IOException ex) {
      Logger.log(ex);
      return null;
    }
    return json;
  }

  public void onPostExecute(String response) {
    String msg;
    switch (response) {
      case "timeout":
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
      case "error":
        msg = responseModel.getServermsg();
        setShowalert(msg);
        onAsyncRequestComplete.asyncResponseFailure(
            resultCode, msg, responseModel.getResponseCode());
        break;
      case "":
        msg = context.getResources().getString(R.string.unknown_error);
        setShowalert(msg);
        onAsyncRequestComplete.asyncResponseFailure(
            resultCode, msg, responseModel.getResponseCode());
        break;
      case "client error":
        msg = responseModel.getServermsg();
        setShowalert(msg);
        onAsyncRequestComplete.asyncResponseFailure(
            resultCode, msg, responseModel.getResponseCode());
        break;
      case "server error":
        msg = responseModel.getServermsg();
        setShowalert(msg);
        onAsyncRequestComplete.asyncResponseFailure(
            resultCode, msg, responseModel.getResponseCode());
        break;
      case "Unknown error":
        msg = responseModel.getServermsg();
        setShowalert(msg);
        onAsyncRequestComplete.asyncResponseFailure(
            resultCode, msg, responseModel.getResponseCode());
        break;
      case "No data":
        msg = responseModel.getServermsg();
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
    String response = responseModel.getResponse();

    if (responseCode.equalsIgnoreCase("0") && response.equalsIgnoreCase("timeout")) {
      response = "timeout";
    } else if (responseCode.equalsIgnoreCase("0") && response.equalsIgnoreCase("")) {
      response = "error";
    } else if (Integer.parseInt(responseCode) >= 201
        && Integer.parseInt(responseCode) < 300
        && response.equalsIgnoreCase("")) {
      response = "No data";
    } else if (Integer.parseInt(responseCode) >= 400
        && Integer.parseInt(responseCode) < 500
        && response.equalsIgnoreCase("http_not_ok")) {
      response = "client error";
    } else if (Integer.parseInt(responseCode) >= 500
        && Integer.parseInt(responseCode) < 600
        && response.equalsIgnoreCase("http_not_ok")) {
      response = "server error";
    } else if (response.equalsIgnoreCase("http_not_ok")) {
      response = "Unknown error";
    } else if (Integer.parseInt(responseCode) == httpUnauthorized) {
      response = "session expired";
    } else if (Integer.parseInt(responseCode) == HttpURLConnection.HTTP_OK
        && !response.equalsIgnoreCase("")) {
      response = "success";

      if (forRefreshToken) {
        obj = parseJson(responseModel, RefreshToken.class);
        RefreshToken refreshToken = (RefreshToken) obj;
        if (refreshToken != null) {
          AppController.getHelperSharedPreference()
              .writePreference(context, context.getString(R.string.auth), refreshToken.getAuth());
          AppController.getHelperSharedPreference()
              .writePreference(
                  context,
                  context.getString(R.string.refreshToken),
                  refreshToken.getRefreshToken());
          AppController.getHelperSharedPreference()
              .writePreference(
                  context,
                  context.getString(R.string.clientToken),
                  refreshToken.getClientToken());
        } else {
          response = "error";
        }
      } else {
        obj = parseJson(responseModel, genericClass);
        if (urlPassed.contains(Urls.BASE_URL_WCP_SERVER + "activity")) {
          try {
            ActivityInfoData activityInfoData = (ActivityInfoData) obj;
            JSONObject jsonObject = new JSONObject(responseModel.getResponse());
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
      JsonReader reader = new JsonReader(new StringReader(responseModel.getResponse()));
      reader.setLenient(true);
      return gson.fromJson(reader, genericClass);
    } catch (Exception e) {
      Logger.log(e);
      return null;
    }
  }
}
