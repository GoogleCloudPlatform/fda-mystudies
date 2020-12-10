package com.google.cloud.healthcare.fdamystudies.oauthscim.controller;

import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.ERROR_VIEW_NAME;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.oauthscim.common.ApiEndpoint;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class GenericErrorControllerTest extends BaseMockIT {

  @Test
  public void shouldReturnErrorPageWhenAuthorizationCodeIsEmpty() throws Exception {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("error", "invalid_request");
    queryParams.add("error_debug", "");
    queryParams.add("error_description", "The request is missing a required parameter");
    queryParams.add("error_description", "The 'redirect_uri' parameter does not match");

    mockMvc
        .perform(
            get(ApiEndpoint.ERROR.getPath()).contextPath(getContextPath()).queryParams(queryParams))
        .andExpect(status().is3xxRedirection())
        .andDo(print())
        .andExpect(redirectedUrl(ERROR_VIEW_NAME));
  }
}
