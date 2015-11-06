package publish.android.lizalinto.pregnancytracker.Calculations;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Handles all date calculations related to pregnancy, required to show in the tracker fragment and settings fragment
 */
public class PregnancyMaths {

    /*Calculates due date  from the date of last period set from settings fragment*/

    public static String calculateDueDate(String dlpDate) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);

        Date newDate = stringToDate(dlpDate,"MMM dd, yyyy");

        Calendar dueDate = Calendar.getInstance();
        dueDate.setTime(newDate);

        dueDate.add(Calendar.DATE, 280); // Adding 280 days

        return dateFormat.format(dueDate.getTime());
    }

    /*Converts a date in string with a date format to date type */

    public static Date stringToDate(String dateString, String dateFormat){
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat,Locale.US);
        Date date = null;
        try {
            date = formatter.parse(dateString);//catch exception
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /*Returns the no of days past last period from due date
    * This calculation assumes the normal case of 280 days*/
    public static int getCurrentDay(String savedDueDate, String dateFormat) {


        Date dueDate = stringToDate(savedDueDate,dateFormat);
        Calendar firstDay = Calendar.getInstance();
        firstDay.setTime(dueDate);

        firstDay.add(Calendar.DATE, -280); // subtracting 280 days


        Calendar today = Calendar.getInstance();

        long diff =  Math.abs(today.getTimeInMillis() - firstDay.getTimeInMillis()); //result in millis

        return Math.round(diff / (24 * 60 * 60 * 1000));

    }

    /* Due date class*/
    public static class DueDate{

        private int mMonth;
        private int mDay;
        private int mYear;

        public int getMonth() {
            return mMonth;
        }

        public int getDay() {
            return mDay;
        }

        public int getYear() {
            return mYear;
        }

        public DueDate(int month,int day, int year) {
            mMonth = month;
            mDay = day;
            mYear = year;
        }

        @Override
        public String toString() {
            return getMonthName(mMonth) + " " + mDay + ", " + mYear;
        }
    }


    public static int getMonth(String dueDate) {

        Date date = new Date();
        SimpleDateFormat dateFormat_in = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        SimpleDateFormat dateFormat_out = new SimpleDateFormat("MM",Locale.US);
        try {
            date = dateFormat_in.parse(dueDate);
        }  catch (ParseException e) {
            e.printStackTrace();
        }

        return Integer.parseInt(dateFormat_out.format(date))-1;
    }

    public static int getDay(String dueDate) {

        String[] pieces = dueDate.split(" ");
        String[] day = pieces[1].split(",");
        return (Integer.parseInt(day[0]));
    }
    public static int getYear(String dueDate) {

        String[] pieces = dueDate.split(" ");
        return (Integer.parseInt(pieces[2]));
    }
    public static String getMonthName(int month) {

        return new DateFormatSymbols().getMonths()[month];
    }

    /* Returns the countdown days from due date*/
    public static int getCountDown(String savedDueDate, String dateFormat) {

        Date dueDate = stringToDate(savedDueDate,dateFormat);

        Calendar thatDay = Calendar.getInstance();
        thatDay.setTime(dueDate);

        Calendar today = Calendar.getInstance();

        long diff =  Math.abs(thatDay.getTimeInMillis() - today.getTimeInMillis()); //result in millis

        return Math.round(diff / (24 * 60 * 60 * 1000));

    }

}
