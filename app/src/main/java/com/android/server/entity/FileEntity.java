package com.android.server.entity;

/**
 * Created by reeman on 2017/5/27.
 */

public class FileEntity {
    int imageId;
    String fileName;
    long fileSise;  //文件的大小
    String filePath;
    int fileStyle;
    public static int FILE_STYLE_DIR = 1;
    public static int FILE_STYLE_FILE = 2;

    int STYLE_FILE;
    /**
     * 图片文件
     */
    public static final int STYLE_FILE_IMAGE = 0;
    /**
     * 音频文件
     */
    public static final int STYLE_FILE_MUSIC = 1;
    /**
     * 视频文件
     */
    public static final int STYLE_FILE_VIDEO = 2;

    /**
     * ZIP文件
     */
    public static final int STYLE_FILE_ZIP = STYLE_FILE_VIDEO + 1;
    /**
     * apk文件
     */
    public static final int STYLE_FILE_APK = STYLE_FILE_ZIP + 1;
    /**
     * ppt文件
     */
    public static final int STYLE_FILE_PPT = STYLE_FILE_APK + 1;
    /**
     * WORD 文档
     */
    public static final int STYLE_FILE_DOC = STYLE_FILE_PPT + 1;
    /**
     * doc文档
     */
    public static final int STYLE_FILE_EXCEL = STYLE_FILE_DOC + 1;

    /**
     * PDF文档
     */
    public static final int STYLE_FILE_PDF = STYLE_FILE_EXCEL + 1;
    /***
     * txt文档
     */
    public static final int STYLE_FILE_TXT = STYLE_FILE_PDF + 1;
    /***
     * 其他類型的文件
     */
    public static final int STYLE_FILE_OTHER = STYLE_FILE_TXT + 1;

    public FileEntity() {

    }

    //文件夹
    public FileEntity(int imageId, String fileName, String filePath, int fileStyle) {
        this.imageId = imageId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileStyle = fileStyle;
    }

    public int getSTYLE_FILE() {
        return STYLE_FILE;
    }

    public void setSTYLE_FILE(int STYLE_FILE) {
        this.STYLE_FILE = STYLE_FILE;
    }

    public int getFileStyle() {
        return fileStyle;
    }

    public void setFileStyle(int fileStyle) {
        this.fileStyle = fileStyle;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSise;
    }

    public void setFileSise(long fileSise) {
        this.fileSise = fileSise;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "FileEntity{" +
                "imageId=" + imageId +
                ", fileName='" + fileName + '\'' +
                ", fileSise=" + fileSise +
                ", filePath='" + filePath + '\'' +
                ", fileStyle=" + fileStyle +
                ", STYLE_FILE=" + STYLE_FILE +
                '}';
    }


}
