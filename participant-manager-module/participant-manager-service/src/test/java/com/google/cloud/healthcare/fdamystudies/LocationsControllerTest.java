package com.google.cloud.healthcare.fdamystudies;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.cloud.healthcare.fdamystudies.bean.LocationBean;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.controller.LocationsController;
import com.google.cloud.healthcare.fdamystudies.service.LocationService;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;
import com.jayway.jsonpath.JsonPath;

public class LocationsControllerTest extends BaseMockIT {

  private static final String LOCATIONS_PATH = "/locations";
  private static String customId;

  @Autowired private LocationsController controller;
  @Autowired private LocationService locationService;

  @Test
  public void contextLoads() {
    assertNotNull(controller);
    assertNotNull(mockMvc);
    assertNotNull(locationService);
  }

  @Test
  public void addNewLocationSuccess() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();

    String requestJson =
        getLocationAsJson(
            Constants.CUSTOM_ID, Constants.LOCATION_NAME, Constants.LOCATION_DESCRIPTION);

    MvcResult result = performPost(LOCATIONS_PATH, requestJson, headers, "customId", CREATED);

    customId = JsonPath.read(result.getResponse().getContentAsString(), "$.customId");
    assertNotNull(customId);
    assertTrue(customId.equals(Constants.CUSTOM_ID));
  }

  @Test
  public void addNewLocationBadRequest() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();

    // empty customId
    String requestJson =
        getLocationAsJson("", Constants.LOCATION_NAME, Constants.LOCATION_DESCRIPTION);
    performPost(LOCATIONS_PATH, requestJson, headers, "", BAD_REQUEST);

    // empty location name
    requestJson = getLocationAsJson(Constants.CUSTOM_ID, "", Constants.LOCATION_DESCRIPTION);
    performPost(LOCATIONS_PATH, requestJson, headers, "", BAD_REQUEST);

    // empty location description
    requestJson = getLocationAsJson(Constants.CUSTOM_ID, Constants.LOCATION_NAME, "");
    performPost(LOCATIONS_PATH, requestJson, headers, "", BAD_REQUEST);

    // custom id not matching with regex
    requestJson =
        getLocationAsJson(
            Constants.CUSTOM_ID_NOT_MATCHING_REGEX,
            Constants.LOCATION_NAME,
            Constants.LOCATION_DESCRIPTION);

    performPost(LOCATIONS_PATH, requestJson, headers, "", BAD_REQUEST);
  }

  @Test
  public void addNewLocationUnauthorized() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.set(Constants.USER_ID_HEADER, Constants.INVALID_USER_ID);

    // invalid user id
    String requestJson =
        getLocationAsJson(
            Constants.CUSTOM_ID, Constants.LOCATION_NAME, Constants.LOCATION_DESCRIPTION);

    performPost(LOCATIONS_PATH, requestJson, headers, Constants.UNAUTHORIZED_MSG, UNAUTHORIZED);
  }

  @Test
  public void addNewLocationForbidden() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.set(Constants.USER_ID_HEADER, Constants.NEW_USER_ID);

    // do not have permission to view or add or update locations
    String requestJson =
        getLocationAsJson(
            Constants.CUSTOM_ID, Constants.LOCATION_NAME, Constants.LOCATION_DESCRIPTION);

    performPost(
        LOCATIONS_PATH, requestJson, headers, Constants.FORBIDDEN_PERMISSION_MSG, FORBIDDEN);
  }

  private String getLocationAsJson(String customId, String name, String description)
      throws JsonProcessingException {
    LocationBean locationBean = new LocationBean(customId, name, description);
    return getObjectMapper().writeValueAsString(locationBean);
  }
}
