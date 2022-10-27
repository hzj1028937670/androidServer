package com.android.server.util.handler;

import com.android.server.config.AppInfo;
import com.yanzhenjie.andserver.RequestMethod;
import com.yanzhenjie.andserver.SimpleRequestHandler;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.util.HttpRequestParser;
import com.yanzhenjie.andserver.view.View;

import org.apache.httpcore.HttpEntity;
import org.apache.httpcore.HttpException;
import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.entity.ContentType;
import org.apache.httpcore.entity.FileEntity;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static com.yanzhenjie.andserver.util.FileUtils.getMimeType;


/**
 * 访问服务器图片
 */
public class DownloadImageHandler extends SimpleRequestHandler {

    @RequestMapping(method = {RequestMethod.GET})
    @Override
    protected View handle(HttpRequest request) throws HttpException, IOException {
        String content = HttpRequestParser.getContentFromUri(request);  //body      形式请求
        if (content == null || content.length() < 2) {
            return null;
        }
        if (!content.contains("fileName")) {
            return null;
        }
        String fileName = content.substring(content.lastIndexOf("=") + 1, content.length());
        String filePath = AppInfo.BASE_IMAGE_PATH + "/" + fileName;
        File file = new File(filePath);
        if (file.exists()) {
            HttpEntity httpEntity = new FileEntity(file, ContentType.create(getMimeType(file.getAbsolutePath()), Charset.defaultCharset()));
            return new View(200, httpEntity);
        }
        return null;
    }
}