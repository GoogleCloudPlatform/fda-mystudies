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

package org.sagebase.crf;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;

import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.ActiveTaskActivity;

import org.sagebionetworks.research.crf.R;

/**
 * Created by TheMDP on 10/24/17.
 */

public class CrfActiveTaskActivity extends ActiveTaskActivity {

    public static Intent newIntent(Context context, Task task)
    {
        Intent intent = new Intent(context, CrfActiveTaskActivity.class);
        intent.putExtra(EXTRA_TASK, task);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean status = super.onCreateOptionsMenu(menu);
        setupToolbar(R.color.azure);
        return status;
    }

    @Override
    public void onDataAuth() {
        storageAccessUnregister();
        MainApplication.mockAuthenticate(this);
        super.onDataReady();
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_active_task;
    }

    public int getViewSwitcherRootId() {
        return R.id.crf_active_container;
    }

    private void setupToolbar(@ColorRes int tintColor) {
        int colorRes = ContextCompat.getColor(this, tintColor);
        Drawable drawable = toolbar.getNavigationIcon();
        if (drawable != null) {
            drawable.setColorFilter(colorRes, PorterDuff.Mode.SRC_ATOP);
        }
        for (int i = 0; i < toolbar.getMenu().size(); i++) {
            MenuItem menuItem = toolbar.getMenu().getItem(i);
            if (menuItem != null && menuItem.getIcon() != null) {
                menuItem.getIcon().setColorFilter(colorRes, PorterDuff.Mode.SRC_ATOP);
            }
        }
    }
}
