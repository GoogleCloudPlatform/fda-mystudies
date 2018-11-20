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

package org.sagebase.crf.step.active;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.RenderScript;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;

import org.sagebase.crf.camera.CameraSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.sagebase.crf.step.active.ImageUtils.toBitmap;

/**
 * Created by TheMDP on 10/17/17.
 */

public class HeartBeatDetector extends Detector<HeartBeatSample> {
    private static final Logger LOG = LoggerFactory.getLogger(HeartBeatDetector.class);
    
    public final Context context;
    private final RenderScript rs;

    public CameraSource cameraSource;

    private int frameCounter = 0;
    private long fpsStartTime = -1;

    
    private static long startTime = -1;
    private HeartBeatUtil heartBeatUtil = new HeartBeatUtil();


    public HeartBeatDetector(Context applicationContext) {
        this.context = applicationContext;
        rs = RenderScript.create(context);
    }
    @Override
    public SparseArray<HeartBeatSample> detect(Frame frame) {

        long now = frame.getMetadata().getTimestampMillis();
        if (startTime < 0) {
            startTime = now;
        }
        if (fpsStartTime < 0) {
            fpsStartTime = now;
        }
        frameCounter++;
        if (frameCounter % 100 == 0) {
            LOG.debug("FPS: {}", 1000f / ((System.currentTimeMillis() - fpsStartTime) / frameCounter));
            frameCounter = 0;
            fpsStartTime = -1;
        }

        if (cameraSource == null || cameraSource.getPreviewSize() == null) {
            LOG.error("Camera source or preview size is null");
            return new SparseArray<>();
        }

        //long startTime = System.currentTimeMillis();
        int w = cameraSource.getPreviewSize().getWidth();
        int h = cameraSource.getPreviewSize().getHeight();

        Bitmap bitmap = toBitmap(rs, frame, w, h);

        // Takes about 10 ms on Samsung Galaxy S6
        //Log.d("Timing", "rgb " + (System.currentTimeMillis() - startTime));
        //startTime = System.currentTimeMillis();

        HeartBeatSample sample = heartBeatUtil.getHeartBeatSample(now, bitmap);

        //Log.d("RGB", "" + sample.r + ", " + sample.g + ", " + sample.b);
        SparseArray<HeartBeatSample> samples = new SparseArray<>();
        samples.append(0, sample);

        // Takes about 4 ms on Samsung Galaxy S6
        //Log.d("Timing", "avg rgb " + (System.currentTimeMillis() - startTime));
        //startTime = System.currentTimeMillis();

        //Log.d("Timing", "Hue " + (System.currentTimeMillis() - startTime));
        //Log.d("Hue", "" + sample.h);
        
        return samples;
    }

    
}
