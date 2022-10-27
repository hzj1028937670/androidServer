package com.android.server.activity.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.server.R;
import com.android.server.activity.BaseActivity;
import com.android.server.config.AppInfo;
import com.android.server.service.UdpService;
import com.android.server.util.SharedPerManager;
import com.android.server.util.net.CodeUtil;

public class TestClientActivity extends BaseActivity implements View.OnClickListener {

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(AppInfo.NOTIFY_VIEW_LINE_STATUES)) {
                String ipaddress = SharedPerManager.getHostIpaddress();
                tv_host_ip.setText("服务器IP: " + ipaddress);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliet);
        initView();
        getScreenSize();
        initReceiver();
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppInfo.NOTIFY_VIEW_LINE_STATUES);
        registerReceiver(receiver, filter);
    }

    Button btn_line_web;
    TextView tv_host_ip, tv_ip_local;

    Button btn_http_test;
    Button btn_down_update;
    Button btn_loadImage;

    private void initView() {
        tv_host_ip = (TextView) findViewById(R.id.tv_host_ip);
        tv_ip_local = (TextView) findViewById(R.id.tv_ip_local);
        btn_loadImage = (Button) findViewById(R.id.btn_loadImage);
        btn_loadImage.setOnClickListener(this);

        btn_line_web = (Button) findViewById(R.id.btn_line_web);
        btn_line_web.setOnClickListener(this);
        btn_http_test = (Button) findViewById(R.id.btn_http_test);
        btn_http_test.setOnClickListener(this);
        btn_down_update = (Button) findViewById(R.id.btn_down_update);
        btn_down_update.setOnClickListener(this);
        String ipLocal = CodeUtil.getIpAddress(TestClientActivity.this);
        tv_ip_local.setText("本机IP: " + ipLocal);
        lineWebServer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_loadImage:
                startActivity(new Intent(TestClientActivity.this, LoadImageActivity.class));
                break;
            case R.id.btn_down_update:
                startActivity(new Intent(TestClientActivity.this, DownUpdateActivity.class));
                break;
            case R.id.btn_http_test:
                startActivity(new Intent(TestClientActivity.this, HttpTestActivity.class));
                break;
            case R.id.btn_line_web:
                lineWebServer();
                break;
        }
    }

    private void lineWebServer() {
        String localIp = CodeUtil.getIpAddress(TestClientActivity.this);
        String baseIp = localIp.substring(0, localIp.lastIndexOf(".") + 1);
        for (int i = 0; i < 255; i++) {
            String sendIp = baseIp + i;
            if (!sendIp.equals(localIp)) {
                String sendJson = "{\"type\":\"getLocalDev\",\"ipaddress\":\"" + localIp + "\"}";
                UdpService.getInstance().sendMessageToUdp(sendJson, sendIp);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    private void getScreenSize() {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int width = localDisplayMetrics.widthPixels;
        int height = localDisplayMetrics.heightPixels;
        SharedPerManager.setScreenWidth(width);
        SharedPerManager.setScreenHeight(height);
    }
}
