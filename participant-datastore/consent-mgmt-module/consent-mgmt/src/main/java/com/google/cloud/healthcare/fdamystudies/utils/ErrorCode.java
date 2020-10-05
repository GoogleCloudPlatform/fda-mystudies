/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.utils;

public enum ErrorCode {
  EC_10(10, "Unable to send Notifications."),

  EC_11(11, "Pickup Time Negotiation offer action failed."),

  EC_20(20, "Push notification Settings Updated Successfully."),

  EC_21(21, "Profile Updated Successfully."),

  EC_22(22, "Logged out Successfully."),

  EC_23(23, "There is no push notification settings available."),

  EC_30(30, "Profile Updated successfully"),

  EC_31(31, "Request status updated successfully"),

  EC_32(32, "Leave created successfully"),

  EC_33(33, "Leave cancelled successfully"),

  EC_34(34, " Unable to update user profile"),

  EC_35(35, "Unable to update request status"),

  EC_36(36, "There is a problem in creating leaves"),

  EC_37(37, "Failed to update leave"),

  EC_38(38, "Leave updated successfully"),

  EC_39(39, "Failed to updated driver location"),

  EC_40(40, "Driver Location Updated successfully"),

  EC_41(41, "There was an issue with the provided data for the request."),

  EC_61(61, "User does not exists."),

  EC_62(62, "Unable to update device details."),

  EC_63(63, "Unable to update the Request."),

  EC_64(64, "Failed to change password"),

  EC_65(65, "Device details updated successfully"),

  EC_71(71, "Your username and/or password are incorrect. Please try again."),

  EC_72(72, "Invalid Credentials."),

  EC_73(73, "Checked Out successfully"),

  EC_74(74, "You are not Check In yet, Please Check In"),

  EC_75(75, "Failed to save User activity"),

  EC_76(76, "You are already Checked In, You have to Check Out to Check In again"),

  EC_77(77, "Checked In successfully"),

  EC_78(78, "You are already Checked Out, You have to Check In to Check Out again"),

  EC_79(79, "You have an Active Request, You are not allowed to Check Out"),

  EC_90(90, "No leave request found"),

  EC_91(91, "No active request found"),

  EC_92(92, "Invalid credentials / User not Active"),

  EC_93(93, "User not Active"),

  EC_94(94, "Request does not exist"),

  EC_95(95, "No upcoming Requests"),

  EC_96(96, "Failed to update User activity"),

  EC_97(97, "Request already cancelled"),

  EC_98(94, "Request already completed"),

  EC_99(99, "You have already updated this request status"),

  EC_101(101, "Your Account/Credentials has expired."),

  EC_102(102, "Your Account has been locked."),

  EC_103(103, "Unable to send Email."),

  EC_104(104, "There are no Notifications."),

  EC_105(105, "There is no request detail found."),

  EC_106(106, "Leave start date and time must have to come before Leave end date and time"),

  EC_107(107, "Leave start date and time must have to come after current date and time"),

  EC_108(108, "Leave request already exists"),

  EC_109(109, "Invalid Inputs"),

  EC_110(110, "Eligibility consent has been updated successfully"),

  EC_111(96, "Failed to update Eligibility consent"),

  EC_200(200, "OK"),

  EC_201(201, "No Content."),

  EC_304(304, "Not Modified."),

  EC_400(400, "Bad Request."),

  EC_401(401, "You are not authorized to access this information"),

  EC_403(403, "You are forbidden to access this information."),

  EC_404(404, "Information not found."),

  EC_406(406, "Information which was sent, was not accepted."),

  EC_500(500, "Internal Server Error."),

  EC_1001(1001, "Your Session has been expired."),

  EC_1003(1003, "Invalid refresh token."),

  EC_1004(1004, "User Not Active/Account locked/Credential expired/Account expired"),

  EC_1005(1005, "Invalid AccessToken or AccessToken changed");

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
}
