/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.common;


public enum MobilePlatform {
  ANDROID("ANDROID", "Represents an Android platform"),
  IOS("IOS", "Represents an Apple platform"),
  UNKNOWN("UNKNOWN", "Any other value. Should not happen");

  private String value;
  private String description;

  private MobilePlatform(String value, String description) {
	this.value = value;
	this.description = description;
}

public static MobilePlatform fromValue(String value) {
    for (MobilePlatform platform : values()) {
      if (platform.value.equals(value)) {
        return platform;
      }
    }
    return null;
  }

  public static boolean isMobileDevice(String mobilePlatform) {
    MobilePlatform platform = fromValue(mobilePlatform);
    return platform == ANDROID || platform == IOS;
  }

public String getValue() {
	return value;
}

public void setValue(String value) {
	this.value = value;
}

public String getDescription() {
	return description;
}

public void setDescription(String description) {
	this.description = description;
}
  
}
