package com.etv.util.xutil.http.client.entity;


import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;


public class GZipDecompressingEntity extends DecompressingEntity {

    /**
     * Creates a new {@link GZipDecompressingEntity} which will wrap the specified
     * {@link cz.msebera.android.httpclient.HttpEntity}.
     *
     * @param entity the non-null {@link org.apache.http.HttpEntity} to be wrapped
     */
    public GZipDecompressingEntity(final HttpEntity entity) {
        super(entity);
    }

    @Override
    InputStream decorate(final InputStream wrapped) throws IOException {
        return new GZIPInputStream(wrapped);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Header getContentEncoding() {

        /* This HttpEntityWrapper has dealt with the Content-Encoding. */
        return null;
    }
}
