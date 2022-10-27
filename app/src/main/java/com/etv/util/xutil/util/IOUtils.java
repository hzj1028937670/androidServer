//package com.etv.util.xutil.util;
//
//import android.database.Cursor;
//
//import java.io.Closeable;
//
///**
// * Author: wyouflf
// * Date: 13-8-26
// * Time: 下午6:02
// */
//public class IOUtils {
//
//    private IOUtils() {
//    }
//
//    public static void closeQuietly(Closeable closeable) {
//        if (closeable != null) {
//            try {
//                closeable.close();
//            } catch (Throwable e) {
//            }
//        }
//    }
//
//    public static void closeQuietly(Cursor cursor) {
//        if (cursor != null) {
//            try {
//                cursor.close();
//            } catch (Throwable e) {
//            }
//        }
//    }
//}
