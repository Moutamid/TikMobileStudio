package com.moutamid.tikmobilestudio;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;

import com.camerakit.CameraKitView;

public interface PassData extends Parcelable {
    void data(CameraKitView cameraKitView);
    void stopservice(Activity context);
}
