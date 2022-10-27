package com.android.server.util.net;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 获取设备相关的编码
 */

public class CodeUtil {

    /***
     * 获取设备的Mac地址
     * @return
     */
    public static String getEthMAC() {
        String macSerial = null;
        String str = "";
        try {
            Process ex = Runtime.getRuntime().exec("cat /sys/class/net/eth0/address");
            InputStreamReader ir = new InputStreamReader(ex.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            while (null != str) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return macSerial != null && !macSerial.equals("") && macSerial.length() == 17 ? macSerial.toUpperCase() : "";
    }

    /***
     * 获取IP地址
     * @param context
     * @return
     */
    public static String getIpAddress(Context context) {
        String ipAddress = "172.21.61.2";
        if (!NetWorkUtils.isNetworkConnected(context)) {
            ipAddress = "当前没有网络";
            return ipAddress;
        }
        try {
            int netType = NetWorkUtils.getNetworkState(context);
            if (netType == NetWorkUtils.NETWORN_WIFI) {
                ipAddress = WifiMgr.getInstance(context).getCurrentIpAddress();
            } else if (netType == NetWorkUtils.NETWORK_MOBILE) {    //手机2.3.4G网络
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                            ipAddress = inetAddress.getHostAddress();
                        }
                    }
                }
            } else if (netType == NetWorkUtils.NETWORK_ETH_NET) {    //以太网,先使用系统API，
                ipAddress = getEthIpAddress();
            }
        } catch (Exception e) {
            ipAddress = "获取IP失败";
            e.printStackTrace();
        }
        //防止IP获取失败
        if (TextUtils.isEmpty(ipAddress) || ipAddress == null) {
            ipAddress = "172.21.61.2";
        }
        return ipAddress;
    }

    /***
     * 获取以太网的IP地址
     * @return
     */
    private static String getEthIpAddress() {
        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return hostIp;
    }


    /***
     * 获取设备的序列号
     * @return
     */
    private static String getSerialNumber() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
    }


    /***
     * 获取固件版本号
     * @return
     */
    public static String getImgVersion() {
        String version = Build.VERSION.INCREMENTAL.toString().trim();
        if (TextUtils.isEmpty(version) || version.length() < 2) {
            return "";
        }
        version = version.substring(version.indexOf(".") + 1);
        version = version.substring(version.indexOf(".") + 1);
        return version;
    }


    /***
     * 获取系统版本的时间
     * @return
     */
    public static String getImgVersionDate() {
        String version = getImgVersion();
        return version;
    }


    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
//        String systemVersion = "4.4";   //7.0
//        int sdkVersion = android.os.Build.VERSION.SDK_INT;
//        if (sdkVersion == Build.VERSION_CODES.KITKAT) {   //19
//            systemVersion = "4.4";
//        } else if (sdkVersion == Build.VERSION_CODES.LOLLIPOP) {  //21
//            systemVersion = "5.0";
//        } else if (sdkVersion == Build.VERSION_CODES.LOLLIPOP_MR1) {  //22
//            systemVersion = "5.1";
//        } else if (sdkVersion == Build.VERSION_CODES.M) {  //23
//            systemVersion = "6.0";
//        } else if (sdkVersion == Build.VERSION_CODES.N) {  //24
//            systemVersion = "7.0";
//        } else if (sdkVersion == Build.VERSION_CODES.N_MR1) {  //25
//            systemVersion = "7.1";
//        } else if (sdkVersion == Build.VERSION_CODES.O) {  //26
//            systemVersion = "8.0";
//        } else {
//            systemVersion = "9.0";
//        }
//        return systemVersion;
        return Build.VERSION.RELEASE;
    }


}
