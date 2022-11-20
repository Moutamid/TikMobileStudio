package com.moutamid.tikmobilestudio;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;


public class Window extends ContextWrapper {

    // declaring required variables
   // private Context context;
    private View mView;
    private View mViewB;
    private WindowManager.LayoutParams mParams;
    private WindowManager.LayoutParams mParamsB;
    private WindowManager mWindowManager;
    private WindowManager mWindowManagerB;
    private LayoutInflater layoutInflater;
    TextView name;
    ImageView imageView,rotateImg;
    private Camera mCamera;
    private CameraPreview mPreview;
    private LinearLayout cameraPreview;
    private boolean cameraFront = true;
    //private long interval = 0;
    private int flag = 1;
    SharedPreferences sharedPreferences;
    private CustomOrientationEventListener customOrientationEventListener;


    @SuppressLint("WrongConstant")
    public Window(Context context){
        super(context);
        //this.context=context;
        sharedPreferences = new SharedPreferences(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // set the layout parameters of the window
            mParams = new WindowManager.LayoutParams(
                    // Shrink the window to wrap the content rather
                    // than filling the screen
                    WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
              //      WindowManager.LayoutParams.TYPE_SYSTEM_ALERT |
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                            WindowManager.LayoutParams.FLAG_FULLSCREEN |
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    // through any transparent parts
                    PixelFormat.TRANSLUCENT);
        }else {
            mParams = new WindowManager.LayoutParams(
                    // Shrink the window to wrap the content rather
                    // than filling the screen
                    WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT |
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                    WindowManager.LayoutParams.FLAG_FULLSCREEN |
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    // through any transparent parts
                    PixelFormat.TRANSLUCENT);
        }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                mParamsB = new WindowManager.LayoutParams(
                        // Shrink the window to wrap the content rather
                        // than filling the screen
                        WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                        // Display it on top of other application windows
                //        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT |
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
            }else {
                mParamsB = new WindowManager.LayoutParams(
                        // Shrink the window to wrap the content rather
                        // than filling the screen
                        WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                        // Display it on top of other application windows
                        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT |
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
            }

        // getting a LayoutInflater
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflating the view with the custom layout we created
        mView = layoutInflater.inflate(R.layout.activity_start, null);
        mViewB = layoutInflater.inflate(R.layout.banner, null);
        // set onClickListener on the remove button, which removes
        // the view from the window

        cameraPreview = mView.findViewById(R.id.camera);
        name = mViewB.findViewById(R.id.name);
        imageView = mViewB.findViewById(R.id.close);
      //  rotateImg = mViewB.findViewById(R.id.rotate);
        name.setText(sharedPreferences.getName());
        try {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        }catch (Exception e){
            e.printStackTrace();
        }
        mPreview = new CameraPreview(context, mCamera);
      //  mCamera.setDisplayOrientation(90);
       // setCameraDisplayOrientation((MainActivity) context,cameraId,mCamera);
        cameraPreview.addView(mPreview);
        mCamera.startPreview();
        imageView.setOnClickListener(v -> {
            close();
            stopService(new Intent(getApplicationContext(), ForegroundService.class));
            mCamera.stopPreview();
            //cameraKitView.onPause();
           // cameraKitView.onStop();
        });
        // Define the position of the
        // window within the screen
        mParams.gravity = Gravity.TOP | Gravity.START;
        mParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_USER;

        mWindowManager = (WindowManager)context.getSystemService(WINDOW_SERVICE);

        mParamsB.gravity = Gravity.BOTTOM;
        mWindowManagerB = (WindowManager)context.getSystemService(WINDOW_SERVICE);
       // mParams.screenOrientation = port;
        //mParamsB.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
      /*  String rotate = getScreenOrientation(context);
        Toast.makeText(context, rotate, Toast.LENGTH_SHORT).show();
        int orientation = this.getResources().getConfiguration().orientation;
        switch(orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                cameraKitView.setRotation(cameraKitView.getRotation() + 90);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                cameraKitView.setRotation(cameraKitView.getRotation() + 90);
                break;
        }*/
      //  cameraKitView.setRotation(cameraKitView.getRotation() + 90);
        dragDrop(mParams);
        dragDropB(mParamsB);

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


    private void checkRotation(WindowManager mWindowManager) {
        Display display = mWindowManager.getDefaultDisplay();
        int displayOrientation = display.getRotation();
        android.hardware.Camera.CameraInfo cameraInfo =
                new android.hardware.Camera.CameraInfo();
        int rotation = cameraInfo.orientation;
        if (Surface.ROTATION_0 != displayOrientation)
        {
            if (Camera.CameraInfo.CAMERA_FACING_BACK == cameraInfo.facing)
            {
                if (Surface.ROTATION_90 == displayOrientation)
                {
                    rotation -= 90;
                }
                else if (Surface.ROTATION_180 == displayOrientation)
                {
                    rotation -= 180;
                }
                if (Surface.ROTATION_270 == displayOrientation)
                {
                    rotation -= 270;
                }

                if (rotation < 0)
                {
                    rotation += 360;
                }
            }
            else
            {
                if (Surface.ROTATION_90 == displayOrientation)
                {
                    rotation += 90;
                }
                else if (Surface.ROTATION_180 == displayOrientation)
                {
                    rotation += 180;
                }
                if (Surface.ROTATION_270 == displayOrientation)
                {
                    rotation += 270;
                }

                rotation %= 360;
            }
        }

        Log.d("TAG", "Camera orientation (" + cameraInfo.orientation + ", " + displayOrientation + ", " + rotation + ")");
        Toast.makeText(this, ""+rotation, Toast.LENGTH_SHORT).show();
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setRotation(rotation);
    }

    private void dragDropB(WindowManager.LayoutParams params) {
        name.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            boolean flag3 = true;
            boolean flag = false;
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction() & event.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        params.alpha = 1.0f;
                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        mParamsB.gravity = Gravity.BOTTOM | Gravity.END;
                        return true;
                    case MotionEvent.ACTION_UP:
                        flag = flag3;
                        mParamsB.gravity = Gravity.BOTTOM | Gravity.END;
                        if (Math.abs(initialTouchX - event.getRawX()) >= 25f){
                            return flag;
                        }else {
                            flag = flag3;
                            if (Math.abs(initialTouchY - event.getRawY()) >= 25f){
                                return flag;
                            }else {
                                return true;
                            }
                        }
                    case MotionEvent.ACTION_MOVE:
                        params.gravity = Gravity.LEFT | Gravity.RIGHT;
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        //Update the layout with new X & Y coordinate
                        mWindowManagerB.updateViewLayout(mViewB,params);
                        return true;
                }
                return flag;

            }
        });
    }


    public void dragDrop(WindowManager.LayoutParams params){
        cameraPreview.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            boolean flag3 = true;
            boolean flag = false;
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction() & event.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        params.alpha = 1.0f;
                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        flag = flag3;
                        if (Math.abs(initialTouchX - event.getRawX()) >= 25f){
                            return flag;
                        }else {
                            flag = flag3;
                            if (Math.abs(initialTouchY - event.getRawY()) >= 25f){
                                return flag;
                            }else {
                                return true;
                            }
                        }
                    case MotionEvent.ACTION_MOVE:
                          params.x = initialX + (int) (event.getRawX() - initialTouchX);
                          params.y = initialY + (int) (event.getRawY() - initialTouchY);
                                //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mView,params);
                        return true;
                }
                return flag;

            }
        });
    }

    public void open() {

        try {
            // check if the view is already
            // inflated or present in the window
            if(mView.getWindowToken()==null && mViewB.getWindowToken()==null) {
                if(mView.getParent()==null && mViewB.getParent()==null) {
                    mWindowManager.addView(mView, mParams);
                    mWindowManagerB.addView(mViewB, mParamsB);
                }
            }
        } catch (Exception e) {
            Log.d("Error1",e.toString());
        }

    }

    public void close() {

        try {
            // remove the view from the window
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(mView);
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(mViewB);
            // invalidate the view
            mView.invalidate();
            mViewB.invalidate();
            // remove all views
            ((ViewGroup) mView.getParent()).removeAllViews();
            ((ViewGroup) mViewB.getParent()).removeAllViews();
            stopService(new Intent(this, ForegroundService.class));

            // the above steps are necessary when you are adding and removing
            // the view simultaneously, it might give some exceptions
        } catch (Exception e) {
            Log.d("Error2", e.toString());
        }
    }
}