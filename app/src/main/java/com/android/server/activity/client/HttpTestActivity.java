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
//                ?????????????????????????????????????????????????????????
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
            waitDialogUtil.show("?????????");
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
        MyLog.message("======?????????????????????===" + lists.size());
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
     * ????????????
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
//========================??????????????????=============================================================================

    private void showChooiceMediaDoalog() {
        ArrayList<String> path = new ArrayList<String>();
        ImageConfig imageConfig = new ImageConfig.Builder(
                // GlideLoader ???????????????????????????
                new GlideLoader())
                // ????????? 4.4 ????????????????????????????????? ??????????????????
                .steepToolBarColor(getResources().getColor(R.color.titleBlue))
                // ????????????????????? ??????????????????
                .titleBgColor(getResources().getColor(R.color.titleBlue))
                // ???????????????????????????  ??????????????????
                .titleSubmitTextColor(getResources().getColor(R.color.white))
                // ???????????? ??????????????????
                .titleTextColor(getResources().getColor(R.color.white))
                // ????????????   ?????????????????????  (?????? ??? singleSelect)
                //.singleSelect()
                //??????
                //.crop()
                // ????????????????????????   ????????? 9 ??????
                .mutiSelectMaxSize(1)
                // ????????????????????????
                .pathList(path)
                // ??????????????????????????????????????? /temp/picture???
                .filePath("/temp")
                // ?????????????????? ??????????????????
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
                showToastView("????????????????????????");
                return;
            }
            List<String> pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
            if (pathList == null || pathList.size() < 1) {
                showToastView("????????????????????????");
                return;
            }
            String filePath = pathList.get(0);
            updateFileToWeb(filePath);
        }
    }

    /**
     * ????????????
     */
    private void updateFileToWeb(String filePath) {
        if (userUpdate == null) {
            showToastView("????????????????????????");
            return;
        }
        String requestUrl = AppInfo.getUpdateFileUrl();
        String fileName = userUpdate.getUsername() + ".jpg";
        MyLog.down("====??????Url====" + requestUrl);
        UpdateFileRunnable runnable = new UpdateFileRunnable(requestUrl, filePath, fileName, new UpdateImageListener() {

            @Override
            public void updateImageProgress(int progress) {
                MyLog.down("====????????????====" + progress);
            }

            @Override
            public void updateImageSuccess(String desc) {
                showToastView("????????????");
                MyLog.down("====????????????====" + desc);
            }
        });
        UdpService.getInstance().executor(runnable);
    }


}
