package com.moutamid.tikmobilestudio;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.camerakit.CameraKit;
import com.camerakit.CameraKitView;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 99107;
    Button start;
    EditText name;
    int requestCode;
    String[] permissions;
    int[] grantResults;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.name);
        start = findViewById(R.id.btnStart);

        /*Dexter.withContext(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                    }
                })
                .check();
*/
        start.setOnClickListener(v -> {
            if (name.getText().toString().isEmpty()){
                name.setError("Please Add Your Name");
            } else {
                if (checkCameraHardware(this)){
                    checkOverlayPermission();
                    startService();
                } else
                Toast.makeText(this, "No Camera Detected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
    *
        //start
            Intent startIntent = new Intent(MainActivity.this, ForegroundService.class);
            startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            startService(startIntent);
        //stop

    * */

    public void startService(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // check if the user has already granted
            // the Draw over other apps permission
            if(Settings.canDrawOverlays(this)) {
                // start the service based on the android version
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent i = new Intent(this, ForegroundService.class);
                    startForegroundService(i);
                } else {
                    Intent i = new Intent(this, ForegroundService.class);
                    startService(i);
                }
            }
        }else{
            Intent i = new Intent(this, ForegroundService.class);
            startService(i);
        }
    }

    // method to ask user to grant the Overlay permission
    public void checkOverlayPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                // send user to the device settings
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivity(myIntent);
            }
        }
    }

    // check for permission again when user grants it from
    // the device settings, and start the service
    @Override
    protected void onResume() {
        super.onResume();
        startService();
    }

    /** A safe way to get an instance of the Camera object. */
    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public PassData getData(){
        return passData;
    }
    public Activity getAct(){
        return MainActivity.this;
    }

    private PassData passData = new PassData() {
        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {}

        @Override
        public void data(CameraKitView cameraKitView) {
            //Toast.makeText(MainActivity.this, "camera", Toast.LENGTH_SHORT).show();
            //cameraKitView.onStart();
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            //performAction(...);
            } else {
                // You can directly ask for the permission.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[] { Manifest.permission.CAMERA }, REQUEST_CODE);
                }
            }
            cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        @Override
        public void stopservice(Activity context) {
            Toast.makeText(MainActivity.this, "fgh", Toast.LENGTH_SHORT).show();
            context.stopService(new Intent(context.getApplicationContext(), ForegroundService.class));
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        this.requestCode = requestCode;
        this.permissions = permissions;
        this.grantResults = grantResults;
    }
}