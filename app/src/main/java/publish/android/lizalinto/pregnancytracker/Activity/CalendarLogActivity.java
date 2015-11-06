package publish.android.lizalinto.pregnancytracker.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;

import publish.android.lizalinto.pregnancytracker.Fragments.CalendarEventsFragment;
import publish.android.lizalinto.pregnancytracker.Fragments.CalendarLogFragment;
import publish.android.lizalinto.pregnancytracker.Model.TableEntry;
import publish.android.lizalinto.pregnancytracker.view.SlidingTabLayout;

/**
 * Used to implement the diary concept that user can see and write details.
 * The details are shown in each page implemented with view pager fragment.
 *
 * The view pager contains 7 pages ,with first page showing the calendar events saved on phone.
 * Other 6 pages are instances of a single fragment that varies with different database tables using.
 *
 * This activity is invoked by 2 ways; either from on option selected from action bar icon or
 * from calendar view fragment on click on a cell corresponding to a date
 *
 * Activity invoked from action bar icon click will show the calendar log for today's date
 * where as from calendar view it will show the corresponding date on calendar**/

 public class CalendarLogActivity extends ActionBarActivity {

    private static final java.lang.String DATE_KEY = "date";
    private static String mDateTitle;

    public static String getDateTitle() {
        return mDateTitle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(publish.android.lizalinto.pregnancytracker.R.layout.activity_calendar_log);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(publish.android.lizalinto.pregnancytracker.R.drawable.ic_action_previous_item);
        }


        mDateTitle = getIntent().getExtras().getString(DATE_KEY, "");
        TextView dateTV = (TextView) findViewById(publish.android.lizalinto.pregnancytracker.R.id.title);
        dateTV.setText(mDateTitle);

        ViewPager viewPager = (ViewPager) findViewById(publish.android.lizalinto.pregnancytracker.R.id.viewpager);
        viewPager.setAdapter(new CalendarFragmentPagerAdapter(getFragmentManager()));

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(publish.android.lizalinto.pregnancytracker.R.id.sliding_tabs);
        slidingTabLayout.setViewPager(viewPager);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home){
            //Finishes this activity to go to previous page on clicking previous icon
                finish();
         }

        return super.onOptionsItemSelected(item);
    }

    /* For showing the different view pages for the users to log their details. It hosts 7 fragments(1+6) */

    private class CalendarFragmentPagerAdapter extends FragmentPagerAdapter {

        public CalendarFragmentPagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int position) {

            String title = getPageTitle(position).toString() ;
            if(position==0){

                //----------New instance of CalendarEventsFragment-------------//
                return CalendarEventsFragment.newInstance(getDateTitle());

            }else {

                //----------New instance of CalendarDetailFragment-------------//
                return CalendarLogFragment.newInstance(title, capitalizeFirstLetter(title), getDateTitle());
            }

        }

        /*Returns the title of view pager*/
        @Override
        public CharSequence getPageTitle(int position) {
            if(position ==0){
                return getString(publish.android.lizalinto.pregnancytracker.R.string.R_string_Calendar_events_title);
            }else {

                return TableEntry.getTitles()[position-1];
            }
        }

        @Override
        public int getCount() {
            return TableEntry.getTitles().length + 1;
        }
        /*Capitalise the first letter for showing the title if it is not.
        * The database table name, which is in small letter, is used as the title*/
        private String capitalizeFirstLetter(String original) {
            if(original.length() == 0)
                return original;
            return original.substring(0, 1).toUpperCase() + original.substring(1);
        }

    }
}
