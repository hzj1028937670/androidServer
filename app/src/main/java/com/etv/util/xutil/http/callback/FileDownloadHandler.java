package com.etv.util.xutil.http.callback;

import android.text.TextUtils;


import org.apache.commons.io.IOUtils;

import java.io.*;

import cz.msebera.android.httpclient.HttpEntity;

public class FileDownloadHandler {

    public File handleEntity(HttpEntity entity,
                             RequestCallBackHandler callBackHandler,
                             String target,
                             boolean isResume,
                             String responseFileName,
                             int LimitSpeed) throws IOException {
        if (entity == null || TextUtils.isEmpty(target)) {
            return null;
        }

        File targetFile = new File(target);

        if (!targetFile.exists()) {
            File dir = targetFile.getParentFile();
            if (dir.exists() || dir.mkdirs()) {
                targetFile.createNewFile();
            }
        }

        long current = 0;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            FileOutputStream fileOutputStream = null;
            if (isResume) {
                current = targetFile.length();
                fileOutputStream = new FileOutputStream(target, true);
            } else {
                fileOutputStream = new FileOutputStream(target);
            }
            long total = entity.getContentLength() + current;
            bis = new BufferedInputStream(entity.getContent());
            bos = new BufferedOutputStream(fileOutputStream);

            if (callBackHandler != null && !callBackHandler.updateProgress(total, current, true)) {
                return targetFile;
            }
            byte[] tmp = new byte[4096];
            int len;
            while ((len = bis.read(tmp)) != -1) {
                bos.write(tmp, 0, len);
                current += len;
                if (callBackHandler != null) {
                    if (!callBackHandler.updateProgress(total, current, false)) {
                        return targetFile;
                    }
                }
                if (LimitSpeed > 0) {     // 开启限速模式
                    int sleepTime = (190 * 20) / LimitSpeed;
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            bos.flush();
            if (callBackHandler != null) {
                callBackHandler.updateProgress(total, current, true);
            }
        } finally {
            IOUtils.closeQuietly(bis);
            IOUtils.closeQuietly(bos);
        }

        if (targetFile.exists() && !TextUtils.isEmpty(responseFileName)) {
            File newFile = new File(targetFile.getParent(), responseFileName);
            while (newFile.exists()) {
                newFile = new File(targetFile.getParent(), System.currentTimeMillis() + responseFileName);
            }
            return targetFile.renameTo(newFile) ? newFile : targetFile;
        } else {
            return targetFile;
        }
    }

}
