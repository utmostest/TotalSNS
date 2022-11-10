package com.enos.totalsns.message.send;

import android.content.Intent;
import android.os.Bundle;


import com.enos.totalsns.R;
import com.enos.totalsns.databinding.ActivityMessageSendBinding;
import com.enos.totalsns.util.ActivityUtils;

import androidx.appcompat.app.AppCompatActivity;

public class MessageSendActivity extends AppCompatActivity {

    ActivityMessageSendBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMessageSendBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setTitle(R.string.title_dm_send_to);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MessageSendFragment.newInstance())
                    .commitNow();
        }
    }

    public static void start(AppCompatActivity context) {
        Intent intent = new Intent(context, MessageSendActivity.class);
//        context.startActivity(intent);
        ActivityUtils.startActivity(context, intent);
    }
}
