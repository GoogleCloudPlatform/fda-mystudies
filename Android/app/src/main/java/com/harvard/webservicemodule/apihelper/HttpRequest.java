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

import android.os.Build;
import android.util.Base64;
import com.google.gson.Gson;
import com.harvard.AppConfig;
import com.harvard.utils.Logger;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

public class HttpRequest {

  private static String basicAuth = AppConfig.API_TOKEN;
  private static int TimeoutInterval = 180000;
  private static String errorDescKey = "error_description";
  private static String headerErrorKey = "StatusMessage";
  private static String SERVER_TYPE_STUDY_DATASTORE = "STUDY_DATASTORE";
  private static String CONTENT_TYPE_KEY = "Content-Type";
  private static String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
  private static String APPLICATION_JSON = "application/json";

  /**
   * To make a Get request.
   *
   * @param url --> url path
   * @param headersData --> null if no header
   * @return Responsemodel
   */
  public static Responsemodel getRequest(
      String url, HashMap<String, String> headersData, String serverType) {
    return getResponse(url, "GET", serverType, headersData, null);
  }

  /**
   * To make post request using hashmap.
   *
   * @param url --> url path
   * @param params --> Hashmap params
   * @param headersData --> null if no header
   * @return Responsemodel
   */
  static Responsemodel postRequestsWithHashmap(
      String url,
      HashMap<String, String> params,
      HashMap<String, String> headersData,
      String serverType) {
    String bodyParams;
    if (headersData != null
        && headersData.containsKey(CONTENT_TYPE_KEY)
        && headersData.get(CONTENT_TYPE_KEY).equalsIgnoreCase(APPLICATION_X_WWW_FORM_URLENCODED)) {
      bodyParams = getDataString(params);
    } else {
      bodyParams = getPostDataString(params);
    }
    return getResponse(url, "POST", serverType, headersData, bodyParams);
  }

  /**
   * To make post request using json object.
   *
   * @param url -->url path
   * @param jsonObject -->json object
   * @param headersData --> null if no header
   * @return Responsemodel
   */
  static Responsemodel makePostRequestWithJson(
      String url, JSONObject jsonObject, HashMap<String, String> headersData, String serverType) {

    return getResponse(url, "POST", serverType, headersData, jsonObject.toString());
  }

  /**
   * To make put request using json object.
   *
   * @param url -->url path
   * @param jsonObject -->json object
   * @param headersData --> null if no header
   * @return Responsemodel
   */
  static Responsemodel makePutRequestWithJson(
      String url, JSONObject jsonObject, HashMap<String, String> headersData, String serverType) {
    return getResponse(url, "PUT", serverType, headersData, jsonObject.toString());
  }

  /**
   * method to parse hashmap to json.
   *
   * @param params --> params of hash map
   * @return String
   */
  private static String getPostDataString(HashMap<String, String> params) {
    return new Gson().toJson(params);
  }

