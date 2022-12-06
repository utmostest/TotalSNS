package com.enos.totalsns.image;

import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.enos.totalsns.databinding.ItemImageBinding;
import com.enos.totalsns.util.GlideUtils;

/**
 * @author utmostest
 * Created 2022-12-05 at 오후 11:56
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private String[] urls;

    public ImageAdapter(String[] urls) {
        this.urls = urls;
    }

    @NonNull
    @Override
    public ImageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemImageBinding itemAccountBinding = ItemImageBinding.inflate(inflater, parent, false);
        return new ImageAdapter.ImageViewHolder(itemAccountBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ImageViewHolder holder, int position) {
        if (urls != null && urls.length > 0) {
            holder.binding(urls[position]);
        }
    }

    @Override
    public int getItemCount() {
        return urls.length;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ItemImageBinding binding;

        public ImageViewHolder(@NonNull ItemImageBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void binding(String imgUrl) {
            if (imgUrl.length() > 0) {
                GlideUtils.loadBigImage(binding.getRoot().getContext(), imgUrl, binding.imageView, new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        if (resource != null) {
                            final int imageHeight = resource.getHeight();
                            final int imageWidth = resource.getWidth();

                            int viewWidth = binding.imageView.getMeasuredWidth();
                            int viewHeight = binding.imageView.getMeasuredHeight();
                            float imageRatio = (float) imageHeight / (float) imageWidth;
                            float viewRatio = (float) viewHeight / (float) viewWidth;

                            int width = viewWidth;
                            int height = viewHeight;

                            if (imageRatio > viewRatio) {
                                width = (height * imageWidth) / imageHeight;
                            } else if (imageRatio < viewRatio) {
                                height = (width * imageHeight) / imageWidth;
                            }

                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
                            layoutParams.gravity = Gravity.CENTER;

                            binding.imageView.setLayoutParams(layoutParams);

//                            float zoomScale = 1.25f;
//
//                            width = (int) ((float) width * zoomScale);
//                            height = (int) ((float) height * zoomScale);

                            if (width >= imageWidth) {
                                binding.imageView.setImageBitmap(resource);
                            } else {
                                Bitmap resizedBmp = Bitmap.createScaledBitmap(resource, width, height, true);
                                binding.imageView.setImageBitmap(resizedBmp);
                            }
                        }
                        return true;
                    }
                });
            }
        }
    }

}