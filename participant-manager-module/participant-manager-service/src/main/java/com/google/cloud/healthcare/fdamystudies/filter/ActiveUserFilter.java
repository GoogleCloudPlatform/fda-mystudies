/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.filter;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.USER_ID_HEADER;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.getObjectMapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.repository.UserRegAdminRepository;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

@Component
@Order(3)
@ConditionalOnProperty(
    value = "commonservice.activeuser.filter.enabled",
    havingValue = "true",
    matchIfMissing = false)
public class ActiveUserFilter implements Filter {

  private XLogger logger = XLoggerFactory.getXLogger(ActiveUserFilter.class.getName());

  public static final String TOKEN = "token";

  public static final String ACTIVE = "active";

  private Map<String, String[]> uriTemplateAndMethods = new HashMap<>();

  @Autowired ServletContext context;

  @Autowired private UserRegAdminRepository userRegAdminRepository;

  @PostConstruct
  public void init() {
    uriTemplateAndMethods.put(
        String.format("%s/locations", context.getContextPath()),
        new String[] {HttpMethod.POST.name(), HttpMethod.GET.name()});
    uriTemplateAndMethods.put(
        String.format("%s/locations/{locationId}", context.getContextPath()),
        new String[] {HttpMethod.PUT.name(), HttpMethod.GET.name()});
  }

  protected Map<String, String[]> getUriTemplateAndHttpMethodsMap() {
    return uriTemplateAndMethods;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    logger.entry(
        String.format("begin doFilter() for %s", ((HttpServletRequest) request).getRequestURI()));
    HttpServletRequest req = (HttpServletRequest) request;
    if (validatePathAndHttpMethod(req)) {
      logger.info(String.format("check user status for %s", req.getRequestURI()));
      String userId = req.getHeader(USER_ID_HEADER);
      ErrorCode ec = null;

      if (StringUtils.isEmpty(userId)) {
        logger.error(String.format("userId is empty errorCode=%s", ErrorCode.USER_ID_REQUIRED));
        ec = ErrorCode.USER_ID_REQUIRED;
      } else {
        Optional<UserRegAdminEntity> optUserRegAdminUser = userRegAdminRepository.findById(userId);
        ec = !optUserRegAdminUser.isPresent() ? ErrorCode.USER_NOT_EXISTS : null;

        if (optUserRegAdminUser.isPresent()) {
          UserRegAdminEntity adminUser = optUserRegAdminUser.get();
          ec = !adminUser.isActive() ? ErrorCode.USER_NOT_ACTIVE : null;
        }
      }

      if (ec != null) {
        logger.error(String.format("User status check failed with error code=%s", ec));
        setErrorResponse(response, ec);
      } else {
        chain.doFilter(request, response);
      }

    } else {
      logger.info(String.format("skip ActiveUserFilter for %s", req.getRequestURI()));
      chain.doFilter(request, response);
    }
  }

  private boolean validatePathAndHttpMethod(HttpServletRequest req) {
    String method = req.getMethod().toUpperCase();
    for (Map.Entry<String, String[]> entry : getUriTemplateAndHttpMethodsMap().entrySet()) {
      if (ArrayUtils.contains(entry.getValue(), method)
          && checkPathMatches(entry.getKey(), req.getRequestURI())) {
        return true;
      }
    }
    return false;
  }

  private static boolean checkPathMatches(String uriTemplate, String path) {
    PathPatternParser parser = new PathPatternParser();
    parser.setMatchOptionalTrailingSeparator(true);
    PathPattern p = parser.parse(uriTemplate);
    return p.matches(PathContainer.parsePath(path));
  }

  private void setErrorResponse(ServletResponse response, ErrorCode ec) throws IOException {
    HttpServletResponse res = (HttpServletResponse) response;
    res.setStatus(ec.getStatus());
    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
    JsonNode reponse = getObjectMapper().convertValue(ec, JsonNode.class);
    res.getOutputStream().write(reponse.toString().getBytes());
  }
}
