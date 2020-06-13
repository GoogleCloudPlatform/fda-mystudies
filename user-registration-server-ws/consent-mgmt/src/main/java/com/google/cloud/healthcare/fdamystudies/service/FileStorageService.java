/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import java.io.OutputStream;
import com.google.cloud.healthcare.fdamystudies.exceptions.CloudStorageException;

public interface FileStorageService {

  String saveFile(String fileName, String content, String underDirectory)
      throws CloudStorageException;

  void downloadFileTo(String fileName, OutputStream outputStream) throws CloudStorageException;
}
