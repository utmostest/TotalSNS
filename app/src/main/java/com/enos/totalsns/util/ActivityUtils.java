package com.enos.totalsns.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.enos.totalsns.R;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.util.autolink.AutoLinkMode;
import com.enos.totalsns.util.autolink.AutoLinkTextView;

public class ActivityUtils {
    public static void setAutoLinkTextView(final Context context, final AutoLinkTextView autoLinkTextView, final Article article) {
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
        autoLinkTextView.setPhoneModeColor(ContextCompat.getColor(context, R.color.text_yellow));
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

        autoLinkTextView.setText(article.getMessage());
        //step3 required settext

        autoLinkTextView.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> {
            Toast.makeText(context, autoLinkMode + " : " + matchedText, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            switch (autoLinkMode) {
                case MODE_URL:
                    Log.i("url", matchedText);
                    String url;
                    if (article.getUrlMap().containsKey(matchedText)) {
                        url = article.getUrlMap().get(matchedText);
                    } else {
                        url = matchedText;
                    }
                    Log.i("url", url + "");
                    if (url == null) url = matchedText;
                    if (!matchedText.contains("http://") && !matchedText.contains("https://")) {
                        url = "http://" + url;
                    }
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndNormalize(Uri.parse(url));
                    checkResolveAndStartActivity(intent, context);
                    return;
                case MODE_PHONE:
                    intent.setAction(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + matchedText));
                    checkResolveAndStartActivity(intent, context);
                    return;
                case MODE_EMAIL:
                    intent.setAction(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{matchedText});
                    checkResolveAndStartActivity(intent, context);
                    return;
                case MODE_HASHTAG:
                    return;
                case MODE_MENTION:
                    return;
            }
        });
        //step4 required set on click listener
    }

    private static void startActivity(Intent intent, Context context) {
        context.startActivity(intent);
    }

    private static void checkResolveAndStartActivity(Intent intent, Context context) {
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }
}
