/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.usermodule.event;

import com.harvard.webservicemodule.events.ResponseDatastoreServerConfigEvent;

public class ActivityStateEvent {
  private ResponseDatastoreServerConfigEvent responseDatastoreServerConfigEvent;

  public ResponseDatastoreServerConfigEvent getResponseDatastoreServerConfigEvent() {
    return responseDatastoreServerConfigEvent;
  }

  public void setResponseDatastoreServerConfigEvent(ResponseDatastoreServerConfigEvent responseDatastoreServerConfigEvent) {
    this.responseDatastoreServerConfigEvent = responseDatastoreServerConfigEvent;
  }
}
