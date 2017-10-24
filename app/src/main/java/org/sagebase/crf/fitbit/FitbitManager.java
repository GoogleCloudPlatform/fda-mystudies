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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.sagebase.crf.fitbit.model.ActivityDistanceResponse;
import org.sagebase.crf.fitbit.model.HeartActivityResponse;
import org.sagebionetworks.research.crf.R;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by rianhouston on 10/11/17.
 */

public class FitbitManager {
  private static final String LOG_TAG = FitbitManager.class.getCanonicalName();

  private static final String PREF_USER_KEY = "PREF_USER_KEY";
  private static final String PREF_ACCESS_TOKEN_KEY = "PREF_ACCESS_TOKEN_KEY";

  private static final String SCHEME = "houston";

  private Context mContext;

  protected CustomTabsClient mClient;
  protected CustomTabsSession mCustomTabsSession;
  protected CustomTabsServiceConnection mCustomTabsServiceConnection;
  private SharedPreferences mPreferences;
  private RequestQueue mRequestQueue;

  private Callback mCallback;

  public interface Callback {
    public void onActivityResult(ActivityDistanceResponse response);
    public void onHeartActivityResult(HeartActivityResponse response);
  }

  public FitbitManager(Context context, Callback callback) {
    mContext = context;
    mCallback = callback;

    mCustomTabsServiceConnection = new CustomTabsServiceConnection() {
      @Override
      public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {
        //Pre-warming
        mClient = customTabsClient;
        mClient.warmup(0L);
        mCustomTabsSession = mClient.newSession(null);
      }

      @Override
      public void onServiceDisconnected(ComponentName name) {
        mClient = null;
      }
    };

    mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    mRequestQueue = Volley.newRequestQueue(mContext);
  }

  public boolean isAuthenticated() {
    return (getAccessToken() != null);
  }

  /**
   * TODO: parameterize the url, pass in the client_id?
   */
  public void authenticate() {
    String url = "https://www.fitbit.com/oauth2/authorize?response_type=token&client_id=228MZV&scope=activity%20heartrate&expires_in=86400&prompt=login%20consent&state=JUJU";
    CustomTabsIntent intent = new CustomTabsIntent.Builder(mCustomTabsSession)
            .setToolbarColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
            .setShowTitle(true)
            .setCloseButtonIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_arrow_back_black_24dp))
            .build();
    intent.launchUrl(mContext, Uri.parse(url));
  }

  /**
   * Inspect the intent to see if it contains Auth info returned from Fitbit.
   * @param intent The intent used to launch the activity.
   * @return True if the intent contains auth info.
   */
  public boolean handleAuthResponse(Intent intent) {
    boolean handled = false;
    if(intent == null || intent.getData() == null) {
      return handled;
    }

    Uri data = intent.getData();
    if(data != null && SCHEME.equals(data.getScheme())) {

      String fragment = data.getFragment();

      try {
        Map<String, String> params = splitQuery(fragment);
        String user = params.get("user_id");
        Log.d(LOG_TAG, "User: " + user);
        if (user != null) {
          mPreferences.edit().putString(PREF_USER_KEY, user).commit();
        }
        String token = params.get("access_token");
        Log.d(LOG_TAG, "Token: " + token);
        if (user != null) {
          mPreferences.edit().putString(PREF_ACCESS_TOKEN_KEY, token).commit();
        }
        String state = params.get("state");
        Log.d(LOG_TAG, "State: " + state);

        handled = true;
      } catch (Exception ex) {
        Log.d(LOG_TAG, "Error parsing response: " + ex.getMessage());
      }
    }

    return handled;
  }

  /**
   * TODO: pass in the day or interval?
   */
  public void fetchActivityData() {
    Log.d(LOG_TAG, "getActivityData()");

    String today = new SimpleDateFormat(GsonRequest.DATE_FORMAT).format(new Date());
    Log.d(LOG_TAG, "Today: " + today);

    String user = getUserId();
    final String token = getAccessToken();
    String url = "https://api.fitbit.com/1/user/-/activities/distance/date/" + today + "/1w.json";

    GsonRequest<ActivityDistanceResponse> request = new GsonRequest<>(url,
            ActivityDistanceResponse.class,
            createAuthHeader(token),
            new Response.Listener<ActivityDistanceResponse>() {
              @Override
              public void onResponse(ActivityDistanceResponse container) {
                Log.d(LOG_TAG, "Got response.");
                if(mCallback != null) {
                  mCallback.onActivityResult(container);
                } else {
                  Log.w(LOG_TAG, "No FitbitManager.Callback specified.  What to do?");
                }
              }
            },
            new Response.ErrorListener(){
              @Override
              public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "Got error response from distance request: " + error.getMessage());
              }
            });

    mRequestQueue.add(request);
  }

  /**
   *
   * TODO: pass in the requested time interval?
   *
   * NOTE: To access the detailed intraday heart rate data, your app must be set as 'Private'
   */
  public void fetchHeartRateData() {
    Log.d(LOG_TAG, "getHeartRateData()");

    String today = new SimpleDateFormat(GsonRequest.DATE_FORMAT).format(new Date());
    Log.d(LOG_TAG, "Today: " + today);

    String user = getUserId();
    final String token = getAccessToken();
    Log.d(LOG_TAG, "Token: " + token);

    String url = "https://api.fitbit.com/1/user/-/activities/heart/date/" + today + "/1d/1sec/time/00:00/01:00.json";
    Log.d(LOG_TAG, "Call Fitbit with: " + url);

    GsonRequest request = new GsonRequest<>(url,
            HeartActivityResponse.class,
            createAuthHeader(token),
            new Response.Listener<HeartActivityResponse>() {
              @Override
              public void onResponse(HeartActivityResponse container) {
                Log.d(LOG_TAG, "Got response.");
                if (mCallback != null) {
                  mCallback.onHeartActivityResult(container);
                } else {
                  Log.w(LOG_TAG, "No FitbitManagerCallback supplied.  What to do?");
                }
              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "Got error response from heart rate request: " + error.getMessage());
                if (error instanceof AuthFailureError) {
                  Log.d(LOG_TAG, "Token expired, reauthenticate...");
                  authenticate();
                }
              }
            });

    mRequestQueue.add(request);
  }

  public String getUserId() {
    return mPreferences.getString(PREF_USER_KEY, null);
  }

  public String getAccessToken() {
    return mPreferences.getString(PREF_ACCESS_TOKEN_KEY, null);
  }

  public static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
    Map<String, String> query_pairs = new LinkedHashMap<String, String>();
    String[] pairs = query.split("&");
    for (String pair : pairs) {
      int idx = pair.indexOf("=");
      query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
    }
    return query_pairs;
  }

  private static String decode(final String encoded) {
    try {
      return encoded == null ? null : URLDecoder.decode(encoded, "UTF-8");
    } catch (final UnsupportedEncodingException e) {
      throw new RuntimeException("Impossible: UTF-8 is a required encoding", e);
    }
  }

  private Map<String, String> createAuthHeader(String token) {
      Map<String, String> params = new HashMap<String, String>();
      params.put("Authorization", "Bearer " + token);
      return params;
  }

}
