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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
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

  /**
   * To make a Get request.
   *
   * @param url         --> url path
   * @param headersData --> null if no header
   * @return Responsemodel
   */
  public static Responsemodel getRequest(
          String url, HashMap<String, String> headersData, String serverType) {
    StringBuffer response = new StringBuffer();
    Responsemodel responseModel = new Responsemodel();
    String responseData;
    HttpURLConnection conn;
    int responseCode = 0;
    try {
      URL obj = new URL(url);
      conn = (HttpURLConnection) obj.openConnection();
      conn.setRequestMethod("GET");
      conn.setReadTimeout(TimeoutInterval); // 3 min timeout
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setRequestProperty(AppConfig.APP_ID_KEY, AppConfig.APP_ID_VALUE);

      if (serverType.equalsIgnoreCase("WCP")) {
        String encoding = Base64.encodeToString(basicAuth.getBytes(), Base64.DEFAULT);
        conn.setRequestProperty("Authorization", "Basic " + encoding);
        conn.setRequestProperty(AppConfig.WCP_APP_ID_KEY, AppConfig.APP_ID_VALUE);
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
      try {
        // Will throw IOException if server responds with 401.
        responseCode = conn.getResponseCode();
      } catch (IOException e) {
        // Will return 401, because now connection has the correct internal state.
        responseCode = conn.getResponseCode();
      }

      if (responseCode >= HttpURLConnection.HTTP_OK && responseCode < HttpURLConnection.HTTP_MULT_CHOICE) {
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
        }
        in.close();
        conn.disconnect();
        responseData = response.toString();
        responseModel.setServermsg("success");
      } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
        responseData = "session expired";
      } else {
        if (conn.getHeaderField(headerErrorKey) != null) {
          responseModel.setServermsg(conn.getHeaderField(headerErrorKey));
          responseData = "http_not_ok";
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

    responseModel.setResponseCode("" + responseCode);
    responseModel.setResponseData(responseData);

    return responseModel;
  }

  /**
   * To make post request using hashmap.
   *
   * @param url         --> url path
   * @param params      --> Hashmap params
   * @param headersData --> null if no header
   * @return Responsemodel
   */
  static Responsemodel postRequestsWithHashmap(
          String url,
          HashMap<String, String> params,
          HashMap<String, String> headersData,
          String serverType) {
    Responsemodel responseModel = new Responsemodel();
    String responseData = "";
    StringBuffer response = new StringBuffer();
    int responseCode = 0;
    URL url1;
    try {
      url1 = new URL(url);
      HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
      conn.setReadTimeout(TimeoutInterval);
      conn.setConnectTimeout(TimeoutInterval);
      conn.setRequestMethod("POST");
      conn.setDoInput(true);
      conn.setRequestProperty("Content-Type", "application/json");

      conn.setRequestProperty(AppConfig.APP_ID_KEY, AppConfig.APP_ID_VALUE);

      if (serverType.equalsIgnoreCase("WCP")) {
        String encoding = Base64.encodeToString(basicAuth.getBytes(), Base64.DEFAULT);
        conn.setRequestProperty("Authorization", "Basic " + encoding);
        conn.setRequestProperty(AppConfig.WCP_APP_ID_KEY, AppConfig.APP_ID_VALUE);
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

      conn.setDoOutput(true);

      OutputStream os = conn.getOutputStream();
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
      if (conn.getRequestProperty("Content-Type").equalsIgnoreCase("application/json")) {
        writer.write(getPostDataString(params));
      } else {
        writer.write(getDataString(params));
      }
      writer.flush();
      writer.close();
      os.close();

      try {
        // Will throw IOException if server responds with 401.
        responseCode = conn.getResponseCode();
      } catch (IOException e) {
        Logger.log(e);
        // Will return 401, because now connection has the correct internal state.
        responseCode = conn.getResponseCode();
      }
      if (responseCode >= HttpURLConnection.HTTP_OK && responseCode < HttpURLConnection.HTTP_MULT_CHOICE) {
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
        }
        in.close();
        conn.disconnect();
        responseData = response.toString();
        responseModel.setServermsg("success");
      } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
        responseData = "session expired";
      } else {
        if (conn.getHeaderField(headerErrorKey) != null) {
          responseModel.setServermsg(conn.getHeaderField(headerErrorKey));
          responseData = "http_not_ok";
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

    responseModel.setResponseCode("" + responseCode);
    responseModel.setResponseData(responseData);
    return responseModel;
  }

  /**
   * To make post request using json object.
   *
   * @param urlpath     -->url path
   * @param jsonObject  -->json object
   * @param headersData --> null if no header
   * @return Responsemodel
   */
  static Responsemodel makePostRequestWithJson(
          String urlpath,
          JSONObject jsonObject,
          HashMap<String, String> headersData,
          String serverType) {
    Responsemodel responseModel = new Responsemodel();
    StringBuffer response = new StringBuffer();
    String responseData = "";
    int responseCode = 0;
    URL url1;
    try {
      url1 = new URL(urlpath);
      HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
      conn.setReadTimeout(TimeoutInterval);
      conn.setConnectTimeout(TimeoutInterval);
      conn.setRequestMethod("POST");
      conn.setDoInput(true);
      conn.setRequestProperty("Content-Type", "application/json");

      conn.setRequestProperty(AppConfig.APP_ID_KEY, AppConfig.APP_ID_VALUE);

      if (serverType.equalsIgnoreCase("WCP")) {
        String encoding = Base64.encodeToString(basicAuth.getBytes(), Base64.DEFAULT);
        conn.setRequestProperty("Authorization", "Basic " + encoding);
        conn.setRequestProperty(AppConfig.WCP_APP_ID_KEY, AppConfig.APP_ID_VALUE);
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

      conn.setDoOutput(true);

      OutputStream os = conn.getOutputStream();
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
      writer.write(jsonObject.toString());

      writer.flush();
      writer.close();
      os.close();

      try {
        // Will throw IOException if server responds with 401.
        responseCode = conn.getResponseCode();
      } catch (IOException e) {
        // Will return 401, because now connection has the correct internal state.
        responseCode = conn.getResponseCode();
      }

      if (responseCode >= HttpURLConnection.HTTP_OK && responseCode < HttpURLConnection.HTTP_MULT_CHOICE) {
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
        }
        in.close();
        conn.disconnect();
        responseData = response.toString();
        responseModel.setServermsg("success");
      } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
        responseData = "session expired";
      } else {
        if (conn.getHeaderField(headerErrorKey) != null) {
          responseModel.setServermsg(conn.getHeaderField(headerErrorKey));
          responseData = "http_not_ok";
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

    responseModel.setResponseCode("" + responseCode);
    responseModel.setResponseData(responseData);
    return responseModel;
  }

  /**
   * To make patch request using json object.
   *
   * @param urlpath     -->url path
   * @param jsonObject  -->json object
   * @param headersData --> null if no header
   * @return Responsemodel
   */
  static Responsemodel makePatchRequestWithJson(
          String urlpath,
          JSONObject jsonObject,
          HashMap<String, String> headersData,
          String serverType) {
    Responsemodel responseModel = new Responsemodel();
    String responseData = "";
    StringBuffer response = new StringBuffer();
    int responseCode = 0;
    URL url1;
    try {
      url1 = new URL(urlpath);
      HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
      conn.setReadTimeout(TimeoutInterval);
      conn.setConnectTimeout(TimeoutInterval);
      conn.setRequestMethod("PATCH");
      conn.setDoInput(true);
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setRequestProperty(AppConfig.APP_ID_KEY, AppConfig.APP_ID_VALUE);

      if (serverType.equalsIgnoreCase("WCP")) {
        String encoding = Base64.encodeToString(basicAuth.getBytes(), Base64.DEFAULT);
        conn.setRequestProperty("Authorization", "Basic " + encoding);
        conn.setRequestProperty(AppConfig.WCP_APP_ID_KEY, AppConfig.APP_ID_VALUE);
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

      conn.setDoOutput(true);

      OutputStream os = conn.getOutputStream();
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
      writer.write(jsonObject.toString());

      writer.flush();
      writer.close();
      os.close();

      try {
        // Will throw IOException if server responds with 401.
        responseCode = conn.getResponseCode();
      } catch (IOException e) {
        // Will return 401, because now connection has the correct internal state.
        responseCode = conn.getResponseCode();
      }

      if (responseCode >= HttpURLConnection.HTTP_OK && responseCode < HttpURLConnection.HTTP_MULT_CHOICE) {
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
        }
        in.close();
        conn.disconnect();
        responseData = response.toString();
        responseModel.setServermsg("success");
      } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
        responseData = "session expired";
      } else {
        if (conn.getHeaderField(headerErrorKey) != null) {
          responseModel.setServermsg(conn.getHeaderField(headerErrorKey));
          responseData = "http_not_ok";
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

    responseModel.setResponseCode("" + responseCode);
    responseModel.setResponseData(responseData);
    return responseModel;
  }

  /**
   * To make put request using json object.
   *
   * @param urlpath     -->url path
   * @param jsonObject  -->json object
   * @param headersData --> null if no header
   * @return Responsemodel
   */
  static Responsemodel makePutRequestWithJson(
          String urlpath,
          JSONObject jsonObject,
          HashMap<String, String> headersData,
          String serverType) {
    Responsemodel responseModel = new Responsemodel();
    StringBuffer response = new StringBuffer();
    String responseData = "";
    int responseCode = 0;
    URL url1;
    try {
      url1 = new URL(urlpath);
      HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
      conn.setReadTimeout(TimeoutInterval);
      conn.setConnectTimeout(TimeoutInterval);
      conn.setRequestMethod("PUT");
      conn.setDoInput(true);
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setRequestProperty(AppConfig.APP_ID_KEY, AppConfig.APP_ID_VALUE);

      if (serverType.equalsIgnoreCase("WCP")) {
        String encoding = Base64.encodeToString(basicAuth.getBytes(), Base64.DEFAULT);
        conn.setRequestProperty("Authorization", "Basic " + encoding);
        conn.setRequestProperty(AppConfig.WCP_APP_ID_KEY, AppConfig.APP_ID_VALUE);
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

      conn.setDoOutput(true);

      OutputStream os = conn.getOutputStream();
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
      writer.write(jsonObject.toString());

      writer.flush();
      writer.close();
      os.close();

      try {
        // Will throw IOException if server responds with 401.
        responseCode = conn.getResponseCode();
      } catch (IOException e) {
        // Will return 401, because now connection has the correct internal state.
        responseCode = conn.getResponseCode();
      }

      if (responseCode >= HttpURLConnection.HTTP_OK && responseCode < HttpURLConnection.HTTP_MULT_CHOICE) {
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
        }
        in.close();
        conn.disconnect();
        responseData = response.toString();
        responseModel.setServermsg("success");
      } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
        responseData = "session expired";
      } else {
        if (conn.getHeaderField(headerErrorKey) != null) {
          responseModel.setServermsg(conn.getHeaderField(headerErrorKey));
          responseData = "http_not_ok";
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

    responseModel.setResponseCode("" + responseCode);
    responseModel.setResponseData(responseData);
    return responseModel;
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

  private static String getDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
    StringBuilder result = new StringBuilder();
    boolean first = true;
    for (Map.Entry<String, String> entry : params.entrySet()) {
      if (first) {
        first = false;
      } else {
        result.append("&");
      }
      result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
      result.append("=");
      result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
    }
    return result.toString();
  }

  /**
   * To make post request for form data and files upload.
   *
   * @param urlPath  --> url path
   * @param headers  --> null if no header
   * @param formData --> null if no form data
   * @param files    --> null if no files
   * @return web-service response as String
   */
  static Responsemodel postRequestMultipart(
          String urlPath,
          HashMap<String, String> headers,
          HashMap<String, String> formData,
          HashMap<String, File> files,
          String serverType) {
    Responsemodel responseModel = new Responsemodel();
    HttpURLConnection conn;
    StringBuffer response = new StringBuffer();
    String responseData = "";
    String lineFeed = "\r\n";
    int responseCode = 0;
    try {
      URL url = new URL(urlPath);
      conn = (HttpURLConnection) url.openConnection();
      conn.setUseCaches(false);
      conn.setDoOutput(true); // indicates POST method
      conn.setDoInput(true);
      conn.setReadTimeout(TimeoutInterval);
      conn.setConnectTimeout(TimeoutInterval);
      conn.setRequestProperty("Content-Type", "multipart/form-data;");
      conn.setRequestProperty("User-Agent", "CodeJava Agent");
      conn.setRequestProperty(AppConfig.APP_ID_KEY, AppConfig.APP_ID_VALUE);

      if (serverType.equalsIgnoreCase("WCP")) {
        String encoding = Base64.encodeToString(basicAuth.getBytes(), Base64.DEFAULT);
        conn.setRequestProperty("Authorization", "Basic " + encoding);
        conn.setRequestProperty(AppConfig.WCP_APP_ID_KEY, AppConfig.APP_ID_VALUE);
      }

      OutputStream outputStream = conn.getOutputStream();
      PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true);

      if (headers != null) {
        Set keys = headers.keySet();
        for (Iterator i = keys.iterator(); i.hasNext(); ) {
          writer.append(i.next() + ": " + headers.get(i.next())).append(lineFeed);
          writer.flush();
        }
      }
      if (formData != null) {
        Set keys = formData.keySet();
        for (Iterator i = keys.iterator(); i.hasNext(); ) {
          writer.append(lineFeed);
          writer
                  .append("Content-Disposition: form-data; name=\"" + i.next() + "\"")
                  .append(lineFeed);
          writer.append("Content-Type: text/plain; charset=UTF-8").append(lineFeed);
          writer.append(lineFeed);
          writer.append(formData.get(i.next())).append(lineFeed);
          writer.flush();
        }
      }
      if (files != null) {
        Set keys = files.keySet();
        for (Iterator i = keys.iterator(); i.hasNext(); ) {
          String fileName = files.get(i.next()).getName();
          writer
                  .append(
                          "Content-Disposition: form-data; name=\""
                                  + i.next()
                                  + "\"; filename=\""
                                  + fileName
                                  + "\"")
                  .append(lineFeed);
          writer
                  .append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName))
                  .append(lineFeed);
          writer.append("Content-Transfer-Encoding: binary").append(lineFeed);
          writer.append(lineFeed);
          writer.flush();

          FileInputStream inputStream = new FileInputStream(files.get(i.next()));
          byte[] buffer = new byte[4096];
          int bytesRead = -1;
          while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
          }
          outputStream.flush();
          inputStream.close();
          writer.append(lineFeed);
          writer.flush();
        }
      }
      writer.close();
      // checks server's status code first
      responseCode = conn.getResponseCode();
      if (responseCode >= HttpURLConnection.HTTP_OK && responseCode < HttpURLConnection.HTTP_MULT_CHOICE) {
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
        }
        in.close();
        conn.disconnect();
        responseData = response.toString();
        responseModel.setServermsg("success");
      } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
        responseData = "session expired";
      } else {
        if (conn.getHeaderField(headerErrorKey) != null) {
          responseModel.setServermsg(conn.getHeaderField(headerErrorKey));
          responseData = "http_not_ok";
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

    responseModel.setResponseCode("" + responseCode);
    responseModel.setResponseData(responseData);
    return responseModel;
  }

  static Responsemodel makeDeleteRequestWithJson(
          String urlpath,
          JSONObject jsonObject,
          HashMap<String, String> headersData,
          String serverType) {
    Responsemodel responseModel = new Responsemodel();
    StringBuffer response = new StringBuffer();
    String responseData = "";
    int responseCode = 0;
    URL url1;
    if (Build.VERSION.SDK_INT >= 21) {
      try {
        url1 = new URL(urlpath);
        HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
        conn.setReadTimeout(TimeoutInterval);
        conn.setConnectTimeout(TimeoutInterval);
        conn.setRequestMethod("DELETE");
        conn.setDoInput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty(AppConfig.APP_ID_KEY, AppConfig.APP_ID_VALUE);

        if (serverType.equalsIgnoreCase("WCP")) {
          String encoding = Base64.encodeToString(basicAuth.getBytes(), Base64.DEFAULT);
          conn.setRequestProperty("Authorization", "Basic " + encoding);
          conn.setRequestProperty(AppConfig.WCP_APP_ID_KEY, AppConfig.APP_ID_VALUE);
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

        conn.setDoOutput(true);

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
        writer.write(jsonObject.toString());

        writer.flush();
        writer.close();
        os.close();

        try {
          // Will throw IOException if server responds with 401.
          responseCode = conn.getResponseCode();
        } catch (IOException e) {
          // Will return 401, because now connection has the correct internal state.
          responseCode = conn.getResponseCode();
        }
        if (responseCode >= HttpURLConnection.HTTP_OK && responseCode < HttpURLConnection.HTTP_MULT_CHOICE) {
          BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
          String inputLine;

          while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
          }
          in.close();
          conn.disconnect();
          responseData = response.toString();
          responseModel.setServermsg("success");
        } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
          responseData = "session expired";
        } else {
          if (conn.getHeaderField(headerErrorKey) != null) {
            responseModel.setServermsg(conn.getHeaderField(headerErrorKey));
            responseData = "http_not_ok";
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
    } else {
      try {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, TimeoutInterval);
        HttpConnectionParams.setSoTimeout(httpParams, TimeoutInterval);
        OwnHttpDelete httppost = new OwnHttpDelete(urlpath);

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
        httppost.addHeader("Content-Type", "application/json");
        httppost.addHeader(AppConfig.APP_ID_KEY, AppConfig.APP_ID_VALUE);

        StringEntity params1 = new StringEntity(jsonObject.toString());
        httppost.setEntity(params1);

        // Execute and get the response.
        HttpClient httpclient = new DefaultHttpClient(httpParams);
        HttpResponse response1 = httpclient.execute(httppost);
        responseCode = response1.getStatusLine().getStatusCode();

        if (serverType.equalsIgnoreCase("Response")) {
          HttpEntity entity = response1.getEntity();
          String line;
          if (entity != null) {
            InputStream instream = entity.getContent();
            try {
              // do something useful
              BufferedReader br = new BufferedReader(new InputStreamReader(instream));
              while ((line = br.readLine()) != null) {
                responseData += line;
              }
              br.close();
            } finally {
              instream.close();
            }
          }
        } else {
          if (responseCode == HttpURLConnection.HTTP_OK) {
            HttpEntity entity = response1.getEntity();
            String line;
            if (entity != null) {
              InputStream instream = entity.getContent();
              try {
                // do something useful
                BufferedReader br = new BufferedReader(new InputStreamReader(instream));
                while ((line = br.readLine()) != null) {
                  responseData += line;
                }
                br.close();
              } finally {
                instream.close();
              }
            }
          } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            responseData = "session expired";
          } else {
            responseData = "http_not_ok";
          }
        }

        if (response1.getFirstHeader(headerErrorKey) != null) {
          responseModel.setServermsg(response1.getFirstHeader(headerErrorKey).getValue());
        } else {
          responseModel.setServermsg("success");
        }

      } catch (ConnectTimeoutException e) {
        Logger.log(e);
      } catch (SocketTimeoutException e) {
        Logger.log(e);
      } catch (Exception e) {
        Logger.log(e);
      }
    }

    responseModel.setResponseCode("" + responseCode);
    responseModel.setResponseData(responseData);
    return responseModel;
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
}
