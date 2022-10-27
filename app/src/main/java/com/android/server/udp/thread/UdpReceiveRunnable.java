package com.android.server.udp.thread;

import com.android.server.config.AppConfig;
import com.android.server.udp.UdpMessageListener;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpReceiveRunnable implements Runnable {

    private DatagramSocket receiveSocket;
    private boolean listenStatus = true; // 接收线程的循环标识
    UdpMessageListener listener;

    public UdpReceiveRunnable(UdpMessageListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            receiveSocket = new DatagramSocket(AppConfig.UDP_PORT);
            while (listenStatus) {
                byte[] inBuf = new byte[1024];
                DatagramPacket inPacket = new DatagramPacket(inBuf, inBuf.length);
                receiveSocket.receive(inPacket);
                byte[] receiveInfo = inPacket.getData();
                backMessage(true, receiveInfo);
            }
        } catch (final Exception e) {
            backageErrorMessage(e.toString());
            e.printStackTrace();
        }
    }

    private void backageErrorMessage(String s) {
        if (listener != null) {
            listener.receiveMessageState(false, s);
        }
    }

    private void backMessage(boolean isTrue, byte[] receiveInfo) {
        if (listener != null) {
            String messageBack = new String(receiveInfo);
            listener.receiveMessageState(isTrue, messageBack);
        }
    }

    /***
     * 停止接收消息
     */
    public void stopReceiveMessage() {
        try {
            listenStatus = false;
            receiveSocket.close();
        } catch (Exception e) {

        }
    }
}
