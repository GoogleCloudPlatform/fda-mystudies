/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.utils;

import android.util.Log;
import com.harvard.BuildConfig;

/**
 * Android Log wrapper class that can use {@link String#format(String, Object...)} in logging.
 * message
 */
public class Logger {

  private static final String TAG = Logger.class.getSimpleName();
  private static final String EMPTY = "";

  /**
   * Send a VERBOSE log message.
   *
   * @param tag
   * @param format
   * @param args
   * @return
   */
  public static int verbose(String tag, String format, Object... args) {
    if (BuildConfig.DEBUG) {
      return Log.v(tag, format(format, args));
    } else {
      return 0;
    }
  }

  /**
   * Send a VERBOSE log message and log the exception.
   *
   * @param tag
   * @param msg
   * @return
   */
  public static int verbose(String tag, String msg) {
    if (BuildConfig.DEBUG) {
      return Log.v(tag, msg);
    } else {
      return 0;
    }
  }

  /**
   * Send a VERBOSE log message and log the exception.
   *
   * @param tag
   * @param format
   * @param e
   * @param args
   * @return
   */
  public static int verbose(String tag, String format, Throwable e, Object... args) {
    if (BuildConfig.DEBUG) {
      return Log.v(tag, format(format, args), e);
    } else {
      return 0;
    }
  }

  /**
   * Send a DEBUG log message.
   *
   * @param tag
   * @param format
   * @param args
   * @return
   */
  public static int debug(String tag, String format, Object... args) {
    if (BuildConfig.DEBUG) {
      return Log.d(tag, format(format, args));
    } else {
      return 0;
    }
  }

  /**
   * Send a DEBUG log message and log the exception.
   *
   * @param tag
   * @param msg
   * @return
   */
  public static int debug(String tag, String msg) {
    if (BuildConfig.DEBUG) {
      return Log.d(tag, msg);
    } else {
      return 0;
    }
  }

  /**
   * Send a DEBUG log message and log the exception.
   *
   * @param tag
   * @param format
   * @param e
   * @param args
   * @return
   */
  public static int debug(String tag, String format, Throwable e, Object... args) {
    if (BuildConfig.DEBUG) {
      return Log.d(tag, format(format, args), e);
    } else {
      return 0;
    }
  }

  /**
   * Send a WARN log message.
   *
   * @param tag
   * @param format
   * @param args
   * @return
   */
  public static int warn(String tag, String format, Object... args) {
    if (BuildConfig.DEBUG) {
      return Log.w(tag, format(format, args));
    } else {
      return 0;
    }
  }

  /**
   * Send a WARN log message and log the exception.
   *
   * @param tag
   * @param msg
   * @return
   */
  public static int warn(String tag, String msg) {
    if (BuildConfig.DEBUG) {
      return Log.w(tag, msg);
    } else {
      return 0;
    }
  }

  /**
   * Send a WARN log message and log the exception.
   *
   * @param tag
   * @param format
   * @param e
   * @param args
   * @return
   */
  public static int warn(String tag, String format, Throwable e, Object... args) {
    if (BuildConfig.DEBUG) {
      return Log.w(tag, format(format, args), e);
    } else {
      return 0;
    }
  }

  /**
   * Send a INFO log message.
   *
   * @param tag
   * @param format
   * @param args
   * @return
   */
  public static int info(String tag, String format, Object... args) {
    if (BuildConfig.DEBUG) {
      return Log.i(tag, format(format, args));
    } else {
      return 0;
    }
  }

  /**
   * Send a INFO log message and log the exception.
   *
   * @param tag
   * @param msg
   * @return
   */
  public static int info(String tag, String msg) {
    if (BuildConfig.DEBUG) {
      return Log.i(tag, msg);
    } else {
      return 0;
    }
  }

  /**
   * Send a INFO log message and log the exception.
   *
   * @param tag
   * @param format
   * @param e
   * @param args
   * @return
   */
  public static int info(String tag, String format, Throwable e, Object... args) {
    if (BuildConfig.DEBUG) {
      return Log.i(tag, format(format, args), e);
    } else {
      return 0;
    }
  }

  /**
   * Send a ERROR log message.
   *
   * @param tag
   * @param format
   * @param args
   * @return
   */
  public static int error(String tag, String format, Object... args) {
    if (BuildConfig.DEBUG) {
      return Log.e(tag, format(format, args));
    } else {
      return 0;
    }
  }

  /**
   * Send a ERROR log message and log the exception.
   *
   * @param tag
   * @param msg
   * @return
   */
  public static int error(String tag, String msg) {
    if (BuildConfig.DEBUG) {
      return Log.e(tag, msg);
    } else {
      return 0;
    }
  }

  /**
   * Send a ERROR log message and log the exception.
   *
   * @param tag
   * @param format
   * @param e
   * @param args
   * @return
   */
  public static int error(String tag, String format, Throwable e, Object... args) {
    if (BuildConfig.DEBUG) {
      return Log.e(tag, format(format, args), e);
    } else {
      return 0;
    }
  }

  private static String format(String format, Object... args) {
    try {
      return String.format(format == null ? EMPTY : format, args);
    } catch (RuntimeException e) {
      Logger.warn(TAG, "format error. reason=%s, format=%s", e.getMessage(), format);
      return String.format(EMPTY, format);
    }
  }

  /** Print exception. */
  public static void log(Exception e) {
    if (BuildConfig.DEBUG) {
      e.printStackTrace();
    }
  }
}
