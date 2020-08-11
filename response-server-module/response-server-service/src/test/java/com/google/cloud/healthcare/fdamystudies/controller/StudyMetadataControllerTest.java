/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.asJsonString;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.readJsonFile;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.cloud.healthcare.fdamystudies.bean.StudyMetadataBean;
import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.dao.CloudFirestoreResponsesDaoImpl;
import com.google.cloud.healthcare.fdamystudies.service.StudyMetadataServiceImpl;
import com.google.cloud.healthcare.fdamystudies.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;

public class StudyMetadataControllerTest extends BaseMockIT {

  @MockBean private CloudFirestoreResponsesDaoImpl responsesDaoMock;

  @InjectMocks private StudyMetadataServiceImpl studyMetadataService;

  @Test
  void testStudyMetadataSavedContent() throws Exception {
    HttpHeaders headers = TestUtils.newCommonHeaders();
    StudyMetadataBean studyMetadataBeanRequest = createValidStudyMetadataBean();

    mockMvc
        .perform(
            post(ApiEndpoint.SAVE_STUDY_METADATA.getPath())
                .contextPath(getContextPath())
                .content(asJsonString(studyMetadataBeanRequest))
                .headers(headers))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  void testStudyMetadataSavedContentInvalidStudyId() throws Exception {
    HttpHeaders headers = TestUtils.newCommonHeaders();
    // Step 1: set empty studyId
    StudyMetadataBean studyMetadataBeanRequest = createValidStudyMetadataBean();
    studyMetadataBeanRequest.setStudyId("");
    // Step 2: call API and expect bad request
    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.SAVE_STUDY_METADATA.getPath())
                    .contextPath(getContextPath())
                    .content(asJsonString(studyMetadataBeanRequest))
                    .headers(headers))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.appErrorCode").isNumber())
            .andExpect(jsonPath("$.userMessage").isNotEmpty())
            .andReturn();

    String actualResponse = result.getResponse().getContentAsString();
    String expectedResponse = readJsonFile("/invalid_args_expected_bad_request_response.json");
    JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.NON_EXTENSIBLE);
  }

  private StudyMetadataBean createValidStudyMetadataBean() {
    return new StudyMetadataBean(
        "TEST_STUDY_ID",
        "TEST_TITLE",
        "1.0",
        "Open",
        "Active",
        "Health",
        "TEST_TAGLINE",
        "TEST_SPONSOR",
        "Yes",
        "TEST_APP_ID",
        "Test App",
        "Test app for population health study",
        "TEST_ORG_ID");
  }
}
