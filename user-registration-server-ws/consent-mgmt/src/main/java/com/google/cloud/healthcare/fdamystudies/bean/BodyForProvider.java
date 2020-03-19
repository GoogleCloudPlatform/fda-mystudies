/** */
package com.google.cloud.healthcare.fdamystudies.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Project Name: UserManagementServiceBundle
 *
 * @author Chiranjibi Dash
 */
@ToString
public class BodyForProvider {

  private String userId;
  private String accessToken;
public String getUserId() {
	return userId;
}
public void setUserId(String userId) {
	this.userId = userId;
}
public String getAccessToken() {
	return accessToken;
}
public void setAccessToken(String accessToken) {
	this.accessToken = accessToken;
}
}
