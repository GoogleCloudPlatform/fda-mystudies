/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.exceptions;

import javax.persistence.RollbackException;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface TransactionRollback {

  Logger logger = LoggerFactory.getLogger(TransactionRollback.class);

  static void rollback(Transaction transaction) {
    if (transaction != null) {
      try {
        transaction.rollback();
      } catch (RollbackException e) {
        logger.error("rollback failed", e);
      }
    }
  }
}
