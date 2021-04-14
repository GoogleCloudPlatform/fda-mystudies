/*
 * Copyright 2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.util;

import com.google.cloud.healthcare.fdamystudies.config.AppPropertyConfig;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ParticipantManagerUtil {

  private XLogger logger = XLoggerFactory.getXLogger(ParticipantManagerUtil.class.getName());

  @Autowired private AppPropertyConfig appConfig;

  @Autowired private Storage storageService;

  public String getSignedUrl(String fileUrl, int signedUrlDurationInHours) {
    try {
      if (StringUtils.isEmpty(fileUrl)) {
        return null;
      }
      String filePath =
          StringUtils.substringAfter(fileUrl, appConfig.getStudyBuilderCloudBucketName() + "/");

      BlobInfo blobInfo =
          BlobInfo.newBuilder(appConfig.getStudyBuilderCloudBucketName(), filePath).build();
      return storageService.signUrl(blobInfo, signedUrlDurationInHours, TimeUnit.HOURS).toString();
    } catch (Exception e) {
      logger.error("Unable to generate signed url", e);
    }
    return null;
  }
}
