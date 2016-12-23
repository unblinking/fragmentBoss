package com.nothingworksright.fragmentbossy.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.nothingworksright.fragmentboss.FragmentBoss;
import com.nothingworksright.fragmentbossy.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements View.OnClickListener {

    View view;
    long uniqueId;
    String uniqueIdString;
    TextView textViewTitle;
    TextView textViewMillis;
    EditText editTextName;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);
        textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
        textViewMillis = (TextView) view.findViewById(R.id.textViewMillis);
        editTextName = (EditText) view.findViewById(R.id.editTextName);

        getFragmentArguments();

        String millisFullText = getString(R.string.unique_id) + Long.toString(uniqueId);
        textViewMillis.setText(millisFullText);

        editTextName.setHint(getString(R.string.type_a_new_name));
        editTextName.addTextChangedListener(textWatcher);

        return view;

    }

    @Override
    public void onClick(View view) {
        // Do nothing.
    }

    public static MainFragment newInstance(long uniqueId){
        MainFragment fragment = new MainFragment();
        /*Bundle bundle = new Bundle();
        bundle.putLong("uniqueId", uniqueId);
        fragment.setArguments(bundle);*/
        return fragment;
    }

    public void getFragmentArguments() {
        Bundle args = getArguments();
        if (args != null && args.containsKey("uniqueId")){
            uniqueId = args.getLong("uniqueId", 0);
            uniqueIdString = Long.toString(uniqueId);
        }
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void afterTextChanged(Editable editable) {
            String newTagName = editTextName.getText().toString();
            textViewTitle.setText(newTagName);
            FragmentBoss.tag
        }
    };

}

