package com.android.server.udp;

/**
 * Created by jsjm on 2018/4/17.
 */

public interface UdpMessageListener {

    /***
     * 接收到消息
     */
    void receiveMessageState(boolean isTrue, String messageReceive);
}
