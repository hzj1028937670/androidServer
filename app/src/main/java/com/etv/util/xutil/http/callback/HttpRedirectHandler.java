package com.etv.util.xutil.http.callback;


import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpRequestBase;

/**
 * Created with IntelliJ IDEA.
 * User: wyouflf
 * Date: 13-7-17
 * Time: 上午10:36
 */
public interface HttpRedirectHandler {
    HttpRequestBase getDirectRequest(HttpResponse response);
}
