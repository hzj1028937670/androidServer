package com.android.server.db;

import android.content.ContentValues;

import com.android.server.ServerApplication;
import com.android.server.config.AppConfig;
import com.android.server.config.AppInfo;
import com.android.server.entity.User;
import com.android.server.util.MyLog;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.List;

public class UserDbUtil {

    public static boolean addUser(User user) {
        boolean isExict = jujleUserExict(user);
        if (isExict) {
            return modifyUser(user);
        } else {
            return addUserToWeb(user);
        }
    }

    /**
     * 删除某一个用户
     *
     * @param username
     * @return
     */
    public static boolean delUser(String username) {
        boolean isDel = false;
        if (username == null) {
            return isDel;
        }
        int delNum = DataSupport.deleteAll(User.class, "username=?", username);
        if (delNum == 0) {
            isDel = false;
        } else {
            isDel = true;
        }
        String filePath = AppInfo.BASE_FILE_PATH + "/" + username + ".jpg";
        MyLog.db("=====需要删除的文件路径==" + filePath);
        File file = new File(filePath);
        MyLog.db("=====需要删除的文件是否存在==" + file.exists());
        if (file.exists()) {
            file.delete();
        }
        return isDel;
    }


    // 全部删除
    public static boolean deleteAll() {
        try {
            int delNUm = DataSupport.deleteAll(User.class);
            if (delNUm == 0) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    /**
     * 修改用户
     *
     * @param user
     * @return
     */
    public static boolean modifyUser(User user) {
        if (user == null) {
            return false;
        }
        try {
            ContentValues values = new ContentValues();
            String username = user.getUsername();
            String password = user.getPassword();
            values.put("password", password);
            int updateNum = DataSupport.updateAll(User.class, values, "username = ?", username);
            if (updateNum == 0) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean addUserImageUrl(String fileName) {
        if (fileName == null || fileName.length() < 2) {
            MyLog.db("文件名字长度不合法");
            return false;
        }
        String imageUrl = ServerApplication.getInstance().getSERVER_IP();
        imageUrl = "http://" + imageUrl + ":" + AppConfig.PORT_SERVER + AppConfig.GET_FILE + "?fileName=" + fileName;
        MyLog.db("保存的URL = " + imageUrl);
        fileName = fileName.substring(0, fileName.indexOf("."));
        MyLog.db("裁剪的用户名字 = " + fileName);
        boolean isUserExict = jujleUserExict(new User(fileName, ""));
        MyLog.db("====查询的用户是否存在 = " + isUserExict);
        if (!isUserExict) {
            MyLog.db("用户不存在，不添加头像地址");
            return false;
        }
        try {
            ContentValues values = new ContentValues();
            values.put("imageUrl", imageUrl);
            int updateNum = DataSupport.updateAll(User.class, values, "username = ?", fileName);
            if (updateNum == 0) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    /**
     * 添加用户
     *
     * @param user
     * @return
     */
    public static boolean addUserToWeb(User user) {
        if (user == null) {
            return false;
        }
        try {
            return user.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断用户是否存在
     *
     * @param user
     * @return
     */
    public static boolean jujleUserExict(User user) {
        if (user == null) {
            return false;
        }
        String userName = user.getUsername();
        List<User> userList = DataSupport.where("username=?", userName).find(User.class);
        if (userList == null || userList.size() < 1) {
            return false;
        }
        return true;
    }


    /**
     * 查询所有的用户信息
     *
     * @return
     */
    public static List<User> getAllUserInfo() {
        List<User> txtList = null;
        try {
            txtList = DataSupport.findAll(User.class);
            if (txtList != null && txtList.size() > 0) {
                MyLog.db("===查询的User====" + txtList.get(0).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return txtList;
    }


}
