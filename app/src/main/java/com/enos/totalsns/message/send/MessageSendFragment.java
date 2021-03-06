package com.enos.totalsns.message.send;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enos.totalsns.R;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.data.source.remote.QueryFollow;
import com.enos.totalsns.databinding.FragmentMessageSendBinding;
import com.enos.totalsns.listener.OnMessageSendListener;
import com.enos.totalsns.message.detail.MessageDetailActivity;
import com.enos.totalsns.util.ViewModelFactory;

public class MessageSendFragment extends Fragment implements OnMessageSendListener {

    private MessageSendViewModel mViewModel;
    private FragmentMessageSendBinding dataBinding;

    public static MessageSendFragment newInstance() {
        return new MessageSendFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getContext())).get(MessageSendViewModel.class);
        mViewModel.fetchFirstFollowList(new QueryFollow(QueryFollow.FIRST));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_message_send, container, false);
        return dataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initObserver();
    }

    private void initView() {
        LinearLayoutManager managerVertical = new LinearLayoutManager(getContext());
        managerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        MessageSendAdapter adapter = new MessageSendAdapter(null, this);
        adapter.setEnableHeader(true, true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                managerVertical.getOrientation());
        dataBinding.msgSendRv.addItemDecoration(dividerItemDecoration);
        dataBinding.msgSendRv.setAdapter(adapter);
        dataBinding.swipeContainer.setOnRefreshListener(direction -> dataBinding.swipeContainer.setRefreshing(false));
    }

    private void initObserver() {
        mViewModel.getSendToList().observe(this, (list) -> {
            MessageSendAdapter adapter = (MessageSendAdapter) dataBinding.msgSendRv.getAdapter();
            adapter.swapUserList(list);
        });
        mViewModel.isNetworkOnUse().observe(this, refresh -> {
            if (refresh == null) return;
            dataBinding.swipeContainer.setRefreshing(refresh);
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onUserToSendClicked(UserInfo userInfo) {
        Activity context = getActivity();
        if (context != null) {
            MessageDetailActivity.start((AppCompatActivity) context, userInfo);
            context.finish();
        }
    }

    @Override
    public void onUserToSendSearchClicked(String query) {
        mViewModel.fetchUserSearchList(query);
    }
}
