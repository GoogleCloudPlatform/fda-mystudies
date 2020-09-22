/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
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
