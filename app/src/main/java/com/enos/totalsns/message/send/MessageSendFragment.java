package com.enos.totalsns.message.send;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.enos.totalsns.R;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.data.source.remote.QueryFollow;
import com.enos.totalsns.databinding.FragmentMessageSendBinding;
import com.enos.totalsns.listener.OnMessageSendListener;
import com.enos.totalsns.message.detail.MessageDetailActivity;
import com.enos.totalsns.util.ViewModelFactory;

public class MessageSendFragment extends Fragment implements OnMessageSendListener {

    private MessageSendViewModel mViewModel;
    private FragmentMessageSendBinding mBinding;

    public static MessageSendFragment newInstance() {
        return new MessageSendFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, (ViewModelProvider.Factory) ViewModelFactory.getInstance(getContext())).get(MessageSendViewModel.class);
        mViewModel.fetchFirstFollowList(new QueryFollow(QueryFollow.FIRST));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_message_send, container, false);
        return mBinding.getRoot();
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
        mBinding.msgSendRv.addItemDecoration(dividerItemDecoration);
        mBinding.msgSendRv.setAdapter(adapter);
        mBinding.swipeContainer.setOnRefreshListener(direction -> mBinding.swipeContainer.setRefreshing(false));
    }

    private void initObserver() {
        mViewModel.getSendToList().observe(getViewLifecycleOwner(), (list) -> {
            MessageSendAdapter adapter = (MessageSendAdapter) mBinding.msgSendRv.getAdapter();
            adapter.swapUserList(list);
        });
        mViewModel.isNetworkOnUse().observe(getViewLifecycleOwner(), refresh -> {
            if (refresh == null) return;
            mBinding.swipeContainer.setRefreshing(refresh);
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
