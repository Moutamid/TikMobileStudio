package com.moutamid.tikmobilestudio;

import static android.content.Context.WINDOW_SERVICE;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Context context;
    private OrientationEventListener mOrientationEventListener;
    private int mOrientation =  -1;

    private static final int ORIENTATION_PORTRAIT_NORMAL =  1;
    private static final int ORIENTATION_PORTRAIT_INVERTED =  2;
    private static final int ORIENTATION_LANDSCAPE_NORMAL =  3;
    private static final int ORIENTATION_LANDSCAPE_INVERTED =  4;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.context = context;
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // create the surface and start camera preview
            if (mCamera == null) {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            }
        } catch (IOException e) {
            Log.d(VIEW_LOG_TAG, "Error setting camera preview: " + e.getMessage());
        }
    }


    public void refreshCamera(Camera camera) {
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }
        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        setCamera(camera);
        try {
            setRotation();
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(VIEW_LOG_TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        refreshCamera(mCamera);
    }

    private void setRotation(){
        if (mOrientationEventListener == null) {
            mOrientationEventListener = new OrientationEventListener(context,
                    SensorManager.SENSOR_DELAY_NORMAL) {

                @Override
                public void onOrientationChanged(int orientation) {

                    // determine our orientation based on sensor response
                    int lastOrientation = mOrientation;

                    if (orientation >= 315 || orientation < 45) {
                        if (mOrientation != ORIENTATION_PORTRAIT_NORMAL) {
                            mOrientation = ORIENTATION_PORTRAIT_NORMAL;
                        }
                    }
                    else if (orientation < 315 && orientation >= 225) {
                        if (mOrientation != ORIENTATION_LANDSCAPE_NORMAL) {
                            mOrientation = ORIENTATION_LANDSCAPE_NORMAL;
                        }
                    }
                    else if (orientation < 225 && orientation >= 135) {
                        if (mOrientation != ORIENTATION_PORTRAIT_INVERTED) {
                            mOrientation = ORIENTATION_PORTRAIT_INVERTED;
                        }
                    }
                    else { // orientation <135 && orientation > 45
                        if (mOrientation != ORIENTATION_LANDSCAPE_INVERTED) {
                            mOrientation = ORIENTATION_LANDSCAPE_INVERTED;
                        }
                    }

                    if (lastOrientation != mOrientation) {
                        changeRotation(mOrientation, lastOrientation);
                    }
                }
            };
        }
        if (mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
        }
    }

    private void changeRotation(int orientation, int lastOrientation) {
        switch (orientation) {
            case ORIENTATION_PORTRAIT_NORMAL:
                mCamera.setDisplayOrientation(90);
                Log.v("CameraActivity", "Orientation = 90");
                break;
            case ORIENTATION_LANDSCAPE_NORMAL:
                mCamera.setDisplayOrientation(0);
                Log.v("CameraActivity", "Orientation = 0");
                break;
            case ORIENTATION_PORTRAIT_INVERTED:
                mCamera.setDisplayOrientation(270);
                Log.v("CameraActivity", "Orientation = 270");
                break;
            case ORIENTATION_LANDSCAPE_INVERTED:
                mCamera.setDisplayOrientation(180);
                Log.v("CameraActivity", "Orientation = 180");
                break;
        }
    }

    public void setCamera(Camera camera) {
        //method to set a camera instance
        mCamera = camera;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        // mCamera.release();

    }
}
