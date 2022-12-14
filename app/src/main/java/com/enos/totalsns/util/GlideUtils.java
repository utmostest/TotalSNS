package com.enos.totalsns.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.enos.totalsns.R;
import com.enos.totalsns.data.Constants;

public class GlideUtils {
    public static void loadProfileImage(Context context, String imageUrl, ImageView imageView) {
        loadProfileImage(context, imageUrl, imageView, R.drawable.ic_account);
    }

    public static void loadProfileImage(Context context, String imageUrl, ImageView imageView, RequestListener<Drawable> callback) {
        Glide.with(context)
                .load(StringUtils.checkHttpSchemeAndInsertIfNotExist(imageUrl))
                .apply(
                        new RequestOptions()
                                .placeholder(R.drawable.ic_account)
                                .dontTransform()
                                .optionalCircleCrop()
                )
                .listener(callback)
                .into(imageView);
    }

    public static void loadProfileImage(Context context, String imageUrl, ImageView imageView, int res) {
        loadProfileImage(context, imageUrl, imageView, res, null);
    }

    public static void loadProfileImage(Context context, String imageUrl, SimpleTarget<Drawable> target) {
        Glide.with(context)
                .load(StringUtils.checkHttpSchemeAndInsertIfNotExist(imageUrl))
                .apply(
                        new RequestOptions()
                                .placeholder(R.drawable.ic_account)
                                .dontTransform()
                                .optionalCircleCrop()
                )
                .transition(
                        new DrawableTransitionOptions()
                                .crossFade(Constants.CROSS_FADE_MILLI)
                )
                .into(target);
    }

    public static void loadProfileImage(Context context, String imageUrl, ImageView imageView, int res, RequestListener<Drawable> callback) {
        Glide.with(context)
                .load(StringUtils.checkHttpSchemeAndInsertIfNotExist(imageUrl))
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
                .listener(callback)
                .into(imageView);
    }

    public static void loadBackImageWithCallback(Context context, String imageUrl, ImageView imageView, RequestListener<Bitmap> callback) {
        Glide.with(context)
                .asBitmap()
                .load(StringUtils.checkHttpSchemeAndInsertIfNotExist(imageUrl))
                .apply(
                        new RequestOptions()
                                .dontTransform()
                                .centerCrop()
                )
                .listener(callback)
                .into(imageView);
    }

    public static void loadProfileImageWithTarget(Context context, String image, int imageSize, Target<Bitmap> target) {
        Glide.with(context)
                .asBitmap()
                .load(StringUtils.checkHttpSchemeAndInsertIfNotExist(image))
                .apply(
                        new RequestOptions()
                                .placeholder(R.drawable.ic_account)
                                .dontTransform()
                                .override(imageSize)
                                .centerCrop()
                                .circleCrop()
                )
                .into(target);
    }

    public static void loadBackImage(Context context, String imageUrl, ImageView imageView) {
        loadBackImage(context, imageUrl, imageView, null);
    }

    public static void loadBackImage(Context context, String imageUrl, ImageView imageView, RequestListener<Drawable> callback) {
        Glide.with(context)
                .load(StringUtils.checkHttpSchemeAndInsertIfNotExist(imageUrl))
                .apply(
                        new RequestOptions()
                                .dontTransform()
                                .centerCrop()
                )
                .listener(callback)
                .into(imageView);
    }

    public static void loadBigImage(Context context, String imageUrl, ImageView imageView, RequestListener<Bitmap> callback) {
        Glide.with(context)
                .asBitmap()
                .load(StringUtils.checkHttpSchemeAndInsertIfNotExist(imageUrl))
                .apply(
                        new RequestOptions()
                                .dontTransform()
                )
                .listener(callback)
                .into(imageView);
    }

    public static void loadBigImage(Context context, String imageUrl, ImageView imageView) {
        Glide.with(context)
                .load(StringUtils.checkHttpSchemeAndInsertIfNotExist(imageUrl))
                .apply(
                        new RequestOptions()
                                .dontTransform()
                )
                .into(imageView);
    }
}
