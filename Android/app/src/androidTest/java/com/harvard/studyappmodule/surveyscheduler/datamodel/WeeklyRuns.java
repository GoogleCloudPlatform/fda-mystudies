/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.studyappmodule.surveyscheduler.datamodel;

import java.util.ArrayList;

public class WeeklyRuns {
  private ArrayList<Runs> weekly;

  public ArrayList<Runs> getWeekly() {
    return weekly;
  }

  public void setWeekly(ArrayList<Runs> weekly) {
    this.weekly = weekly;
  }
}
