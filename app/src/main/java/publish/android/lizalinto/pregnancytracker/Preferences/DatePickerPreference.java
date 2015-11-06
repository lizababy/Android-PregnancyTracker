package publish.android.lizalinto.pregnancytracker.Preferences;

/**
 * Date picker preference for settings on dates
 */

import android.content.Context;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import publish.android.lizalinto.pregnancytracker.Calculations.PregnancyMaths;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class DatePickerPreference extends DialogPreference {


    private DatePicker picker;


    PregnancyMaths.DueDate mDueDate;
    long mMinDate;
    long mMaxDate;



    public void setMinDate(long minDate) {
        mMinDate = minDate;
    }


    public void setMaxDate(long maxDate) {
        mMaxDate = maxDate;
    }

    public DatePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);


        mDueDate = new PregnancyMaths.DueDate(0,0,0);
        Log.i("due",attrs.toString());

        setPositiveButtonText(publish.android.lizalinto.pregnancytracker.R.string.set);
        setNegativeButtonText(publish.android.lizalinto.pregnancytracker.R.string.cancel);


    }
    @Override
    protected void onBindDialogView(@NonNull View v) {
        super.onBindDialogView(v);

        int lastYear = mDueDate.getYear();
        int lastMonth = mDueDate.getMonth();
        int lastDay = mDueDate.getDay();

        picker.updateDate(lastYear, lastMonth, lastDay);
    }


    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        String dueDate;

        if (restorePersistedValue) {
            if (defaultValue == null) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                String formatted = dateFormat.format(calendar.getTime());
                dueDate = getPersistedString(formatted);
            } else {
                dueDate = getPersistedString(defaultValue.toString());
            }
        } else {
            dueDate = defaultValue.toString();
        }

        int lastYear = PregnancyMaths.getYear(dueDate);
        int lastMonth = PregnancyMaths.getMonth(dueDate);
        int lastDay = PregnancyMaths.getDay(dueDate);
        mDueDate = new PregnancyMaths.DueDate(lastMonth,lastDay,lastYear);
    }

    @Override
    protected View onCreateDialogView() {
        picker = new DatePicker(getContext());
        picker.setCalendarViewShown(false);
        picker.setMinDate(mMinDate);
        picker.setMaxDate(mMaxDate);

        return picker;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {

            int lastYear = picker.getYear();
            int lastMonth = picker.getMonth();
            int lastDay = picker.getDayOfMonth();
            mDueDate = new PregnancyMaths.DueDate(lastMonth,lastDay,lastYear);
            String dueDate = mDueDate.toString();

            if (callChangeListener(dueDate)) {
                persistString(dueDate);
            }
        }
    }

}
