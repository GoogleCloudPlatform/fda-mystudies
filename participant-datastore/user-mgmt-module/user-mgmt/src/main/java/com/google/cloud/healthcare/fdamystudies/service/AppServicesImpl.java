package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.AppContactEmailsResponse;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.repository.AppRepository;
import java.util.Optional;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppServicesImpl implements AppServices {
  private XLogger logger = XLoggerFactory.getXLogger(AppServicesImpl.class.getName());

  @Autowired private AppRepository appRepository;

  @Override
  public AppContactEmailsResponse getAppContactEmails(String customAppId) {
    logger.entry("getAppContactEmails(customAppId)");
    AppContactEmailsResponse appResponse = null;
    Optional<AppEntity> optApp = appRepository.findByAppId(customAppId);
    if (!optApp.isPresent()) {
      throw new ErrorCodeException(ErrorCode.APP_NOT_FOUND);
    } else {
      AppEntity app = optApp.get();
      appResponse =
          new AppContactEmailsResponse(
              MessageCode.GET_APP_SUCCESS, app.getContactUsToEmail(), app.getFormEmailId());
    }

    logger.exit(String.format("customAppId=%s contact details fetched successfully", customAppId));
    return appResponse;
  }
}
