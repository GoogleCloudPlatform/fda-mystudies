package com.google.cloud.healthcare.fdamystudies.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.jayway.jsonpath.JsonPath;
import javax.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;

public class AppsControllerTest extends BaseMockIT {

  private static final String ERROR_DESCRIPTION = "$.error_description";

  private static final String CUSTOM_APP_ID = "customAppId";

  private static final String GET_APP_CONTACT_EMAILS_PATH = "/participant-user-datastore/apps";

  @Autowired private AppsController appsController;

  @Test
  public void contextLoads() {
    assertNotNull(appsController);
    assertNotNull(mockMvc);
  }

  @Test
  public void getAppContactEmailsSuccess() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
    MvcResult result =
        mockMvc
            .perform(
                get(GET_APP_CONTACT_EMAILS_PATH)
                    .param(CUSTOM_APP_ID, "GCPMS002")
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

    String contactUsEmailId =
        JsonPath.read(result.getResponse().getContentAsString(), "$.contactUsEmail");
    assertEquals(Constants.CONTACT_US_EMAIL, contactUsEmailId);

    String fromEmailId = JsonPath.read(result.getResponse().getContentAsString(), "$.fromEmail");
    assertEquals(Constants.FROM_EMAIL, fromEmailId);
  }

  @Test
  public void getAppContactEmailsBadRequest() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
    MvcResult result =
        mockMvc
            .perform(
                get(GET_APP_CONTACT_EMAILS_PATH)
                    .param(CUSTOM_APP_ID, "")
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andReturn();

    String contactUsEmailId =
        JsonPath.read(result.getResponse().getContentAsString(), ERROR_DESCRIPTION);
    assertEquals("The request cannot be fulfilled due to bad syntax", contactUsEmailId);
  }

  @Test
  public void getAppContactEmailsNotFound() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
    MvcResult result =
        mockMvc
            .perform(
                get(GET_APP_CONTACT_EMAILS_PATH)
                    .param(CUSTOM_APP_ID, "GCPMS0012")
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andReturn();

    String contactUsEmailId =
        JsonPath.read(result.getResponse().getContentAsString(), ERROR_DESCRIPTION);
    assertEquals("App not found", contactUsEmailId);
  }
}
