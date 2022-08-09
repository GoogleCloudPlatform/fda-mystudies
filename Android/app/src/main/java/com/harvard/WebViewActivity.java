/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
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

package com.harvard;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.harvard.utils.CustomFirebaseAnalytics;
import com.harvard.utils.Logger;
import com.harvard.utils.NetworkChangeReceiver;


public class WebViewActivity extends AppCompatActivity implements
    NetworkChangeReceiver.NetworkChangeCallback {

  private CustomFirebaseAnalytics analyticsInstance;
  private RelativeLayout shareBtn;
  private NetworkChangeReceiver networkChangeReceiver;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_web_view);
    analyticsInstance = CustomFirebaseAnalytics.getInstance(this);
    networkChangeReceiver = new NetworkChangeReceiver(this);
    shareBtn = (RelativeLayout) findViewById(R.id.shareBtn);
    WebView webView = (WebView) findViewById(R.id.webView);
    webView.getSettings().setLoadsImagesAutomatically(true);
    webView.getSettings().setJavaScriptEnabled(true);
    webView.getSettings().setDefaultTextEncodingName("utf-8");

    webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    final String webData = getIntent().getStringExtra("consent");
    if (Build.VERSION.SDK_INT >= 24) {
      webView.loadDataWithBaseURL(null,
              Html.fromHtml((webData), Html.FROM_HTML_MODE_LEGACY).toString(), "text/html", "UTF-8", null);
    } else {
      webView.loadDataWithBaseURL(null, Html.fromHtml((webData)).toString(), "text/html", "UTF-8", null);
    }
    RelativeLayout backBtn = (RelativeLayout) findViewById(R.id.backBtn);
    backBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Bundle eventProperties = new Bundle();
            eventProperties.putString(
                CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                getString(R.string.webview_back));
            analyticsInstance.logEvent(
                CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
            finish();
          }
        });
    shareBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Bundle eventProperties = new Bundle();
            eventProperties.putString(
                CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON, "Study overview share");
            analyticsInstance.logEvent(
                CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
            Log.e("check", "webData is " + Html.fromHtml(webData).toString());
            try {
              Intent shareIntent = new Intent(Intent.ACTION_SEND);
              shareIntent.setData(Uri.parse("mailto:"));
              shareIntent.setType("application/pdf");
              shareIntent.setType("text/html");
              shareIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(webData).toString());
              shareIntent.putExtra(Intent.EXTRA_HTML_TEXT, Html.fromHtml(webData).toString());
              startActivity(shareIntent);
            } catch (Exception e) {
              Logger.log(e);
            }
          }
        });
  }

  @Override
  public void onNetworkChanged(boolean status) {
    if (!status) {
      shareBtn.setClickable(false);
      shareBtn.setAlpha(.3F);
    } else {
      shareBtn.setClickable(true);
      shareBtn.setAlpha(1F);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    registerReceiver(networkChangeReceiver, intentFilter);
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (networkChangeReceiver != null) {
      unregisterReceiver(networkChangeReceiver);
    }
  }
}
