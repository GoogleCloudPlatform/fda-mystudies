/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.usermodule;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.harvard.AppConfig;
import com.harvard.R;
import com.harvard.gatewaymodule.GatewayActivity;
import com.harvard.studyappmodule.StandaloneActivity;

public class Error extends AppCompatActivity {
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Toast.makeText(Error.this, getString(R.string.filter_error), Toast.LENGTH_SHORT).show();
    if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
      Intent intent = new Intent(Error.this, GatewayActivity.class);
      ComponentName cn = intent.getComponent();
      Intent mainIntent = Intent.makeRestartActivityTask(cn);
      startActivity(mainIntent);
      finish();
    } else {
      Intent intent = new Intent(Error.this, StandaloneActivity.class);
      ComponentName cn = intent.getComponent();
      Intent mainIntent = Intent.makeRestartActivityTask(cn);
      startActivity(mainIntent);
      finish();
    }
  }
}
