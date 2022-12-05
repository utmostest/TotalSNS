package com.enos.totalsns.nearby;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.enos.totalsns.R;
import com.enos.totalsns.databinding.ActivityNearbyArticleBinding;
import com.enos.totalsns.util.ActivityUtils;

public class NearbyArticleActivity extends AppCompatActivity {

    ActivityNearbyArticleBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityNearbyArticleBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
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
