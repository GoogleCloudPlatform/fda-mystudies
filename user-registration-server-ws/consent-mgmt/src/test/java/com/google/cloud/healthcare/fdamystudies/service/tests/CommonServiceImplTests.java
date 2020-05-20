package com.google.cloud.healthcare.fdamystudies.service.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import org.hibernate.HibernateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.service.CommonServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class CommonServiceImplTests {

  @Mock private RestTemplate restTemplate;

  @Mock private ApplicationPropertyConfiguration appConfig;

  @Mock private CommonDao commonDao;

  @InjectMocks private CommonServiceImpl commonServiceImpl;

  @Test
  public void testGetUserDetailsId() {
    String userId = "userId";
    Integer userDetailsId = 2;
    when(commonDao.getUserDetailsId(userId)).thenReturn(userDetailsId);
    Integer result = commonServiceImpl.getUserDetailsId(userId);
    assertEquals(userDetailsId, result);
  }

  @Test
  public void testGetUserDetailsIdExceptionCase() {
    String userId = "userId";
    Integer result = null;
    when(commonDao.getUserDetailsId(userId))
        .thenThrow(new HibernateException("Some hibernate exception"));
    try {
      result = commonServiceImpl.getUserDetailsId(userId);
    } catch (HibernateException expected) {
    }
    assertNull(result);
  }

  @Test
  public void testValidateAccessToken() {
    String userId = "userId";
    String accessToken = "accessToken";
    String accessTokenUrl = "http://example.com";
    String clientToken = "clientToken";
    when(appConfig.getAuthServerAccessTokenValidationUrl()).thenReturn(accessTokenUrl);
    when(restTemplate.exchange(eq(accessTokenUrl), eq(HttpMethod.POST), any(), eq(Integer.class)))
        .thenReturn(new ResponseEntity<Integer>(1, HttpStatus.OK));

    Integer result = commonServiceImpl.validateAccessToken(userId, accessToken, clientToken);
    assertEquals(Integer.valueOf(1), result);
  }
}
