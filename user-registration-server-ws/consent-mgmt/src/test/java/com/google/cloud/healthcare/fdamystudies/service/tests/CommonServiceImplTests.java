package com.google.cloud.healthcare.fdamystudies.service.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.hibernate.HibernateException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.service.CommonServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class CommonServiceImplTests {

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private ApplicationPropertyConfiguration appConfig;

	@Mock
	private CommonDao commonDao;

	@InjectMocks
	private CommonServiceImpl commonServiceImpl;

	@Test
	public void testGetUserDetailsId() {
		String userId = "kJSdYD2e";
		Integer userDetailsId = 2;
		Mockito.when(commonDao.getUserDetailsId(userId)).thenReturn(userDetailsId);
		Integer result = commonServiceImpl.getUserDetailsId(userId);
		assertEquals(userDetailsId, result);
	}

	@Test
	public void testGetUserDetailsIdExceptionCase() {
		String userId = "kJSdYD2e";
		Integer result=null;
		Mockito.when(commonDao.getUserDetailsId(userId)).thenThrow(new HibernateException("Some hibernate exception"));
		try{
			 result = commonServiceImpl.getUserDetailsId(userId);
		}catch (HibernateException expected) {
		}
		assertNull(result);
	}

	@Test
	@Ignore
	public void testValidateAccessToken() {
		String userId = "kJSdYD2e";
		String accessToken = "jsdYUdbsKUDY&jshdDsknsdhjsds";
		String accessTokenUrl="http://someurl-not-to-be-hit.com";
		String clientToken = "skdj7dsjhdhYTTD65TDjksbdbKSDHSDJAsjhdsjdsd";
		Mockito.when(appConfig.getAuthServerAccessTokenValidationUrl()).thenReturn(accessTokenUrl);
		Mockito.when(restTemplate.exchange(ArgumentMatchers.eq(accessTokenUrl),
				ArgumentMatchers.eq(HttpMethod.POST), ArgumentMatchers.any(),
				ArgumentMatchers.eq(String.class))).thenReturn(new ResponseEntity<String>("1", HttpStatus.OK));

		Integer result = commonServiceImpl.validateAccessToken(userId, accessToken, clientToken);
		assertEquals(Integer.valueOf(1), result);
	}
}
