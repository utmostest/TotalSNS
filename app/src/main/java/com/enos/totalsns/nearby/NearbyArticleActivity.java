package com.enos.totalsns.nearby;

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
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        getSupportFragmentManager().beginTransaction().add(R.id.container, NearbyArticleFragment.newInstance(), NearbyArticleFragment.class.getSimpleName()).commitNow();
    }
}
