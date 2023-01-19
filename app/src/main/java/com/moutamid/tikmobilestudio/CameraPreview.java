package com.moutamid.tikmobilestudio;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.List;

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
    private String rotation = "portrait";

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


    public void refreshCamera(Camera camera, int w, int h) {
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        setCamera(camera);
        try {
            setRotation();
           // final Camera.Parameters params = mCamera.getParameters();
            // viewParams is from the view where the preview is displayed
            final Camera.Parameters parameters = mCamera.getParameters();
            Camera.Size optimalSize = getOptimalPreviewSize(mCamera,w,h);
            parameters.setPreviewSize(optimalSize.width, optimalSize.height);
            mCamera.setParameters(parameters);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(VIEW_LOG_TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
    /*@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);

        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);

      //  mPreviewSize = CameraUtil.getOptimalPreviewSize(context,mCamera,width,height);
        if (mSupportedPreviewSizes != null) {

            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);

        }

        if (mPreviewSize != null) {

            float ratio;

            if (mPreviewSize.height >= mPreviewSize.width)

                ratio = (float) mPreviewSize.height / (float) mPreviewSize.width;

            else

                ratio = (float) mPreviewSize.width / (float) mPreviewSize.height;


            // One of these methods should be used, second method squishes preview slightly

            setMeasuredDimension(width, (int) (width * ratio));

            //        setMeasuredDimension((int) (width * ratio), height);

        }
    }*/

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        refreshCamera(mCamera,w,h);
    }

    public int setCameraDisplayOrientation(Activity activity, int cameraId) {

        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();

        android.hardware.Camera.getCameraInfo(cameraId, info);

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

        int degrees = 0;

        switch (rotation) {

            case Surface.ROTATION_0:

                degrees = 0;

                break;

            case Surface.ROTATION_90:

                degrees = 90;

                break;

            case Surface.ROTATION_180:

                degrees = 180;

                break;

            case Surface.ROTATION_270:

                degrees = 270;

                break;

        }


        int result;

        result = (info.orientation + degrees) % 360;

        result = (360 - result) % 360;

        //int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        // do something for phones running an SDK before lollipop

        /*  if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {

            result = (info.orientation + degrees) % 360;

            result = (360 - result) % 360; // compensate the mirror

        } else { // back-facing

            result = (info.orientation - degrees + 360) % 360;

        }*/

        return (result);

    }

    public Camera.Size getOptimalPreviewSize(Camera camera, int w, int h) {
        if (camera == null) {
            return null;
        }

        List<Camera.Size> sizes = camera.getParameters().getSupportedPreviewSizes();
        double targetRatio = 0;
        if (rotation.equals("portrait")) {

            Log.d("p_width",""+w);
            Log.d("p_height",""+h);
            targetRatio = (double) h / w;

            Log.d("p_target",""+targetRatio);
        }
        else if (rotation.equals("landscape")){

            targetRatio = (double) w / h;
            Log.d("l_width",""+w);
            Log.d("l_height",""+h);

            Log.d("l_target",""+targetRatio);
        }

        final double ASPECT_TOLERANCE = 0.1;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }


    private void setRotation(){
        if (mOrientationEventListener == null) {
            mOrientationEventListener = new OrientationEventListener(context,
                    SensorManager.SENSOR_DELAY_NORMAL) {

                @Override
                public void onOrientationChanged(int orientation) {

                    // determine our orientation based on sensor response
                    int lastOrientation = mOrientation;
                    //Toast.makeText(context, ""+screenorientation, Toast.LENGTH_SHORT).show();

                    /*if (screenorientation == Surface.ROTATION_0) {
                        if (orientation >= 315 || orientation < 45) {
                            if (mOrientation != ORIENTATION_LANDSCAPE_NORMAL) {
                                mOrientation = ORIENTATION_LANDSCAPE_NORMAL;
                            }
                        } else if (orientation < 315 && orientation >= 225) {
                            if (mOrientation != ORIENTATION_PORTRAIT_INVERTED) {
                                mOrientation = ORIENTATION_PORTRAIT_INVERTED;
                            }
                        } else if (orientation < 225 && orientation >= 135) {
                            if (mOrientation != ORIENTATION_LANDSCAPE_INVERTED) {
                                mOrientation = ORIENTATION_LANDSCAPE_INVERTED;
                            }
                        } else if (orientation < 135 && orientation > 45) { // orientation <135 && orientation > 45
                            if (mOrientation != ORIENTATION_PORTRAIT_NORMAL) {
                                mOrientation = ORIENTATION_PORTRAIT_NORMAL;
                            }
                        }
                    }else {
                        if (orientation >= 315 || orientation < 45) {
                            if (mOrientation != ORIENTATION_PORTRAIT_NORMAL) {
                                mOrientation = ORIENTATION_PORTRAIT_NORMAL;
                            }
                        } else if (orientation < 315 && orientation >= 225) {
                            if (mOrientation != ORIENTATION_LANDSCAPE_NORMAL) {
                                mOrientation = ORIENTATION_LANDSCAPE_NORMAL;
                            }
                        } else if (orientation < 225 && orientation >= 135) {
                            if (mOrientation != ORIENTATION_PORTRAIT_INVERTED) {
                                mOrientation = ORIENTATION_PORTRAIT_INVERTED;
                            }
                        } else if (orientation < 135 && orientation > 45) { // orientation <135 && orientation > 45
                            if (mOrientation != ORIENTATION_LANDSCAPE_INVERTED) {
                                mOrientation = ORIENTATION_LANDSCAPE_INVERTED;
                            }
                        }
                    }*/

                    if (orientation >= 315 || orientation < 45) {
                        if (mOrientation != ORIENTATION_PORTRAIT_NORMAL) {
                            mOrientation = ORIENTATION_PORTRAIT_NORMAL;
                        }
                    } else if (orientation < 315 && orientation >= 225) {
                        if (mOrientation != ORIENTATION_LANDSCAPE_NORMAL) {
                            mOrientation = ORIENTATION_LANDSCAPE_NORMAL;
                        }
                    } else if (orientation < 225 && orientation >= 135) {
                        if (mOrientation != ORIENTATION_PORTRAIT_INVERTED) {
                            mOrientation = ORIENTATION_PORTRAIT_INVERTED;
                        }
                    } else if (orientation < 135 && orientation > 45) { // orientation <135 && orientation > 45
                        if (mOrientation != ORIENTATION_LANDSCAPE_INVERTED) {
                            mOrientation = ORIENTATION_LANDSCAPE_INVERTED;
                        }
                    }

                    if (lastOrientation != mOrientation) {
                        changeRotation(mOrientation);
                    }
                }
            };
        }
        if (mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
        }
    }

    private void changeRotation(int orientation) {
        switch (orientation) {
            case ORIENTATION_PORTRAIT_NORMAL:
                mCamera.setDisplayOrientation(90);
                rotation = "portrait";
                Log.v("CameraActivity", "Orientation = 90");
                Log.v("Rotation", rotation);
                break;
            case ORIENTATION_LANDSCAPE_NORMAL:
                mCamera.setDisplayOrientation(0);
                rotation = "landscape";
                Log.v("CameraActivity", "Orientation = 0");
                Log.v("Rotation", rotation);
                break;
            case ORIENTATION_PORTRAIT_INVERTED:
                rotation = "portrait";
                mCamera.setDisplayOrientation(270);
                Log.v("CameraActivity", "Orientation = 270");
                Log.v("Rotation", rotation);
                break;
            case ORIENTATION_LANDSCAPE_INVERTED:
                rotation = "landscape";
                mCamera.setDisplayOrientation(180);
                Log.v("Rotation", rotation);
                Log.v("CameraActivity", "Orientation = 180");
                break;
        }
        //  List<Camera.Size> mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();


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
