package com.enos.totalsns.custom;

import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.sangcomz.fishbun.adapter.image.ImageAdapter;

/**
 * @author utmostest
 * Created 2022-12-27 at 오후 2:00
 */
public class GlideCustomAdapter implements ImageAdapter {
    @Override
    public void loadDetailImage(@NonNull ImageView imageView, @NonNull Uri uri) {
        RequestOptions options = new RequestOptions().fitCenter();
        Glide
                .with(imageView.getContext())
                .load(uri)
                .apply(options)
                .into(imageView);
    }

    @Override
    public void loadImage(@NonNull ImageView imageView, @NonNull Uri uri) {
        RequestOptions options = new RequestOptions().centerCrop().format(DecodeFormat.PREFER_RGB_565).override(imageView.getWidth(), imageView.getHeight());

        Glide
                .with(imageView.getContext())
                .load(uri)
                .apply(options)
                .thumbnail(0.1f)
                .into(imageView);

    }
}
