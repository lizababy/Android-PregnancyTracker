package publish.android.lizalinto.pregnancytracker.HelperClass;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * This static class is used to query the calendar events date to show in calendar view and
 * also query on event name, start date,end date on a particular date to be vied on phone events page on
 * calendarLogFragment
 */
public class CalendarUtility {
    public static ArrayList<String> startDates = new ArrayList<String>();

    public static void readCalendarEventDates(Context context) {

        // Projection array. Creating indices for this array instead of doing
        // dynamic lookups improves performance.
         final String[] EVENT_PROJECTION = new String[] {
                CalendarContract.Events.CALENDAR_ID,
                CalendarContract.Events.DTSTART
        };

        final int PROJECTION_DTSTART_INDEX = 1;


        ContentResolver cr = context.getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;
        startDates.clear();
        //Disables the all day events as it is may saved in utc time zone,can result in mismatches in actual date
        String selection = CalendarContract.Events.ALL_DAY + " = 0";
        Cursor cursor = cr.query(uri, EVENT_PROJECTION, selection, null, null);
        if (cursor.moveToFirst()) {
            do {
                startDates.add(getDate(Long.parseLong(cursor.getString(PROJECTION_DTSTART_INDEX))));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public static ArrayList<String> readCalendarEventsOfTheDay(Context context, String date) throws ParseException {
        // Projection array. Creating indices for this array instead of doing
        // dynamic lookups improves performance.
        final String[] EVENT_PROJECTION = new String[] {
                CalendarContract.Events.CALENDAR_ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND

        };

        // The indices for the projection array above.
        final int PROJECTION_TITLE_INDEX = 1;
        final int PROJECTION_DTSTART_INDEX = 2;
        final int PROJECTION_DTEND_INDEX = 3;


        ArrayList<String> nameOfEvent = new ArrayList<String>();

        ContentResolver cr = context.getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;

        long dateInMillis = getTimeMillis(date);

        Calendar endOfDay = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss yyyy-MM-dd",Locale.US);
        Date dateEnd = null;
        try {
             dateEnd = formatter.parse("24:59:59 " + date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        endOfDay.setTime(dateEnd);


        String selection = "(" + CalendarContract.Events.DTSTART + ">=" + dateInMillis + " and "
                + CalendarContract.Events.DTEND + "<" + endOfDay.getTimeInMillis() + ")";

        String sortOrder = CalendarContract.Events.DTSTART +" ASC";

        Cursor cursor = cr.query(uri, EVENT_PROJECTION,selection ,null, sortOrder);
        String startTime ;
        String endTime;
        String eventName;

        if (cursor.moveToFirst()) {

            do {
                startTime = getTime(Long.parseLong(cursor.getString(PROJECTION_DTSTART_INDEX)));
                endTime = getTime(Long.parseLong((cursor.getString(PROJECTION_DTEND_INDEX))));
                eventName = cursor.getString(PROJECTION_TITLE_INDEX);

                nameOfEvent.add(eventName + "\n[ " + startTime + " - " + endTime + " ]" );

            } while (cursor.moveToNext());
        }
        cursor.close();
        return nameOfEvent;
    }

    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static String getTime(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss a", Locale.US);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static long getTimeMillis(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date newDate = null;
        try {
            newDate = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar beginTime = Calendar.getInstance();
        beginTime.setTime(newDate);
        return beginTime.getTimeInMillis();
    }

}
