/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;

public interface AuditLogDAO {

  public String updateDraftToEditedStatus(
      Session session, Transaction transaction, String userId, String actionType, String studyId);
}
