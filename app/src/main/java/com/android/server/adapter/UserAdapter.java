package com.android.server.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.server.R;
import com.android.server.activity.client.LoadImageActivity;
import com.android.server.config.AppConfig;
import com.android.server.config.AppInfo;
import com.android.server.entity.User;
import com.android.server.util.MyLog;
import com.bumptech.glide.Glide;

import java.util.List;

public class UserAdapter extends BaseAdapter {
    List<User> appInfos;
    Context context;
    LayoutInflater inflater;

    public void setList(List<User> paramList) {
        this.appInfos = paramList;
        notifyDataSetChanged();
    }

    public UserAdapter(Context paramContext, List<User> paramList) {
        this.context = paramContext;
        this.appInfos = paramList;
        inflater = LayoutInflater.from(context);
    }

    public Context getContext() {
        return this.context;
    }

    public int getCount() {
        return appInfos.size();
    }

    public Object getItem(int paramInt) {
        return this.appInfos.get(paramInt);
    }

    public long getItemId(int paramInt) {
        return paramInt;
    }

    public View getView(final int position, View convertView, ViewGroup paramViewGroup) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            if (AppConfig.APP_MODEL == AppConfig.APP_MODEL_CLIENT) {
                convertView = inflater.inflate(R.layout.item_bean_client, null);
            } else {
                convertView = inflater.inflate(R.layout.item_bean_text, null);
            }
            viewHolder.tv_username = ((TextView) convertView.findViewById(R.id.tv_username));
            viewHolder.tv_password = ((TextView) convertView.findViewById(R.id.tv_password));
            viewHolder.btn_modify_item = ((Button) convertView.findViewById(R.id.btn_modify_item));
            viewHolder.btn_del_item = ((Button) convertView.findViewById(R.id.btn_del_item));
            viewHolder.btn_update_header = ((Button) convertView.findViewById(R.id.btn_update_header));
            viewHolder.iv_image_show = ((ImageView) convertView.findViewById(R.id.iv_image_show));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (AppConfig.APP_MODEL == AppConfig.APP_MODEL_CLIENT) {
            viewHolder.btn_update_header.setVisibility(View.VISIBLE);
        } else {
            viewHolder.btn_update_header.setVisibility(View.GONE);
        }
        final User user = appInfos.get(position);
        viewHolder.tv_username.setText("u: " + user.getUsername());
        viewHolder.tv_password.setText("p: " + user.getPassword());
        String imagePath = user.getImageUrl();
        MyLog.cdl("=====展示的url===" + imagePath);
        if (imagePath == null || imagePath.contains("null") || imagePath.length() < 2) {
        } else {
            if (AppConfig.APP_MODEL == AppConfig.APP_MODEL_CLIENT) {
                Glide.with(context).load(imagePath).into(viewHolder.iv_image_show);
            } else if (AppConfig.APP_MODEL == AppConfig.APP_MODEL_SERVER) { //服务端口
                String fileName = imagePath.substring(imagePath.indexOf("=") + 1, imagePath.length());
                String showPath = AppInfo.BASE_FILE_PATH + "/" + fileName;
                MyLog.cdl("=====展示的url===" + showPath);
                Glide.with(context).load(showPath).into(viewHolder.iv_image_show);
            }
        }
        viewHolder.btn_modify_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener == null) {
                    return;
                }
                listener.clickItemModifyListener(position, user);
            }
        });
        viewHolder.btn_del_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener == null) {
                    return;
                }
                listener.clickItemDelListener(position, user);
            }
        });
        viewHolder.btn_update_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener == null) {
                    return;
                }
                listener.clickItemUpdateListener(position, user);
            }
        });
        return convertView;
    }

    private class ViewHolder {
        TextView tv_username, tv_password;
        Button btn_modify_item;
        Button btn_del_item;
        Button btn_update_header;
        ImageView iv_image_show;
    }

    AdapterItemClickListener listener;

    public void setOnItemClickListener(AdapterItemClickListener listener) {
        this.listener = listener;
    }

    public interface AdapterItemClickListener {
        void clickItemModifyListener(int position, User user);

        void clickItemDelListener(int position, User user);

        void clickItemUpdateListener(int position, User user);
    }


}
