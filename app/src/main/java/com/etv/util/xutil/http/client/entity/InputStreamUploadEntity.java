package com.etv.util.xutil.http.client.entity;

import com.etv.util.xutil.http.callback.RequestCallBackHandler;
import org.apache.commons.io.IOUtils;


import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;

import cz.msebera.android.httpclient.entity.AbstractHttpEntity;

/**
 * Created with IntelliJ IDEA.
 * User: wyouflf
 * Date: 13-6-28
 * Time: 上午12:14
 */
public class InputStreamUploadEntity extends AbstractHttpEntity implements UploadEntity {

    private final static int BUFFER_SIZE = 2048;

    private final InputStream content;
    private final long length;

    public InputStreamUploadEntity(final InputStream inputStream, long length) {
        super();
        if (inputStream == null) {
            throw new IllegalArgumentException("Source input stream may not be null");
        }
        this.content = inputStream;
        this.length = length;
    }

    public boolean isRepeatable() {
        return false;
    }

    public long getContentLength() {
        return this.length;
    }

    public InputStream getContent() throws IOException {
        return this.content;
    }

    private long uploadedSize = 0;

    public void writeTo(final OutputStream outStream) throws IOException {
        if (outStream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        InputStream inStream = this.content;
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            int l;
            if (this.length < 0) {
                // consume until EOF
                while ((l = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, l);
                    uploadedSize += l;
                    if (callBackHandler != null) {
                        if (!callBackHandler.updateProgress(uploadedSize + 1, uploadedSize, false)) {
                            throw new InterruptedIOException("cancel");
                        }
                    }
                }
            } else {
                // consume no more than length
                long remaining = this.length;
                while (remaining > 0) {
                    l = inStream.read(buffer, 0, (int) Math.min(BUFFER_SIZE, remaining));
                    if (l == -1) {
                        break;
                    }
                    outStream.write(buffer, 0, l);
                    remaining -= l;
                    uploadedSize += l;
                    if (callBackHandler != null) {
                        if (!callBackHandler.updateProgress(length, uploadedSize, false)) {
                            throw new InterruptedIOException("cancel");
                        }
                    }
                }
            }
            outStream.flush();
            if (callBackHandler != null) {
                callBackHandler.updateProgress(length, uploadedSize, true);
            }
        } finally {
            IOUtils.closeQuietly(inStream);
        }
    }

    public boolean isStreaming() {
        return true;
    }

    /**
     * @deprecated Either use {@link #getContent()} and call {@link InputStream#close()} on that;
     * otherwise call {@link #writeTo(OutputStream)} which is required to free the resources.
     */
    public void consumeContent() throws IOException {
        // If the input stream is from a connection, closing it will read to
        // the end of the content. Otherwise, we don't care what it does.
        this.content.close();
    }

    private RequestCallBackHandler callBackHandler = null;

    @Override
    public void setCallBackHandler(RequestCallBackHandler callBackHandler) {
        this.callBackHandler = callBackHandler;
    }
}