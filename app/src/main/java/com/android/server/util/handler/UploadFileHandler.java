package com.android.server.util.handler;

import com.android.server.config.AppInfo;
import com.android.server.db.UserDbUtil;
import com.android.server.util.MyLog;
import com.yanzhenjie.andserver.RequestHandler;
import com.yanzhenjie.andserver.upload.HttpFileUpload;
import com.yanzhenjie.andserver.upload.HttpUploadContext;
import com.yanzhenjie.andserver.util.HttpRequestParser;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.httpcore.HttpEntityEnclosingRequest;
import org.apache.httpcore.HttpException;
import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.HttpResponse;
import org.apache.httpcore.entity.StringEntity;
import org.apache.httpcore.protocol.HttpContext;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class UploadFileHandler implements RequestHandler {

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {
        MyLog.message("===========接受到上传请求========");
        if (!HttpRequestParser.isMultipartContentRequest(request)) { // 是否Form传文件的请求。
            response(403, "说好的文件呢", response);
        } else {
            try {
                processFileUpload(request);
                response(200, "上传成功", response);
            } catch (Exception e) {
                response(500, "保存文件失败", response);
            }
        }
    }

    private void response(int responseCode, String message, HttpResponse response)
            throws HttpException, IOException {
        response.setStatusCode(responseCode);
        response.setEntity(new StringEntity(message, "utf-8"));
    }

    /**
     * 保存文件和参数处理。
     */
    private void processFileUpload(HttpRequest request) throws Exception {
        String fulePath = AppInfo.BASE_FILE_PATH;
        File mDirectory = new File(fulePath);
        FileItemFactory factory = new DiskFileItemFactory(1024 * 1024, mDirectory);
        HttpFileUpload fileUpload = new HttpFileUpload(factory);

        fileUpload.setProgressListener(new ProgressListener() {
            @Override
            public void update(long var1, long var2, int i) {
                int progress = (int) (var1 * 100 / var2);
//                MyLog.message("===上传进度===" + progress);
            }
        });

        try {
            HttpUploadContext context = new HttpUploadContext((HttpEntityEnclosingRequest) request);
            List<FileItem> fileItems = fileUpload.parseRequest(context);
            String fileName = "";
            for (FileItem fileItem : fileItems) {
                if (fileItem.isFormField()) { // 普通参数。
                    String key = fileItem.getName();      // 表单参数名。
                    fileName = fileItem.getString();  // 表单参数值。
                    MyLog.message("====提交的参数==" + key + " / " + fileName);
                } else { // 文件。
                    String formName = fileItem.getFieldName();           // 表单参数名。
                    long fileSize = fileItem.getSize();                // 文件大小。
                    String fileType = fileItem.getContentType();         // 文件的MimeType。
                    if (fileName == null || fileName.length() < 2) {
                        //文件名字，如果用户没有提交参数，就用源文件名字，如果提交的新文件名字，就使用新文件名字用来保存
                        fileName = fileItem.getName();                // 源文件名字
                    }
                    MyLog.message("====提交的文件参数大小==" + fileSize + " /类型： " + fileType + " /名字： " + fileName + " /表单名字： " + formName);
                    File uploadedFile = new File(mDirectory, fileName);
                    fileItem.write(uploadedFile);
                    boolean isSaveUrl = UserDbUtil.addUserImageUrl(fileName);
                    MyLog.message("===添加头像返回结果===" + isSaveUrl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}