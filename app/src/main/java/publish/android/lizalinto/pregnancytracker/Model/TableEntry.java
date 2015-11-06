package publish.android.lizalinto.pregnancytracker.Model;

import android.content.ContentValues;
import android.content.Intent;
/**
 * This class handles all database related things such as table,key names, classes for table and
 * some other functions to determine the selection columns in different columns
 */
public class TableEntry {

    //-------DataBase Table Names---------//
    public static final String TABLE_MEASURES = "measures";
    public static final String TABLE_EVENTS = "events";
    public static final String TABLE_SYMPTOMS = "symptoms";
    public static final String TABLE_NOTES = "notes";
    public static final String TABLE_APPOINTMENTS = "appointments";
    public static final String TABLE_TODOS = "todos";
    public static final String TABLE_DOCTORS = "doctors_contacts";

    //-------DataBase Table Columns Names---------//
    public static final String KEY_ID = "_id";
    public static final String KEY_TODO = "todo" ;
    public static final String KEY_STATUS = "status" ;
    public static final String KEY_DATE = "date" ;
    public static final String KEY_DOCTOR = "doctor_name";
    public static final String KEY_TIME = "time";
    public static final String KEY_SYMPTOM = "symptom";
    public static final String KEY_NOTE = "note";
    public static final String KEY_MEASURE = "measure";
    public static final String KEY_VALUE = "value";
    public static final String KEY_EVENT = "event";
    public static final String KEY_DOCTOR_PHONE = "phone";
    public static final String KEY_DOCTOR_EMAIL = "email";
    public static final String KEY_DOCTOR_STREET = "street";
    public static final String KEY_DOCTOR_ZIP = "zip";

    static String[] sSelectedColumns;

    static ContentValues sContentValues;
    static String sDate;

    public static String[] titles = {
            TABLE_MEASURES,
            TABLE_EVENTS,
            TABLE_SYMPTOMS,
            TABLE_APPOINTMENTS,
            TABLE_TODOS,
            TABLE_NOTES
    };

    public static String[] getTitles() {
        return titles;
    }

    public static String getSelectionDate(String table) {
        switch (table){
            case TABLE_SYMPTOMS:
            case TABLE_EVENTS:
            case TABLE_MEASURES:
                return "-";
            case TABLE_APPOINTMENTS:
                return null;
        }
        return sDate;
    }
    public static String getSelectionTable(String table) {
        if(table.equals(TABLE_APPOINTMENTS)){
            return TABLE_DOCTORS;
        }else{
            return table;
        }

    }

    public static String[] getSelectedColumns() {
        return sSelectedColumns;
    }

    public static void setSelectedColumns(String[] selectedColumns) {
        sSelectedColumns = selectedColumns;
    }

    public static ContentValues getContentValues() {
        return sContentValues;
    }

    public static void setContentValues(ContentValues contentValues) {
        sContentValues = contentValues;
    }
    public TableEntry(String table){

        switch (table) {
            case TABLE_MEASURES:

                setSelectedColumns(new String[]{KEY_ID, KEY_MEASURE, KEY_VALUE});

                break;
            case TABLE_APPOINTMENTS:

                setSelectedColumns(new String[]{KEY_ID, KEY_DOCTOR,KEY_TIME});
                break;
            case TABLE_EVENTS:

                setSelectedColumns(new String[]{KEY_ID, KEY_EVENT});
                break;
            case TABLE_SYMPTOMS:

                setSelectedColumns(new String[]{KEY_ID, KEY_SYMPTOM});
                break;
            case TABLE_TODOS:

                setSelectedColumns(new String[]{KEY_ID, KEY_TODO, KEY_STATUS});
                break;
            case TABLE_NOTES:
                setSelectedColumns(new String[]{KEY_ID, KEY_NOTE});

                break;
        }

    }

    //--------new Instance of Table Entry----------//
    public TableEntry(String table, Intent data, String date) {

        switch (table) {
            case TABLE_MEASURES:
                new Measure(data.getExtras().getString("text", ""), "---", date);
                break;

            case TABLE_EVENTS:
                new EventP(data.getExtras().getString("text", ""), date);
                break;

            case TABLE_SYMPTOMS:
                new Symptom(data.getExtras().getString("text", ""), date);
                break;

            case TABLE_NOTES:
                new NoteP(data.getExtras().getString("text", ""), date);
                break;

            case TABLE_APPOINTMENTS:
                new Appointment(data.getExtras().getString("text", ""), "--:--", date);
                break;

            case TABLE_TODOS:
                new Todo(data.getExtras().getString("text", ""), "Pending", date);
                break;
            case TABLE_DOCTORS:
                new Doctors(data.getExtras().getString("text", ""));
                break;
        }
    }

    public static String[] getDisplayColumns(String table) {//columns to show on table view

        String[] columns = new String[0];
        switch(table) {
            case TABLE_MEASURES:
                columns = new String[]{KEY_MEASURE, KEY_VALUE};
                break;
            case TABLE_EVENTS:
                columns = new String[]{KEY_EVENT};
                break;
            case TABLE_SYMPTOMS:
                columns = new String[]{KEY_SYMPTOM};
                break;
            case TABLE_NOTES:
                columns = new String[]{KEY_NOTE};
                break;
            case TABLE_APPOINTMENTS:
                columns = new String[]{KEY_DOCTOR, KEY_TIME};
                break;
            case TABLE_TODOS:
                columns = new String[]{KEY_TODO, KEY_STATUS};
                break;
            case TABLE_DOCTORS:
                columns = new String[]{KEY_DOCTOR,KEY_DOCTOR_PHONE};
                break;
        }

        return columns;
    }

