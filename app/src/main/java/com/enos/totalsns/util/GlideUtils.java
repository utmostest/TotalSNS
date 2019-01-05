package com.enos.totalsns.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.enos.totalsns.R;
import com.enos.totalsns.data.Constants;

public class GlideUtils {
    public static void loadProfileImage(Context context, String imageUrl, ImageView imageView) {
        loadProfileImage(context, imageUrl, imageView, R.drawable.ic_account_circle_black_48dp);
    }

    public static void loadProfileImage(Context context, String imageUrl, ImageView imageView, int res) {
        Glide.with(context)
                .load(imageUrl)
                .apply(
                        new RequestOptions()
                                .placeholder(res)
                                .dontTransform()
                                .optionalCircleCrop()
                )
                .transition(
                        new DrawableTransitionOptions()
                                .crossFade(Constants.CROSS_FADE_MILLI)
                )
                .into(imageView);
    }

    public static void loadBackImageWithCallback(Context context, String imageUrl, ImageView imageView, RequestListener<Bitmap> callback) {
        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .apply(
                        new RequestOptions()
                                .placeholder(R.drawable.side_nav_bar)
                                .dontTransform()
                                .centerCrop()
                )
                .listener(callback)
                .into(imageView);
    }

    public static void loadProfileImageWithTarget(Context context, String image, int imageSize, SimpleTarget<Bitmap> target) {
        Glide.with(context)
                .asBitmap()
                .load(image)
                .apply(
                        new RequestOptions()
                                .placeholder(R.drawable.ic_account_circle_black_48dp)
                                .dontTransform()
                                .override(imageSize)
                                .centerCrop()
                                .circleCrop()
                )
                .into(target);
    }

    public static void loadBackImage(Context context, String imageUrl, ImageView imageView) {
        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .apply(
                        new RequestOptions()
                                .placeholder(R.drawable.side_nav_bar)
                                .dontTransform()
                                .centerCrop()
                )
                .into(imageView);
    }
}
