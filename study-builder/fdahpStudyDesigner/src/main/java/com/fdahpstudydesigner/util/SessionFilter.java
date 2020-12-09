package com.fdahpstudydesigner.util;

import java.io.IOException;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SessionFilter implements Filter {

  protected static final Map<String, String> configMap = FdahpStudyDesignerUtil.getAppProperties();

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse res = (HttpServletResponse) response;
    Cookie[] allCookies = req.getCookies();
    if (allCookies != null) {
      for (Cookie cookie : allCookies) {
        if ("JSESSIONID".equals(cookie.getName())) {
          String secure = configMap.getOrDefault("secure.cookie", String.valueOf(true));
          cookie.setSecure(Boolean.valueOf(secure));
          // We don't have setHttpOnly() method in servlet 2.5 version
          res.addCookie(cookie);
        }
      }
    }
    chain.doFilter(req, res);
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  @Override
  public void destroy() {}
}
