/*
 *    Copyright 2018 Sage Bionetworks
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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v8.renderscript.RenderScript;
import android.util.Range;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.FrameLayout;

import org.joda.time.DateTime;
import org.researchstack.backbone.result.FileResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.recorder.Recorder;
import org.researchstack.backbone.step.active.recorder.RecorderListener;
import org.sagebase.crf.step.CrfHeartRateStepLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Single;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import static android.graphics.ImageFormat.YUV_420_888;
import static org.sagebase.crf.step.active.HeartBeatUtil.getHeartBeatSample;
import static org.sagebase.crf.step.active.ImageUtils.toBitmap;

/**
 * Created by liujoshua on 2/19/2018.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class HeartRateCamera2Recorder extends Recorder {
    private static final Logger LOG = LoggerFactory.getLogger(HeartRateCamera2Recorder.class);
    
    public static final String MP4_CONTENT_TYPE = "video/mp4";
    private final CompositeSubscription subscriptions;
    private final List<Surface> allSurfaces = new ArrayList(3);
    private final List<Surface> surfacesNoMediaRecorder = new ArrayList<>(2);
    private final BpmRecorder.HeartBeatJsonWriter heartBeatJsonWriter;
    private final RenderScript renderScript;
    
    
    // displays preview
    private TextureView textureView;
    // image processing
    private ImageReader imageReader;
    
    DateTime startTime;
    
    private Size mVideoSize;
    private MediaRecorder mediaRecorder;
    private final File mediaRecorderFile;
    private CameraCaptureSession cameraCaptureSession;
    
    
    public HeartRateCamera2Recorder(String identifier, Step step, File outputDirectory,
                                    CrfHeartRateStepLayout stepLayout) {
        super(identifier + "Video", step, outputDirectory);
        textureView = stepLayout.getCameraPreview();
    
        Context context = textureView.getContext();
    
        mediaRecorderFile = new File(getOutputDirectory(), uniqueFilename + ".mp4");
        subscriptions = new CompositeSubscription();
        heartBeatJsonWriter = new BpmRecorder.HeartBeatJsonWriter(stepLayout, stepLayout,
                identifier + "Json", step,
                outputDirectory );
        heartBeatJsonWriter.setRecorderListener(stepLayout);
        renderScript = RenderScript.create(context);
    }

    @Override
    public void start(Context context) {
        startTime = DateTime.now();
        heartBeatJsonWriter.start(context);
        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        if (manager == null) {
            LOG.warn("This device doesn't support Camera2 API");
            recordingFailed(new IllegalStateException("This device doesn't support Camera2 API"));
            return;
        }
        String cameraId = selectCamera(manager, context);
    
        Observable<CameraCaptureSession> cameraCaptureSessionObservable =
                openCameraObservable(manager, cameraId)
                        .doOnUnsubscribe(()->LOG.debug("Camera Capture unsubscribed"))
                        .doOnNext(cameraDevice -> {
                            setRecording(true);
                        })
                        .flatMap(cameraDevice -> {
                            // video recording surface
                            mediaRecorder =
                                    createMediaRecorder(mVideoSize, mediaRecorderFile);
                            try {
                                mediaRecorder.prepare();
                            } catch (IOException e) {
                                return Observable.error(e);
                            }
                        
                            allSurfaces.add(mediaRecorder.getSurface());
                        
                            // preview surface
                            textureView.setLayoutParams(new FrameLayout.LayoutParams(
                                    mVideoSize.getWidth(),
                                    mVideoSize.getHeight()
                            ));
                        
                            Surface previewSurface = new Surface(textureView.getSurfaceTexture());
                        
                            allSurfaces.add(previewSurface);
                            surfacesNoMediaRecorder.add(previewSurface);
                        
                            // heart rate processing surface
                            imageReader = ImageReader.newInstance(mVideoSize.getWidth(), mVideoSize
                                            .getHeight(),
                                    YUV_420_888, 1
                            );
                            allSurfaces.add(imageReader.getSurface());
                            surfacesNoMediaRecorder.add(imageReader.getSurface());
                        
                            return createCaptureSessionObservable(cameraDevice, allSurfaces)
                                    .doOnNext(session -> mediaRecorder.start())
                                    .doOnNext(session ->
                                            subscriptions.add(createImageReaderObservable(imageReader)
                                                    .doOnUnsubscribe(() -> LOG.debug("ImageReader unsubscribed"))
                                                    .map(imageReader1 -> {
                                                        Image image = imageReader1.acquireNextImage();
                                                        Bitmap bitmap =
                                                                toBitmap(renderScript, image, mVideoSize.getWidth(),
                                                                        mVideoSize.getHeight());
                                                        HeartBeatSample sample =
                                                                getHeartBeatSample(image.getTimestamp(), bitmap);
                                                        LOG.trace("Got heart beat sample: {}", sample);
                                                        image.close();
                                                        return sample;
                                                    }).observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(heartBeatJsonWriter::onHeartRateSampleDetected)))
                                    .doOnUnsubscribe(() -> LOG.debug("Capture session 0 unsubscribed"))
                                    .doOnUnsubscribe(() -> {
                                        try {
                                            mediaRecorder.stop();
                                        } catch (Exception e) {
                                            // this usually seems to happen due to order of unsubscribes
                                            LOG.debug("Couldn't stop MediaRecorder", e);
                                        }})
                                    .doOnUnsubscribe(mediaRecorder::release);
                        });
    
        subscriptions.add(cameraCaptureSessionObservable
                .subscribe(
                        s -> {
                            cameraCaptureSession = s;
                            doRepeatingRequest(s, surfacesNoMediaRecorder);
                        }, t -> {
                            cameraCaptureSession = null;
                            recordingFailed(t);
                        },
                        () -> LOG.debug("CaptureSession completed")));
    }
    
    public void startVideoRecording() {
        if (cameraCaptureSession == null) {
            LOG.warn("Could not start video recording, cameraCaptureSession is null");
            return;
        }
        LOG.warn("Started video recording");
        doRepeatingRequest(cameraCaptureSession, allSurfaces);
    }
    
    @Override
    public void setRecorderListener(RecorderListener listener) {
        super.setRecorderListener(listener);
        heartBeatJsonWriter.setRecorderListener(listener);
    }
    
    @Override
    public void stop() {
        subscriptions.unsubscribe();
        heartBeatJsonWriter.stop();
        if (mediaRecorderFile.exists()) {
            FileResult fileResult = new FileResult(fileResultIdentifier(), mediaRecorderFile, MP4_CONTENT_TYPE);
            fileResult.setStartDate(startTime.toDate());
            fileResult.setEndDate(new Date());
            getRecorderListener().onComplete(this, fileResult);
        }
    }

    @Override
    public void cancel() {
        stop();
        mediaRecorderFile.delete();
    }

    @SuppressLint("MissingPermission")
    @Nullable
    private String selectCamera(CameraManager manager, Context context) {
        Single.just(manager)
        .flatMapObservable(m -> {
            try {
                return Observable.just(m.getCameraIdList());
            } catch (CameraAccessException e) {
                return Observable.error(e);
            }
        });
        try {
            for (String cameraId : manager.getCameraIdList()) {

                CameraCharacteristics cameraCharacteristics
                        = manager.getCameraCharacteristics(cameraId);

                if (!cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
                    continue;
                }

                if (CameraCharacteristics.LENS_FACING_BACK !=
                        cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)) {
                    continue;
                }

                Range<Long> exposureTimeRange = cameraCharacteristics
                        .get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);
                LOG.debug("Exposure time range: {}", exposureTimeRange);

                Range<Integer> sensitivityTimeRange = cameraCharacteristics
                        .get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
                LOG.debug("Sensitivity time range: {}", sensitivityTimeRange);

                StreamConfigurationMap streamConfigurationMap = cameraCharacteristics
                        .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                // resolution
                mVideoSize = chooseOptimalSize(streamConfigurationMap.getOutputSizes(MediaRecorder.class), 192,
                        144);

                // calculate frame rate
                long durationRecorderNs = streamConfigurationMap.getOutputMinFrameDuration(MediaRecorder.class,
                        mVideoSize);
                LOG.debug("Min output frame duration for MediaRecorder: {}", durationRecorderNs);

                long durationPreviewNs = streamConfigurationMap.getOutputMinFrameDuration(SurfaceTexture.class,
                        mVideoSize);
                LOG.debug("Min output frame duration for SurfaceTexture: {}", durationPreviewNs);
                int[] outputFormats = streamConfigurationMap.getOutputFormats();
                LOG.debug("Output formats: {}", outputFormats);

                int[] inputFormats = new int[0];
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    inputFormats = streamConfigurationMap.getInputFormats();
                }
                LOG.debug("Input formats: {}", inputFormats);

                return cameraId;
            }
        } catch (CameraAccessException e) {
            LOG.warn("Failed to access camera", e);
            recordingFailed(e);
        }
        return null;
    }

    Observable<CameraDevice> openCameraObservable(CameraManager manager, String cameraId) {

        final CameraDevice[] cameraDevice = new CameraDevice[1];

        return Observable.unsafeCreate(new Observable.OnSubscribe<CameraDevice>() {
            @SuppressLint("MissingPermission")
            @Override
            public void call(Subscriber<? super CameraDevice> subscriber) {
                try {
                    manager.openCamera(cameraId, new CameraDevice.StateCallback() {
                        @Override
                        public void onOpened(@NonNull CameraDevice camera) {
                            LOG.debug("CameraDevice opened");
                            subscriber.onNext(camera);
                            cameraDevice[0] = camera;
                        }

                        public void onClosed(@NonNull CameraDevice camera) {
                            LOG.debug("CameraDevice closed");
                            subscriber.onCompleted();
                        }

                        @Override
                        public void onDisconnected(@NonNull CameraDevice camera) {
                            LOG.debug("CameraDevice disconnected");
                            subscriber.onCompleted();
                        }

                        @Override
                        public void onError(@NonNull CameraDevice camera, int error) {
                            LOG.debug("CameraDevice errored");
                            subscriber.onError(new IllegalStateException());
                        }
                    }, null);
                } catch (CameraAccessException e) {
                    subscriber.onError(e);
                }
            }
        }).cache()
                .doOnUnsubscribe(() -> {
                    if (cameraDevice[0] != null) {
                        LOG.debug("Closing CameraDevice");
                        cameraDevice[0].close();
                    }
                });
    }

    @NonNull
    public static Observable<ImageReader> createImageReaderObservable(final ImageReader imageReader) {
        HandlerThread handlerThread = new HandlerThread("ImageReader thread");
        if (!handlerThread.isAlive()) {
            handlerThread.start();
        }
        return Observable.create((Observable.OnSubscribe<ImageReader>) subscriber -> {
            imageReader.setOnImageAvailableListener(new OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    subscriber.onNext(reader);
                }
            },null);
        }).subscribeOn(AndroidSchedulers.from(handlerThread.getLooper()));
    }

    @NonNull
    public static Observable<CameraCaptureSession>
    createCaptureSessionObservable(
            @NonNull CameraDevice cameraDevice,
            @NonNull List<Surface> surfaceList) {
        final CameraCaptureSession[] captureSession = {null};
        return Observable.<CameraCaptureSession>create
                (new Observable.OnSubscribe<CameraCaptureSession>() {
                    @Override
                    public void call(Subscriber<? super CameraCaptureSession> observableEmitter) {
                        try {
                            cameraDevice.createCaptureSession(surfaceList, new CameraCaptureSession
                                    .StateCallback() {

                                @Override
                                public void onConfigured(@NonNull CameraCaptureSession session) {
                                    LOG.debug("CameraCaptureSession configured");
                                    captureSession[0] = session;
                                    observableEmitter.onNext(session);
                                }

                                @Override
                                public void onConfigureFailed(@NonNull CameraCaptureSession
                                                                      session) {
                                    LOG.debug("Camera session configuration failed");
                                    observableEmitter.onError(new IllegalStateException(
                                            "CameraCaptureSession configuration failed"));
                                }

                                @Override
                                public void onClosed(@NonNull CameraCaptureSession session) {
                                    LOG.debug("CameraCaptureSession closed");
                                    observableEmitter.onCompleted();
                                }

                            }, null);
                        } catch (CameraAccessException e) {
                            observableEmitter.onError(e);
                        }
                    }
                }).cache()
                .doOnUnsubscribe(() -> {
                    if (captureSession[0] != null) {
                        LOG.debug("Closing CameraCaptureSession");
                        captureSession[0].close();
                    }
                });
    }
    
    void doRepeatingRequest(@NonNull CameraCaptureSession session, @NonNull List<Surface> surfaces) {
        try {
            LOG.debug("Attempting to create capture request");
            CaptureRequest.Builder requestBuilder = session.getDevice()
                    .createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            for (Surface s : surfaces) {
                requestBuilder.addTarget(s);
            }
            requestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_OFF);
            requestBuilder.set(CaptureRequest.SENSOR_FRAME_DURATION, 16666666L);

            // no auto-exposure
            requestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata
                    .CONTROL_AE_MODE_OFF);
            requestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, 1000000L);
            requestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, 400);

            // infinite focus distance
            requestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata
                    .CONTROL_AF_MODE_OFF);
            requestBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, Float.MAX_VALUE);

            requestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);

            CaptureRequest captureRequest = requestBuilder.build();
            for (CaptureRequest.Key<?> k : captureRequest.getKeys()) {
                Object value = captureRequest.get(k);
                LOG.trace("Capture request Key: {}, value: {}", k, value);
            }
            session.setRepeatingRequest(captureRequest, null, null);
        } catch (CameraAccessException e) {
            LOG.warn("Failed to set capture request", e);
            recordingFailed(e);
        }
    }

    private void recordingFailed(Throwable throwable) {
        LOG.warn("Recording failed: ", throwable);
        cancel();
        if (getRecorderListener() != null) {
            getRecorderListener().onFail(this, throwable);
        }
    }

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, chooses the smallest one whose
     * width and height are at least as large as the respective requested values.
     *
     * @param choices Size options
     * @param width   The minimum desired width
     * @param height  The minimum desired height
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    private static Size chooseOptimalSize(Size[] choices, int width, int height) {

        List<Size> isBigEnough = new ArrayList<>();

        for (Size option : choices) {
            if (option.getWidth() >= width && option.getHeight() >= height) {
                isBigEnough.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (isBigEnough.size() > 0) {
            return Collections.min(isBigEnough, new CompareSizesByArea());
        } else {
            LOG.warn("Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight()
                    - (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    private MediaRecorder createMediaRecorder(Size videoSize, File file) {
        // TODO: uncomment
//the lowest available resolution for the first back camera
        try {
//        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
            MediaRecorder mediaRecorder = new MediaRecorder();

            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);

            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setOutputFile(file.getAbsolutePath());
            mediaRecorder.setVideoEncodingBitRate(1000000);
            mediaRecorder.setVideoFrameRate(30);
            mediaRecorder.setVideoSize(videoSize.getWidth(), videoSize.getHeight());
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

            return mediaRecorder;
        } catch (Exception e) {
            LOG.warn("Failed to create media recorder", e);
            recordingFailed(e);
        }
        return null;
    }
}
