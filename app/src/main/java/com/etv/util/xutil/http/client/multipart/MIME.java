package com.etv.util.xutil.http.client.multipart;


import java.nio.charset.Charset;

import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * @since 4.0
 */
public class MIME {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TRANSFER_ENC = "Content-Transfer-Encoding";
    public static final String CONTENT_DISPOSITION = "Content-Disposition";

    public static final String ENC_8BIT = "8bit";
    public static final String ENC_BINARY = "binary";

    /**
     * The default character set to be used, i.e. "UTF-8"
     */
    public static final Charset DEFAULT_CHARSET = Charset.forName(HTTP.UTF_8);

}
