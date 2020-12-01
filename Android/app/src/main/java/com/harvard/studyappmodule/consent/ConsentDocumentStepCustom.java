/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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