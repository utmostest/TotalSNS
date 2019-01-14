package com.enos.totalsns.util;

import android.text.format.DateFormat;

import java.util.Date;

public class TimeUtils {
    public static String getDateString(long dateTime) {
        Date date = new Date();
        date.setTime(dateTime);
        CharSequence dateStr = DateFormat.format("yyyy.M.d h:m a", date);
        return dateTime == 0 ? "" : dateStr.toString();
    }

    public static long getSecondsByMilli(long quitDelayMilli) {
        return quitDelayMilli / 1000;
    }
}
