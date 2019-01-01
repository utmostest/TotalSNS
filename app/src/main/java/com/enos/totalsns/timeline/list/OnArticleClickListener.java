package com.enos.totalsns.timeline.list;

import android.widget.ImageView;

import com.enos.totalsns.data.Article;

public interface OnArticleClickListener {
    void onArticleClicked(Article mItem, int position);

    void onArticleImageClicked(ImageView iv, Article article, int position);
}
