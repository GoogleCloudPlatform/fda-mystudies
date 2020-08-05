/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

public interface AuditLogEvent {

  default PlatformComponent getSource() {
    return null;
  }

  default PlatformComponent getDestination() {
    return null;
  }

  default PlatformComponent getResourceServer() {
    return null;
  }

  default String getEventName() {
    return null;
  }

  default String getDescription() {
    return null;
  }

  default UserAccessLevel getUserAccessLevel() {
    return null;
  }

  default String getEventCode() {
    return null;
  }
}
