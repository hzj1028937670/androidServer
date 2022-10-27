package com.android.server.parsener;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.android.server.config.AppConfig;
import com.android.server.config.AppInfo;
import com.android.server.view.MyToastView;

import java.io.File;

/**
 * 工作分组
 */
public class PlayTaskFileParsener {

    Activity context;

    public PlayTaskFileParsener(Activity context) {
        this.context = context;
    }

    public void cutImage(String inputUrl, String outputUrl) {
        try {
            File file_out = new File(outputUrl);
            if (file_out.exists()) {
                file_out.delete();
            }
            file_out.createNewFile();
            cropPicture(inputUrl, outputUrl);
        } catch (Exception e) {
            MyToastView.getInstance().Toast(context, e.toString());
        }
    }

    /**
     * 调用系统剪裁功能
     */
    public void cropPicture(String inputUrl, String output) {
        File fileinput = new File(inputUrl);
        Uri imageUri;
        Uri outputUri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String authorities = AppConfig.authorities;
            imageUri = FileProvider.getUriForFile(context, authorities, fileinput);
            outputUri = Uri.fromFile(new File(output));
        } else {
            imageUri = Uri.fromFile(fileinput);
            outputUri = Uri.fromFile(new File(output));
        }
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        //设置宽高比例
//        intent.putExtra("aspectX", 16);
//        intent.putExtra("aspectY", 16);
        //设置裁剪图片宽高
        // intent.putExtra("outputX", 300);
        // intent.putExtra("outputY", 300);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        context.startActivityForResult(intent, AppInfo.IMAGE_CUT_BACK);
    }


}
