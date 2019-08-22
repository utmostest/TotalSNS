package com.enos.totalsns.userlist;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enos.totalsns.R;
import com.enos.totalsns.custom.ArraySetList;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.data.source.remote.QueryFollow;
import com.enos.totalsns.data.source.remote.QuerySearchUser;
import com.enos.totalsns.databinding.FragmentFollowListBinding;
import com.enos.totalsns.listener.OnFollowBtnClickListener;
import com.enos.totalsns.listener.OnUserClickListener;
import com.enos.totalsns.util.CompareUtils;
import com.enos.totalsns.util.ViewModelFactory;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.ArrayList;
import java.util.List;

public class UserListFragment extends Fragment implements OnFollowBtnClickListener {

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
        initCommonView();
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
        mViewModel.getUserFollowList().observe(this, list -> {
            UserAdapter adapter = (UserAdapter) dataBinding.msgRv.getAdapter();
            if (list == null || adapter == null) return;
            ArrayList<UserInfo> current = (ArrayList<UserInfo>) adapter.getUserList();
            ArraySetList<UserInfo> temp = new ArraySetList<>();
            if (current != null) temp.addAll(current);
            temp.addAll(list);
            adapter.swapUserList(temp);
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
        mViewModel.getSearchedUserList().observe(this, list -> {
            UserAdapter adapter = (UserAdapter) dataBinding.msgRv.getAdapter();
            if (list == null || adapter == null) return;
            ArrayList<UserInfo> current = (ArrayList<UserInfo>) adapter.getUserList();
            ArraySetList<UserInfo> temp = new ArraySetList<>();
            if (current != null) temp.addAll(current);
            temp.addAll(list);
            adapter.swapUserList(temp);
        });
    }

    private void initCommonView() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        dataBinding.msgRv.setLayoutManager(manager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                manager.getOrientation());
        dataBinding.msgRv.addItemDecoration(dividerItemDecoration);
        UserAdapter adapter = new UserAdapter(null, mListener, this);
        dataBinding.msgRv.setAdapter(adapter);

        mViewModel.isNetworkOnUse().observe(this, onUse -> {
            dataBinding.swipeContainer.setRefreshing(onUse);
        });
        mViewModel.getFollowUser().observe(this, user -> {
            ArrayList<UserInfo> current = (ArrayList<UserInfo>) adapter.getUserList();
            if (current != null) adapter.swapUserList(replaceChangedUser(current, user));
        });
        mViewModel.getUserCache().observe(this, cache -> {
            ArrayList<UserInfo> current = (ArrayList<UserInfo>) adapter.getUserList();
            LongSparseArray<UserInfo> changed = new LongSparseArray<>();
            if (current == null || cache == null) return;
            Log.i("userCache", cache.size() + " user");
            for (UserInfo user : current) {
                if (!CompareUtils.isUserInfoEqual(user, cache.get(user.getLongUserId()))) {
                    UserInfo changedUser = cache.get(user.getLongUserId());
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
    }

    private List<UserInfo> replaceChangedUser(ArrayList<UserInfo> current, UserInfo user) {
        ArrayList<UserInfo> users = new ArrayList<>(current);
        int index = current.indexOf(user);
        users.remove(index);
        users.add(index, user);
        return users;
    }

    @Override
    public void onFollowButtonClicked(UserInfo info) {
        if (!info.getFollowInfo().isMe()) {
            mViewModel.fetchFollow(info.getLongUserId(), !info.getFollowInfo().isFollowing());
        }
    }
}
