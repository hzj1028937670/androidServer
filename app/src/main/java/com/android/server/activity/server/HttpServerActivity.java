package com.android.server.activity.server;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.server.R;
import com.android.server.activity.BaseActivity;
import com.android.server.activity.client.HttpTestActivity;
import com.android.server.adapter.UserAdapter;
import com.android.server.entity.User;
import com.android.server.parsener.MainParsener;
import com.android.server.parsener.MainServerParsener;
import com.android.server.util.MyLog;
import com.android.server.view.MainView;
import com.android.server.view.MyToastView;
import com.android.server.view.WaitDialogUtil;

import java.util.ArrayList;
import java.util.List;

public class HttpServerActivity extends BaseActivity implements View.OnClickListener, MainView, UserAdapter.AdapterItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);
        initView();
    }

    Button btn_add;
    EditText et_username;
    MainServerParsener mainParsener;
    WaitDialogUtil waitDialogUtil;
    ListView lv_content;
    UserAdapter adapter;
    List<User> listsContent = new ArrayList<>();

    private void initView() {
        waitDialogUtil = new WaitDialogUtil(HttpServerActivity.this);
        et_username = (EditText) findViewById(R.id.et_username);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
        lv_content = (ListView) findViewById(R.id.lv_content);
        adapter = new UserAdapter(HttpServerActivity.this, listsContent);
        lv_content.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        mainParsener = new MainServerParsener(HttpServerActivity.this, this);
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

    private void updateUserList() {
        listsContent.clear();
        adapter.setList(listsContent);
        mainParsener.getUserList();
    }


    @Override
    public void showToastView(String desc) {
        MyToastView.getInstance().Toast(HttpServerActivity.this, desc);
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

    @Override
    public void clickItemUpdateListener(int position, User user) {

    }
}