package com.android.server.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.android.server.config.AppInfo;
import com.android.server.udp.parsener.UdpParnsener;
import com.android.server.udp.view.UdpMessageView;
import com.android.server.util.MyLog;
import com.android.server.util.SharedPerManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UdpService extends Service implements UdpMessageView {

    public static UdpService instance;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    ExecutorService executor = Executors.newFixedThreadPool(CPU_COUNT * 2);

    public static UdpService getInstance() {
        if (instance == null) {
            synchronized (UdpService.class) {
                if (instance == null) {
                    instance = new UdpService();
                }
            }
        }
        return instance;
    }

    public void executor(Runnable runnable) {
        executor.execute(runnable);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initUdp();
        udpParnsener.receiveMessage();
    }

    UdpParnsener udpParnsener;

    private void initUdp() {
        if (udpParnsener == null) {
            udpParnsener = new UdpParnsener(UdpService.this, this);
        }
    }

    /**
     * 发消息给接收者
     *
     * @param message
     * @param ip
     */
    public void sendMessageToUdp(String message, String ip) {
        if (udpParnsener == null) {
            initUdp();
        }
        udpParnsener.sendUdpMessage(message, ip);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (udpParnsener != null) {
            udpParnsener.stopReceiveMessage();
        }
    }

    @Override
    public void backMessage(String message) {
        SharedPerManager.setHostIpaddress(message);
        sendBroadcast(new Intent(AppInfo.NOTIFY_VIEW_LINE_STATUES));
    }
}
