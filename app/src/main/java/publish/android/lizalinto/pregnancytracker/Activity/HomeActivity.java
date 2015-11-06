package publish.android.lizalinto.pregnancytracker.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

import publish.android.lizalinto.pregnancytracker.Fragments.CalendarLogFragment;
import publish.android.lizalinto.pregnancytracker.Fragments.CalendarViewFragment;
import publish.android.lizalinto.pregnancytracker.Fragments.ContactsFragment;
import publish.android.lizalinto.pregnancytracker.Fragments.NavigationDrawerFragment;
import publish.android.lizalinto.pregnancytracker.HelperClass.DataBaseHelper;
import publish.android.lizalinto.pregnancytracker.Model.TableEntry;
import publish.android.lizalinto.pregnancytracker.Fragments.SettingsFragment;
import publish.android.lizalinto.pregnancytracker.Fragments.TrackerFragment;

import java.io.IOException;


/***
 * Home activity that hosts navigation drawer and a fragment container
 * Based on the selection on items on navigation drawer corresponding fragments will get replaced
 * Fragment at position zero, Tracker fragment is the default view in the home screen.
 * *
 * Creates pregnancy database if it is not created!
 * App uses database and several tables . These are all pre created and saved in assets folder.
 *
 *  On first time and on version change it reads from database file from assets folder and copy to
 *  default database folder . This is done on this activity.
 */


 public class HomeActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, TrackerFragment.OnDueDateSelectedListener {

    private NavigationDrawerFragment mNavigationDrawerFragment;

    public CharSequence mTitle;

    private int mCurrentFragmentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(publish.android.lizalinto.pregnancytracker.R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(publish.android.lizalinto.pregnancytracker.R.id.navigation_drawer);
        mTitle = getTitle();
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(publish.android.lizalinto.pregnancytracker.R.id.navigation_drawer,
                (DrawerLayout) findViewById(publish.android.lizalinto.pregnancytracker.R.id.drawer_layout));

        DataBaseHelper db = new DataBaseHelper(this);
        //creating new pregnancy database, if not created or when need to upgrade
        try {
            db.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        // update the menu_main content by replacing fragments

        mCurrentFragmentPosition = position;
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment;

        switch (position){

            case 0://Shows the progress of pregnancy
                fragment = TrackerFragment.newInstance(position);
                break;

            case 1://shows the calendar view
                fragment = CalendarViewFragment.newInstance(position);
                break;

            case 2://application settings
                fragment = SettingsFragment.newInstance(position);
                break;

            case 3://app contacts list
                fragment = ContactsFragment.newInstance(position);
                break;

            case 4://Symptoms list
                 String symptomsListTitle = getString(publish.android.lizalinto.pregnancytracker.R.string.symptoms_list_title);
                 onSectionAttached(4);
                 fragment = CalendarLogFragment.newInstance(TableEntry.TABLE_SYMPTOMS, symptomsListTitle, "-");
                 break;

            case 5://Events list
                String eventsListTitle = getString(publish.android.lizalinto.pregnancytracker.R.string.events_list_title);
                onSectionAttached(5);
                fragment = CalendarLogFragment.newInstance(TableEntry.TABLE_EVENTS, eventsListTitle, "-");
                break;

            case 6://App notes list
                String notesListTitle = getString(publish.android.lizalinto.pregnancytracker.R.string.notes_list_title);
                onSectionAttached(6);
                fragment = CalendarLogFragment.newInstance(TableEntry.TABLE_NOTES, notesListTitle, "-");
                break;

            default://Shows the progress of pregnancy;But this never call
                fragment = TrackerFragment.newInstance(position);
        }

        fragmentManager.beginTransaction()
                .replace(publish.android.lizalinto.pregnancytracker.R.id.container, fragment)
                .commit();


    }
    /*sets the title of current fragment */
    public void onSectionAttached(int number) {

        String[] navigationItems = getResources().getStringArray(publish.android.lizalinto.pregnancytracker.R.array.navigationItems);
        mTitle = navigationItems[number];
    }
    /* Sets the subtitle of current fragment on action bar*/
    public void restoreActionBar() {

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setSubtitle(mTitle);
    }

    /* Population action bar menu. what actions to do on selection of options is
      *  defined in navigation drawer fragment*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(publish.android.lizalinto.pregnancytracker.R.menu.menu_main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    /*To prevent users from quiting the app on back press from any other fragment except main.
                                activity with fragment position 0 is considered as home*/

    @Override
    public void onBackPressed() {

        if(mCurrentFragmentPosition != 0){

            onSectionAttached(0);
            restoreActionBar();
            mNavigationDrawerFragment.selectItem(0);
        }
        else {
            super.onBackPressed();
        }
    }

    /*Call back from Tracker fragment to communicate event to settings fragment*/
    /*Allows users to click on the button to set the due date when it is not set or need to change*/
    @Override
    public void OnDueDateSelected() {

        onSectionAttached(2);
        restoreActionBar();
        mNavigationDrawerFragment.selectItem(2);

    }
}
