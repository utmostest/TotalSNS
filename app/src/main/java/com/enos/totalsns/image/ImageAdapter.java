package com.enos.totalsns.image;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.enos.totalsns.databinding.ItemImageBinding;
import com.enos.totalsns.util.GlideUtils;

/**
 * @author Jeonggu Kim
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
                GlideUtils.loadBigImage(binding.getRoot().getContext(), imgUrl, binding.imageView);
            }
        }
    }

}