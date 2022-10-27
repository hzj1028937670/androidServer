package com.android.server.activity.server;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.android.server.R;
import com.android.server.activity.BaseActivity;
import com.android.server.adapter.FileAdapter;
import com.android.server.config.AppInfo;
import com.android.server.entity.FileEntity;
import com.android.server.util.FileUtil;
import com.android.server.util.file.FileergodicUtil;
import com.android.server.view.MyToastView;
import com.android.server.view.OridinryDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件管理器
 */
public class FileListActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener, AdapterView.OnItemSelectedListener {

    private GridView lv_file;
    List<FileEntity> list = new ArrayList<FileEntity>();
    FileAdapter adapter;
    public static final String PATH_SEARCH = "PATH_SEARCH";
    OridinryDialog oridinryDialog;
    String path_search = AppInfo.BASE_SD_PATH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);
        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            boolean hasFullStorageAccess = Environment.isExternalStorageManager();
            if (!hasFullStorageAccess){
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }
    }

    Button btn_refresh;

    private void initView() {
        try {
            Intent intent = getIntent();
            if (intent != null) {
                path_search = intent.getStringExtra(PATH_SEARCH);
            }
        } catch (Exception e) {
            path_search = AppInfo.BASE_SD_PATH;
        }
        lv_file = (GridView) findViewById(R.id.lv_file);
        adapter = new FileAdapter(FileListActivity.this, list);
        lv_file.setAdapter(adapter);
        lv_file.setOnItemClickListener(this);
        lv_file.setOnItemLongClickListener(this);
        lv_file.setOnItemSelectedListener(this);
        btn_refresh = (Button) findViewById(R.id.btn_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFileList();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        getFileList();
    }

    private void getFileList() {
        if (list!=null){
            list.clear();
            adapter.setItems(list);
            if (path_search == null || path_search.length() < 5) {
                return;
            }
            list = FileergodicUtil.getFileList(path_search);
            if (list == null) {
                return;
            }
            adapter.setItems(list);
        }

    }

    FileEntity currentFileEntity = null;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        currentFileEntity = list.get(position);
        Log.e("path", "============url===" + currentFileEntity.getFilePath());
        int fileStyle = currentFileEntity.getFileStyle();
        if (fileStyle == FileEntity.FILE_STYLE_DIR) {  //文件夹
            String fileDirPath = currentFileEntity.getFilePath();
            Intent intent = new Intent(FileListActivity.this, FileListActivity.class);
            intent.putExtra(FileListActivity.PATH_SEARCH, fileDirPath);
            startActivity(intent);
        } else if (fileStyle == FileEntity.FILE_STYLE_FILE) {  //文件
            MyToastView.getInstance().Toast(FileListActivity.this, "暂不支持预览");
        }
    }

    /***
     * listView长按事件
     * @param parent
     * @param view
     * @param position
     * @param id
     * @return
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        currentFileEntity = list.get(position);
        delFileChooice(currentFileEntity);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        currentFileEntity = list.get(position);
    }

    /***
     * 删除选中的文件
     * @param entity
     */
    private void delFileChooice(FileEntity entity) {
        if (oridinryDialog == null) {
            oridinryDialog = new OridinryDialog(FileListActivity.this);
        }
        final String filePath = entity.getFilePath();
        String fileName = entity.getFileName();
        oridinryDialog.show("是否删除 <" + fileName + "> 文件 ？", "删除", "在想想");
        oridinryDialog.setOnDialogClickListener(new OridinryDialog.OridinryDialogClick() {
            @Override
            public void sure() {
                FileUtil.deleteDirOrFile(filePath);
                getFileList();
            }

            @Override
            public void noSure() {

            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
