package com.enos.totalsns.util;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.app.SharedElementCallback;
import androidx.core.util.Pair;

import java.util.Iterator;
import java.util.List;

public class ActivityUtils {

    public static void startActivity(AppCompatActivity context, Intent intent) {
        context.startActivity(intent);
    }

    public static void checkResolveAndStartActivity(Intent intent, AppCompatActivity context) {
        if (intent.resolveActivity(context.getPackageManager()) != null) {
//            context.startActivity(intent);
            startActivity(context, intent);
        }
    }

    public static void startActivityWithTransition(AppCompatActivity context, Intent intent, List<Pair<View, String>> pairList) {
        // call before activity setContsView
        // Apply activity transition inside your activity (if you did not enable transitions in your theme)
//            context.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        // set an exit transition
//            context.getWindow().setExitTransition(new Explode());
        Pair<View, String>[] pairs = getVisiblePairs(pairList);
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

    public static boolean requestPermissionIfNeeded(AppCompatActivity context, int REQUEST_PERMISSION, String... PERMISSIONS) {
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

    public static Pair<View, String>[] getVisiblePairs(List<Pair<View, String>> pairs) {
        Iterator<Pair<View, String>> iter = pairs.iterator();

        while (iter.hasNext()) {
            Pair<View, String> p = iter.next();
            if (p.first == null || p.first.getVisibility() == View.GONE) iter.remove();
        }
        Pair<View, String>[] array = new Pair[0];
        return pairs.toArray(array);
    }
}
