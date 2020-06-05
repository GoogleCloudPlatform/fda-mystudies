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

package com.harvard;

public class AppConfig {

  public static String PackageName = BuildConfig.APPLICATION_ID;
  public static String API_TOKEN = BuildConfig.apikey;
  private static String GateWay = "gateway";
  private static String Standalone = "standalone";
  public static String AppType = GateWay;
  public static String StudyId = "";

  // AppId
  public static String APP_ID_KEY = "appId";
  public static String WCP_APP_ID_KEY = "applicationId";
  public static String APP_ID_VALUE = "mystudies-demo";
  // OrgId
  public static String ORG_ID_KEY = "orgId";
  public static String ORG_ID_VALUE = "mystudies-demo-org";

  // ClientId
  public static String CLIENT_ID_KEY = "clientId";
  public static String CLIENT_ID_VALUE = BuildConfig.CLIENT_ID_VALUE;
  //Secret Key
  public static String SECRET_KEY = "secretKey";
  public static String SECRET_KEY_VALUE = BuildConfig.SECRET_KEY_VALUE;
  //Client Token
  public static String CLIENT_TOKEN = "Dr?yB@-uP1QILVFm";

}
