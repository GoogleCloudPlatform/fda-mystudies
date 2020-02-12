/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
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

package com.harvard.utils;

import android.util.Log;
import com.crashlytics.android.BuildConfig;

/**
 * Android Log wrapper class that can use {@link String#format(String, Object...)} in logging message
 */
public class Logger {

    private static final String TAG = Logger.class.getSimpleName();
    private static final String EMPTY = "";

    /**
     * Send a VERBOSE log message.
     * @param tag
     * @param format
     * @param args
     * @return
     */
    public static int v(String tag, String format, Object... args) {
        if (BuildConfig.DEBUG) {
            return Log.v(tag, format(format, args));
        }
        else {
            return 0;
        }
    }

    /**
     * Send a VERBOSE log message and log the exception.
     * @param tag
     * @param msg
     * @return
     */
    public static int v(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            return Log.v(tag, msg);
        }
        else {
            return 0;
        }
    }

    /**
     * Send a VERBOSE log message and log the exception.
     * @param tag
     * @param format
     * @param e
     * @param args
     * @return
     */
    public static int v(String tag, String format, Throwable e, Object... args) {
        if (BuildConfig.DEBUG) {
            return Log.v(tag, format(format, args), e);
        } else {
            return 0;
        }
    }

    /**
     * Send a DEBUG log message.
     * @param tag
     * @param format
     * @param args
     * @return
     */
    public static int d(String tag, String format, Object... args) {
        if (BuildConfig.DEBUG) {
            return Log.d(tag, format(format, args));
        } else {
            return 0;
        }
    }

    /**
     * Send a DEBUG log message and log the exception.
     * @param tag
     * @param msg
     * @return
     */
    public static int d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            return Log.d(tag, msg);
        }
        else {
            return 0;
        }
    }

    /**
     * Send a DEBUG log message and log the exception.
     * @param tag
     * @param format
     * @param e
     * @param args
     * @return
     */
    public static int d(String tag, String format, Throwable e, Object... args) {
        if (BuildConfig.DEBUG) {
            return Log.d(tag, format(format, args), e);
        } else {
            return 0;
        }
    }

    /**
     * Send a WARN log message.
     * @param tag
     * @param format
     * @param args
     * @return
     */
    public static int w(String tag, String format, Object... args) {
        if (BuildConfig.DEBUG) {
            return Log.w(tag, format(format, args));
        } else {
            return 0;
        }
    }

    /**
     * Send a WARN log message and log the exception.
     * @param tag
     * @param msg
     * @return
     */
    public static int w(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            return Log.w(tag, msg);
        } else {
            return 0;
        }
    }

    /**
     * Send a WARN log message and log the exception.
     * @param tag
     * @param format
     * @param e
     * @param args
     * @return
     */
    public static int w(String tag, String format, Throwable e, Object... args) {
        if (BuildConfig.DEBUG) {
            return Log.w(tag, format(format, args), e);
        } else {
            return 0;
        }
    }

    /**
     * Send a INFO log message.
     * @param tag
     * @param format
     * @param args
     * @return
     */
    public static int i(String tag, String format, Object... args) {
        if (BuildConfig.DEBUG) {
            return Log.i(tag, format(format, args));
        } else {
            return 0;
        }
    }

    /**
     * Send a INFO log message and log the exception.
     * @param tag
     * @param msg
     * @return
     */
    public static int i(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            return Log.i(tag, msg);
        } else {
            return 0;
        }
    }

    /**
     * Send a INFO log message and log the exception.
     * @param tag
     * @param format
     * @param e
     * @param args
     * @return
     */
    public static int i(String tag, String format, Throwable e, Object... args) {
        if (BuildConfig.DEBUG) {
            return Log.i(tag, format(format, args), e);
        } else {
            return 0;
        }
    }

    /**
     * Send a ERROR log message.
     * @param tag
     * @param format
     * @param args
     * @return
     */
    public static int e(String tag, String format, Object... args) {
        if (BuildConfig.DEBUG) {
            return Log.e(tag, format(format, args));
        } else {
            return 0;
        }
    }

    /**
     * Send a ERROR log message and log the exception.
     * @param tag
     * @param msg
     * @return
     */
    public static int e(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            return Log.e(tag, msg);
        } else {
            return 0;
        }
    }

    /**
     * Send a ERROR log message and log the exception.
     * @param tag
     * @param format
     * @param e
     * @param args
     * @return
     */
    public static int e(String tag, String format, Throwable e, Object... args) {
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
            Logger.w(TAG, "format error. reason=%s, format=%s", e.getMessage(), format);
            return String.format(EMPTY, format);
        }

    }

    /**
     * Print exception
     */
    public static void log(Exception e) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace();
        }
    }

}