/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.studyappmodule.consent;

import com.harvard.R;
import org.researchstack.backbone.step.ConsentDocumentStep;

public class ConsentDocumentStepCustom extends ConsentDocumentStep {
  private String html;

  private String confirmMessage;

  public ConsentDocumentStepCustom(String identifier) {
    super(identifier);
  }

  @Override
  public int getStepTitle() {
    return R.string.rsb_consent;
  }

  @Override
  public Class getStepLayoutClass() {
    return ConsentDocumentStepLayoutCustom.class;
  }

  /**
   * Returns the HTML string of the consent document.
   *
   * @return the string representation of the entire consent HTML document
   */
  public String getConsentHTML() {
    return html;
  }

  /**
   * Sets the HTML string that is used to show the user your consent document.
   *
   * @param html a string representation of the entire consent HTML document
   */
  public void setConsentHTML(String html) {
    this.html = html;
  }

  /**
   * Gets the message to show the user when they are asked to confirm their agreement.
   *
   * @return the string to show the user during confirmation
   */
  public String getConfirmMessage() {
    return confirmMessage;
  }

  /**
   * Sets the message to show the user when they are asked to confirm their agreement.
   *
   * @param confirmMessage the string to show the user during confirmation
   */
  public void setConfirmMessage(String confirmMessage) {
    this.confirmMessage = confirmMessage;
  }
}