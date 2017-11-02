/*
 *    Copyright 2017 Sage Bionetworks
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

package org.sagebase.crf.view;

import android.support.annotation.DrawableRes;

/**
 * Created by TheMDP on 10/31/17.
 */

public interface CrfTaskToolbarIconManipulator {

    int NO_ICON = -1;

    /**
     * @return the icon that will appear on the left, defaults to back button
     */
    @DrawableRes
    int crfToolbarLeftIcon();

    /**
     * @return the icon that will appear on the right, defaults to clear ("X") button
     */
    @DrawableRes int crfToolbarRightIcon();
}
