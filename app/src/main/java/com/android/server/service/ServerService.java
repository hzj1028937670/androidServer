package com.android.server.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.android.server.config.AppConfig;
import com.android.server.util.handler.UploadFileHandler;
import com.android.server.util.net.CodeUtil;
import com.android.server.util.handler.DownloadFileHandler;
import com.android.server.util.handler.DownloadImageHandler;
import com.android.server.util.handler.JsonHandler;
import com.android.server.util.net.NetWorkUtils;
import com.android.server.util.server.ServerPresenter;
import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;
import com.yanzhenjie.andserver.filter.HttpCacheFilter;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

public class ServerService extends Service {

    private Server server;

    private static final String TAG = "ServerService";

    @Override
    public void onCreate() {
        server = AndServer.serverBuilder()
                .inetAddress(NetWorkUtils.getLocalIPAddress())  //服务器要监听的网络地址
                .port(AppConfig.PORT_SERVER) //服务器要监听的端口
                .timeout(10, TimeUnit.SECONDS) //Socket超时时间
                .registerHandler(AppConfig.GET_FILE, new DownloadFileHandler()) //注册一个文件下载接口
                .registerHandler(AppConfig.GET_IMAGE, new DownloadImageHandler()) //注册一个图片下载接口
                .registerHandler(AppConfig.POST_JSON, new JsonHandler()) //注册一个Post Json接口
                .registerHandler(AppConfig.UPDATE_FILE, new UploadFileHandler()) //注册一个上传接口
                .filter(new HttpCacheFilter()) //开启缓存支持
                .listener(new Server.ServerListener() {  //服务器监听接口
                    @Override
                    public void onStarted() {
                        String hostAddress = server.getInetAddress().getHostAddress();
                        Log.e(TAG, "onStarted : " + hostAddress);
                        ServerPresenter.onServerStarted(ServerService.this, hostAddress);
                    }

                    @Override
                    public void onStopped() {
                        Log.e(TAG, "onStopped");
                        ServerPresenter.onServerStopped(ServerService.this);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "onError : " + e.getMessage());
                        ServerPresenter.onServerError(ServerService.this, e.getMessage());
                    }
                })
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startServer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopServer();
    }

    private void startServer() {
        //如果Server已启动则不再重复启动，此时只是向外发布已启动的状态
        if (server.isRunning()) {
            InetAddress inetAddress = server.getInetAddress();
            if (inetAddress != null) {
                String hostAddress = inetAddress.getHostAddress();
                if (!TextUtils.isEmpty(hostAddress)) {
                    ServerPresenter.onServerStarted(ServerService.this, hostAddress);
                }
            }
        } else {
            server.startup();
        }
    }

    private void stopServer() {
        if (server != null && server.isRunning()) {
            server.shutdown();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
