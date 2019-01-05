package com.enos.totalsns.follow;

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
import com.enos.totalsns.data.source.remote.QueryFollow;
import com.enos.totalsns.databinding.FragmentFollowListBinding;
import com.enos.totalsns.search.OnUserClickListener;
import com.enos.totalsns.util.ViewModelFactory;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

public class FollowListFragment extends Fragment {

    public static String ARG_QUERY_FOLLOW = "query-follow";
    private FollowListViewModel mViewModel;
    private FragmentFollowListBinding dataBinding;
    private OnUserClickListener mListener;

    public static FollowListFragment newInstance(QueryFollow queryFollow) {
        FollowListFragment followListFragment = new FollowListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_QUERY_FOLLOW, queryFollow);
        followListFragment.setArguments(bundle);
        return followListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getContext())).get(FollowListViewModel.class);
        if (getArguments() != null) {
            QueryFollow follow = getArguments().getParcelable(ARG_QUERY_FOLLOW);
            mViewModel.fetchFirstFollowList(follow);
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
        FollowAdapter adapter = new FollowAdapter(null, mListener);
        dataBinding.msgRv.setAdapter(adapter);

        mViewModel.getUserFollowList().observe(this, list -> {
            if (list == null) return;
            for (int i = 0; i < list.size(); i++) {
                adapter.swapUserList(list);
            }
        });
        mViewModel.isNetworkOnUse().observe(this, onUse -> {
            dataBinding.swipeContainer.setRefreshing(onUse);
        });
    }
}
