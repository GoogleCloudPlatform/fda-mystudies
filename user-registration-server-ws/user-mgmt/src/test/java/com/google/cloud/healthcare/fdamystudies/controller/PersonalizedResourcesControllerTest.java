/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.controller;

import static org.mockito.Mockito.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.cloud.healthcare.fdamystudies.TestApplicationContextInitializer;
import com.google.cloud.healthcare.fdamystudies.beans.UserResourceBean;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.service.CommonServiceImpl;
import com.google.cloud.healthcare.fdamystudies.service.PersonalizedUserReportService;
import com.google.cloud.healthcare.fdamystudies.util.GetUserInstitutionResources;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(
    controllers = {PersonalizedResourcesController.class},
    secure = false)
@ActiveProfiles("test")
@ContextConfiguration(initializers = TestApplicationContextInitializer.class)
@Import(ApplicationPropertyConfiguration.class)
public class PersonalizedResourcesControllerTest {

  @MockBean private PersonalizedUserReportService personalizedUserReportService;
  @MockBean private GetUserInstitutionResources institutionResourcesService;
  @MockBean private CommonServiceImpl commonService;

  @Autowired private MockMvc mvc;

  private static final UserResourceBean.ResourceType reportType =
      UserResourceBean.ResourceType.PERSONALIZED_REPORT;
  private static final UserResourceBean.ResourceType resourceType =
      UserResourceBean.ResourceType.INSTITUTION_RESOURCE;

  @Test
  public void ReturnsUserResources() throws Exception {
    Mockito.when(
            commonService.validateAccessToken(
                "test_user_id", "test_access_token", "test_client_token"))
        .thenReturn(1);
    Mockito.when(
            personalizedUserReportService.getLatestPersonalizedUserReports(
                "test_user_id", "test_study_id"))
        .thenReturn(
            Arrays.asList(
                new UserResourceBean("Report", "content", reportType, "0"),
                new UserResourceBean("Report 2", "content 2", reportType, "1")));
    Mockito.when(institutionResourcesService.getInstitutionResourcesForUser("test_user_id"))
        .thenReturn(
            Arrays.asList(
                new UserResourceBean("Resource 1", "content 1", resourceType, "2"),
                new UserResourceBean("Resource 2", "content 2", resourceType, "3")));
    mvc.perform(
            get("/getPersonalizedResources")
                .accept(MediaType.ALL)
                .header("userId", "test_user_id")
                .header("accessToken", "test_access_token")
                .header("clientToken", "test_client_token")
                .param("studyId", "test_study_id")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath(
                    "$.resources.[?(@.title == \"Report\" && @.content == \"content\" &&"
                        + " @.resourceType == \"report\" && @.resourcesId == \"report:0\" &&"
                        + " @.audience == \"All\" && @.notificationText == \"\" &&"
                        + " @.availability.length() == 0)]")
                .exists())
        .andExpect(
            jsonPath(
                    "$.resources.[?(@.title == \"Report 2\" && @.content == \"content 2\" &&"
                        + " @.resourceType == \"report\" && @.resourcesId == \"report:1\" &&"
                        + " @.audience == \"All\" && @.notificationText == \"\" &&"
                        + " @.availability.length() == 0)]")
                .exists())
        .andExpect(
            jsonPath(
                    "$.resources.[?(@.title == \"Resource 1\" && @.content == \"content 1\" &&"
                        + " @.resourceType == \"resources\" && @.resourcesId == \"resources:2\" &&"
                        + " @.audience == \"All\" && @.notificationText == \"\" &&"
                        + " @.availability.length() == 0)]")
                .exists())
        .andExpect(
            jsonPath(
                    "$.resources.[?(@.title == \"Resource 2\" && @.content == \"content 2\" &&"
                        + " @.resourceType == \"resources\" && @.resourcesId == \"resources:3\" &&"
                        + " @.audience == \"All\" && @.notificationText == \"\" &&"
                        + " @.availability.length() == 0)]")
                .exists());
  }

  @Test
  public void FailsToAuthenticate() throws Exception {
    Mockito.when(commonService.validateAccessToken(anyString(), anyString(), anyString()))
        .thenReturn(0);
    mvc.perform(
            get("/getPersonalizedResources")
                .accept(MediaType.ALL)
                .header("userId", "test_user_id")
                .header("accessToken", "test_access_token")
                .header("clientToken", "test_client_token")
                .param("studyId", "test_study_id")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isUnauthorized());
  }
}
