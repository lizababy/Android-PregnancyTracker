package publish.android.lizalinto.pregnancytracker.HelperClass;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Adapter to populate the calendar view as grid view.
 *
 * implements on touch and click listener to go to previous and next
 *
 * Event marker is added for those cells having events saved in phone
 */
public class CalendarAdapter extends BaseAdapter {
    private Context mContext;

    private java.util.Calendar mMonth;
    public GregorianCalendar mPrevMonth; // calendar instance for previous month
    /**
     * calendar instance for previous month for getting complete view
     */
    public GregorianCalendar mPrevMonthMaxSet;
    int mFirstDay;
    int mMaxWeekNumber;
    int mMaxP;
    int mCalMaxP;
    int mMonthLength;
    String mItemValue, mCurrentDateString;
    DateFormat mDateFormat;

    private ArrayList<String> mItems;
    public static List<String> mDayString;
    private View mPreviousView;

    public CalendarAdapter(Context c, GregorianCalendar monthCalendar) {
        mDayString = new ArrayList<>();
        Locale.setDefault(Locale.US);
        mMonth = monthCalendar;
        GregorianCalendar selectedDate = (GregorianCalendar) monthCalendar.clone();
        mContext = c;
        mMonth.set(GregorianCalendar.DAY_OF_MONTH, 1);
        this.mItems = new ArrayList<>();
        mDateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
        mCurrentDateString = mDateFormat.format(selectedDate.getTime());
        refreshDays();
    }

    public void setItems(ArrayList<String> items) {
        for (int i = 0; i != items.size(); i++) {
            if (items.get(i).length() == 1) {
                items.set(i, "0" + items.get(i));
            }
        }
        this.mItems = items;
    }

    public int getCount() {
        return mDayString.size();
    }

    public Object getItem(int position) {
        return mDayString.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View calendarView = convertView;
        TextView dayView;
        if (convertView == null) { // if it's not recycled, initialize some
            // attributes
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            calendarView = inflater.inflate(publish.android.lizalinto.pregnancytracker.R.layout.calendar_item, null);

        }
        dayView = (TextView) calendarView.findViewById(publish.android.lizalinto.pregnancytracker.R.id.date);
        // separates dayString into parts.
        String[] separatedTime = mDayString.get(position).split("-");
        // taking last part of date. ie; 2 from 2012-12-02
        String gridValue = separatedTime[2].replaceFirst("^0*", "");

        // checking whether the day is in current month or not for
        //setting color of day
        if ((Integer.parseInt(gridValue) > 1) && (position < mFirstDay)) {
            // setting offDays to white color.
            dayView.setTextColor(Color.GRAY);
            dayView.setClickable(false);
            dayView.setFocusable(false);
        } else if ((Integer.parseInt(gridValue) < 7) && (position > 28)) {
            dayView.setTextColor(Color.GRAY);
            dayView.setClickable(false);
            dayView.setFocusable(false);
        } else {
            // setting current month's days in red color.
            dayView.setTextColor(Color.argb(255,111,16,8));
        }

        if (mDayString.get(position).equals(mCurrentDateString)) {

            mPreviousView = calendarView;
            calendarView.setBackgroundResource(publish.android.lizalinto.pregnancytracker.R.drawable.calendar_cel_selectl);
        } else {
            calendarView.setBackgroundResource(publish.android.lizalinto.pregnancytracker.R.drawable.list_item_background);
        }
        dayView.setText(gridValue);

        // create date string for comparison
        String date = mDayString.get(position);

        if (date.length() == 1) {
            date = "0" + date;
        }


        // show icon if date is not empty and it exists in the items array
        ImageView iView = (ImageView) calendarView.findViewById(publish.android.lizalinto.pregnancytracker.R.id.date_icon);
        if (date.length() > 0 && mItems != null && mItems.contains(date)) {
            iView.setVisibility(View.VISIBLE);
        } else {
            iView.setVisibility(View.INVISIBLE);
        }
        return calendarView;
    }

    public View setSelected(View view) {
        if (mPreviousView != null) {
            mPreviousView.setBackgroundResource(publish.android.lizalinto.pregnancytracker.R.drawable.list_item_background);

        }
        mPreviousView = view;
        view.setBackgroundResource(publish.android.lizalinto.pregnancytracker.R.drawable.calendar_cel_selectl);
        return view;
    }

    public void refreshDays() {
        // clear items
        mItems.clear();
        mDayString.clear();
        Locale.setDefault(Locale.US);
        mPrevMonth = (GregorianCalendar) mMonth.clone();
        // month start day. ie; sun, mon, etc
        mFirstDay = mMonth.get(GregorianCalendar.DAY_OF_WEEK);
        // finding number of weeks in current month.
        mMaxWeekNumber = mMonth.getActualMaximum(GregorianCalendar.WEEK_OF_MONTH);
        // allocating maximum row number for the gridView.
        mMonthLength = mMaxWeekNumber * 7;
        mMaxP = getMaxP(); // previous month maximum day 31,30....
        mCalMaxP = mMaxP - (mFirstDay - 1);// calendar offDay starting 24,25 ...
        /**
         * Calendar instance for getting a complete gridView including the three
         * month's (previous,current,next) dates.
         */
        mPrevMonthMaxSet = (GregorianCalendar) mPrevMonth.clone();
        /**
         * setting the start date as previous month's required date.
         */
        mPrevMonthMaxSet.set(GregorianCalendar.DAY_OF_MONTH, mCalMaxP + 1);

        /**
         * filling calendar gridView.
         */
        for (int n = 0; n < mMonthLength; n++) {

            mItemValue = mDateFormat.format(mPrevMonthMaxSet.getTime());
            mPrevMonthMaxSet.add(GregorianCalendar.DATE, 1);
            mDayString.add(mItemValue);

        }
    }

    private int getMaxP() {
        int maxP;
        if (mMonth.get(GregorianCalendar.MONTH) == mMonth
                .getActualMinimum(GregorianCalendar.MONTH)) {
            mPrevMonth.set((mMonth.get(GregorianCalendar.YEAR) - 1),
                    mMonth.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            mPrevMonth.set(GregorianCalendar.MONTH,
                    mMonth.get(GregorianCalendar.MONTH) - 1);
        }
        maxP = mPrevMonth.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

        return maxP;
    }

}
