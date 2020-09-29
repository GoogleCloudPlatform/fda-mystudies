/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.filter;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class TokenIntrospectionFilter extends BaseTokenIntrospectionFilter {

  private Map<String, String[]> uriTemplateAndMethods = new HashMap<>();

  @Autowired ServletContext context;

  @PostConstruct
  public void init() {

    uriTemplateAndMethods.put(
        String.format("%s/apps", context.getContextPath()), new String[] {HttpMethod.GET.name()});
    uriTemplateAndMethods.put(
        String.format("%s/apps/{appId}/participants", context.getContextPath()),
        new String[] {HttpMethod.GET.name()});

    uriTemplateAndMethods.put(
        String.format("%s/consents/{consentId}/consentDocument", context.getContextPath()),
        new String[] {HttpMethod.GET.name()});

    uriTemplateAndMethods.put(
        String.format("%s/locations", context.getContextPath()),
        new String[] {HttpMethod.POST.name(), HttpMethod.GET.name()});
    uriTemplateAndMethods.put(
        String.format("%s/locations/{locationId}", context.getContextPath()),
        new String[] {HttpMethod.PUT.name(), HttpMethod.GET.name()});

    uriTemplateAndMethods.put(
        String.format("%s/sites", context.getContextPath()),
        new String[] {HttpMethod.POST.name(), HttpMethod.GET.name()});
    uriTemplateAndMethods.put(
        String.format("%s/sites/{siteId}/decommission", context.getContextPath()),
        new String[] {HttpMethod.PUT.name()});
    uriTemplateAndMethods.put(
        String.format("%s/sites/{siteId}/participants", context.getContextPath()),
        new String[] {HttpMethod.POST.name(), HttpMethod.GET.name()});
    uriTemplateAndMethods.put(
        String.format("%s/sites/{siteId}/participants/invite", context.getContextPath()),
        new String[] {HttpMethod.POST.name()});
    uriTemplateAndMethods.put(
        String.format("%s/sites/{participantRegistrySiteId}/participant", context.getContextPath()),
        new String[] {HttpMethod.GET.name()});
    uriTemplateAndMethods.put(
        String.format("%s/sites/{siteId}/participants/import", context.getContextPath()),
        new String[] {HttpMethod.POST.name()});
    uriTemplateAndMethods.put(
        String.format("%s/sites/{siteId}/participants/status", context.getContextPath()),
        new String[] {HttpMethod.PATCH.name()});

    uriTemplateAndMethods.put(
        String.format("%s/studies", context.getContextPath()),
        new String[] {HttpMethod.GET.name()});
    uriTemplateAndMethods.put(
        String.format("%s/studies/{studyId}/participants", context.getContextPath()),
        new String[] {HttpMethod.GET.name()});
    uriTemplateAndMethods.put(
        String.format("%s/studies/{studyId}/targetEnrollment", context.getContextPath()),
        new String[] {HttpMethod.PATCH.name()});

    uriTemplateAndMethods.put(
        String.format("%s/users", context.getContextPath()),
        new String[] {HttpMethod.POST.name(), HttpMethod.GET.name()});
    uriTemplateAndMethods.put(
        String.format("%s/users/{superAdminUserId}/", context.getContextPath()),
        new String[] {HttpMethod.PUT.name()});
    uriTemplateAndMethods.put(
        String.format("%s/users/{adminId}", context.getContextPath()),
        new String[] {HttpMethod.GET.name()});
    uriTemplateAndMethods.put(
        String.format("%s/users/admin/{adminId}", context.getContextPath()),
        new String[] {HttpMethod.GET.name()});

    uriTemplateAndMethods.put(
        String.format("%s/users/{userId}", context.getContextPath()),
        new String[] {HttpMethod.GET.name(), HttpMethod.PATCH.name()});
    uriTemplateAndMethods.put(
        String.format("%s/users/{userId}/profile", context.getContextPath()),
        new String[] {HttpMethod.PUT.name()});
    uriTemplateAndMethods.put(
        String.format("%s/users/", context.getContextPath()),
        new String[] {HttpMethod.POST.name()});
  }

  @Override
  protected Map<String, String[]> getUriTemplateAndHttpMethodsMap() {
    return uriTemplateAndMethods;
  }
}
