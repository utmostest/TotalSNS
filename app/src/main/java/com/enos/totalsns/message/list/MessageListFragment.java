package com.enos.totalsns.message.list;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.enos.totalsns.R;
import com.enos.totalsns.databinding.FragmentMessageBinding;
import com.enos.totalsns.listener.OnMessageClickListener;
import com.enos.totalsns.util.ViewModelFactory;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

public class MessageListFragment extends Fragment {

    private OnMessageClickListener mListener;
    private MessageListViewModel mViewModel;
    private FragmentMessageBinding mBinding;

    public MessageListFragment() {
    }

    public static MessageListFragment newInstance() {
        MessageListFragment fragment = new MessageListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this, (ViewModelProvider.Factory) ViewModelFactory.getInstance(getContext())).get(MessageListViewModel.class);
        mViewModel.fetchDirectMessage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_message, container, false);
        //Log.i("list", "onCreateView");
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUI();
    }

    private void initUI() {
        if (mBinding == null) return;

        mBinding.swipeContainer.setOnRefreshListener(direction -> {
            if (direction == SwipyRefreshLayoutDirection.TOP) {
                mViewModel.fetchRecentDirectMessage();
            } else if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
                mViewModel.fetchPastDirectMessage();
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mBinding.msgRv.setLayoutManager(manager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                manager.getOrientation());
        mBinding.msgRv.addItemDecoration(dividerItemDecoration);
        MessageAdapter adapter = new MessageAdapter(null, mListener);
        mBinding.msgRv.setAdapter(adapter);

        mViewModel.getMessageList().observe(getViewLifecycleOwner(), articleList -> {
//                LinearLayoutManager lm = (LinearLayoutManager) mBinding.tlRv.getLayoutManager();
//                int currentPosFirst = lm.findFirstCompletelyVisibleItemPosition();

            adapter.swapMessageList(articleList);

//                if (currentPosFirst == 0)
//                    mBinding.tlRv.smoothScrollToPosition(0);
        });
        mViewModel.isNetworkOnUse().observe(getViewLifecycleOwner(), refresh -> {
            if (refresh == null) return;
            mBinding.swipeContainer.setRefreshing(refresh);
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
