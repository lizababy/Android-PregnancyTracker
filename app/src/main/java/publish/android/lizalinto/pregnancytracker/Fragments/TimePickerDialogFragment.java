package publish.android.lizalinto.pregnancytracker.Fragments;


import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Tme picker dialog for appointment scheduling
 * */

public class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {


    private static final String DATA_BACK = "text";

    public TimePickerDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }


    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {

        int hourIn12Format = getHourIn12Format(hourOfDay);

        String timeSet =  hourIn12Format + " : " + minute + " " + getAM_PM(hourOfDay);
        Intent toBack = getActivity().getIntent();
        toBack.putExtra(DATA_BACK, timeSet.trim());
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, toBack);

    }
    private int getHourIn12Format(int hour24) {
        int hourIn12Format;
        if(hour24==0)
            hourIn12Format = 12;
        else if(hour24<=12)
            hourIn12Format = hour24;
        else
            hourIn12Format = hour24-12;
        return hourIn12Format;
    }

    private String getAM_PM(int hour24) {
        return (hour24 < 12)? "AM":"PM";
    }
}
