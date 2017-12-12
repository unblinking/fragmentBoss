# fragmentBoss  

[![Release](https://jitpack.io/v/com.nothingworksright/fragmentBoss.svg)](https://jitpack.io/#com.nothingworksright/fragmentBoss) [![GitHub release](https://img.shields.io/github/release/nothingworksright/fragmentBoss.svg)](https://github.com/nothingworksright/fragmentBoss/releases)  [![GitHub tag](https://img.shields.io/github/tag/nothingworksright/fragmentBoss.svg)](https://github.com/nothingworksright/fragmentBoss/tags)  [![GitHub commits](https://img.shields.io/github/commits-since/nothingworksright/fragmentBoss/v1.0.1.svg)](https://github.com/nothingworksright/fragmentBoss/commits/master)  

<a href="https://codeclimate.com/github/nothingworksright/fragmentBoss"><img src="https://codeclimate.com/github/nothingworksright/fragmentBoss/badges/gpa.svg" /></a>  <a href="https://codeclimate.com/github/nothingworksright/fragmentBoss/coverage"><img src="https://codeclimate.com/github/nothingworksright/fragmentBoss/badges/coverage.svg" /></a>  <a href="https://codeclimate.com/github/nothingworksright/fragmentBoss"><img src="https://codeclimate.com/github/nothingworksright/fragmentBoss/badges/issue_count.svg" /></a>  

# Bossing your Fragments around with the FragmentBoss Library

## Usage

FragmentBoss is a library module for Android projects.

### Manage your fragments with the FragmentBoss
Replacing fragments in containers is simple when you use FragmentBoss. Unique fragments are identified by a pipe delimited `String tagCombo`, which includes the fragment's `int containerViewId`, `String tagTitle`, and may optionally include a `long dbRecordId` to associate a database record with a fragment.  If the `tagCombo` is an exact match to a fragment that already exists in the fragment manager's back stack, that fragment is resurfaced. If the `tagCombo` is not found in the fragment manager, the fragment is added. The fragment's view is also brought to the front. Other methods include burying a fragment at the bottom of the back stack, popping the back stack, creating and splitting a `tagCombo`, locating existing fragments in the back stack by their `tagTitle` and `dbRecordId`, removing fragments from the back stack by their `tagTitle` and `dbRecordId`, and calling the `onResume` method of the fragment at the top of the back stack.

### Setting up FragmentBoss
Adding FragmentBoss to your project is simple. Using JitPack and this GitHub repository, add to your `app/build.gradle` file:

```java
    allprojects {
        repositories {
            maven { url "https://jitpack.io" }
        }
    }

    dependencies {
        compile 'com.nothingworksright:fragmentBoss:1.00'
    }
```

### Adding and/or replacing a fragment in a container
Once FragmentBoss has been added to your project, a fragment can be added and/or replaced in a container like this:

```java
    int containerViewId = R.id.mainContainer;
    String tagTitle = getString(R.string.app_name);
    int dbRecordId = -1;
    FragmentManager fm = getSupportFragmentManager();
    Fragment fragment = MainFragment.newInstance();
    String tagCombo = FragmentBoss.tagJoiner(tagTitle, containerViewId, dbRecordId);
    FragmentBoss.replaceFragmentInContainer(
        containerViewId,
        fm,
        fragment,
        tagCombo
    );
```

### Sending a fragment to the bottom of the back stack
A fragment can be moved from its current location to the bottom of the back stack like this:

```java
    FragmentManager fm = getSupportFragmentManager();
    int containerViewId = R.id.mainContainer;
    String tagTitle = getString(R.string.app_name);
    int dbRecordId = -1;
    String tagCombo = FragmentBoss.tagJoiner(tagTitle, containerViewId, dbRecordId);
    FragmentBoss.buryFragmentInBackStack(fm, tagCombo);
```

### Popping the fragment manager back stack
The fragment on the top of the back stack can be removed like this:

```java
    FragmentManager fm = getSupportFragmentManager();
    FragmentBoss.popBackStack(fm);
```

### Joining fragment information into a `tagCombo`
A `tagCombo` is a fragment tag that contains a combination of information. A `tagCombo` can be created like this:

```java
    int containerViewId = R.id.mainContainer;
    String tagTitle = getString(R.string.app_name);
    int dbRecordId = -1;
    String tagCombo = FragmentBoss.tagJoiner(tagTitle, containerViewId, dbRecordId);
```

### Splitting fragment information out of a `tagCombo`
A `tagCombo` is a fragment tag that contains a combination of information. A `tagCombo` can be split into pieces like this:

```java
    String tagCombo = fm.getBackStackEntryAt(entry).getName();
    String tagTitle = tagSplitter(tagCombo)[0];
    int contViewId = Integer.valueOf(tagSplitter(tagCombo)[1]);
    long dbRecordId = Long.valueOf(tagSplitter(tagCombo)[2]);
```

### Locating a fragment by its `tagTitle` and `dbRecordId`
A fragment may be located in the fragment manager by using its `tagTitle` and `dbRecordId` like this:

```java
    dbRecordId = -1;
    Fragment fragment = FragmentBoss.findFragmentByTagTitleAndDbId(
        getFragmentManager(),
        getString(R.string.app_name),
        dbRecordId
    );
```

### Removing a fragment by its `tagTitle` and `dbRecordId`
A fragment may be removed from the fragment manager back stack by using its `tagTitle` and `dbRecordId` like this:

```java
    dbRecordId = -1;
    Fragment fragment = FragmentBoss.removeFragmentByTagTitleAndDbId(
        getFragmentManager(),
        getString(R.string.app_name),
        dbRecordId
    );
```

### Calling the `onResume` method in the fragment at the top of the back stack
Sometimes after making changes, it can be helpful to manually call the `onResume` method of the fragment at the top of the back stack. This can be accomplished like this:

```java
    FragmentManager fm = getSupportFragmentManager();
    FragmentBoss.topFragmentOnResume(fm);
```

---

## More

### Take a look at the source code
The interesting parts [are right here.](../blob/master/fragmentboss/src/main/java/com/nothingworksright/fragmentboss/FragmentBoss.java)

For additional information, please refer to the [fragmentBoss GitHub Wiki](https://github.com/nothingworksright/fragmentBoss/wiki).  
