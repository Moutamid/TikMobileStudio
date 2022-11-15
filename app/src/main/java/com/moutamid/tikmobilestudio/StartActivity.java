package com.moutamid.tikmobilestudio;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.camerakit.CameraKit;
import com.camerakit.CameraKitView;

import java.util.List;

public class StartActivity extends AppCompatActivity {
    TextView nameTv;
    ImageView closeImg;
    private CameraKitView cameraKitView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        cameraKitView = findViewById(R.id.camera);
        nameTv = findViewById(R.id.name);
        closeImg = findViewById(R.id.close);
        String name = getIntent().getStringExtra("name");
        nameTv.setText(name);

        cameraKitView.setFacing(CameraKit.FACING_FRONT);

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}