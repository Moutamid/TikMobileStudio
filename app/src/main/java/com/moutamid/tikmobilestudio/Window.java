package com.moutamid.tikmobilestudio;


import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import static android.content.Context.WINDOW_SERVICE;

import androidx.appcompat.app.AppCompatActivity;

import com.camerakit.CameraKit;
import com.camerakit.CameraKitView;

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
    public CameraKitView cameraKitView;
    TextView name;

    public Window(Context context){
        super(context);
        //this.context=context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // set the layout parameters of the window
            mParams = new WindowManager.LayoutParams(
                    // Shrink the window to wrap the content rather
                    // than filling the screen
                    WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.MATCH_PARENT,
                    // Display it on top of other application windows
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    // Don't let it grab the input focus
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    // Make the underlying application window visible
                    // through any transparent parts
                    PixelFormat.TRANSLUCENT);

            mParamsB = new WindowManager.LayoutParams(
                    // Shrink the window to wrap the content rather
                    // than filling the screen
                    WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                    // Display it on top of other application windows
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    // Don't let it grab the input focus
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    // Make the underlying application window visible
                    // through any transparent parts
                    PixelFormat.TRANSLUCENT);

        }

        // getting a LayoutInflater
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflating the view with the custom layout we created
        mView = layoutInflater.inflate(R.layout.activity_start, null);
        mViewB = layoutInflater.inflate(R.layout.banner, null);
        // set onClickListener on the remove button, which removes
        // the view from the window

        cameraKitView = mView.findViewById(R.id.camera);
//        cameraKitView.onPause();
//        cameraKitView.onStop();
        cameraKitView.onStart();
        //cameraKitView.requestPermissions(activity);
        name = mViewB.findViewById(R.id.name);

        cameraKitView.setFacing(CameraKit.FACING_FRONT);
        //data.data(cameraKitView);

        name.setOnClickListener(v -> {
            close();
            stopService(new Intent(getApplicationContext(), ForegroundService.class));
            cameraKitView.onPause();
            cameraKitView.onStop();
        });

        // Define the position of the
        // window within the screen
        mParams.gravity = Gravity.RIGHT;
        mWindowManager = (WindowManager)context.getSystemService(WINDOW_SERVICE);

        mParamsB.gravity = Gravity.BOTTOM;
        mWindowManagerB = (WindowManager)context.getSystemService(WINDOW_SERVICE);

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