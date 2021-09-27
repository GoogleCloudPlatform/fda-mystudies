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

package com.harvard.usermodule;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import com.harvard.R;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import io.realm.Realm;

public class TermsPrivacyPolicyActivity extends AppCompatActivity {
  private RelativeLayout backBtn;
  private AppCompatTextView title;
  private WebView webView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_terms_privacy_policy);
    initializeXmlId();
    setTextForView();
    setFont();
    bindEvents();
  }

  private void initializeXmlId() {
    backBtn = (RelativeLayout) findViewById(R.id.backBtn);
    title = (AppCompatTextView) findViewById(R.id.title);
    webView = (WebView) findViewById(R.id.webView);
  }

  private void setTextForView() {
    try {
      if (getIntent().getStringExtra("title") == null) {
        if (getIntent().getData().getPath().equalsIgnoreCase("/mystudies/privacyPolicy")) {
          title.setText(getString(R.string.privacy_policy));
        }

        if (getIntent().getData().getPath().equalsIgnoreCase("/mystudies/terms")) {
          title.setText(getString(R.string.terms));
        }
      } else {
        title.setText(getIntent().getStringExtra("title"));
      }
      AppController.getHelperProgressDialog().showProgress(TermsPrivacyPolicyActivity.this, "", "", false);
      webView.getSettings().setLoadsImagesAutomatically(true);
      webView.getSettings().setJavaScriptEnabled(true);
      webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
      webView.setWebViewClient(new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
          AppController.getHelperProgressDialog().dismissDialog();
        }
      });
      if (getIntent().getStringExtra("url") == null) {
        DbServiceSubscriber dbServiceSubscriber = new DbServiceSubscriber();
        Realm realm = AppController.getRealmobj(TermsPrivacyPolicyActivity.this);
        if (getIntent().getData().getPath().equalsIgnoreCase("/mystudies/privacyPolicy")) {
          webView.loadUrl(dbServiceSubscriber.getApps(realm).getPrivacyPolicyUrl());
        }

        if (getIntent().getData().getPath().equalsIgnoreCase("/mystudies/terms")) {
          webView.loadUrl(dbServiceSubscriber.getApps(realm).getTermsUrl());
        }
        dbServiceSubscriber.closeRealmObj(realm);
      } else {
        webView.loadUrl(getIntent().getStringExtra("url"));
      }
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void setFont() {
    try {
      title.setTypeface(AppController.getTypeface(TermsPrivacyPolicyActivity.this, "medium"));

    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void bindEvents() {
    backBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            finish();
          }
        });
  }
}
