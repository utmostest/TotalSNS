package com.enos.totalsns.message.send;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.enos.totalsns.R;

public class MessageSendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_send);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MessageSendFragment.newInstance())
                    .commitNow();
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MessageSendActivity.class);
        context.startActivity(intent);
    }
}
