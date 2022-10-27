package com.android.server.util.upload;

import com.android.server.util.MyLog;
import com.android.server.util.SimpleDateUtil;
import com.etv.util.xutil.HttpUtils;
import com.etv.util.xutil.exception.HttpException;
import com.etv.util.xutil.http.RequestParams;
import com.etv.util.xutil.http.ResponseInfo;
import com.etv.util.xutil.http.callback.RequestCallBack;
import com.etv.util.xutil.http.client.multipart.MIME;
import com.etv.util.xutil.http.client.util.HttpMethod;

import java.io.File;

public class UpdateFileRunnable implements Runnable {

    String requestUtl;
    String filePath;
    UpdateImageListener listener;
    String fileName;

    public UpdateFileRunnable(String requestUtl, String filePath, UpdateImageListener listener) {
        String fileNameNew = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
        initInfos(requestUtl, filePath, fileNameNew, listener);
    }

    public UpdateFileRunnable(String requestUtl, String filePath, String fileName, UpdateImageListener listener) {
        initInfos(requestUtl, filePath, fileName, listener);
    }

    private void initInfos(String requestUtl, String filePath, String fileNameNew, UpdateImageListener listener) {
        this.requestUtl = requestUtl;
        this.filePath = filePath;
        this.fileName = fileNameNew;
        this.listener = listener;
    }

    @Override
    public void run() {
        photoUpload();
    }

    public void photoUpload() {
        HttpUtils utils = new HttpUtils(50000); // 设置连接超时
        RequestParams params = new RequestParams();
        String token = SimpleDateUtil.formatBig(System.currentTimeMillis()) + "";
        params.addHeader("token", token);
        //提交保存的名字
        params.addBodyParameter("fileName", fileName);
        File file = new File(filePath);
        //表单名字       表单类型
        params.addBodyParameter("files", file, MIME.ENC_BINARY);

        utils.send(HttpMethod.POST, requestUtl, params,
                new RequestCallBack() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onLoading(long total, long current,
                                          boolean isUploading) {
                        super.onLoading(total, current, isUploading);
                        int progress = (int) (current * 100 / total);
                        if (listener == null) {
                            return;
                        }
                        listener.updateImageProgress(progress);
                    }

                    @Override
                    public void onSuccess(ResponseInfo arg0) {
                        MyLog.down("上传成功===" + arg0.result.toString());
                        if (listener == null) {
                            return;
                        }
                        listener.updateImageSuccess("上传成功");
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        MyLog.down("上传失败==" + arg0 + ":" + arg1);
                        if (listener == null) {
                            return;
                        }
                        listener.updateImageSuccess("上传失败:" + arg0.toString());
                    }
                });
    }

}
