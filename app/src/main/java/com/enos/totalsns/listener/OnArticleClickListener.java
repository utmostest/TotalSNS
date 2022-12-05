package com.enos.totalsns.listener;

import android.widget.ImageView;

import com.enos.totalsns.custom.autolink.AutoLinkMode;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.databinding.ItemArticleBinding;

import java.util.HashMap;

public interface OnArticleClickListener {
    void onArticleClicked(ItemArticleBinding binding, Article mItem);

    void onArticleImageClicked(ImageView iv, Article article, int position);

    void onArticleProfileImgClicked(Article article);

    void onAutoLinkClicked(AutoLinkMode autoLinkMode, String text, HashMap<String, String> map);
}
