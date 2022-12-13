package com.enos.totalsns.search;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.enos.totalsns.R;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
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
    private FragmentSearchBinding mBinding;
    private SearchAdapter mSearchAdapter;

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

        mViewModel = ViewModelProviders.of(this, (ViewModelProvider.Factory) ViewModelFactory.getInstance(getContext())).get(SearchViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initObserver();
    }

    private void initObserver() {
        mViewModel.getSearchQuery().observe(getViewLifecycleOwner(), (query) -> {
            if (query != null && query.length() > 0) {
                mSearchAdapter.swapUserList(null);
                mViewModel.fetchSearch(query);
            }
        });
        mViewModel.getSearchUserList().observe(getViewLifecycleOwner(), list -> {
            mSearchAdapter.swapUserList(list);
        });
        mViewModel.getSearchList().observe(getViewLifecycleOwner(), (list) -> {
            List<Article> old = mSearchAdapter.getArticleList();
            if (list != null) {
                if (mViewModel.isBetweenFetching()) {
                    if (list.size() < 20) {
                        old.get(old.indexOf(mViewModel.getCurrentBetween())).setSinceId(Constants.INVALID_ID);
                    }
                }
            }
            if (old != null && old.size() > 0) {
                Article oldLast = old.get(0);
                Article oldFirst = old.get(old.size() - 1);
                if (list != null && list.size() > 0) {
                    Article last = list.get(0);
                    Article first = list.get(list.size() - 1);
                    if (first.getArticleId() > oldLast.getArticleId()) {
                        list.addAll(list.size(), old);
                    } else if (last.getArticleId() < oldFirst.getArticleId()) {
                        list.addAll(0, old);
                    } else if (mViewModel.isBetweenFetching()) {

                        int index = old.indexOf(mViewModel.getCurrentBetween());
                        if (index > 0) {
                            old.get(index).setSinceId(Constants.INVALID_ID);
                            list.addAll(0, old.subList(0, index + 1));
                            list.addAll(list.size(), old.subList(index + 1, old.size()));
                        }
                    }
                } else if (list != null) {
                    list.addAll(old);
                }
            }
            if (mViewModel.isBetweenFetching()) {
                mViewModel.setBetweenFetching(false);
            }
            mSearchAdapter.swapTimelineList(list);
            mSearchAdapter.notifyDataSetChanged();
        });
        mViewModel.getUserCache().observe(getViewLifecycleOwner(), cache -> {
            ArrayList<UserInfo> current = (ArrayList<UserInfo>) mSearchAdapter.getUserList();
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
                mSearchAdapter.swapUserList(changedList);
            }
        });
        mViewModel.isNetworkOnUse().observe(getViewLifecycleOwner(), refresh -> {
            if (refresh == null) return;
            mBinding.swipeContainer.setRefreshing(refresh);
        });
        mViewModel.getFollowUser().observe(getViewLifecycleOwner(), user -> {
            SearchAdapter adapter = (SearchAdapter) mBinding.searchArticleRv.getAdapter();
            ArrayList<UserInfo> current = (ArrayList<UserInfo>) adapter.getUserList();
            if (current != null) adapter.swapUserList(replaceChangedUser(current, user));
        });
    }

    private List<UserInfo> replaceChangedUser(ArrayList<UserInfo> current, UserInfo user) {
        int index = current.indexOf(user);
        current.remove(index);
        current.add(index, user);
        return current;
    }

    private void initView() {
        if (mBinding == null) return;

        LinearLayoutManager managerVertical = new LinearLayoutManager(getContext());
        managerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        mSearchAdapter = new SearchAdapter(mViewModel.getSearchUserList().getValue(), mViewModel.getSearchList().getValue(),
                mListener, mArticleListener, mMoreUserListener, this, mViewModel);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                managerVertical.getOrientation());
        mSearchAdapter.setEnableHeader(true, true);
        mBinding.searchArticleRv.addItemDecoration(dividerItemDecoration);
        mBinding.searchArticleRv.setAdapter(mSearchAdapter);
        mBinding.swipeContainer.setOnRefreshListener(direction -> {
            switch (direction) {
                case TOP:
                    List<Article> articles = mSearchAdapter.getArticleList();
                    if (articles.size() > 0) {
                        mViewModel.fetchRecent(articles.get(0).getArticleId());
                    } else {
                        mViewModel.fetchRecent(-1);
                    }
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
