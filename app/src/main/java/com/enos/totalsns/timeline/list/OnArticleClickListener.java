package com.enos.totalsns.timeline.list;

import com.enos.totalsns.data.Article;
import com.enos.totalsns.databinding.ItemArticleBinding;

public interface OnArticleClickListener {
    void onArticleClicked(ItemArticleBinding binding, Article mItem, int position);
}
