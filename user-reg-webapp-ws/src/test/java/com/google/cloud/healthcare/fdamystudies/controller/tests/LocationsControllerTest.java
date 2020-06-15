package com.google.cloud.healthcare.fdamystudies.controller.tests;

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

  private static final String LOCATIONS_WITH_LOCATION_ID_PATH = "/locations/{locationId}";
  private static final String LOCATIONS_PATH = "/locations";
  private static String customId;
  private static String status;

  @Autowired private LocationsController controller;
  @Autowired private LocationService locationService;

  @Test
  public void contextLoads() {
    assertNotNull(controller);
    assertNotNull(mockMvc);
    assertNotNull(locationService);
  }

  @Test
  public void getLocationsSuccess() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();
    // without location Id
    performGet(LOCATIONS_PATH, headers, "", OK, "");

    // with location id
    performGet(
        LOCATIONS_WITH_LOCATION_ID_PATH, headers, "", OK, Constants.LOCATION_ID_PATH_VARIABLE);
  }

  @Test
  public void getLocationsUnauthorized() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.set(Constants.USER_ID_HEADER, Constants.INVALID_USER_ID);

    // invalid user id
    performGet(LOCATIONS_PATH, headers, Constants.UNAUTHORIZED_MSG, UNAUTHORIZED, "");
  }

  @Test
  public void getLocationsForbidden() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.set(Constants.USER_ID_HEADER, Constants.NEW_USER_ID);

    // do not have permission to view or add or update locations
    performGet(
        LOCATIONS_WITH_LOCATION_ID_PATH,
        headers,
        Constants.FORBIDDEN_PERMISSION_MSG,
        FORBIDDEN,
        Constants.LOCATION_ID_PATH_VARIABLE);
  }

  @Test
  public void getLocationsBadRequests() throws Exception {
    // empty user id
    performGet(LOCATIONS_PATH, new HttpHeaders(), "", BAD_REQUEST, "");
  }

  @Test
  public void addNewLocationSuccess() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();

    String requestJson =
        getLocationAsJson(
            Constants.CUSTOM_ID, Constants.LOCATION_NAME, Constants.LOCATION_DESCRIPTION);

    MvcResult result = performPost(LOCATIONS_PATH, requestJson, headers, "", CREATED);

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

  @Test
  public void updateLocationSuccess() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();

    LocationBean locationBean = new LocationBean("", Constants.LOCATION_STATUS);
    String requestJson = getObjectMapper().writeValueAsString(locationBean);

    MvcResult result =
        performPut(
            LOCATIONS_WITH_LOCATION_ID_PATH,
            requestJson,
            headers,
            "",
            OK,
            Constants.LOCATION_ID_PATH_VARIABLE);

    status = JsonPath.read(result.getResponse().getContentAsString(), "$.status");
    assertNotNull(status);
    assertTrue(status.equals(Constants.LOCATION_STATUS));
  }

  @Test
  public void updateLocationForbidden() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.set(Constants.USER_ID_HEADER, Constants.NEW_USER_ID);
    // new user id
    LocationBean locationBean = new LocationBean("", Constants.LOCATION_STATUS);
    String requestJson = getObjectMapper().writeValueAsString(locationBean);

    performPut(
        LOCATIONS_WITH_LOCATION_ID_PATH,
        requestJson,
        headers,
        "",
        FORBIDDEN,
        Constants.LOCATION_ID_PATH_VARIABLE);
  }

  @Test
  public void updateLocationUnauthorized() throws Exception {
    // invalid user id
    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.set(Constants.USER_ID_HEADER, Constants.INVALID_USER_ID);

    LocationBean locationBean = new LocationBean("", Constants.LOCATION_STATUS);
    String requestJson = getObjectMapper().writeValueAsString(locationBean);

    performPut(
        LOCATIONS_WITH_LOCATION_ID_PATH,
        requestJson,
        headers,
        "",
        UNAUTHORIZED,
        Constants.LOCATION_ID_PATH_VARIABLE);
  }

  @Test
  public void updateLocationBadRequest() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);

    // with custom id
    LocationBean locationBean = new LocationBean(Constants.CUSTOM_ID, Constants.LOCATION_STATUS);
    String requestJson = getObjectMapper().writeValueAsString(locationBean);

    performPut(
        LOCATIONS_WITH_LOCATION_ID_PATH,
        requestJson,
        headers,
        "",
        BAD_REQUEST,
        Constants.LOCATION_ID_PATH_VARIABLE);

    // location invalid status
    locationBean = new LocationBean("", Constants.LOCATION_INVALID_STATUS);
    requestJson = getObjectMapper().writeValueAsString(locationBean);

    performPut(
        LOCATIONS_WITH_LOCATION_ID_PATH,
        requestJson,
        headers,
        "",
        BAD_REQUEST,
        Constants.LOCATION_ID_PATH_VARIABLE);

    // with DECOMMISSION status
    locationBean = new LocationBean("", Constants.LOCATION_DECOMMISSION_STATUS);
    requestJson = getObjectMapper().writeValueAsString(locationBean);

    performPut(
        LOCATIONS_WITH_LOCATION_ID_PATH,
        requestJson,
        headers,
        "",
        BAD_REQUEST,
        Constants.LOCATION_ID_PATH_VARIABLE);
  }

  private String getLocationAsJson(String customId, String name, String description)
      throws JsonProcessingException {
    LocationBean locationBean = new LocationBean(customId, name, description);
    return getObjectMapper().writeValueAsString(locationBean);
  }
}
