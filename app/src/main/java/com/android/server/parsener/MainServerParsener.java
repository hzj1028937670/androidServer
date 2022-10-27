package com.android.server.parsener;

import android.content.Context;

import com.android.server.config.AppInfo;
import com.android.server.db.UserDbUtil;
import com.android.server.entity.User;
import com.android.server.http.PostRequestRunnable;
import com.android.server.http.RequestBackListener;
import com.android.server.service.UdpService;
import com.android.server.util.MyLog;
import com.android.server.util.net.NetWorkUtils;
import com.android.server.view.MainView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class MainServerParsener {

    Context context;
    MainView mainView;

    public MainServerParsener(Context context, MainView mainView) {
        this.context = context;
        this.mainView = mainView;
    }

    public void delUserByName(String userName) {
        boolean isModify = UserDbUtil.delUser(userName);
        if (isModify) {
            mainView.modifyUserStatues(true, "删除成功");
        } else {
            mainView.modifyUserStatues(false, "删除失败");
        }
    }

    /**
     * 修改用户
     *
     * @param userName
     */
    public void modifyUser(String userName) {
        User user = new User(userName, "235645");
        boolean isModify = UserDbUtil.modifyUser(user);
        if (isModify) {
            mainView.modifyUserStatues(true, "修改成功");
        } else {
            mainView.modifyUserStatues(false, "修改失败");
        }
    }


    /**
     * 添加用户
     *
     * @param username
     */
    public void addUser(String username) {
        User user = new User(username, "123456");
        boolean isSave = UserDbUtil.addUser(user);
        if (isSave) {
            mainView.addUserStatues(true, "保存成功");
        } else {
            mainView.addUserStatues(false, "新增失败");
        }
    }

    /**
     * 获取用户列表
     */
    public void getUserList() {
        List<User> lists = UserDbUtil.getAllUserInfo();
        if (lists == null || lists.size() < 1) {
            mainView.queryUserInfo(false, null, "数据库没有数据");
            return;
        }
        mainView.queryUserInfo(true, lists, "查询成功");
    }

}
