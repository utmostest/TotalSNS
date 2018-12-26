package com.enos.totalsns.timelinewrite;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enos.totalsns.R;
import com.enos.totalsns.util.ViewModelFactory;

public class TimelineWriteFragment extends Fragment {

    private TimelineWriteViewModel mViewModel;

    public static TimelineWriteFragment newInstance() {
        return new TimelineWriteFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timeline_write, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getActivity())).get(TimelineWriteViewModel.class);
        // TODO: Use the ViewModel
    }

}
