package com.nothingworksright.fragmentboss;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import java.util.ArrayList;

/**
 * FragmentBoss is a library module for Android.
 *
 * The methods in this library make use of the android.support.v4.app Android API classes, such as
 * the Fragment, FragmentManager, and FragmentTransaction.
 *
 * Some methods use a handler that runs on the UI thread.
 *
 * @author Joshua Gray
 * @version 1.0.0
 *
 * @see android.support.v4.app.Fragment
 * @see android.support.v4.app.FragmentManager
 * @see android.support.v4.app.FragmentTransaction
 */
public class FragmentBoss {

    /**
     * Called to replace a fragment in a container. Uses the fragment tag to identify unique
     * fragments.
     *
     * Uses a handler that's running on the UI thread.
     *
     * This is the primary method of the FragmentBoss class. This method could be used any time a
     * fragment needs to be placed into a container.
     *
     * If a fragment with a matching tag can be found in the fragment manager, it will be resurfaced
     * by using {@link #resurfaceFragmentInBackStack(FragmentManager, String)}. If no matching
     * fragment tag can be found in the fragment manager, the fragment will be added using the
     * fragmentTransaction.replace(int containerViewId, Fragment fragment, String tag) method.
     * The fragment tag is added to the back stack. Last, bringToFront is called on the fragment's
     * view, to be sure that the fragment at the top of the back stack is also visible.
     *
     * @param containerViewId int: Identifier of the container whose fragment(s) are to be replaced.
     * @param fm FragmentManager: The fragment manager interface being used to interact with the
     *           fragment objects inside of the activity.
     * @param fragment Fragment: The fragment to be placed into the activity.
     * @param tagCombo String: The tagCombo is a pipe delimited string of values. Always create the
     *                 tagCombo by using the {@link #tagJoiner(String, int, long)}  method. Always
     *                 split the tagCombo by using the {@link #tagSplitter(String)} method.
     */
    public static void replaceFragmentInContainer(final int containerViewId,
                                                  final FragmentManager fm, final Fragment fragment,
                                                  final String tagCombo) {
        // Get a handler that can be used to post to the main thread
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (fm != null && fragment != null && tagCombo != null) {
                    if (fm.findFragmentByTag(tagCombo) != null) {
                        // If a fragment with the same tag is already in the fragment manager,
                        // just resurface it.
                        resurfaceFragmentInBackStack(fm, tagCombo);
                    } else {
                        // If the fragment isn't in the fragment manager, add it, using replace.
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(
                                containerViewId,
                                fragment,
                                tagCombo
                        );
                        ft.addToBackStack(tagCombo);
                        ft.commit();
                    }
                    fm.executePendingTransactions();
                    // Bring the fragment's view to the front.
                    if (fragment.getView() != null) {
                        fragment.getView().bringToFront();
                    }
                }
            }
        };
        handler.post(runnable);
    }

    /**
     * Called by {@link #replaceFragmentInContainer(int, FragmentManager, Fragment, String)}
     * Do not call this method directly.
     *
     * Called to resurface a desired fragment which already exists somewhere in the back stack,
     * without changing or losing other currently existing fragments in the back stack.
     *
     * Uses a handler that's running on the UI thread.
     *
     * First, the current back stack is replicated in an ArrayList. Next, the back stack is emptied,
     * and all fragments are removed from the fragment manager. Next, the fragment manager and back
     * stack are refilled from the ArrayList in order, skipping the desired fragment. Last, the
     * desired fragment is added, leaving it at the surface.
     *
     * @param fm FragmentManager: The fragment manager interface being used to interact with the
     *           fragment objects inside of the activity.
     * @param desiredTagCombo String: The tagCombo is a pipe delimited string of values. Always
     *                        create the tagCombo by using the {@link #tagJoiner(String, int, long)}
     *                        method. Always split the tagCombo by using the
     *                        {@link #tagSplitter(String)} method.
     */
    public static void resurfaceFragmentInBackStack(final FragmentManager fm,
                                                    final String desiredTagCombo) {

        // Get a handler that can be used to post to the main thread
        //Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (fm != null) {

                    int backStackEntryCount = fm.getBackStackEntryCount();
                    if (backStackEntryCount > 0) {

                        // Populate our own ArrayList of the current back stack entries.
                        ArrayList<BackStackBoss> backStackArrayList = new ArrayList<>();
                        for (int entry = 0; entry < backStackEntryCount; entry++) {

                            // Get the tagCombo from this back stack entry in the fragment manager.
                            String tagCombo = fm.getBackStackEntryAt(entry).getName();

                            // Using BackStackBoss(), set the back stack entry values.
                            BackStackBoss bsb = new BackStackBoss();
                            bsb.setTagCombo(tagCombo);
                            bsb.setTagTitle(tagSplitter(tagCombo)[0]);
                            bsb.setContainerViewId(Integer.valueOf(tagSplitter(tagCombo)[1]));
                            bsb.setDbRecordId(Long.valueOf(tagSplitter(tagCombo)[2]));
                            bsb.setFragment(fm.findFragmentByTag(tagCombo));

                            // Add the backStackBoss to our array list.
                            backStackArrayList.add(bsb);

                        }

                        int backStackArrayListSize = backStackArrayList.size();

                        // Clear the fragment manager back stack completely
                        FragmentManager.BackStackEntry firstEntry = fm.getBackStackEntryAt(0);
                        fm.popBackStackImmediate(
                            firstEntry.getId(),
                            FragmentManager.POP_BACK_STACK_INCLUSIVE
                        );
                        fm.executePendingTransactions();

                        // Remove all fragments from the fragment manager
                        for (int entry = 0; entry < backStackArrayListSize; entry++) {
                            BackStackBoss bsb = backStackArrayList.get(entry);
                            Fragment entryFragment = bsb.getFragment();
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.remove(entryFragment);
                            ft.commit();
                        }
                        fm.executePendingTransactions();

                        // The fragment manager and back stack are refilled from the ArrayList in order,
                        // skipping the desired fragment.
                        for (int entry = 0; entry < backStackArrayListSize; entry++) {
                            BackStackBoss bsb = backStackArrayList.get(entry);
                            int containerViewId = bsb.getContainerViewId();
                            Fragment fragment = bsb.getFragment();
                            String tagCombo = bsb.getTagCombo();
                            if (!tagCombo.equals(desiredTagCombo)) {
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.add(containerViewId, fragment, tagCombo);
                                ft.addToBackStack(tagCombo);
                                ft.commit();
                            }
                        }
                        fm.executePendingTransactions();

                        // Last, the desired fragment is added to the fragment manager and back stack,
                        // leaving it on top.
                        for (int entry = 0; entry < backStackArrayListSize; entry++) {
                            BackStackBoss bsb = backStackArrayList.get(entry);
                            int containerViewId = bsb.getContainerViewId();
                            Fragment fragment = bsb.getFragment();
                            String tagCombo = bsb.getTagCombo();
                            if (tagCombo.equals(desiredTagCombo)) {
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.add(containerViewId, fragment, tagCombo);
                                ft.addToBackStack(tagCombo);
                                ft.commit();
                            }
                        }
                        fm.executePendingTransactions();

                    }

                }
            }
        };
        //handler.post(runnable);
        Thread thread = new Thread(runnable);
        thread.start();

    }

    /**
     * Called to bury a fragment at the bottom of the back stack.
     *
     * Uses a handler that's running on the UI thread.
     *
     * First, the current back stack is replicated in an ArrayList. Next, the back stack is emptied,
     * and all fragments are removed from the fragment manager. Next, the fragment manager and back
     * stack are refilled beginning with the desired fragment, and then the remaining fragments from
     * the ArrayList in order.
     *
     * @param fm FragmentManager: The fragment manager interface being used to interact with the
     *           fragment objects inside of the activity.
     * @param desiredTagCombo String: The tagCombo is a pipe delimited string of values. Always
     *                        create the tagCombo by using the {@link #tagJoiner(String, int, long)}
     *                        method. Always split the tagCombo by using the
     *                        {@link #tagSplitter(String)} method.
     */
    public static void buryFragmentInBackStack(final FragmentManager fm,
                                               final String desiredTagCombo) {
        // Get a handler that can be used to post to the main thread
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (fm != null) {

                    int backStackEntryCount = fm.getBackStackEntryCount();
                    if (backStackEntryCount > 0) {

                        // Populate our own ArrayList of the current back stack entries.
                        ArrayList<BackStackBoss> backStackArrayList = new ArrayList<>();
                        for (int entry = 0; entry < backStackEntryCount; entry++) {

                            // Get the tagCombo from this back stack entry in the fragment manager.
                            String tagCombo = fm.getBackStackEntryAt(entry).getName();

                            // Using BackStackBoss(), set the back stack entry values.
                            BackStackBoss bsb = new BackStackBoss();
                            bsb.setTagCombo(tagCombo);
                            bsb.setTagTitle(tagSplitter(tagCombo)[0]);
                            bsb.setContainerViewId(Integer.valueOf(tagSplitter(tagCombo)[1]));
                            bsb.setDbRecordId(Long.valueOf(tagSplitter(tagCombo)[2]));
                            bsb.setFragment(fm.findFragmentByTag(tagCombo));

                            // Add the backStackBoss to our array list.
                            backStackArrayList.add(bsb);

                        }

                        int backStackArrayListSize = backStackArrayList.size();

                        // Clear the fragment manager back stack completely
                        FragmentManager.BackStackEntry firstEntry = fm.getBackStackEntryAt(0);
                        fm.popBackStackImmediate(
                                firstEntry.getId(),
                                FragmentManager.POP_BACK_STACK_INCLUSIVE
                        );
                        fm.executePendingTransactions();

                        // Remove all fragments from the fragment manager
                        for (int entry = 0; entry < backStackArrayListSize; entry++) {
                            BackStackBoss bsb = backStackArrayList.get(entry);
                            Fragment entryFragment = bsb.getFragment();
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.remove(entryFragment);
                            ft.commit();
                        }
                        fm.executePendingTransactions();

                        // First, the desired fragment is added to the fragment manager and back stack,
                        // leaving it on the bottom.
                        for (int entry = 0; entry < backStackArrayListSize; entry++) {
                            BackStackBoss bsb = backStackArrayList.get(entry);
                            int containerViewId = bsb.getContainerViewId();
                            Fragment fragment = bsb.getFragment();
                            String tagCombo = bsb.getTagCombo();
                            if (tagCombo.equals(desiredTagCombo)) {
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.add(containerViewId, fragment, tagCombo);
                                ft.addToBackStack(tagCombo);
                                ft.commit();
                            }
                        }
                        fm.executePendingTransactions();

                        // Next, the fragment manager and back stack are refilled with the remaining
                        // fragments from the ArrayList in order.
                        for (int entry = 0; entry < backStackArrayListSize; entry++) {
                            BackStackBoss bsb = backStackArrayList.get(entry);
                            int containerViewId = bsb.getContainerViewId();
                            Fragment fragment = bsb.getFragment();
                            String tagCombo = bsb.getTagCombo();
                            if (!tagCombo.equals(desiredTagCombo)) {
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.add(containerViewId, fragment, tagCombo);
                                ft.addToBackStack(tagCombo);
                                ft.commit();
                            }
                        }
                        fm.executePendingTransactions();

                    }

                }
            }
        };
        handler.post(runnable);
    }

    /**
     * Called to pop the top fragment off of the fragment manager's back stack.
     *
     * Uses a handler that's running on the UI thread.
     *
     * @param fm FragmentManager: The fragment manager interface being used to interact with the
     *           fragment objects inside of the activity.
     */
    public static void popBackStack(final FragmentManager fm) {
        // Get a handler that can be used to post to the main thread
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Pop the top fragment off of the back stack.
                fm.popBackStackImmediate();
                fm.executePendingTransactions();
            }
        };
        handler.post(runnable);
    }

    /**
     * Called to join multiple fields into a pipe delimited String.
     *
     * @param tagTitle String: A traditional fragment tag, a unique string used to identify a unique
     *                 fragment. Used here as a fragment title.
     * @param containerViewId int: Identifier of the container for the fragment to be placed in.
     * @param dbRecordId long: A database record ID, such as a unique column or primary key value.
     *
     * @return The return value is a pipe delimited String. This is called the tagCombo.
     */
    public static String tagJoiner(String tagTitle, int containerViewId, long dbRecordId) {
        String result = new String();
        // At a minimum, the tagTitle must be supplied.
        if (tagTitle != null) {
            Joiner joiner = Joiner.on("|");
            result = joiner.join(
                    tagTitle,
                    String.valueOf(containerViewId),
                    String.valueOf(dbRecordId)
            );
        }
        return result;
    }

    /**
     * Called to split multiple fields from a pipe delimited String.
     *
     * @param tagCombo String: A pipe delimited String containing multiple values.
     *
     * @return The return value is a String array of the separated values from the tagCombo. These
     * values are: The String which is the fragment tag title; the string value of the int which
     * is the fragment's containerViewId.
     */
    public static String[] tagSplitter(String tagCombo) {
        String[] result = new String[0];
        if (tagCombo != null) {
            Splitter splitter = Splitter.on("|");
            Iterable<String> iterable = splitter.split(tagCombo);
            result = Iterables.toArray(iterable, String.class);
        }
        return result;
    }

    /**
     * Called to locate and return a fragment where the tagCombo contains matching tagTitle and
     * dbRecordId values.
     *
     * @param fm FragmentManager: The fragment manager interface being used to interact with the
     *           fragment objects inside of the activity.
     * @param desiredTagTitle String: The first of two pipe delimited values contained in the
     *                        tagCombo. The tagTitle is a traditional fragment tag, a unique string
     *                        used to identify a fragment.
     * @param desiredDbRecordId long: A database record ID, such as a unique column or primary key
     *                          value.
     *
     * @return The return value is a Fragment if a match is found, or null if no match is found.
     */
    public static Fragment findFragmentByTagTitleAndDbId(final FragmentManager fm,
                                                         String desiredTagTitle,
                                                         long desiredDbRecordId) {
        if (fm != null) {

            int backStackEntryCount = fm.getBackStackEntryCount();

            if (backStackEntryCount > 0) {

                for (int entry = 0; entry < backStackEntryCount; entry++) {

                    // Gather the details from the back stack entry in the fragment manager.
                    String tagCombo = fm.getBackStackEntryAt(entry).getName();
                    String tagTitle = tagSplitter(tagCombo)[0];
                    int contViewId = Integer.valueOf(tagSplitter(tagCombo)[1]);
                    long dbRecordId = Long.valueOf(tagSplitter(tagCombo)[2]);

                    // If the fragment tagTitle and DB record ID are a match, return the fragment.
                    if (tagTitle.equals(desiredTagTitle) && dbRecordId == desiredDbRecordId) {
                        return fm.findFragmentByTag(tagCombo);
                    }

                }

            }

        }
        // If no fragment tagTitle matched, return null.
        return null;
    }

    /**
     * Called to remove a fragment from the fragment manager and back stack. The fragment is located
     * by matching on the tagTitle and dbRecordId, fields in the BackStackBoss tagCombo.
     *
     * Uses a handler that's running on the UI thread.
     *
     * @param fm FragmentManager: The fragment manager interface being used to interact with the
     *           fragment objects inside of the activity.
     * @param undesiredTagTitle String: The first of two pipe delimited values contained in the
     *                          tagCombo. The tagTitle is a traditional fragment tag, a unique
     *                          string used to identify a fragment.
     * @param undesiredDbRecordId long: A database record ID, such as a unique column or primary key
     *                            value.
     */
    public static void removeFragmentByTagTitleAndDbId(final FragmentManager fm,
                                                       final String undesiredTagTitle,
                                                       final long undesiredDbRecordId) {

        // Get a handler that can be used to post to the main thread
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (fm != null) {

                    int backStackEntryCount = fm.getBackStackEntryCount();

                    if (backStackEntryCount > 0) {

                        // Populate our own ArrayList of the current back stack entries.
                        ArrayList<BackStackBoss> backStackArrayList = new ArrayList<>();
                        for (int entry = 0; entry < backStackEntryCount; entry++) {

                            // Get the tagCombo from this back stack entry in the fragment manager.
                            String tagCombo = fm.getBackStackEntryAt(entry).getName();

                            // Using BackStackBoss(), set the back stack entry values.
                            BackStackBoss bsb = new BackStackBoss();
                            bsb.setTagCombo(tagCombo);
                            bsb.setTagTitle(tagSplitter(tagCombo)[0]);
                            bsb.setContainerViewId(Integer.valueOf(tagSplitter(tagCombo)[1]));
                            bsb.setDbRecordId(Long.valueOf(tagSplitter(tagCombo)[2]));
                            bsb.setFragment(fm.findFragmentByTag(tagCombo));

                            // Add the backStackBoss to our array list.
                            backStackArrayList.add(bsb);

                        }

                        int backStackArrayListSize = backStackArrayList.size();

                        // Clear the fragment manager back stack completely
                        FragmentManager.BackStackEntry firstEntry = fm.getBackStackEntryAt(0);
                        fm.popBackStackImmediate(
                                firstEntry.getId(),
                                FragmentManager.POP_BACK_STACK_INCLUSIVE
                        );
                        fm.executePendingTransactions();

                        // Remove all fragments from the fragment manager
                        for (int entry = 0; entry < backStackArrayListSize; entry++) {
                            BackStackBoss bsb = backStackArrayList.get(entry);
                            Fragment entryFragment = bsb.getFragment();
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.remove(entryFragment);
                            ft.commit();
                        }
                        fm.executePendingTransactions();

                        // The fragment manager and back stack are refilled from the ArrayList in order,
                        // skipping the undesired fragment.
                        for (int entry = 0; entry < backStackArrayListSize; entry++) {

                            BackStackBoss bsb = backStackArrayList.get(entry);

                            Fragment fragment = bsb.getFragment();
                            String tagCombo = bsb.getTagCombo();
                            String tagTitle = bsb.getTagTitle();
                            int containerViewId = bsb.getContainerViewId();
                            long dbRecordId = bsb.getDbRecordId();

                            String entryCombo = tagTitle + String.valueOf(dbRecordId);
                            String undesiredCombo;
                            undesiredCombo = undesiredTagTitle + String.valueOf(undesiredDbRecordId);

                            if (!entryCombo.equals(undesiredCombo)) {
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.add(containerViewId, fragment, tagCombo);
                                ft.addToBackStack(tagCombo);
                                ft.commit();
                            }

                        }
                        fm.executePendingTransactions();

                    }

                }
            }
        };
        handler.post(runnable);

    }

    /**
     *
     * Called to call the onResume method in the fragment at the top of the back stack.
     *
     * Uses a handler that's running on the UI thread.
     *
     * @param fm FragmentManager: The fragment manager interface being used to interact with the
     *           fragment objects inside of the activity.
     */
    public static void topFragmentOnResume(final FragmentManager fm) {
        // Get a handler that can be used to post to the main thread
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (fm != null) {
                    int backStackEntryCount = fm.getBackStackEntryCount();
                    if (backStackEntryCount > 0) {
                        String tagCombo = fm.getBackStackEntryAt(backStackEntryCount - 1).getName();
                        Fragment fragment = fm.findFragmentByTag(tagCombo);
                        fragment.onResume();
                    }
                }
            }
        };
        handler.post(runnable);
    }

    /**
     * This class represents a layer from the back stack.
     *
     * Each layer includes a {@link #containerViewId}, {@link #fragment}, and {@link #tagCombo}.
     *
     * The {@link #tagCombo} is a pipe delimited string, containing a {@link #tagTitle} and
     * {@link #containerViewId} at minimum, so that the FragmentManager back stack may be
     * deconstructed and reconstructed later. It can also contain a {@link #dbRecordId} to associate
     * a database value with the fragment.
     */
    static class BackStackBoss {

        public int containerViewId;
        public Fragment fragment;
        public String tagCombo;
        public String tagTitle;
        public long dbRecordId;

        public int getContainerViewId() {
            return containerViewId;
        }
        public Fragment getFragment() {
            return fragment;
        }
        public String getTagCombo() {
            return tagCombo;
        }
        public String getTagTitle() {
            return tagTitle;
        }
        public long getDbRecordId() {
            return dbRecordId;
        }

        public void setContainerViewId(int containerViewId) {
            this.containerViewId = containerViewId;
        }
        public void setFragment(Fragment fragment) {
            this.fragment = fragment;
        }
        public void setTagCombo(String tagCombo) {
            this.tagCombo = tagCombo;
        }
        public void setTagTitle(String tagTitle) {
            this.tagTitle = tagTitle;
        }
        public void setDbRecordId(long dbRecordId) {
            this.dbRecordId = dbRecordId;
        }

    }

}

