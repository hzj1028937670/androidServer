package com.cdl.permission.test;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.cdl.permission.PermissionCallback;
import com.cdl.permission.PermissionItem;
import com.cdl.permission.PermissionUtil;
import com.cdl.permission.R;

import java.util.ArrayList;
import java.util.List;


public class TestActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkVideoPermission();
    }

    private void checkVideoPermission() {
        List<PermissionItem> permissonItems = new ArrayList<PermissionItem>();
        permissonItems.add(new PermissionItem(Manifest.permission.READ_EXTERNAL_STORAGE, "SD卡读写", R.drawable.permission_ic_storage));
        permissonItems.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "SD卡读写", R.drawable.permission_ic_storage));
        PermissionUtil.create(TestActivity.this)
                .permissions(permissonItems)
                .checkMutiPermission(new PermissionCallback() {
                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void notAllow() {

                    }
                });

    }
}
