package com.serenegiant.usbcameratest4;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.hardware.usb.UsbDevice;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.herohan.uvcapp.CameraException;
import com.herohan.uvcapp.CameraHelper;
import com.herohan.uvcapp.ICameraHelper;
import com.herohan.uvcapp.VideoCapture;
import com.serenegiant.usb.Size;
import com.serenegiant.widget.AspectRatioSurfaceView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Compatibility bridge between the historical Telemetry Viewer CameraFragment API
 * and the maintained UVCAndroid library (com.herohan:UVCAndroid:1.0.13).
 *
 * The application still embeds this framework Fragment by class name, so the public
 * package and the methods used by MapsActivity are intentionally preserved.
 */
public class CameraFragment extends Fragment {

    private ICameraHelper cameraHelper;
    private AspectRatioSurfaceView cameraView;
    private ImageButton recordButton;
    private TextView recordingTime;
    private CameraFragmentListener listener;
    private boolean containerVisible = true;
    private int compressionQuality = 1;
    private long recordingStartMs = 0L;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    private final Runnable recordingClock = new Runnable() {
        @Override
        public void run() {
            if (recordingStartMs == 0L || recordingTime == null) return;
            long elapsed = Math.max(0L, (System.currentTimeMillis() - recordingStartMs) / 1000L);
            long min = elapsed / 60L;
            long sec = elapsed % 60L;
            recordingTime.setText(String.format(Locale.US, "%02d:%02d", min, sec));
            uiHandler.postDelayed(this, 1000L);
        }
    };

