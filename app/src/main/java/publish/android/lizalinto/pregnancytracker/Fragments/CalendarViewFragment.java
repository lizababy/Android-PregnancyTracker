package publish.android.lizalinto.pregnancytracker.Fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import publish.android.lizalinto.pregnancytracker.Activity.CalendarLogActivity;
import publish.android.lizalinto.pregnancytracker.Activity.HomeActivity;
import publish.android.lizalinto.pregnancytracker.HelperClass.CalendarAdapter;
import publish.android.lizalinto.pregnancytracker.HelperClass.CalendarUtility;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CalendarViewFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, View.OnTouchListener {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    public GregorianCalendar mMonth;// calendar instances.

    public CalendarAdapter mAdapter;// adapter instance
    public Handler mHandler;// for grabbing some event values for showing the dot marker.
    public ArrayList<String> mItems; // container to store calendar items which
    // needs showing the event marker
    private GestureDetector mGestureDetector;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CalendarViewFragment newInstance(int sectionNumber) {
        CalendarViewFragment fragment = new CalendarViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    public CalendarViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(publish.android.lizalinto.pregnancytracker.R.layout.fragment_calendar_view, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        //----------Load initial states-----------//
        Locale.setDefault(Locale.US);
        mMonth = (GregorianCalendar) GregorianCalendar.getInstance();

        mItems = new ArrayList<>();

        mAdapter = new CalendarAdapter(getActivity(), mMonth);

        GridView gridView = (GridView) view.findViewById(publish.android.lizalinto.pregnancytracker.R.id.gridView);
        gridView.setAdapter(mAdapter);

        mHandler = new Handler();
        mHandler.post(calendarUpdater);


        TextView titleTV = (TextView) view.findViewById(publish.android.lizalinto.pregnancytracker.R.id.title);
        titleTV.setText(android.text.format.DateFormat.format("MMMM yyyy", mMonth));

        RelativeLayout previousRelLayout = (RelativeLayout) view.findViewById(publish.android.lizalinto.pregnancytracker.R.id.previous);
        previousRelLayout.setOnClickListener(this);

        RelativeLayout nextRelLayout = (RelativeLayout) view.findViewById(publish.android.lizalinto.pregnancytracker.R.id.next);
        nextRelLayout.setOnClickListener(this);

        // Create an object of our Custom Gesture Detector Class
        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        // Create a GestureDetector
        mGestureDetector = new GestureDetector(getActivity(), customGestureDetector);

        LinearLayout linearL = (LinearLayout)view.findViewById(publish.android.lizalinto.pregnancytracker.R.id.linearL_id);
        linearL.setOnTouchListener(this);

        gridView.setOnItemClickListener(this);


    }
    protected void setNextMonth() {
        if (mMonth.get(GregorianCalendar.MONTH) == mMonth
                .getActualMaximum(GregorianCalendar.MONTH)) {
            mMonth.set((mMonth.get(GregorianCalendar.YEAR) + 1),
                    mMonth.getActualMinimum(GregorianCalendar.MONTH), 1);
        } else {
            mMonth.set(GregorianCalendar.MONTH,
                    mMonth.get(GregorianCalendar.MONTH) + 1);
        }

    }

    protected void setPreviousMonth() {
        if (mMonth.get(GregorianCalendar.MONTH) == mMonth
                .getActualMinimum(GregorianCalendar.MONTH)) {
            mMonth.set((mMonth.get(GregorianCalendar.YEAR) - 1),
                    mMonth.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            mMonth.set(GregorianCalendar.MONTH,
                    mMonth.get(GregorianCalendar.MONTH) - 1);
        }

    }
    public void refreshCalendar() {

        mAdapter.refreshDays();
        mAdapter.notifyDataSetChanged();
        mHandler.post(calendarUpdater); // generate calendar items
        if(getView()!=null) {
            TextView title = (TextView) getView().findViewById(publish.android.lizalinto.pregnancytracker.R.id.title);

            title.setText(android.text.format.DateFormat.format("MMMM yyyy", mMonth));
        }
    }

    public Runnable calendarUpdater = new Runnable() {

        @Override
        public void run() {
            mItems.clear();
            try {
                CalendarUtility.readCalendarEventDates(getActivity());
            }catch (Exception e){
                e.printStackTrace();
            }
            for (int i = 0; i < CalendarUtility.startDates.size(); i++) {
                mItems.add(CalendarUtility.startDates.get(i));
            }
            mAdapter.setItems(mItems);
            mAdapter.notifyDataSetChanged();
        }
    };



    @Override
    public void onClick(View view) {
        switch(view.getId()){

            case publish.android.lizalinto.pregnancytracker.R.id.previous:
                setPreviousMonth();
                refreshCalendar();
                break;

            case publish.android.lizalinto.pregnancytracker.R.id.next:
                setNextMonth();
                refreshCalendar();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {



        String selectedGridDate = CalendarAdapter.mDayString.get(position);
        String[] separatedTime = selectedGridDate.split("-");
        String gridValueString = separatedTime[2].replaceFirst("^0*","");// taking last part of date. ie; 2 from 2012-12-02.

        int gridValue = Integer.parseInt(gridValueString);

        // navigate to next or previous month on clicking offDays.
        if ((gridValue > 10) && (position < 8)) {
            setPreviousMonth();
            refreshCalendar();
        } else if ((gridValue < 7) && (position > 28)) {
            setNextMonth();
            refreshCalendar();
        }

        ((CalendarAdapter) adapterView.getAdapter()).setSelected(view);

          //Start new CalendarDetailActivity
        Intent intent = new Intent(getActivity(), CalendarLogActivity.class);
        intent.putExtra("date",selectedGridDate);
        startActivity(intent);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        mGestureDetector.onTouchEvent(motionEvent);

        return true;
    }

    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {


        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //Right swipe
            if (e1.getX() < e2.getX()) {
                setNextMonth();
                refreshCalendar();
            }
            //left swipe
            if (e1.getX() > e2.getX()) {
                setPreviousMonth();
                refreshCalendar();
            }

            return true;
        }

    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((HomeActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

}
