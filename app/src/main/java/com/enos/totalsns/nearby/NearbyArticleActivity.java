package com.enos.totalsns.nearby;

import android.content.Intent;
import android.os.Bundle;

import com.enos.totalsns.R;
import com.enos.totalsns.util.ActivityUtils;

import androidx.appcompat.app.AppCompatActivity;

public class NearbyArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_article);
        setTitle(R.string.nearby_artilce);
        initFragment();
    }

    private void initFragment() {
        getSupportFragmentManager().beginTransaction().add(R.id.container, NearbyArticleFragment.newInstance(), NearbyArticleFragment.class.getSimpleName()).commitNow();
    }

    public static void start(AppCompatActivity context) {
        Intent intent = new Intent(context, NearbyArticleActivity.class);
//        context.startActivity(intent);
        ActivityUtils.startActivity(context, intent);
    }
}
