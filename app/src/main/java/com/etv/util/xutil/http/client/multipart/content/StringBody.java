package com.etv.util.xutil.http.client.multipart.content;

import com.etv.util.xutil.http.client.multipart.MIME;


import java.io.*;
import java.nio.charset.Charset;

import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * @since 4.0
 */
public class StringBody extends AbstractContentBody {

    private final byte[] content;
    private final Charset charset;

    /**
     * @since 4.1
     */
    public static StringBody create(
            final String text,
            final String mimeType,
            final Charset charset) throws IllegalArgumentException {
        try {
            return new StringBody(text, mimeType, charset);
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalArgumentException("Charset " + charset + " is not supported", ex);
        }
    }

    /**
     * @since 4.1
     */
    public static StringBody create(
            final String text, final Charset charset) throws IllegalArgumentException {
        return create(text, null, charset);
    }

    /**
     * @since 4.1
     */
    public static StringBody create(final String text) throws IllegalArgumentException {
        return create(text, null, null);
    }

    /**
     * Create a StringBody from the specified text, mime type and character set.
     *
     * @param text     to be used for the body, not {@code null}
     * @param mimeType the mime type, not {@code null}
     * @param charset  the character set, may be {@code null}, in which case the UTF-8 charset is used
     * @throws UnsupportedEncodingException
     *
     * @throws IllegalArgumentException if the {@code text} parameter is null
     */
    public StringBody(
            final String text,
            final String mimeType,
            Charset charset) throws UnsupportedEncodingException {
        super(mimeType);
        if (text == null) {
            throw new IllegalArgumentException("Text may not be null");
        }
        if (charset == null) {
            charset = Charset.forName(HTTP.UTF_8);
        }
        this.content = text.getBytes(charset.name());
        this.charset = charset;
    }

    /**
     * Create a StringBody from the specified text and character set.
     * The mime type is set to "text/plain".
     *
     * @param text    to be used for the body, not {@code null}
     * @param charset the character set, may be {@code null}, in which case the UTF-8 charset is used
     * @throws UnsupportedEncodingException
     *
     * @throws IllegalArgumentException if the {@code text} parameter is null
     */
    public StringBody(final String text, final Charset charset) throws UnsupportedEncodingException {
        this(text, "text/plain", charset);
    }

    /**
     * Create a StringBody from the specified text.
     * The mime type is set to "text/plain".
     * The hosts default charset is used.
     *
     * @param text to be used for the body, not {@code null}
     * @throws UnsupportedEncodingException
     *
     * @throws IllegalArgumentException if the {@code text} parameter is null
     */
    public StringBody(final String text) throws UnsupportedEncodingException {
        this(text, "text/plain", null);
    }

    public Reader getReader() {
        return new InputStreamReader(
                new ByteArrayInputStream(this.content),
                this.charset);
    }

    public void writeTo(final OutputStream out) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        InputStream in = new ByteArrayInputStream(this.content);
        byte[] tmp = new byte[4096];
        int l;
        while ((l = in.read(tmp)) != -1) {
            out.write(tmp, 0, l);
            callBackInfo.pos += l;
            if (!callBackInfo.doCallBack(false)) {
                throw new InterruptedIOException("cancel");
            }
        }
        out.flush();
    }

    public String getTransferEncoding() {
        return MIME.ENC_8BIT;
    }

    public String getCharset() {
        return this.charset.name();
    }

    public long getContentLength() {
        return this.content.length;
    }

    public String getFilename() {
        return null;
    }

}
