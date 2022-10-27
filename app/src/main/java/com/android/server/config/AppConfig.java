package com.android.server.config;

public class AppConfig {

    public static final int APP_MODEL_SERVER = 0;
    public static final int APP_MODEL_CLIENT = 1;
    //APP运行的版本
    public static int APP_MODEL = APP_MODEL_CLIENT;
    //UDP端口号
    public final static int UDP_PORT = 8953;

    //服务端接口的端口号
    public static final int PORT_SERVER = 1995;

    public static final String GET_FILE = "/file";
    public static final String GET_IMAGE = "/image";
    public static final String POST_JSON = "/json";
    public static final String UPDATE_FILE = "/update";


    public static String authorities = "com.android.server.fileprovider";
}
