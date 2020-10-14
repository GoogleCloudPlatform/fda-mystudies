package com.google.cloud.healthcare.fdamystudies.filter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.PlatformComponent;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TokenIntrospectionFilterTest extends BaseMockIT {

  @BeforeEach
  public void setUp() {
    WireMock.resetAllRequests();
  }

  @ParameterizedTest
  @EnumSource(
      value = ApiEndpoint.class,
      names = {
        "ADD",
        "PROCESS_ACTIVITY_RESPONSE",
        "WITHDRAW",
        "UPDATE_ACTIVITY_STATE",
        "STUDYMETADATA"
      })
  @DisplayName("Test token validation for POST endpoints")
  void shouldValidateTokenForPostEndpoints(ApiEndpoint apiEnum) throws Exception {
    mockMvc
        .perform(post(apiEnum.getPath()).contextPath(getContextPath()).headers(getCommonHeaders()))
        .andDo(print());

    verifyTokenIntrospectRequest(1);
  }

  @ParameterizedTest
  @EnumSource(
      value = ApiEndpoint.class,
      names = {"GET_PROCESS_ACTIVITY_RESPONSE", "GET_ACTIVITY_STATE"})
  @DisplayName("Test token validation for GET endpoints")
  void shouldValidateTokenForGetEndpoints(ApiEndpoint apiEnum) throws Exception {
    mockMvc
        .perform(get(apiEnum.getPath()).contextPath(getContextPath()).headers(getCommonHeaders()))
        .andDo(print());
    verifyTokenIntrospectRequest(1);
  }

  private HttpHeaders getCommonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("Authorization", VALID_BEARER_TOKEN);
    headers.add("source", PlatformComponent.PARTICIPANT_MANAGER.getValue());
    return headers;
  }
}
