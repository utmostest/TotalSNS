package com.enos.totalsns.timeline.list;

import com.enos.totalsns.data.Article;

import java.util.List;

public interface OnTimelineResult {
    void onReceivedTimeline(List<Article> articleList);
    void onFailedReceiveTimeline(String message);
}
