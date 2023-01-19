package com.moutamid.tikmobilestudio;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import java.util.List;

public class CameraUtil {
    public static Camera getCameraInstance() {
        Camera camera = null;
        int numCams = Camera.getNumberOfCameras();
        if (numCams > 0) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            try {
                for (int i = 0; i < numCams; i++) {
                    Camera.getCameraInfo(i, info);
                    if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        camera = Camera.open(i);
                        camera.setDisplayOrientation(90);
                        // also set the camera's output orientation
                        Camera.Parameters params = camera.getParameters();
                        params.setRotation(90);
                        camera.setParameters(params);
                        break;
                    }
                }
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        }

        return camera;
    }


    public static int getCameraOrientation(Context context) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT, info);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                Log.d("rotation",""+degrees);
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                Log.d("rotation",""+degrees);
                break;
            case Surface.ROTATION_180:
              //  degrees = 180;
                Log.d("rotation",""+degrees);
                break;
            case Surface.ROTATION_270:
                degrees =180;
                Log.d("rotation",""+degrees);
                break;
        }

        //return (info.orientation - degrees + 360) % 360;
        int result;
        result = (info.orientation + degrees) % 360;
        result = (360 - result) % 360;
        return result;
    }

}
