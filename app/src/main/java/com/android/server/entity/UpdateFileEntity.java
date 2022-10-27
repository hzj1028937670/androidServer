package com.android.server.entity;


public class UpdateFileEntity {

    public static final int UPDATE_PARPER = -1;
    public static final int UPDATE_PROGRESS = 0;
    public static final int UPDATE_SUCCESS = 1;
    public static final int UPDATE_FAILED = 2;

    String filePath;

    int updateType;
    int progress;


    public UpdateFileEntity(String filePath, int updateType, int progress) {
        this.filePath = filePath;
        this.updateType = updateType;
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getUpdateType() {
        return updateType;
    }

    public void setUpdateType(int updateType) {
        this.updateType = updateType;
    }
}
