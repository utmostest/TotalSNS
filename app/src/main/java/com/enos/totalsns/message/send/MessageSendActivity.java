package com.enos.totalsns.message.send;

import android.content.Intent;
import android.os.Bundle;


import com.enos.totalsns.R;
import com.enos.totalsns.util.ActivityUtils;

import androidx.appcompat.app.AppCompatActivity;

public class MessageSendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_send);
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
