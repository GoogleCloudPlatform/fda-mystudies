/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as
 * Contract no. HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.fdahpstudydesigner.bean;

public class AppDetailsBean {

  private String appId;
  private String appName;
  //  private String appDescription;
  private String appType;
  private String appPlatform;
  private String organizationName;

  private String contactEmail;
  private String feedBackEmail;
  private String appSupportEmail;
  private String fromEmail;

  private String appTermsUrl;
  private String appPrivacyUrl;
  private String appStoreUrl;
  private String playStoreUrl;
  private String appWebSiteUrl;

  private String androidBundleId;
  private String androidServerKey;
  private Integer androidForceUpgrade;
  private String androidAppBuildVersion;

  private String iosBundleId;
  private String iosServerKey;
  private String iosAppBuildVersion;
  private Integer iosForceUpgrade;
  private String iosXCodeAppVersion;

  private String appStatus;

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getAppType() {
    return appType;
  }

  public void setAppType(String appType) {
    this.appType = appType;
  }

  public String getAppPlatform() {
    return appPlatform;
  }

  public void setAppPlatform(String appPlatform) {
    this.appPlatform = appPlatform;
  }

  public String getOrganizationName() {
    return organizationName;
  }

  public void setOraganizationName(String organizationName) {
    this.organizationName = organizationName;
  }

  public String getContactEmail() {
    return contactEmail;
  }

  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }

  public String getFeedBackEmail() {
    return feedBackEmail;
  }

  public void setFeedBackEmail(String feedBackEmail) {
    this.feedBackEmail = feedBackEmail;
  }

  public String getAppSupportEmail() {
    return appSupportEmail;
  }

  public void setAppSupportEmail(String appSupportEmail) {
    this.appSupportEmail = appSupportEmail;
  }

  public String getFromEmail() {
    return fromEmail;
  }

  public void setFromEmail(String fromEmail) {
    this.fromEmail = fromEmail;
  }

  public String getAppTermsUrl() {
    return appTermsUrl;
  }

  public void setAppTermsUrl(String appTermsUrl) {
    this.appTermsUrl = appTermsUrl;
  }

  public String getAppPrivacyUrl() {
    return appPrivacyUrl;
  }

  public void setAppPrivacyUrl(String appPrivacyUrl) {
    this.appPrivacyUrl = appPrivacyUrl;
  }

  public String getAppStoreUrl() {
    return appStoreUrl;
  }

  public void setAppStoreUrl(String appStoreUrl) {
    this.appStoreUrl = appStoreUrl;
  }

  public String getPlayStoreUrl() {
    return playStoreUrl;
  }

  public void setPlayStoreUrl(String playStoreUrl) {
    this.playStoreUrl = playStoreUrl;
  }

  public String getAndroidBundleId() {
    return androidBundleId;
  }

  public void setAndroidBundleId(String androidBundleId) {
    this.androidBundleId = androidBundleId;
  }

  public String getAndroidServerKey() {
    return androidServerKey;
  }

  public void setAndroidServerKey(String androidServerKey) {
    this.androidServerKey = androidServerKey;
  }

  public Integer getAndroidForceUpdrade() {
    return androidForceUpgrade;
  }

  public void setAndroidForceUpdrade(Integer androidForceUpgrade) {
    this.androidForceUpgrade = androidForceUpgrade;
  }

  public String getAndroidAppBuildVersion() {
    return androidAppBuildVersion;
  }

  public void setAndroidAppBuildVersion(String androidAppBuildVersion) {
    this.androidAppBuildVersion = androidAppBuildVersion;
  }

  public String getIosBundleId() {
    return iosBundleId;
  }

  public void setIosBundleId(String iosBundleId) {
    this.iosBundleId = iosBundleId;
  }

  public String getIosServerKey() {
    return iosServerKey;
  }

  public void setIosServerKey(String iosServerKey) {
    this.iosServerKey = iosServerKey;
  }

  public String getIosAppBuildVersion() {
    return iosAppBuildVersion;
  }

  public void setIosAppBuildVersion(String iosAppBuildVersion) {
    this.iosAppBuildVersion = iosAppBuildVersion;
  }

  public Integer getIosForceUpgrade() {
    return iosForceUpgrade;
  }

  public void setIosForceUpgrade(Integer iosForceUpgrade) {
    this.iosForceUpgrade = iosForceUpgrade;
  }

  public String getIosXCodeAppVersion() {
    return iosXCodeAppVersion;
  }

  public void setIosXCodeAppVersion(String iosXCodeAppVersion) {
    this.iosXCodeAppVersion = iosXCodeAppVersion;
  }

  public String getAppWebSiteUrl() {
    return appWebSiteUrl;
  }

  public void setAppWebSiteUrl(String appWebSiteUrl) {
    this.appWebSiteUrl = appWebSiteUrl;
  }

  public String getAppStatus() {
    return appStatus;
  }

  public void setAppStatus(String appStatus) {
    this.appStatus = appStatus;
  }
}
