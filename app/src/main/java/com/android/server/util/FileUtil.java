package com.android.server.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import com.android.server.config.AppInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    public static final String TAG = "FileUtil";

    //删除目录或者文件
    public static boolean deleteDirOrFile(String Path) {
        return deleteDirOrFile(new File(Path));
    }

    public static boolean deleteDirOrFile(File file) {
        if (file.isFile()) {
            file.delete();
            return false;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return false;
            }
            for (File f : childFile) {
                deleteDirOrFile(f);
            }
            file.delete();
        }
        FileUtil.creatPathNotExcit();
        return false;
    }


    public static void MKDIRSfILE(String path) {
        File file = null;
        try {
            file = new File(path);
            if (!file.exists()) {
                boolean isCreate = file.mkdirs();
                Log.i("MKDIRSfILE",isCreate+"");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void creatPathNotExcit() {
        try {
            List<String> listsPath = new ArrayList<String>();
            listsPath.add(AppInfo.BASE_SD_PATH);
            listsPath.add(AppInfo.BASE_FILE_PATH);
            listsPath.add(AppInfo.BASE_IMAGE_PATH);
            for (int i = 0; i < listsPath.size(); i++) {
                String filePath = listsPath.get(i).toString();
                File file = new File(filePath);
                if (!file.exists()) {
                    boolean isCreate = file.mkdirs();
                    Log.e("cdl", "==========" + isCreate + "/" + filePath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String getTxtInfoFromTxtFile(String filePath) {
        return getTxtInfoFromTxtFile(filePath, TYPE_UTF_8);
    }

    /***
     * 从文本中获取txt信息
     * @param filePath
     * @type type
     * @return
     */
    public static final String TYPE_UTF_8 = "UTF-8";
    public static final String TYPE_GBK = "GBK";

    public static String getTxtInfoFromTxtFile(String filePath, String type) {
        String result = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return result;
            }
            int length = (int) file.length();
            byte[] buff = new byte[length];
            FileInputStream fin = new FileInputStream(file);
            fin.read(buff);
            fin.close();
            result = new String(buff, type);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String FormetFileSize(long paramLong) {
        Object localObject = new DecimalFormat("#.00");
        if (paramLong == 0L) {
            return "0B";
        }
        if (paramLong < 1024L) {
            localObject = ((DecimalFormat) localObject).format(paramLong) + "B";
        }
        if (paramLong < 1048576L) {
            localObject = ((DecimalFormat) localObject).format(paramLong / 1024.0D) + "KB";
        } else if (paramLong < 1073741824L) {
            localObject = ((DecimalFormat) localObject).format(paramLong / 1048576.0D) + "MB";
        } else {
            localObject = ((DecimalFormat) localObject).format(paramLong / 1073741824.0D) + "GB";
        }
        return (String) localObject;
    }

    public static Bitmap ResizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        bitmap.recycle();
        return resizedBitmap;
    }

    public static Bitmap ResizeBitmap(Bitmap bitmap, int scale) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(1 / scale, 1 / scale);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        bitmap.recycle();
        return resizedBitmap;
    }


    /**
     * @param bitmap
     * @param destPath
     * @param quality
     */
    public static void writeImage(Bitmap bitmap, String destPath, int quality) {
        try {
            deleteDirOrFile(destPath);
            if (createFile(destPath)) {
                FileOutputStream out = new FileOutputStream(destPath);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)) {
                    out.flush();
                    out.close();
                    out = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static boolean createFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                return file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


}
