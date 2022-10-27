package com.android.server.udp.parsener;

import android.content.Context;

import com.android.server.service.UdpService;
import com.android.server.udp.UdpMessageListener;
import com.android.server.udp.UdpSendMessageListener;
import com.android.server.udp.thread.UdpReceiveRunnable;
import com.android.server.udp.thread.UdpSendRunnable;
import com.android.server.udp.view.UdpMessageView;
import com.android.server.util.MyLog;
import com.android.server.util.net.CodeUtil;

import org.json.JSONObject;

public class UdpParnsener {

    private static final String TAG = "UdpParnsener";
    Context context;
    UdpMessageView udpMessageView;

    public UdpParnsener(Context context, UdpMessageView udpMessageView) {
        this.context = context;
        this.udpMessageView = udpMessageView;
    }

    /***
     * 发送消息
     * @param message
     * @param ip
     */
    public void sendUdpMessage(String message, String ip) {
        Runnable runnable = new UdpSendRunnable(message, ip, new UdpSendMessageListener() {

            @Override
            public void sendMessageState(boolean isSuccess) {
            }
        });
        UdpService.getInstance().executor(runnable);
    }

    UdpReceiveRunnable receiverunnable;

    public void receiveMessage() {
        receiverunnable = new UdpReceiveRunnable(new UdpMessageListener() {
            @Override
            public void receiveMessageState(boolean isTrue, String messageReceive) {
                MyLog.message("=======接收到消息parsener000==" + isTrue + " / " + messageReceive);
                if (isTrue) {
                    parsenerJsonInfo(messageReceive);
                }
            }
        });
        UdpService.getInstance().executor(receiverunnable);
    }

    private void parsenerJsonInfo(String messageReceive) {
        try {
            JSONObject jsonObject = new JSONObject(messageReceive);
            String type = jsonObject.getString("type");
            String sendIp = jsonObject.getString("ipaddress");
            String localIpaddress = CodeUtil.getIpAddress(context);
            if (sendIp.contains(localIpaddress)) {
                MyLog.message("========屏蔽自己发送的消息==");
                return;
            }
            MyLog.message("========解析消息==" + type + " / " + sendIp);
            if (type.contains("getLocalDev")) { //终端发给服务器
                backMineIpaddress(sendIp);
            } else if (type.contains("submitDev")) { //服务器返回的指令
                MyLog.message("========服务器返回来的==" + sendIp);
                if (udpMessageView == null) {
                    MyLog.message("========服务器返回来的udpMessageView == null==");
                    return;
                }
                udpMessageView.backMessage(sendIp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 接收到设备发来的请求设备的消息 ，
     * 这里把自己的ip地址发回去，
     * 用来列表显示
     * @param ipaddress
     */
    private void backMineIpaddress(String ipaddress) {
        String ipAddress_mine = CodeUtil.getIpAddress(context);
        String sendMessage = "{\"type\":\"submitDev\",\"ipaddress\":\"" + ipAddress_mine + "\"}";
        sendUdpMessage(sendMessage, ipaddress);
    }

    public void stopReceiveMessage() {
        if (receiverunnable != null) {
            receiverunnable.stopReceiveMessage();
        }
    }
}
