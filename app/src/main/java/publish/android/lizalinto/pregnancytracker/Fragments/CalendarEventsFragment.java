package publish.android.lizalinto.pregnancytracker.Fragments;

import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import publish.android.lizalinto.pregnancytracker.HelperClass.CalendarUtility;

import java.util.ArrayList;

/**
 * This fragment displays the events saved on the phone calendar on a particular day.
 *
 * It will not show full day events saved in phone; only shows events saved with start time and end time
 */
public class CalendarEventsFragment extends ListFragment{

    private static final String ARG_DATE = "date" ;
    private String mDate;
    private ArrayList<String> mEvents;

    public static Fragment newInstance(String date) {
        CalendarEventsFragment fragment = new CalendarEventsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATE,date);
        fragment.setArguments(args);

        return fragment;
    }
    public CalendarEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mDate = getArguments().getString(ARG_DATE);
        }
        /*Reads the calendar events saved on this date excluding all events and stored in array list*/
        try {
            mEvents = CalendarUtility.readCalendarEventsOfTheDay(getActivity(), mDate);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(publish.android.lizalinto.pregnancytracker.R.layout.fragment_list, container, false);
        TextView footerTv = (TextView)v.findViewById(publish.android.lizalinto.pregnancytracker.R.id.textView);

        /*Footer view that shows only when there is nothing to display*/
        if(mEvents.isEmpty()) {
            footerTv.setVisibility(View.VISIBLE);
            footerTv.setText(publish.android.lizalinto.pregnancytracker.R.string.event_empty);
        }

        return v;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        //Reuse the layout file used in other fragments with add button hidden
        Button addButton = (Button)view.findViewById(publish.android.lizalinto.pregnancytracker.R.id.button_add);
        addButton.setVisibility(View.INVISIBLE);

        TextView tv = (TextView)view.findViewById(publish.android.lizalinto.pregnancytracker.R.id.item_title);
        tv.setText(publish.android.lizalinto.pregnancytracker.R.string.calendar_events);
        //
        // Populate Array list
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mEvents);
        getListView().setAdapter(adapter);

    }

}
