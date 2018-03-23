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
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
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
import android.support.annotation.WorkerThread;
import android.support.v8.renderscript.RenderScript;
import android.util.Range;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import com.google.common.collect.Sets;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Single;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.graphics.ImageFormat.YUV_420_888;
import static android.hardware.camera2.CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_3;
import static android.hardware.camera2.CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_FULL;
import static android.hardware.camera2.CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY;
import static android.hardware.camera2.CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED;
import static android.hardware.camera2.CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR;
import static android.hardware.camera2.CaptureResult.*;
import static org.sagebase.crf.step.active.HeartBeatUtil.getHeartBeatSample;
import static org.sagebase.crf.step.active.ImageUtils.toBitmap;

/**
 * Created by liujoshua on 2/19/2018.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class HeartRateCamera2Recorder extends Recorder {
    private static final Logger LOG = LoggerFactory.getLogger(HeartRateCamera2Recorder.class);
    
    public static final String MP4_CONTENT_TYPE = "video/mp4";
    public static final long CAMERA_FRAME_DURATION_NANOS = 16_666_666L;
    public static final long CAMERA_EXPOSURE_DURATION_NANOS = 8_333_333L;
    public static final int CAMERA_SENSITIVITY = 60;
    public static final int VIDEO_ENCODING_BIT_RATE = 500_000;
    public static final int VIDEO_FRAME_RATE = 60;
    public static final int VIDEO_ENCODER = MediaRecorder.VideoEncoder.H264;
    public static final int VIDEO_WIDTH = 192;
    public static final int VIDEO_SIZE = 144;
    
    private final CompositeSubscription subscriptions;
    private final List<Surface> allSurfaces = new ArrayList(3);
    private final List<Surface> surfacesNoMediaRecorder = new ArrayList<>(2);
    private final List<Surface> surfacesNoPreview = new ArrayList<>(2);
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
    private CameraManager manager;
    
    
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
        manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        if (manager == null) {
            LOG.warn("This device doesn't support Camera2 API");
            recordingFailed(new IllegalStateException("This device doesn't support Camera2 API"));
            return;
        }
        String cameraId = selectCamera(manager, context);
    
        Observable<CameraCaptureSession> cameraCaptureSessionObservable =
                openCameraObservable(manager, cameraId)
                        .doOnUnsubscribe(() -> LOG.debug("Camera Capture unsubscribed"))
                        .flatMap(cameraDevice -> {
                            // video recording surface
                            mediaRecorder =
                                    createMediaRecorder(mVideoSize, mediaRecorderFile);
                            try {
                                mediaRecorder.prepare();
                            } catch (IOException e) {
                                return Observable.error(e);
                            }
                        
                            Surface recordingSurface = mediaRecorder.getSurface();
                            allSurfaces.add(recordingSurface);
                            surfacesNoPreview.add(recordingSurface);
                        
                            // preview surface
//                            textureView.setLayoutParams(new FrameLayout.LayoutParams(
//                                    mVideoSize.getWidth(),
//                                    mVideoSize.getHeight()
//                            ));
//
                            Surface previewSurface = new Surface(textureView.getSurfaceTexture());
                        
                            allSurfaces.add(previewSurface);
                            surfacesNoMediaRecorder.add(previewSurface);
                        
                            // heart rate processing surface
                            imageReader = ImageReader.newInstance(
                                    mVideoSize.getWidth(),
                                    mVideoSize.getHeight(),
                                    YUV_420_888,
                                    5);
                            
                            Surface processingSurface = imageReader.getSurface();
                            allSurfaces.add(processingSurface);
                            surfacesNoMediaRecorder.add(processingSurface);
                            surfacesNoPreview.add(processingSurface);
                        
                            return createCaptureSessionObservable(cameraDevice, allSurfaces)
                                    .doOnNext(session -> {
                                        mediaRecorder.start();
                                    })
                                    .doOnNext(session ->
                                            subscriptions.add(createImageReaderObservable(imageReader)
                                                    .observeOn(Schedulers.computation())
                                                    .map(this::toHeartBeatSample)
                                                    .subscribeOn(Schedulers.io())
                                                    .doOnUnsubscribe(() -> LOG.debug("ImageReader unsubscribed"))
                                                    .subscribe(
                                                            heartBeatJsonWriter::onHeartRateSampleDetected,
                                                            this::recordingFailed,
                                                            imageReader::close)))
                                    .doOnUnsubscribe(() -> LOG.debug("Capture session 0 unsubscribed"))
                                    .doOnUnsubscribe(() -> {
                                        try {
                                            mediaRecorder.release();
                                        } catch (Throwable t) {
                                            LOG.error("Couldn't release mediaRecorder", t);
                                        }
                                    });
                        });
    
        subscriptions.add(cameraCaptureSessionObservable
                .subscribe(
                        s -> {
                            cameraCaptureSession = s;
                            doRepeatingRequest(s, surfacesNoMediaRecorder);
                            setRecording(true);
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
        doRepeatingRequest(cameraCaptureSession, surfacesNoPreview);
    }
    
    @WorkerThread
    public HeartBeatSample toHeartBeatSample(ImageReader imageReader) {
        Image image = imageReader.acquireNextImage();
        Bitmap bitmap =
                toBitmap(renderScript, image, mVideoSize.getWidth(),
                        mVideoSize.getHeight());

        HeartBeatSample sample =
                getHeartBeatSample(image.getTimestamp() / 1_000_000D, bitmap);
        image.close();
        return sample;
    }
    @Override
    public void setRecorderListener(RecorderListener listener) {
        super.setRecorderListener(listener);
        heartBeatJsonWriter.setRecorderListener(listener);
    }
    
    @Override
    public void stop() {
        heartBeatJsonWriter.stop();
        subscriptions.unsubscribe();

        if (mediaRecorderFile.exists()) {
            FileResult fileResult = new FileResult(fileResultIdentifier(), mediaRecorderFile, MP4_CONTENT_TYPE);
            fileResult.setStartDate(startTime.toDate());
            fileResult.setEndDate(new Date());
    
            getRecorderListener().onComplete(this, fileResult);
        }
    }

    @Override
    public void cancel() {
        heartBeatJsonWriter.cancel();
        subscriptions.unsubscribe();
        
        if (mediaRecorderFile.exists()) {
            mediaRecorderFile.delete();
        }
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
                
                StreamConfigurationMap streamConfigurationMap = cameraCharacteristics
                        .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                // resolution
                mVideoSize = chooseOptimalSize(streamConfigurationMap.getOutputSizes(MediaRecorder.class), VIDEO_WIDTH,
                        VIDEO_SIZE);
                LOG.debug("Video Size: {}", mVideoSize);
                

                // wasn't working for OnePlus
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    manager.setTorchMode(cameraId, true);
//                }
    
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
                        try {
                            LOG.debug("Closing CameraCaptureSession");
                            captureSession[0].close();
                        } catch (IllegalStateException e) {
                            LOG.debug("Couldn't close camera", e);
                        }
                    }
                });
    }
    
    void doRepeatingRequest(@NonNull CameraCaptureSession session, @NonNull List<Surface> surfaces) {
        try {
            CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(session.getDevice().getId());
    
            StreamConfigurationMap streamConfigurationMap = cameraCharacteristics
                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            
            // calculate frame rate
            long durationRecorderNs = streamConfigurationMap.getOutputMinFrameDuration(MediaRecorder.class,
                    mVideoSize);
            LOG.debug("Min output frame duration for MediaRecorder: {}", durationRecorderNs);
    
            long durationPreviewNs = streamConfigurationMap.getOutputMinFrameDuration(SurfaceTexture.class,
                    mVideoSize);
            LOG.debug("Min output frame duration for SurfaceTexture: {}", durationPreviewNs);
            int[] outputFormats = streamConfigurationMap.getOutputFormats();
            LOG.debug("Output formats: {}", outputFormats);
            
    
            Range<Long> exposureTimeRange = cameraCharacteristics
                    .get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);
            LOG.debug("Exposure time range: {}", exposureTimeRange);
    
            Range<Integer> sensitivityTimeRange = cameraCharacteristics
                    .get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
            LOG.debug("Sensitivity time range: {}", sensitivityTimeRange);
    
            int[] inputFormats = new int[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                inputFormats = streamConfigurationMap.getInputFormats();
            }
            LOG.debug("Input formats: {}", inputFormats);
            
            LOG.debug("Attempting to create capture request");
            CaptureRequest.Builder requestBuilder = session.getDevice()
                    .createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            for (Surface s : surfaces) {
                requestBuilder.addTarget(s);
            }
    
            // turns off AF, AE, AWB
            requestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_OFF);
    
            // let's not do any AWB for now. seems complex and interacts with AE
            requestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_OFF);
            // requestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CONTROL_AWB_MODE_DAYLIGHT);
            
            int supportLevel = cameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
    
            int[] availableCapabilities = cameraCharacteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
            
            if (INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY == supportLevel) {
                requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                        CameraMetadata.CONTROL_AE_MODE_ON);
                requestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
            } else if (INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED == supportLevel
                    && Arrays.asList(availableCapabilities).contains(REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR)) {
                // no auto-exposure
                requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                        CameraMetadata.CONTROL_AE_MODE_OFF);
                requestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
            } else if (INFO_SUPPORTED_HARDWARE_LEVEL_FULL == supportLevel
                    || INFO_SUPPORTED_HARDWARE_LEVEL_3 == supportLevel) {
                // no auto-exposure
                requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                        CameraMetadata.CONTROL_AE_MODE_OFF);
                requestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
            }

            requestBuilder.set(CaptureRequest.SENSOR_FRAME_DURATION, CAMERA_FRAME_DURATION_NANOS);
            requestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, CAMERA_EXPOSURE_DURATION_NANOS);
            requestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, CAMERA_SENSITIVITY);
    
            // should work on legacy devices
            // no auto-focus infinite, focus distance
            requestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_OFF);
            requestBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, 0.0f);
    
    
            CaptureRequest captureRequest = requestBuilder.build();
//            for (CaptureRequest.Key<?> k : captureRequest.getKeys()) {
//                Object value = captureRequest.get(k);
//                LOG.debug("Capture request Key: {}, value: {}", k, value);
//            }
    
            session.setRepeatingRequest(captureRequest, mPreCaptureCallback, null);
        } catch (CameraAccessException e) {
            LOG.warn("Failed to set capture request", e);
            recordingFailed(e);
        }
    }
    
    private CameraCaptureSession.CaptureCallback mPreCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {
        Set<CaptureResult.Key> keys = Sets.newHashSet(CONTROL_AE_MODE,CONTROL_AWB_MODE,CONTROL_AF_MODE,
                LENS_FOCAL_LENGTH,SENSOR_SENSITIVITY,
                LENS_FOCUS_DISTANCE,
                SENSOR_EXPOSURE_TIME,SENSOR_FRAME_DURATION
                );
                
                @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            for (CaptureResult.Key key : keys) {
                LOG.debug("Capture progress result with setting key: {}, value: {}", key.getName(), partialResult.get
                        (key));
            }
        }
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            for (CaptureResult.Key key : keys) {
                LOG.debug("Capture complete result with setting key: {}, value: {}", key.getName(), result.get(key));
            }
        }
        public void onCaptureBufferLost(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request, @NonNull Surface target, long frameNumber) {
            LOG.warn("Capture Buffer lost");
        }
    };
    
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
        // the lowest available resolution for the first back camera
        try {
            MediaRecorder mediaRecorder = new MediaRecorder();

            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);

            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setOutputFile(file.getAbsolutePath());
            mediaRecorder.setVideoEncodingBitRate(VIDEO_ENCODING_BIT_RATE);
            mediaRecorder.setVideoFrameRate(VIDEO_FRAME_RATE);
            mediaRecorder.setVideoSize(videoSize.getWidth(), videoSize.getHeight());
            mediaRecorder.setVideoEncoder(VIDEO_ENCODER);

            return mediaRecorder;
        } catch (Exception e) {
            LOG.warn("Failed to create media recorder", e);
            recordingFailed(e);
        }
        return null;
    }
}
