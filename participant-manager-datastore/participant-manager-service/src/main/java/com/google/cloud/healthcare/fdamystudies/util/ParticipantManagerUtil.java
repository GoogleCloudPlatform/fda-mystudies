/*
 * Copyright 2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.util;

import com.google.cloud.healthcare.fdamystudies.config.AppPropertyConfig;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ParticipantManagerUtil {

  private static final String PATH_SEPARATOR = "/";

  private static final String STUDYLOGO = "studylogo";

  private static final String STUDIES = "studies";

  private static final String DEFAULT_IMAGES_FOLDER_NAME = "defaultImages";

  private static final String DEFAULT_IMAGE = "STUDY_BI_GATEWAY.jpg";

  private XLogger logger = XLoggerFactory.getXLogger(ParticipantManagerUtil.class.getName());

  @Autowired private AppPropertyConfig appConfig;

  public String getImageResources(String fileUrl, String customStudyId) {

    try {
      if (StringUtils.isEmpty(fileUrl)) {
        return null;
      }
      String fileName = fileUrl.substring(fileUrl.lastIndexOf(PATH_SEPARATOR) + 1);
      String filePath;
      if (DEFAULT_IMAGE.equals(fileName)) {
        filePath = DEFAULT_IMAGES_FOLDER_NAME + PATH_SEPARATOR + fileName;
      } else {
        filePath =
            STUDIES
                + PATH_SEPARATOR
                + customStudyId
                + PATH_SEPARATOR
                + STUDYLOGO
                + PATH_SEPARATOR
                + fileName;
      }

      if (StringUtils.isNotBlank(filePath)) {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        Blob blob = storage.get(BlobId.of(appConfig.getStudyBuilderCloudBucketName(), filePath));
        if (blob != null) {
          ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
          blob.downloadTo(outputStream);
          return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(blob.getContent());
        }
      }
    } catch (Exception e) {
      logger.error("Unable to getImageResources", e);
    }
    return null;
  }
}
