/*
 *    Copyright 2017 Sage Bionetworks
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package org.sagebase.crf.fitbit;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Random;

/**
 * This class was orginally copied from the Volley developer docs.
 *
 * http://developer.android.com/training/volley/request-custom.html
 *
 * @param <T>
 */
public class GsonRequest<T> extends Request<T> {
  private static final String LOG_TAG = GsonRequest.class.getCanonicalName();

  /** Charset for request. */
  private static final String PROTOCOL_CHARSET = "utf-8";

  public static final String DATE_FORMAT = "yyyy-MM-dd";

  /** Content type for request. */
  private static final String PROTOCOL_CONTENT_TYPE =
    String.format("application/json; charset=%s", PROTOCOL_CHARSET);

  private final Gson gson;
  private final Class<T> clazz;
  private final Map<String, String> headers;
  private final Response.Listener<T> listener;
  private final String requestBody;

  /**
   * Make a GET request and return a parsed object from JSON.
   *
   * @param url URL of the request to make
   * @param clazz Relevant class object, for Gson's reflection
   * @param headers Map of request headers
   */
  public GsonRequest(String url, Class<T> clazz, Map<String, String> headers,
                     Response.Listener<T> listener, Response.ErrorListener errorListener) {
    super(Method.GET, url, errorListener);
    this.clazz = clazz;
    this.headers = headers;
    this.requestBody = null;
    this.listener = listener;

    GsonBuilder gsonb = new GsonBuilder();
    gsonb.setDateFormat(DATE_FORMAT);
    this.gson = gsonb.create();
  }

  /**
   * Make a GET request and return a parsed object from JSON.
   *
   * @param method
   * @param url URL of the request to make
   * @param clazz Relevant class object, for Gson's reflection
   * @param headers Map of request headers
   */
  public GsonRequest(int method, String url, Class<T> clazz, Map<String, String> headers,
                     Response.Listener<T> listener, Response.ErrorListener errorListener) {
    super(method, url, errorListener);
    this.clazz = clazz;
    this.headers = headers;
    this.requestBody = null;
    this.listener = listener;

    GsonBuilder gsonb = new GsonBuilder();
    gsonb.setDateFormat(DATE_FORMAT);
    this.gson = gsonb.create();
  }

  /**
   * Make a GET request and return a parsed object from JSON using externally constructed Gson object.
   *
   * @param gson
   * @param url
   * @param clazz
   * @param headers
   * @param listener
   * @param errorListener
   */
  public GsonRequest(Gson gson, String url, Class<T> clazz, Map<String, String> headers,
                     Response.Listener<T> listener, Response.ErrorListener errorListener) {
    super(Method.GET, url, errorListener);
    this.gson = gson;
    this.clazz = clazz;
    this.headers = headers;
    this.requestBody = null;
    this.listener = listener;
  }

  /**
   * Make a GET request and return a parsed object from JSON using externally constructed Gson object.
   *
   * @param gson
   * @param method
   * @param url
   * @param clazz
   * @param headers
   * @param listener
   * @param errorListener
   */
  public GsonRequest(Gson gson,
                     int method,
                     String url,
                     Class<T> clazz,
                     Map<String, String> headers,
                     String body,
                     Response.Listener<T> listener,
                     Response.ErrorListener errorListener) {
    super(method, url, errorListener);
    this.gson = gson;
    this.clazz = clazz;
    this.headers = headers;
    this.requestBody = body;
    this.listener = listener;
  }

  @Override
  public Map<String, String> getHeaders() throws AuthFailureError {
    return headers != null ? headers : super.getHeaders();
  }

  @Override
  public void deliverResponse(T response) {
    listener.onResponse(response);
  }

  @Override
  protected Response<T> parseNetworkResponse(NetworkResponse response) {
    try {
      String json = new String(
              response.data,
              HttpHeaderParser.parseCharset(response.headers));
      Log.i(LOG_TAG, "GsonRequest response: " + json);
      return Response.success(
              gson.fromJson(json, clazz),
              HttpHeaderParser.parseCacheHeaders(response));
    } catch (UnsupportedEncodingException e) {
      return Response.error(new ParseError(e));
    } catch (JsonSyntaxException e) {
      return Response.error(new ParseError(e));
    }
  }

  /**
   * @deprecated Use {@link #getBodyContentType()}.
   */
  @Override
  public String getPostBodyContentType() {
    return getBodyContentType();
  }

  /**
   * @deprecated Use {@link #getBody()}.
   */
  @Override
  public byte[] getPostBody() {
    return getBody();
  }

  @Override
  public String getBodyContentType() {
    return PROTOCOL_CONTENT_TYPE;
  }

  @Override
  public byte[] getBody() {
    try {
      return requestBody == null ? null : requestBody.getBytes(PROTOCOL_CHARSET);
    } catch (UnsupportedEncodingException uee) {
      VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
        requestBody, PROTOCOL_CHARSET);
      return null;
    }
  }

  /**
   * USED FOR TESTING
   * @return
   */
  private int getRandomNumber() {
    int min = 0;
    int max = 5;

    // Usually this can be a field rather than a method variable
    Random rand = new Random();

    // nextInt is normally exclusive of the top value,
    // so add 1 to make it inclusive
    int randomNum = rand.nextInt((max - min) + 1) + min;

    return randomNum;
  }
}
