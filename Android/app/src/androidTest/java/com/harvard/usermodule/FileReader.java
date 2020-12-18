/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.usermodule;

import android.app.Application;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class FileReader {
  public static String readStringFromFile(String filename) {
    Context context =
        InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
    StringBuilder stringBuilder = new StringBuilder();
    try {
      InputStream is = ((Application) context).getAssets().open(filename);
      BufferedReader bufferedReader =
          new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
      String strAppendValue;
      while ((strAppendValue = bufferedReader.readLine()) != null) {
        stringBuilder.append(strAppendValue);
      }
      bufferedReader.close();
    } catch (IOException e) {
      e.getMessage();
    }
    return stringBuilder.toString();
  }
}
