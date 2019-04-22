/*
 *    Copyright 2019 Sage Bionetworks
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package org.sagebase.crf.result;

import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import org.threeten.bp.ZonedDateTime;

public class CrfCollectionResult extends CrfResult {
    private static final String TYPE = "collection";

    @NonNull
    private final ImmutableList<CrfResult> results;

    public CrfCollectionResult(@NonNull String identifier, @NonNull ZonedDateTime startTime,
                               @NonNull ZonedDateTime endTime,
                               @NonNull ImmutableList<CrfResult> results) {
        super(identifier, TYPE, startTime, endTime);
        this.results = results;
    }

    /**
     * @return A list of the results that make up this collection Result.
     */
    @NonNull
    public ImmutableList<CrfResult> getResults() {
        return results;
    }
}
