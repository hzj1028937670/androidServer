package com.android.server.util;

import android.util.Log;

public class MyLog {
    public static void message(String s) {
        Log.e("message", s);
    }

    public static void http(String s) {
        Log.e("http", s);
    }

    public static void down(String s) {
        Log.e("down", s);
    }

    public static void cdl(String s) {
        Log.e("cdl", s);
    }

    public static void db(String desc) {
        Log.e("db", desc);
    }
}
