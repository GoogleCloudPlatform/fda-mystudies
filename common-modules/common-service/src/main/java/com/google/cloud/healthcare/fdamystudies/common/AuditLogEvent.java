/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

public interface AuditLogEvent {

  default String getEventName() {
    return null;
  }

  default boolean isAlert() {
    return false;
  }

  default boolean isFallback() {
    return false;
  }

  default String getSystemId() {
    return null;
  }

  default String getAccessLevel() {
    return null;
  }

  default String getClientId() {
    return null;
  }

  default String getClientAccessLevel() {
    return null;
  }

  default String getResourceServer() {
    return null;
  }

  default String getEventDetail() {
    return null;
  }

  default String getDescription() {
    return null;
  }
}
