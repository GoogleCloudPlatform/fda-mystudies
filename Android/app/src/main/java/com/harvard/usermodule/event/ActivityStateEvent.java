/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.usermodule.event;

import com.harvard.webservicemodule.events.ResponseDatastoreConfigEvent;

public class ActivityStateEvent {
  private ResponseDatastoreConfigEvent responseDatastoreConfigEvent;

  public ResponseDatastoreConfigEvent getResponseDatastoreConfigEvent() {
    return responseDatastoreConfigEvent;
  }

  public void setResponseDatastoreConfigEvent(ResponseDatastoreConfigEvent responseDatastoreConfigEvent) {
    this.responseDatastoreConfigEvent = responseDatastoreConfigEvent;
  }
}
