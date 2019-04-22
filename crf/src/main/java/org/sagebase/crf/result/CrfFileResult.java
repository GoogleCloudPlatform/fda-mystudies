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
import android.support.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import org.threeten.bp.ZonedDateTime;

public class CrfFileResult extends CrfResult {
private static final String TYPE = "file";
    @Nullable
    private final String fileType;
    @Nullable
    private final String relativePath;

    public CrfFileResult(@NonNull String identifier, @NonNull ZonedDateTime startTime,
                         @NonNull ZonedDateTime endTime, @Nullable String fileType,
                         @Nullable String relativePath) {
        super(identifier, TYPE, startTime, endTime);
        this.fileType = fileType;
        this.relativePath = relativePath;
    }

    /**
     * @return A String representing the type of content in the file.
     */
    @Nullable
    public String getFileType() {
        return fileType;
    }

    /**
     * @return The relative path of the file as a String.
     */
    @Nullable
    public String getRelativePath() {
        return relativePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) {
            return false;
        }
        CrfFileResult that = (CrfFileResult) o;
        return Objects.equal(fileType, that.fileType) &&
                Objects.equal(relativePath, that.relativePath);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), fileType, relativePath);
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("fileType", fileType)
                .add("relativePath", relativePath);
    }
}