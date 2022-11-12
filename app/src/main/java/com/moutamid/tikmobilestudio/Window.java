package com.moutamid.tikmobilestudio;


import android.app.Activity;
import android.content.Context;
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

public class Window extends AppCompatActivity{

    // declaring required variables
    private Context context;
    private View mView;
    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;
    private LayoutInflater layoutInflater;
    public CameraKitView cameraKitView;
    TextView name;

    public Window(Context context, Activity activity, PassData data){
        this.context=context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // set the layout parameters of the window
            mParams = new WindowManager.LayoutParams(
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
        // set onClickListener on the remove button, which removes
        // the view from the window

        cameraKitView = mView.findViewById(R.id.camera);
        //cameraKitView.requestPermissions(activity);
        name = mView.findViewById(R.id.name);

        cameraKitView.setFacing(CameraKit.FACING_FRONT);
        //data.data(cameraKitView);

        name.setOnClickListener(v -> {
            close();
            data.stopservice(activity);
        });

        // Define the position of the
        // window within the screen
        mParams.gravity = Gravity.CENTER;
        mWindowManager = (WindowManager)context.getSystemService(WINDOW_SERVICE);

    }

    public void open() {

        try {
            // check if the view is already
            // inflated or present in the window
            if(mView.getWindowToken()==null) {
                if(mView.getParent()==null) {
                    mWindowManager.addView(mView, mParams);
                }
            }
        } catch (Exception e) {
            Log.d("Error1",e.toString());
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    protected void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }


    public void close() {

        try {
            // remove the view from the window
            ((WindowManager) context.getSystemService(WINDOW_SERVICE)).removeView(mView);
            // invalidate the view
            mView.invalidate();
            // remove all views
            ((ViewGroup) mView.getParent()).removeAllViews();
            stopService(new Intent(this, ForegroundService.class));

            // the above steps are necessary when you are adding and removing
            // the view simultaneously, it might give some exceptions
        } catch (Exception e) {
            Log.d("Error2", e.toString());
        }
    }
}