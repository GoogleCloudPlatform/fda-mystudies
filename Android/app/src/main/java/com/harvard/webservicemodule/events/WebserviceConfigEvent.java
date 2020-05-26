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

package com.harvard.webservicemodule.events;

import android.content.Context;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class WebserviceConfigEvent<T, V> {
  private String url;
  private Class<T> objT;
  private V objV;
  private HashMap<String, String> requestParams = new HashMap<>();
  private HashMap<String, String> headers = new HashMap<>();
  private JSONObject requestParamsJson;
  private JSONArray requestParamsJsonArray;
  private boolean showAlert;
  private String requestType;
  private int responseCode;
  private Context context;

  public WebserviceConfigEvent(
      String method,
      String url,
      int requestCode,
      Context context,
      Class modelclass,
      HashMap<String, String> params,
      HashMap<String, String> header,
      JSONObject jsonobj,
      boolean showAlert,
      V objV) {
    this.url = url;
    this.objT = modelclass;
    this.objV = objV;
    this.requestParams = params;
    this.headers = header;
    this.requestParamsJson = jsonobj;
    this.showAlert = showAlert;
    this.requestType = method;
    this.responseCode = requestCode;
    this.context = context;
  }

  public WebserviceConfigEvent(
      String method,
      String url,
      int requestCode,
      Context context,
      Class modelclass,
      HashMap<String, String> header,
      JSONArray jsonArray,
      boolean showAlert,
      V objV) {
    this.url = url;
    this.objT = modelclass;
    this.objV = objV;
    this.headers = header;
    this.requestParamsJsonArray = jsonArray;
    this.showAlert = showAlert;
    this.requestType = method;
    this.responseCode = requestCode;
    this.context = context;
  }

  public JSONArray getmRequestParamsJsonArray() {
    return requestParamsJsonArray;
  }

  public void setmRequestParamsJsonArray(JSONArray requestParamsJsonArray) {
    this.requestParamsJsonArray = requestParamsJsonArray;
  }

  public abstract String getProductionUrl();

  public abstract String getDevelopmentUrl();

  public JSONObject getmRequestParamsJson() {
    return requestParamsJson;
  }

  public void setmRequestParamsJson(JSONObject requestParamsJson) {
    this.requestParamsJson = requestParamsJson;
  }

  public Context getmContext() {
    return context;
  }

  public void setmContext(Context context) {
    this.context = context;
  }

  public int getmResponseCode() {
    return responseCode;
  }

  public void setmResponseCode(int responseCode) {
    this.responseCode = responseCode;
  }

  public String getmRequestType() {
    return requestType;
  }

  public void setmRequestType(String requestType) {
    this.requestType = requestType;
  }

  public String getmUrl() {
    return url;
  }

  public void setmUrl(String url) {
    this.url = url;
  }

  public Class<T> gettClass() {
    return objT;
  }

  public void settClass(Class<T> objT) {
    this.objT = objT;
  }

  public V getV() {
    return objV;
  }

  /**
   * setting response interface.
   *
   * @param objV
   */
  public void setV(V objV) {
    this.objV = objV;
  }

  public HashMap<String, String> getmRequestParams() {
    return requestParams;
  }

  public void setmRequestParams(HashMap<String, String> requestParams) {
    this.requestParams = requestParams;
  }

  public HashMap<String, String> getmHeaders() {
    return headers;
  }

  public void setmHeaders(HashMap<String, String> headers) {
    this.headers = headers;
  }

  public boolean ismShowAlert() {
    return showAlert;
  }

  public void setmShowAlert(boolean showAlert) {
    this.showAlert = showAlert;
  }
}
