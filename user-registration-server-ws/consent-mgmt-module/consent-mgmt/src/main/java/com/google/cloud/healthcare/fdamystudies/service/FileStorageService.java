/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import java.util.List;

public interface FileStorageService {

  List<String> listFiles(String underDirectory, boolean recursive);

  String saveFile(String fileName, String content, String underDirectory);

  String getDocumentContent(String filepath);
}
