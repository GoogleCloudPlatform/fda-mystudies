/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.webservicemodule.apihelper;

public class ApiCallSyncronizer {
  public void doWait(long l) {
    synchronized (this) {
      try {
        this.wait(l);
      } catch (InterruptedException e) {
      }
    }
  }

  public void doNotify() {
    synchronized (this) {
      this.notify();
    }
  }

  public void doWait() {
    synchronized (this) {
      try {
        this.wait();
      } catch (InterruptedException e) {
      }
    }
  }
}
