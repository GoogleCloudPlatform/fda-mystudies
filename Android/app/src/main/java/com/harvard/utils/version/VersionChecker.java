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

package com.harvard.utils.version;

import android.os.AsyncTask;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.harvard.AppConfig;
import com.harvard.BuildConfig;
import com.harvard.utils.Logger;
import com.harvard.utils.Urls;
import com.harvard.webservicemodule.apihelper.HttpRequest;
import com.harvard.webservicemodule.apihelper.Responsemodel;
import java.io.StringReader;
import java.net.HttpURLConnection;

public class VersionChecker extends AsyncTask<String, String, String> {

  private String newVersion;
  private boolean force = false;
  private Upgrade upgrade;
  private String versionUrl = Urls.BASE_URL_STUDY_DATASTORE_SERVER + Urls.VERSION_INFO;
  public static String PLAY_STORE_URL =
      "https://play.google.com/store/apps/details?id=" + AppConfig.PackageName;

  public VersionChecker(Upgrade upgrade) {
    this.upgrade = upgrade;
  }

  @Override
  protected String doInBackground(String... params) {
    newVersion = currentVersion();
    VersionModel versionModel;
    try {
      Responsemodel responsemodel = HttpRequest.getRequest(versionUrl, null, "STUDY_DATASTORE");

      if (responsemodel.getResponseCode().equalsIgnoreCase("" + HttpURLConnection.HTTP_OK)) {
        versionModel = parseJson(responsemodel, VersionModel.class);
        if (versionModel != null) {
          newVersion = versionModel.getAndroid().getLatestVersion();
          force = Boolean.parseBoolean(versionModel.getAndroid().getForceUpdate());
        }
      }
    } catch (Exception e) {
      Logger.log(e);
    }
    return newVersion;
  }

  private VersionModel parseJson(Responsemodel responseModel, Class genericClass) {
    Gson gson = new Gson();
    try {
      JsonReader reader = new JsonReader(new StringReader(responseModel.getResponseData()));
      reader.setLenient(true);
      return gson.fromJson(reader, genericClass);
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  protected void onPostExecute(String s) {
    super.onPostExecute(s);
    Version currVer = new Version(currentVersion());
    Version newVer = new Version(newVersion);
    if (currVer.equals(newVer) || currVer.compareTo(newVer) > 0) {
      upgrade.isUpgrade(false, newVersion, force);
    } else {
      upgrade.isUpgrade(true, newVersion, force);
    }
  }

  public interface Upgrade {
    void isUpgrade(boolean b, String newVersion, boolean force);
  }

  public String currentVersion() {
    return BuildConfig.VERSION_NAME + "." + BuildConfig.VERSION_CODE;
  }
}
