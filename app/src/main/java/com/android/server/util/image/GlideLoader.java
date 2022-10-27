package com.android.server.util.image;


import android.content.Context;
import android.widget.ImageView;

import com.android.server.R;
import com.bumptech.glide.Glide;
import com.jaiky.imagespickers.ImageLoader;

public class GlideLoader implements ImageLoader {

    @Override
    public void displayImage(Context context, String path, ImageView imageView) {
        Glide.with(context)
                .load(path)
                .placeholder(R.mipmap.imageselector_photo)
                .centerCrop()
                .into(imageView);
    }

}