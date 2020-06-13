/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Base64;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.WriteChannel;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.exceptions.CloudStorageException;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Service
public class CloudStorageService implements FileStorageService {

  private Storage storageService;

  private static final String PATH_SEPARATOR = "/";

  private static final Logger logger = LoggerFactory.getLogger(CloudStorageService.class);

  @Autowired private ApplicationPropertyConfiguration appConfig;

  @PostConstruct
  private void init() {
    storageService = StorageOptions.getDefaultInstance().getService();
  }

  @Override
  public String saveFile(String fileName, String content, String underDirectory)
      throws CloudStorageException {
    String absoluteFileName = null;
    if (!StringUtils.isBlank(content)) {
      absoluteFileName =
          underDirectory == null ? fileName : underDirectory + PATH_SEPARATOR + fileName;
      BlobInfo blobInfo = BlobInfo.newBuilder(appConfig.getBucketName(), absoluteFileName).build();
      byte[] bytes = null;

      try (WriteChannel writer = storageService.writer(blobInfo)) {
        bytes = Base64.getDecoder().decode(content.replaceAll("\n", ""));

        writer.write(ByteBuffer.wrap(bytes, 0, bytes.length));
      } catch (Exception e) {
        logger.error(
            String.format(
                "Exception while saving file %s under directory %s into google cloud storage",
                fileName, underDirectory),
            e);
        throw new CloudStorageException(e);
      }
    }
    return absoluteFileName;
  }

  @Override
  public void downloadFileTo(String absoluteFileName, OutputStream outputStream)
      throws CloudStorageException {
    try {
      if (StringUtils.isNotBlank(absoluteFileName)) {
        Blob blob = storageService.get(BlobId.of(appConfig.getBucketName(), absoluteFileName));
        blob.downloadTo(outputStream);
      }
    } catch (Exception e) {
      logger.error(
          String.format(
              "Exception while downloading file %s from google cloud storage", absoluteFileName),
          e);
      throw new CloudStorageException(e);
    }
  }
}
