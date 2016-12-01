package com.anke.vehicle.camputertest;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import permissiongen.PermissionFail;
import permissiongen.PermissionGen;
import permissiongen.PermissionSuccess;

public class GetPermissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionGen.with(GetPermissionActivity.this)
                .addRequestCode(100)
                .permissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .request();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
    @PermissionSuccess(requestCode = 100)
    public void doSuccess(){
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
    @PermissionFail(requestCode = 100)
    public void doFail(){
        Toast.makeText(this, "获取权限失败", Toast.LENGTH_SHORT).show();
        finish();
    }
}



