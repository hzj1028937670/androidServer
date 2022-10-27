package com.android.server.activity.server;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.server.R;
import com.android.server.ServerApplication;
import com.android.server.activity.BaseActivity;
import com.android.server.config.AppConfig;
import com.android.server.config.AppInfo;
import com.android.server.util.SharedPerManager;
import com.android.server.util.server.OnServerChangeListener;
import com.android.server.util.server.ServerPresenter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements OnServerChangeListener, View.OnClickListener {

    private ServerPresenter serverPresenter;
    private Button btn_startServer;
    private Button btn_stopServer;
    private TextView tv_message;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        getScreenSize();
        serverPresenter = new ServerPresenter(this, this);
    }

    Button btn_http;
    Button btn_down_update;

    private void initView() {
        btn_down_update = findViewById(R.id.btn_down_update);
        btn_down_update.setOnClickListener(this);
        btn_http = findViewById(R.id.btn_http);
        btn_http.setOnClickListener(this);
        btn_startServer = findViewById(R.id.btn_startServer);
        btn_startServer.setOnClickListener(this);
        btn_stopServer = findViewById(R.id.btn_stopServer);
        btn_stopServer.setOnClickListener(this);
        tv_message = findViewById(R.id.tv_message);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_down_update:
                Intent intent = new Intent(MainActivity.this, FileListActivity.class);
                String path = AppInfo.BASE_SD_PATH;
                intent.putExtra(FileListActivity.PATH_SEARCH, path);
                startActivity(intent);
                break;
            case R.id.btn_http: // http 请求
                startActivity(new Intent(MainActivity.this, HttpServerActivity.class));
                break;
            case R.id.btn_startServer: {
                serverPresenter.startServer(MainActivity.this);
                break;
            }
            case R.id.btn_stopServer: {
                serverPresenter.stopServer(MainActivity.this);
                break;
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        serverPresenter.unregister(this);
        serverPresenter = null;
    }

    @Override
    public void onServerStarted(String ipAddress) {
        ServerApplication.getInstance().setSERVER_IP(ipAddress);
        btn_startServer.setVisibility(View.GONE);
        btn_stopServer.setVisibility(View.VISIBLE);
        Log.e(TAG, "IP Address: " + ipAddress);
        if (!TextUtils.isEmpty(ipAddress)) {
            List<String> addressList = new ArrayList<>();
            addressList.add("http://" + ipAddress + ":" + AppConfig.PORT_SERVER + AppConfig.GET_FILE);
            addressList.add("http://" + ipAddress + ":" + AppConfig.PORT_SERVER + AppConfig.GET_IMAGE);
            addressList.add("http://" + ipAddress + ":" + AppConfig.PORT_SERVER + AppConfig.POST_JSON);
            addressList.add("http://" + ipAddress + ":" + AppConfig.PORT_SERVER + AppConfig.UPDATE_FILE);
            tv_message.setText(TextUtils.join("\n", addressList));
        } else {
            tv_message.setText("error");
        }
    }

    @Override
    public void onServerStopped() {
        btn_startServer.setVisibility(View.VISIBLE);
        btn_stopServer.setVisibility(View.GONE);
        tv_message.setText("服务器停止了");
    }

    @Override
    public void onServerError(String errorMessage) {
        btn_startServer.setVisibility(View.VISIBLE);
        btn_stopServer.setVisibility(View.GONE);
        tv_message.setText(errorMessage);
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
