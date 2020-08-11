/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.readJsonFile;
import static com.google.cloud.healthcare.fdamystudies.common.TestConstants.CUSTOM_ID_VALUE;
import static com.google.cloud.healthcare.fdamystudies.common.TestConstants.LOCATION_DESCRIPTION_VALUE;
import static com.google.cloud.healthcare.fdamystudies.common.TestConstants.LOCATION_NAME_VALUE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.cloud.healthcare.fdamystudies.beans.LocationRequest;
import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.CommonConstants;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.JsonUtils;
import com.google.cloud.healthcare.fdamystudies.common.ManageLocation;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.controller.LocationController;
import com.google.cloud.healthcare.fdamystudies.helper.TestDataHelper;
import com.google.cloud.healthcare.fdamystudies.model.LocationEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.repository.LocationRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserRegAdminRepository;
import com.google.cloud.healthcare.fdamystudies.service.LocationService;
import com.jayway.jsonpath.JsonPath;

public class LocationControllerTest extends BaseMockIT {

  @Autowired private LocationController controller;

  @Autowired private LocationService locationService;

  @Autowired private TestDataHelper testDataHelper;

  @Autowired private LocationRepository locationRepository;

  @Autowired private UserRegAdminRepository userRegAdminRepository;

  private UserRegAdminEntity userRegAdminEntity;

  @BeforeEach
  public void setUp() {
    userRegAdminEntity = testDataHelper.createUserRegAdmin();
  }

  @Test
  public void contextLoads() {
    assertNotNull(controller);
    assertNotNull(mockMvc);
    assertNotNull(locationService);
  }

  @Test
  public void shouldReturnBadRequestForAddNewLocation() throws Exception {
    HttpHeaders headers = newCommonHeaders();
    LocationRequest locationRequest = new LocationRequest();
    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.ADD_NEW_LOCATION.getPath())
                    .content(JsonUtils.asJsonString(locationRequest))
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.violations").isArray())
            .andReturn();

    String actualResponse = result.getResponse().getContentAsString();
    String expectedResponse = readJsonFile("/responses/add_location_bad_request.json");
    JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void shouldReturnForbiddenForLocationAccessDenied() throws Exception {

    userRegAdminEntity.setManageLocations(ManageLocation.DENY.getValue());
    userRegAdminRepository.saveAndFlush(userRegAdminEntity);
    HttpHeaders headers = newCommonHeaders();

    mockMvc
        .perform(
            post(ApiEndpoint.ADD_NEW_LOCATION.getPath())
                .content(JsonUtils.asJsonString(getLocationRequest()))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(
            jsonPath("$.error_description", is(ErrorCode.LOCATION_ACCESS_DENIED.getDescription())))
        .andReturn();
  }

  @Test
  public void shouldCreateANewLocation() throws Exception {
    HttpHeaders headers = newCommonHeaders();
    // Step 1: Call API to create new location
    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.ADD_NEW_LOCATION.getPath())
                    .content(JsonUtils.asJsonString(getLocationRequest()))
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.locationId", notNullValue()))
            .andExpect(jsonPath("$.message", is(MessageCode.ADD_LOCATION_SUCCESS.getMessage())))
            .andReturn();

    String locationId = JsonPath.read(result.getResponse().getContentAsString(), "$.locationId");

    // Step 2: verify saved values
    Optional<LocationEntity> optLocationEntity = locationRepository.findById(locationId);
    LocationEntity locationEntity = optLocationEntity.get();
    assertNotNull(locationEntity);
    assertEquals(CUSTOM_ID_VALUE, locationEntity.getCustomId());
    assertEquals(LOCATION_NAME_VALUE, locationEntity.getName());
    assertEquals(LOCATION_DESCRIPTION_VALUE, locationEntity.getDescription());

    // Step 3: delete location
    locationRepository.deleteById(locationId);
  }

  @AfterEach
  public void cleanUp() {
    testDataHelper.getUserRegAdminRepository().delete(userRegAdminEntity);
  }

  private LocationRequest getLocationRequest() throws JsonProcessingException {
    return new LocationRequest(CUSTOM_ID_VALUE, LOCATION_NAME_VALUE, LOCATION_DESCRIPTION_VALUE);
  }

  public HttpHeaders newCommonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.set(CommonConstants.USER_ID_HEADER, userRegAdminEntity.getId());
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }
}
