package com.enos.totalsns.message.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.enos.totalsns.R;
import com.enos.totalsns.data.Message;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.listener.OnMessageClickListener;
import com.enos.totalsns.util.ActivityUtils;
import com.enos.totalsns.util.SingletonToast;
import com.enos.totalsns.util.ViewModelFactory;

public class MessageDetailActivity extends AppCompatActivity implements OnMessageClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        if (savedInstanceState == null) {
            long userId = getIntent().getLongExtra(MessageDetailFragment.COLUMN_SENDER_ID, MessageDetailFragment.INVALID_SENDER_ID);
            initFragment(userId);
            initActionBar(userId);
        }
    }

    private void initActionBar(long userId) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        MessageDetailViewModel messageDetailViewModel =
                ViewModelProviders.of(this, ViewModelFactory.getInstance(this)).get(MessageDetailViewModel.class);
        UserInfo current = messageDetailViewModel.getUserFromCache(userId);
        setTitle(getString(R.string.title_message_detail, current == null ? "USER" : current.getUserId()));
    }

    private void initFragment(long senderTableId) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, MessageDetailFragment.newInstance(senderTableId)).commitNow();
    }

    @Override
    public void onMessageClicked(Message message) {
        SingletonToast.getInstance().log(message.getMessage());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
//            navigateUpTo(new Intent(this, ContentsActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void start(AppCompatActivity context, long senderId) {
        Intent intent = new Intent(context, MessageDetailActivity.class);
        intent.putExtra(MessageDetailFragment.COLUMN_SENDER_ID, senderId);
//        context.startActivity(intent);
        ActivityUtils.startActivity(context, intent);
    }
}
