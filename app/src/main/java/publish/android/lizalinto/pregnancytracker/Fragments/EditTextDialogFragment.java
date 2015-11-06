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
import android.view.WindowManager;
import android.widget.EditText;


public class EditTextDialogFragment extends DialogFragment {


    private static final String ARG_DATA = "data";
    private static final String ARG_KEY = "column_key";
    private static final String TEXT_STRING = "text";
    private String mData;
    private String mKey;

    public static EditTextDialogFragment newInstance(String data,String key) {
        EditTextDialogFragment fragment = new EditTextDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATA, data);
        args.putString(ARG_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }
    public EditTextDialogFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mData = getArguments().getString(ARG_DATA);
            mKey = getArguments().getString(ARG_KEY);
        }
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View editTextView = inflater.inflate(publish.android.lizalinto.pregnancytracker.R.layout.fragment_edit_text, null);
        final EditText mEditText = (EditText) editTextView.findViewById(publish.android.lizalinto.pregnancytracker.R.id.text_et1);

        String title;
        String positiveButtonText;
        if(mData!=null){//Edit

            mEditText.setText(mData);
            int pos = mEditText.getSelectionStart();
            mEditText.setSelection(mData.length()+pos);
            title = String.format(getString(publish.android.lizalinto.pregnancytracker.R.string.edit),mKey);
            positiveButtonText = getString(publish.android.lizalinto.pregnancytracker.R.string.update);

        }else{//Add

            title = String.format(getString(publish.android.lizalinto.pregnancytracker.R.string.add_title),mKey);
            positiveButtonText = getString(publish.android.lizalinto.pregnancytracker.R.string.add);
            String hintText = String.format(getString(publish.android.lizalinto.pregnancytracker.R.string.enter),mKey);
            mEditText.setHint(hintText);
        }

        builder.setView(editTextView);
        builder.setTitle(title).
                setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String text = mEditText.getText().toString();
                        Intent toBack = getActivity().getIntent();
                        toBack.putExtra(TEXT_STRING, text.trim());
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, toBack);

                    }
                })
                .setNegativeButton(getString(publish.android.lizalinto.pregnancytracker.R.string.cancel), null);

        return builder.create();
    }


}

