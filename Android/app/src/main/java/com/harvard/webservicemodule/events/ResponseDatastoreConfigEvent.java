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
import com.harvard.utils.Urls;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

public class ResponseDatastoreConfigEvent<V> extends WebserviceConfigEvent {
  public ResponseDatastoreConfigEvent(
      String method,
      String url,
      int requestCode,
      Context context,
      Class modelclass,
      HashMap params,
      HashMap header,
      JSONObject jsonobj,
      boolean showAlert,
      V o) {
    super(method, url, requestCode, context, modelclass, params, header, jsonobj, showAlert, o);
  }

  public ResponseDatastoreConfigEvent(
      String method,
      String url,
      int requestCode,
      Context context,
      Class modelclass,
      HashMap<String, String> headers,
      JSONArray jsonArray,
      boolean showAlert,
      V v) {
    super(method, url, requestCode, context, modelclass, headers, jsonArray, showAlert, v);
  }

  @Override
  public String getProductionUrl() {
    return Urls.BASE_URL_RESPONSE_DATASTORE_SERVER;
  }

  @Override
  public String getDevelopmentUrl() {
    return Urls.BASE_URL_RESPONSE_DATASTORE_SERVER;
  }
}
