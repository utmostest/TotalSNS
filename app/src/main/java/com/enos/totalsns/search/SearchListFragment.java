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
import com.enos.totalsns.util.ViewModelFactory;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnUserClickListener} {@link OnArticleClickListener}
 * interface.
 */
public class SearchListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_AUTO_SEARCH = "auto-search";
    // TODO: Customize parameters
    private boolean isAutoSearch = true;
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
    public static SearchListFragment newInstance(boolean isAutoSearch) {
        SearchListFragment fragment = new SearchListFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_AUTO_SEARCH, isAutoSearch);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            isAutoSearch = getArguments().getBoolean(ARG_AUTO_SEARCH, true);
        }
        mViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getContext())).get(SearchViewModel.class);
        if (isAutoSearch) mViewModel.fetchSearchForStart();
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
            mViewModel.fetchSearch(query);
        });
        mViewModel.getSearchList().observe(this, (list) -> {
            SearchAdapter searchAdapter = (SearchAdapter) mDataBinding.searchRv.getAdapter();
            searchAdapter.swapTimelineList(list);
        });
        mViewModel.isNetworkOnUse().observe(this, refresh -> mDataBinding.swipeContainer.setRefreshing(refresh));
    }

    private void initView() {
        if (mDataBinding == null) return;

        mDataBinding.swipeContainer.setOnRefreshListener(direction -> {
            if (direction == SwipyRefreshLayoutDirection.TOP) {
                mViewModel.fetchRecentSearch();
            } else if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
                mViewModel.fetchPastSearch();
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mDataBinding.searchRv.setLayoutManager(manager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                manager.getOrientation());
        mDataBinding.searchRv.addItemDecoration(dividerItemDecoration);
        SearchAdapter adapter = new SearchAdapter(null, mArticleListener, mListener);
        mDataBinding.searchRv.setAdapter(adapter);
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
