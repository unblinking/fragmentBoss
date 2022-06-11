package com.unblinking.fragmentbossy.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.unblinking.fragmentbossy.R;

import static com.unblinking.fragmentboss.FragmentBoss.tagSplitter;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements View.OnClickListener {

    View view;
    String tagCombo;
    TextView textViewTagName;
    TextView textViewContainerViewId;
    TextView textViewDbRecordId;
    EditText editText;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);
        textViewTagName = (TextView) view.findViewById(R.id.textViewTagName);
        textViewContainerViewId = (TextView) view.findViewById(R.id.textViewContainerViewId);
        textViewDbRecordId = (TextView) view.findViewById(R.id.textViewDbRecordId);
        editText = (EditText) view.findViewById(R.id.editText);

        getFragmentArguments();

        String tagName = tagSplitter(tagCombo)[0];
        String containerViewId = tagSplitter(tagCombo)[1];
        String dbRecordId = tagSplitter(tagCombo)[2];

        textViewTagName.setText(getString(R.string.tag_name, tagName));
        textViewContainerViewId.setText(getString(R.string.container_view_id, containerViewId));
        textViewDbRecordId.setText(getString(R.string.db_record_id, dbRecordId));

        return view;

    }

    @Override
    public void onClick(View view) {
        // Do nothing.
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);
    }

    public static MainFragment newInstance(){
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    public void getFragmentArguments() {
        Bundle args = getArguments();
        tagCombo = args.getString("tagCombo");
    }

}

