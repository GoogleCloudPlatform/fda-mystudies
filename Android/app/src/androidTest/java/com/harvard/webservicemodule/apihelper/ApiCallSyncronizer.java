/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.webservicemodule.apihelper;

import com.harvard.utils.Logger;

public class ApiCallSyncronizer {
  public void doWait(long l) {
    synchronized (this) {
      try {
        this.wait(l);
      } catch (InterruptedException e) {
        Logger.log(e);
      }
    }
  }

  public void doWait() {
    synchronized (this) {
      try {
        this.wait();
      } catch (InterruptedException e) {
        Logger.log(e);
      }
    }
  }

  public void doNotify() {
    synchronized (this) {
      this.notify();
    }
  }
}
