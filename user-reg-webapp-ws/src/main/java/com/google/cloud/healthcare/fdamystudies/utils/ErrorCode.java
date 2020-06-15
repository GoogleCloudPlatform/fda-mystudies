/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.utils;

public enum ErrorCode {
  EC_21(21, "Profile Updated Successfully"),

  EC_22(22, "Logged out Successfully. "),

  EC_30(30, "Profile Updated successfully"),

  EC_31(31, "Your account is temporarily deactivated"),

  EC_33(33, "Data updated successfully"),

  EC_34(34, " Unable to update user profile"),

  EC_41(41, "There was an issue with the authentication data for the request."),

  EC_42(42, "Invalid access token/Your session Expired ."),

  EC_43(43, "Please enter all required fields"),

  EC_44(44, "Provided value is invalid"),

  EC_52(52, "Required  userId  was not submitted in request."),

  EC_53(53, "Required  email address was not submitted/incorrect format in request."),

  EC_54(54, "Required  user object  has not submitted"),

  EC_56(56, "Required requestId  was not submitted in request."),

  EC_61(61, "User does not exists."),

  EC_73(73, "Your Credentials is Invalid."),

  EC_86(86, "This method requires a GET/POST."),

  EC_91(91, "Invalid credentials"),

  EC_93(93, "User not Active"),

  EC_101(101, "Your Account/Credentials has expired."),

  EC_102(102, "Your Account has been locked."),

  EC_103(103, "Unable to send Email."),

  EC_104(104, "This Email id already exists."),

  EC_105(105, "Link Has Expired."),

  EC_106(106, "Failed to update user."),

  EC_107(107, "Registration Failed. Please check your access code."),

  EC_108(108, "Failed to update user status."),

  EC_109(109, "Failed to Reset user's password"),

  EC_110(110, "Entered email does not exist or not registered."),

  EC_111(111, "Failed to send Email."),

  EC_112(112, "Required New Password was not submitted in request."),

  EC_113(113, "Failed to Reset password. Please check your access code."),

  EC_114(114, "Your Account is disabled. Please contact admin"),

  EC_115(114, "Fail to send Reminder"),

  EC_200(200, "OK"),

  EC_201(201, "No Content"),

  EC_304(304, "Not Modified"),

  EC_400(400, "Bad Request"),

  EC_401(401, "You are not authorized to access this information"),

  EC_403(403, "You forbidden to access this information"),

  EC_404(404, "Information not found"),

  EC_406(406, "Information sent was not accepted"),

  EC_407(407, "Password CANNOT be any of the last 3 passwords used"),

  EC_408(408, "Invalid userId."),

  EC_409(409, "Invalid appId."),

  EC_500(500, "Internal Server Error"),

  EC_776(776, "Provided argument value is invalid"),

  EC_777(777, "Missing required argument"),

  EC_778(778, "Provided argument value is invalid"),

  EC_779(779, "Missing required argument"),

  EC_788(779, "Provided argument value is invalid"),

  EC_789(779, "Missing required argument"),

  EC_803(803, "Missing required argument"),

  EC_804(804, "Provided argument value is invalid"),

  EC_814(814, "Site not found"),

  EC_816(816, "Study not found."),

  EC_817(817, " App not found."),

  EC_812(812, "Missing required argument"),

  EC_813(813, "Provided argument value is invalid"),

  EC_835(835, "Provided argument value is invalid"),

  EC_836(836, "Site already decommissioned"),

  EC_881(881, "No Locations Found"),

  EC_882(882, "You do not have permission to view or add or update locations"),

  EC_883(883, "customId already exists"),

  EC_884(884, "custom id can't be updated"),

  EC_885(
      885,
      "This Location is being used as an active Site in one or"
          + " more studies and cannot be decomissioned"),

  EC_886(886, "Can't decommision an already decommissioned location"),

  EC_887(887, "Can't reactive an already active location"),

  EC_888(888, "Default site can't be modified"),

  EC_861(861, "User does not exist"),

  EC_911(911, "Missing required argument"),

  EC_912(912, "Provided argument value is invalid"),

  EC_920(920, "Missing required arguments"),

  EC_978(978, "Missing required arguments"),

  EC_979(979, "This email has already been used. Please try with different email address."),

  EC_987(987, "Missing required argument"),

  EC_988(988, "Provided argument value is invalid"),

  EC_967(967, "Email address exists for site"),

  EC_989(989, "Can not add participant to open study"),

  EC_963(963, "Error getting participants."),

  EC_956(956, "Missing required argument."),

  EC_957(957, "Provided argument value is invalid"),

  EC_862(862, "Participant already enrolled"),

  EC_863(863, "You do not have permission to manage site"),

  EC_864(864, "Email address exists for site"),

  EC_865(865, "Site doesn't exists or is inactive"),

  EC_866(866, "Import Document not in prescribed format"),

  EC_868(867, "Please upload a .xls or .xlsx file"),

  EC_913(913, "At least one email address already exists"),

  EC_914(
      914,
      "Note :{num} emails failed to import.\\n"
          + "Reason for failure of import emails may be due to "
          + "following reasons:\\n1.Email not in proper format "
          + "\\n2.Duplicate email exisits\\n3.Participant enabled in another site"
          + " with in same study\\n4.Email already exisit"),

  EC_915(915, "{num} email failed to import"),

  EC_964(964, "Error getting consent data."),

  EC_867(867, "Invalid emailId"),

  EC_880(868, "Security code has expired"),

  EC_869(869, "Invalid Security code"),

  EC_870(870, "You are not authorized to access this information"),

  EC_871(871, "Invalid password");

  private final int code;
  private final String errorMessage;

  private ErrorCode(int code, String errorMessage) {
    this.code = code;
    this.errorMessage = errorMessage;
  }

  public int code() {
    return this.code;
  }

  public String errorMessage() {
    return this.errorMessage;
  }

  public static ErrorCode fromCode(int code) {
    for (ErrorCode ec : ErrorCode.values()) {
      if (ec.code == code) {
        return ec;
      }
    }
    throw new IllegalArgumentException("No matching constant for [" + code + "]");
  }

  public static ErrorCode fromErrorMessage(String errorMessage) {
    for (ErrorCode ec : ErrorCode.values()) {
      if (ec.errorMessage.equals(errorMessage)) {
        return ec;
      }
    }
    throw new IllegalArgumentException("No matching constant for [" + errorMessage + "]");
  }
}
