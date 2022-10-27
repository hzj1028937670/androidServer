package com.android.server.activity.client;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.android.server.R;
import com.android.server.activity.BaseActivity;
import com.android.server.config.AppInfo;
import com.bumptech.glide.Glide;

public class LoadImageActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_load);
        initView();
    }

    ImageView iv_image_show;

    private void initView() {
        iv_image_show = (ImageView) findViewById(R.id.iv_image_show);
        String imagePath = AppInfo.getBaseUrImage("aaa.jpg");
        Glide.with(LoadImageActivity.this).load(imagePath).into(iv_image_show);
    }
}
