package com.moutamid.tikmobilestudio;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;
import android.view.TextureView;


public interface PassData extends Parcelable {
    void data(TextureView cameraKitView);
    void stopservice(Activity context);
}
