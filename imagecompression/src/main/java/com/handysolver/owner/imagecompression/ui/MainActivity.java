package com.handysolver.owner.imagecompression.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.handysolver.owner.imagecompression.Java.MarshMallowPermission;
import com.handysolver.owner.imagecompression.R;

public class MainActivity extends AppCompatActivity {
    private MarshMallowPermission marshMallowPermission;
    private static String TAG="NEW DATA";
    private LinearLayout permissionDetail;
    private Boolean openPermissionSettingMenu=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_permission);
        permissionDetail=(LinearLayout) findViewById(R.id.permission_detail);
        checkPermission();
    }
    public void checkPermission(){
        marshMallowPermission=new MarshMallowPermission(this);
        if(marshMallowPermission.checkPermissionForExternalStorage()){
            finish();
            Log.d("permission access","permission for call access");

        }else{
            Log.d("permission access","permission for call denied");
            marshMallowPermission.requestPermissionForExternalStorage();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d("request pemission","request permission "+requestCode);
        switch (requestCode) {
            case MarshMallowPermission.EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d("Req","onRequestPermissionsResult:  request permission granted");
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    // show message
                    Log.d(TAG, "onRequestPermissionsResult: denied");
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //Show permission explanation dialog...
                        permissionDetail.setVisibility(View.VISIBLE);
                        Log.d("Req","onRequestPermissionsResult: kl granted");
                    }else{
                        permissionDetail.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this, "Permission denied to read your External Storage", Toast.LENGTH_SHORT).show();
                    }

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    public void openSettingMenu(View view){
        openPermissionSettingMenu=true;
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }
    public void onResume(){
        super.onResume();
        if(openPermissionSettingMenu){
            // check if permissions are allowed.
            if(marshMallowPermission.checkPermissionForExternalStorage()){
                openPermissionSettingMenu=false;
                Log.d("permission access","permission access");
                permissionDetail.setVisibility(View.GONE);
                finish();
            }
        }
    }
    public void backActivity(View view){
        finish();
    }
}
