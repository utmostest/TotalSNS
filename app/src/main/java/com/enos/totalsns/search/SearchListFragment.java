package com.enos.totalsns.search;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.LongSparseArray;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enos.totalsns.R;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.databinding.FragmentSearchBinding;
import com.enos.totalsns.listener.OnArticleClickListener;
import com.enos.totalsns.listener.OnFollowBtnClickListener;
import com.enos.totalsns.listener.OnMoreUserButtonClickListener;
import com.enos.totalsns.listener.OnSearchUserClickListener;
import com.enos.totalsns.util.CompareUtils;
import com.enos.totalsns.util.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class SearchListFragment extends Fragment implements OnFollowBtnClickListener {

    private OnSearchUserClickListener mListener;
    private OnArticleClickListener mArticleListener;
    private OnMoreUserButtonClickListener mMoreUserListener;

    private SearchViewModel mViewModel;
    private FragmentSearchBinding mDataBinding;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchListFragment() {
    }

    public static SearchListFragment newInstance() {
        SearchListFragment fragment = new SearchListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                ((SearchAdapter) mDataBinding.searchArticleRv.getAdapter()).swapUserList(null);
                mViewModel.fetchSearch(query);
            }
        });
        mViewModel.getSearchUserList().observe(this, list -> {
            SearchAdapter adapter = (SearchAdapter) mDataBinding.searchArticleRv.getAdapter();
            adapter.swapUserList(list);
        });
        mViewModel.getSearchList().observe(this, (list) -> {
            SearchAdapter adapter = (SearchAdapter) mDataBinding.searchArticleRv.getAdapter();
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
        mViewModel.getUserCache().observe(this, cache -> {
            SearchAdapter adapter = (SearchAdapter) mDataBinding.searchArticleRv.getAdapter();
            ArrayList<UserInfo> current = (ArrayList<UserInfo>) adapter.getUserList();
            if (current == null || cache == null) return;
            Log.i("userCache", cache.size() + " search");
            LongSparseArray<UserInfo> changed = new LongSparseArray<>();
            for (UserInfo user : current) {
                if (!CompareUtils.isUserInfoEqual(user, cache.get(user.getLongUserId()))) {
                    UserInfo changedUser = cache.get(user.getLongUserId());
                    assert changedUser != null;
                    changed.put(changedUser.getLongUserId(), changedUser);
                }
            }
            if (changed.size() > 0) {
                ArrayList<UserInfo> changedList = new ArrayList<>(current);
                for (int i = 0; i < changed.size(); i++) {
                    UserInfo userInfo = changed.get(changed.keyAt(i));
                    int index = changedList.indexOf(userInfo);
                    changedList.remove(index);
                    changedList.add(index, userInfo);
                }
                adapter.swapUserList(changedList);
            }
        });
        mViewModel.isNetworkOnUse().observe(this, refresh -> {
            if (refresh == null) return;
            mDataBinding.swipeContainer.setRefreshing(refresh);
        });
        mViewModel.getFollowUser().observe(this, user -> {
            SearchAdapter adapter = (SearchAdapter) mDataBinding.searchArticleRv.getAdapter();
            ArrayList<UserInfo> current = (ArrayList<UserInfo>) adapter.getUserList();
            if (current != null) adapter.swapUserList(replaceChangedUser(current, user));
        });
    }

    private List<UserInfo> replaceChangedUser(ArrayList<UserInfo> current, UserInfo user) {
        ArrayList<UserInfo> users = new ArrayList<>(current);
        int index = current.indexOf(user);
        users.remove(index);
        users.add(index, user);
        return users;
    }

    private void initView() {
        if (mDataBinding == null) return;

        LinearLayoutManager managerVertical = new LinearLayoutManager(getContext());
        managerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        SearchAdapter searchAdapter = new SearchAdapter(mViewModel.getSearchUserList().getValue(), mViewModel.getSearchList().getValue(),
                mListener, mArticleListener, mMoreUserListener, this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                managerVertical.getOrientation());
        searchAdapter.setEnableHeader(true, true);
        mDataBinding.searchArticleRv.addItemDecoration(dividerItemDecoration);
        mDataBinding.searchArticleRv.setAdapter(searchAdapter);
        mDataBinding.swipeContainer.setOnRefreshListener(direction -> {
            switch (direction) {
                case TOP:
                    mViewModel.fetchRecent();
                    break;
                case BOTTOM:
                    mViewModel.fetchPast();
                    break;
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnArticleClickListener && context instanceof OnSearchUserClickListener &&
                context instanceof OnMoreUserButtonClickListener) {
            mListener = (OnSearchUserClickListener) context;
            mArticleListener = (OnArticleClickListener) context;
            mMoreUserListener = (OnMoreUserButtonClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSearchClickListener and OnUserClickListener and OnMoreUserButtonClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onFollowButtonClicked(UserInfo info) {
        if (!info.getFollowInfo().isMe()) {
            mViewModel.fetchFollow(info.getLongUserId(), !info.getFollowInfo().isFollowing());
        }
    }
}
