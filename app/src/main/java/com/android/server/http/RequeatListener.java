package com.android.server.http;


/***
 * 网络请求接口
 */
public interface RequeatListener {
    void requestSuccess(String json);

    void requestFailed(String errorrDesc);
}