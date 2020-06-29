/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.matchers;

import com.google.cloud.healthcare.fdamystudies.model.PersonalizedUserReportBO;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class HasReport extends TypeSafeMatcher<PersonalizedUserReportBO> {

  private String reportTitle;
  private String reportContent;

  private HasReport(String reportTitle, String reportContent) {
    this.reportTitle = reportTitle;
    this.reportContent = reportContent;
  }

  @Override
  protected boolean matchesSafely(PersonalizedUserReportBO report) {
    return report.getReportTitle() == this.reportTitle
        && report.getReportContent() == reportContent;
  }

  @Override
  public void describeTo(Description description) {
    description.appendText(
        "has report with title " + reportTitle + " and content " + reportContent);
  }

  public static Matcher<PersonalizedUserReportBO> hasReport(
      String reportTitle, String reportContent) {
    return new HasReport(reportTitle, reportContent);
  }
}
