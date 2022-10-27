package com.android.server.view;

import com.android.server.entity.User;

import java.util.List;

public interface MainView {
    void showToastView(String desc);

    void showWaitDialog(boolean isShow);

    void addUserStatues(boolean isTrue, String desc);

    void modifyUserStatues(boolean isTrue, String desc);

    void delUserStatues(boolean isTrue, String desc);

    void queryUserInfo(boolean isTrue, List<User> lists, String desc);
}
