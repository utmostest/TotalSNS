package com.enos.totalsns.timelines;

import com.enos.totalsns.data.Article;

import java.util.List;

public interface OnTimelineResult {
    void onReceivedTimeline(List<Article> articleList);
    void onFailedReceiveTimeline(String message);
}