    public static String[] getSelectedColumns(String table) {//columns selected from DB to show on table view

        String[] selectedColumns = new String[0];

        switch (table) {
            case TABLE_MEASURES:

                selectedColumns = new String[]{KEY_ID, KEY_MEASURE, KEY_VALUE};

                break;
            case TABLE_APPOINTMENTS:

                selectedColumns = new String[]{KEY_ID, KEY_DOCTOR,KEY_TIME};
                break;
            case TABLE_EVENTS:

                selectedColumns = new String[]{KEY_ID, KEY_EVENT};
                break;
            case TABLE_SYMPTOMS:

                selectedColumns = new String[]{KEY_ID, KEY_SYMPTOM};
                break;
            case TABLE_TODOS:

                selectedColumns = new String[]{KEY_ID, KEY_TODO, KEY_STATUS};
                break;
            case TABLE_NOTES:
                selectedColumns = new String[]{KEY_ID, KEY_NOTE};

                break;
        }
        return selectedColumns;
    }

    public static ContentValues getUpdateContentValues(String table, Intent data) {
        ContentValues cV= new ContentValues();
        switch(table){
            case TABLE_MEASURES:
                cV.put(TableEntry.KEY_VALUE,data.getStringExtra("text"));
                break;
            case TABLE_APPOINTMENTS:
                cV.put(TableEntry.KEY_TIME,data.getStringExtra("text"));
                break;
            case TABLE_TODOS:
                cV.put(TableEntry.KEY_STATUS,data.getStringExtra("text"));
                break;
            case TABLE_EVENTS:
                cV.put(TableEntry.KEY_EVENT,data.getStringExtra("text"));
                break;
            case TABLE_SYMPTOMS:
                cV.put(TableEntry.KEY_SYMPTOM,data.getStringExtra("text"));
                break;
            case TABLE_NOTES:
                cV.put(TableEntry.KEY_NOTE,data.getStringExtra("text"));
                break;
        }


        return cV;
    }


    public static class EventP {
        ContentValues mContentValues;//object to store name/value pairs

        public EventP(String event, String date) {
            mContentValues = new ContentValues();

            mContentValues.put(KEY_EVENT, event);
            mContentValues.put(KEY_DATE, date);
            TableEntry.setContentValues(mContentValues);
        }
    }

    public static class Measure {
        ContentValues mContentValues;//object to store name/value pairs


        public Measure(String measure, String value, String date) {
            mContentValues = new ContentValues();
            mContentValues.put(KEY_MEASURE, measure);
            mContentValues.put(KEY_DATE, date);
            mContentValues.put(KEY_VALUE, value);

            TableEntry.setContentValues(mContentValues);
        }

    }

    public static class Symptom {
        ContentValues mContentValues;//object to store name/value pairs


        public Symptom(String symptom, String date) {
            mContentValues = new ContentValues();

            mContentValues.put(KEY_SYMPTOM, symptom);
            mContentValues.put(KEY_DATE, date);
            TableEntry.setContentValues(mContentValues);
        }
    }


    public static class Todo {
        ContentValues mContentValues;//object to store name/value pairs

        public Todo(String note, String status, String date) {
            mContentValues = new ContentValues();

            mContentValues.put(KEY_TODO, note);
            mContentValues.put(KEY_DATE, date);
            mContentValues.put(KEY_STATUS, status);
            TableEntry.setContentValues(mContentValues);
        }
    }


    public static class Appointment {
        ContentValues mContentValues;//object to store name/value pairs


        public Appointment(String doctor, String time, String date) {
            mContentValues = new ContentValues();

            mContentValues.put(KEY_DOCTOR, doctor);
            mContentValues.put(KEY_DATE, date);
            mContentValues.put(KEY_TIME, time);
            TableEntry.setContentValues(mContentValues);
        }
    }


    public static class NoteP {
        ContentValues mContentValues;//object to store name/value pairs

        public NoteP(String note, String date) {
            mContentValues = new ContentValues();

            mContentValues.put(KEY_NOTE, note);
            mContentValues.put(KEY_DATE, date);
            TableEntry.setContentValues(mContentValues);

        }
    }

    public static class Doctors {
        ContentValues mContentValues;//object to store name/value pairs
        public Doctors(String name) {
            mContentValues = new ContentValues();

            mContentValues.put(KEY_DOCTOR, name);
            TableEntry.setContentValues(mContentValues);

        }
        public Doctors(String name,String phone,String email,String street,String zip) {
            mContentValues = new ContentValues();

            mContentValues.put(KEY_DOCTOR, name);
            mContentValues.put(KEY_DOCTOR_PHONE, phone);
            mContentValues.put(KEY_DOCTOR_EMAIL, email);
            mContentValues.put(KEY_DOCTOR_STREET, street);
            mContentValues.put(KEY_DOCTOR_ZIP, zip);
            TableEntry.setContentValues(mContentValues);

        }
    }
}
