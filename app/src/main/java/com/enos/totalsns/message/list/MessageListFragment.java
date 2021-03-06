package com.enos.totalsns.message.list;

import android.content.Context;

import androidx.annotation.Nullable;
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
import com.enos.totalsns.databinding.FragmentMessageBinding;
import com.enos.totalsns.listener.OnMessageClickListener;
import com.enos.totalsns.util.ViewModelFactory;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

public class MessageListFragment extends Fragment {

    private OnMessageClickListener mListener;
    private MessageListViewModel mViewModel;
    private FragmentMessageBinding mDataBinding;

    public MessageListFragment() {
    }

    public static MessageListFragment newInstance() {
        MessageListFragment fragment = new MessageListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getContext())).get(MessageListViewModel.class);
        mViewModel.fetchDirectMessage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_message, container, false);
        //Log.i("list", "onCreateView");
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
                mViewModel.fetchRecentDirectMessage();
            } else if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
                mViewModel.fetchPastDirectMessage();
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mDataBinding.msgRv.setLayoutManager(manager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                manager.getOrientation());
        mDataBinding.msgRv.addItemDecoration(dividerItemDecoration);
        MessageAdapter adapter = new MessageAdapter(null, mListener);
        mDataBinding.msgRv.setAdapter(adapter);

        mViewModel.getMessageList().observe(this, articleList -> {
//                LinearLayoutManager lm = (LinearLayoutManager) mDataBinding.tlRv.getLayoutManager();
//                int currentPosFirst = lm.findFirstCompletelyVisibleItemPosition();

            adapter.swapMessageList(articleList);

//                if (currentPosFirst == 0)
//                    mDataBinding.tlRv.smoothScrollToPosition(0);
        });
        mViewModel.isNetworkOnUse().observe(this, refresh -> {
            if (refresh == null) return;
            mDataBinding.swipeContainer.setRefreshing(refresh);
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMessageClickListener) {
            mListener = (OnMessageClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMessageClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
