/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.webservicemodule.events;

import android.content.Context;
import com.harvard.utils.URLs;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

public class RegistrationServerConsentConfigEvent<V> extends WebserviceConfigEvent {

  /**
   * @param method
   * @param url
   * @param requestCode
   * @param context
   * @param modelclass
   * @param params
   * @param headers
   * @param jsonobj
   * @param showAlert
   */
  public RegistrationServerConsentConfigEvent(
      String method,
      String url,
      int requestCode,
      Context context,
      Class modelclass,
      HashMap<String, String> params,
      HashMap<String, String> headers,
      JSONObject jsonobj,
      boolean showAlert,
      V v) {
    super(method, url, requestCode, context, modelclass, params, headers, jsonobj, showAlert, v);
  }

  public RegistrationServerConsentConfigEvent(
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
    return URLs.BASE_URL_REGISTRATION_CONSENT_SERVER;
  }

  @Override
  public String getDevelopmentUrl() {
    return URLs.BASE_URL_REGISTRATION_CONSENT_SERVER;
  }
}
