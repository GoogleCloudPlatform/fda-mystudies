/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MobilePlatform {
  ANDROID("ANDROID", "Represents an Android platform"),
  IOS("IOS", "Represents an Apple platform"),
  UNKNOWN("UNKNOWN", "Any other value. Should not happen");

  private String value;
  private String description;

  public static MobilePlatform fromValue(String value) {
    for (MobilePlatform platform : values()) {
      if (platform.value.equals(value)) {
        return platform;
      }
    }
    return UNKNOWN;
  }

  public static boolean isMobileDevice(String mobilePlatform) {
    MobilePlatform platform = fromValue(mobilePlatform);
    return platform == ANDROID || platform == IOS;
  }
}
