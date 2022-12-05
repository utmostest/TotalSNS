package com.enos.totalsns.util;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.enos.totalsns.R;
import com.enos.totalsns.custom.autolink.AutoLinkMode;
import com.enos.totalsns.custom.autolink.AutoLinkOnClickListener;
import com.enos.totalsns.custom.autolink.AutoLinkTextView;

public class AutoLinkTextUtils {
    public static void set(final Context context, final AutoLinkTextView autoLinkTextView, final String message, AutoLinkOnClickListener listener) {
        autoLinkTextView.addAutoLinkMode(
                AutoLinkMode.MODE_HASHTAG,
                AutoLinkMode.MODE_MENTION,
                AutoLinkMode.MODE_URL,
                AutoLinkMode.MODE_PHONE,
                AutoLinkMode.MODE_EMAIL);
//                AutoLinkMode.MODE_CUSTOM);
        //step1 required add auto link mode
//        autoLinkTextView.setCustomRegex("\\d{4,7}");
        //step1 optional add custom regex

        autoLinkTextView.setHashtagModeColor(ContextCompat.getColor(context, R.color.red)); //setColor
        autoLinkTextView.setPhoneModeColor(ContextCompat.getColor(context, R.color.purple));
        autoLinkTextView.setCustomModeColor(ContextCompat.getColor(context, R.color.green));
        autoLinkTextView.setUrlModeColor(ContextCompat.getColor(context, R.color.blue));
        autoLinkTextView.setMentionModeColor(ContextCompat.getColor(context, R.color.orange));
        autoLinkTextView.setEmailModeColor(ContextCompat.getColor(context, R.color.gray));
        autoLinkTextView.setSelectedStateColor(ContextCompat.getColor(context, R.color.batang_white)); //clickedColor
        autoLinkTextView.setBoldAutoLinkModes(
                AutoLinkMode.MODE_HASHTAG,
                AutoLinkMode.MODE_PHONE,
                AutoLinkMode.MODE_URL,
                AutoLinkMode.MODE_EMAIL,
                AutoLinkMode.MODE_MENTION
        ); //bold
        autoLinkTextView.enableUnderLine(); //underline
        // step2 optional set mode color, selected color, bold, underline

        autoLinkTextView.setText(message);
        //step3 required settext

        autoLinkTextView.setAutoLinkOnClickListener(listener);
        //step4 required set on click listener
    }
}
