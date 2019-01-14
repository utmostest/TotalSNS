package com.enos.totalsns.nearby;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.enos.totalsns.R;

public class NearbyArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_article);

        initFragment();
    }

    private void initFragment() {
        getSupportFragmentManager().beginTransaction().add(R.id.container, NearbyArticleFragment.newInstance(), NearbyArticleFragment.class.getSimpleName()).commitNow();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, NearbyArticleActivity.class);
        context.startActivity(intent);
    }
}
