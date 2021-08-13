package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.AppContactEmailsResponse;

public interface AppServices {
  AppContactEmailsResponse getAppContactEmails(String customAppId);
}
