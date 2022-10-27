package com.android.server.parsener;

import android.content.Context;

import com.android.server.config.AppInfo;
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

public class MainParsener {

    Context context;
    MainView mainView;

    public MainParsener(Context context, MainView mainView) {
        this.context = context;
        this.mainView = mainView;
    }

    public void delUserByName(String userName) {
        if (!NetWorkUtils.isNetworkConnected(context)) {
            mainView.delUserStatues(false, "网络异常");
            return;
        }
        mainView.showWaitDialog(true);
        String requestUrl = AppInfo.getBaseUrlJson();
        User user = new User("delUser", userName, "");
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Gson gson = new Gson();
        String requestJson = gson.toJson(user);
        RequestBody requestBody = RequestBody.create(JSON, requestJson);
        PostRequestRunnable runnable = new PostRequestRunnable(requestUrl, requestBody, new RequestBackListener() {
            @Override
            public void requestSuccess(String json) {
                MyLog.http("提交设备信息success=" + json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int code = jsonObject.getInt("code");
                    String msg = jsonObject.getString("msg");
                    if (code == 0) {
                        mainView.delUserStatues(true, "删除成功");
                    } else {
                        mainView.delUserStatues(true, msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestFailed(String errorDesc) {
                MyLog.http("提交设备信息failed=" + errorDesc);
                mainView.delUserStatues(false, "请检查网络");
            }
        });
        UdpService.getInstance().executor(runnable);
    }


    /**
     * 修改用户
     *
     * @param userName
     */
    public void modifyUser(String userName) {
        if (!NetWorkUtils.isNetworkConnected(context)) {
            mainView.modifyUserStatues(false, "网络异常");
            return;
        }
        mainView.showWaitDialog(true);
        String requestUrl = AppInfo.getBaseUrlJson();
        int password = new Random().nextInt(100000) + 1000;
//        {"action":"modifyUser","username":"xun","password":"123456"}
        User user = new User("modifyUser", userName, password + "");
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Gson gson = new Gson();
        String requestJson = gson.toJson(user);
        RequestBody requestBody = RequestBody.create(JSON, requestJson);
        PostRequestRunnable runnable = new PostRequestRunnable(requestUrl, requestBody, new RequestBackListener() {
            @Override
            public void requestSuccess(String json) {
                MyLog.http("提交设备信息success=" + json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int code = jsonObject.getInt("code");
                    String msg = jsonObject.getString("msg");
                    if (code == 0) {
                        mainView.modifyUserStatues(true, "修改成功");
                    } else {
                        mainView.modifyUserStatues(true, msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestFailed(String errorDesc) {
                MyLog.http("提交设备信息failed=" + errorDesc);
                mainView.modifyUserStatues(false, "请检查网络");
            }
        });
        UdpService.getInstance().executor(runnable);
    }


    /**
     * 添加用户
     *
     * @param username
     */
    public void addUser(String username) {
        if (!NetWorkUtils.isNetworkConnected(context)) {
            mainView.addUserStatues(false, "网络异常");
            return;
        }
        mainView.showWaitDialog(true);
        String requestUrl = AppInfo.getBaseUrlJson();
        User user = new User();
        user.setAction("addUser");
        user.setUsername(username);
        user.setPassword("123456");
        user.setImageUrl("null");
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Gson gson = new Gson();
        String requestJson = gson.toJson(user);
        RequestBody requestBody = RequestBody.create(JSON, requestJson);
        PostRequestRunnable runnable = new PostRequestRunnable(requestUrl, requestBody, new RequestBackListener() {
            @Override
            public void requestSuccess(String json) {
                MyLog.http("提交设备信息success=" + json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int code = jsonObject.getInt("code");
                    String msg = jsonObject.getString("msg");
                    if (code == 0) {
                        mainView.addUserStatues(true, "添加成功");
                    } else {
                        mainView.addUserStatues(true, msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestFailed(String errorDesc) {
                MyLog.http("提交设备信息failed=" + errorDesc);
                mainView.addUserStatues(false, "请检查网络");
            }
        });
        UdpService.getInstance().executor(runnable);
    }

    /**
     * 获取用户列表
     */
    public void getUserList() {
        if (!NetWorkUtils.isNetworkConnected(context)) {
            mainView.queryUserInfo(false, null, "网络异常");
            return;
        }
        mainView.showWaitDialog(true);
        String requestUrl = AppInfo.getBaseUrlJson();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String requestJson = "{\"action\":\"getUser\"}";
        RequestBody requestBody = RequestBody.create(JSON, requestJson);
        PostRequestRunnable runnable = new PostRequestRunnable(requestUrl, requestBody, new RequestBackListener() {
            @Override
            public void requestSuccess(String json) {
                MyLog.http("查询设备信息===" + json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    int code = jsonObject.getInt("code");
                    String msg = jsonObject.getString("msg");
                    if (code == 0) {
                        String data = jsonObject.getString("data");
                        parsenerUserList(data);
                    } else {
                        mainView.queryUserInfo(false, null, msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestFailed(String errorDesc) {
                MyLog.http("查询设备信息===" + errorDesc);
                mainView.queryUserInfo(false, null, "请检查网络");
            }
        });
        UdpService.getInstance().executor(runnable);
    }

    /**
     * 解析查询的数据
     *
     * @param data
     */
    private void parsenerUserList(String data) {
        try {
            JSONArray jsonArray = new JSONArray(data);
            int num = jsonArray.length();
            if (num < 1) {
                mainView.queryUserInfo(false, null, "没有用户数据");
                return;
            }
            List<User> lists = new ArrayList<>();
            for (int i = 0; i < num; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String username = jsonObject.getString("username");
                String password = jsonObject.getString("password");
                User user = new User();
                if (jsonObject.toString().contains("imageUrl")) {
                    String imageUrl = jsonObject.getString("imageUrl");
                    user.setImageUrl(imageUrl);
                }
                user.setUsername(username);
                user.setPassword(password);
                lists.add(user);
            }
            mainView.queryUserInfo(true, lists, "查询成功");
        } catch (Exception e) {
            mainView.queryUserInfo(false, null, "JSON解析异常");
            e.printStackTrace();
        }
    }


}
