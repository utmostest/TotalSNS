package com.enos.totalsns.profile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enos.totalsns.R;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.databinding.FragmentProfileBinding;
import com.enos.totalsns.timeline.list.OnArticleClickListener;
import com.enos.totalsns.userlist.OnFollowListener;
import com.enos.totalsns.util.ViewModelFactory;

import java.util.List;

public class ProfileFragment extends Fragment {

    private ProfileViewModel mViewModel;

    public static final String ARG_USER_INFO = "user-info";
    public static final String ARG_USER_ID = "user-id";
    public static final long INVALID_ID = -1;
    private FragmentProfileBinding dataBinding;
    private OnFollowListener mListener;
    private OnArticleClickListener articleListener;
    private UserInfo userInfo;
    private long userId = INVALID_ID;

    public static ProfileFragment newInstance(UserInfo userInfo) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARG_USER_INFO, userInfo);
        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setArguments(arguments);
        return profileFragment;
    }

    public static Fragment newInstance(long userId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_USER_ID, userId);
        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setArguments(arguments);
        return profileFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userInfo = getArguments().getParcelable(ARG_USER_INFO);
            userId = getArguments().getLong(ARG_USER_ID);
        }
        mViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getContext())).get(ProfileViewModel.class);
        if (userInfo != null) {
            fetchUserTimelineFirst(userInfo.getLongUserId());
        } else if (userId > INVALID_ID) {
            mViewModel.fetchProfile(userId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        return dataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (userInfo != null) initView();
        initObserver();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFollowListener && context instanceof OnArticleClickListener) {
            mListener = (OnFollowListener) context;
            articleListener = (OnArticleClickListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFollowListener and OnArticleClickListener");
        }
    }

    private void initView() {
        if (dataBinding == null) return;

        LinearLayoutManager managerVertical = new LinearLayoutManager(getContext());
        managerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        ProfileAdapter searchAdapter = new ProfileAdapter(userInfo, mViewModel.getUserTimeline().getValue(), articleListener, mListener);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                managerVertical.getOrientation());
        searchAdapter.setEnableHeader(true, true);
        dataBinding.profileRv.addItemDecoration(dividerItemDecoration);
        dataBinding.profileRv.setAdapter(searchAdapter);
        dataBinding.swipeContainer.setOnRefreshListener(direction -> {
            switch (direction) {
                case TOP:
                    if (isUserTimelineAvailable()) {
                        mViewModel.fetchUserTimelineRecent();
                    } else {
                        dataBinding.swipeContainer.setRefreshing(false);
                    }
                    break;
                case BOTTOM:
                    if (isUserTimelineAvailable()) {
                        mViewModel.fetchUserTimelinePast();
                    } else {
                        dataBinding.swipeContainer.setRefreshing(false);
                    }
                    break;
            }
        });
    }

    private void initObserver() {
        mViewModel.getUserTimeline().observe(this, (list) -> {
            if (dataBinding.profileRv.getAdapter() == null) initView();
            ProfileAdapter adapter = (ProfileAdapter) dataBinding.profileRv.getAdapter();
            List<Article> old = adapter.getArticleList();
            if (old != null && old.size() > 0) {
                Article oldLast = old.get(0);
                Article oldFirst = old.get(old.size() - 1);
                if (list != null && list.size() > 0) {
                    Article last = list.get(0);
                    Article first = list.get(list.size() - 1);
                    if (first.getPostedAt() > oldLast.getPostedAt()) {
                        list.addAll(list.size(), old);
                    } else if (last.getPostedAt() < oldFirst.getPostedAt()) {
                        list.addAll(0, old);
                    }
                } else if (list != null) {
                    list.addAll(old);
                }
            }
            adapter.swapTimelineList(list);
        });
        mViewModel.isNetworkOnUse().observe(this, refresh -> {
            if (refresh == null) return;
            dataBinding.swipeContainer.setRefreshing(refresh);
        });
        mViewModel.getUserProfile().observe(this, profile -> {
            if (userInfo == null && profile != null) {
                userInfo = profile;
                fetchUserTimelineFirst(profile.getLongUserId());
                initView();
            }
        });
    }

    private boolean isUserTimelineAvailable() {
        if (userInfo == null) return false;
        return !userInfo.isProtected();
    }

    private void fetchUserTimelineFirst(long userId) {
        if (isUserTimelineAvailable()) mViewModel.fetchUserTimelineFirst(userId);
    }
}
