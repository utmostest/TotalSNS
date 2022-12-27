package com.enos.totalsns.message.detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.enos.totalsns.R;
import com.enos.totalsns.custom.GlideCustomAdapter;
import com.enos.totalsns.data.UserInfo;
import com.enos.totalsns.databinding.FragmentMessageDetailBinding;
import com.enos.totalsns.listener.OnMessageClickListener;
import com.enos.totalsns.util.SingletonToast;
import com.enos.totalsns.util.ViewModelFactory;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.sangcomz.fishbun.FishBun;

import java.util.ArrayList;

public class MessageDetailFragment extends Fragment implements View.OnClickListener {

    public static final String COLUMN_SENDER_MSG = "comlumn_sender_id";
    private MessageDetailViewModel mViewModel;
    private OnMessageClickListener mListener;
    private FragmentMessageDetailBinding mBinding;

    private UserInfo receiver = null;

    ActivityResultLauncher<Intent> lancher;

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

        lancher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                ArrayList<Uri> uriList = new ArrayList<>();
                uriList = result.getData().getParcelableArrayListExtra(FishBun.INTENT_PATH);
//                if (result.getData() == null) {   // 어떤 이미지도 선택하지 않은 경우
//
//                } else {   // 이미지를 하나라도 선택한 경우
//                    if (result.getData().getClipData() == null) {     // 이미지를 하나만 선택한 경우
//                        Uri imageUri = result.getData().getData();
//                        uriList.add(imageUri);
//
//                    } else {      // 이미지를 여러장 선택한 경우
//                        ClipData clipData = result.getData().getClipData();
//
//                        for (int i = 0; i < clipData.getItemCount(); i++) {
//                            Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
//                            uriList.add(imageUri);  //uri를 list에 담는다.
//                        }
//                    }
//                }

                SingletonToast.getInstance().show("선택한 이미지는 " + uriList.size() + "개");
            }
        });

        mViewModel = ViewModelProviders.of(this, (ViewModelProvider.Factory) ViewModelFactory.getInstance(getContext())).get(MessageDetailViewModel.class);
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_message_detail, container, false);
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
                mViewModel.fetchPastDirectMessage();
            } else if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
                mViewModel.fetchRecentDirectMessage();
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setStackFromEnd(true);
        mBinding.msgRv.setLayoutManager(manager);
        MessageChatAdapter adapter = new MessageChatAdapter(null, mListener);
        mBinding.msgRv.setAdapter(adapter);
        mBinding.messageDetailImage.setOnClickListener(this);
        mBinding.messageDetailSend.setOnClickListener(this);

        mViewModel.getDirectMessageDetail().observe(getViewLifecycleOwner(), articleList -> {
            if (articleList != null) {
                mBinding.msgRv.scrollToPosition(adapter.getItemCount() - 1);
            }
            adapter.swapMessageList(articleList);
        });
        mViewModel.isNetworkOnUse().observe(getViewLifecycleOwner(), refresh -> {
            if (refresh == null) return;
            mBinding.swipeContainer.setRefreshing(refresh);
        });
        mViewModel.getCurrentUploadingDM().observe(getViewLifecycleOwner(), (dm) -> {
            if (dm == null) return;
            mBinding.messageDetailEdit.setText("");
        });
    }

    private void postDirectMessage() {
        Editable editable = mBinding.messageDetailEdit.getEditableText();
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
                FishBun.with(getActivity())
                        .setImageAdapter(new GlideCustomAdapter())
                        .setMaxCount(4)
                        .setActionBarColor(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark), false)
//                        .setActionBarTitleColor(getResources().getColor(R.color.search_back))
                        .startAlbumWithActivityResultCallback(lancher);
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.CONTENT_TYPE);
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);  // 다중 이미지를 가져올 수 있도록 세팅
//                lancher.launch(intent);
//                Toast.makeText(getContext(), "TODO : add image selector", Toast.LENGTH_SHORT).show();
                break;
            case R.id.message_detail_send:
                postDirectMessage();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }
}
