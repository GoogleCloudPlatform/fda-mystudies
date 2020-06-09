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

package com.harvard.studyappmodule.custom.question;

import com.harvard.studyappmodule.custom.ChoiceAnswerFormatCustom;
import com.harvard.utils.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextAnswerFormatRegex extends ChoiceAnswerFormatCustom {

  private String regex;
  private static final int UNLIMITED_LENGTH = 0;

  private boolean isMultipleLines = false;
  private int maximumLength;
  private String inValidMsg;

  public TextAnswerFormatRegex(int maximumLength, String regex, String inValidMsg) {
    this.regex = regex;
    this.maximumLength = maximumLength;
    this.inValidMsg = inValidMsg;
  }

  String getInValidMsg() {
    return inValidMsg;
  }

  /** Creates a TextAnswerFormat with no maximum length. */
  public TextAnswerFormatRegex() {
    this(UNLIMITED_LENGTH);
  }

  /**
   * Creates a TextAnswerFormat with a specified maximum length.
   *
   * @param maximumLength the maximum text length allowed
   */
  private TextAnswerFormatRegex(int maximumLength) {
    this.maximumLength = maximumLength;
  }

  /**
   * Returns the maximum length for the answer, <code>UNLIMITED_LENGTH</code> (0) if no maximum.
   *
   * @return the maximum length, <code>UNLIMITED_LENGTH</code> (0) if no maximum
   */
  int getMaximumLength() {
    return maximumLength;
  }

  /**
   * Sets whether the EditText should allow multiple lines.
   *
   * @param isMultipleLines boolean indicating if multiple lines are allowed
   */
  public void setIsMultipleLines(boolean isMultipleLines) {
    this.isMultipleLines = isMultipleLines;
  }

  /**
   * Returns whether multiple lines are allowed.
   *
   * @return boolean indicating if multiple lines are allowed
   */
  boolean isMultipleLines() {
    return isMultipleLines;
  }

  boolean isAnswerValid(String text) {
    if (text != null
        && text.length() > 0
        && (maximumLength == UNLIMITED_LENGTH || text.length() <= maximumLength)
        && validate(text)) {
      return true;
    }
    return false;
  }

  private boolean validate(final String hex) {
    if (regex != null && !regex.equalsIgnoreCase("")) {
      try {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(hex);
        return matcher.matches();
      } catch (Exception e) {
        Logger.log(e);
        return true;
      }
    }
    return true;
  }

  @Override
  public QuestionType getQuestionType() {
    return Type.TextRegex;
  }
}
