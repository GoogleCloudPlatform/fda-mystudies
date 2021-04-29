/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package com.hphc.mystudies.web.servlet;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class StudyMetaDataController extends HttpServlet {

  private static final XLogger LOGGER =
      XLoggerFactory.getXLogger(StudyMetaDataController.class.getName());

  private static final long serialVersionUID = 1L;

  private String port = "";

  private String forwardURL = "";

  @Override
  public void init(ServletConfig servletConfig) throws ServletException {
    LOGGER.entry("begin init()");
    super.init(servletConfig);
    LOGGER.exit("init() :: Ends");
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    LOGGER.entry("begin doGet()");
    doPost(req, resp);
    LOGGER.exit("doGet() :: Ends");
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    LOGGER.entry("begin doPost()");
    String jsonp_callback = null;
    String localForwardURL = forwardURL;
    URL URL = new URL(localForwardURL);
    HttpURLConnection urlConnection = (HttpURLConnection) URL.openConnection();
    BufferedInputStream buffer = new BufferedInputStream(urlConnection.getInputStream());
    StringBuilder builder = new StringBuilder();
    int byteRead;
    try {
      jsonp_callback = (String) req.getSession().getServletContext().getAttribute("jsonp.callback");
    } catch (Exception e) {
      LOGGER.error(
          "StudyMetaDataController - doPost() :: ERROR ==> jsonp_callback key is missing... ", e);
    }

    try {
      localForwardURL +=
          ":"
              + new Integer(port).toString()
              + req.getContextPath()
              + req.getPathInfo()
              + "?"
              + req.getQueryString();
    } catch (NumberFormatException e) {
      localForwardURL += req.getContextPath() + req.getPathInfo() + "?" + req.getQueryString();
    }

    while ((byteRead = buffer.read()) != -1) {
      builder.append((char) byteRead);
    }
    buffer.close();
    LOGGER.info(
        "INFO: StudyMetaDataController - doPost() :: HttpURLConnection.response = " + builder);
    if (req.getParameter(jsonp_callback) != null) {
      if (req.getPathInfo().indexOf("/json") != -1) {
        resp.getWriter().write(req.getParameter(jsonp_callback) + "(" + builder.toString() + ")");
      } else {
        resp.getWriter().write(builder.toString());
      }
    } else {
      resp.getWriter().write(builder.toString());
    }
    LOGGER.exit("doPost() :: Ends ");
  }
}
