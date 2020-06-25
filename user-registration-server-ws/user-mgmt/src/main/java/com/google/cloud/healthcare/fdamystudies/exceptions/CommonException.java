/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;

public abstract class CommonException extends Exception {

  private String errorCode;

  public CommonException(String pErrorCode, Throwable arg1) {
    super(pErrorCode, arg1);
    errorCode = pErrorCode;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  private static final long serialVersionUID = 1L;

  @Override
  public synchronized Throwable fillInStackTrace() {
    return super.fillInStackTrace();
  }

  @Override
  public synchronized Throwable getCause() {
    return super.getCause();
  }

  @Override
  public String getLocalizedMessage() {
    return super.getLocalizedMessage();
  }

  @Override
  public String getMessage() {
    return super.getMessage();
  }

  @Override
  public StackTraceElement[] getStackTrace() {
    return super.getStackTrace();
  }

  @Override
  public synchronized Throwable initCause(Throwable arg0) {
    return super.initCause(arg0);
  }

  @Override
  public void printStackTrace() {
    super.printStackTrace();
  }

  @Override
  public void printStackTrace(PrintStream arg0) {
    super.printStackTrace(arg0);
  }

  @Override
  public void printStackTrace(PrintWriter arg0) {
    super.printStackTrace(arg0);
  }

  @Override
  public void setStackTrace(StackTraceElement[] arg0) {
    super.setStackTrace(arg0);
  }

  @Override
  public String toString() {
    return super.toString();
  }
}
