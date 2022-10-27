package com.etv.util.xutil.http.client.entity;

//import com.android.server.entity.FileEntity;
import com.etv.util.xutil.http.callback.RequestCallBackHandler;

import org.apache.commons.io.IOUtils;


import java.io.*;

import cz.msebera.android.httpclient.entity.FileEntity;

/**
 * Created with IntelliJ IDEA.
 * User: wyouflf
 * Date: 13-6-24
 * Time: 下午4:45
 */
public class FileUploadEntity extends FileEntity implements UploadEntity {

    public FileUploadEntity(File file, String contentType) {
        super(file, contentType);
        fileSize = file.length();
    }

    private long fileSize;
    private long uploadedSize = 0;

    @Override
    public void writeTo(OutputStream outStream) throws IOException {
        if (outStream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        BufferedInputStream inStream = null;
        try {
            inStream = new BufferedInputStream(new FileInputStream(this.file));
            byte[] tmp = new byte[4096];
            int len;
            while ((len = inStream.read(tmp)) != -1) {
                outStream.write(tmp, 0, len);
                uploadedSize += len;
                if (callBackHandler != null) {
                    if (!callBackHandler.updateProgress(fileSize, uploadedSize, false)) {
                        throw new InterruptedIOException("cancel");
                    }
                }
            }
            outStream.flush();
            if (callBackHandler != null) {
                callBackHandler.updateProgress(fileSize, uploadedSize, true);
            }
        } finally {
            IOUtils.closeQuietly(inStream);
        }
    }

    private RequestCallBackHandler callBackHandler = null;

    @Override
    public void setCallBackHandler(RequestCallBackHandler callBackHandler) {
        this.callBackHandler = callBackHandler;
    }
}