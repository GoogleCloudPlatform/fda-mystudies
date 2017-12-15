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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;

import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.ui.MainActivity;
import org.sagebase.crf.fitbit.FitbitManager;
import org.sagebase.crf.view.CrfFilterableActivityDisplay;
import org.sagebionetworks.research.crf.R;

/**
 * Created by TheMDP on 10/23/17.
 */

public class CrfMainActivity extends MainActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.toolbar).setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!DataProvider.getInstance().isSignedIn(this)) {
            startActivity(new Intent(this, CrfOverviewActivity.class));
            finish();
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
        if(fragment instanceof  CrfFilterableActivityDisplay) {
            CrfFilterableActivityDisplay filterable = ((CrfFilterableActivityDisplay)fragment);
            if(filterable.isFiltered()) {
                filterable.clearFilter();
                return true;
            }
        }

        return false;
    }
}
