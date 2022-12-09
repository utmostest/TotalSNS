package com.enos.totalsns.message.detail;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.enos.totalsns.R;
import com.enos.totalsns.data.Message;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.databinding.ActivityMessageDetailBinding;
import com.enos.totalsns.listener.OnMessageClickListener;
import com.enos.totalsns.util.ActivityUtils;
import com.enos.totalsns.util.SingletonToast;

public class MessageDetailActivity extends AppCompatActivity implements OnMessageClickListener {

    ActivityMessageDetailBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMessageDetailBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        if (savedInstanceState == null) {
            UserInfo receiver = getIntent().getParcelableExtra(MessageDetailFragment.COLUMN_SENDER_MSG);
            initFragment(receiver);
            initActionBar(receiver);
        }
    }

    private void initActionBar(UserInfo receiver) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(getString(R.string.title_message_detail, receiver == null ? "USER" : receiver.getUserId()));
    }

    private void initFragment(UserInfo receiver) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, MessageDetailFragment.newInstance(receiver)).commitNow();
    }

    @Override
    public void onMessageClicked(Message message) {
        SingletonToast.getInstance().log(message.getMessage());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void start(AppCompatActivity context, UserInfo receiver) {
        Intent intent = new Intent(context, MessageDetailActivity.class);
        intent.putExtra(MessageDetailFragment.COLUMN_SENDER_MSG, receiver);
//        context.startActivity(intent);
        ActivityUtils.startActivity(context, intent);
    }
}
