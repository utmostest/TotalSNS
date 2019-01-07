package com.enos.totalsns.userlist;

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
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.data.source.remote.QueryFollow;
import com.enos.totalsns.data.source.remote.QuerySearchUser;
import com.enos.totalsns.databinding.FragmentFollowListBinding;
import com.enos.totalsns.util.ViewModelFactory;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.ArrayList;

public class UserListFragment extends Fragment {

    public static String ARG_QUERY_FOLLOW = "query-follow";
    public static String ARG_QUERY_SEARCH_USER = "query-search-user";

    private UserListViewModel mViewModel;
    private FragmentFollowListBinding dataBinding;
    private OnUserClickListener mListener;

    private QueryFollow follow;
    private QuerySearchUser searchUser;

    public static UserListFragment newInstance(QueryFollow queryFollow) {
        UserListFragment userListFragment = new UserListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_QUERY_FOLLOW, queryFollow);
        userListFragment.setArguments(bundle);
        return userListFragment;
    }

    public static UserListFragment newInstance(QuerySearchUser querySearchUser) {
        UserListFragment userListFragment = new UserListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_QUERY_SEARCH_USER, querySearchUser);
        userListFragment.setArguments(bundle);
        return userListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getContext())).get(UserListViewModel.class);

        if (getArguments() == null) return;
        follow = getArguments().getParcelable(ARG_QUERY_FOLLOW);
        searchUser = getArguments().getParcelable(ARG_QUERY_SEARCH_USER);
        if (follow != null) {
            mViewModel.fetchFirstFollowList(follow);
        } else if (searchUser != null) {
            mViewModel.fetchFirstSearchedUserList();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_follow_list, container, false);
        return dataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserClickListener) {
            mListener = (OnUserClickListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnUserClickListener");
        }
    }

    private void initView() {
        if (follow != null) {
            initViewForFollowList();
        } else if (searchUser != null) {
            initViewForSearchedUserList();
        }
    }

    private void initViewForFollowList() {
        dataBinding.swipeContainer.setOnRefreshListener(direction -> {
            if (direction == SwipyRefreshLayoutDirection.TOP) {
                mViewModel.fetchPreviousFollowList();
            } else if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
                mViewModel.fetchNextFollowList();
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        dataBinding.msgRv.setLayoutManager(manager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                manager.getOrientation());
        dataBinding.msgRv.addItemDecoration(dividerItemDecoration);
        UserAdapter adapter = new UserAdapter(null, mListener);
        dataBinding.msgRv.setAdapter(adapter);

        mViewModel.getUserFollowList().observe(this, list -> {
            ArrayList<UserInfo> current = (ArrayList<UserInfo>) adapter.getUserList();
            if (list == null) return;
            if (current != null) list.addAll(0, current);
            adapter.swapUserList(list);
        });
        mViewModel.isNetworkOnUse().observe(this, onUse -> {
            dataBinding.swipeContainer.setRefreshing(onUse);
        });
    }

    private void initViewForSearchedUserList() {
        dataBinding.swipeContainer.setOnRefreshListener(direction -> {
            if (direction == SwipyRefreshLayoutDirection.TOP) {
                dataBinding.swipeContainer.setRefreshing(false);
            } else if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
                mViewModel.fetchNextSearchedUserList();
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        dataBinding.msgRv.setLayoutManager(manager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                manager.getOrientation());
        dataBinding.msgRv.addItemDecoration(dividerItemDecoration);
        UserAdapter adapter = new UserAdapter(null, mListener);
        dataBinding.msgRv.setAdapter(adapter);

        mViewModel.getSearchedUserList().observe(this, list -> {
            ArrayList<UserInfo> current = (ArrayList<UserInfo>) adapter.getUserList();
            if (list == null) return;
            if (current != null) list.addAll(0, current);
            adapter.swapUserList(list);
        });
        mViewModel.isNetworkOnUse().observe(this, onUse -> {
            dataBinding.swipeContainer.setRefreshing(onUse);
        });
    }
}
