package com.google.cloud.healthcare.fdamystudies.beans;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.EMAIL_LENGTH;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString
@Component
@Scope(value = "prototype")
public class SetUpAccountRequest {

  private static final String PASSWORD_REGEX =
      "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!\\\"#$%&'()*+,-.:;<=>?@\\[\\]^_`{|}~]).{8,64}$";

  @ToString.Exclude
  @NotBlank
  @Size(max = EMAIL_LENGTH)
  @Email
  private String email;

  @ToString.Exclude
  @NotBlank
  @Size(
      min = 8,
      max = 64,
      message =
          "Password must contain at least 8 characters, including uppercase, lowercase letters, numbers and allowed special characters.")
  @Pattern(regexp = PASSWORD_REGEX, message = "Your password does not meet the required criteria.")
  private String password;

  @ToString.Exclude
  @NotBlank
  @Size(max = 320)
  private String firstName;

  @ToString.Exclude
  @NotBlank
  @Size(max = 320)
  private String lastName;
}
