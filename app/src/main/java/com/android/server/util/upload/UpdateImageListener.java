package com.android.server.util.upload;


public interface UpdateImageListener {

    void updateImageProgress(int progress);

    void updateImageSuccess(String desc);


}
