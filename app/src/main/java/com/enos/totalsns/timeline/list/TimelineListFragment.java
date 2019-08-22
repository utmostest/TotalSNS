package com.enos.totalsns.timeline.list;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enos.totalsns.R;
import com.enos.totalsns.databinding.FragmentTimelineListBinding;
import com.enos.totalsns.listener.OnArticleClickListener;
import com.enos.totalsns.util.ViewModelFactory;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

public class TimelineListFragment extends Fragment {

    private TimelineListViewModel mViewModel;
    private FragmentTimelineListBinding mDataBinding;
    private OnArticleClickListener mListener;

    public static TimelineListFragment newInstance() {
        return new TimelineListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getContext())).get(TimelineListViewModel.class);
        mViewModel.fetchTimelineForStart();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timeline_list, container, false);
        return mDataBinding.getRoot();
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
        if (mDataBinding == null) return;

        mDataBinding.swipeContainer.setOnRefreshListener(direction -> {
            if (direction == SwipyRefreshLayoutDirection.TOP) {
                mViewModel.fetchRecentTimeline();
            } else if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
                mViewModel.fetchPastTimeline();
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mDataBinding.tlRv.setLayoutManager(manager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                manager.getOrientation());
        mDataBinding.tlRv.addItemDecoration(dividerItemDecoration);
        TimelineAdapter adapter = new TimelineAdapter(null, mListener);
        mDataBinding.tlRv.setAdapter(adapter);

        mViewModel.getHomeTimeline().observe(this, articleList -> {
//                LinearLayoutManager lm = (LinearLayoutManager) mDataBinding.tlRv.getLayoutManager();
//                int currentPosFirst = lm.findFirstCompletelyVisibleItemPosition();

            adapter.swapTimelineList(articleList);

//                if (currentPosFirst == 0)
//                    mDataBinding.tlRv.smoothScrollToPosition(0);
        });
        mViewModel.isNetworkOnUse().observe(this, refresh -> {
            if (refresh == null) return;
            mDataBinding.swipeContainer.setRefreshing(refresh);
        });
    }
}
