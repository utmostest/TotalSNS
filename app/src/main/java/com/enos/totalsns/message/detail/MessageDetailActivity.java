package com.enos.totalsns.message.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.enos.totalsns.R;
import com.enos.totalsns.data.Message;
import com.enos.totalsns.message.OnMessageClickListener;
import com.enos.totalsns.profile.ProfileActivity;
import com.enos.totalsns.profile.ProfileFragment;
import com.enos.totalsns.util.SingletonToast;

public class MessageDetailActivity extends AppCompatActivity implements OnMessageClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        if (savedInstanceState == null) {
            initFragment(getIntent().getLongExtra(MessageDetailFragment.COLUMN_SENDER_ID, MessageDetailFragment.INVALID_SENDER_ID));
        }

        initActionBar();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initFragment(long senderTableId) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, MessageDetailFragment.newInstance(senderTableId)).commitNow();
    }

    @Override
    public void onMessageClicked(Message message) {
        SingletonToast.getInstance().log(message.getMessage());
    }

    @Override
    public void onMessageProfileClicked(long senderTableId) {
        startProfileActivity(senderTableId);
    }

    private void startProfileActivity(long userId) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileFragment.ARG_USER_ID, userId);
        startActivity(intent);
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
}
