package com.enos.totalsns.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.enos.totalsns.R;
import com.enos.totalsns.util.autolink.AutoLinkMode;
import com.enos.totalsns.util.autolink.AutoLinkOnClickListener;
import com.enos.totalsns.util.autolink.AutoLinkTextView;

import java.util.HashMap;
import java.util.List;

public class ActivityUtils {

    public static void startActivity(Intent intent, Context context) {
        context.startActivity(intent);
    }

    public static void checkResolveAndStartActivity(Intent intent, Context context) {
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public static void startActivityWithTransition(AppCompatActivity context, Intent intent, Pair<View, String>... pairs) {

        // call before activity setContsView
        // Apply activity transition inside your activity (if you did not enable transitions in your theme)
//            context.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        // set an exit transition
//            context.getWindow().setExitTransition(new Explode());

        context.startActivity(intent,
                ActivityOptionsCompat.makeSceneTransitionAnimation(context, pairs).toBundle());
    }

    public static void setExitCallback(AppCompatActivity activity) {
        activity.setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onSharedElementEnd(List<String> names, List<View> elements, List<View> snapshots) {
                super.onSharedElementEnd(names, elements, snapshots);
            }
        });

    }

    public static void setEnterCallback(AppCompatActivity activity) {
        activity.setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onSharedElementEnd(List<String> names,
                                           List<View> elements,
                                           List<View> snapshots) {
                super.onSharedElementEnd(names, elements, snapshots);
            }
        });
    }

    public static boolean requestPermissionIfNeeded(Activity context, int REQUEST_PERMISSION, String... PERMISSIONS) {
        boolean permissionNeeded = false;
        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionNeeded = true;
                break;
            }
        }
        if (permissionNeeded) {
            ActivityCompat.requestPermissions(context, PERMISSIONS, REQUEST_PERMISSION);
        }

        return permissionNeeded;
    }
}
