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

import com.google.common.base.Strings;

import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;

public class CrfAnswerResult<T> extends CrfResult {
    private static final String TYPE = "answer";

    @Nullable
    private final T answer;
    @Nullable
    private final String answerType;

    public CrfAnswerResult(@NonNull String identifier, @NonNull Date startTime,
                           @NonNull Date endTime, @Nullable T answer,
                           @NonNull String answerType) {
        super(identifier, TYPE, startTime, endTime);
        this.answer = answer;
        checkArgument(!Strings.isNullOrEmpty(answerType));
        this.answerType = answerType;
    }

    /**
     * @return the Answer associated with this result.
     */
    @Nullable
    public T getAnswer() {
        return answer;
    }

    /**
     * @return the type of the answer associated with this result.
     */
    @NonNull
    public String getAnswerType() {
        return answerType;
    }
}
