package publish.android.lizalinto.pregnancytracker.Fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Edit text dialog ued to enter logs - add/edit
 * */
public class ListDialogFragment extends DialogFragment {

    private static final String ARG_TITLE = "key";
    private static final String ARG_LIST = "data";
    private String mTitle = null;
    private String[] mList = null;
    private String mSelectedItem = "";

    public ListDialogFragment() {
        // Required empty public constructor
    }
    public static ListDialogFragment newInstance(String title, String[] listArray) {
        ListDialogFragment fragment = new ListDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putStringArray(ARG_LIST, listArray);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_TITLE);
            mList = getArguments().getStringArray(ARG_LIST);
        }
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View editTextView = inflater.inflate(publish.android.lizalinto.pregnancytracker.R.layout.fragment_edit_text, null);
        final EditText mEditText = (EditText) editTextView.findViewById(publish.android.lizalinto.pregnancytracker.R.id.text_et1);
        String hintText = getString(publish.android.lizalinto.pregnancytracker.R.string.edit_text_hint);
        mEditText.setHint(hintText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mTitle)
                .setItems(mList, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mSelectedItem = mList[which];
                        callBack();
                    }
                }).setView(editTextView)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!mEditText.getText().toString().isEmpty()) {
                    mSelectedItem = mEditText.getText().toString();
                }
                callBack();
            }
        }).setNegativeButton("Cancel", null);

        return builder.create();

    }

    private void callBack(){
        if(!mSelectedItem.isEmpty()) {
            Intent toBack = getActivity().getIntent();
            toBack.putExtra("text", mSelectedItem.trim());
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, toBack);
        }

    }
    }
