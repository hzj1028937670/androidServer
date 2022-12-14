
package com.etv.util.xutil.http;

import com.etv.util.xutil.HttpUtils;
import com.etv.util.xutil.exception.HttpException;
import com.etv.util.xutil.http.callback.DefaultHttpRedirectHandler;
import com.etv.util.xutil.http.callback.HttpRedirectHandler;


import java.io.IOException;
import java.net.UnknownHostException;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.HttpRequestRetryHandler;
import cz.msebera.android.httpclient.client.methods.HttpRequestBase;
import cz.msebera.android.httpclient.impl.client.AbstractHttpClient;
import cz.msebera.android.httpclient.protocol.HttpContext;

public class SyncHttpHandler {

    private final AbstractHttpClient client;
    private final HttpContext context;

    private HttpRedirectHandler httpRedirectHandler;

    public void setHttpRedirectHandler(HttpRedirectHandler httpRedirectHandler) {
        this.httpRedirectHandler = httpRedirectHandler;
    }

    private String requestUrl;
    private String requestMethod;
    private String charset; // The default charset of response header info.

    private int retriedTimes = 0;

    public SyncHttpHandler(AbstractHttpClient client, HttpContext context, String charset) {
        this.client = client;
        this.context = context;
        this.charset = charset;
    }


    private long expiry = HttpCache.getDefaultExpiryTime();

    public void setExpiry(long expiry) {
        this.expiry = expiry;
    }

    public ResponseStream sendRequest(HttpRequestBase request) throws HttpException {

        HttpRequestRetryHandler retryHandler = client.getHttpRequestRetryHandler();
        while (true) {
            boolean retry = true;
            IOException exception = null;
            try {
                requestUrl = request.getURI().toString();
                requestMethod = request.getMethod();
                if (HttpUtils.sHttpCache.isEnabled(requestMethod)) {
                    String result = HttpUtils.sHttpCache.get(requestUrl);
                    if (result != null) {
                        return new ResponseStream(result);
                    }
                }

                HttpResponse response = client.execute(request, context);
                return handleResponse(response);
            } catch (UnknownHostException e) {
                exception = e;
                retry = retryHandler.retryRequest(exception, ++retriedTimes, context);
            } catch (IOException e) {
                exception = e;
                retry = retryHandler.retryRequest(exception, ++retriedTimes, context);
            } catch (NullPointerException e) {
                exception = new IOException(e.getMessage());
                exception.initCause(e);
                retry = retryHandler.retryRequest(exception, ++retriedTimes, context);
            } catch (HttpException e) {
                throw e;
            } catch (Throwable e) {
                exception = new IOException(e.getMessage());
                exception.initCause(e);
                retry = retryHandler.retryRequest(exception, ++retriedTimes, context);
            }
            if (!retry) {
                throw new HttpException(exception);
            }
        }
    }

    private ResponseStream handleResponse(HttpResponse response) throws HttpException, IOException {
        if (response == null) {
            throw new HttpException("response is null");
        }
        StatusLine status = response.getStatusLine();
        int statusCode = status.getStatusCode();
        if (statusCode < 300) {
            ResponseStream responseStream = new ResponseStream(response, charset, requestUrl, expiry);
            responseStream.setRequestMethod(requestMethod);
            return responseStream;
        } else if (statusCode == 301 || statusCode == 302) {
            if (httpRedirectHandler == null) {
                httpRedirectHandler = new DefaultHttpRedirectHandler();
            }
            HttpRequestBase request = httpRedirectHandler.getDirectRequest(response);
            if (request != null) {
                return this.sendRequest(request);
            }
        } else if (statusCode == 416) {
            throw new HttpException(statusCode, "maybe the file has downloaded completely");
        } else {
            throw new HttpException(statusCode, status.getReasonPhrase());
        }
        return null;
    }
}
