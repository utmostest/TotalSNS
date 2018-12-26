package com.enos.totalsns.accounts;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enos.totalsns.R;
import com.enos.totalsns.data.Account;
import com.enos.totalsns.data.Constants;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            final AccountsViewModel viewModel = ViewModelProviders.of(this).get(AccountsViewModel.class);
            AccountsAdapter adapter = new AccountsAdapter(getContext(), mSnsType, mListener);
            adapter.setEnableFooter(true, true, v -> {
                if (mListener != null) mListener.onNewAccountButtonClicked(mSnsType);
            });
            adapter.swapAccountsList(viewModel.getAccounts().getValue());
            recyclerView.setAdapter(adapter);
            viewModel.getAccounts().observe(this, accounts -> adapter.swapAccountsList(getFilteredAccount(accounts)));
        }
        return view;
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
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
