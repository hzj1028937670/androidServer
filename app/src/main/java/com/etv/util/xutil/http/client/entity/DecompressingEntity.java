package com.etv.util.xutil.http.client.entity;

import com.etv.util.xutil.http.callback.RequestCallBackHandler;
import org.apache.commons.io.IOUtils;


import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.HttpEntityWrapper;

/**
 * Common base class for decompressing {@link org.apache.http.HttpEntity} implementations.
 *
 * @since 4.1
 */
abstract class DecompressingEntity extends HttpEntityWrapper implements UploadEntity {

    /**
     * {@link #getContent()} method must return the same {@link InputStream}
     * instance when DecompressingEntity is wrapping a streaming entity.
     */
    private InputStream content;

    /**
     * Creates a new {@link DecompressingEntity}.
     *
     * @param wrapped the non-null {@link org.apache.http.HttpEntity} to be wrapped
     */
    public DecompressingEntity(final HttpEntity wrapped) {
        super(wrapped);
        this.uncompressedLength = wrapped.getContentLength();
    }

    abstract InputStream decorate(final InputStream wrapped) throws IOException;

    private InputStream getDecompressingStream() throws IOException {
        InputStream in = null;
        try {
            in = wrappedEntity.getContent();
            return decorate(in);
        } catch (IOException ex) {
            IOUtils.closeQuietly(in);
            throw ex;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getContent() throws IOException {
        if (wrappedEntity.isStreaming()) {
            if (content == null) {
                content = getDecompressingStream();
            }
            return content;
        } else {
            return getDecompressingStream();
        }
    }

    private long uncompressedLength;

    @Override
    public long getContentLength() {
        /* length of compressed content is not known */
        return -1;
    }

    private long uploadedSize = 0;

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeTo(OutputStream outStream) throws IOException {
        if (outStream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        InputStream inStream = null;
        try {
            inStream = getContent();

            byte[] tmp = new byte[4096];
            int len;
            while ((len = inStream.read(tmp)) != -1) {
                outStream.write(tmp, 0, len);
                uploadedSize += len;
                if (callBackHandler != null) {
                    if (!callBackHandler.updateProgress(uncompressedLength, uploadedSize, false)) {
                        throw new InterruptedIOException("cancel");
                    }
                }
            }
            outStream.flush();
            if (callBackHandler != null) {
                callBackHandler.updateProgress(uncompressedLength, uploadedSize, true);
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

    public abstract Header getContentEncoding();
}
