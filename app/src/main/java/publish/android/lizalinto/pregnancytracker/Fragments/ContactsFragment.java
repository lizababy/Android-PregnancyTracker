package publish.android.lizalinto.pregnancytracker.Fragments;


import android.app.Activity;
import android.app.ListFragment;
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
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import publish.android.lizalinto.pregnancytracker.Activity.DisplayContactActivity;
import publish.android.lizalinto.pregnancytracker.Activity.HomeActivity;
import publish.android.lizalinto.pregnancytracker.HelperClass.DataBaseHelper;
import publish.android.lizalinto.pregnancytracker.Model.TableEntry;


public class ContactsFragment extends ListFragment implements View.OnClickListener, AdapterView.OnItemClickListener {


    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static final int REQUEST_DETAILS = 2;
    private static final int REQUEST_INSERT = 1;
    private DataBaseHelper db;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ContactsFragment newInstance(int sectionNumber) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((HomeActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        tv.setText(publish.android.lizalinto.pregnancytracker.R.string.enter_contacts);
        showList();
        getListView().setOnItemClickListener(this);
        getListView().setSelector(android.R.color.holo_blue_light);

    }


    private void showList() {

        String[] columns = TableEntry.getDisplayColumns(TableEntry.TABLE_DOCTORS);
        int[] views = new int[]{publish.android.lizalinto.pregnancytracker.R.id.text1, publish.android.lizalinto.pregnancytracker.R.id.text2};

        Cursor cursor = db.getAllRows(null, TableEntry.TABLE_DOCTORS);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), publish.android.lizalinto.pregnancytracker.R.layout.contact_list_item, cursor,
                columns, views, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        getListView().setAdapter(adapter);

    }


    @Override
    public void onClick(View view) {
        EditTextDialogFragment editTextDialog = EditTextDialogFragment.newInstance(null, getString(publish.android.lizalinto.pregnancytracker.R.string.doctor_name));
        editTextDialog.setTargetFragment(this,REQUEST_INSERT);
        editTextDialog.show(getFragmentManager().beginTransaction(), "Doctors");
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == Activity.RESULT_OK) {

            if(requestCode == REQUEST_INSERT) {//insert name
                new TableEntry(TableEntry.TABLE_DOCTORS, data,null);

                //insert into database on clicking add button
                long rowId = db.insert(TableEntry.TABLE_DOCTORS);

                if (rowId <= 0)
                    Toast.makeText(getActivity(), getString(publish.android.lizalinto.pregnancytracker.R.string.insert_failed), Toast.LENGTH_SHORT).show();
            }
            if(requestCode == REQUEST_DETAILS) {//Delete or update

                String affectedName = data.getExtras().getString(DisplayContactActivity.INTENT_STRING_NAME);
                if (data.getExtras().getBoolean(DisplayContactActivity.INTENT_BOOLEAN_DELETE))
                    Toast.makeText(getActivity(), String.format(getString(publish.android.lizalinto.pregnancytracker.R.string.delete_toast), affectedName), Toast.LENGTH_SHORT).show();

                if (data.getExtras().getBoolean(DisplayContactActivity.INTENT_BOOLEAN_UPDATE))
                    Toast.makeText(getActivity(), String.format(getString(publish.android.lizalinto.pregnancytracker.R.string.update_toast), affectedName), Toast.LENGTH_SHORT).show();

            }


            showList();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        getListView().setItemChecked(i,true);
        Intent intent = new Intent(getActivity(),DisplayContactActivity.class);
        intent.putExtra(DisplayContactActivity.INTENT_STRING_ROWID,l);
        startActivityForResult(intent, REQUEST_DETAILS);
    }


}
