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

package org.sagebase.crf.step;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.researchstack.backbone.ResourceManager;
import org.researchstack.backbone.result.FileResult;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.ViewWebDocumentActivity;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.sagebase.crf.CrfActivitiesFragment;
import org.sagebase.crf.CrfActivityResultListener;
import org.sagebionetworks.research.crf.BuildConfig;
import org.sagebionetworks.research.crf.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CrfPhotoCaptureStepLayout extends CrfInstructionStepLayout implements CrfActivityResultListener {

    private static final String LOG_TAG = CrfPhotoCaptureStepLayout.class.getCanonicalName();
    public static final int CRF_PHOTO_CAPTURE_REQUEST_CODE = 453;

    private CrfPhotoCaptureStep crfPhotoCaptureStep;
    private File mCapturedPhoto;

    public CrfPhotoCaptureStepLayout(Context context) {
        super(context);
    }

    public CrfPhotoCaptureStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CrfPhotoCaptureStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CrfPhotoCaptureStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        validateAndSetCrfPhotoCaptureStep(step);
        super.initialize(step, result);
    }

    protected void validateAndSetCrfPhotoCaptureStep(Step step) {
        if (!(step instanceof CrfPhotoCaptureStep)) {
            throw new IllegalStateException("CrfPhotoCaptureStepLayout only works with CrfPhotoCaptureStep");
        }
        this.crfPhotoCaptureStep = (CrfPhotoCaptureStep) step;
    }

//    @Override
//    public void connectStepUi(int titleRId, int textRId, int imageRId, int detailRId) {
//        super.connectStepUi(titleRId, textRId, imageRId, detailRId);
//    }
//
    @Override
    public void refreshStep() {
        super.refreshStep();

        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
    }

    @Override
    public void onActivityFinished(int requestCode, int resultCode, Intent data) {
        Log.d(LOG_TAG, "onActivityFinished()");

        StepResult result = new StepResult(step);

        FileResult fileResult = new FileResult(step.getIdentifier(), mCapturedPhoto, "photo");
        fileResult.setEndDate(result.getEndDate());
        fileResult.setStartDate(result.getStartDate());

        result.setResult(fileResult);

        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, result);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            Activity activity = (Activity)getContext();
            // Create the File where the photo should go
            try {
                mCapturedPhoto = createImageFile();
            } catch (IOException ex) {
                Log.e(LOG_TAG, "Error creating file: " + ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (mCapturedPhoto != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        BuildConfig.APPLICATION_ID + ".provider", mCapturedPhoto);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(takePictureIntent, CRF_PHOTO_CAPTURE_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpeg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }
}
