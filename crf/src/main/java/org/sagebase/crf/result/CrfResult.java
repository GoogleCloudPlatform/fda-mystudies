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

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Strings;

import org.threeten.bp.ZonedDateTime;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base class for all Cardiorespiratory Fitness module result types.
 */
public abstract class CrfResult {
    @NonNull
    private final String identifier;
    @NonNull
    private final String type;
    @NonNull
    private final ZonedDateTime startTime;
    @NonNull
    private final ZonedDateTime endTime;

    public CrfResult(@NonNull String identifier, @NonNull String type,
                     @NonNull ZonedDateTime startTime, @NonNull ZonedDateTime endTime) {
        checkArgument(!Strings.isNullOrEmpty(identifier));
        this.identifier = identifier;
        checkArgument(!Strings.isNullOrEmpty(type));
        this.type = type;
        this.startTime = checkNotNull(startTime);
        this.endTime = checkNotNull(endTime);
    }

    /**
     * @return identifier for this result
     */
    @NonNull
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @return String representing the result type of this result
     */
    @NonNull
    public String getType() {
        return type;
    }


    /**
     * @return time this result started
     */
    @NonNull
    public ZonedDateTime getStartTime() {
        return startTime;
    }

    /**
     * @return time this result ended
     */
    @NonNull
    public ZonedDateTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CrfResult crfResult = (CrfResult) o;
        return Objects.equal(identifier, crfResult.identifier) &&
                Objects.equal(type, crfResult.type) &&
                Objects.equal(startTime, crfResult.startTime) &&
                Objects.equal(endTime, crfResult.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier, type, startTime, endTime);
    }

    @Override
    public String toString() {
        return toStringHelper()
                .toString();
    }

    protected MoreObjects.ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
                .add("identifier", identifier)
                .add("type", type)
                .add("startTime", startTime)
                .add("endTime", endTime);
    }
}
