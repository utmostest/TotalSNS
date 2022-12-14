package com.enos.totalsns.mention;

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
import com.enos.totalsns.databinding.FragmentMentionBinding;
import com.enos.totalsns.listener.OnArticleClickListener;
import com.enos.totalsns.timeline.list.TimelineAdapter;
import com.enos.totalsns.util.ViewModelFactory;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

public class MentionListFragment extends Fragment {

    private MentionListViewModel mViewModel;
    private FragmentMentionBinding mBinding;
    private OnArticleClickListener mListener;

    public static MentionListFragment newInstance() {
        return new MentionListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, (ViewModelProvider.Factory) ViewModelFactory.getInstance(getContext())).get(MentionListViewModel.class);
        mViewModel.fetchMentionForStart();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_mention, container, false);
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
                mViewModel.fetchRecentMention();
            } else if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
                mViewModel.fetchPastMention();
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mBinding.mentionRv.setLayoutManager(manager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                manager.getOrientation());
        mBinding.mentionRv.addItemDecoration(dividerItemDecoration);
        TimelineAdapter adapter = new TimelineAdapter(null, mListener, mViewModel);
        mBinding.mentionRv.setAdapter(adapter);

        mViewModel.getMention().observe(getViewLifecycleOwner(), articleList -> {
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
