/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.repository;

import com.google.cloud.healthcare.fdamystudies.model.AppInfoDetailsBO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppInfoDetailsRepository extends JpaRepository<AppInfoDetailsBO, Integer> {}