  private static String getDataString(HashMap<String, String> params) {
    StringBuilder result = new StringBuilder();
    try {
      boolean first = true;
      for (Map.Entry<String, String> entry : params.entrySet()) {
        if (first) {
          first = false;
        } else {
          result.append("&");
        }
        result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.name()));
        result.append("=");
        result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.name()));
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return result.toString();
  }

  static Responsemodel makeDeleteRequestWithJson(
      String url, JSONObject jsonObject, HashMap<String, String> headersData, String serverType) {
    Responsemodel responseModel = new Responsemodel();
    String responseData = "";
    int responseCode = 0;
    if (Build.VERSION.SDK_INT >= 21) {
      return getResponse(url, "DELETE", serverType, headersData, jsonObject.toString());
    } else {
      try {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, TimeoutInterval);
        HttpConnectionParams.setSoTimeout(httpParams, TimeoutInterval);
        OwnHttpDelete httppost = new OwnHttpDelete(url);

        if (headersData != null) {
          Set mapSet = (Set) headersData.entrySet();
          Iterator mapIterator = mapSet.iterator();
          while (mapIterator.hasNext()) {
            Map.Entry mapEntry = (Map.Entry) mapIterator.next();
            String keyValue = (String) mapEntry.getKey();
            String value = (String) mapEntry.getValue();
            httppost.addHeader(keyValue, value);
          }
        }
        httppost.addHeader(CONTENT_TYPE_KEY, APPLICATION_JSON);
        httppost.addHeader(AppConfig.APP_ID_KEY, AppConfig.APP_ID_VALUE);

        StringEntity params1 = new StringEntity(jsonObject.toString());
        httppost.setEntity(params1);

        // Execute and get the response.
        HttpClient httpclient = new DefaultHttpClient(httpParams);
        HttpResponse response1 = httpclient.execute(httppost);
        responseCode = response1.getStatusLine().getStatusCode();

        if (isSuccessfull(responseCode)) {
          responseData = getResponseData(response1);
          responseModel.setServermsg("success");
        } else if (isUnauthorized(responseCode)) {
          responseData = "session expired";
        } else {
          if (response1.getFirstHeader(headerErrorKey) != null) {
            responseModel.setServermsg(response1.getFirstHeader(headerErrorKey).getValue());
          } else {
            responseData = getResponseData(response1);
            JSONObject responseDataJson = new JSONObject(responseData);
            if (responseDataJson.has(errorDescKey)) {
              responseModel.setServermsg(responseDataJson.getString(errorDescKey));
            } else {
              responseModel.setServermsg("server error");
            }
          }
        }
      } catch (ConnectTimeoutException | SocketTimeoutException e) {
        responseModel.setServermsg("No internet connection/cannot connect to server");
        responseData = "timeout";
        Logger.log(e);
      } catch (Exception e) {
        responseModel.setServermsg("No internet connection/cannot connect to server");
        responseData = "";
        Logger.log(e);
      }
    }

    responseModel.setResponseCode("" + responseCode);
    responseModel.setResponseData(responseData);
    return responseModel;
  }

  private static String getResponseData(HttpResponse response1) throws IOException {
    StringBuilder responseData = new StringBuilder();
    HttpEntity entity = response1.getEntity();
    String line;
    if (entity != null) {
      InputStream instream = entity.getContent();
      try {
        BufferedReader br = new BufferedReader(new InputStreamReader(instream));
        while ((line = br.readLine()) != null) {
          responseData.append(line);
        }
        br.close();
      } finally {
        instream.close();
      }
    }
    return responseData.toString();
  }

  private static class OwnHttpDelete extends HttpPost {
    public static final String METHOD_NAME = "DELETE";

    public OwnHttpDelete() {
      super();
    }

    public OwnHttpDelete(URI uri) {
      super(uri);
    }

    public OwnHttpDelete(String uri) {
      super(uri);
    }

    public String getMethod() {
      return METHOD_NAME;
    }
  }

  private static Responsemodel getResponse(
      String url,
      String methodType,
      String serverType,
      HashMap<String, String> headersData,
      String bodyParams) {
    Responsemodel responseModel = new Responsemodel();
    StringBuilder response = new StringBuilder();
    String responseData = "";
    int responseCode = 0;
    try {
      URL obj = new URL(url);
      HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
      conn.setRequestMethod(methodType);
      conn.setReadTimeout(TimeoutInterval);
      conn.setConnectTimeout(TimeoutInterval);
      conn.setRequestProperty(CONTENT_TYPE_KEY, APPLICATION_JSON);
      conn.setRequestProperty(AppConfig.APP_ID_KEY, AppConfig.APP_ID_VALUE);

      if (serverType.equalsIgnoreCase(SERVER_TYPE_STUDY_DATASTORE)) {
        String encoding = Base64.encodeToString(basicAuth.getBytes(), Base64.DEFAULT);
        conn.setRequestProperty("Authorization", "Basic " + encoding);
        conn.setRequestProperty(AppConfig.STUDY_DATASTORE_APP_ID_KEY, AppConfig.APP_ID_VALUE);
      }

      if (headersData != null) {
        Set mapSet = (Set) headersData.entrySet();
        Iterator mapIterator = mapSet.iterator();
        while (mapIterator.hasNext()) {
          Map.Entry mapEntry = (Map.Entry) mapIterator.next();
          String keyValue = (String) mapEntry.getKey();
          String value = (String) mapEntry.getValue();
          conn.setRequestProperty(keyValue, value);
        }
      }

      if (!methodType.equalsIgnoreCase("get")) {
        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        BufferedWriter writer =
            new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
        writer.write(bodyParams);
        writer.flush();
        writer.close();
        os.close();
      }

      try {
        // Will throw IOException if server responds with 401.
        responseCode = conn.getResponseCode();
      } catch (IOException e) {
        // Will return 401, because now connection has the correct internal state.
        responseCode = conn.getResponseCode();
      }

      if (isSuccessfull(responseCode)) {
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
        }
        in.close();
        conn.disconnect();
        responseData = response.toString();
        responseModel.setServermsg("success");
      } else if (isUnauthorized(responseCode)) {
        responseData = "session expired";
      } else {
        if (conn.getHeaderField(headerErrorKey) != null) {
          responseModel.setServermsg(conn.getHeaderField(headerErrorKey));
        } else {
          BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
          String inputLine;

          while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
          }
          in.close();
          conn.disconnect();
          responseData = response.toString();

          JSONObject responseDataJson = new JSONObject(responseData);
          if (responseDataJson.has(errorDescKey)) {
            responseModel.setServermsg(responseDataJson.getString(errorDescKey));
          } else {
            responseModel.setServermsg("server error");
          }
        }
      }
    } catch (ConnectException e) {
      responseModel.setServermsg("No internet connection/cannot connect to server");
      responseData = "timeout";
      Logger.log(e);
    } catch (Exception e) {
      responseModel.setServermsg("No internet connection/cannot connect to server");
      responseData = "";
      Logger.log(e);
    }

    responseModel.setResponseCode(String.valueOf(responseCode));
    responseModel.setResponseData(responseData);
    return responseModel;
  }

  private static boolean isUnauthorized(int responseCode) {
    return responseCode == HttpURLConnection.HTTP_UNAUTHORIZED;
  }

  private static boolean isSuccessfull(int responseCode) {
    return responseCode >= HttpURLConnection.HTTP_OK
        && responseCode < HttpURLConnection.HTTP_MULT_CHOICE;
  }
}
