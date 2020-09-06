/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.WriteChannel;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CloudStorageService implements FileStorageService {

  @Autowired private Storage storageService;

  private static final String PATH_SEPARATOR = "/";

  @Autowired private ApplicationPropertyConfiguration appConfig;

  @Override
  public List<String> listFiles(String underDirectory, boolean recursive) {
    return null;
  }

  @Override
  public String saveFile(String fileName, String content, String underDirectory) {
    String absoluteFileName = null;
    if (!StringUtils.isBlank(content)) {
      absoluteFileName =
          underDirectory == null ? fileName : underDirectory + PATH_SEPARATOR + fileName;
      BlobInfo blobInfo = BlobInfo.newBuilder(appConfig.getBucketName(), absoluteFileName).build();
      byte[] bytes = null;

      try (WriteChannel writer = storageService.writer(blobInfo)) {
        bytes = Base64.getDecoder().decode(content.replaceAll("\n", ""));

        writer.write(ByteBuffer.wrap(bytes, 0, bytes.length));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return absoluteFileName;
  }

  @Override
  public String getDocumentContent(String filepath) {
    if (StringUtils.isNotBlank(filepath)) {
      Blob blob = storageService.get(BlobId.of(appConfig.getBucketName(), filepath));
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      blob.downloadTo(outputStream);
      return new String(Base64.getEncoder().encode(blob.getContent()));
    }

    return StringUtils.EMPTY;
  }
}
