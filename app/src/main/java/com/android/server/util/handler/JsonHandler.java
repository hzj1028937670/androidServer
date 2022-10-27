package com.android.server.util.handler;

import android.util.Log;

import com.android.server.db.UserDbUtil;
import com.android.server.entity.User;
import com.google.gson.Gson;
import com.yanzhenjie.andserver.RequestHandler;
import com.yanzhenjie.andserver.RequestMethod;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.util.HttpRequestParser;

import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.HttpResponse;
import org.apache.httpcore.entity.StringEntity;
import org.apache.httpcore.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class JsonHandler implements RequestHandler {

    HttpResponse httpResponse;

    @RequestMapping(method = {RequestMethod.POST})
    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws IOException {
        String content = HttpRequestParser.getContentFromBody(httpRequest);  //body      形式请求
        this.httpResponse = httpResponse;
//        String username = (String) httpContext.getAttribute("username");
//        String content = HttpRequestParser.getContentFromUri(httpRequest);     //get请求
        parsenerJsonContent(content);
    }

    /**
     * 解析请求信息
     *
     * @param content
     */
    private void parsenerJsonContent(String content) {
        try {
            if (content == null || content.length() < 2) {
                backIntoToView(-1, "请求参数异常,请检查", null);
                return;
            }
            JSONObject jsonObject = new JSONObject(content);
            String action = jsonObject.getString("action");
            Log.e("cdl", "请求的类型: " + action);
            if (action.contains("addUser")) {
                addUserToDeb(content);    //{"action":"addUser","username":"xiao","password":"123456"}
            } else if (action.contains("getUser")) {
                getUserListInfo();        //{"action":"getUser"}
            } else if (action.contains("delUser")) {
                delUserInfo(content);     //{"action":"delUser","username":"admin"}
            } else if (action.contains("modifyUser")) {
                modifyUserInfo(content);  //{"action":"modifyUser","username":"xun","password":"123456"}
            }
        } catch (JSONException e) {
            backIntoToView(-1, "请求参数不正确", null);
            e.printStackTrace();
        }
    }

    /**
     * 修改用户
     *
     * @param content
     */
    private void modifyUserInfo(String content) {
        try {
            JSONObject jsonObject = new JSONObject(content);
            String username = jsonObject.getString("username");
            String password = jsonObject.getString("password");
            boolean isSave = UserDbUtil.modifyUser(new User(username, password));
            if (isSave) {
                backIntoToView(0, "修改成功", null);
            } else {
                backIntoToView(-1, "修改用户失败", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除用户信息
     */
    private void delUserInfo(String content) {
        try {
            JSONObject jsonObject = new JSONObject(content);
            String username = jsonObject.getString("username");
            boolean isSave = UserDbUtil.delUser(username);
            if (isSave) {
                backIntoToView(0, "删除成功", null);
            } else {
                backIntoToView(-1, "删除用户失败", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取用户列表
     */
    private void getUserListInfo() {
        List<User> lists = UserDbUtil.getAllUserInfo();
        if (lists == null || lists.size() < 1) {
            backIntoToView(-1, "当前没有用户信息", null);
            return;
        }
        Gson gson = new Gson();
        String userString = gson.toJson(lists);
        Log.e("cdl", "====查询用户==" + userString);
        backIntoToView(0, "查询成功", userString);
    }

    /**
     * 增加用户
     *
     * @param content
     */
    private void addUserToDeb(String content) {
        try {
            JSONObject jsonObject = new JSONObject(content);
            String username = jsonObject.getString("username");
            String password = jsonObject.getString("password");
            String imageUrl = jsonObject.getString("imageUrl");
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setImageUrl(imageUrl);
            boolean isSave = UserDbUtil.addUser(user);
            if (isSave) {
                backIntoToView(0, "添加成功", null);
            } else {
                backIntoToView(-1, "添加数据库失败", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 提交的数据必须是JSON格式的=================================================================================
     */
    private void backIntoToView(int code, String msg, String data) {
        try {
            String content = "{\"code\":" + code + ",\"msg\":" + msg + ",\"data\":" + data + "}";
            JSONObject jsonObject = new JSONObject(content);
            StringEntity stringEntity = new StringEntity(jsonObject.toString(), "utf-8");
            httpResponse.setStatusCode(200);
            httpResponse.setEntity(stringEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
