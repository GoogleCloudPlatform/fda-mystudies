/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.EmailRequest;
import com.google.cloud.healthcare.fdamystudies.beans.EmailResponse;
import java.util.Map;

public interface EmailService {

  public EmailResponse sendMimeMail(EmailRequest emailRequest);

  public EmailResponse sendMimeMailWithImages(
      EmailRequest emailRequest, Map<String, String> inlineImages);
}
