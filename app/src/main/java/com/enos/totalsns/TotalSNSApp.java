package com.enos.totalsns;

import android.content.SharedPreferences;

import androidx.multidex.MultiDexApplication;
import androidx.preference.PreferenceManager;

import com.enos.totalsns.util.SingletonToast;
import com.enos.totalsns.util.ThemeHelper;

public class TotalSNSApp extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        SingletonToast.getInstance(this);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String themePref = sharedPreferences.getString(ThemeHelper.THEME_PREF_KEY, ThemeHelper.DEFAULT_MODE);

        ThemeHelper.applyTheme(themePref);
    }
}
