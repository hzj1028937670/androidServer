package com.android.server;

import android.app.Application;
import android.content.SharedPreferences;

import com.android.server.util.FileUtil;

import org.litepal.LitePal;

/**
 * 作者：leavesC
 * 时间：2018/4/5 16:30
 * 描述：https://github.com/leavesC/AndroidServer
 * https://www.jianshu.com/u/9df45b87cfdf
 */
public class ServerApplication extends Application {

    private static ServerApplication sInstance;
    private static SharedPreferences mSharedPreferences;
    public static String USER_INFO = "ANDROID_SERVER_SHARE";
    public String SERVER_IP;  //服务器IP地址


    @Override
    public void onCreate() {
        super.onCreate();
        if (sInstance == null) {
            sInstance = this;
        }
        initOther();
    }

    public String getSERVER_IP() {
        return SERVER_IP;
    }

    public void setSERVER_IP(String SERVER_IP) {
        this.SERVER_IP = SERVER_IP;
    }


    private void initOther() {
        FileUtil.creatPathNotExcit();
        mSharedPreferences = getSharedPreferences(USER_INFO, 0);
        LitePal.initialize(this); //数据库初始化
    }

    public static ServerApplication getInstance() {
        return sInstance;
    }

    public void saveData(String key, Object data) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        try {
            if (data instanceof Integer) {
                editor.putInt(key, (Integer) data);
            } else if (data instanceof Boolean) {
                editor.putBoolean(key, (Boolean) data);
            } else if (data instanceof String) {
                editor.putString(key, (String) data);
            } else if (data instanceof Float) {
                editor.putFloat(key, (Float) data);
            } else if (data instanceof Long) {
                editor.putLong(key, (Long) data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean isSave = editor.commit();
    }

    public Object getData(String key, Object defaultObject) {
        try {
            if (defaultObject instanceof String) {
                return mSharedPreferences.getString(key, (String) defaultObject);
            } else if (defaultObject instanceof Integer) {
                return mSharedPreferences.getInt(key, (Integer) defaultObject);
            } else if (defaultObject instanceof Boolean) {
                return mSharedPreferences.getBoolean(key, (Boolean) defaultObject);
            } else if (defaultObject instanceof Float) {
                return mSharedPreferences.getFloat(key, (Float) defaultObject);
            } else if (defaultObject instanceof Long) {
                return mSharedPreferences.getLong(key, (Long) defaultObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
