package com.enos.totalsns.timelinewrite;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enos.totalsns.R;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.databinding.FragmentTimelineWriteBinding;
import com.enos.totalsns.util.ViewModelFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class TimelineWriteFragment extends Fragment implements View.OnClickListener {

    private TimelineWriteViewModel mViewModel;
    FragmentTimelineWriteBinding mDataBinding;

    public static TimelineWriteFragment newInstance() {
        return new TimelineWriteFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timeline_write, container, false);
        initUI();
        return mDataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity(), ViewModelFactory.getInstance(getActivity())).get(TimelineWriteViewModel.class);

        initObserver();
        mViewModel.testSignIn();
    }

    private void initObserver() {
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
                break;
            case R.id.tl_write_picture:
                break;
            case R.id.tl_write_post:
                post();
                break;
        }
    }

    private void profileClicked() {

    }

    private void close() {
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        atomicBoolean.set(true);
        mViewModel.isShouldClose().setValue(atomicBoolean);
        Log.i("observer", "setValue : " + true);
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
