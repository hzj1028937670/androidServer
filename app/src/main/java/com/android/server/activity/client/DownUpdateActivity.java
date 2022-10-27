package com.android.server.activity.client;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.server.R;
import com.android.server.activity.BaseActivity;
import com.android.server.config.AppInfo;
import com.android.server.parsener.PlayTaskFileParsener;
import com.android.server.service.UdpService;
import com.android.server.util.MyLog;
import com.android.server.util.down.DownFileEntity;
import com.android.server.util.down.DownRunnable;
import com.android.server.util.down.DownStateListener;
import com.android.server.util.image.GlideLoader;
import com.android.server.util.upload.UpdateFileRunnable;
import com.android.server.util.upload.UpdateImageListener;
import com.android.server.view.MyToastView;
import com.cdl.permission.PermissionCallback;
import com.cdl.permission.PermissionItem;
import com.cdl.permission.PermissionUtil;
import com.jaiky.imagespickers.ImageConfig;
import com.jaiky.imagespickers.ImageSelector;
import com.jaiky.imagespickers.ImageSelectorActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class DownUpdateActivity extends BaseActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_update);
        initView();
    }

    Button btn_down;
    ProgressBar progressBar_down;
    Button btn_upload;
    TextView tv_speed, tv_progress;

    private void initView() {
        tv_speed = (TextView) findViewById(R.id.tv_speed);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        btn_upload = (Button) findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(this);
        btn_down = (Button) findViewById(R.id.btn_down);
        btn_down.setOnClickListener(this);
        progressBar_down = (ProgressBar) findViewById(R.id.progressBar_down);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upload:
                checkAppPermission();
                break;
            case R.id.btn_down:
                downFileFromWeb();
                break;
        }
    }


    DownRunnable runnable;

    private void downFileFromWeb() {
        String fileName = "hzj.jpg";
//        String fileName = "xxx.wmv";
        String requestUrl = AppInfo.getBaseUrImage(fileName);
        MyLog.message("=====下载的Url==" + requestUrl);
        String savePath = AppInfo.BASE_SD_PATH + "/image/" + fileName;
        runnable = new DownRunnable(requestUrl, savePath, new DownStateListener() {
            @Override
            public void downStateInfo(DownFileEntity entity) {
                int statues = entity.getDownState();
                int progress = entity.getProgress();
                int speend = entity.getDownSpeed();
                progressBar_down.setProgress(progress);
                if (statues == DownFileEntity.DOWN_STATE_PROGRESS) {
                    tv_speed.setText(speend + " kb/s");
                    tv_progress.setText(progress + "%");
                    btn_down.setText("下载中");
                } else if (statues == DownFileEntity.DOWN_STATE_SUCCESS) {
                    btn_down.setText("下载成功");
                } else if (statues == DownFileEntity.DOWN_STATE_FAIED) {
                    btn_down.setText("下载失败");
                }
                MyLog.down("====下载状态==" + entity.toString());
            }
        });
        runnable.setIsDelFile(true);
        runnable.setLimitDownSpeed(-1);
        UdpService.getInstance().executor(runnable);
    }

    public void checkAppPermission() {
        List<PermissionItem> permissonItems = new ArrayList<PermissionItem>();
        permissonItems.add(new PermissionItem(Manifest.permission.CAMERA, "拍照", R.drawable.permission_ic_storage));
        permissonItems.add(new PermissionItem(Manifest.permission.READ_EXTERNAL_STORAGE, "SD卡读", R.drawable.permission_ic_storage));
        permissonItems.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "SD卡写", R.drawable.permission_ic_storage));
        PermissionUtil.create(DownUpdateActivity.this)
                .permissions(permissonItems)
                .checkMutiPermission(new PermissionCallback() {
                    @Override
                    public void onFinish() {
                        showChooiceMediaDoalog();
                    }

                    @Override
                    public void notAllow() {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageSelector.IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data == null) {
                showToastView("当前没有选择图片");
                return;
            }
            List<String> pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
            if (pathList == null || pathList.size() < 1) {
                showToastView("当前没有选择图片");
                return;
            }
            String filePath = pathList.get(0);
            updateFileToWeb(filePath);
            //裁剪功能，需要的话可以打开

//            PlayTaskFileParsener  playTaskParsener = new PlayTaskFileParsener(DownUpdateActivity.this);
//            playTaskParsener.cropPicture(filePath, AppInfo.CACHE_IMAGE);
        } else if (requestCode == AppInfo.IMAGE_CUT_BACK && resultCode == RESULT_OK) {
            if (data == null) {
                showToastView("裁剪失败");
                return;
            }
            String filePathUpdate = AppInfo.CACHE_IMAGE;
            File file = new File(filePathUpdate);
            if (!file.exists()) {
                showToastView("裁剪文件异常");
                return;
            }
            updateFileToWeb(filePathUpdate);
        }
    }

    /**
     * 上传文件
     */
    private void updateFileToWeb(String filePath) {
        String requestUrl = AppInfo.getUpdateFileUrl();
        MyLog.down("====上传Url====" + requestUrl);
        UpdateFileRunnable runnable = new UpdateFileRunnable(requestUrl, filePath, new UpdateImageListener() {

            @Override
            public void updateImageProgress(int progress) {
                progressBar_down.setProgress(progress);
                MyLog.down("====上传进度====" + progress);
                tv_speed.setText(" ");
                tv_progress.setText(progress + "%");
            }

            @Override
            public void updateImageSuccess(String desc) {
                showToastView("上传成功");
                MyLog.down("====上传成功====" + desc);
            }
        });
        UdpService.getInstance().executor(runnable);
    }


    private void showToastView(String desc) {
        MyToastView.getInstance().Toast(DownUpdateActivity.this, desc);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (runnable != null) {
            runnable.stopDown();
        }
    }


    private void showChooiceMediaDoalog() {
        ArrayList<String> path = new ArrayList<String>();
        ImageConfig imageConfig = new ImageConfig.Builder(
                // GlideLoader 可用自己用的缓存库
                new GlideLoader())
                // 如果在 4.4 以上，则修改状态栏颜色 （默认黑色）
                .steepToolBarColor(getResources().getColor(R.color.titleBlue))
                // 标题的背景颜色 （默认黑色）
                .titleBgColor(getResources().getColor(R.color.titleBlue))
                // 提交按钮字体的颜色  （默认白色）
                .titleSubmitTextColor(getResources().getColor(R.color.white))
                // 标题颜色 （默认白色）
                .titleTextColor(getResources().getColor(R.color.white))
                // 开启多选   （默认为多选）  (单选 为 singleSelect)
                //.singleSelect()
                //裁剪
                //.crop()
                // 多选时的最大数量   （默认 9 张）
                .mutiSelectMaxSize(1)
                // 已选择的图片路径
                .pathList(path)
                // 拍照后存放的图片路径（默认 /temp/picture）
                .filePath("/temp")
                // 开启拍照功能 （默认开启）
                .showCamera()
                .requestCode(ImageSelector.IMAGE_REQUEST_CODE)
                .build();
        ImageSelector.open(DownUpdateActivity.this, imageConfig);
    }

}
