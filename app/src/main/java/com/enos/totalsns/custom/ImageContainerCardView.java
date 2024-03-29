package com.enos.totalsns.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.enos.totalsns.R;
import com.enos.totalsns.data.Constants;

import java.util.WeakHashMap;

public class ImageContainerCardView extends CardView {

    private LinearLayout imageContainer;

    // horizontal == 0 vertical == 1
    private int imageOrientation;

    private int imageCount;

    private final int DEFAULT_IMAGE_COUNT = 0;

    private WeakHashMap<Integer, ImageView> imageViewMap;

    private ImageView.ScaleType imageViewScaleType = ImageView.ScaleType.CENTER_CROP;

    @Nullable
    private OnImageClickedListener onImageClickedListener = null;

    public ImageContainerCardView(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    public ImageContainerCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ImageContainerCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        imageViewMap = new WeakHashMap<>();
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ImageContainerCardView, defStyleAttr, 0);

        try {
            imageOrientation = typedArray.getInteger(R.styleable.ImageContainerCardView_imageOrientation, LinearLayout.HORIZONTAL);
            imageCount = typedArray.getInteger(R.styleable.ImageContainerCardView_imageCount, DEFAULT_IMAGE_COUNT);
        } finally {
            typedArray.recycle();
        }

        imageContainer = new LinearLayout(context);
        imageContainer.setOrientation(imageOrientation);
        this.addView(imageContainer);
        clearLayoutAndFillImageView(imageCount);
    }

    private void clearLayoutAndFillImageView(int imageCount) {
        if (imageContainer == null) return;

        imageViewMap.clear();
        imageContainer.removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1;

        for (int i = 0; i < imageCount; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(imageViewScaleType);
            imageView.setLayoutParams(layoutParams);
            imageContainer.addView(imageView);
            final int position = i;
            imageView.setOnClickListener(v -> {
                if (onImageClickedListener != null)
                    onImageClickedListener.onImageClicked((ImageView) v, position);
            });
            imageViewMap.put(i, imageView);
        }
    }

    public int getImageOrientation() {
        return imageOrientation;
    }

    public void setImageOrientation(int imageOrientation) {
        this.imageOrientation = imageOrientation;
    }

    public int getImageCount() {
        return imageCount;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
        clearLayoutAndFillImageView(imageCount);
    }

    public WeakHashMap<Integer, ImageView> getImageViewRef() {
        return imageViewMap;
    }

    public OnImageClickedListener getOnImageClickedListener() {
        return onImageClickedListener;
    }

    public void setOnImageClickedListener(OnImageClickedListener onImageClickedListener) {
        this.onImageClickedListener = onImageClickedListener;
    }

    public void loadImageViewsWithGlide(RequestManager glide, String[] imgUrls) {
        if (imgUrls == null || imageViewMap == null) return;
        int size = Math.min(imgUrls.length, imageViewMap.size());
        for (int i = 0; i < size; i++) {
            ImageView iv = imageViewMap.get(i);
            glide.load(imgUrls[i])
                    .apply(
                            new RequestOptions()
                                    .dontTransform()
                    )
                    .transition(
                            new DrawableTransitionOptions()
                                    .crossFade(Constants.CROSS_FADE_MILLI)
                    )
                    .into(iv);
        }
    }

    public ImageView.ScaleType getImageViewScaleType() {
        return imageViewScaleType;
    }

    public void setImageViewScaleType(ImageView.ScaleType imageViewScaleType) {
        this.imageViewScaleType = imageViewScaleType;
        updateImageViewScaleType(imageViewScaleType);
    }

    private void updateImageViewScaleType(ImageView.ScaleType scaleType) {
        if (imageViewMap == null) return;
        int size = imageViewMap.size();
        for (int i = 0; i < size; i++) {
            ImageView iv = imageViewMap.get(i);
            iv.setScaleType(scaleType);
        }
    }
}
