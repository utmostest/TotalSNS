package com.enos.totalsns.message.detail;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.enos.totalsns.R;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.databinding.FragmentMessageDetailBinding;
import com.enos.totalsns.listener.OnMessageClickListener;
import com.enos.totalsns.util.ViewModelFactory;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

public class MessageDetailFragment extends Fragment implements View.OnClickListener {

    public static final String COLUMN_SENDER_MSG = "comlumn_sender_id";
    private MessageDetailViewModel mViewModel;
    private OnMessageClickListener mListener;
    private FragmentMessageDetailBinding mDataBinding;

    private UserInfo receiver = null;

    public static MessageDetailFragment newInstance(UserInfo receiver) {
        MessageDetailFragment fragment = new MessageDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(COLUMN_SENDER_MSG, receiver);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getContext())).get(MessageDetailViewModel.class);
        if (getArguments() != null) {
            receiver = getArguments().getParcelable(COLUMN_SENDER_MSG);
            if (receiver != null) {
                mViewModel.fetchDirectMessageDetail(receiver.getLongUserId());
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_message_detail, container, false);
        return mDataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUI();
    }

    private void initUI() {
        if (mDataBinding == null) return;

        mDataBinding.swipeContainer.setOnRefreshListener(direction -> {
            if (direction == SwipyRefreshLayoutDirection.TOP) {
                mViewModel.fetchPastDirectMessage();
            } else if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
                mViewModel.fetchRecentDirectMessage();
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setStackFromEnd(true);
        mDataBinding.msgRv.setLayoutManager(manager);
        MessageChatAdapter adapter = new MessageChatAdapter(null, mListener);
        mDataBinding.msgRv.setAdapter(adapter);
        mDataBinding.messageDetailImage.setOnClickListener(this);
        mDataBinding.messageDetailSend.setOnClickListener(this);

        mViewModel.getDirectMessageDetail().observe(this, articleList -> {
            if (articleList != null) {
                mDataBinding.msgRv.scrollToPosition(adapter.getItemCount() - 1);
            }
            adapter.swapMessageList(articleList);
        });
        mViewModel.isNetworkOnUse().observe(this, refresh -> {
            if (refresh == null) return;
            mDataBinding.swipeContainer.setRefreshing(refresh);
        });
        mViewModel.getCurrentUploadingDM().observe(this, (dm) -> {
            if (dm == null) return;
            mDataBinding.messageDetailEdit.setText("");
        });
    }

    private void postDirectMessage() {
        Editable editable = mDataBinding.messageDetailEdit.getEditableText();
        if (editable != null && editable.toString().length() > 0) {
            String message = editable.toString();
            mViewModel.postDirectMessage(receiver, message, null);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMessageClickListener) {
            mListener = (OnMessageClickListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnMessageClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.message_detail_image:
                Toast.makeText(getContext(), "TODO : add image selector", Toast.LENGTH_SHORT).show();
                break;
            case R.id.message_detail_send:
                postDirectMessage();
                break;
        }
    }
}
