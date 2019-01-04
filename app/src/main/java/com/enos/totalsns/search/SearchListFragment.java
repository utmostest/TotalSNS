package com.enos.totalsns.search;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enos.totalsns.R;
import com.enos.totalsns.databinding.FragmentSearchBinding;
import com.enos.totalsns.timeline.list.OnArticleClickListener;
import com.enos.totalsns.timeline.list.TimelineAdapter;
import com.enos.totalsns.util.ViewModelFactory;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnUserClickListener} {@link OnArticleClickListener}
 * interface.
 */
public class SearchListFragment extends Fragment {

    private OnUserClickListener mListener;
    private OnArticleClickListener mArticleListener;

    private SearchViewModel mViewModel;
    private FragmentSearchBinding mDataBinding;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SearchListFragment newInstance() {
        SearchListFragment fragment = new SearchListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }
        mViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getContext())).get(SearchViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);

        return mDataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initObserver();
    }

    private void initObserver() {
        mViewModel.getSearchQuery().observe(this, (query) -> {
            if (query != null && query.length() > 0) {
                ((TimelineAdapter) mDataBinding.searchArticleRv.getAdapter()).swapTimelineList(null);
                ((UserAdapter) mDataBinding.searchRv.getAdapter()).swapUserList(null);
                mViewModel.fetchSearch(query);
            }
        });
        mViewModel.getSearchUserList().observe(this, (list) -> {
            UserAdapter adapter = new UserAdapter(list, mListener);
            mDataBinding.searchRv.setAdapter(adapter);
        });
        mViewModel.getSearchList().observe(this, (list) -> {
            TimelineAdapter adapter = new TimelineAdapter(list, mArticleListener);
            mDataBinding.searchArticleRv.setAdapter(adapter);
        });
        mViewModel.isNetworkOnUse().observe(this, refresh -> {
            if(refresh==null) return;
            mDataBinding.swipeContainer.setRefreshing(refresh);
            if (!refresh) mDataBinding.swipeContainer.setEnabled(false);
        });
    }

    private void initView() {
        if (mDataBinding == null) return;

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mDataBinding.searchRv.setLayoutManager(manager);
        UserAdapter adapter = new UserAdapter(mViewModel.getSearchUserList().getValue(), mListener);
        mDataBinding.searchRv.setAdapter(adapter);

        LinearLayoutManager managerVertical = new LinearLayoutManager(getContext());
        managerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        managerVertical.setAutoMeasureEnabled(true);
        TimelineAdapter timelineAdapter = new TimelineAdapter(mViewModel.getSearchList().getValue(), mArticleListener);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                manager.getOrientation());
        mDataBinding.searchArticleRv.addItemDecoration(dividerItemDecoration);
        mDataBinding.searchArticleRv.setAdapter(timelineAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnArticleClickListener && context instanceof OnUserClickListener) {
            mListener = (OnUserClickListener) context;
            mArticleListener = (OnArticleClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSearchClickListener and OnUserClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
