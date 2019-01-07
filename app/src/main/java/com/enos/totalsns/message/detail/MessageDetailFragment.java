package com.enos.totalsns.message.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enos.totalsns.R;
import com.enos.totalsns.data.Message;
import com.enos.totalsns.databinding.FragmentMessageDetailBinding;
import com.enos.totalsns.message.OnMessageClickListener;
import com.enos.totalsns.util.ViewModelFactory;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

public class MessageDetailFragment extends Fragment implements View.OnClickListener {

    public static final String COLUMN_SENDER_ID = "comlumn_sender_id";
    private MessageDetailViewModel mViewModel;
    private OnMessageClickListener mListener;
    private FragmentMessageDetailBinding mDataBinding;

    private long currentReceiverId = INVALID_SENDER_ID;
    private Message sampleMessage = null;

    public static final long INVALID_SENDER_ID = -1;

    public static MessageDetailFragment newInstance(long senderId) {
        MessageDetailFragment fragment = new MessageDetailFragment();
        Bundle args = new Bundle();
        args.putLong(COLUMN_SENDER_ID, senderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getContext())).get(MessageDetailViewModel.class);
        if (getArguments() != null) {
            if (getArguments().getLong(COLUMN_SENDER_ID, INVALID_SENDER_ID) != INVALID_SENDER_ID) {
                currentReceiverId = getArguments().getLong(COLUMN_SENDER_ID);
                mViewModel.fetchDirectMessageDetail(currentReceiverId);
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
                if (articleList.size() > 0) sampleMessage = articleList.get(0);
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
            mViewModel.postDirectMessage(currentReceiverId, message, null, sampleMessage);
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
                break;
            case R.id.message_detail_send:
                postDirectMessage();
                break;
        }
    }
}
