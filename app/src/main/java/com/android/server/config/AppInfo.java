package com.android.server.config;

import android.os.Environment;

import com.android.server.util.SharedPerManager;

public class AppInfo {

    public static final String CACHE_IMAGE = "/sdcard/istar.jpg";  //裁剪返回的数据
    public static final int IMAGE_CUT_BACK = 56234;  //裁剪返回的数据
    /**
     * 连接UDP成功，通知界面
     */
    public static final String NOTIFY_VIEW_LINE_STATUES = "NOTIFY_VIEW_LINE_STATUES";

    public static String getBaseUrlJson() {
        String url = "http://" + SharedPerManager.getHostIpaddress();
        url = url + ":" + AppConfig.PORT_SERVER + "/json";
        return url;
    }

    /**
     * 获取下载地址
     *
     * @param filename
     * @return
     */
    public static String getBaseUrlFile(String filename) {
        String url = "http://" + SharedPerManager.getHostIpaddress();
        url = url + ":" + AppConfig.PORT_SERVER + "/file";
        url = url + "?fileName=" + filename;
        return url;
    }

    /**
     * 上传文件的 Url
     *
     * @return
     */
    public static String getUpdateFileUrl() {
        String url = "http://" + SharedPerManager.getHostIpaddress();
        url = url + ":" + AppConfig.PORT_SERVER + "/update";
        return url;
    }


    public static String getBaseUrImage(String fileName) {
        String url = "http://" + SharedPerManager.getHostIpaddress();
        url = url + ":" + AppConfig.PORT_SERVER + "/image";
        url = url + "?fileName=" + fileName;
        return url;
    }

    public static final String BASE_PATH_INNER = Environment.getExternalStorageDirectory().getPath();
    public static final String BASE_SD_PATH = BASE_PATH_INNER + "/androidServer";
    public static final String BASE_FILE_PATH = BASE_PATH_INNER + "/androidServer/file";
    public static final String BASE_IMAGE_PATH = BASE_PATH_INNER + "/androidServer/image";
}
