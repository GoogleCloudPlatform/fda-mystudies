package com.google.cloud.healthcare.fdamystudies.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.asJsonString;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.matching.ContainsPattern;
import com.google.cloud.healthcare.fdamystudies.beans.UserRegistrationForm;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.repository.UserDetailsBORepository;
import com.google.cloud.healthcare.fdamystudies.service.CommonService;
import com.google.cloud.healthcare.fdamystudies.service.FdaEaUserDetailsServiceImpl;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.UserDetailsBO;
import com.jayway.jsonpath.JsonPath;
import java.util.Optional;
import javax.mail.internet.MimeMessage;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MvcResult;

@TestMethodOrder(OrderAnnotation.class)
public class UserRegistrationControllerTest extends BaseMockIT {

  private static final String REGISTER_PATH = "/myStudiesUserMgmtWS/register";

  @Autowired private FdaEaUserDetailsServiceImpl userDetailsService;

  @Autowired private UserRegistrationController controller;

  @Autowired private CommonService service;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private JavaMailSender emailSender;

  @Autowired private UserDetailsBORepository userDetailsRepository;

  @Value("${register.url}")
  private String authRegisterUrl;

  @Test
  public void contextLoads() {
    assertNotNull(controller);
    assertNotNull(mockMvc);
    assertNotNull(service);
  }

  @Test
  public void healthCheck() throws Exception {
    mockMvc.perform(get("/healthCheck")).andDo(print()).andExpect(status().isOk());
  }

  @Test
  public void shouldReturnBadRequestForInvalidPassword() throws Exception {
    HttpHeaders headers =
        TestUtils.getCommonHeaders(Constants.APP_ID_HEADER, Constants.ORG_ID_HEADER);

    // invalid  password
    UserRegistrationForm userRegistrationForm = new UserRegistrationForm();
    mockMvc
        .perform(
            post(REGISTER_PATH)
                .content(asJsonString(userRegistrationForm))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldReturnBadRequestForEmailExists() throws Exception {
    HttpHeaders headers =
        TestUtils.getCommonHeaders(Constants.APP_ID_HEADER, Constants.ORG_ID_HEADER);

    // user exists
    String requestJson = getRegisterUser(Constants.EMAIL_ID, Constants.PASSWORD);
    mockMvc
        .perform(
            post(REGISTER_PATH).content(requestJson).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isConflict());
  }

  @Test
  public void shouldRegisterUser() throws Exception {
    HttpHeaders headers =
        TestUtils.getCommonHeaders(Constants.APP_ID_HEADER, Constants.ORG_ID_HEADER);

    String requestJson = getRegisterUser(Constants.EMAIL, Constants.PASSWORD);

    MvcResult result =
        mockMvc
            .perform(
                post(REGISTER_PATH)
                    .content(requestJson)
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").isNotEmpty())
            .andReturn();

    String userId = JsonPath.read(result.getResponse().getContentAsString(), "$.userId");
    // find userDetails by userId and assert email
    Optional<UserDetailsBO> optUserDetails =
        userDetailsRepository.findByUserDetailsId(Integer.valueOf(userId));

    assertEquals(Constants.EMAIL, optUserDetails.get().getEmail());

    verify(emailSender, atLeastOnce()).send(isA(MimeMessage.class));

    verify(
        1,
        postRequestedFor(urlEqualTo("/oauth-scim-service/users"))
            .withRequestBody(new ContainsPattern(Constants.PASSWORD)));
  }

  private String getRegisterUser(String emailId, String password) throws JsonProcessingException {
    UserRegistrationForm userRegistrationForm = new UserRegistrationForm(emailId, password);
    return getObjectMapper().writeValueAsString(userRegistrationForm);
  }

  protected ObjectMapper getObjectMapper() {
    return objectMapper;
  }
}
