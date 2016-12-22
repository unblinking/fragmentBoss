package com.nothingworksright.fragmentbossy.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nothingworksright.fragmentbossy.R;
import com.nothingworksright.fragmentbossy.activities.MainActivity;

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
        TextView textView = (TextView) view.findViewById(R.id.textView);

        mainActivity = (MainActivity) getActivity();

        getFragmentArguments();

        String textMillis = Long.toString(currentMillis);
        textView.setText("Milliseconds since epoch at the time that this fragment was created: " + textMillis);

        return view;

    }

    @Override
    public void onClick(View view) {
        // Do nothing.
    }

    public static MainFragment newInstance(long currentMillis){
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

