/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import com.google.cloud.healthcare.fdamystudies.bean.EnrollmentTokenIdentifierBean;
import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantBo;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantBoRepository;
import com.google.cloud.healthcare.fdamystudies.utils.TestUtils;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;

import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.asJsonString;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.readJsonFile;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.APPLICATION_ID_HEADER;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.APPLICATION_ID_VALUE;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.STUDY_ID;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ParticipantIdControllerTest extends BaseMockIT {
  @Autowired private ParticipantBoRepository repository;

  @Test
  void shouldAddParticipant() throws Exception {
    // Step-1 call API to details to add participant id
    HttpHeaders headers = TestUtils.newCommonHeaders();
    headers.add(APPLICATION_ID_HEADER, APPLICATION_ID_VALUE);
    EnrollmentTokenIdentifierBean enrollmentTokenIdentifierBeanRequest =
        createValidEnrollmentTokenIdentifierBean();
    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.ADD_PARTICIPANT.getPath())
                    .contextPath(getContextPath())
                    .content(asJsonString(enrollmentTokenIdentifierBeanRequest))
                    .headers(headers))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isNotEmpty())
            .andReturn();

    // TODO(Dhanya) use .andExpect to assert the participantIdAddedVal instead of assertNotNull -
    // DONE - See above
    // TODO(Dhanya) rename to participantId - DONE
    String participantId = result.getResponse().getContentAsString();
    assertNotNull(participantId);
    // Step-2 Find ParticipantBo by participantId and compare with input ParticipantBo object
    List<ParticipantBo> participantBoList = repository.findByParticipantIdentifier(participantId);
    assertNotNull(participantBoList);
    assertEquals(1, participantBoList.size());
    assertEquals(
        enrollmentTokenIdentifierBeanRequest.getCustomStudyId(),
        participantBoList.get(0).getStudyId());
    assertEquals(
        enrollmentTokenIdentifierBeanRequest.getTokenIdentifier(),
        participantBoList.get(0).getTokenIdentifier());

    // Step-3 cleanup - delete the record from database
    repository.deleteAll();
  }

  @Test
  void shouldReturnBadRequestForMissingApplicationId() throws Exception {
    HttpHeaders headers = TestUtils.newCommonHeaders();

    // TODO (Dhanya) mockMvc preferred and expected error message required
    // DONE - Not checking for error message as the GlobalExceptionHandler causes test to fail
    // when running with Maven (different error messages with Maven and JUnit running locally)
    mockMvc
        .perform(
            post(ApiEndpoint.ADD_PARTICIPANT.getPath())
                .contextPath(getContextPath())
                .content(asJsonString(createValidEnrollmentTokenIdentifierBean()))
                .headers(headers))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  // TODO (Dhanya) assigning null not needed
  // Removed method shouldReturnBadRequestForNullStudyId()
  // Removed method shouldReturnBadRequestForNullEnrollmentTokenIdentifier

  // TODO (Dhanya) duplicate test scenario, combine tests with shouldReturnBadRequestForBlankStudyId
  // Done - Renamed test to BlankStudyId
  @Test
  void shouldReturnBadRequestForBlankStudyId() throws Exception {

    HttpHeaders headers = TestUtils.newCommonHeaders();
    headers.add(APPLICATION_ID_HEADER, APPLICATION_ID_VALUE);
    EnrollmentTokenIdentifierBean enrollTokenIdentifierBeanInvalidStudyId =
        new EnrollmentTokenIdentifierBean();
    enrollTokenIdentifierBeanInvalidStudyId.setCustomStudyId(" ");
    enrollTokenIdentifierBeanInvalidStudyId.setTokenIdentifier(UUID.randomUUID().toString());
    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.ADD_PARTICIPANT.getPath())
                    .contextPath(getContextPath())
                    .content(asJsonString(enrollTokenIdentifierBeanInvalidStudyId))
                    .headers(headers))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.appErrorCode").isNumber())
            .andExpect(jsonPath("$.userMessage").isNotEmpty())
            .andReturn();

    // TODO (Dhanya) use .andExpect to assert the appErrorCode and error message
    // DONE - See above. Followed AuditLog method to check for code type and message not empty.
    String actualResponse = result.getResponse().getContentAsString();
    String expectedResponse = readJsonFile("/invalid_args_expected_bad_request_response.json");
    JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.NON_EXTENSIBLE);
  }

  // TODO (Dhanya) duplicate test scenario, combine into
  // shouldReturnBadRequestForBlankEnrollmentToken
  // DONE
  @Test
  void shouldReturnBadRequestForBlankEnrollmentToken() throws Exception {

    HttpHeaders headers = TestUtils.newCommonHeaders();
    headers.add(APPLICATION_ID_HEADER, APPLICATION_ID_VALUE);
    EnrollmentTokenIdentifierBean enrollTokenIdentifierBeanInvalidStudyId =
        new EnrollmentTokenIdentifierBean();
    enrollTokenIdentifierBeanInvalidStudyId.setCustomStudyId(STUDY_ID);
    enrollTokenIdentifierBeanInvalidStudyId.setTokenIdentifier(" ");
    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.ADD_PARTICIPANT.getPath())
                    .contextPath(getContextPath())
                    .content(asJsonString(enrollTokenIdentifierBeanInvalidStudyId))
                    .headers(headers))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.appErrorCode").isNumber())
            .andExpect(jsonPath("$.userMessage").isNotEmpty())
            .andReturn();

    // TODO (Dhanya) use .andExpect to assert the appErrorCode and error message
    // DONE
    String actualResponse = result.getResponse().getContentAsString();
    String expectedResponse = readJsonFile("/invalid_args_expected_bad_request_response.json");
    JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.NON_EXTENSIBLE);
  }

  private EnrollmentTokenIdentifierBean createValidEnrollmentTokenIdentifierBean() {
    EnrollmentTokenIdentifierBean enrollmentTokenIdentifierBean =
        new EnrollmentTokenIdentifierBean();
    enrollmentTokenIdentifierBean.setCustomStudyId(STUDY_ID);
    enrollmentTokenIdentifierBean.setTokenIdentifier(UUID.randomUUID().toString());
    return enrollmentTokenIdentifierBean;
  }
}
