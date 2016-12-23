package com.nothingworksright.fragmentbossy.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.nothingworksright.fragmentboss.FragmentBoss;
import com.nothingworksright.fragmentbossy.R;
import com.nothingworksright.fragmentbossy.fragments.MainFragment;

import java.util.ArrayList;
import java.util.List;

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
            case R.id.action_add:
                return addAnotherFragment();
            case R.id.action_list:
                return listFragmentsToResurface();
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean addAnotherFragment() {

        // Add another fragment.

        String tagName = "tagName";
        int containerViewId = R.id.mainContainer;
        long uniqueId = System.currentTimeMillis();
        final String tagCombo = FragmentBoss.tagJoiner(tagName, containerViewId, uniqueId);

        MainFragment fragment = MainFragment.newInstance();

        Bundle bundle = new Bundle();
        bundle.putLong("uniqueId", uniqueId);
        bundle.putString("tagCombo", tagCombo);
        fragment.setArguments(bundle);

        FragmentBoss.replaceFragmentInContainer(
                containerViewId,
                getSupportFragmentManager(),
                fragment,
                tagCombo
        );

        return true;

    }

    public boolean listFragmentsToResurface() {

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

        // Just the names from the tagCombo values.
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
        // Just show the names for the user to click on.
        builder.setItems(charSeqTagNames, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Resurface the fragment.

                // Use the tagName item index position to get the corresponding tagCombo
                String tagCombo = charSeqTagCombos[item].toString();

                // Resurface the fragment (bring the fragment to the top)
                FragmentBoss.resurfaceFragmentInBackStack(
                        getSupportFragmentManager(),
                        tagCombo
                );

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

        return true;

    }

}

