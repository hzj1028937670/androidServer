package com.etv.util.xutil.http.client.multipart.content;


import com.etv.util.xutil.http.client.multipart.MultipartEntity;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @since 4.0
 */
public interface ContentBody extends ContentDescriptor {

    String getFilename();

    void writeTo(OutputStream out) throws IOException;

    void setCallBackInfo(MultipartEntity.CallBackInfo callBackInfo);

}
