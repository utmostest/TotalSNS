package com.enos.totalsns.timeline.list;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.enos.totalsns.R;
import com.enos.totalsns.databinding.FragmentTimelineListBinding;
import com.enos.totalsns.listener.OnArticleClickListener;
import com.enos.totalsns.util.ViewModelFactory;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

public class TimelineListFragment extends Fragment {

    private TimelineListViewModel mViewModel;
    private FragmentTimelineListBinding mBinding;
    private OnArticleClickListener mListener;

    public static TimelineListFragment newInstance() {
        return new TimelineListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, (ViewModelProvider.Factory) ViewModelFactory.getInstance(getContext())).get(TimelineListViewModel.class);
        mViewModel.fetchTimelineForStart();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timeline_list, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initContentUI();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnArticleClickListener) {
            mListener = (OnArticleClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnArticleClickListener");
        }
    }

    private void initContentUI() {
        if (mBinding == null) return;

        mBinding.swipeContainer.setOnRefreshListener(direction -> {
            if (direction == SwipyRefreshLayoutDirection.TOP) {
                mViewModel.fetchRecentTimeline();
            } else if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
                mViewModel.fetchPastTimeline();
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mBinding.tlRv.setLayoutManager(manager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                manager.getOrientation());
        mBinding.tlRv.addItemDecoration(dividerItemDecoration);
        TimelineAdapter adapter = new TimelineAdapter(null, mListener, mViewModel);
        mBinding.tlRv.setAdapter(adapter);

        mViewModel.getHomeTimeline().observe(getViewLifecycleOwner(), articleList -> {
//                LinearLayoutManager lm = (LinearLayoutManager) mBinding.tlRv.getLayoutManager();
//                int currentPosFirst = lm.findFirstCompletelyVisibleItemPosition();
            if (mViewModel.isBetweenFetching()) {
                mViewModel.setBetweenFetching(false);
            }
            adapter.swapTimelineList(articleList);
            adapter.notifyDataSetChanged();
//                if (currentPosFirst == 0)
//                    mBinding.tlRv.smoothScrollToPosition(0);
        });
        mViewModel.isNetworkOnUse().observe(getViewLifecycleOwner(), refresh -> {
            if (refresh == null) return;
            mBinding.swipeContainer.setRefreshing(refresh);
        });
    }
}
