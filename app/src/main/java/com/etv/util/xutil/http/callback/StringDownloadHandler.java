package com.etv.util.xutil.http.callback;

import org.apache.commons.io.IOUtils;

import com.etv.util.xutil.util.OtherUtils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cz.msebera.android.httpclient.HttpEntity;

public class StringDownloadHandler {

    public String handleEntity(HttpEntity entity, RequestCallBackHandler callBackHandler, String charset) throws IOException {
        if (entity == null) return null;

        long current = 0;
        long total = entity.getContentLength();

        if (callBackHandler != null && !callBackHandler.updateProgress(total, current, true)) {
            return null;
        }

        InputStream inputStream = null;
        StringBuilder sb = new StringBuilder();
        try {
            inputStream = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
                current += OtherUtils.sizeOfString(line, charset);
                if (callBackHandler != null) {
                    if (!callBackHandler.updateProgress(total, current, false)) {
                        break;
                    }
                }
            }
            if (callBackHandler != null) {
                callBackHandler.updateProgress(total, current, true);
            }
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return sb.toString().trim();
    }

}
