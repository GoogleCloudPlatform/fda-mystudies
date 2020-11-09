/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.studyappmodule.surveyscheduler.datamodel;

import java.util.ArrayList;

public class OneTimeRuns {
  private ArrayList<Runs> onetime;

  public ArrayList<Runs> getOnetime() {
    return onetime;
  }

  public void setOnetime(ArrayList<Runs> onetime) {
    this.onetime = onetime;
  }
}
