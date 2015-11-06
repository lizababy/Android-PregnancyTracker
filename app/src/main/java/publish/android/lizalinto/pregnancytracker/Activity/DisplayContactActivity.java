package publish.android.lizalinto.pregnancytracker.Activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import publish.android.lizalinto.pregnancytracker.HelperClass.DataBaseHelper;
import publish.android.lizalinto.pregnancytracker.Model.TableEntry;

import java.util.List;

/**
 * Displays the name,phone,email,address of contacts
 *
 * Allows users to edit, delete and call phone
 *
 * Edit and delete options are given as action bar icon
 *
 * This activity is invoked by 2 ways, one from selecting items on contact list
 * and other from appointments page in Calendar log activity, on clicking the appointment item
 *
 * **/
public class DisplayContactActivity extends ActionBarActivity {

    public static final String INTENT_BOOLEAN_DELETE = "deleteSuccess";
    public static final String INTENT_STRING_ROWID = "rowId";
    public static final String INTENT_BOOLEAN_UPDATE = "updateSuccess";
    public static final String INTENT_STRING_NAME = "contactName";

    TextView mNameTV ;
    TextView mPhoneTV;
    TextView mEmailTV;
    TextView mStreetTV;
    TextView mZipTV;
    long mRowId = 0;
    private DataBaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(publish.android.lizalinto.pregnancytracker.R.layout.activity_display_contact);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(publish.android.lizalinto.pregnancytracker.R.drawable.ic_action_previous_item);
        }

        mNameTV = (TextView) findViewById(publish.android.lizalinto.pregnancytracker.R.id.editTextName);
        mPhoneTV = (TextView) findViewById(publish.android.lizalinto.pregnancytracker.R.id.editTextPhone);
        mEmailTV = (TextView) findViewById(publish.android.lizalinto.pregnancytracker.R.id.editTextStreet);
        mStreetTV = (TextView) findViewById(publish.android.lizalinto.pregnancytracker.R.id.editTextEmail);
        mZipTV = (TextView) findViewById(publish.android.lizalinto.pregnancytracker.R.id.editTextZip);

        db = new DataBaseHelper(this);//instance of pregnancy details database

        db.openDataBase();

        Bundle extras = getIntent().getExtras();
        if(extras !=null)
        {
            mRowId = extras.getLong(INTENT_STRING_ROWID);
            if(mRowId>0){
                //view part not the add contact part.

                Cursor c = db.getRow(mRowId, TableEntry.TABLE_DOCTORS);
                String zip = null;
                String street = null;
                String email = null;
                String phone = null;
                String name = null;
                if(c.moveToFirst()) {
                    name = c.getString(c.getColumnIndex(TableEntry.KEY_DOCTOR));
                    phone = c.getString(c.getColumnIndex(TableEntry.KEY_DOCTOR_PHONE));
                    email = c.getString(c.getColumnIndex(TableEntry.KEY_DOCTOR_EMAIL));
                    street = c.getString(c.getColumnIndex(TableEntry.KEY_DOCTOR_STREET));
                    zip = c.getString(c.getColumnIndex(TableEntry.KEY_DOCTOR_ZIP));
                }

                Button b = (Button)findViewById(publish.android.lizalinto.pregnancytracker.R.id.save_contact_button);
                b.setVisibility(View.INVISIBLE);

                mNameTV.setText(name);
                mNameTV.setFocusable(false);
                mNameTV.setClickable(false);

                mPhoneTV.setText(phone);
                mPhoneTV.setFocusable(false);
                mPhoneTV.setClickable(false);

                mEmailTV.setText(email);
                mEmailTV.setFocusable(false);
                mEmailTV.setClickable(false);

                mStreetTV.setText(street);
                mStreetTV.setFocusable(false);
                mStreetTV.setClickable(false);

                mZipTV.setText(zip);
                mZipTV.setFocusable(false);
                mZipTV.setClickable(false);
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(mRowId>0){
            getMenuInflater().inflate(publish.android.lizalinto.pregnancytracker.R.menu.menu_display_contact, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);
        switch(item.getItemId())
        {
            //Finishes this activity to go to previous page on clicking previous icon
            case android.R.id.home:

                finish();
                return true;

            case publish.android.lizalinto.pregnancytracker.R.id.Edit_Contact:
                Button b = (Button)findViewById(publish.android.lizalinto.pregnancytracker.R.id.save_contact_button);
                b.setVisibility(View.VISIBLE);
                mNameTV.setEnabled(true);
                mNameTV.setFocusableInTouchMode(true);
                mNameTV.setClickable(true);

                mPhoneTV.setEnabled(true);
                mPhoneTV.setFocusableInTouchMode(true);
                mPhoneTV.setClickable(true);

                mEmailTV.setEnabled(true);
                mEmailTV.setFocusableInTouchMode(true);
                mEmailTV.setClickable(true);

                mStreetTV.setEnabled(true);
                mStreetTV.setFocusableInTouchMode(true);
                mStreetTV.setClickable(true);

                mZipTV.setEnabled(true);
                mZipTV.setFocusableInTouchMode(true);
                mZipTV.setClickable(true);

                return true;
            case publish.android.lizalinto.pregnancytracker.R.id.Delete_Contact:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(publish.android.lizalinto.pregnancytracker.R.string.deleteContact)
                        .setPositiveButton(publish.android.lizalinto.pregnancytracker.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Boolean deleteSuccess = db.delete(mRowId,TableEntry.TABLE_DOCTORS);
                                if(!deleteSuccess)
                                    Toast.makeText(getApplicationContext(), getString(publish.android.lizalinto.pregnancytracker.R.string.delete_failed), Toast.LENGTH_SHORT).show();

                                Intent toPassBack = getIntent();
                                toPassBack.putExtra(INTENT_BOOLEAN_DELETE, deleteSuccess);
                                toPassBack.putExtra(INTENT_STRING_NAME, mNameTV.getText().toString());
                                setResult(RESULT_OK, toPassBack);
                                finish();
                            }
                        })
                        .setNegativeButton(publish.android.lizalinto.pregnancytracker.R.string.no, null);
                AlertDialog d = builder.create();
                d.setTitle(getString(publish.android.lizalinto.pregnancytracker.R.string.are_u_sure));
                d.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void saveContact(View view) {
        ContentValues cV = new ContentValues();
        cV.put(TableEntry.KEY_DOCTOR, mNameTV.getText().toString().trim());
        cV.put(TableEntry.KEY_DOCTOR_PHONE, mPhoneTV.getText().toString().trim());
        cV.put(TableEntry.KEY_DOCTOR_EMAIL,  mEmailTV.getText().toString().trim());
        cV.put(TableEntry.KEY_DOCTOR_STREET, mStreetTV.getText().toString().trim());
        cV.put(TableEntry.KEY_DOCTOR_ZIP, mNameTV.getText().toString().trim());

        Boolean updateSuccess = db.update(cV,mRowId,TableEntry.TABLE_DOCTORS);
        if (!updateSuccess)
            Toast.makeText(this, getString(publish.android.lizalinto.pregnancytracker.R.string.data_base_update_failed)+mRowId, Toast.LENGTH_SHORT).show();
        Intent toPassBack = getIntent();
        toPassBack.putExtra(INTENT_BOOLEAN_UPDATE, updateSuccess);
        toPassBack.putExtra(INTENT_STRING_NAME, mNameTV.getText().toString());
        setResult(RESULT_OK, toPassBack);
        finish();
    }

    /*Method invoked by clicking the call button
    * Uses the the phone built in app to make calls*/

     public void CallContact(View view) {
        String  phoneNo = mPhoneTV.getText().toString();

        Uri number = Uri.parse("tel:"+phoneNo);
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);

        // Verify it resolves
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(callIntent, 0);
        boolean isIntentSafe = activities.size() > 0;

        // Start an activity if it's safe
        if (isIntentSafe) {
            startActivity(callIntent);
        }else{
            Toast.makeText(this, publish.android.lizalinto.pregnancytracker.R.string.call_failed,Toast.LENGTH_SHORT).show();
        }
    }
}