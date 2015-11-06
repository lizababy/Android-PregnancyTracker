package publish.android.lizalinto.pregnancytracker.Fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import publish.android.lizalinto.pregnancytracker.Activity.HomeActivity;
import publish.android.lizalinto.pregnancytracker.Calculations.PregnancyMaths;

/**
 * Default view fragment in the home activity
 *
 * Shows the current due date on clicking it sends callback to activity to start settings fragment
 *
 * Shows countdown(days until due date), current week, current status in month - week -days format
 * assuming an ideal condition of total 280 days period of pregnancy
 *
 */
public class TrackerFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private boolean isSetDueDate = true;
    OnDueDateSelectedListener mDueDateSelectedListener;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TrackerFragment newInstance(int sectionNumber) {
        TrackerFragment fragment = new TrackerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public TrackerFragment() {
    }
    // Container Activity must implement this interface
    public interface OnDueDateSelectedListener {
        public void OnDueDateSelected();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ((HomeActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        try {
            mDueDateSelectedListener = (OnDueDateSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnDueDateSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            View rootView = inflater.inflate(publish.android.lizalinto.pregnancytracker.R.layout.fragment_tracker, container, false);



            SharedPreferences dueDatePref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String savedDueDate = dueDatePref.getString("dueDate_pref", getString(publish.android.lizalinto.pregnancytracker.R.string.due_date_initial_summary));
            if(savedDueDate.equalsIgnoreCase(getString(publish.android.lizalinto.pregnancytracker.R.string.due_date_initial_summary))){
                isSetDueDate = false;
            }

            TextView tvDueDate = (TextView)rootView.findViewById(publish.android.lizalinto.pregnancytracker.R.id.dueDate_tv);
            tvDueDate.setText(savedDueDate);
            LinearLayout dueDateLinearL = (LinearLayout)rootView.findViewById(publish.android.lizalinto.pregnancytracker.R.id.due_linearL_id);
            dueDateLinearL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDueDateSelectedListener.OnDueDateSelected();
                }
            });

            TextView tvCountDown = (TextView) rootView.findViewById(publish.android.lizalinto.pregnancytracker.R.id.countDown_tv);

            TextView tvCurrentWeek = (TextView) rootView.findViewById(publish.android.lizalinto.pregnancytracker.R.id.week_tv);
            TextView tvWaitMonths = (TextView) rootView.findViewById(publish.android.lizalinto.pregnancytracker.R.id.waitMonths_tv);
            TextView tvWaitWeeks = (TextView) rootView.findViewById(publish.android.lizalinto.pregnancytracker.R.id.waitWeeks_tv);
            TextView tvWaitDays = (TextView) rootView.findViewById(publish.android.lizalinto.pregnancytracker.R.id.waitDays_tv);
            ProgressBar progressBar = (ProgressBar) rootView.findViewById(publish.android.lizalinto.pregnancytracker.R.id.progressBar);


            if(isSetDueDate) {

                int countDown = PregnancyMaths.getCountDown(savedDueDate, "MMM dd, yyyy");
                int currentDay = PregnancyMaths.getCurrentDay(savedDueDate, "MMM dd, yyyy");

                int currentWeek = currentDay/7;
                int currentMonth = currentDay/30;
                int remainingWeeksOFMonth = (currentDay % 30) / 7;
                int remainingDaysOfWeek = (currentDay % 30) % 7;
                tvCountDown.setText(String.valueOf(countDown));
                tvCurrentWeek.setText(String.valueOf(currentWeek));
                tvWaitMonths.setText(String.valueOf(currentMonth));
                tvWaitWeeks.setText(String.valueOf(remainingWeeksOFMonth));
                tvWaitDays.setText(String.valueOf(remainingDaysOfWeek));
                progressBar.setProgress(currentDay);

            }else{

                tvCountDown.setText("");
                tvCurrentWeek.setText("");
                tvWaitMonths.setText("");
                tvWaitWeeks.setText("");
                tvWaitDays.setText("");

            }


            return rootView;
    }


}