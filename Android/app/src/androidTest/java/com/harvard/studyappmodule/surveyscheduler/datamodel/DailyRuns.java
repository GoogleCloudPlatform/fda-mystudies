/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.studyappmodule.surveyscheduler.datamodel;

import java.util.ArrayList;

public class DailyRuns {
  private ArrayList<Runs> dailyruns;

  public ArrayList<Runs> getDailyruns() {
    return dailyruns;
  }

  public void setDailyruns(ArrayList<Runs> dailyruns) {
    this.dailyruns = dailyruns;
  }
}
