package com.enos.totalsns.interfaces;

import com.enos.totalsns.data.Article;

import java.util.List;

public interface OnTimelineResult {
    void onReceivedTimeline(List<Article> articleList);
    void onFailedReceiveTimeline(String message);
}
