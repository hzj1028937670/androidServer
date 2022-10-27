package com.android.server.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.android.server.R;
import com.android.server.activity.client.TestClientActivity;
import com.android.server.activity.server.MainActivity;
import com.android.server.config.AppConfig;
import com.cdl.permission.PermissionCallback;
import com.cdl.permission.PermissionItem;
import com.cdl.permission.PermissionUtil;

import java.util.ArrayList;
import java.util.List;

public class StartActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAppPermission();
        setContentView(R.layout.activity_start);
        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            boolean hasFullStorageAccess = Environment.isExternalStorageManager();
            if (!hasFullStorageAccess) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }
    }

    private void initView() {
        findViewById(R.id.btnServer).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(StartActivity.this, MainActivity.class));
                        finish();
                    }
                }
        );
        findViewById(R.id.btnClient).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(StartActivity.this, TestClientActivity.class));
                        finish();
                    }
                }
        );
    }

    public void checkAppPermission() {
        List<PermissionItem> permissonItems = new ArrayList<PermissionItem>();
        if (AppConfig.APP_MODEL == AppConfig.APP_MODEL_CLIENT) {
            permissonItems.add(new PermissionItem(Manifest.permission.CAMERA, "摄像机", R.drawable.permission_ic_camera));
        }
        permissonItems.add(new PermissionItem(Manifest.permission.READ_EXTERNAL_STORAGE, "SD卡读", R.drawable.permission_ic_storage));
        permissonItems.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "SD卡写", R.drawable.permission_ic_storage));
        PermissionUtil.create(StartActivity.this)
                .permissions(permissonItems)
                .checkMutiPermission(new PermissionCallback() {
                    @Override
                    public void onFinish() {
//                        startMain();
                    }

                    @Override
                    public void notAllow() {

                    }
                });
    }

    private void startMain() {
        if (AppConfig.APP_MODEL == AppConfig.APP_MODEL_SERVER) { //服务端口
            startActivity(new Intent(StartActivity.this, MainActivity.class));
        } else {  //
            startActivity(new Intent(StartActivity.this, TestClientActivity.class));
        }

        finish();
    }


}