    public CameraFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof CameraFragmentListener) {
            listener = (CameraFragmentListener) activity;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        cameraView = root.findViewById(R.id.camera_view);
        recordButton = root.findViewById(R.id.record_button);
        recordingTime = root.findViewById(R.id.recording_time);

        cameraView.setAspectRatio(640, 480);
        cameraView.getHolder().addCallback(surfaceCallback);
        recordButton.setOnClickListener(v -> toggleRecording());
        applyContainerVisibility();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        initCameraHelper();
    }

    @Override
    public void onStop() {
        stopRecordingClock();
        clearCameraHelper();
        super.onStop();
    }

    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }

    private void initCameraHelper() {
        if (cameraHelper != null) return;
        cameraHelper = new CameraHelper();
        cameraHelper.setStateCallback(stateCallback);

        // Some devices are already present before the fragment starts. Select the
        // first UVC candidate in that case; onAttach handles hot-plug afterwards.
        try {
            List<UsbDevice> devices = cameraHelper.getDeviceList();
            if (devices != null && !devices.isEmpty()) {
                cameraHelper.selectDevice(devices.get(0));
            }
        } catch (Throwable ignored) {
            // Device discovery will continue through StateCallback.onAttach().
        }
    }

    private void clearCameraHelper() {
        if (cameraHelper != null) {
            try {
                if (cameraView != null && cameraView.getHolder().getSurface().isValid()) {
                    cameraHelper.removeSurface(cameraView.getHolder().getSurface());
                }
            } catch (Throwable ignored) {
            }
            try {
                if (cameraHelper.isRecording()) cameraHelper.stopRecording();
            } catch (Throwable ignored) {
            }
            cameraHelper.release();
            cameraHelper = null;
        }
    }

    private final SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {
            if (cameraHelper != null && containerVisible) {
                try {
                    cameraHelper.addSurface(holder.getSurface(), false);
                } catch (Throwable ignored) {
                }
            }
        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            if (cameraHelper != null) {
                try {
                    cameraHelper.removeSurface(holder.getSurface());
                } catch (Throwable ignored) {
                }
            }
        }
    };

    private final ICameraHelper.StateCallback stateCallback = new ICameraHelper.StateCallback() {
        @Override
        public void onAttach(UsbDevice device) {
            if (listener != null) listener.onUsbDeviceAttached();
            if (cameraHelper != null) {
                cameraHelper.selectDevice(device);
            }
        }

        @Override
        public void onDeviceOpen(UsbDevice device, boolean isFirstOpen) {
            if (listener != null) listener.onCameraConnecting();
            if (cameraHelper != null) cameraHelper.openCamera();
        }

        @Override
        public void onCameraOpen(UsbDevice device) {
            if (cameraHelper == null) return;
            cameraHelper.startPreview();
            Size size = cameraHelper.getPreviewSize();
            if (cameraView != null && size != null) {
                cameraView.setAspectRatio(size.width, size.height);
            }
            if (cameraView != null && containerVisible && cameraView.getHolder().getSurface().isValid()) {
                try {
                    cameraHelper.addSurface(cameraView.getHolder().getSurface(), false);
                } catch (Throwable ignored) {
                }
            }
            if (listener != null) listener.onCameraConnected();
        }

        @Override
        public void onCameraClose(UsbDevice device) {
            if (cameraHelper != null && cameraView != null) {
                try {
                    cameraHelper.removeSurface(cameraView.getHolder().getSurface());
                } catch (Throwable ignored) {
                }
            }
            stopRecordingClock();
        }

        @Override
        public void onDeviceClose(UsbDevice device) {
        }

        @Override
        public void onDetach(UsbDevice device) {
            stopRecordingClock();
        }

        @Override
        public void onCancel(UsbDevice device) {
            stopRecordingClock();
        }

        @Override
        public void onError(UsbDevice device, CameraException error) {
            stopRecordingClock();
            Activity activity = getActivity();
            if (activity != null) {
                String message = error != null && error.getMessage() != null
                        ? error.getMessage() : "USB camera error";
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
            }
        }
    };

    private void toggleRecording() {
        if (cameraHelper == null || getActivity() == null) return;
        try {
            if (cameraHelper.isRecording()) {
                cameraHelper.stopRecording();
                return;
            }
        } catch (Throwable ignored) {
        }

        final VideoCapture.OutputFileOptions options;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            String name = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US).format(new Date()) + ".mp4";
            values.put(MediaStore.Video.Media.DISPLAY_NAME, name);
            values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            values.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/TelemetryCamera");
            options = new VideoCapture.OutputFileOptions.Builder(
                    getActivity().getContentResolver(),
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    values).build();
        } else {
            File base = getActivity().getExternalFilesDir(Environment.DIRECTORY_MOVIES);
            File dir = new File(base != null ? base : getActivity().getFilesDir(), "TelemetryCamera");
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir,
                    new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US).format(new Date()) + ".mp4");
            options = new VideoCapture.OutputFileOptions.Builder(file).build();
        }

        cameraHelper.startRecording(options, new VideoCapture.OnVideoCaptureCallback() {
            @Override
            public void onStart() {
                recordingStartMs = System.currentTimeMillis();
                uiHandler.removeCallbacks(recordingClock);
                uiHandler.post(recordingClock);
                if (recordButton != null) recordButton.setColorFilter(0x7fff0000);
            }

            @Override
            public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
                stopRecordingClock();
                if (recordButton != null) recordButton.setColorFilter(0);
                Activity activity = getActivity();
                if (activity != null) {
                    Uri uri = outputFileResults.getSavedUri();
                    Toast.makeText(activity,
                            uri != null ? "Video saved: " + uri : "Video saved",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(int videoCaptureError,
                                @NonNull String message,
                                @Nullable Throwable cause) {
                stopRecordingClock();
                if (recordButton != null) recordButton.setColorFilter(0);
                Activity activity = getActivity();
                if (activity != null) Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void stopRecordingClock() {
        recordingStartMs = 0L;
        uiHandler.removeCallbacks(recordingClock);
        if (recordingTime != null) recordingTime.setText("00:00");
    }

    /** Historical Telemetry Viewer API retained for MapsActivity. */
    public void onContainerVisibilityChange(Boolean visible) {
        containerVisible = visible == null || visible;
        applyContainerVisibility();
        if (cameraHelper == null || cameraView == null || !cameraView.getHolder().getSurface().isValid()) return;
        try {
            if (containerVisible) {
                cameraHelper.addSurface(cameraView.getHolder().getSurface(), false);
            } else {
                cameraHelper.removeSurface(cameraView.getHolder().getSurface());
            }
        } catch (Throwable ignored) {
        }
    }

    private void applyContainerVisibility() {
        if (cameraView != null) cameraView.setVisibility(containerVisible ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * Historical API retained. UVCAndroid 1.0.13 owns its encoder profile, so the
     * old 0/1/2 custom H.264 compression parameter has no direct one-to-one API.
     */
    public void setCompressionQuality(int quality) {
        compressionQuality = Math.max(0, Math.min(2, quality));
    }

    public int getCompressionQuality() {
        return compressionQuality;
    }
}
