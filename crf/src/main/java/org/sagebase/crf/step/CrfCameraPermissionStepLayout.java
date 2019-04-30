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

package org.sagebase.crf.step;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;

import org.researchstack.backbone.ui.callbacks.ActivityCallback;
import org.researchstack.backbone.ui.step.layout.StepPermissionRequest;

public class CrfCameraPermissionStepLayout extends CrfInstructionStepLayout implements StepPermissionRequest {

    private ActivityCallback permissionCallback;

    public CrfCameraPermissionStepLayout(Context context) {
        super(context);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (getContext() instanceof ActivityCallback) {
            permissionCallback = (ActivityCallback) getContext();
        }
    }

    public void goForwardClicked(View v) {
        if (hasCameraPermission()) {
            super.goForwardClicked(v);
        } else {
            nextButton.setEnabled(false);
            permissionCallback.onRequestPermission(Manifest.permission.CAMERA);
        }
    }

    private boolean hasCameraPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }



    @Override
    public void onUpdateForPermissionResult() {
        //Re-enable the button so user can tap to go to the next step.
        //Automatically taking user to next step causes weird layout problems
        nextButton.setEnabled(true);
    }
}
