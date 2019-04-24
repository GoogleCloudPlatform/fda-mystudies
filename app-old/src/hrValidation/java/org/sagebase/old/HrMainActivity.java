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

package org.sagebase.old;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.factory.IntentFactory;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.MainActivity;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.backbone.utils.LogExt;
import org.sagebase.crf.CrfActiveTaskActivity;
import org.sagebase.old.view.CrfFilterableActivityDisplay;
import org.sagebase.crf.researchstack.CrfResourceManager;
import org.sagebase.crf.researchstack.CrfTaskFactory;
import org.sagebionetworks.research.crf.R;

import static org.researchstack.backbone.ui.fragment.ActivitiesFragment.REQUEST_TASK;

public class HrMainActivity extends MainActivity {

    private static final String LOG_TAG = HrMainActivity.class.getCanonicalName();

    private CrfTaskFactory taskFactory = new CrfTaskFactory();
    private IntentFactory intentFactory = IntentFactory.INSTANCE;

    /** Intent factory, made available for subclasses to create Intent instances. */
    public final IntentFactory getIntentFactory() {
        return intentFactory;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.toolbar).setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG, "HrMainActivity onResume");
        super.onResume();
        if (!DataProvider.getInstance().isSignedIn(this)) {
            startActivity(new Intent(this, HrOverviewActivity.class));
            finish();
        } else {
            startHrValidation();
        }
    }

    private void startHrValidation() {
        // Modified to test out the training task
        //Task activeTask = taskFactory.createTask(this, CrfResourceManager.HEART_RATE_VALIDATION_TEST_RESOURCE);
        Task activeTask = taskFactory.createTask(this, CrfResourceManager.HEART_RATE_TRAINING_TEST_RESOURCE);
        Intent intent = getIntentFactory().newTaskIntent(this, CrfActiveTaskActivity.class, activeTask);
        startActivityForResult(intent, REQUEST_TASK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_TASK) {
            LogExt.d(LOG_TAG, "Received task result from task activity");
            TaskResult taskResult = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);
            StorageAccess.getInstance().getAppDatabase().saveTaskResult(taskResult);
            DataProvider.getInstance().uploadTaskResult(this, taskResult);
        } else {
            LogExt.d(LOG_TAG, "Received task result cancelled");
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDataAuth() {
        storageAccessUnregister();
        MainApplication.mockAuthenticate(this);
        super.onDataReady();
    }

    @Override
    public void onBackPressed() {
        if(!clearFilter()) {
            // Finishes the app no matter what (fixes bug where fitbit chrome tab is open in the stack)
            Intent intent = new Intent(getApplicationContext(), CrfExitActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            if(!clearFilter()) {
                super.onOptionsItemSelected(item);
            } else {
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean clearFilter() {
        Fragment fragment = pagerAdapter.getRegisteredFragment(0);
        if(fragment instanceof CrfFilterableActivityDisplay) {
            CrfFilterableActivityDisplay filterable = ((CrfFilterableActivityDisplay)fragment);
            if(filterable.isFiltered()) {
                filterable.clearFilter();
                return true;
            }
        }

        return false;
    }
}
