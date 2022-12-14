package com.cdl.permission;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by Administrator on 2017/5/10 0010.
 */

public class PermissionUtil {
    private final Context mContext;
    private String mTitle;
    private String mMsg;
    private int mStyleResId = -1;
    private PermissionCallback mCallback;
    private List<PermissionItem> mCheckPermissions;
    private int mPermissionType;

    private String[] mNormalPermissionNames;
    private String[] mNormalPermissions = {
            WRITE_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION, CAMERA};
    private int[] mNormalPermissionIconRes = {
            R.drawable.permission_ic_storage, R.drawable.permission_ic_location, R.drawable.permission_ic_camera};
    private int mFilterColor = 0;
    private int mAnimStyleId = -1;

    public static PermissionUtil create(Context context) {
        return new PermissionUtil(context);
    }

    public PermissionUtil(Context context) {
        mContext = context;
        mNormalPermissionNames = mContext.getResources().getStringArray(R.array.permissionNames);
    }

    public PermissionUtil title(String title) {
        mTitle = title;
        return this;
    }

    public PermissionUtil msg(String msg) {
        mMsg = msg;
        return this;
    }

    public PermissionUtil permissions(List<PermissionItem> permissionItems) {
        mCheckPermissions = permissionItems;
        return this;
    }

    public PermissionUtil filterColor(int color) {
        mFilterColor = color;
        return this;
    }

    public PermissionUtil animStyle(int styleId) {
        mAnimStyleId = styleId;
        return this;
    }

    public PermissionUtil style(int styleResIdsId) {
        mStyleResId = styleResIdsId;
        return this;
    }

    private List<PermissionItem> getNormalPermissions() {
        List<PermissionItem> permissionItems = new ArrayList<>();
        for (int i = 0; i < mNormalPermissionNames.length; i++) {
            permissionItems.add(new PermissionItem(mNormalPermissions[i], mNormalPermissionNames[i], mNormalPermissionIconRes[i]));
        }
        return permissionItems;
    }

    public static boolean checkPermission(Context context, String permission) {
        int checkPermission = ContextCompat.checkSelfPermission(context, permission);
        if (checkPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    /**
     * ??????????????????
     *
     * @param callback
     */
    public void checkMutiPermission(PermissionCallback callback) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (callback != null)
                callback.onFinish();
            return;
        }

        if (mCheckPermissions == null) {
            mCheckPermissions = new ArrayList<>();
            mCheckPermissions.addAll(getNormalPermissions());
        }
        //???????????????????????????????????????
        Iterator<PermissionItem> iterator = mCheckPermissions.listIterator();
        while (iterator.hasNext()) {
            if (checkPermission(mContext, iterator.next().Permission))
                iterator.remove();
        }
        mCallback = callback;
        if (mCheckPermissions.size() > 0) {
            startActivity();
        } else {
            if (callback != null)
                callback.onFinish();
        }
    }

    /**
     * ??????????????????
     *
     * @param permission
     * @param callback
     */
    public void checkSinglePermission(String permission, PermissionCallback callback) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkPermission(mContext, permission)) {
            if (callback != null)
                callback.notAllow();
            return;
        }
        mCallback = callback;
        mPermissionType = PermissionActivity.PERMISSION_TYPE_SINGLE;
        mCheckPermissions = new ArrayList<>();
        mCheckPermissions.add(new PermissionItem(permission));
        startActivity();
    }

    private void startActivity() {
        PermissionActivity.setCallBack(mCallback);
        Intent intent = new Intent(mContext, PermissionActivity.class);
        intent.putExtra(ConstantValue.DATA_TITLE, mTitle);
        intent.putExtra(ConstantValue.DATA_PERMISSION_TYPE, mPermissionType);
        intent.putExtra(ConstantValue.DATA_MSG, mMsg);
        intent.putExtra(ConstantValue.DATA_FILTER_COLOR, mFilterColor);
        intent.putExtra(ConstantValue.DATA_STYLE_ID, mStyleResId);
        intent.putExtra(ConstantValue.DATA_ANIM_STYLE, mAnimStyleId);
        intent.putExtra(ConstantValue.DATA_PERMISSIONS, (Serializable) mCheckPermissions);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

}
