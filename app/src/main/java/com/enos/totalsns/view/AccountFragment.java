package com.enos.totalsns.view;

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
import com.enos.totalsns.view.adapter.AccountAdapter;
import com.enos.totalsns.viewmodel.AccountListViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnSnsAccountListener}
 * interface.
 */
public class AccountFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_SNS_TYPE = "sns-type";
    // TODO: Customize parameters
    private int mSnsType = Constants.DEFAULT_SNS;
    private OnSnsAccountListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AccountFragment() {
    }

    // TODO: Customize parameter initialization
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
        View view = inflater.inflate(R.layout.fragment_account_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            final AccountListViewModel viewModel = ViewModelProviders.of(this).get(AccountListViewModel.class);
            recyclerView.setAdapter(getAdapter(viewModel.getAccounts().getValue()));
            viewModel.getAccounts().observe(this, accounts -> recyclerView.setAdapter(getAdapter(accounts)));
        }
        return view;
    }

    private AccountAdapter getAdapter(List<Account> accounts) {
        AccountAdapter adapter = new AccountAdapter(getContext(), mSnsType, mListener);
        adapter.setEnableFooter(true, true, v -> {
            if (mListener != null) mListener.onNewAccountButtonClicked(mSnsType);
        });
        adapter.setAccountsList(getFilteredAccount(accounts));

        return adapter;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnSnsAccountListener {
        // TODO: Update argument type and name
        void onAccountClicked(Account item);

        void onNewAccountButtonClicked(int snsType);
    }
}
