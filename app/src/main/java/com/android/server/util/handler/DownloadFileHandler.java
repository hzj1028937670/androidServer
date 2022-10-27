package com.android.server.util.handler;

import com.android.server.config.AppInfo;
import com.yanzhenjie.andserver.RequestHandler;
import com.yanzhenjie.andserver.RequestMethod;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.util.HttpRequestParser;

import org.apache.httpcore.HttpEntity;
import org.apache.httpcore.HttpException;
import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.HttpResponse;
import org.apache.httpcore.entity.ContentType;
import org.apache.httpcore.entity.FileEntity;
import org.apache.httpcore.entity.StringEntity;
import org.apache.httpcore.protocol.HttpContext;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static com.yanzhenjie.andserver.util.FileUtils.getMimeType;

public class DownloadFileHandler implements RequestHandler {
    HttpResponse httpResponse;

    @RequestMapping(method = {RequestMethod.GET})
    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
        this.httpResponse = httpResponse;
        String content = HttpRequestParser.getContentFromUri(httpRequest);  //body      形式请求
        parsenerJsonContent(content);
    }

    private void parsenerJsonContent(String content) {
        if (content == null || content.length() < 2) {
            response(-1, "没有参数", httpResponse);
            return;
        }
        if (!content.contains("fileName")) {
            response(-1, "请输入合法参数:fileName", httpResponse);
            return;
        }
        String fileName = content.substring(content.lastIndexOf("=") + 1, content.length());
        boolean isFileExict = jujleFileIsExict(fileName);
        if (isFileExict) {
            startToDownFile(fileName);
        } else {
            response(-1, "下载文件不存在", httpResponse);
        }
    }


    private void response(int responseCode, String message, HttpResponse response) {
        try {
            response.setStatusCode(responseCode);
            response.setEntity(new StringEntity(message, "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startToDownFile(String fileName) {
        String filePathDown = AppInfo.BASE_FILE_PATH + "/" + fileName;
        File file = new File(filePathDown);
        HttpEntity httpEntity = new FileEntity(file, ContentType.create(getMimeType(file.getAbsolutePath()), Charset.defaultCharset()));
        httpResponse.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
        httpResponse.setStatusCode(200);
        httpResponse.setEntity(httpEntity);
    }

    private boolean jujleFileIsExict(String fileName) {
        String filePath = AppInfo.BASE_FILE_PATH;
        File fileDir = new File(filePath);
        File[] fileList = fileDir.listFiles();
        if (fileList == null || fileList.length < 1) {
            return false;
        }
        for (File file : fileList) {
            String name = file.getName();
            if (name.contains(fileName)) {
                return true;
            }
        }
        return false;
    }
}
