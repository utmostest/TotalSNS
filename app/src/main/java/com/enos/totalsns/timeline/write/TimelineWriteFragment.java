package com.enos.totalsns.timeline.write;

import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.enos.totalsns.R;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.databinding.FragmentTimelineWriteBinding;
import com.enos.totalsns.util.GlideUtils;
import com.enos.totalsns.util.ViewModelFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class TimelineWriteFragment extends Fragment implements View.OnClickListener {

    private TimelineWriteViewModel mViewModel;
    FragmentTimelineWriteBinding mDataBinding;

    public static TimelineWriteFragment newInstance() {
        return new TimelineWriteFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity(), ViewModelFactory.getInstance(getActivity())).get(TimelineWriteViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timeline_write, container, false);
        return mDataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUI();
        initObserver();
    }

    private void initObserver() {
        mViewModel.getCurrentUser().observe(this, (user) -> {
            if (user == null) return;
            GlideUtils.loadProfileImage(getContext(), user.getProfileImg(), mDataBinding.tlWriteAccount, R.drawable.ic_account_circle_black_36dp);
        });
    }

    private void initUI() {
        if (mDataBinding != null) {
            mDataBinding.tlWriteAccount.setOnClickListener(this);
            mDataBinding.tlWriteLocation.setOnClickListener(this);
            mDataBinding.tlWritePicture.setOnClickListener(this);
            mDataBinding.tlWritePost.setOnClickListener(this);
            mDataBinding.tlWriteClose.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tl_write_close:
                close();
                break;
            case R.id.tl_write_account:
                profileClicked();
                break;
            case R.id.tl_write_location:
                Toast.makeText(getContext(), "TODO : add location / place selector", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tl_write_picture:
                Toast.makeText(getContext(), "TODO : add image selector", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tl_write_post:
                post();
                break;
        }
    }

    private void profileClicked() {
        Toast.makeText(getContext(), "TODO : add account selector", Toast.LENGTH_SHORT).show();
    }

    private void close() {
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        atomicBoolean.set(true);
        mViewModel.isShouldClose().setValue(atomicBoolean);
    }

    private void post() {

        Editable editable = mDataBinding.tlWriteEdit.getText();
        if (editable != null && editable.length() > 0) {
            Article article = new Article();
            article.setMessage(mDataBinding.tlWriteEdit.getText().toString());
            article.setSnsType(Constants.TWITTER);

            mViewModel.postArticle(article);
        }
    }
}
