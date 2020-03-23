package com.fdahpstudydesigner.util;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ServletContextHolder implements ServletContextListener {

  private static ServletContext servletContext;


  public static ServletContext getServletContext() {
    return servletContext;
  }


  public static void setServletContext(ServletContext servletContext) {
    ServletContextHolder.servletContext = servletContext;
  }

  
  @Override
  public void contextDestroyed(ServletContextEvent sce) {}

  
  @Override
  public void contextInitialized(ServletContextEvent sce) {
    ServletContextHolder.setServletContext(sce.getServletContext());
  }
}
