package com.android.server.udp.thread;

import com.android.server.config.AppConfig;
import com.android.server.udp.UdpSendMessageListener;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class UdpSendRunnable implements Runnable {

    private DatagramSocket sendSocket;
    private InetAddress serverAddr;
    UdpSendMessageListener listener;
    String jsonSend;
    String sendIp;

    public UdpSendRunnable(String jsonSend, String sendIp, UdpSendMessageListener listener) {
        this.listener = listener;
        this.jsonSend = jsonSend;
        this.sendIp = sendIp;
    }

    @Override
    public void run() {
        try {
            byte[] buf = jsonSend.getBytes();
            sendSocket = new DatagramSocket();
            serverAddr = InetAddress.getByName(sendIp);
            DatagramPacket outPacket = new DatagramPacket(buf, buf.length, serverAddr, AppConfig.UDP_PORT);
            sendSocket.send(outPacket);
            sendSocket.close();
            backMessageState(true);
        } catch (Exception e) {
            backMessageState(false);
            e.printStackTrace();
        }
    }

    private void backMessageState(final boolean b) {
        if (listener != null) {
            listener.sendMessageState(b);
        }
    }
}