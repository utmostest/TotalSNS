package com.enos.totalsns.accounts;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enos.totalsns.R;
import com.enos.totalsns.data.Account;
import com.enos.totalsns.data.Constants;
import com.enos.totalsns.databinding.FragmentAccountBinding;
import com.enos.totalsns.listener.OnSnsAccountListener;
import com.enos.totalsns.util.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnSnsAccountListener}
 * interface.
 */
public class AccountFragment extends Fragment {

    private static final String ARG_SNS_TYPE = "sns-type";

    private int mSnsType = Constants.DEFAULT_SNS;

    private OnSnsAccountListener mListener;

    private FragmentAccountBinding mDataBinding;

    private AccountsViewModel viewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AccountFragment() {
    }

    public static AccountFragment newInstance(int snsType) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SNS_TYPE, snsType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mSnsType = getArguments().getInt(ARG_SNS_TYPE);
        }
        viewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getActivity())).get(AccountsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false);

        Context context = mDataBinding.list.getContext();
        final LinearLayoutManager manager = new LinearLayoutManager(context);
        mDataBinding.list.setLayoutManager(manager);

        AccountsAdapter adapter = new AccountsAdapter(getContext(), mSnsType, mListener);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), manager.getOrientation());
        mDataBinding.list.addItemDecoration(dividerItemDecoration);
        mDataBinding.list.setAdapter(adapter);
        viewModel.getAccounts().observe(this, accounts -> adapter.swapAccountsList(getFilteredAccount(accounts)));
        return mDataBinding.getRoot();
    }

    private List<Account> getFilteredAccount(List<Account> accounts) {
        ArrayList<Account> list = new ArrayList<>();
        if (accounts != null) {
            for (Account account : accounts) {
                if (account.getSnsType() == mSnsType) list.add(account);
            }
        }
        return list;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSnsAccountListener) {
            mListener = (OnSnsAccountListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSnsAccountListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
