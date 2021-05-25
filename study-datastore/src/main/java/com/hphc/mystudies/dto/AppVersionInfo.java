/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package com.hphc.mystudies.dto;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "version_info")
@NamedQueries(
    value = {
      @NamedQuery(
          name = "AppVersionInfo.findAll",
          query = "FROM AppVersionInfo where appId=:appId"),
    })
public class AppVersionInfo implements Serializable {

  private static final long serialVersionUID = 4985607753888575491L;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "version_info_id", updatable = false, nullable = false)
  private String versionInfoId;

  @Column(name = "android")
  private String androidVersion;

  @Column(name = "ios")
  private String iosVersion = "";

  @Column(name = "app_id")
  private String appId;

  @Column(name = "android_force_update")
  private Boolean androidForceUpdate;

  @Column(name = "ios_force_update")
  private Boolean iosForceUpdate;

  public String getVersionInfoId() {
    return versionInfoId;
  }

  public void setVersionInfoId(String versionInfoId) {
    this.versionInfoId = versionInfoId;
  }

  public String getAndroidVersion() {
    return androidVersion;
  }

  public void setAndroidVersion(String androidVersion) {
    this.androidVersion = androidVersion;
  }

  public String getIosVersion() {
    return iosVersion;
  }

  public void setIosVersion(String iosVersion) {
    this.iosVersion = iosVersion;
  }

  public String getAppId() {
    return appId;
  }

  public Boolean getAndroidForceUpdate() {
    return androidForceUpdate;
  }

  public Boolean getIosForceUpdate() {
    return iosForceUpdate;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public void setAndroidForceUpdate(Boolean androidForceUpdate) {
    this.androidForceUpdate = androidForceUpdate;
  }

  public void setIosForceUpdate(Boolean iosForceUpdate) {
    this.iosForceUpdate = iosForceUpdate;
  }
}
