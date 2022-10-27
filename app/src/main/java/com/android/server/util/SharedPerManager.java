package com.android.server.util;

import com.android.server.ServerApplication;

public class SharedPerManager {


    String hostIpaddredd;

    public static String getHostIpaddress() {
        return ((String) ServerApplication.getInstance().getData("hostIpaddredd", ""));
    }

    public static void setHostIpaddress(String hostIpaddredd) {
        ServerApplication.getInstance().saveData("hostIpaddredd", hostIpaddredd);
    }

    public static int getScreenHeight() {
        return ((Integer) ServerApplication.getInstance().getData("screenHeight", 1080));
    }

    public static int getScreenWidth() {
        return ((Integer) ServerApplication.getInstance().getData("screenWidth", 1920));
    }

    public static void setScreenHeight(int screenHeight) {
        ServerApplication.getInstance().saveData("screenHeight", screenHeight);
    }

    public static void setScreenWidth(int screenWidth) {
        ServerApplication.getInstance().saveData("screenWidth", screenWidth);
    }

}
