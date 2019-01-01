package com.enos.totalsns.mention;

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
import com.enos.totalsns.databinding.FragmentMentionBinding;
import com.enos.totalsns.timeline.list.OnArticleClickListener;
import com.enos.totalsns.util.ViewModelFactory;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnArticleClickListener}
 * interface.
 */
public class MentionListFragment extends Fragment {

    private MentionListViewModel mViewModel;
    private FragmentMentionBinding mDataBinding;
    private OnArticleClickListener mListener;

    public static MentionListFragment newInstance() {
        return new MentionListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getContext())).get(MentionListViewModel.class);
        mViewModel.fetchMentionForStart();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_mention, container, false);
        //Log.i("list", "onCreateView");
        return mDataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Log.i("list", "onActivityCreated");
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
                mViewModel.fetchRecentMention();
            } else if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
                mViewModel.fetchPastMention();
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mDataBinding.mentionRv.setLayoutManager(manager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                manager.getOrientation());
        mDataBinding.mentionRv.addItemDecoration(dividerItemDecoration);
        MentionAdapter adapter = new MentionAdapter(null, mListener);
        mDataBinding.mentionRv.setAdapter(adapter);

        mViewModel.getMention().observe(this, articleList -> {
            if (articleList != null) {
//                LinearLayoutManager lm = (LinearLayoutManager) mDataBinding.tlRv.getLayoutManager();
//                int currentPosFirst = lm.findFirstCompletelyVisibleItemPosition();

                adapter.swapMentionList(articleList);

//                if (currentPosFirst == 0)
//                    mDataBinding.tlRv.smoothScrollToPosition(0);
            }
        });
        mViewModel.isNetworkOnUse().observe(this, refresh -> mDataBinding.swipeContainer.setRefreshing(refresh));
    }
}
