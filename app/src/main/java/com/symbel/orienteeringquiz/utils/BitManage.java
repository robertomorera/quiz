package com.symbel.orienteeringquiz.utils;


import android.app.Activity;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class BitManage {

    /**
     * DESCARGA DE IMAGEN CON GLIDE. MUY OPTIMO
     * @param url REQUIRED
     * @param imageView REQUIRED
     * @param activity REQUIRED
     */
    public static void loadBitmap(String url , final ImageView imageView, Activity activity) {
        Glide.with(activity)
                .load(url)
                .crossFade()
                .fitCenter()
                .into(imageView);
    }

    public static void loadUri(Uri uri, final ImageView imageView, Activity activity) {
        Glide.with(activity)
                .load(uri)
                .crossFade()
                .fitCenter()
                .into(imageView);
    }
}
