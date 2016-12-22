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
                // Add another fragment.

                String tagName = "tagName";
                int containerViewId = R.id.mainContainer;
                long currentMillis = System.currentTimeMillis();
                final String tagCombo = FragmentBoss.tagJoiner(tagName, containerViewId, currentMillis);

                MainFragment fragment = MainFragment.newInstance(currentMillis);

                Bundle bundle = new Bundle();
                bundle.putLong("currentMillis", currentMillis);
                fragment.setArguments(bundle);

                FragmentBoss.replaceFragmentInContainer(
                        containerViewId,
                        getSupportFragmentManager(),
                        fragment,
                        tagCombo
                );

                return true;

            case R.id.action_list:
                // List available fragments.

                List<String> list = new ArrayList<>();
                FragmentManager fm = getSupportFragmentManager();
                if (fm != null) {
                    int backStackEntryCount = fm.getBackStackEntryCount();
                    for (int entry = 0; entry < backStackEntryCount; entry++) {
                        String fragmentTag = fm.getBackStackEntryAt(entry).getName();
                        list.add(fragmentTag);
                    }
                }
                final CharSequence[] charSequenceItems = list.toArray(new CharSequence[list.size()]);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Open Fragments (name, containerViewId, and millis)");
                // builder.setMessage("message");
                builder.setItems(charSequenceItems, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Resurface the fragment.

                        String tagCombo = charSequenceItems[item].toString();

                        FragmentBoss.resurfaceFragmentInBackStack(
                                getSupportFragmentManager(),
                                tagCombo
                        );

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}

