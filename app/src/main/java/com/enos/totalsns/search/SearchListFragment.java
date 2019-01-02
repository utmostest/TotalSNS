package com.enos.totalsns.search;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enos.totalsns.R;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnSearchClickListener}
 * interface.
 */
public class SearchListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_AUTO_SEARCH = "auto-search";
    // TODO: Customize parameters
    private boolean isAutoSearch = true;
    private OnSearchClickListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SearchListFragment newInstance(boolean isAutoSearch) {
        SearchListFragment fragment = new SearchListFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_AUTO_SEARCH, isAutoSearch);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            isAutoSearch = getArguments().getBoolean(ARG_AUTO_SEARCH, true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new SearchAdapter(null, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSearchClickListener) {
            mListener = (OnSearchClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSearchClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
