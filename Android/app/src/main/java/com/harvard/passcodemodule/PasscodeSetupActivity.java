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

package com.harvard.passcodemodule;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.harvard.R;
import com.harvard.gatewaymodule.GatewayActivity;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import com.harvard.utils.SharedPreferenceHelper;

public class PasscodeSetupActivity extends AppCompatActivity {
  private RelativeLayout backBtn;
  private AppCompatTextView title;
  private AppCompatTextView setup;
  private AppCompatTextView hrLine1;
  private RelativeLayout cancelBtn;
  private PasscodeView passcodeView;
  private TextView forgot;
  private TextView passcodeTitle;
  private TextView passcodeDesc;
  private DbServiceSubscriber dbServiceSubscriber;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_passcode_setup);
    getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    dbServiceSubscriber = new DbServiceSubscriber();
    initializeXmlId();
    setTextForView();
    setFont();
    bindEvent();
  }

  private void initializeXmlId() {
    backBtn = (RelativeLayout) findViewById(R.id.backBtn);
    title = (AppCompatTextView) findViewById(R.id.title);
    setup = (AppCompatTextView) findViewById(R.id.setup_info);
    hrLine1 = (AppCompatTextView) findViewById(R.id.hrLine1);
    cancelBtn = (RelativeLayout) findViewById(R.id.cancelBtn);
    passcodeView = (PasscodeView) findViewById(R.id.passcode_view);
    passcodeTitle = (TextView) findViewById(R.id.passcodetitle);

    forgot = (TextView) findViewById(R.id.forgot);
    passcodeDesc = (TextView) findViewById(R.id.passcodedesc);
  }

  private void setTextForView() {
    cancelBtn.setVisibility(View.GONE);
    hrLine1.setVisibility(View.GONE);
    passcodeDesc.setVisibility(View.INVISIBLE);
    title.setText("");
    setup.setText("");
    passcodeTitle.setText(getString(R.string.enter_your_passcode));
  }

  private void setFont() {
    try {
      title.setTypeface(AppController.getTypeface(PasscodeSetupActivity.this, "medium"));
      passcodeTitle.setTypeface(AppController.getTypeface(PasscodeSetupActivity.this, "regular"));
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void bindEvent() {
    passcodeView.postDelayed(
        new Runnable() {
          @Override
          public void run() {
            passcodeView.requestToShowKeyboard();
          }
        },
        400);

    backBtn.setVisibility(View.GONE);
    forgot.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            // display sign out confirmation.
            AlertDialog.Builder adb = new AlertDialog.Builder(PasscodeSetupActivity.this);
            adb.setTitle(getResources().getString(R.string.app_name));
            adb.setIcon(android.R.drawable.ic_dialog_alert);
            adb.setMessage(R.string.forgotpasscodemsg);
            adb.setPositiveButton(
                getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                    AppController.forceSignout(PasscodeSetupActivity.this);
                  }
                });

            adb.setNegativeButton(
                getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                  }
                });
            adb.show();
          }
        });

    passcodeView.setPasscodeEntryListener(
        new PasscodeView.PasscodeEntryListener() {
          @Override
          public void onPasscodeEntered(String passcode) {
            // chk with keystore passcode
            if (passcode.equalsIgnoreCase(AppController.refreshKeys("passcode"))) {
              AppController.getHelperSharedPreference()
                  .writePreference(getApplicationContext(), "passcodeAnswered", "yes");
              AppController.getHelperHideKeyboard(PasscodeSetupActivity.this);
              Intent intent = new Intent();
              setResult(RESULT_OK, intent);
              finish();
            } else {
              Toast.makeText(PasscodeSetupActivity.this, R.string.invalidcode, Toast.LENGTH_SHORT)
                  .show();
              passcodeView.clearText();
            }
          }
        });
  }

  @Override
  protected void onStop() {
    super.onStop();
    finish();
  }

  @Override
  public void onBackPressed() {
    if (getIntent().getStringExtra("from") != null
        && getIntent().getStringExtra("from").equalsIgnoreCase("profile")) {
      super.onBackPressed();
    }
  }
}
