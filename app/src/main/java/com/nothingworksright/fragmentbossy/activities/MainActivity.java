package com.unblinking.fragmentbossy.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.unblinking.fragmentboss.FragmentBoss;
import com.unblinking.fragmentbossy.R;
import com.unblinking.fragmentbossy.fragments.MainFragment;

import java.util.ArrayList;
import java.util.List;

import static com.unblinking.fragmentboss.FragmentBoss.tagSplitter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_1:
                return showFragment(getString(R.string.action_show_1), R.id.mainContainer, 1);
            case R.id.action_show_2:
                return showFragment(getString(R.string.action_show_2), R.id.mainContainer, 2);
            case R.id.action_show_3:
                return showFragment(getString(R.string.action_show_3), R.id.mainContainer, 3);
            case R.id.action_list:
                return fragmentListDialog();
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean showFragment(String tagName, int containerViewId, long dbRecordId) {

        String tagCombo = FragmentBoss.tagJoiner(tagName, containerViewId, dbRecordId);

        // We prepare a new fragment for this dbRecordId in case it isn't in the back stack yet.
        MainFragment fragment = MainFragment.newInstance();

        Bundle bundle = new Bundle();
        bundle.putString("tagCombo", tagCombo);
        fragment.setArguments(bundle);

        /*
         * Using the fragmentBoss.replaceFragmentInContainer() method:
         * If the tagCombo is an exact match to a fragment that already exists in the fragment
         * manager's back stack, that fragment is resurfaced. If the tagCombo is not found in the
         * fragment manager, the fragment is added. The fragment's view is also brought to the
         * front.
         */
        FragmentBoss.replaceFragmentInContainer(
                containerViewId,
                getSupportFragmentManager(),
                fragment,
                tagCombo
        );

        Snackbar.make(
                findViewById(R.id.mainContainer),
                getString(R.string.showing_fragment, tagName),
                Snackbar.LENGTH_LONG
        ).show();

        return true;

    }

    public boolean fragmentListDialog() {

        // Prepare a list of fragment tagCombo Strings.
        List<String> fragmentTagcomboList = new ArrayList<>();
        FragmentManager fm = getSupportFragmentManager();
        if (fm != null) {
            int backStackEntryCount = fm.getBackStackEntryCount();
            for (int entry = 0; entry < backStackEntryCount; entry++) {
                String tagCombo = fm.getBackStackEntryAt(entry).getName();
                fragmentTagcomboList.add(tagCombo);
            }
        }
        final CharSequence[] charSeqTagCombos;
        charSeqTagCombos = fragmentTagcomboList.toArray(
                new CharSequence[fragmentTagcomboList.size()]
        );

        // Prepare a list of only the names from the tagCombo values.
        List<String> fragmentTagnameList = new ArrayList<>();
        for (String tagCombo : fragmentTagcomboList) {
            String tagName = FragmentBoss.tagSplitter(tagCombo)[0];
            fragmentTagnameList.add(tagName);
        }
        final CharSequence[] charSeqTagNames;
        charSeqTagNames = fragmentTagnameList.toArray(
                new CharSequence[fragmentTagnameList.size()]
        );

        // Use the list as items in an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Resurface a fragment");
        // Just show the tagName values for the user to click on.
        builder.setItems(charSeqTagNames, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Use the tagName item index position to get the corresponding tagCombo.
                String tagCombo = charSeqTagCombos[item].toString();
                // Split fragment information out of the tagCombo.
                String tagName = tagSplitter(tagCombo)[0];
                int containerViewId = Integer.valueOf(tagSplitter(tagCombo)[1]);
                long dbRecordId = Long.valueOf(tagSplitter(tagCombo)[2]);
                // Show the fragment
                showFragment(tagName, containerViewId, dbRecordId);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

        return true;

    }

}

