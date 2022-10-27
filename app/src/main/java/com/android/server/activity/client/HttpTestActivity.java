package com.android.server.activity.client;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.server.R;
import com.android.server.activity.BaseActivity;
import com.android.server.adapter.UserAdapter;
import com.android.server.config.AppInfo;
import com.android.server.entity.User;
import com.android.server.parsener.MainParsener;
import com.android.server.service.UdpService;
import com.android.server.util.MyLog;
import com.android.server.util.image.GlideLoader;
import com.android.server.util.upload.UpdateFileRunnable;
import com.android.server.util.upload.UpdateImageListener;
import com.android.server.view.MainView;
import com.android.server.view.MyToastView;
import com.android.server.view.WaitDialogUtil;
import com.jaiky.imagespickers.ImageConfig;
import com.jaiky.imagespickers.ImageSelector;
import com.jaiky.imagespickers.ImageSelectorActivity;

import java.util.ArrayList;
import java.util.List;

public class HttpTestActivity extends BaseActivity implements View.OnClickListener, MainView, UserAdapter.AdapterItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);
        initView();
    }

    Button btn_add;
    EditText et_username;
    MainParsener mainParsener;
    WaitDialogUtil waitDialogUtil;
    ListView lv_content;
    UserAdapter adapter;
    List<User> listsContent = new ArrayList<>();

    private void initView() {
        waitDialogUtil = new WaitDialogUtil(HttpTestActivity.this);
        et_username = (EditText) findViewById(R.id.et_username);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
        lv_content = (ListView) findViewById(R.id.lv_content);
        adapter = new UserAdapter(HttpTestActivity.this, listsContent);
        lv_content.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        mainParsener = new MainParsener(HttpTestActivity.this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                String username = et_username.getText().toString();
                mainParsener.addUser(username);
                et_username.setText("");
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private Handler handler = new Handler();

    private void updateUserList() {
        showWaitDialog(true);
        listsContent.clear();
        adapter.setList(listsContent);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                主要是正对图片上传成功立马下拉没有数据
                mainParsener.getUserList();
            }
        }, 1500);


    }

    @Override
    public void showToastView(String desc) {
        MyToastView.getInstance().Toast(HttpTestActivity.this, desc);
    }


    @Override
    public void showWaitDialog(boolean isShow) {
        if (isShow) {
            waitDialogUtil.show("加载中");
        } else {
            waitDialogUtil.dismiss();
        }
    }

    @Override
    public void addUserStatues(boolean isTrue, String desc) {
        showWaitDialog(false);
        showToastView(desc);
        if (isTrue) {
            updateUserList();
        }
    }

    @Override
    public void modifyUserStatues(boolean isTrue, String desc) {
        showWaitDialog(false);
        showToastView(desc);
        if (isTrue) {
            updateUserList();
        }
    }

    @Override
    public void delUserStatues(boolean isTrue, String desc) {
        showWaitDialog(false);
        showToastView(desc);
        if (isTrue) {
            updateUserList();
        }
    }

    @Override
    public void queryUserInfo(boolean isTrue, List<User> lists, String desc) {
        showWaitDialog(false);
        showToastView(desc);
        if (!isTrue) {
            return;
        }
        if (lists == null && lists.size() < 1) {
            return;
        }
        this.listsContent = lists;
        adapter.setList(listsContent);
        MyLog.message("======界面查询的数据===" + lists.size());
    }

    @Override
    public void clickItemModifyListener(int position, User user) {
        String userName = user.getUsername();
        mainParsener.modifyUser(userName);
    }

    @Override
    public void clickItemDelListener(int position, User user) {
        String userName = user.getUsername();
        mainParsener.delUserByName(userName);
    }

    /**
     * 头像上传
     *
     * @param position
     * @param user
     */
    @Override
    public void clickItemUpdateListener(int position, User user) {
        this.userUpdate = user;
        showChooiceMediaDoalog();
    }

    User userUpdate = null;
//========================头像上传相关=============================================================================

    private void showChooiceMediaDoalog() {
        ArrayList<String> path = new ArrayList<String>();
        ImageConfig imageConfig = new ImageConfig.Builder(
                // GlideLoader 可用自己用的缓存库
                new GlideLoader())
                // 如果在 4.4 以上，则修改状态栏颜色 （默认黑色）
                .steepToolBarColor(getResources().getColor(R.color.titleBlue))
                // 标题的背景颜色 （默认黑色）
                .titleBgColor(getResources().getColor(R.color.titleBlue))
                // 提交按钮字体的颜色  （默认白色）
                .titleSubmitTextColor(getResources().getColor(R.color.white))
                // 标题颜色 （默认白色）
                .titleTextColor(getResources().getColor(R.color.white))
                // 开启多选   （默认为多选）  (单选 为 singleSelect)
                //.singleSelect()
                //裁剪
                //.crop()
                // 多选时的最大数量   （默认 9 张）
                .mutiSelectMaxSize(1)
                // 已选择的图片路径
                .pathList(path)
                // 拍照后存放的图片路径（默认 /temp/picture）
                .filePath("/temp")
                // 开启拍照功能 （默认开启）
                .showCamera()
                .requestCode(ImageSelector.IMAGE_REQUEST_CODE)
                .build();
        ImageSelector.open(HttpTestActivity.this, imageConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageSelector.IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data == null) {
                showToastView("当前没有选择图片");
                return;
            }
            List<String> pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
            if (pathList == null || pathList.size() < 1) {
                showToastView("当前没有选择图片");
                return;
            }
            String filePath = pathList.get(0);
            updateFileToWeb(filePath);
        }
    }

    /**
     * 上传文件
     */
    private void updateFileToWeb(String filePath) {
        if (userUpdate == null) {
            showToastView("请选择上传的对象");
            return;
        }
        String requestUrl = AppInfo.getUpdateFileUrl();
        String fileName = userUpdate.getUsername() + ".jpg";
        MyLog.down("====上传Url====" + requestUrl);
        UpdateFileRunnable runnable = new UpdateFileRunnable(requestUrl, filePath, fileName, new UpdateImageListener() {

            @Override
            public void updateImageProgress(int progress) {
                MyLog.down("====上传进度====" + progress);
            }

            @Override
            public void updateImageSuccess(String desc) {
                showToastView("上传成功");
                MyLog.down("====上传成功====" + desc);
            }
        });
        UdpService.getInstance().executor(runnable);
    }


}
