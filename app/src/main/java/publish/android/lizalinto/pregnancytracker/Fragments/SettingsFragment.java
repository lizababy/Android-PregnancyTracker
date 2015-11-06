package publish.android.lizalinto.pregnancytracker.Fragments;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import publish.android.lizalinto.pregnancytracker.Activity.HomeActivity;
import publish.android.lizalinto.pregnancytracker.Calculations.PregnancyMaths;
import publish.android.lizalinto.pregnancytracker.Preferences.DatePickerPreference;

import java.util.Calendar;

/**
 * Preference fragment to show application specific settings
 * **/

public class SettingsFragment extends PreferenceFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    DatePickerPreference dueDatePickerPref;
    private ListPreference listPref;

    public static SettingsFragment newInstance(int sectionNumber) {

        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(publish.android.lizalinto.pregnancytracker.R.xml.pref_settings);

        ///-------------Settings for Due Date------------------///
        dueDatePickerPref = (DatePickerPreference) findPreference("dueDate_pref");

        dueDateSettings(dueDatePickerPref);

        ///-------------Settings for DLP------------------///
        final DatePickerPreference dlpDatePickerPref = (DatePickerPreference) findPreference("dlpDate_pref");

        dlpSettings(dlpDatePickerPref);


        ///-------------Settings for average Cycle------------------///

        listPref= (ListPreference) findPreference("avgCycle_pref");
        String savedValue = listPref.getSharedPreferences().getString("avgCycle_pref",getString(publish.android.lizalinto.pregnancytracker.R.string.pref_avg_cycle_summary));
        listPref.setSummary(savedValue);

        listPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference,Object newValue) {

                listPref.setSummary((String) newValue);
                return true;
            }
        });


        ///-------------Settings for PrePregnancy weight------------------///
        final EditTextPreference weightEditTextPref = (EditTextPreference) findPreference("pre_weight");
        final String savedWeight = weightEditTextPref.getSharedPreferences().getString("pre_weight", getString(publish.android.lizalinto.pregnancytracker.R.string.pref_weight_def_summary));
        weightEditTextPref.setSummary(savedWeight+" (lbs)");

        weightEditTextPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                weightEditTextPref.setSummary(newValue +" (lbs)");
                return false;
            }
        });


    }

    private void dlpSettings(final DatePickerPreference dlpDatePickerPref) {

        long minDate;
        long maxDate;
        Calendar calendarMin;//get the min range
        Calendar calendarMax;//get the max range

        calendarMin = Calendar.getInstance();

        calendarMin.add(Calendar.DATE,-280);
        minDate = calendarMin.getTimeInMillis();

        calendarMax = Calendar.getInstance();
        maxDate = calendarMax.getTimeInMillis();


        dlpDatePickerPref.setMinDate(minDate);
        dlpDatePickerPref.setMaxDate(maxDate);


        String savedDlp = dlpDatePickerPref.getSharedPreferences().getString("dlpDate_pref", getString(publish.android.lizalinto.pregnancytracker.R.string.dlp_default_summary));
        dlpDatePickerPref.setSummary(savedDlp);

        dlpDatePickerPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                dlpDatePickerPref.setSummary((String) newValue);
                String calculatedDueDate = PregnancyMaths.calculateDueDate((String) newValue);
                dueDatePickerPref.setSummary(calculatedDueDate);

                SharedPreferences dueDatePref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = dueDatePref.edit();

                editor.putString("dueDate_pref",calculatedDueDate);

                // Commit the edits!
                editor.apply();

                return true;
            }
        });
    }

    private void dueDateSettings(final DatePickerPreference dueDatePickerPref) {

        long minDate;
        long maxDate;
        Calendar calendarMin;//min day
        Calendar calendarMax;//max day

        calendarMin = Calendar.getInstance();
        minDate = calendarMin.getTimeInMillis();

        calendarMax = Calendar.getInstance();

        calendarMax.add(Calendar.DATE,280);
        maxDate = calendarMax.getTimeInMillis();

        String savedDueDate = dueDatePickerPref.getSharedPreferences().
                getString("dueDate_pref", getString(publish.android.lizalinto.pregnancytracker.R.string.pref_due_date_def_summary));
        dueDatePickerPref.setSummary(savedDueDate);

        dueDatePickerPref.setMinDate(minDate);
        dueDatePickerPref.setMaxDate(maxDate);


        dueDatePickerPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                dueDatePickerPref.setSummary((String) newValue);
                return true;
            }
        });


    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((HomeActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }



}
