package com.enos.totalsns.timeline.write;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.enos.totalsns.R;
import com.enos.totalsns.custom.GlideCustomAdapter;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.databinding.FragmentTimelineWriteBinding;
import com.enos.totalsns.util.GlideUtils;
import com.enos.totalsns.util.SingletonToast;
import com.enos.totalsns.util.ViewModelFactory;
import com.sangcomz.fishbun.FishBun;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class TimelineWriteFragment extends Fragment implements View.OnClickListener {

    private TimelineWriteViewModel mViewModel;
    FragmentTimelineWriteBinding mBinding;
    ActivityResultLauncher<Intent> lancher;

    public static TimelineWriteFragment newInstance() {
        return new TimelineWriteFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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

        mViewModel = ViewModelProviders.of(getActivity(), (ViewModelProvider.Factory) ViewModelFactory.getInstance(getActivity())).get(TimelineWriteViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_timeline_write, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUI();
        initObserver();
    }

    private void initObserver() {
        mViewModel.getCurrentUser().observe(getViewLifecycleOwner(), (user) -> {
            if (user == null) return;
            GlideUtils.loadProfileImage(getContext(), user.getProfileImg(), mBinding.tlWriteAccount, R.drawable.ic_account);
        });
    }

    private void initUI() {
        if (mBinding != null) {
            mBinding.tlWriteAccount.setOnClickListener(this);
            mBinding.tlWriteLocation.setOnClickListener(this);
            mBinding.tlWritePicture.setOnClickListener(this);
            mBinding.tlWritePost.setOnClickListener(this);
            mBinding.tlWriteClose.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tl_write_close:
                close();
                break;
            case R.id.tl_write_account:
                profileClicked();
                break;
            case R.id.tl_write_location:
                Toast.makeText(getContext(), "TODO : add location / place selector", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tl_write_picture:
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
                break;
            case R.id.tl_write_post:
                post();
                break;
        }
    }

    private void profileClicked() {
        Toast.makeText(getContext(), "TODO : add account selector", Toast.LENGTH_SHORT).show();
    }

    private void close() {
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        atomicBoolean.set(true);
        mViewModel.isShouldClose().setValue(atomicBoolean);
    }

    private void post() {

        Editable editable = mBinding.tlWriteEdit.getText();
        if (editable != null && editable.length() > 0) {
            Article article = new Article();
            article.setMessage(mBinding.tlWriteEdit.getText().toString());
            article.setSnsType(Constants.TWITTER);

            mViewModel.postArticle(article);
        }
    }
}
