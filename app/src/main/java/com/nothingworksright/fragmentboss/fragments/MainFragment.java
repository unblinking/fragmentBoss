package com.nothingworksright.fragmentboss.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nothingworksright.fragmentboss.R;
import com.nothingworksright.fragmentboss.activities.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements View.OnClickListener {

    View view;
    MainActivity mainActivity;
    long currentMillis;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);

        mainActivity = (MainActivity) getActivity();

        getFragmentArguments();

        return view;

    }

    @Override
    public void onClick(View view) {
        // Do nothing.
    }

    public static MainFragment newInstance(int currentMillis){
        MainFragment fragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("currentMillis", currentMillis);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void getFragmentArguments() {
        Bundle args = getArguments();
        if (args != null && args.containsKey("currentMillis")){
            currentMillis = args.getLong("currentMillis", 0);
        }
    }

}

