package publish.android.lizalinto.pregnancytracker.HelperClass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import publish.android.lizalinto.pregnancytracker.Model.TableEntry;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * Creates pregnancy database if it is not created!
 * App uses database and several tables . These are all pre created and saved in assets folder.
 *
 *  On first time and on version change it reads from database file from assets folder and copy to
 *  default database folder .
 *
 *  Defines needed query functions

 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    //The Android's default system path of application database.
    private static String DB_PATH ;

    private static String DB_NAME = "PregnancyDB";

    private SQLiteDatabase mDatabase;

    private final Context mContext;


    public DataBaseHelper(Context context) {

        super(context, DB_NAME, null, DATABASE_VERSION);
        this.mContext = context;
        //The Android's default system path of application database.
        DB_PATH = context.getFilesDir().getParent() +  "/databases/";


    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if(!dbExist){
            //By calling this method an empty database will be created into the default system path
            //of the application so will be able to overwrite that database with this database.
            mDatabase = this.getReadableDatabase();
            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
            this.openDataBase();
            mDatabase.setVersion(DATABASE_VERSION);
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;
        String defaultDbPath = DB_PATH + DB_NAME;
        try{

            checkDB = SQLiteDatabase.openDatabase(defaultDbPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            e.printStackTrace();
        }

        if(checkDB != null){

            if (checkDB.getVersion() != DATABASE_VERSION) {
                mContext.deleteDatabase(defaultDbPath);
                checkDB.close();
                return false;
            } else {
                checkDB.close();
                return true;
            }

        } else {
            return false;
        }
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{
        //Open your local db as the input stream
        InputStream input = mContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream output = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = input.read(buffer))>0){
            output.write(buffer, 0, length);
        }

        //Close the streams
        output.flush();
        output.close();
        input.close();

    }

    public void openDataBase() throws SQLException {
        //Open the database
        String defaultDbPath = DB_PATH + DB_NAME;
        mDatabase = SQLiteDatabase.openDatabase(defaultDbPath, null, SQLiteDatabase.OPEN_READWRITE);

    }

    @Override
    public synchronized void close() {

        if(mDatabase != null)
            mDatabase.close();
        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    //---insert into the database---
    public long insert(String table) throws SQLException
    {
        long rowId;
        ContentValues values = TableEntry.getContentValues();
        rowId = mDatabase.insert(table, null, values);

        return rowId;
    }
    //-----retrieves all rows for a specified date---------------
    public Cursor getAllRows(String date, String table) throws SQLException
    {
        String[] selectedColumns = TableEntry.getSelectedColumns(table);
        if(date ==  null){
            return mDatabase.query(table, selectedColumns,
                    null, null, null, null, null);

        }else {
            return mDatabase.query(table, selectedColumns
                    , TableEntry.KEY_DATE + "=?", new String[]{date}, null, null, null);
        }
    }
    //-----------retrieves a row for given ID-------------
    public Cursor getRow(long rowId, String table) throws SQLException
    {
        Cursor cursor;
        //uses the Cursor class as a return value for queries
        //retrieves all columns for a given rowId

        cursor = mDatabase.query(true, table, null, TableEntry.KEY_ID + "=" + rowId, null,
                        null, null, null, null);
        return cursor;
    }
    //-----------retrieves a contact for given name-------------
    public Cursor getContactId(String name) throws SQLException
    {
        Cursor cursor;
        //retrieves rowId that matches given name
        String[] columns = {TableEntry.KEY_ID};
        cursor = mDatabase.query(true, TableEntry.TABLE_DOCTORS, columns , TableEntry.KEY_DOCTOR + "="+"'"+name.trim()+"'", null,
                null, null, null, "1");
        return cursor;
    }



    //----------updates content of the row for a given id ----------------
    public boolean update(ContentValues values,long id , String table) throws SQLException
    {
        return mDatabase.update(table, values, TableEntry.KEY_ID + "=" + id, null) > 0;
    }
    //------deletes contents of a row for a given id ---
    public boolean delete(long id, String table) throws SQLException
    {
        return mDatabase.delete(table,TableEntry.KEY_ID + "=" + id, null) > 0;
    }



}