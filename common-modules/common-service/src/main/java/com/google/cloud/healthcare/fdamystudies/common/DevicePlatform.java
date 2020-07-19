/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DevicePlatform {
  IOS("IOS", "Represents an apple platform"),

  ANDROID("ANDROID", "Represents an android platform"),

  UNKNOWN("UNKNOWN", "Represents unknown platform");

  private final String value;

  private final String description;

  public static DevicePlatform fromString(String value) {
    for (DevicePlatform devicePlatform : DevicePlatform.values()) {
      if (devicePlatform.value.equalsIgnoreCase(value)) {
        return devicePlatform;
      }
    }
    return null;
  }
}
