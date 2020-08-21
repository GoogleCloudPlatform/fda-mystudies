/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.testutils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.google.cloud.healthcare.fdamystudies.service.FileStorageService;
import com.google.cloud.storage.StorageException;
import java.io.OutputStream;

public class MockUtils {

  private MockUtils() {}

  public static void setCloudStorageSaveFileExpectations(
      final FileStorageService cloudStorageService) {
    when(cloudStorageService.saveFile(anyString(), anyString(), anyString()))
        .thenAnswer(
            (invocation) -> {
              String fileName = invocation.getArgument(0);
              String underDirectory = invocation.getArgument(2);
              return underDirectory + "/" + fileName;
            });
  }

  public static void setCloudStorageDownloadExpectations(
      final FileStorageService cloudStorageService, final String content) {
    doAnswer(
            (invocation) -> {
              OutputStream os = invocation.getArgument(1);
              // This is expected to return the actual decoded value
              os.write(content.getBytes());
              return null;
            })
        .when(cloudStorageService)
        .downloadFileTo(anyString(), any(OutputStream.class));
  }

  public static void setCloudStorageDownloadExceptionExpectations(
      final FileStorageService cloudStorageService) {
    doThrow(StorageException.class).when(cloudStorageService).downloadFileTo(anyString(), any());
  }
}
