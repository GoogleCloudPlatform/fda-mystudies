/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.filter;

import static com.google.cloud.healthcare.fdamystudies.common.CommonAuditEvent.ACCESS_TOKEN_INVALID_OR_EXPIRED;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.getObjectMapper;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.getObjectNode;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.AuditEventHelper;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.service.OAuthService;
import java.io.IOException;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

public abstract class BaseTokenIntrospectionFilter implements Filter {

  private XLogger logger = XLoggerFactory.getXLogger(BaseTokenIntrospectionFilter.class.getName());

  public static final String TOKEN = "token";

  public static final String ACTIVE = "active";

  @Autowired private OAuthService oauthService;

  @Autowired private AuditEventHelper auditEventHelper;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    logger.entry(
        String.format("begin doFilter() for %s", ((HttpServletRequest) request).getRequestURI()));
    HttpServletRequest req = (HttpServletRequest) request;
    if (validatePathAndHttpMethod(req)) {
      logger.info(String.format("validate token for %s", req.getRequestURI()));

      String auth = req.getHeader("Authorization");
      if (StringUtils.isEmpty(auth)) {
        logger.exit("token is empty, return 401 Unauthorized response");
        setUnauthorizedResponse(response);
      } else {
        validateOAuthToken(request, response, chain, auth);
      }
    } else {
      logger.info(String.format("skip token validation for %s", req.getRequestURI()));
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

  private void validateOAuthToken(
      ServletRequest request, ServletResponse response, FilterChain chain, String auth)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(req);

    String token = StringUtils.replace(auth, "Bearer", "").trim();
    ObjectNode params = getObjectNode();
    params.put(TOKEN, token);
    ResponseEntity<JsonNode> oauthResponse = oauthService.introspectToken(params);
    if (oauthResponse.getStatusCode().is2xxSuccessful()) {
      if (oauthResponse.getBody().get(ACTIVE).booleanValue()) {
        chain.doFilter(request, response);
      } else {
        logger.exit("token is invalid, return 401 Unauthorized response");
        auditEventHelper.logEvent(ACCESS_TOKEN_INVALID_OR_EXPIRED, auditRequest);
        setUnauthorizedResponse(response);
      }
    } else {
      logger.exit(
          String.format(
              "status=%d, active=%b",
              oauthResponse.getStatusCodeValue(),
              oauthResponse.getBody().get(ACTIVE).booleanValue()));
      setUnauthorizedResponse(response);
    }
  }

  private void setUnauthorizedResponse(ServletResponse response) throws IOException {
    HttpServletResponse res = (HttpServletResponse) response;
    res.setStatus(HttpStatus.UNAUTHORIZED.value());
    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
    JsonNode reponse = getObjectMapper().convertValue(ErrorCode.UNAUTHORIZED, JsonNode.class);
    res.getOutputStream().write(reponse.toString().getBytes());
  }
  /** HashMap where key=uriTemplate, value=array of http method names */
  protected abstract Map<String, String[]> getUriTemplateAndHttpMethodsMap();
}
