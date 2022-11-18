package com.moutamid.tikmobilestudio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.Surface;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.List;
import java.util.concurrent.ExecutionException;

public class StartActivity extends AppCompatActivity {
    TextView nameTv;
    ImageView closeImg;
    private Camera mCamera;
    private CameraPreview mPreview;
    private LinearLayout cameraPreview;
    private boolean cameraFront = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        cameraPreview = findViewById(R.id.camera);
        nameTv = findViewById(R.id.name);
        closeImg = findViewById(R.id.close);
        String name = getIntent().getStringExtra("name");
        nameTv.setText(name);
        startCamera();
    }

    private void startCamera() {
        int cameraId = findFrontFacingCamera();
        mCamera =  Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
     //   mCamera.setDisplayOrientation((int) (cameraPreview.getRotation() + 90));
        mPreview = new CameraPreview(this, mCamera);
        cameraPreview.addView(mPreview);
        setCameraDisplayOrientation(StartActivity.this,cameraId,mCamera);
        mCamera.startPreview();
    }


    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private int findFrontFacingCamera() {

        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void onResume() {

        super.onResume();
        if(mCamera == null) {
            mCamera = Camera.open();
            mCamera.setDisplayOrientation(90);
            mPreview.refreshCamera(mCamera);

        }else {
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        //when on Pause, release camera in order to be used from other applications
        releaseCamera();
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
       // cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}