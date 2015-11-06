package publish.android.lizalinto.pregnancytracker.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import publish.android.lizalinto.pregnancytracker.Activity.DisplayContactActivity;
import publish.android.lizalinto.pregnancytracker.HelperClass.DataBaseHelper;
import publish.android.lizalinto.pregnancytracker.Model.TableEntry;

/**
 * This fragment is reused in several places. It populates list view from cursor which can do add
 * edit and delete from database
 * */
public class CalendarLogFragment extends ListFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String ARG_TABLE = "table";
    private static final String ARG_DATE = "date" ;
    private static final String ARG_TITLE = "title";
    private static final int REQUEST_CONTACT = 3;
    private static final int REQUEST_UPDATE = 2;
    private static final int REQUEST_INSERT = 1;

    DataBaseHelper db;
    private String mDate;
    private String mTitle;
    private ShowListCursorAdapter mAdapter;
    private long mRowId;
    private String mTable;

    public String getDate() {
        return mDate;
    }


    public static CalendarLogFragment newInstance(String table,String title, String date) {

        CalendarLogFragment fragment = new CalendarLogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TABLE, table);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DATE,date);
        fragment.setArguments(args);

        return fragment;
    }

    public CalendarLogFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mTable = getArguments().getString(ARG_TABLE);
            mTitle = getArguments().getString(ARG_TITLE);
            mDate = getArguments().getString(ARG_DATE);
        }

        db = new DataBaseHelper(getActivity());//instance of pregnancy details database
        db.openDataBase();

    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(publish.android.lizalinto.pregnancytracker.R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        Button addButton = (Button)view.findViewById(publish.android.lizalinto.pregnancytracker.R.id.button_add);
        addButton.setOnClickListener(this);
        TextView tv = (TextView)view.findViewById(publish.android.lizalinto.pregnancytracker.R.id.item_title);
        tv.setText(mTitle);
        getListView().setOnItemClickListener(this);
        showList();
    }

    private void showList() {

        String[] columns = TableEntry.getDisplayColumns(mTable);
        int[] views = new int[0];
        if(columns.length == 1)
            views = new int[]{publish.android.lizalinto.pregnancytracker.R.id.value_text1};
        else if (columns.length == 2)
            views = new int[]{publish.android.lizalinto.pregnancytracker.R.id.value_text1, publish.android.lizalinto.pregnancytracker.R.id.value_text2};

        Cursor cursor = db.getAllRows(getDate(), mTable);


        mAdapter = new ShowListCursorAdapter(getActivity(), publish.android.lizalinto.pregnancytracker.R.layout.fragment_list_item, cursor,
                columns, views, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        setListAdapter(mAdapter);
    }


    @Override
    public void onClick(View view) {

        if (view.getId() == publish.android.lizalinto.pregnancytracker.R.id.button_add){//add

            if(getDate().equals("-")|| (mTable.equals(TableEntry.TABLE_TODOS) || mTable.equals(TableEntry.TABLE_NOTES))){

                EditTextDialogFragment editTextDialog = EditTextDialogFragment.newInstance(null, TableEntry.getDisplayColumns(mTable)[0]);
                editTextDialog.setTargetFragment(this,REQUEST_INSERT);
                editTextDialog.show(getFragmentManager().beginTransaction(), mTable);
                return;

            }
            if(mTable.equals(TableEntry.TABLE_SYMPTOMS) || mTable.equals(TableEntry.TABLE_APPOINTMENTS) ||
                    mTable.equals(TableEntry.TABLE_EVENTS)|| mTable.equals(TableEntry.TABLE_MEASURES)){

                String title ;
                Cursor c;
                String key;
                c = db.getAllRows(TableEntry.getSelectionDate(mTable),TableEntry.getSelectionTable(mTable) );
                key = TableEntry.getDisplayColumns(mTable)[0];
                if (c.getCount() <= 0) {
                     title = String.format(getString(publish.android.lizalinto.pregnancytracker.R.string.pick_items_warning), key, key);
                } else {
                    title = String.format(getString(publish.android.lizalinto.pregnancytracker.R.string.pick_item), key);
                }

                ListDialogFragment listDialog = ListDialogFragment.newInstance(title,loadDialogItemsList(c,key));
                listDialog.setTargetFragment(this,REQUEST_INSERT);
                listDialog.show(getFragmentManager().beginTransaction(), mTitle);

            }
        }
    }

    private String[] loadDialogItemsList(Cursor c, String key) {
        // looping through all rows and adding to array of items in a column in DB

        String[] listArray = new String[0];
        int i = 0;
        if (c != null) {
            listArray = new String[c.getCount()];
            if (c.moveToFirst()) {
                do {
                    listArray[i] = c.getString(c.getColumnIndex(key));
                    i++;
                } while (c.moveToNext());
            }
        }
        return listArray;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == Activity.RESULT_OK) {

            if(requestCode == REQUEST_INSERT) {
                new TableEntry(mTable, data, getDate());

                //insert into data base on clicking add button
                mRowId = db.insert(mTable);

                if (mRowId <= 0)
                    Toast.makeText(getActivity(), getString(publish.android.lizalinto.pregnancytracker.R.string.insert_failed), Toast.LENGTH_SHORT).show();
            }
            if(requestCode == REQUEST_UPDATE) {
                ContentValues cV = TableEntry.getUpdateContentValues(mTable,data);

                Boolean updateSuccess = db.update(cV,mRowId,mTable);
                if (!updateSuccess)
                    Toast.makeText(getActivity(), getString(publish.android.lizalinto.pregnancytracker.R.string.data_base_update_failed), Toast.LENGTH_SHORT).show();
            }
            if(requestCode == REQUEST_CONTACT) {//Delete or update name from app contacts

                String affectedName = data.getExtras().getString(DisplayContactActivity.INTENT_STRING_NAME);

                if (data.getExtras().getBoolean(DisplayContactActivity.INTENT_BOOLEAN_DELETE)) {
                    db.delete(mRowId, TableEntry.TABLE_APPOINTMENTS);
                    Toast.makeText(getActivity(), String.format(getString(publish.android.lizalinto.pregnancytracker.R.string.delete_toast), affectedName), Toast.LENGTH_SHORT).show();
                }

                if (data.getExtras().getBoolean(DisplayContactActivity.INTENT_BOOLEAN_UPDATE)) {

                    ContentValues cV = new ContentValues();
                    cV.put(TableEntry.KEY_DOCTOR,affectedName);
                    db.update(cV,mRowId,mTable);
                    Toast.makeText(getActivity(), String.format(getString(publish.android.lizalinto.pregnancytracker.R.string.update_toast), affectedName), Toast.LENGTH_SHORT).show();
                }

            }
            showList();
        }
    }




    public void performEditOnItem(int position) {

        Cursor c = (Cursor) mAdapter.getItem(position);
        mRowId = c.getLong(mAdapter.getCursor().getColumnIndex(TableEntry.KEY_ID));

        switch (mTable) {
            case TableEntry.TABLE_APPOINTMENTS:

                TimePickerDialogFragment timePickerDialog = new TimePickerDialogFragment();
                timePickerDialog.setTargetFragment(this, REQUEST_UPDATE);
                timePickerDialog.show(getFragmentManager().beginTransaction(), mTitle);

                break;
            case TableEntry.TABLE_TODOS:
                String[] listArray = new String[]{getString(publish.android.lizalinto.pregnancytracker.R.string.todo_status1), getString(publish.android.lizalinto.pregnancytracker.R.string.todo_status2)};
                String title = getString(publish.android.lizalinto.pregnancytracker.R.string.pick_status);

                ListDialogFragment listDialog = ListDialogFragment.newInstance(title, listArray);
                listDialog.setTargetFragment(this, REQUEST_UPDATE);
                listDialog.show(getFragmentManager().beginTransaction(), mTitle);
                break;
            default:


                int editColumnNo = mAdapter.getCursor().getColumnCount();

                EditTextDialogFragment editTextDialog = EditTextDialogFragment.newInstance(c.getString
                        (c.getColumnIndex(mAdapter.mFrom[editColumnNo - 2])), mAdapter.mFrom[editColumnNo - 2]);//first column

                editTextDialog.setTargetFragment(this, REQUEST_UPDATE);
                editTextDialog.show(getFragmentManager().beginTransaction(), mTitle);
                break;
        }
    }

    private void performDeleteOnItem(int position) {

        Cursor c = (Cursor) mAdapter.getItem(position);
        mRowId = c.getLong(mAdapter.getCursor().getColumnIndex(TableEntry.KEY_ID));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(publish.android.lizalinto.pregnancytracker.R.string.delete_warning)
                .setPositiveButton(publish.android.lizalinto.pregnancytracker.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Boolean deleteSuccess = db.delete(mRowId, mTable);
                        if(!deleteSuccess)
                            Toast.makeText(getActivity(), "Deleted failed" + mRowId, Toast.LENGTH_SHORT).show();
                        showList();
                    }
                })
                .setNegativeButton(publish.android.lizalinto.pregnancytracker.R.string.no, null);
        AlertDialog d = builder.create();
        d.setTitle(publish.android.lizalinto.pregnancytracker.R.string.are_u_sure);
        d.show();


    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long rowId) {
        if(mTable.equals(TableEntry.TABLE_APPOINTMENTS)){

            TextView contactNameTv = (TextView)view.findViewById(publish.android.lizalinto.pregnancytracker.R.id.value_text1);
            String contactName = contactNameTv.getText().toString();
            Cursor c = db.getContactId(contactName);
            if(c.moveToFirst()) {
                mRowId = rowId;
                rowId = c.getLong(c.getColumnIndex(TableEntry.KEY_ID));
                Intent intent = new Intent(getActivity(), DisplayContactActivity.class);
                intent.putExtra(DisplayContactActivity.INTENT_STRING_ROWID, rowId);
                startActivityForResult(intent, REQUEST_CONTACT);
            }else{
                Toast.makeText(getActivity(), publish.android.lizalinto.pregnancytracker.R.string.contacts_not_seen,Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class ShowListCursorAdapter extends CursorAdapter {


        private final LayoutInflater mInflater;
        private int[] mTo;
        private Cursor mCursor;
        public String[] mFrom;
        private int mLayout;


        public ShowListCursorAdapter(Context context, int layout, Cursor cursor, String[] columns, int[] views, int flag) {
            super(context, cursor, flag);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mCursor = cursor;
            mLayout = layout;
            mTo = views;
            mFrom = columns;

        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

            View view = mInflater.inflate(mLayout, viewGroup, false);
            if(mTable.equals(TableEntry.TABLE_APPOINTMENTS))
                view.findViewById(publish.android.lizalinto.pregnancytracker.R.id.button_detail).setVisibility(View.VISIBLE);

            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            for (int i = 0; i < mTo.length; i++) {
                TextView content = (TextView) view.findViewById(mTo[i]);
                content.setText(mCursor.getString(mCursor.getColumnIndex(mFrom[i])));
            }
        }



        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);

            v.findViewById(publish.android.lizalinto.pregnancytracker.R.id.button_edit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    performEditOnItem(position);
                }
            });
            v.findViewById(publish.android.lizalinto.pregnancytracker.R.id.button_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    performDeleteOnItem(position);
                }
            });

            return v;
        }


    }


}
