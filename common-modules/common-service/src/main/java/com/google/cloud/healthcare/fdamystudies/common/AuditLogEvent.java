/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import java.util.Optional;

public interface AuditLogEvent {

  default Optional<PlatformComponent> getSource() {
    return Optional.empty();
  }

  default PlatformComponent getDestination() {
    return null;
  }

  default Optional<PlatformComponent> getResourceServer() {
    return Optional.empty();
  }

  default String getEventName() {
    return null;
  }

  default String getDescription() {
    return null;
  }

  default Optional<UserAccessLevel> getUserAccessLevel() {
    return Optional.empty();
  }

  default String getEventCode() {
    return null;
  }
}
